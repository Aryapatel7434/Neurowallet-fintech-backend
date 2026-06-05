package com.smartwallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransactionRequest {

    @NotBlank(message = "Receiver email is required")
    @Email(message = "Invalid receiver email format")
    private String receiverEmail;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least 1")
    private BigDecimal amount;

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}