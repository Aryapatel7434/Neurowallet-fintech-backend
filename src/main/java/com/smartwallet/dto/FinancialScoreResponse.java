package com.smartwallet.dto;

public class FinancialScoreResponse {

    private int score;

    private String rating;

    private String risk;

    private String remark;

    public FinancialScoreResponse() {
    }

    public FinancialScoreResponse(
            int score,
            String rating,
            String risk,
            String remark) {

        this.score = score;
        this.rating = rating;
        this.risk = risk;
        this.remark = remark;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}