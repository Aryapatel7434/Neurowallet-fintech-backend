package com.smartwallet.dto;

import java.math.BigDecimal;
import java.util.List;

public class BudgetAdvisorResponse {

    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpense;
    private BigDecimal currentSavings;

    private BigDecimal recommendedSavings;

    private BigDecimal essentialBudget;
    private BigDecimal discretionaryBudget;

    private BigDecimal essentialExpense;
    private BigDecimal discretionaryExpense;

    private String priority;
    private String summary;

    private List<String> recommendations;

    public BudgetAdvisorResponse() {
    }

    public BudgetAdvisorResponse(
            BigDecimal monthlyIncome,
            BigDecimal monthlyExpense,
            BigDecimal currentSavings,
            BigDecimal recommendedSavings,
            BigDecimal essentialBudget,
            BigDecimal discretionaryBudget,
            BigDecimal essentialExpense,
            BigDecimal discretionaryExpense,
            String priority,
            String summary,
            List<String> recommendations) {

        this.monthlyIncome = monthlyIncome;
        this.monthlyExpense = monthlyExpense;
        this.currentSavings = currentSavings;
        this.recommendedSavings = recommendedSavings;
        this.essentialBudget = essentialBudget;
        this.discretionaryBudget = discretionaryBudget;
        this.essentialExpense = essentialExpense;
        this.discretionaryExpense = discretionaryExpense;
        this.priority = priority;
        this.summary = summary;
        this.recommendations = recommendations;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getMonthlyExpense() {
        return monthlyExpense;
    }

    public void setMonthlyExpense(BigDecimal monthlyExpense) {
        this.monthlyExpense = monthlyExpense;
    }

    public BigDecimal getCurrentSavings() {
        return currentSavings;
    }

    public void setCurrentSavings(BigDecimal currentSavings) {
        this.currentSavings = currentSavings;
    }

    public BigDecimal getRecommendedSavings() {
        return recommendedSavings;
    }

    public void setRecommendedSavings(
            BigDecimal recommendedSavings) {

        this.recommendedSavings = recommendedSavings;
    }

    public BigDecimal getEssentialBudget() {
        return essentialBudget;
    }

    public void setEssentialBudget(
            BigDecimal essentialBudget) {

        this.essentialBudget = essentialBudget;
    }

    public BigDecimal getDiscretionaryBudget() {
        return discretionaryBudget;
    }

    public void setDiscretionaryBudget(
            BigDecimal discretionaryBudget) {

        this.discretionaryBudget = discretionaryBudget;
    }

    public BigDecimal getEssentialExpense() {
        return essentialExpense;
    }

    public void setEssentialExpense(
            BigDecimal essentialExpense) {

        this.essentialExpense = essentialExpense;
    }

    public BigDecimal getDiscretionaryExpense() {
        return discretionaryExpense;
    }

    public void setDiscretionaryExpense(
            BigDecimal discretionaryExpense) {

        this.discretionaryExpense = discretionaryExpense;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(
            List<String> recommendations) {

        this.recommendations = recommendations;
    }
}