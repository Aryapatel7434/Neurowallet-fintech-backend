
package com.smartwallet.repository;

import com.smartwallet.model.Goal;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    // Get all goals belonging to a user with pagination.
    Page<Goal> findByUserUserId(
            int userId,
            Pageable pageable
    );

    // Get a specific goal belonging to a user.
    Optional<Goal> findByGoalIdAndUserUserId(
            Long goalId,
            int userId
    );

    // Check goal ownership.
    boolean existsByGoalIdAndUserUserId(
            Long goalId,
            int userId
    );

    // Count goals belonging to a user by status.
    long countByUserUserIdAndStatus(
            int userId,
            String status
    );

    // Calculate total target amount for a user's goals.
    @Query("""
           SELECT COALESCE(SUM(g.targetAmount), 0)
           FROM Goal g
           WHERE g.user.userId = :userId
           """)
    BigDecimal getTotalTargetAmount(
            @Param("userId") int userId
    );

    // Calculate total saved amount for a user's goals.
    @Query("""
           SELECT COALESCE(SUM(g.currentAmount), 0)
           FROM Goal g
           WHERE g.user.userId = :userId
             AND g.status <> 'CANCELLED'
           """)
    BigDecimal getTotalSavedAmount(
            @Param("userId") int userId
    );

    // Retrieve and lock a goal before updating its financial data.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           SELECT g
           FROM Goal g
           WHERE g.goalId = :goalId
             AND g.user.userId = :userId
           """)
    Optional<Goal> findByIdAndUserIdForUpdate(
            @Param("goalId") Long goalId,
            @Param("userId") int userId
    );
}
