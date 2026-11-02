
package com.smartwallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ScheduleTransactionRequest {

    @NotBlank(message = "Receiver email is required")
    @Email(message = "Please provide a valid receiver email")
    private String receiverEmail;

    @NotNull(message = "Transaction amount is required")
    @DecimalMin(
        value = "0.01",
        message = "Transaction amount must be greater than zero"
    )
    @Digits(
        integer = 17,
        fraction = 2,
        message = "Amount must have at most 17 integer digits and 2 decimal places"
    )
    private BigDecimal amount;

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledTime;

    public ScheduleTransactionRequest() {
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
