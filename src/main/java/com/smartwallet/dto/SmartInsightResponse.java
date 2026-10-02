package com.smartwallet.dto;

public class SmartInsightResponse {

    private final String type;
    private final String severity;
    private final String title;
    private final String message;
    private final String recommendation;

    public SmartInsightResponse(
            String type,
            String severity,
            String title,
            String message,
            String recommendation) {

        this.type = type;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.recommendation = recommendation;
    }

    public String getType() {
        return type;
    }

    public String getSeverity() {
        return severity;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getRecommendation() {
        return recommendation;
    }
}