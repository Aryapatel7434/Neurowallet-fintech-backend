package com.smartwallet.dto;

import java.math.BigDecimal;

public class CategorySpendingResponse {

    private String category;
    private BigDecimal amount;
    private double percentage;

    public CategorySpendingResponse(
            String category,
            BigDecimal amount,
            double percentage) {

        this.category = category;
        this.amount = amount;
        this.percentage = percentage;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public double getPercentage() {
        return percentage;
    }
}