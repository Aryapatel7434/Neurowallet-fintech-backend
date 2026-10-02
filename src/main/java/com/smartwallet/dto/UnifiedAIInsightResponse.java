package com.smartwallet.dto;

import java.util.List;

public class UnifiedAIInsightResponse {

    private final String overview;
    private final String financialHealthExplanation;
    private final String keyObservation;
    private final String priorityAction;
    private final List<String> recommendations;

    public UnifiedAIInsightResponse(
            String overview,
            String financialHealthExplanation,
            String keyObservation,
            String priorityAction,
            List<String> recommendations) {

        this.overview = overview;
        this.financialHealthExplanation =
                financialHealthExplanation;
        this.keyObservation = keyObservation;
        this.priorityAction = priorityAction;
        this.recommendations = recommendations;
    }

    public String getOverview() {
        return overview;
    }

    public String getFinancialHealthExplanation() {
        return financialHealthExplanation;
    }

    public String getKeyObservation() {
        return keyObservation;
    }

    public String getPriorityAction() {
        return priorityAction;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }
}