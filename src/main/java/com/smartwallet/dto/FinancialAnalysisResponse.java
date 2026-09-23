package com.smartwallet.dto;

public class FinancialAnalysisResponse {

    private String summary;
    private String spendingAnalysis;
    private String riskAnalysis;
    private String recommendation;

    public FinancialAnalysisResponse() {
    }

    public FinancialAnalysisResponse(
            String summary,
            String spendingAnalysis,
            String riskAnalysis,
            String recommendation) {

        this.summary = summary;
        this.spendingAnalysis = spendingAnalysis;
        this.riskAnalysis = riskAnalysis;
        this.recommendation = recommendation;
    }

    public String getSummary() {
        return summary;
    }

    public String getSpendingAnalysis() {
        return spendingAnalysis;
    }

    public String getRiskAnalysis() {
        return riskAnalysis;
    }

    public String getRecommendation() {
        return recommendation;
    }
}