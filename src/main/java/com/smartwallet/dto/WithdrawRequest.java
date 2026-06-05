package com.smartwallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class WithdrawRequest {

    @NotNull(message = "Withdraw amount is required")
    @DecimalMin(value = "1.00", message = "Withdraw amount must be at least 1")
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }
}