
package com.smartwallet.repository;

import com.smartwallet.model.Budget;
import com.smartwallet.model.TransactionCategory;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    // =========================================================
    // USER BUDGETS
    // =========================================================

    Page<Budget> findByUserUserId(
            int userId,
            Pageable pageable
    );

    List<Budget> findByUserUserIdOrderByCreatedAtDesc(
            int userId
    );

    Optional<Budget> findByBudgetIdAndUserUserId(
            Long budgetId,
            int userId
    );

    boolean existsByBudgetIdAndUserUserId(
            Long budgetId,
            int userId
    );

    long countByUserUserId(
            int userId
    );

    // =========================================================
    // CATEGORY AND PERIOD QUERIES
    // =========================================================

    Optional<Budget> findByUserUserIdAndCategoryAndPeriodStartAndPeriodEnd(
            int userId,
            TransactionCategory category,
            LocalDate periodStart,
            LocalDate periodEnd
    );

    List<Budget> findByUserUserIdAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
            int userId,
            LocalDate date,
            LocalDate sameDate
    );

    List<Budget> findByUserUserIdAndStatus(
            int userId,
            String status
    );

    Page<Budget> findByUserUserIdAndStatus(
            int userId,
            String status,
            Pageable pageable
    );

    // =========================================================
    // ACTIVE BUDGETS FOR A GIVEN DATE
    // =========================================================

    @Query("""
            SELECT b
            FROM Budget b
            WHERE b.user.userId = :userId
              AND b.status = 'ACTIVE'
              AND b.periodStart <= :date
              AND b.periodEnd >= :date
            ORDER BY b.createdAt DESC
            """)
    List<Budget> findActiveBudgetsForDate(
            @Param("userId") int userId,
            @Param("date") LocalDate date
    );

    // =========================================================
    // CHECK CATEGORY DUPLICATE
    // =========================================================

    @Query("""
            SELECT COUNT(b) > 0
            FROM Budget b
            WHERE b.user.userId = :userId
              AND b.category = :category
              AND b.periodStart = :periodStart
              AND b.periodEnd = :periodEnd
              AND (:excludeBudgetId IS NULL
                   OR b.budgetId <> :excludeBudgetId)
            """)
    boolean existsDuplicateBudget(
            @Param("userId") int userId,
            @Param("category") TransactionCategory category,
            @Param("periodStart") LocalDate periodStart,
            @Param("periodEnd") LocalDate periodEnd,
            @Param("excludeBudgetId") Long excludeBudgetId
    );

    // =========================================================
    // LOCK BUDGET BEFORE UPDATE
    // =========================================================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT b
            FROM Budget b
            WHERE b.budgetId = :budgetId
              AND b.user.userId = :userId
            """)
    Optional<Budget> findByIdAndUserIdForUpdate(
            @Param("budgetId") Long budgetId,
            @Param("userId") int userId
    );

    // =========================================================
    // DELETE / OWNERSHIP VALIDATION
    // =========================================================

    void deleteByBudgetIdAndUserUserId(
            Long budgetId,
            int userId
    );
}
