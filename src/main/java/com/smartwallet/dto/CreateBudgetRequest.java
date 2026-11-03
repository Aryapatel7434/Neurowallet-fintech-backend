
package com.smartwallet.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateBudgetRequest {

    @NotBlank(message = "Budget name is required")
    @Size(
        min = 3,
        max = 100,
        message = "Budget name must contain 3 to 100 characters"
    )
    private String budgetName;

    @NotBlank(message = "Budget category is required")
    @Pattern(
        regexp = "FOOD|SHOPPING|BILLS|TRAVEL|HEALTH|ENTERTAINMENT|EDUCATION|INVESTMENT|RENT|EMI|TRANSFER|OTHER",
        message = "Invalid budget category"
    )
    private String category;

    @NotNull(message = "Budget limit is required")
    @DecimalMin(
        value = "1.00",
        message = "Budget limit must be at least 1.00"
    )
    @Digits(
        integer = 17,
        fraction = 2,
        message = "Budget limit supports up to 2 decimal places"
    )
    private BigDecimal limitAmount;

    @NotNull(message = "Alert threshold is required")
    @Min(
        value = 1,
        message = "Alert threshold must be at least 1%"
    )
    @Max(
        value = 100,
        message = "Alert threshold cannot exceed 100%"
    )
    private Integer alertThreshold = 80;

    @NotNull(message = "Budget start date is required")
    private LocalDate periodStart;

    @NotNull(message = "Budget end date is required")
    private LocalDate periodEnd;

    public CreateBudgetRequest() {
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public Integer getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(Integer alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }
}
