package com.smartwallet.dto;

import com.smartwallet.model.TransactionCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransferMoneyRequest {

    @NotBlank
    @Email
    private String receiverEmail;

    @NotNull
    @DecimalMin(value = "1.0")
    private BigDecimal amount;
    private TransactionCategory category;
    public TransferMoneyRequest() {
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(
            String receiverEmail
    ) {
        this.receiverEmail = receiverEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount
    ) {
        this.amount = amount;
    }

   public TransactionCategory getCategory() {
    return category;
}

public void setCategory(TransactionCategory category) {
    this.category = category;
}
}