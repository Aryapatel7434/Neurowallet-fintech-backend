
package com.smartwallet.service;

import com.smartwallet.dto.DashboardInsightResponse;
import com.smartwallet.dto.TransactionAnalyticsResponse;
import com.smartwallet.dto.TransactionEvent;
import com.smartwallet.dto.TransactionRequest;
import com.smartwallet.dto.TransactionResponseDTO;
import com.smartwallet.exception.BadRequestException;
import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.kafka.TransactionEventProducer;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionCategory;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.TransactionType;
import com.smartwallet.model.User;
import com.smartwallet.model.Wallet;
import com.smartwallet.model.WalletTransaction;
import com.smartwallet.repository.TransactionRepository;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.repository.WalletRepository;
import com.smartwallet.repository.WalletTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private static final Logger logger =
            LoggerFactory.getLogger(TransactionService.class);

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionAuditService transactionAuditService;
    private final WalletCacheService walletCacheService;
    private final TransactionEventProducer transactionEventProducer;
    private final WalletTransactionRepository walletTransactionRepository;

    public TransactionService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            TransactionAuditService transactionAuditService,
            WalletCacheService walletCacheService,
            TransactionEventProducer transactionEventProducer,
            WalletTransactionRepository walletTransactionRepository) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.transactionAuditService = transactionAuditService;
        this.walletCacheService = walletCacheService;
        this.transactionEventProducer = transactionEventProducer;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    // ============================================================
    // SEND MONEY
    // ============================================================

    @Transactional
    public String sendMoney(TransactionRequest request) {

        if (request == null) {
            logger.warn("Transaction failed because request is null");
            throw new BadRequestException(
                    "Transaction request cannot be null");
        }

        if (request.getAmount() == null
                || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Transaction failed due to invalid amount");
            throw new BadRequestException(
                    "Amount must be greater than zero");
        }

        if (request.getReceiverEmail() == null
                || request.getReceiverEmail().isBlank()) {
            logger.warn(
                    "Transaction failed because receiver email is missing");
            throw new BadRequestException(
                    "Receiver email is required");
        }

        String receiverEmail = request.getReceiverEmail().trim();

        String senderEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        logger.info("Transaction processing started");

        // User validation
        User sender = userRepository.findByEmail(senderEmail);

        if (sender == null) {
            logger.warn("Transaction failed. Sender not found");
            throw new ResourceNotFoundException("Sender not found");
        }

        User receiver = userRepository.findByEmail(receiverEmail);

        if (receiver == null) {
            logger.warn("Transaction failed. Receiver not found");
            transactionAuditService.saveFailedTransaction(
                    senderEmail, receiverEmail, request.getAmount());
            throw new ResourceNotFoundException("Receiver not found");
        }

        // Self-transfer validation
        if (sender.getEmail().equals(receiver.getEmail())) {
            logger.warn("Transaction failed. Self-transfer attempted");
            transactionAuditService.saveFailedTransaction(
                    sender.getEmail(),
                    receiver.getEmail(),
                    request.getAmount());
            throw new BadRequestException(
                    "Cannot send money to yourself");
        }

        // Wallet validation
        Wallet senderWallet =
                walletRepository.findByUserEmail(sender.getEmail());

        Wallet receiverWallet =
                walletRepository.findByUserEmail(receiver.getEmail());

        if (senderWallet == null) {
            logger.warn("Transaction failed. Sender wallet not found");
            throw new ResourceNotFoundException(
                    "Sender wallet not found");
        }

        if (receiverWallet == null) {
            logger.warn("Transaction failed. Receiver wallet not found");
            throw new ResourceNotFoundException(
                    "Receiver wallet not found");
        }

        // Wallet balance integrity validation
        if (senderWallet.getBalance() == null) {
            logger.error(
                    "Transaction blocked because sender wallet balance is null");
            throw new BadRequestException(
                    "Sender wallet balance is invalid");
        }

        if (receiverWallet.getBalance() == null) {
            logger.error(
                    "Transaction blocked because receiver wallet balance is null");
            throw new BadRequestException(
                    "Receiver wallet balance is invalid");
        }

        if (senderWallet.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            logger.error(
                    "Transaction blocked because sender wallet has negative balance");
            throw new BadRequestException(
                    "Sender wallet balance is invalid");
        }

        if (receiverWallet.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            logger.error(
                    "Transaction blocked because receiver wallet has negative balance");
            throw new BadRequestException(
                    "Receiver wallet balance is invalid");
        }

        // Insufficient balance validation
        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            logger.warn(
                    "Transaction failed due to insufficient balance");
            transactionAuditService.saveFailedTransaction(
                    sender.getEmail(),
                    receiver.getEmail(),
                    request.getAmount());
            throw new BadRequestException("Insufficient balance");
        }

        // Create transaction
        Transaction transaction = new Transaction(
                sender.getEmail(),
                receiver.getEmail(),
                request.getAmount(),
                TransactionStatus.PENDING,
                TransactionType.TRANSFER,
                request.getCategory(),
                LocalDateTime.now());

        transactionRepository.save(transaction);
        logger.info("Transaction saved with PENDING status");

        // Update wallet balances
        senderWallet.setBalance(
                senderWallet.getBalance().subtract(request.getAmount()));

        receiverWallet.setBalance(
                receiverWallet.getBalance().add(request.getAmount()));

        // Post-update balance integrity checks
        if (senderWallet.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            logger.error(
                    "Transaction blocked because sender balance became negative");
            throw new BadRequestException(
                    "Transaction would result in negative sender balance");
        }

        if (receiverWallet.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            logger.error(
                    "Transaction blocked because receiver balance became invalid");
            throw new BadRequestException(
                    "Transaction would result in invalid receiver balance");
        }

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // Sender ledger
        WalletTransaction senderLedger = new WalletTransaction();
        senderLedger.setWallet(senderWallet);
        senderLedger.setAmount(request.getAmount());
        senderLedger.setType("DEBIT");
        senderLedger.setCreatedAt(LocalDateTime.now());
        walletTransactionRepository.save(senderLedger);

        // Receiver ledger
        WalletTransaction receiverLedger = new WalletTransaction();
        receiverLedger.setWallet(receiverWallet);
        receiverLedger.setAmount(request.getAmount());
        receiverLedger.setType("CREDIT");
        receiverLedger.setCreatedAt(LocalDateTime.now());
        walletTransactionRepository.save(receiverLedger);

        logger.info(
                "Wallet balances updated successfully for sender and receiver");

        // Mark transaction successful
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);
        logger.info("Transaction status updated to SUCCESS");

        // Clear wallet cache
        walletCacheService.clearWalletCache(sender.getEmail());
        walletCacheService.clearWalletCache(receiver.getEmail());
        logger.info("Wallet cache cleared for sender and receiver");

        // Publish Kafka event
        TransactionEvent event = new TransactionEvent(
                sender.getEmail(),
                receiver.getEmail(),
                request.getAmount(),
                "SUCCESS",
                LocalDateTime.now());

        transactionEventProducer
                .publishTransactionEvent(event)
                .thenAccept(published -> {
                    if (published) {
                        logger.info(
                                "Transaction event successfully published to Kafka");
                    } else {
                        logger.warn(
                                "Transaction completed successfully, but Kafka event publication failed");
                    }
                });

        return "Transaction Successful";
    }

    // ============================================================
    // TRANSACTION HISTORY
    // ============================================================

    public Page<TransactionResponseDTO> getTransactionHistory(
            String email,
            int page,
            int size) {

        logger.info(
                "Fetching transaction history. Page: {}, Size: {}",
                page, size);

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("timestamp").descending());

        Page<Transaction> transactions =
                transactionRepository.findBySenderEmailOrReceiverEmail(
                        email, email, pageable);

        return transactions.map(tx -> new TransactionResponseDTO(
                tx.getTransactionId(),
                tx.getSenderEmail(),
                tx.getReceiverEmail(),
                tx.getAmount(),
                tx.getStatus().name(),
                tx.getType(),
                tx.getCategory(),
                tx.getTimestamp()));
    }

    // ============================================================
    // SENT TRANSACTIONS
    // ============================================================

    public Page<TransactionResponseDTO> getSentTransactions(
            String email,
            int page,
            int size) {

        logger.info("Fetching sent transactions");

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("timestamp").descending());

        Page<Transaction> transactions =
                transactionRepository.findBySenderEmail(email, pageable);

        return transactions.map(tx -> new TransactionResponseDTO(
                tx.getTransactionId(),
                tx.getSenderEmail(),
                tx.getReceiverEmail(),
                tx.getAmount(),
                tx.getStatus().name(),
                tx.getCategory(),
                tx.getTimestamp()));
    }

    // ============================================================
    // RECEIVED TRANSACTIONS
    // ============================================================

    public Page<Transaction> getReceivedTransactions(
            String email,
            int page,
            int size) {

        logger.info("Fetching received transactions");

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("timestamp").descending());

        return transactionRepository.findByReceiverEmail(email, pageable);
    }

    // ============================================================
    // TRANSACTIONS BY STATUS
    // ============================================================

    public Page<Transaction> getTransactionsByStatus(
            TransactionStatus status,
            int page,
            int size) {

        logger.info("Fetching transactions by status: {}", status);

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("timestamp").descending());

        return transactionRepository.findByStatus(status, pageable);
    }

    // ============================================================
    // SEARCH TRANSACTIONS BY EMAIL
    // ============================================================

    public Page<Transaction> searchTransactionsByEmail(
            String email,
            int page,
            int size) {

        logger.info("Searching transactions by email keyword");

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("timestamp").descending());

        return transactionRepository
                .findBySenderEmailContainingOrReceiverEmailContaining(
                        email, email, pageable);
    }

    // ============================================================
    // TRANSACTIONS BY AMOUNT RANGE
    // ============================================================

    public Page<Transaction> getTransactionsByAmountRange(
            BigDecimal minAmount,
            BigDecimal maxAmount,
            int page,
            int size) {

        logger.info("Fetching transactions by amount range");

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("timestamp").descending());

        return transactionRepository.findByAmountBetween(
                minAmount, maxAmount, pageable);
    }

    // ============================================================
    // DASHBOARD INSIGHTS
    // ============================================================

    public DashboardInsightResponse getDashboardInsights() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        logger.info("Generating dashboard insights");

        List<Transaction> transactions =
                transactionRepository.findBySenderEmailOrReceiverEmail(
                        email, email);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        Map<TransactionCategory, BigDecimal> categoryTotals =
                new HashMap<>();

        long transactionCount = 0;

        for (Transaction tx : transactions) {
            if (tx == null
                    || tx.getAmount() == null
                    || tx.getStatus() == null) {
                continue;
            }

            if (tx.getStatus() != TransactionStatus.SUCCESS) {
                continue;
            }

            BigDecimal amount = tx.getAmount().abs();

            boolean isReceiver = email.equals(tx.getReceiverEmail());
            boolean isSender = email.equals(tx.getSenderEmail());

            if (isReceiver) {
                totalIncome = totalIncome.add(amount);
                transactionCount++;
            }

            if (isSender) {
                totalExpense = totalExpense.add(amount);
                transactionCount++;

                TransactionCategory category = tx.getCategory();

                if (category != null) {
                    categoryTotals.put(
                            category,
                            categoryTotals.getOrDefault(
                                    category, BigDecimal.ZERO).add(amount));
                }
            }
        }

        BigDecimal netCashFlow = totalIncome.subtract(totalExpense);
        TransactionCategory topCategory = null;
        BigDecimal topCategoryAmount = BigDecimal.ZERO;

        for (Map.Entry<TransactionCategory, BigDecimal> entry
                : categoryTotals.entrySet()) {
            if (entry.getValue().compareTo(topCategoryAmount) > 0) {
                topCategory = entry.getKey();
                topCategoryAmount = entry.getValue();
            }
        }

        String topCategoryName =
                topCategory != null ? topCategory.name() : null;

        logger.info("Dashboard insights generated successfully");

        return new DashboardInsightResponse(
                totalIncome,
                totalExpense,
                netCashFlow,
                transactionCount,
                topCategoryName,
                topCategoryAmount);
    }

   
