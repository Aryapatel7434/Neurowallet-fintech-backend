package com.smartwallet.dto;

public class FinancialHealthScoreResponse {

    private final int score;
    private final String level;
    private final String summary;

    private final int savingsScore;
    private final int budgetScore;
    private final int spendingScore;
    private final int concentrationScore;
    private final int riskScore;

    public FinancialHealthScoreResponse(
            int score,
            String level,
            String summary,
            int savingsScore,
            int budgetScore,
            int spendingScore,
            int concentrationScore,
            int riskScore) {

        this.score = score;
        this.level = level;
        this.summary = summary;
        this.savingsScore = savingsScore;
        this.budgetScore = budgetScore;
        this.spendingScore = spendingScore;
        this.concentrationScore = concentrationScore;
        this.riskScore = riskScore;
    }

    public int getScore() {
        return score;
    }

    public String getLevel() {
        return level;
    }

    public String getSummary() {
        return summary;
    }

    public int getSavingsScore() {
        return savingsScore;
    }

    public int getBudgetScore() {
        return budgetScore;
    }

    public int getSpendingScore() {
        return spendingScore;
    }

    public int getConcentrationScore() {
        return concentrationScore;
    }

    public int getRiskScore() {
        return riskScore;
    }
}