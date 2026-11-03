
package com.smartwallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionEvent {

    private String eventId;
    private Long scheduledTransactionId;
    private String eventType;

    private String senderEmail;
    private String receiverEmail;
    private BigDecimal amount;
    private String status;
    private LocalDateTime timestamp;

    public TransactionEvent() {
    }

    public TransactionEvent(
            String senderEmail,
            String receiverEmail,
            BigDecimal amount,
            String status,
            LocalDateTime timestamp) {

        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getScheduledTransactionId() {
        return scheduledTransactionId;
    }

    public void setScheduledTransactionId(Long scheduledTransactionId) {
        this.scheduledTransactionId = scheduledTransactionId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
