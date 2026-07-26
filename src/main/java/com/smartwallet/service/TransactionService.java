package com.smartwallet.service;

import com.smartwallet.dto.TransactionAnalyticsResponse;
import com.smartwallet.dto.TransactionEvent;
import com.smartwallet.dto.TransactionRequest;
import com.smartwallet.exception.BadRequestException;
import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.kafka.TransactionEventProducer;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.User;
import com.smartwallet.model.Wallet;
import com.smartwallet.repository.TransactionRepository;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.repository.WalletRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartwallet.dto.TransactionResponseDTO;
import com.smartwallet.dto.DashboardInsightResponse;
import java.util.HashMap;
import java.util.Map;
import com.smartwallet.model.TransactionCategory;
import com.smartwallet.model.TransactionType;
import java.util.List;
import com.smartwallet.model.WalletTransaction;
import com.smartwallet.repository.WalletTransactionRepository;
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
        WalletTransactionRepository walletTransactionRepository){
    
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.transactionAuditService = transactionAuditService;
        this.walletCacheService = walletCacheService;
        this.transactionEventProducer = transactionEventProducer;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @Transactional
    public String sendMoney(TransactionRequest request) {

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            logger.warn("Transaction failed due to invalid amount: {}",
                    request.getAmount());

            throw new BadRequestException("Amount must be greater than zero");
        }

        String senderEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        logger.info("Transaction started from {} to {} amount {}",
                senderEmail,
                request.getReceiverEmail(),
                request.getAmount());

        User sender = userRepository.findByEmail(senderEmail);

        if (sender == null) {

            logger.warn("Transaction failed. Sender not found: {}", senderEmail);

            throw new ResourceNotFoundException("Sender not found");
        }

        User receiver = userRepository.findByEmail(request.getReceiverEmail());

        if (receiver == null) {

            logger.warn("Transaction failed. Receiver not found: {}",
                    request.getReceiverEmail());

            transactionAuditService.saveFailedTransaction(
                    senderEmail,
                    request.getReceiverEmail(),
                    request.getAmount()
            );

            throw new ResourceNotFoundException("Receiver not found");
        }

        if (sender.getEmail().equals(receiver.getEmail())) {

            logger.warn("Transaction failed. User tried to send money to self: {}",
                    sender.getEmail());

            transactionAuditService.saveFailedTransaction(
                    sender.getEmail(),
                    receiver.getEmail(),
                    request.getAmount()
            );

            throw new BadRequestException("Cannot send money to yourself");
        }

        Wallet senderWallet =
                walletRepository.findByUserEmail(sender.getEmail());

        Wallet receiverWallet =
                walletRepository.findByUserEmail(receiver.getEmail());

        if (senderWallet == null) {

            logger.warn("Transaction failed. Sender wallet not found: {}",
                    sender.getEmail());

            throw new ResourceNotFoundException("Sender wallet not found");
        }

        if (receiverWallet == null) {

            logger.warn("Transaction failed. Receiver wallet not found: {}",
                    receiver.getEmail());

            throw new ResourceNotFoundException("Receiver wallet not found");
        }

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {

            logger.warn("Transaction failed due to insufficient balance. Sender: {}, Amount: {}",
                    sender.getEmail(),
                    request.getAmount());

            transactionAuditService.saveFailedTransaction(
                    sender.getEmail(),
                    receiver.getEmail(),
                    request.getAmount()
            );

            throw new BadRequestException("Insufficient balance");
        }
     Transaction transaction =
    new Transaction(
        sender.getEmail(),
        receiver.getEmail(),
        request.getAmount(),
        TransactionStatus.PENDING,
        TransactionType.TRANSFER,
        request.getCategory(),
        LocalDateTime.now()
    );

        transactionRepository.save(transaction);

        logger.info("Transaction saved with PENDING status");

        senderWallet.setBalance(
                senderWallet.getBalance().subtract(request.getAmount())
        );

        receiverWallet.setBalance(
                receiverWallet.getBalance().add(request.getAmount())
        );

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);
        
        WalletTransaction senderLedger = new WalletTransaction();

senderLedger.setWallet(senderWallet);
senderLedger.setAmount(request.getAmount());
senderLedger.setType("DEBIT");
senderLedger.setCreatedAt(LocalDateTime.now());

walletTransactionRepository.save(senderLedger);

WalletTransaction receiverLedger = new WalletTransaction();

receiverLedger.setWallet(receiverWallet);
receiverLedger.setAmount(request.getAmount());
receiverLedger.setType("CREDIT");
receiverLedger.setCreatedAt(LocalDateTime.now());

