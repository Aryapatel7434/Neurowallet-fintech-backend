
package com.smartwallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartwallet.dto.TransactionEvent;
import com.smartwallet.model.OutboxEvent;
import com.smartwallet.repository.OutboxEventRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxEventService {

    private static final String EVENT_TYPE =
            "SCHEDULED_TRANSACTION_COMPLETED";

    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxEventService(
            OutboxEventRepository outboxRepository,
            ObjectMapper objectMapper) {

        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Stores a scheduled-payment event in the outbox.
     *
     * This method must execute inside the same database
     * transaction as the wallet transfer.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void createScheduledTransactionEvent(
            Long scheduledTransactionId,
            String senderEmail,
            String receiverEmail,
            BigDecimal amount,
            LocalDateTime timestamp) {

        if (scheduledTransactionId == null
                || senderEmail == null
                || receiverEmail == null
                || amount == null
                || timestamp == null) {

            throw new IllegalArgumentException(
                    "Scheduled transaction event details cannot be null"
            );
        }

        String eventId = UUID.randomUUID().toString();

        TransactionEvent event = new TransactionEvent(
                senderEmail,
                receiverEmail,
                amount,
                "SUCCESS",
                timestamp
        );

        event.setEventId(eventId);
        event.setScheduledTransactionId(scheduledTransactionId);
        event.setEventType(EVENT_TYPE);

        final String payload;

        try {

            payload = objectMapper.writeValueAsString(event);

        } catch (JsonProcessingException exception) {

            throw new IllegalStateException(
                    "Unable to serialize scheduled transaction event",
                    exception
            );
        }

        OutboxEvent outboxEvent = new OutboxEvent();

        outboxEvent.setEventId(eventId);
        outboxEvent.setScheduledTransactionId(
                scheduledTransactionId
        );
        outboxEvent.setEventType(EVENT_TYPE);
        outboxEvent.setPayload(payload);
        outboxEvent.setStatus("PENDING");
        outboxEvent.setAttempts(0);
        outboxEvent.setNextAttemptAt(timestamp);

        outboxRepository.save(outboxEvent);
    }
}
