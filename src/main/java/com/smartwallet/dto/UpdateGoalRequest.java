
package com.smartwallet.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateGoalRequest {

    @NotBlank(message = "Goal name is required")
    @Size(
        min = 3,
        max = 100,
        message = "Goal name must contain 3 to 100 characters"
    )
    private String goalName;

    @Size(
        max = 500,
        message = "Description cannot exceed 500 characters"
    )
    private String description;

    @NotBlank(message = "Goal category is required")
    @Pattern(
        regexp = "EMERGENCY_FUND|EDUCATION|TRAVEL|VEHICLE|HOME|GADGETS|INVESTMENT|OTHER",
        message = "Invalid goal category"
    )
    private String category;

    @NotNull(message = "Target amount is required")
    @DecimalMin(
        value = "1.00",
        message = "Target amount must be at least 1.00"
    )
    @Digits(
        integer = 17,
        fraction = 2,
        message = "Target amount supports up to 2 decimal places"
    )
    private BigDecimal targetAmount;

    @NotNull(message = "Target date is required")
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;

    @NotBlank(message = "Priority is required")
    @Pattern(
        regexp = "LOW|MEDIUM|HIGH",
        message = "Priority must be LOW, MEDIUM or HIGH"
    )
    private String priority;

    public UpdateGoalRequest() {
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
