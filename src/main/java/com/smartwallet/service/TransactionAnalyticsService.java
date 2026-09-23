package com.smartwallet.service;

import com.smartwallet.dto.TransactionAnalyticsResponse;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.TransactionType;
import com.smartwallet.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TransactionAnalyticsService {

    private final TransactionRepository transactionRepository;

    public TransactionAnalyticsService(
            TransactionRepository transactionRepository) {

        this.transactionRepository =
                transactionRepository;
    }

    public TransactionAnalyticsResponse
            getTransactionAnalytics(String email) {

        List<Transaction> transactions =
                transactionRepository
                        .findBySenderEmailOrReceiverEmail(
                                email,
                                email
                        );

        long totalTransactions = 0;
        long successfulTransactions = 0;
        long failedTransactions = 0;

        BigDecimal totalIncome =
                BigDecimal.ZERO;

        BigDecimal totalExpense =
                BigDecimal.ZERO;

        BigDecimal totalTransfer =
                BigDecimal.ZERO;

        BigDecimal totalTransactionAmount =
                BigDecimal.ZERO;

        BigDecimal largestTransaction =
                BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            if (transaction == null
                    || transaction.getAmount() == null
                    || transaction.getStatus() == null
                    || transaction.getType() == null) {

                continue;
            }

            totalTransactions++;

            TransactionStatus status =
                    transaction.getStatus();

            if (status == TransactionStatus.SUCCESS) {

                successfulTransactions++;

            } else if (status == TransactionStatus.FAILED
                    || status == TransactionStatus.FAILD) {

                failedTransactions++;
            }

            // Only successful transactions contribute
            // to financial calculations.
            if (status != TransactionStatus.SUCCESS) {
                continue;
            }

            BigDecimal amount =
                    transaction.getAmount().abs();

            totalTransactionAmount =
                    totalTransactionAmount.add(amount);

            if (amount.compareTo(largestTransaction) > 0) {

                largestTransaction = amount;
            }

            TransactionType type =
                    transaction.getType();

            // ====================================================
            // INCOME
            // ====================================================

            if (type == TransactionType.CREDIT
                    && email.equals(
                            transaction.getReceiverEmail())) {

                totalIncome =
                        totalIncome.add(amount);
            }

            // ====================================================
            // EXPENSE
            // ====================================================

            else if (type == TransactionType.DEBIT
                    && email.equals(
                            transaction.getSenderEmail())) {

                totalExpense =
                        totalExpense.add(amount);
            }

            // ====================================================
            // TRANSFER
            // ====================================================

        else if (type == TransactionType.TRANSFER) {

    // Track total transfer volume
    totalTransfer =
            totalTransfer.add(amount);

    // Money received = income
    if (email.equals(transaction.getReceiverEmail())) {

        totalIncome =
                totalIncome.add(amount);
    }

    // Money sent = expense
    if (email.equals(transaction.getSenderEmail())) {

        totalExpense =
                totalExpense.add(amount);
    }
}
        }

        // ========================================================
        // SAVINGS
        // ========================================================

        BigDecimal savings =
                totalIncome.subtract(totalExpense);

        // ========================================================
        // SAVINGS RATIO
        // ========================================================

        BigDecimal savingsRatio =
                BigDecimal.ZERO;

        if (totalIncome.compareTo(
                BigDecimal.ZERO) > 0) {

            savingsRatio =
                    savings
                            .multiply(
                                    new BigDecimal("100")
                            )
                            .divide(
                                    totalIncome,
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        // ========================================================
        // AVERAGE TRANSACTION
        // ========================================================

        BigDecimal averageTransaction =
                BigDecimal.ZERO;

        if (successfulTransactions > 0) {

            averageTransaction =
                    totalTransactionAmount
                            .divide(
                                    BigDecimal.valueOf(
                                            successfulTransactions
                                    ),
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        // ========================================================
        // RESPONSE
        // ========================================================

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
}