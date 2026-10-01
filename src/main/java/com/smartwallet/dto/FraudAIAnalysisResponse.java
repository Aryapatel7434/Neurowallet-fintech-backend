package com.smartwallet.dto;

import java.util.List;

public class FraudAIAnalysisResponse {

    private String analysis;
    private String recommendation;
    private List<String> observations;

    public FraudAIAnalysisResponse() {
    }

    public FraudAIAnalysisResponse(
            String analysis,
            String recommendation,
            List<String> observations) {

        this.analysis = analysis;
        this.recommendation = recommendation;
        this.observations = observations;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public List<String> getObservations() {
        return observations;
    }

    public void setObservations(List<String> observations) {
        this.observations = observations;
    }
}