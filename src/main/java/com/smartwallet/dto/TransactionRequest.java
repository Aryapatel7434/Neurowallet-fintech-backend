package com.smartwallet.dto;

import java.math.BigDecimal;

public class TransactionRequest {

    private String receiverEmail;

    private BigDecimal amount;

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
}