
package com.smartwallet.service;

import com.smartwallet.dto.BudgetResponse;
import com.smartwallet.dto.BudgetSummaryResponse;
import com.smartwallet.dto.CreateBudgetRequest;
import com.smartwallet.dto.UpdateBudgetRequest;

import com.smartwallet.exception.BadRequestException;
import com.smartwallet.exception.ResourceNotFoundException;

import com.smartwallet.model.Budget;
import com.smartwallet.model.TransactionCategory;
import com.smartwallet.model.User;

import com.smartwallet.repository.BudgetRepository;
import com.smartwallet.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final BudgetSpendingService spendingService;

    public BudgetService(
            BudgetRepository budgetRepository,
            UserRepository userRepository,
            BudgetSpendingService spendingService
    ) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.spendingService = spendingService;
    }

    // =========================================================
    // AUTHENTICATED USER
    // =========================================================

    private String getCurrentUserEmail() {

        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            throw new BadRequestException(
                    "Authenticated user is required."
            );
        }

        return authentication.getName();
    }

    private User getCurrentUser() {

        User user = userRepository.findByEmail(
                getCurrentUserEmail()
        );

        if (user == null) {
            throw new ResourceNotFoundException(
                    "Authenticated user not found."
            );
        }

        return user;
    }

    // =========================================================
    // CREATE BUDGET
    // =========================================================

    @Transactional
    public BudgetResponse createBudget(
            CreateBudgetRequest request
    ) {

        User user = getCurrentUser();

        validateBudgetRequest(
                request.getLimitAmount(),
                request.getPeriodStart(),
                request.getPeriodEnd(),
                request.getAlertThreshold()
        );

        TransactionCategory category =
                parseCategory(request.getCategory());

        validateDuplicate(
                user.getUserId(),
                category,
                request.getPeriodStart(),
                request.getPeriodEnd(),
                null
        );

        Budget budget = new Budget();

        budget.setUser(user);
        budget.setBudgetName(request.getBudgetName().trim());
        budget.setCategory(category);
        budget.setLimitAmount(request.getLimitAmount());
        budget.setAlertThreshold(request.getAlertThreshold());
        budget.setPeriodStart(request.getPeriodStart());
        budget.setPeriodEnd(request.getPeriodEnd());
        budget.setStatus("ACTIVE");

        Budget savedBudget = budgetRepository.save(budget);

        return mapToResponse(savedBudget);
    }

    // =========================================================
    // GET ALL USER BUDGETS
    // =========================================================

    @Transactional(readOnly = true)
    public Page<BudgetResponse> getMyBudgets(
            Pageable pageable
    ) {

        User user = getCurrentUser();

        return budgetRepository
                .findByUserUserId(
                        user.getUserId(),
                        pageable
                )
                .map(this::mapToResponse);
    }

    // =========================================================
    // GET ACTIVE BUDGETS
    // =========================================================

    @Transactional(readOnly = true)
    public List<BudgetResponse> getActiveBudgets() {

        User user = getCurrentUser();

        return budgetRepository
                .findActiveBudgetsForDate(
                        user.getUserId(),
                        LocalDate.now()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // GET SINGLE BUDGET
    // =========================================================

    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long budgetId) {

        Budget budget = getOwnedBudget(budgetId);

        return mapToResponse(budget);
    }

    // =========================================================
    // UPDATE BUDGET
    // =========================================================

    @Transactional
    public BudgetResponse updateBudget(
            Long budgetId,
            UpdateBudgetRequest request
    ) {

        Budget budget = getOwnedBudgetForUpdate(budgetId);

        if (!"ACTIVE".equals(budget.getStatus())) {
            throw new BadRequestException(
                    "Only active budgets can be edited."
            );
        }

        validateBudgetRequest(
                request.getLimitAmount(),
                request.getPeriodStart(),
                request.getPeriodEnd(),
                request.getAlertThreshold()
        );

        TransactionCategory category =
                parseCategory(request.getCategory());

        validateDuplicate(
                budget.getUser().getUserId(),
                category,
                request.getPeriodStart(),
                request.getPeriodEnd(),
                budgetId
        );

        budget.setBudgetName(request.getBudgetName().trim());
        budget.setCategory(category);
        budget.setLimitAmount(request.getLimitAmount());
        budget.setAlertThreshold(request.getAlertThreshold());
        budget.setPeriodStart(request.getPeriodStart());
        budget.setPeriodEnd(request.getPeriodEnd());

        return mapToResponse(
                budgetRepository.save(budget)
        );
    }

    // =========================================================
    // DELETE BUDGET
    // =========================================================

    @Transactional
    public void deleteBudget(Long budgetId) {

        Budget budget = getOwnedBudgetForUpdate(budgetId);

        budgetRepository.delete(budget);
    }

    // =========================================================
    // UPDATE BUDGET STATUS
    // =========================================================

    @Transactional
    public BudgetResponse updateBudgetStatus(
            Long budgetId,
            String newStatus
    ) {

        Budget budget = getOwnedBudgetForUpdate(budgetId);

        if (newStatus == null || newStatus.isBlank()) {
            throw new BadRequestException(
                    "Budget status is required."
            );
        }

        String normalizedStatus =
                newStatus.trim().toUpperCase();

        if (!List.of(
                "ACTIVE",
                "PAUSED",
                "CANCELLED"
        ).contains(normalizedStatus)) {

            throw new BadRequestException(
                    "Invalid budget status."
            );
        }

        if ("CANCELLED".equals(budget.getStatus())) {
            throw new BadRequestException(
                    "Cancelled budgets cannot be reactivated."
            );
        }

        if ("PAUSED".equals(budget.getStatus())
                && "CANCELLED".equals(normalizedStatus)) {

            budget.setStatus("CANCELLED");

        } else if ("PAUSED".equals(budget.getStatus())
                && "ACTIVE".equals(normalizedStatus)) {

            budget.setStatus("ACTIVE");

        } else if ("ACTIVE".equals(budget.getStatus())
                && ("PAUSED".equals(normalizedStatus)
                || "CANCELLED".equals(normalizedStatus))) {

            budget.setStatus(normalizedStatus);

        } else if (budget.getStatus().equals(normalizedStatus)) {

            return mapToResponse(budget);

        } else {

            throw new BadRequestException(
                    "Invalid budget status transition."
            );
        }

        return mapToResponse(
                budgetRepository.save(budget)
        );
    }

    // =========================================================
    // BUDGET SUMMARY
    // =========================================================

    @Transactional(readOnly = true)
    public BudgetSummaryResponse getBudgetSummary() {

        User user = getCurrentUser();

        List<Budget> budgets =
                budgetRepository
                        .findByUserUserIdOrderByCreatedAtDesc(
                                user.getUserId()
                        );

        LocalDate today = LocalDate.now();

        BigDecimal totalAllocated = ZERO;
        BigDecimal totalSpent = ZERO;
        BigDecimal totalRemaining = ZERO;
        BigDecimal totalOverspent = ZERO;

        long activeBudgets = 0;
        long exceededBudgets = 0;
        long thresholdReachedBudgets = 0;

        for (Budget budget : budgets) {

            BudgetResponse response = mapToResponse(budget);

            if ("ACTIVE".equals(budget.getStatus())
                    && !today.isBefore(budget.getPeriodStart())
                    && !today.isAfter(budget.getPeriodEnd())) {

                activeBudgets++;

                totalAllocated = totalAllocated.add(
                        response.getLimitAmount()
                );

                totalSpent = totalSpent.add(
                        response.getSpentAmount()
                );

                if (response.isOverBudget()) {
                    exceededBudgets++;

                    totalOverspent = totalOverspent.add(
                            response.getSpentAmount()
                                    .subtract(response.getLimitAmount())
                    );
                }

                if (response.isThresholdReached()) {
                    thresholdReachedBudgets++;
                }
            }
        }

        totalRemaining = totalAllocated.subtract(totalSpent);

        if (totalRemaining.compareTo(ZERO) < 0) {
            totalRemaining = ZERO;
        }

        BigDecimal overallUsage = ZERO;

        if (totalAllocated.compareTo(ZERO) > 0) {

            overallUsage = totalSpent
                    .multiply(HUNDRED)
                    .divide(
                            totalAllocated,
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        BudgetSummaryResponse summary =
                new BudgetSummaryResponse();

        summary.setTotalAllocated(totalAllocated);
        summary.setTotalSpent(totalSpent);
        summary.setTotalRemaining(totalRemaining);
        summary.setTotalOverspent(totalOverspent);

        summary.setTotalBudgets((long) budgets.size());
        summary.setActiveBudgets(activeBudgets);
        summary.setExceededBudgets(exceededBudgets);
        summary.setThresholdReachedBudgets(
                thresholdReachedBudgets
        );

        summary.setOverallUsagePercentage(overallUsage);

        return summary;
    }

    // =========================================================
    // OWNERSHIP VALIDATION
    // =========================================================

    private Budget getOwnedBudget(Long budgetId) {

        User user = getCurrentUser();

        return budgetRepository
                .findByBudgetIdAndUserUserId(
                        budgetId,
                        user.getUserId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Budget not found."
                        )
                );
    }

    private Budget getOwnedBudgetForUpdate(Long budgetId) {

        User user = getCurrentUser();

        return budgetRepository
                .findByIdAndUserIdForUpdate(
                        budgetId,
                        user.getUserId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Budget not found."
                        )
                );
    }

    // =========================================================
    // REQUEST VALIDATION
    // =========================================================

    private void validateBudgetRequest(
            BigDecimal limitAmount,
            LocalDate periodStart,
            LocalDate periodEnd,
            Integer alertThreshold
    ) {

        if (limitAmount == null
                || limitAmount.compareTo(ZERO) <= 0) {

            throw new BadRequestException(
                    "Budget limit must be greater than zero."
            );
        }

        if (periodStart == null || periodEnd == null) {
            throw new BadRequestException(
                    "Budget period dates are required."
            );
        }

        if (periodEnd.isBefore(periodStart)) {
            throw new BadRequestException(
                    "Budget end date cannot be before start date."
            );
        }

        if (alertThreshold == null
                || alertThreshold < 1
                || alertThreshold > 100) {

            throw new BadRequestException(
                    "Alert threshold must be between 1 and 100."
            );
        }
    }

    // =========================================================
    // CATEGORY VALIDATION
    // =========================================================

    private TransactionCategory parseCategory(String value) {

        if (value == null || value.isBlank()) {
            throw new BadRequestException(
                    "Budget category is required."
            );
        }

        try {
            return TransactionCategory.valueOf(
                    value.trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {

            throw new BadRequestException(
                    "Invalid transaction category."
            );
        }
    }

    // =========================================================
    // DUPLICATE VALIDATION
    // =========================================================

    private void validateDuplicate(
            int userId,
            TransactionCategory category,
            LocalDate periodStart,
            LocalDate periodEnd,
            Long excludeBudgetId
    ) {

        boolean duplicate =
                budgetRepository.existsDuplicateBudget(
                        userId,
                        category,
                        periodStart,
                        periodEnd,
                        excludeBudgetId
                );

        if (duplicate) {
            throw new BadRequestException(
                    "A budget already exists for this category "
                    + "and period."
            );
        }
    }

    // =========================================================
    // RESPONSE MAPPER
    // =========================================================

    private BudgetResponse mapToResponse(Budget budget) {

        BigDecimal spent = spendingService.getCategoryAmount(
                budget.getUser().getEmail(),
                budget.getCategory(),
                budget.getPeriodStart(),
                budget.getPeriodEnd()
        );

        BigDecimal limit = budget.getLimitAmount();

        BigDecimal remaining = limit.subtract(spent);

        boolean overBudget = spent.compareTo(limit) > 0;

        if (remaining.compareTo(ZERO) < 0) {
            remaining = ZERO;
        }

        BigDecimal usage = ZERO;

        if (limit.compareTo(ZERO) > 0) {

            usage = spent
                    .multiply(HUNDRED)
                    .divide(
                            limit,
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        boolean thresholdReached =
                usage.compareTo(
                        BigDecimal.valueOf(
                                budget.getAlertThreshold()
                        )
                ) >= 0;

        BudgetResponse response = new BudgetResponse();

        response.setBudgetId(budget.getBudgetId());
        response.setBudgetName(budget.getBudgetName());

        response.setCategory(
                budget.getCategory().name()
        );

        response.setLimitAmount(limit);
        response.setSpentAmount(spent);
        response.setRemainingAmount(remaining);
        response.setUsagePercentage(usage);

        response.setAlertThreshold(
                budget.getAlertThreshold()
        );

        response.setThresholdReached(thresholdReached);
        response.setOverBudget(overBudget);

        response.setStatus(budget.getStatus());

        response.setPeriodStart(budget.getPeriodStart());
        response.setPeriodEnd(budget.getPeriodEnd());

        response.setCreatedAt(budget.getCreatedAt());
        response.setUpdatedAt(budget.getUpdatedAt());

        return response;
    }
}
