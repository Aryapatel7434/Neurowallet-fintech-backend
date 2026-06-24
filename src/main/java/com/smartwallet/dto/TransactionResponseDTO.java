package com.smartwallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDTO {

    private Long transactionId;
    private String senderEmail;
    private String receiverEmail;
    private BigDecimal amount;
    private String status;
    private LocalDateTime timestamp;

    public TransactionResponseDTO(
            Long transactionId,
            String senderEmail,
            String receiverEmail,
            BigDecimal amount,
            String status,
            LocalDateTime timestamp) {

        this.transactionId = transactionId;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}