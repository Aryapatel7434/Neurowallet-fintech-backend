
package com.smartwallet.dto;

import java.math.BigDecimal;

public class BudgetSummaryResponse {

    private BigDecimal totalAllocated;

    private BigDecimal totalSpent;

    private BigDecimal totalRemaining;

    private BigDecimal totalOverspent;

    private Long totalBudgets;

    private Long activeBudgets;

    private Long exceededBudgets;

    private Long thresholdReachedBudgets;

    private BigDecimal overallUsagePercentage;

    public BudgetSummaryResponse() {
    }

    public BigDecimal getTotalAllocated() {
        return totalAllocated;
    }

    public void setTotalAllocated(BigDecimal totalAllocated) {
        this.totalAllocated = totalAllocated;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getTotalRemaining() {
        return totalRemaining;
    }

    public void setTotalRemaining(BigDecimal totalRemaining) {
        this.totalRemaining = totalRemaining;
    }

    public BigDecimal getTotalOverspent() {
        return totalOverspent;
    }

    public void setTotalOverspent(BigDecimal totalOverspent) {
        this.totalOverspent = totalOverspent;
    }

    public Long getTotalBudgets() {
        return totalBudgets;
    }

    public void setTotalBudgets(Long totalBudgets) {
        this.totalBudgets = totalBudgets;
    }

    public Long getActiveBudgets() {
        return activeBudgets;
    }

    public void setActiveBudgets(Long activeBudgets) {
        this.activeBudgets = activeBudgets;
    }

    public Long getExceededBudgets() {
        return exceededBudgets;
    }

    public void setExceededBudgets(Long exceededBudgets) {
        this.exceededBudgets = exceededBudgets;
    }

    public Long getThresholdReachedBudgets() {
        return thresholdReachedBudgets;
    }

    public void setThresholdReachedBudgets(Long thresholdReachedBudgets) {
        this.thresholdReachedBudgets = thresholdReachedBudgets;
    }

    public BigDecimal getOverallUsagePercentage() {
        return overallUsagePercentage;
    }

    public void setOverallUsagePercentage(
            BigDecimal overallUsagePercentage
    ) {
        this.overallUsagePercentage = overallUsagePercentage;
    }
}
