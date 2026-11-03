
package com.smartwallet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "outbox_event",
    indexes = {
        @Index(
            name = "idx_outbox_status_next_attempt",
            columnList = "status, next_attempt_at"
        )
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_outbox_scheduled_transaction",
            columnNames = "scheduled_transaction_id"
        )
    }
)
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "event_id",
        nullable = false,
        unique = true,
        length = 36
    )
    private String eventId;

    @Column(
        name = "scheduled_transaction_id",
        nullable = false
    )
    private Long scheduledTransactionId;

    @Column(
        name = "event_type",
        nullable = false,
        length = 100
    )
    private String eventType;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(nullable = false)
    private int attempts = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "next_attempt_at", nullable = false)
    private LocalDateTime nextAttemptAt;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "last_error", length = 2000)
    private String lastError;

    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        if (eventId == null) {
            eventId = UUID.randomUUID().toString();
        }

        if (createdAt == null) {
            createdAt = now;
        }

        if (nextAttemptAt == null) {
            nextAttemptAt = now;
        }

        if (status == null) {
            status = "PENDING";
        }
    }

    public Long getId() {
        return id;
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

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getNextAttemptAt() {
        return nextAttemptAt;
    }

    public void setNextAttemptAt(LocalDateTime nextAttemptAt) {
        this.nextAttemptAt = nextAttemptAt;
    }

    public LocalDateTime getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(LocalDateTime claimedAt) {
        this.claimedAt = claimedAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }
}