public TransactionAnalyticsResponse getTransactionAnalytics() {

    List<Transaction> transactions = transactionRepository.findAll();

    long totalTransactions = transactions.size();
    long successfulTransactions = 0;
    long failedTransactions = 0;

    BigDecimal totalIncome = BigDecimal.ZERO;
    BigDecimal totalExpense = BigDecimal.ZERO;
    BigDecimal totalTransfer = BigDecimal.ZERO;
    BigDecimal totalSuccessfulAmount = BigDecimal.ZERO;
    BigDecimal largestTransaction = BigDecimal.ZERO;

    for (Transaction transaction : transactions) {

        if (transaction == null
                || transaction.getAmount() == null
                || transaction.getStatus() == null
                || transaction.getType() == null) {
            continue;
        }

        TransactionStatus status = transaction.getStatus();

        if (status == TransactionStatus.SUCCESS) {
            successfulTransactions++;

            BigDecimal amount = transaction.getAmount().abs();
            totalSuccessfulAmount = totalSuccessfulAmount.add(amount);

            if (amount.compareTo(largestTransaction) > 0) {
                largestTransaction = amount;
            }

            switch (transaction.getType()) {
                case CREDIT ->
                    totalIncome = totalIncome.add(amount);

                case DEBIT ->
                    totalExpense = totalExpense.add(amount);

                case TRANSFER -> {
                    totalTransfer = totalTransfer.add(amount);

                    // Internal transfers affect both sides of global cash flow.
                    totalIncome = totalIncome.add(amount);
                    totalExpense = totalExpense.add(amount);
                }
            }

        } else if (status == TransactionStatus.FAILED
                || status == TransactionStatus.FAILD) {
            failedTransactions++;
        }
    }

    BigDecimal savings = totalIncome.subtract(totalExpense);

    BigDecimal savingsRatio = BigDecimal.ZERO;

    if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
        savingsRatio = savings
                .multiply(new BigDecimal("100"))
                .divide(
                        totalIncome,
                        2,
                        java.math.RoundingMode.HALF_UP
                );
    }

    BigDecimal averageTransaction = BigDecimal.ZERO;

    if (successfulTransactions > 0) {
        averageTransaction = totalSuccessfulAmount.divide(
                BigDecimal.valueOf(successfulTransactions),
                2,
                java.math.RoundingMode.HALF_UP
        );
    }

    return new TransactionAnalyticsResponse(
            totalTransactions,
            successfulTransactions,
            failedTransactions,
            totalIncome,
            totalExpense,
            totalTransfer,
            averageTransaction,
            largestTransaction,
            savings,
            savingsRatio
    );
}


    // ============================================================
    // FILTERED USER TRANSACTION HISTORY
    // ============================================================

    public Page<TransactionResponseDTO> getFilteredTransactionHistory(
            String email,
            int page,
            int size,
            String keyword,
            TransactionStatus status,
            TransactionType type,
            TransactionCategory category,
            LocalDateTime fromDate,
            LocalDateTime toDateExclusive,
            String sortBy,
            String direction) {

        if (page < 0) {
            throw new BadRequestException(
                    "Page must be zero or greater");
        }

        if (size < 1 || size > 100) {
            throw new BadRequestException(
                    "Page size must be between 1 and 100");
        }

        if (fromDate != null
                && toDateExclusive != null
                && !fromDate.isBefore(toDateExclusive)) {
            throw new BadRequestException(
                    "Invalid transaction date range");
        }

        String normalizedKeyword =
                keyword == null || keyword.isBlank()
                        ? null
                        : keyword.trim();

        Long transactionId = null;

        if (normalizedKeyword != null) {
            try {
                transactionId = Long.valueOf(normalizedKeyword);
            } catch (NumberFormatException ignored) {
                // Non-numeric keywords may match email or enum fields.
            }
        }

        String requestedSortBy =
                sortBy == null ? "timestamp" : sortBy;

        String safeSortBy = switch (requestedSortBy) {
            case "timestamp", "amount" -> requestedSortBy;
            default -> throw new BadRequestException(
                    "Sort field must be timestamp or amount");
        };

        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.fromString(
                    direction == null ? "DESC" : direction);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(
                    "Sort direction must be ASC or DESC");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, safeSortBy));

        TransactionCategory keywordCategory = null;
        TransactionType keywordType = null;
        TransactionStatus keywordStatus = null;

        if (normalizedKeyword != null) {
            String normalizedEnumKeyword =
                    normalizedKeyword.toUpperCase(Locale.ROOT);

            try {
                keywordCategory =
                        TransactionCategory.valueOf(normalizedEnumKeyword);
            } catch (IllegalArgumentException ignored) {
                // Not a category.
            }

            try {
                keywordType =
                        TransactionType.valueOf(normalizedEnumKeyword);
            } catch (IllegalArgumentException ignored) {
                // Not a transaction type.
            }

            try {
                keywordStatus =
                        TransactionStatus.valueOf(normalizedEnumKeyword);
            } catch (IllegalArgumentException ignored) {
                // Not a transaction status.
            }
        }

        // Temporary diagnostic logging for search investigation.
        logger.info(
                "Transaction search debug | email={}, keyword={}, "
                        + "keywordCategory={}, keywordType={}, "
                        + "keywordStatus={}, transactionId={}, "
                        + "statusFilter={}, typeFilter={}, categoryFilter={}",
                email,
                normalizedKeyword,
                keywordCategory,
                keywordType,
                keywordStatus,
                transactionId,
                status,
                type,
                category);

        Page<Transaction> transactions =
                transactionRepository.findFilteredUserHistory(
                        email,
                        status,
                        type,
                        category,
                        fromDate,
                        toDateExclusive,
                        normalizedKeyword,
                        transactionId,
                        keywordCategory,
                        keywordType,
                        keywordStatus,
                        pageable);

        logger.info(
                "Transaction search result | email={}, keyword={}, "
                        + "page={}, size={}, totalElements={}, returnedElements={}",
                email,
                normalizedKeyword,
                page,
                size,
                transactions.getTotalElements(),
                transactions.getNumberOfElements());

        return transactions.map(tx -> new TransactionResponseDTO(
                tx.getTransactionId(),
                tx.getSenderEmail(),
                tx.getReceiverEmail(),
                tx.getAmount(),
                tx.getStatus().name(),
                tx.getType(),
                tx.getCategory(),
                tx.getTimestamp()));
    }
}
