
package com.smartwallet.repository;

import com.smartwallet.model.OutboxEvent;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT event
        FROM OutboxEvent event
        WHERE event.status = 'PENDING'
          AND event.nextAttemptAt <= :now
        ORDER BY event.createdAt ASC
        """)
    List<OutboxEvent> findPendingEventsForUpdate(
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT event
        FROM OutboxEvent event
        WHERE event.status = 'PROCESSING'
          AND event.claimedAt <= :leaseExpiry
        ORDER BY event.claimedAt ASC
        """)
    List<OutboxEvent> findExpiredProcessingEventsForUpdate(
            @Param("leaseExpiry") LocalDateTime leaseExpiry,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT event
        FROM OutboxEvent event
        WHERE event.id = :id
        """)
    Optional<OutboxEvent> findByIdForUpdate(
            @Param("id") Long id
    );

    long countByStatus(String status);
}
