
package com.smartwallet.service;

import com.smartwallet.model.OutboxEvent;
import com.smartwallet.repository.OutboxEventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxEventClaimService {

    private static final int BATCH_SIZE = 20;

    private static final int LEASE_MINUTES = 2;

    private final OutboxEventRepository outboxRepository;

    public OutboxEventClaimService(
            OutboxEventRepository outboxRepository) {

        this.outboxRepository = outboxRepository;
    }

    /**
     * Claims pending events and recovers abandoned processing events.
     *
     * The transaction commits before the caller contacts Kafka.
     */
    @Transactional
    public List<OutboxEvent> claimEvents() {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime leaseExpiry =
                now.minusMinutes(LEASE_MINUTES);

        List<OutboxEvent> claimedEvents = new ArrayList<>();

        // Recover events left in PROCESSING after a worker interruption.
        List<OutboxEvent> expiredEvents =
                outboxRepository.findExpiredProcessingEventsForUpdate(
                        leaseExpiry,
                        PageRequest.of(0, BATCH_SIZE)
                );

        for (OutboxEvent event : expiredEvents) {

            claim(event, now);
            claimedEvents.add(event);
        }

        // Claim new pending events.
        int remainingSlots = BATCH_SIZE - claimedEvents.size();

        if (remainingSlots > 0) {

            List<OutboxEvent> pendingEvents =
                    outboxRepository.findPendingEventsForUpdate(
                            now,
                            PageRequest.of(0, remainingSlots)
                    );

            for (OutboxEvent event : pendingEvents) {

                claim(event, now);
                claimedEvents.add(event);
            }
        }

        return claimedEvents;
    }

    private void claim(
            OutboxEvent event,
            LocalDateTime claimedAt) {

        event.setStatus("PROCESSING");
        event.setClaimedAt(claimedAt);
        event.setAttempts(event.getAttempts() + 1);

        outboxRepository.save(event);
    }
}
