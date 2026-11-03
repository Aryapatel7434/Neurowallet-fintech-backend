
package com.smartwallet.service;

import com.smartwallet.dto.AddGoalContributionRequest;
import com.smartwallet.dto.CreateGoalRequest;
import com.smartwallet.dto.GoalContributionResponse;
import com.smartwallet.dto.GoalResponse;
import com.smartwallet.dto.UpdateGoalRequest;

import com.smartwallet.exception.BadRequestException;
import com.smartwallet.exception.ResourceNotFoundException;

import com.smartwallet.model.Goal;
import com.smartwallet.model.GoalContribution;
import com.smartwallet.model.User;

import com.smartwallet.repository.GoalContributionRepository;
import com.smartwallet.repository.GoalRepository;
import com.smartwallet.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalContributionRepository contributionRepository;
    private final UserRepository userRepository;

    public GoalService(
            GoalRepository goalRepository,
            GoalContributionRepository contributionRepository,
            UserRepository userRepository) {

        this.goalRepository = goalRepository;
        this.contributionRepository = contributionRepository;
        this.userRepository = userRepository;
    }

    // =====================================================
    // AUTHENTICATED USER
    // =====================================================

    private String getCurrentUserEmail() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    private User getCurrentUser() {

        User user = userRepository.findByEmail(
                getCurrentUserEmail()
        );

        if (user == null) {
            throw new ResourceNotFoundException(
                    "Authenticated user not found"
            );
        }

        return user;
    }

    // =====================================================
    // CREATE GOAL
    // =====================================================

    @Transactional
    public GoalResponse createGoal(CreateGoalRequest request) {

        User user = getCurrentUser();

        Goal goal = new Goal();

        goal.setUser(user);
        goal.setGoalName(request.getGoalName().trim());
        goal.setDescription(request.getDescription());
        goal.setCategory(request.getCategory());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setTargetDate(request.getTargetDate());
        goal.setPriority(request.getPriority());
        goal.setStatus("ACTIVE");

        Goal savedGoal = goalRepository.save(goal);

        return mapToGoalResponse(savedGoal);
    }

    // =====================================================
    // GET ALL USER GOALS
    // =====================================================

    @Transactional(readOnly = true)
    public Page<GoalResponse> getMyGoals(Pageable pageable) {

        User user = getCurrentUser();

        return goalRepository
                .findByUserUserId(
                        user.getUserId(),
                        pageable
                )
                .map(this::mapToGoalResponse);
    }

    // =====================================================
    // GET SINGLE GOAL
    // =====================================================

    @Transactional(readOnly = true)
    public GoalResponse getGoalById(Long goalId) {

        Goal goal = getOwnedGoal(goalId);

        return mapToGoalResponse(goal);
    }

    // =====================================================
    // UPDATE GOAL
    // =====================================================

    @Transactional
    public GoalResponse updateGoal(
            Long goalId,
            UpdateGoalRequest request) {

        Goal goal = getOwnedGoalForUpdate(goalId);

        if ("CANCELLED".equals(goal.getStatus())
                || "COMPLETED".equals(goal.getStatus())) {

            throw new BadRequestException(
                    "Completed or cancelled goals cannot be edited"
            );
        }

        if (request.getTargetAmount()
                .compareTo(goal.getCurrentAmount()) < 0) {

            throw new BadRequestException(
                    "Target amount cannot be less than the saved amount"
            );
        }

        goal.setGoalName(request.getGoalName().trim());
        goal.setDescription(request.getDescription());
        goal.setCategory(request.getCategory());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        goal.setPriority(request.getPriority());

        if (goal.getCurrentAmount()
                .compareTo(goal.getTargetAmount()) >= 0) {

            goal.setStatus("COMPLETED");
            goal.setCompletedAt(LocalDateTime.now());
        }

        return mapToGoalResponse(
                goalRepository.save(goal)
        );
    }

    // =====================================================
    // DELETE GOAL
    // =====================================================

    @Transactional
    public void deleteGoal(Long goalId) {

        Goal goal = getOwnedGoalForUpdate(goalId);

        if (contributionRepository.countByGoalGoalId(goalId) > 0) {

            throw new BadRequestException(
                    "This goal has contribution history. "
                    + "Cancel it instead of deleting it."
            );
        }

        goalRepository.delete(goal);
    }

    // =====================================================
    // ADD MANUAL CONTRIBUTION
    // =====================================================

    @Transactional
    public GoalContributionResponse addContribution(
            Long goalId,
            AddGoalContributionRequest request) {

        Goal goal = getOwnedGoalForUpdate(goalId);

        if (!"ACTIVE".equals(goal.getStatus())) {

            throw new BadRequestException(
                    "Contributions are allowed only for active goals"
            );
        }

        BigDecimal amount = request.getAmount();

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Contribution amount must be greater than zero"
            );
        }

        BigDecimal newAmount =
                goal.getCurrentAmount().add(amount);

        if (newAmount.compareTo(goal.getTargetAmount()) > 0) {

            throw new BadRequestException(
                    "Contribution exceeds the remaining goal amount"
            );
        }

        GoalContribution contribution =
                new GoalContribution();

        contribution.setGoal(goal);
        contribution.setAmount(amount);
        contribution.setNote(request.getNote());
        contribution.setSource("MANUAL");

        GoalContribution savedContribution =
                contributionRepository.save(contribution);

        goal.setCurrentAmount(newAmount);

        if (newAmount.compareTo(goal.getTargetAmount()) == 0) {

            goal.setStatus("COMPLETED");
            goal.setCompletedAt(LocalDateTime.now());
        }

        goalRepository.save(goal);

        return mapToContributionResponse(savedContribution);
    }

    // =====================================================
    // GET CONTRIBUTION HISTORY
    // =====================================================

    @Transactional(readOnly = true)
    public Page<GoalContributionResponse> getContributionHistory(
            Long goalId,
            Pageable pageable) {

        Goal goal = getOwnedGoal(goalId);

        return contributionRepository
                .findByGoalGoalIdOrderByCreatedAtDesc(
                        goal.getGoalId(),
                        pageable
                )
                .map(this::mapToContributionResponse);
    }

    // =====================================================
    // GOAL OWNERSHIP
    // =====================================================

    private Goal getOwnedGoal(Long goalId) {

        User user = getCurrentUser();

        return goalRepository
                .findByGoalIdAndUserUserId(
                        goalId,
                        user.getUserId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Goal not found"
                        )
                );
    }

    private Goal getOwnedGoalForUpdate(Long goalId) {

        User user = getCurrentUser();

        return goalRepository
                .findByIdAndUserIdForUpdate(
                        goalId,
                        user.getUserId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Goal not found"
                        )
                );
    }

    // =====================================================
    // GOAL RESPONSE MAPPER
    // =====================================================

    private GoalResponse mapToGoalResponse(Goal goal) {

        GoalResponse response = new GoalResponse();

        BigDecimal target = goal.getTargetAmount();
        BigDecimal current = goal.getCurrentAmount();

        BigDecimal remaining = target.subtract(current);

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        BigDecimal progress = current
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        target,
                        2,
                        RoundingMode.HALF_UP
                );

        if (progress.compareTo(BigDecimal.valueOf(100)) > 0) {
            progress = BigDecimal.valueOf(100);
        }

        response.setGoalId(goal.getGoalId());
        response.setGoalName(goal.getGoalName());
        response.setDescription(goal.getDescription());
        response.setCategory(goal.getCategory());

        response.setTargetAmount(target);
        response.setCurrentAmount(current);
        response.setRemainingAmount(remaining);
        response.setProgressPercentage(progress);

        response.setTargetDate(goal.getTargetDate());

        response.setRemainingDays(
                Math.max(
                        0,
                        LocalDate.now().until(
                                goal.getTargetDate()
                        ).getDays()
                )
        );

        response.setPriority(goal.getPriority());
        response.setStatus(goal.getStatus());

        response.setCreatedAt(goal.getCreatedAt());
        response.setUpdatedAt(goal.getUpdatedAt());
        response.setCompletedAt(goal.getCompletedAt());

        return response;
    }

    // =====================================================
    // CONTRIBUTION RESPONSE MAPPER
    // =====================================================

    private GoalContributionResponse mapToContributionResponse(
            GoalContribution contribution) {

        GoalContributionResponse response =
                new GoalContributionResponse();

        response.setContributionId(
                contribution.getContributionId()
        );

        response.setGoalId(
                contribution.getGoal().getGoalId()
        );

        response.setAmount(contribution.getAmount());
        response.setNote(contribution.getNote());
        response.setSource(contribution.getSource());
        response.setReferenceId(contribution.getReferenceId());
        response.setCreatedAt(contribution.getCreatedAt());

        return response;
    }
}
