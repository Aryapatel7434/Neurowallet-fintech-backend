package com.smartwallet.dto;

import java.math.BigDecimal;
import java.util.List;

public class FinancialAnalysisContext {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal savings;
    private BigDecimal savingsRatio;

    private BigDecimal totalSpending;
    private BigDecimal averageSpending;
    private BigDecimal largestTransaction;

    private String topCategory;
    private BigDecimal topCategoryAmount;
    private double topCategoryPercentage;

    private int activeCategories;
    private long highValueTransactionCount;

    private String spendingConcentration;

    private List<CategorySpendingResponse> categories;

    public FinancialAnalysisContext(
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal savings,
            BigDecimal savingsRatio,
            BigDecimal totalSpending,
            BigDecimal averageSpending,
            BigDecimal largestTransaction,
            String topCategory,
            BigDecimal topCategoryAmount,
            double topCategoryPercentage,
            int activeCategories,
            long highValueTransactionCount,
            String spendingConcentration,
            List<CategorySpendingResponse> categories) {

        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.savings = savings;
        this.savingsRatio = savingsRatio;
        this.totalSpending = totalSpending;
        this.averageSpending = averageSpending;
        this.largestTransaction = largestTransaction;
        this.topCategory = topCategory;
        this.topCategoryAmount = topCategoryAmount;
        this.topCategoryPercentage = topCategoryPercentage;
        this.activeCategories = activeCategories;
        this.highValueTransactionCount =
                highValueTransactionCount;
        this.spendingConcentration =
                spendingConcentration;
        this.categories = categories;
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

    public BigDecimal getTotalSpending() {
        return totalSpending;
    }

    public BigDecimal getAverageSpending() {
        return averageSpending;
    }

    public BigDecimal getLargestTransaction() {
        return largestTransaction;
    }

    public String getTopCategory() {
        return topCategory;
    }

    public BigDecimal getTopCategoryAmount() {
        return topCategoryAmount;
    }

    public double getTopCategoryPercentage() {
        return topCategoryPercentage;
    }

    public int getActiveCategories() {
        return activeCategories;
    }

    public long getHighValueTransactionCount() {
        return highValueTransactionCount;
    }

    public String getSpendingConcentration() {
        return spendingConcentration;
    }

    public List<CategorySpendingResponse> getCategories() {
        return categories;
    }
}