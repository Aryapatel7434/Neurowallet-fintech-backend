
package com.smartwallet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartwallet.dto.TransactionEvent;
import com.smartwallet.model.OutboxEvent;
import com.smartwallet.repository.OutboxEventRepository;
import com.smartwallet.kafka.TransactionEventProducer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class OutboxEventPublisherService {

    private static final int MAX_ATTEMPTS = 10;
    private static final int MAX_BACKOFF_SECONDS = 300;
    private static final int PUBLISH_TIMEOUT_SECONDS = 10;

    private final OutboxEventClaimService claimService;
    private final OutboxEventRepository outboxRepository;
    private final TransactionEventProducer eventProducer;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    public OutboxEventPublisherService(
            OutboxEventClaimService claimService,
            OutboxEventRepository outboxRepository,
            TransactionEventProducer eventProducer,
            ObjectMapper objectMapper,
            PlatformTransactionManager transactionManager) {

        this.claimService = claimService;
        this.outboxRepository = outboxRepository;
        this.eventProducer = eventProducer;
        this.objectMapper = objectMapper;
        this.transactionTemplate =
                new TransactionTemplate(transactionManager);
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<OutboxEvent> events = claimService.claimEvents();

        for (OutboxEvent outboxEvent : events) {
            publishEvent(outboxEvent);
        }
    }

    private void publishEvent(OutboxEvent outboxEvent) {

        try {
            TransactionEvent event = objectMapper.readValue(
                    outboxEvent.getPayload(),
                    TransactionEvent.class
            );

            CompletableFuture<Boolean> publishFuture =
                    eventProducer.publishTransactionEvent(event);

            Boolean published = publishFuture.get(
                    PUBLISH_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
            );

            if (Boolean.TRUE.equals(published)) {
                markPublished(outboxEvent.getId());
            } else {
                markForRetry(
                        outboxEvent.getId(),
                        "Kafka producer reported unsuccessful publication"
                );
            }

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            markForRetry(
                    outboxEvent.getId(),
                    "Event publishing interrupted"
            );

        } catch (Exception exception) {

            markForRetry(
                    outboxEvent.getId(),
                    exception.getMessage() == null
                            ? exception.getClass().getSimpleName()
                            : exception.getMessage()
            );
        }
    }

    private void markPublished(Long eventId) {

        transactionTemplate.executeWithoutResult(status -> {

            OutboxEvent event = outboxRepository
                    .findByIdForUpdate(eventId)
                    .orElse(null);

            if (event == null) {
                return;
            }

            event.setStatus("PUBLISHED");
            event.setPublishedAt(LocalDateTime.now());
            event.setClaimedAt(null);
            event.setLastError(null);

            outboxRepository.save(event);
        });
    }

    private void markForRetry(Long eventId, String errorMessage) {

        transactionTemplate.executeWithoutResult(status -> {

            OutboxEvent event = outboxRepository
                    .findByIdForUpdate(eventId)
                    .orElse(null);

            if (event == null) {
                return;
            }

            event.setClaimedAt(null);
            event.setLastError(truncate(errorMessage, 2000));

            if (event.getAttempts() >= MAX_ATTEMPTS) {

                event.setStatus("FAILED");

            } else {

                event.setStatus("PENDING");

                long backoffSeconds = Math.min(
                        1L << Math.min(event.getAttempts(), 8),
                        MAX_BACKOFF_SECONDS
                );

                event.setNextAttemptAt(
                        LocalDateTime.now().plusSeconds(backoffSeconds)
                );
            }

            outboxRepository.save(event);
        });
    }

    private String truncate(String value, int maxLength) {

        if (value == null) {
            return "Unknown publishing error";
        }

        return value.length() <= maxLength
                ? value
                : value.substring(0, maxLength);
    }
}
