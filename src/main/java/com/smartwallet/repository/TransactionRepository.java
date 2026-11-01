
package com.smartwallet.repository;

import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionCategory;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.TransactionType;

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

    // ============================================================
    // BASIC TRANSACTION QUERIES
    // ============================================================

    Page<Transaction> findBySenderEmailOrReceiverEmail(
            String senderEmail,
            String receiverEmail,
            Pageable pageable
    );

    Page<Transaction> findBySenderEmail(
            String senderEmail,
            Pageable pageable
    );

    Page<Transaction> findByReceiverEmail(
            String receiverEmail,
            Pageable pageable
    );

    Page<Transaction> findByStatus(
            TransactionStatus status,
            Pageable pageable
    );

    long countByStatus(TransactionStatus status);

    Page<Transaction> findBySenderEmailContainingOrReceiverEmailContaining(
            String senderEmail,
            String receiverEmail,
            Pageable pageable
    );

    Page<Transaction> findByAmountBetween(
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable
    );

    List<Transaction> findBySenderEmailOrReceiverEmail(
            String senderEmail,
            String receiverEmail
    );

    // ============================================================
    // FRAUD DETECTION QUERIES
    // ============================================================

    /**
     * Retrieves a user's transactions since a given timestamp.
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
     * Retrieves a user's transactions for a specified status.
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
     * Retrieves recent failed transactions, including the legacy
     * FAILD status, for user-scoped fraud analysis.
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

    // ============================================================
    // DASHBOARD PERIOD QUERIES
    // ============================================================

    /**
     * Retrieves a user's transactions within an optional date range.
     *
     * fromDate is inclusive.
     * toDateExclusive is exclusive.
     *
     * Both null means all-time transaction history.
     */
    @Query("""
            SELECT t
            FROM Transaction t
            WHERE (t.senderEmail = :email
                   OR t.receiverEmail = :email)
              AND (:fromDate IS NULL
                   OR t.timestamp >= :fromDate)
              AND (:toDateExclusive IS NULL
                   OR t.timestamp < :toDateExclusive)
            ORDER BY t.timestamp DESC
            """)
    List<Transaction> findUserTransactionsInPeriod(
            @Param("email") String email,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDateExclusive") LocalDateTime toDateExclusive
    );

    // ============================================================
    // FILTERED USER TRANSACTION HISTORY
    // ============================================================

    /**
     * Retrieves a user's transaction history with:
     *
     * - User-scoped access
     * - Server-side pagination
     * - Sorting
     * - Date filtering
     * - Sender/receiver email search
     * - Transaction ID search
     * - Amount search
     * - Category search
     * - Transaction type search
     * - Transaction status search
     */
    @Query(
            value = """
                    SELECT t
                    FROM Transaction t
                    WHERE (t.senderEmail = :email
                           OR t.receiverEmail = :email)

                      AND (:status IS NULL
                           OR t.status = :status)

                      AND (:type IS NULL
                           OR t.type = :type)

                      AND (:category IS NULL
                           OR t.category = :category)

                      AND (:fromDate IS NULL
                           OR t.timestamp >= :fromDate)

                      AND (:toDateExclusive IS NULL
                           OR t.timestamp < :toDateExclusive)

                      AND (
                            :keyword IS NULL

                            OR LOWER(t.senderEmail)
                               LIKE LOWER(CONCAT('%', :keyword, '%'))

                            OR LOWER(t.receiverEmail)
                               LIKE LOWER(CONCAT('%', :keyword, '%'))

                            OR (
                                :transactionId IS NOT NULL
                                AND t.transactionId = :transactionId
                            )

                            OR (
                                :keywordAmount IS NOT NULL
                                AND t.amount = :keywordAmount
                            )

                            OR (
                                :keywordCategory IS NOT NULL
                                AND t.category = :keywordCategory
                            )

                            OR (
                                :keywordType IS NOT NULL
                                AND t.type = :keywordType
                            )

                            OR (
                                :keywordStatus IS NOT NULL
                                AND t.status = :keywordStatus
                            )
                          )
                    """,

            countQuery = """
                    SELECT COUNT(t)
                    FROM Transaction t
                    WHERE (t.senderEmail = :email
                           OR t.receiverEmail = :email)

                      AND (:status IS NULL
                           OR t.status = :status)

                      AND (:type IS NULL
                           OR t.type = :type)

                      AND (:category IS NULL
                           OR t.category = :category)

                      AND (:fromDate IS NULL
                           OR t.timestamp >= :fromDate)

                      AND (:toDateExclusive IS NULL
                           OR t.timestamp < :toDateExclusive)

                      AND (
                            :keyword IS NULL

                            OR LOWER(t.senderEmail)
                               LIKE LOWER(CONCAT('%', :keyword, '%'))

                            OR LOWER(t.receiverEmail)
                               LIKE LOWER(CONCAT('%', :keyword, '%'))

                            OR (
                                :transactionId IS NOT NULL
                                AND t.transactionId = :transactionId
                            )

                            OR (
                                :keywordAmount IS NOT NULL
                                AND t.amount = :keywordAmount
                            )

                            OR (
                                :keywordCategory IS NOT NULL
                                AND t.category = :keywordCategory
                            )

                            OR (
                                :keywordType IS NOT NULL
                                AND t.type = :keywordType
                            )

                            OR (
                                :keywordStatus IS NOT NULL
                                AND t.status = :keywordStatus
                            )
                          )
                    """
    )
    Page<Transaction> findFilteredUserHistory(
            @Param("email") String email,
            @Param("status") TransactionStatus status,
            @Param("type") TransactionType type,
            @Param("category") TransactionCategory category,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDateExclusive") LocalDateTime toDateExclusive,
            @Param("keyword") String keyword,
            @Param("transactionId") Long transactionId,
            @Param("keywordAmount") BigDecimal keywordAmount,
            @Param("keywordCategory") TransactionCategory keywordCategory,
            @Param("keywordType") TransactionType keywordType,
            @Param("keywordStatus") TransactionStatus keywordStatus,
            Pageable pageable
    );
}
