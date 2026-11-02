
package com.smartwallet.dto;

import com.smartwallet.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ScheduledTransactionResponse {

    private Long scheduledTransactionId;
    private String receiverEmail;
    private BigDecimal amount;
    private LocalDateTime scheduledTime;
    private TransactionStatus status;
    private LocalDateTime executedAt;
    private String failureReason;
    private LocalDateTime createdAt;

    public ScheduledTransactionResponse() {
    }

    public ScheduledTransactionResponse(
            Long scheduledTransactionId,
            String receiverEmail,
            BigDecimal amount,
            LocalDateTime scheduledTime,
            TransactionStatus status,
            LocalDateTime executedAt,
            String failureReason,
            LocalDateTime createdAt) {

        this.scheduledTransactionId = scheduledTransactionId;
        this.receiverEmail = receiverEmail;
        this.amount = amount;
        this.scheduledTime = scheduledTime;
        this.status = status;
        this.executedAt = executedAt;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
    }

    public Long getScheduledTransactionId() {
        return scheduledTransactionId;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
