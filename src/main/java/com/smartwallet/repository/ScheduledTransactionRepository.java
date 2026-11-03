package com.smartwallet.repository;

import com.smartwallet.model.ScheduledTransaction;
import com.smartwallet.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduledTransactionRepository
        extends JpaRepository<ScheduledTransaction, Long> {

    // Find due scheduled transactions
    List<ScheduledTransaction> findByStatusAndScheduledTimeBefore(
            TransactionStatus status,
            LocalDateTime time
    );

    // Find and lock all due scheduled transactions
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           SELECT st
           FROM ScheduledTransaction st
           WHERE st.status = :status
             AND st.scheduledTime <= :time
           ORDER BY st.scheduledTime ASC
           """)
    List<ScheduledTransaction> findDueTransactionsForUpdate(
            @Param("status") TransactionStatus status,
            @Param("time") LocalDateTime time
    );

    // Find and lock one scheduled transaction by ID
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           SELECT st
           FROM ScheduledTransaction st
           WHERE st.scheduledTransactionId = :id
           """)
    Optional<ScheduledTransaction> findByIdForUpdate(
            @Param("id") Long id
    );

    // Find all scheduled transactions belonging to a user
    Page<ScheduledTransaction> findBySenderEmail(
            String senderEmail,
            Pageable pageable
    );

    // Find a particular scheduled transaction owned by a user
    Optional<ScheduledTransaction>
    findByScheduledTransactionIdAndSenderEmail(
            Long scheduledTransactionId,
            String senderEmail
    );

    // Find a user's scheduled transactions by status
    Page<ScheduledTransaction> findBySenderEmailAndStatus(
            String senderEmail,
            TransactionStatus status,
            Pageable pageable
    );

    // Find a user's scheduled transactions ordered by execution time
    List<ScheduledTransaction> findAllBySenderEmailOrderByScheduledTimeAsc(
            String senderEmail
    );

    // Check whether a scheduled transaction belongs to a user
    boolean existsByScheduledTransactionIdAndSenderEmail(
            Long scheduledTransactionId,
            String senderEmail
    );

    // ============================================================
    // SCHEDULED TRANSACTION ANALYTICS
    // ============================================================

    // Count all scheduled transactions created by a user
    long countBySenderEmail(String senderEmail);

    // Count transactions by user and status
    long countBySenderEmailAndStatus(
            String senderEmail,
            TransactionStatus status
    );

    // Calculate total amount of all scheduled payments for a user
    @Query("""
           SELECT COALESCE(SUM(st.amount), 0)
           FROM ScheduledTransaction st
           WHERE st.senderEmail = :senderEmail
           """)
    BigDecimal getTotalScheduledAmount(
            @Param("senderEmail") String senderEmail
    );

    // Calculate total amount of successful payments for a user
    @Query("""
           SELECT COALESCE(SUM(st.amount), 0)
           FROM ScheduledTransaction st
           WHERE st.senderEmail = :senderEmail
             AND st.status = :status
           """)
    BigDecimal getTotalAmountBySenderAndStatus(
            @Param("senderEmail") String senderEmail,
            @Param("status") TransactionStatus status
    );
}