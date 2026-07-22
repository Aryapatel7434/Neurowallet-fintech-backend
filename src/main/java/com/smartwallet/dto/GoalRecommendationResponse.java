package com.smartwallet.dto;

import java.math.BigDecimal;

public class GoalRecommendationResponse {

    private String goalName;

    private BigDecimal targetAmount;

    private BigDecimal currentAmount;

    private int progressPercentage;

    private String recommendation;

    public GoalRecommendationResponse() {
    }

    public GoalRecommendationResponse(
            String goalName,
            BigDecimal targetAmount,
            BigDecimal currentAmount,
            int progressPercentage,
            String recommendation) {

        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.progressPercentage = progressPercentage;
        this.recommendation = recommendation;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}