walletTransactionRepository.save(receiverLedger);

        logger.info("Wallet balances updated successfully for sender and receiver");

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        logger.info("Transaction status updated to SUCCESS");

        walletCacheService.clearWalletCache(sender.getEmail());
        walletCacheService.clearWalletCache(receiver.getEmail());

        logger.info("Wallet cache cleared for sender and receiver");

        TransactionEvent event =
                new TransactionEvent(
                        sender.getEmail(),
                        receiver.getEmail(),
                        request.getAmount(),
                        "SUCCESS",
                        LocalDateTime.now()
                );

        transactionEventProducer.publishTransactionEvent(event);

        logger.info("Transaction successful and Kafka event published");

        return "Transaction Successful";
    }

  public Page<TransactionResponseDTO> getTransactionHistory(
        String email,
        int page,
        int size) {

    logger.info(
            "Fetching transaction history for email: {}, page: {}, size: {}",
            email,
            page,
            size
    );

    Pageable pageable =
            PageRequest.of(
                    page,
                    size,
                    Sort.by("timestamp").descending()
            );

    Page<Transaction> transactions =
            transactionRepository
                    .findBySenderEmailOrReceiverEmail(
                            email,
                            email,
                            pageable
                    );

return transactions.map(tx ->
    new TransactionResponseDTO(
    tx.getTransactionId(),
    tx.getSenderEmail(),
    tx.getReceiverEmail(),
    tx.getAmount(),
    tx.getStatus().name(),
    tx.getType(),
    tx.getCategory(),
    tx.getTimestamp()
)
);
}

     public Page<TransactionResponseDTO> getSentTransactions(
        String email,
        int page,
        int size) {

    logger.info(
            "Fetching sent transactions for email: {}",
            email
    );

    Pageable pageable =
            PageRequest.of(
                    page,
                    size,
                    Sort.by("timestamp").descending()
            );

    Page<Transaction> transactions =
            transactionRepository
                    .findBySenderEmail(
                            email,
                            pageable
                    );

  return transactions.map(tx ->
        new TransactionResponseDTO(
                tx.getTransactionId(),
                tx.getSenderEmail(),
                tx.getReceiverEmail(),
                tx.getAmount(),
                tx.getStatus().name(),
                tx.getCategory(),
                tx.getTimestamp()
        )
);
}
    public Page<Transaction> getReceivedTransactions(
            String email,
            int page,
            int size) {

        logger.info("Fetching received transactions for email: {}", email);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("timestamp").descending()
                );

        return transactionRepository.findByReceiverEmail(email, pageable);
    }

    public Page<Transaction> getTransactionsByStatus(
            TransactionStatus status,
            int page,
            int size) {

        logger.info("Fetching transactions by status: {}", status);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("timestamp").descending()
                );

        return transactionRepository.findByStatus(status, pageable);
    }

    public TransactionAnalyticsResponse getTransactionAnalytics() {

        logger.info("Fetching transaction analytics");

        long successCount =
                transactionRepository.countByStatus(
                        TransactionStatus.SUCCESS
                );

        long failedCount =
                transactionRepository.countByStatus(
                        TransactionStatus.FAILED
                );

        logger.info("Transaction analytics fetched. Success: {}, Failed: {}",
                successCount,
                failedCount);

        return new TransactionAnalyticsResponse(
                successCount,
                failedCount
        );
    }

    public Page<Transaction> searchTransactionsByEmail(
            String email,
            int page,
            int size) {

        logger.info("Searching transactions by email keyword: {}", email);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("timestamp").descending()
                );

        return transactionRepository
                .findBySenderEmailContainingOrReceiverEmailContaining(
                        email,
                        email,
                        pageable
                );
    }

    public Page<Transaction> getTransactionsByAmountRange(
            BigDecimal minAmount,
            BigDecimal maxAmount,
            int page,
            int size) {

        logger.info("Fetching transactions by amount range. Min: {}, Max: {}",
                minAmount,
                maxAmount);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("timestamp").descending()
                );

        return transactionRepository.findByAmountBetween(
                minAmount,
                maxAmount,
                pageable
        );
    }
    public DashboardInsightResponse getDashboardInsights() {

    // Logged-in user
    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    // Fetch all transactions of logged-in user
    List<Transaction> transactions =
            transactionRepository
                    .findBySenderEmailOrReceiverEmail(
                            email,
                            email
                    );

    BigDecimal totalIncome = BigDecimal.ZERO;
    BigDecimal totalExpense = BigDecimal.ZERO;

    Map<TransactionCategory, BigDecimal> categoryTotals =
            new HashMap<>();

    // Calculate analytics
    for (Transaction tx : transactions) {

        // Money received
        if (email.equals(tx.getReceiverEmail())
                && tx.getStatus() == TransactionStatus.SUCCESS) {

            totalIncome = totalIncome.add(tx.getAmount());
        }

        // Money sent
        if (email.equals(tx.getSenderEmail())
                && tx.getStatus() == TransactionStatus.SUCCESS) {

            totalExpense = totalExpense.add(tx.getAmount());

            TransactionCategory category = tx.getCategory();

            categoryTotals.put(
                    category,
                    categoryTotals.getOrDefault(
                            category,
                            BigDecimal.ZERO
                    ).add(tx.getAmount())
            );
        }
    }

    // Net Cash Flow
    BigDecimal netCashFlow =
            totalIncome.subtract(totalExpense);

    // Top Spending Category
    TransactionCategory topCategory = null;
    BigDecimal topCategoryAmount = BigDecimal.ZERO;

    for (Map.Entry<TransactionCategory, BigDecimal> entry
            : categoryTotals.entrySet()) {

        if (entry.getValue().compareTo(topCategoryAmount) > 0) {

            topCategory = entry.getKey();
            topCategoryAmount = entry.getValue();
        }
    }

    // Return response
    return new DashboardInsightResponse(

            totalIncome,
            totalExpense,
            netCashFlow,
            transactions.size(),
            topCategory == null
                    ? "NONE"
                    : topCategory.name(),
            topCategoryAmount
    );
}
       
}