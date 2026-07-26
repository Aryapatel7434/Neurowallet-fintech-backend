package com.smartwallet.dto;

import com.smartwallet.model.TransactionCategory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.smartwallet.model.TransactionType;
public class TransactionResponseDTO {

    private Long transactionId;
    private String senderEmail;
    private String receiverEmail;
    private BigDecimal amount;
    private String status;
    private TransactionCategory category;
    private LocalDateTime timestamp;
private TransactionType type;
    public TransactionResponseDTO(Long transactionId1, String senderEmail1, String receiverEmail1, BigDecimal amount1, String name, TransactionCategory category1, LocalDateTime timestamp1) {
    }

 public TransactionResponseDTO(
        Long transactionId,
        String senderEmail,
        String receiverEmail,
        BigDecimal amount,
        String status,
        TransactionType type,
        TransactionCategory category,
        LocalDateTime timestamp) {

    this.transactionId = transactionId;
    this.senderEmail = senderEmail;
    this.receiverEmail = receiverEmail;
    this.amount = amount;
    this.status = status;
    this.type = type;
    this.category = category;
    this.timestamp = timestamp;
}
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
public TransactionType getType() {
    return type;
}

public void setType(TransactionType type) {
    this.type = type;
}
    public TransactionCategory getCategory() {
        return category;
    }

    public void setCategory(TransactionCategory category) {
        this.category = category;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}