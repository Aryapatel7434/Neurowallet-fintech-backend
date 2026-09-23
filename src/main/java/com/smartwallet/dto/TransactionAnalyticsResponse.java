package com.smartwallet.dto;

import java.math.BigDecimal;

public class TransactionAnalyticsResponse {

    private long totalTransactions;
    private long successfulTransactions;
    private long failedTransactions;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalTransfer;

    private BigDecimal averageTransaction;
    private BigDecimal largestTransaction;

    private BigDecimal savings;
    private BigDecimal savingsRatio;

    public TransactionAnalyticsResponse(
            long totalTransactions,
            long successfulTransactions,
            long failedTransactions,
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal totalTransfer,
            BigDecimal averageTransaction,
            BigDecimal largestTransaction,
            BigDecimal savings,
            BigDecimal savingsRatio) {

        this.totalTransactions = totalTransactions;
        this.successfulTransactions = successfulTransactions;
        this.failedTransactions = failedTransactions;

        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.totalTransfer = totalTransfer;

        this.averageTransaction = averageTransaction;
        this.largestTransaction = largestTransaction;

        this.savings = savings;
        this.savingsRatio = savingsRatio;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public long getSuccessfulTransactions() {
        return successfulTransactions;
    }

    public long getFailedTransactions() {
        return failedTransactions;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public BigDecimal getTotalTransfer() {
        return totalTransfer;
    }

    public BigDecimal getAverageTransaction() {
        return averageTransaction;
    }

    public BigDecimal getLargestTransaction() {
        return largestTransaction;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public BigDecimal getSavingsRatio() {
        return savingsRatio;
    }
}