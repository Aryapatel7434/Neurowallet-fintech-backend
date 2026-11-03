
package com.smartwallet.repository;

import com.smartwallet.model.GoalContribution;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface GoalContributionRepository
        extends JpaRepository<GoalContribution, Long> {

    // Get paginated contribution history for a goal.
    Page<GoalContribution> findByGoalGoalIdOrderByCreatedAtDesc(
            Long goalId,
            Pageable pageable
    );

    // Find a contribution belonging to a specific goal.
    Optional<GoalContribution> findByContributionIdAndGoalGoalId(
            Long contributionId,
            Long goalId
    );

    // Calculate total contributions for a goal.
    @Query("""
           SELECT COALESCE(SUM(c.amount), 0)
           FROM GoalContribution c
           WHERE c.goal.goalId = :goalId
           """)
    BigDecimal getTotalContributions(
            @Param("goalId") Long goalId
    );

    // Count contributions for a goal.
    long countByGoalGoalId(Long goalId);

    // Check whether a wallet transaction was already recorded.
    boolean existsByReferenceId(String referenceId);
}
