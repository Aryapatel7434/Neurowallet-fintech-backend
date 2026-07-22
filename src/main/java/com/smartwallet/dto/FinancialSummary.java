package com.smartwallet.dto;

import java.math.BigDecimal;

public class FinancialSummary {

    private BigDecimal balance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal savings;
    private BigDecimal savingsRatio;
    private int totalTransactions;

    public FinancialSummary(
            BigDecimal balance,
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal savings,
            BigDecimal savingsRatio,
            int totalTransactions) {

        this.balance = balance;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.savings = savings;
        this.savingsRatio = savingsRatio;
        this.totalTransactions = totalTransactions;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public BigDecimal getSavingsRatio() {
        return savingsRatio;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }
}