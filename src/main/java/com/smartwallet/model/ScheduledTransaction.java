
package com.smartwallet.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "scheduled_transaction",
    indexes = {
        @Index(
            name = "idx_scheduled_sender",
            columnList = "sender_email"
        ),
        @Index(
            name = "idx_scheduled_status_time",
            columnList = "status, scheduled_time"
        ),
        @Index(
            name = "idx_scheduled_sender_status",
            columnList = "sender_email, status"
        )
    }
)
public class ScheduledTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduledTransactionId;

    @Column(name = "sender_email", nullable = false, length = 255)
    private String senderEmail;

    @Column(name = "receiver_email", nullable = false, length = 255)
    private String receiverEmail;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TransactionStatus status;

    // Execution tracking
    private LocalDateTime executedAt;

    @Column(length = 1000)
    private String failureReason;

    // Audit timestamps
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Optimistic locking for concurrent updates
    @Version
    private Long version;

    public ScheduledTransaction() {
    }

    public ScheduledTransaction(
            String senderEmail,
            String receiverEmail,
            BigDecimal amount,
            LocalDateTime scheduledTime,
            TransactionStatus status) {

        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.amount = amount;
        this.scheduledTime = scheduledTime;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getScheduledTransactionId() {
        return scheduledTransactionId;
    }

    public void setScheduledTransactionId(Long scheduledTransactionId) {
        this.scheduledTransactionId = scheduledTransactionId;
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

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }
}
