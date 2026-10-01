package com.smartwallet.repository;

import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Page<Transaction>
    findBySenderEmailOrReceiverEmail(
            String senderEmail,
            String receiverEmail,
            Pageable pageable
    );

    Page<Transaction>
    findBySenderEmail(
            String senderEmail,
            Pageable pageable
    );

    Page<Transaction>
    findByReceiverEmail(
            String receiverEmail,
            Pageable pageable
    );

    Page<Transaction>
    findByStatus(
            TransactionStatus status,
            Pageable pageable
    );

    long countByStatus(TransactionStatus status);

    Page<Transaction>
    findBySenderEmailContainingOrReceiverEmailContaining(
            String senderEmail,
            String receiverEmail,
            Pageable pageable
    );

    Page<Transaction>
    findByAmountBetween(
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable
    );

    List<Transaction>
    findBySenderEmailOrReceiverEmail(
            String senderEmail,
            String receiverEmail
    );

    /*
     * ============================================================
     * FRAUD DETECTION QUERIES
     * ============================================================
     */

    /**
     * Retrieves transactions belonging to a user
     * from a specific point in time.
     *
     * Used by the fraud engine for recent behavioral analysis.
     */
    @Query("""
            SELECT t
            FROM Transaction t
            WHERE (t.senderEmail = :email
                   OR t.receiverEmail = :email)
              AND t.timestamp >= :from
            ORDER BY t.timestamp DESC
            """)
    List<Transaction> findUserTransactionsSince(
            @Param("email") String email,
            @Param("from") LocalDateTime from
    );

    /**
     * Retrieves successful transactions belonging to a user.
     *
     * Used to calculate normal spending behavior and
     * historical transaction averages.
     */
    @Query("""
            SELECT t
            FROM Transaction t
            WHERE (t.senderEmail = :email
                   OR t.receiverEmail = :email)
              AND t.status = :status
            ORDER BY t.timestamp DESC
            """)
    List<Transaction> findUserTransactionsByStatus(
            @Param("email") String email,
            @Param("status") TransactionStatus status
    );

    /**
     * Retrieves recent failed transactions for a user.
     *
     * Used to detect a burst of failed transactions
     * before a suspicious transaction.
     */
    @Query("""
            SELECT t
            FROM Transaction t
            WHERE (t.senderEmail = :email
                   OR t.receiverEmail = :email)
              AND (t.status = :failedStatus
                   OR t.status = :legacyFailedStatus)
              AND t.timestamp >= :from
            ORDER BY t.timestamp DESC
            """)
    List<Transaction> findRecentFailedTransactions(
            @Param("email") String email,
            @Param("failedStatus") TransactionStatus failedStatus,
            @Param("legacyFailedStatus") TransactionStatus legacyFailedStatus,
            @Param("from") LocalDateTime from
    );
}