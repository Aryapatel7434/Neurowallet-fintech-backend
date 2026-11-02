
package com.smartwallet.service;

import com.smartwallet.model.ScheduledTransaction;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.repository.ScheduledTransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTransactionProcessor {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    ScheduledTransactionProcessor.class
            );

    private final ScheduledTransactionRepository repository;

    private final ScheduledTransactionExecutionService executionService;

    public ScheduledTransactionProcessor(
            ScheduledTransactionRepository repository,
            ScheduledTransactionExecutionService executionService) {

        this.repository = repository;
        this.executionService = executionService;
    }

    /**
     * Checks for due scheduled payments every 30 seconds.
     *
     * Each payment is executed through a separate transactional
     * service method. The execution service locks the scheduled
     * transaction and both wallets before processing the payment.
     */
    @Scheduled(fixedRate = 30000)
    public void processScheduledTransactions() {

        LocalDateTime processingTime = LocalDateTime.now();

        logger.info(
                "Scheduled transaction processor started at {}",
                processingTime
        );

        int processedCount = 0;
        int successfulCount = 0;
        int skippedCount = 0;
        int errorCount = 0;

        try {

            // ========================================================
            // STEP 1: FETCH DUE PENDING TRANSACTIONS
            // ========================================================

            List<ScheduledTransaction> dueTransactions =
                    repository.findByStatusAndScheduledTimeBefore(
                            TransactionStatus.PENDING,
                            processingTime.plusNanos(1)
                    );

            if (dueTransactions == null || dueTransactions.isEmpty()) {

                logger.debug(
                        "No scheduled transactions are due"
                );

                return;
            }

            logger.info(
                    "Found {} scheduled transactions for processing",
                    dueTransactions.size()
            );

            // ========================================================
            // STEP 2: PROCESS EACH PAYMENT INDEPENDENTLY
            // ========================================================

            for (ScheduledTransaction scheduledTransaction : dueTransactions) {

                if (scheduledTransaction == null) {

                    logger.warn(
                            "Skipping null scheduled transaction record"
                    );

                    skippedCount++;
                    continue;
                }

                Long scheduledTransactionId =
                        scheduledTransaction.getScheduledTransactionId();

                if (scheduledTransactionId == null
                        || scheduledTransactionId <= 0) {

                    logger.warn(
                            "Skipping scheduled transaction with invalid ID"
                    );

                    skippedCount++;
                    continue;
                }

                processedCount++;

                try {

                    logger.info(
                            "Processing scheduled transaction ID: {}",
                            scheduledTransactionId
                    );

                    boolean executed =
                            executionService.executeScheduledTransaction(
                                    scheduledTransactionId
                            );

                    if (executed) {

                        successfulCount++;

                        logger.info(
                                "Scheduled transaction {} executed successfully",
                                scheduledTransactionId
                        );

                    } else {

                        skippedCount++;

                        logger.warn(
                                "Scheduled transaction {} was not executed. "
                                + "Check its current status or failure reason.",
                                scheduledTransactionId
                        );
                    }

                } catch (Exception exception) {

                    errorCount++;

                    // Continue processing the remaining transactions.
                    logger.error(
                            "Error processing scheduled transaction ID: {}",
                            scheduledTransactionId,
                            exception
                    );
                }
            }

        } catch (Exception exception) {

            logger.error(
                    "Scheduled transaction processor encountered an error",
                    exception
            );

        } finally {

            // ========================================================
            // STEP 3: PROCESSING SUMMARY
            // ========================================================

            logger.info(
                    "Scheduled transaction processor completed. "
                    + "Candidates={}, Successful={}, Skipped={}, Errors={}",
                    processedCount,
                    successfulCount,
                    skippedCount,
                    errorCount
            );
        }
    }
}
