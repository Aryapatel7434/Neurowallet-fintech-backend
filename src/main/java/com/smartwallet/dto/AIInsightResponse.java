package com.smartwallet.dto;

public class AIInsightResponse {

    private int confidence;

    private String savingOpportunity;

    private String budgetAlert;

    private String investmentSuggestion;

    private String financialRisk;

    public AIInsightResponse() {
    }

    public AIInsightResponse(
            int confidence,
            String savingOpportunity,
            String budgetAlert,
            String investmentSuggestion,
            String financialRisk) {

        this.confidence = confidence;
        this.savingOpportunity = savingOpportunity;
        this.budgetAlert = budgetAlert;
        this.investmentSuggestion = investmentSuggestion;
        this.financialRisk = financialRisk;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public String getSavingOpportunity() {
        return savingOpportunity;
    }

    public void setSavingOpportunity(String savingOpportunity) {
        this.savingOpportunity = savingOpportunity;
    }

    public String getBudgetAlert() {
        return budgetAlert;
    }

    public void setBudgetAlert(String budgetAlert) {
        this.budgetAlert = budgetAlert;
    }

    public String getInvestmentSuggestion() {
        return investmentSuggestion;
    }

    public void setInvestmentSuggestion(String investmentSuggestion) {
        this.investmentSuggestion = investmentSuggestion;
    }

    public String getFinancialRisk() {
        return financialRisk;
    }

    public void setFinancialRisk(String financialRisk) {
        this.financialRisk = financialRisk;
    }
}