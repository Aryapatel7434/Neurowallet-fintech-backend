package com.smartwallet.dto;

import java.math.BigDecimal;
import java.util.List;

public class AIAssistantResponse {

    private String answer;

    private String intent;

    private String category;

    private BigDecimal amount;

    private List<String> recommendations;

    public AIAssistantResponse() {
    }

    public AIAssistantResponse(
            String answer,
            String intent,
            String category,
            BigDecimal amount,
            List<String> recommendations) {

        this.answer = answer;
        this.intent = intent;
        this.category = category;
        this.amount = amount;
        this.recommendations = recommendations;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(
            List<String> recommendations) {

        this.recommendations = recommendations;
    }
}