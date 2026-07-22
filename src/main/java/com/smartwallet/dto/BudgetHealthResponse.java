package com.smartwallet.dto;

import java.math.BigDecimal;

public class BudgetHealthResponse {

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private BigDecimal remainingBudget;

    private int healthPercentage;

    private String status;

    public BudgetHealthResponse() {
    }

    public BudgetHealthResponse(
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal remainingBudget,
            int healthPercentage,
            String status) {

        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.remainingBudget = remainingBudget;
        this.healthPercentage = healthPercentage;
        this.status = status;
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

    public BigDecimal getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(BigDecimal remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public int getHealthPercentage() {
        return healthPercentage;
    }

    public void setHealthPercentage(int healthPercentage) {
        this.healthPercentage = healthPercentage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}