
package com.smartwallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class AddGoalContributionRequest {

    @NotNull(message = "Contribution amount is required")
    @DecimalMin(
        value = "0.01",
        message = "Contribution must be greater than zero"
    )
    @Digits(
        integer = 17,
        fraction = 2,
        message = "Contribution supports up to 2 decimal places"
    )
    private BigDecimal amount;

    @Size(
        max = 500,
        message = "Note cannot exceed 500 characters"
    )
    private String note;

    public AddGoalContributionRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
