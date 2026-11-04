
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

    @Query("""
            SELECT t
            FROM Transaction t
            WHERE (t.senderEmail = :email
                   OR t.receiverEmail = :email)
              AND t.status = :failedStatus
              AND t.timestamp >= :from
            ORDER BY t.timestamp DESC
            """)
    List<Transaction> findRecentFailedTransactions(
            @Param("email") String email,
            @Param("failedStatus") TransactionStatus failedStatus,
            @Param("from") LocalDateTime from
    );

    // ============================================================
    // DASHBOARD PERIOD QUERIES
    // ============================================================

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

    // ============================================================
    // BUDGET MODULE — CATEGORY SPENDING AGGREGATION
    // ============================================================

    @Query("""
            SELECT
                t.category,
                SUM(t.amount)
            FROM Transaction t
            WHERE t.senderEmail = :email
              AND t.status = :status
              AND t.type IN :types
              AND t.timestamp >= :fromDate
              AND t.timestamp < :toDateExclusive
            GROUP BY t.category
            ORDER BY SUM(t.amount) DESC
            """)
    List<Object[]> getBudgetCategorySpending(
            @Param("email") String email,
            @Param("status") TransactionStatus status,
            @Param("types") List<TransactionType> types,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDateExclusive") LocalDateTime toDateExclusive
    );

    // ============================================================
    // ANALYTICS — TRANSACTION AGGREGATION
    // ============================================================

    /**
     * Object[] positions:
     * 0 - Total transactions
     * 1 - Successful transactions
     * 2 - Failed transactions
     * 3 - Total income
     * 4 - Total expense
     * 5 - Total transfer
     * 6 - Average successful transaction
     * 7 - Largest successful transaction
     */
    @Query("""
            SELECT
                COUNT(t),

                COUNT(
                    CASE WHEN t.status = :successStatus
                    THEN t.transactionId END
                ),

                COUNT(
                    CASE WHEN t.status = :failedStatus
                    THEN t.transactionId END
                ),

                COALESCE(
                    SUM(
                        CASE
                            WHEN t.status = :successStatus
                             AND (
                                 (t.type = :creditType
                                  AND t.receiverEmail = :email)
                                 OR
                                 (t.type = :transferType
                                  AND t.receiverEmail = :email)
                             )
                            THEN ABS(t.amount)
                            ELSE 0
                        END
                    ),
                    0
                ),

                COALESCE(
                    SUM(
                        CASE
                            WHEN t.status = :successStatus
                             AND (
                                 (t.type = :debitType
                                  AND t.senderEmail = :email)
                                 OR
                                 (t.type = :transferType
                                  AND t.senderEmail = :email)
                             )
                            THEN ABS(t.amount)
                            ELSE 0
                        END
                    ),
                    0
                ),

                COALESCE(
                    SUM(
                        CASE
                            WHEN t.status = :successStatus
                             AND t.type = :transferType
                            THEN ABS(t.amount)
                            ELSE 0
                        END
                    ),
                    0
                ),

                COALESCE(
                    AVG(
                        CASE
                            WHEN t.status = :successStatus
                            THEN ABS(t.amount)
                            ELSE NULL
                        END
                    ),
                    0
                ),

                COALESCE(
                    MAX(
                        CASE
                            WHEN t.status = :successStatus
                            THEN ABS(t.amount)
                            ELSE NULL
                        END
                    ),
                    0
                )

            FROM Transaction t

            WHERE
                (t.senderEmail = :email
                 OR t.receiverEmail = :email)

                AND (:fromDate IS NULL
                     OR t.timestamp >= :fromDate)

                AND (:toDateExclusive IS NULL
                     OR t.timestamp < :toDateExclusive)
            """)
    Object[] getTransactionAnalyticsAggregation(
            @Param("email") String email,
            @Param("successStatus") TransactionStatus successStatus,
            @Param("failedStatus") TransactionStatus failedStatus,
            @Param("creditType") TransactionType creditType,
            @Param("debitType") TransactionType debitType,
            @Param("transferType") TransactionType transferType,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDateExclusive") LocalDateTime toDateExclusive
    );

    // ============================================================
    // ANALYTICS — CATEGORY AND SPENDING AGGREGATION
    // ============================================================

    /**
     * Object[] positions:
     * 0 - TransactionCategory
     * 1 - Total spending
     * 2 - Spending transaction count
     * 3 - Largest transaction
     * 4 - High-value transaction count (>= 10000)
     */
    @Query("""
            SELECT
                t.category,
                SUM(ABS(t.amount)),
                COUNT(t),
                MAX(ABS(t.amount)),
                COUNT(
                    CASE
                        WHEN ABS(t.amount) >= 10000
                        THEN t.transactionId
                    END
                )
            FROM Transaction t
            WHERE t.senderEmail = :email
              AND t.status = :successStatus
              AND t.type IN :types
              AND t.category IS NOT NULL
              AND (:fromDate IS NULL
                   OR t.timestamp >= :fromDate)
              AND (:toDateExclusive IS NULL
                   OR t.timestamp < :toDateExclusive)
            GROUP BY t.category
            ORDER BY SUM(ABS(t.amount)) DESC
            """)
    List<Object[]> getAnalyticsCategorySpending(
            @Param("email") String email,
            @Param("successStatus") TransactionStatus successStatus,
            @Param("types") List<TransactionType> types,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDateExclusive") LocalDateTime toDateExclusive
    );
}
