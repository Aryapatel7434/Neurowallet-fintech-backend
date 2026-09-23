package com.smartwallet.dto;

import java.math.BigDecimal;

public class SpendingPatternResponse {

    private BigDecimal totalSpending;
    private BigDecimal averageSpending;
    private BigDecimal largestTransaction;

    private String topCategory;
    private BigDecimal topCategoryAmount;
    private double topCategoryPercentage;

    private int activeCategories;
    private long highValueTransactionCount;

    private String spendingConcentration;

    public SpendingPatternResponse(
            BigDecimal totalSpending,
            BigDecimal averageSpending,
            BigDecimal largestTransaction,
            String topCategory,
            BigDecimal topCategoryAmount,
            double topCategoryPercentage,
            int activeCategories,
            long highValueTransactionCount,
            String spendingConcentration) {

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
}