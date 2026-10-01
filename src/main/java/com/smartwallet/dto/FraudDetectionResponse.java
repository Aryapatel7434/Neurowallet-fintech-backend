package com.smartwallet.dto;

import com.smartwallet.model.FraudRiskLevel;

import java.math.BigDecimal;
import java.util.List;

public class FraudDetectionResponse {

    private Long transactionId;

    private FraudRiskLevel riskLevel;

    private BigDecimal riskScore;

    private String reason;

    private String recommendation;

    private List<String> riskFactors;
    private FraudAIAnalysisResponse aiAnalysis;

    public FraudDetectionResponse() {
    }

    public FraudDetectionResponse(
            Long transactionId,
            FraudRiskLevel riskLevel,
            BigDecimal riskScore,
            String reason,
            String recommendation,
            List<String> riskFactors) {

        this.transactionId = transactionId;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.reason = reason;
        this.recommendation = recommendation;
        this.riskFactors = riskFactors;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public FraudRiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(FraudRiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public List<String> getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(List<String> riskFactors) {
        this.riskFactors = riskFactors;
    }
    public FraudAIAnalysisResponse getAiAnalysis() {
    return aiAnalysis;
}

public void setAiAnalysis(
        FraudAIAnalysisResponse aiAnalysis) {
    this.aiAnalysis = aiAnalysis;
}
}