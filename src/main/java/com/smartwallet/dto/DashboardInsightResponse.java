package com.smartwallet.dto;

import java.math.BigDecimal;

public class DashboardInsightResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netCashFlow;

    private long transactionCount;

    private String topCategory;
    private BigDecimal topCategoryAmount;

    public DashboardInsightResponse() {
    }

    public DashboardInsightResponse(
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal netCashFlow,
            long transactionCount,
            String topCategory,
            BigDecimal topCategoryAmount) {

        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netCashFlow = netCashFlow;
        this.transactionCount = transactionCount;
        this.topCategory = topCategory;
        this.topCategoryAmount = topCategoryAmount;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getNetCashFlow() {
        return netCashFlow;
    }

    public void setNetCashFlow(BigDecimal netCashFlow) {
        this.netCashFlow = netCashFlow;
    }

    public long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getTopCategory() {
        return topCategory;
    }

    public void setTopCategory(String topCategory) {
        this.topCategory = topCategory;
    }

    public BigDecimal getTopCategoryAmount() {
        return topCategoryAmount;
    }

    public void setTopCategoryAmount(BigDecimal topCategoryAmount) {
        this.topCategoryAmount = topCategoryAmount;
    }
}