
package com.smartwallet.service;

import com.smartwallet.model.ScheduledTransaction;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionCategory;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.TransactionType;
import com.smartwallet.model.Wallet;
import com.smartwallet.model.WalletTransaction;

import com.smartwallet.repository.ScheduledTransactionRepository;
import com.smartwallet.repository.TransactionRepository;
import com.smartwallet.repository.WalletRepository;
import com.smartwallet.repository.WalletTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class ScheduledTransactionExecutionService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    ScheduledTransactionExecutionService.class
            );

    private final ScheduledTransactionRepository scheduledRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletCacheService walletCacheService;
    private final NotificationService notificationService;
    private final OutboxEventService outboxEventService;

    public ScheduledTransactionExecutionService(
            ScheduledTransactionRepository scheduledRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            WalletTransactionRepository walletTransactionRepository,
            WalletCacheService walletCacheService,
            NotificationService notificationService,
            OutboxEventService outboxEventService) {

        this.scheduledRepository = scheduledRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletCacheService = walletCacheService;
        this.notificationService = notificationService;
        this.outboxEventService = outboxEventService;
    }

    @Transactional
    public boolean executeScheduledTransaction(Long scheduledTransactionId) {

        if (scheduledTransactionId == null || scheduledTransactionId <= 0) {
            logger.warn("Invalid scheduled transaction ID");
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        // STEP 1: LOCK THE SCHEDULED PAYMENT

        ScheduledTransaction scheduledTransaction =
                scheduledRepository.findByIdForUpdate(
                        scheduledTransactionId
                ).orElse(null);

        if (scheduledTransaction == null) {
            logger.warn(
                    "Scheduled transaction not found: {}",
                    scheduledTransactionId
            );
            return false;
        }

        // STEP 2: VALIDATE PAYMENT STATE

        if (scheduledTransaction.getStatus() != TransactionStatus.PENDING) {
            logger.info(
                    "Scheduled transaction {} is no longer pending",
                    scheduledTransactionId
            );
            return false;
        }

        if (scheduledTransaction.getScheduledTime() == null
                || scheduledTransaction.getScheduledTime().isAfter(now)) {
            return false;
        }

        // STEP 3: VALIDATE AND NORMALIZE EMAILS

        String rawSenderEmail = scheduledTransaction.getSenderEmail();
        String rawReceiverEmail = scheduledTransaction.getReceiverEmail();
        BigDecimal amount = scheduledTransaction.getAmount();

        if (rawSenderEmail == null || rawSenderEmail.isBlank()
                || rawReceiverEmail == null || rawReceiverEmail.isBlank()) {

            return markFailed(
                    scheduledTransaction,
                    "Invalid sender or receiver details",
                    now
            );
        }

        final String senderEmail =
                rawSenderEmail.trim().toLowerCase(Locale.ROOT);

        final String receiverEmail =
                rawReceiverEmail.trim().toLowerCase(Locale.ROOT);

        if (senderEmail.equals(receiverEmail)) {
            return markFailed(
                    scheduledTransaction,
                    "Sender and receiver cannot be the same",
                    now
            );
        }

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            return markFailed(
                    scheduledTransaction,
                    "Invalid scheduled payment amount",
                    now
            );
        }

        if (amount.scale() > 2 || amount.precision() > 19) {
            return markFailed(
                    scheduledTransaction,
                    "Invalid scheduled payment amount precision",
                    now
            );
        }

        // STEP 4: LOCK BOTH WALLETS IN A CONSISTENT ORDER

        Wallet senderWallet;
        Wallet receiverWallet;

        if (senderEmail.compareTo(receiverEmail) < 0) {

            senderWallet =
                    walletRepository.findByUserEmailForUpdate(senderEmail);

            receiverWallet =
                    walletRepository.findByUserEmailForUpdate(receiverEmail);

        } else {

            receiverWallet =
                    walletRepository.findByUserEmailForUpdate(receiverEmail);

            senderWallet =
                    walletRepository.findByUserEmailForUpdate(senderEmail);
        }

        if (senderWallet == null) {
            return markFailed(
                    scheduledTransaction,
                    "Sender wallet not found",
                    now
            );
        }

        if (receiverWallet == null) {
            return markFailed(
                    scheduledTransaction,
                    "Receiver wallet not found",
                    now
            );
        }

        // STEP 5: VALIDATE WALLET STATUS

        if (!"ACTIVE".equalsIgnoreCase(senderWallet.getStatus())) {
            return markFailed(
                    scheduledTransaction,
                    "Sender wallet is not active",
                    now
            );
        }

        if (!"ACTIVE".equalsIgnoreCase(receiverWallet.getStatus())) {
            return markFailed(
                    scheduledTransaction,
                    "Receiver wallet is not active",
                    now
            );
        }

        // STEP 6: VALIDATE WALLET BALANCES

        if (senderWallet.getBalance() == null
                || receiverWallet.getBalance() == null) {

            return markFailed(
                    scheduledTransaction,
                    "Wallet balance is invalid",
                    now
            );
        }

        if (senderWallet.getBalance().compareTo(BigDecimal.ZERO) < 0
                || receiverWallet.getBalance().compareTo(BigDecimal.ZERO) < 0) {

            return markFailed(
                    scheduledTransaction,
                    "Wallet contains an invalid negative balance",
                    now
            );
        }

        // STEP 7: CHECK AVAILABLE BALANCE

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            return markFailed(
                    scheduledTransaction,
                    "Insufficient wallet balance",
                    now
            );
        }

        // STEP 8: UPDATE BOTH WALLET BALANCES

        senderWallet.setBalance(
                senderWallet.getBalance().subtract(amount)
        );

        receiverWallet.setBalance(
                receiverWallet.getBalance().add(amount)
        );

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // STEP 9: CREATE NORMAL TRANSACTION HISTORY

        Transaction transaction = new Transaction(
                senderEmail,
                receiverEmail,
                amount,
                TransactionStatus.SUCCESS,
                TransactionType.TRANSFER,
                TransactionCategory.TRANSFER,
                now
        );

        transaction.setScheduledTransactionId(scheduledTransactionId);

        transactionRepository.save(transaction);

        // STEP 10: CREATE SENDER DEBIT LEDGER ENTRY

        WalletTransaction senderLedger = new WalletTransaction();

        senderLedger.setWallet(senderWallet);
        senderLedger.setAmount(amount);
        senderLedger.setType("DEBIT");
        senderLedger.setCreatedAt(now);

        walletTransactionRepository.save(senderLedger);

        // STEP 11: CREATE RECEIVER CREDIT LEDGER ENTRY

        WalletTransaction receiverLedger = new WalletTransaction();

        receiverLedger.setWallet(receiverWallet);
        receiverLedger.setAmount(amount);
        receiverLedger.setType("CREDIT");
        receiverLedger.setCreatedAt(now);

        walletTransactionRepository.save(receiverLedger);

        // STEP 12: UPDATE SCHEDULED PAYMENT STATUS

        scheduledTransaction.setStatus(TransactionStatus.SUCCESS);
        scheduledTransaction.setExecutedAt(now);
        scheduledTransaction.setFailureReason(null);

        scheduledRepository.save(scheduledTransaction);

        // STEP 13: CREATE NOTIFICATIONS

        notificationService.createNotification(
                senderEmail,
                "₹" + amount + " scheduled payment sent to " + receiverEmail
        );

        notificationService.createNotification(
                receiverEmail,
                "₹" + amount + " scheduled payment received from " + senderEmail
        );

        // STEP 14: SAVE KAFKA EVENT IN TRANSACTIONAL OUTBOX

        // This joins the current database transaction because
        // OutboxEventService uses Propagation.MANDATORY.
        //
        // If the outbox insert fails, the wallet transfer also rolls back.

        outboxEventService.createScheduledTransactionEvent(
                scheduledTransactionId,
                senderEmail,
                receiverEmail,
                amount,
                now
        );

        // STEP 15: INVALIDATE CACHE AFTER DATABASE COMMIT

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {

                        try {

                            walletCacheService.clearWalletCache(senderEmail);
                            walletCacheService.clearWalletCache(receiverEmail);

                        } catch (Exception exception) {

                            logger.error(
                                    "Wallet cache invalidation failed for scheduled transaction {}",
                                    scheduledTransactionId,
                                    exception
                            );
                        }
                    }
                }
        );

        logger.info(
                "Scheduled transaction {} executed successfully. Outbox event created.",
                scheduledTransactionId
        );

        return true;
    }

    /**
     * Marks an invalid or unsuccessful scheduled payment as FAILED.
     */
    private boolean markFailed(
            ScheduledTransaction scheduledTransaction,
            String reason,
            LocalDateTime executedAt) {

        scheduledTransaction.setStatus(TransactionStatus.FAILED);
        scheduledTransaction.setExecutedAt(executedAt);

        scheduledTransaction.setFailureReason(
                reason.length() > 1000
                        ? reason.substring(0, 1000)
                        : reason
        );

        scheduledRepository.save(scheduledTransaction);

        logger.warn(
                "Scheduled transaction {} failed: {}",
                scheduledTransaction.getScheduledTransactionId(),
                reason
        );

        return false;
    }
}
