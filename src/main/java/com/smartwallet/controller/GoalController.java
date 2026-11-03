
package com.smartwallet.controller;

import com.smartwallet.dto.AddGoalContributionRequest;
import com.smartwallet.dto.CreateGoalRequest;
import com.smartwallet.dto.GoalContributionResponse;
import com.smartwallet.dto.GoalResponse;
import com.smartwallet.dto.UpdateGoalRequest;

import com.smartwallet.exception.BadRequestException;
import com.smartwallet.service.GoalService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    // =====================================================
    // CREATE FINANCIAL GOAL
    // =====================================================

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @Valid @RequestBody CreateGoalRequest request) {

        GoalResponse response =
                goalService.createGoal(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // GET ALL AUTHENTICATED USER GOALS
    // =====================================================

    @GetMapping
    public ResponseEntity<Page<GoalResponse>> getMyGoals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        validatePagination(page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                goalService.getMyGoals(pageable)
        );
    }

    // =====================================================
    // GET SINGLE GOAL
    // =====================================================

    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponse> getGoalById(
            @PathVariable Long goalId) {

        return ResponseEntity.ok(
                goalService.getGoalById(goalId)
        );
    }

    // =====================================================
    // UPDATE FINANCIAL GOAL
    // =====================================================

    @PutMapping("/{goalId}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long goalId,
            @Valid @RequestBody UpdateGoalRequest request) {

        return ResponseEntity.ok(
                goalService.updateGoal(goalId, request)
        );
    }

    // =====================================================
    // DELETE FINANCIAL GOAL
    // =====================================================

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long goalId) {

        goalService.deleteGoal(goalId);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // ADD SAVINGS CONTRIBUTION
    // =====================================================

    @PostMapping("/{goalId}/contributions")
    public ResponseEntity<GoalContributionResponse> addContribution(
            @PathVariable Long goalId,
            @Valid @RequestBody AddGoalContributionRequest request) {

        GoalContributionResponse response =
                goalService.addContribution(goalId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // GET GOAL CONTRIBUTION HISTORY
    // =====================================================

    @GetMapping("/{goalId}/contributions")
    public ResponseEntity<Page<GoalContributionResponse>>
            getContributionHistory(
                    @PathVariable Long goalId,
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size) {

        validatePagination(page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                goalService.getContributionHistory(
                        goalId,
                        pageable
                )
        );
    }

    // =====================================================
    // PAGINATION VALIDATION
    // =====================================================

    private void validatePagination(int page, int size) {

        if (page < 0) {
            throw new BadRequestException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new BadRequestException(
                    "Page size must be between 1 and 100"
            );
        }
    }
    // =====================================================
// GOAL LIFECYCLE MANAGEMENT
// =====================================================

@PatchMapping("/{goalId}/status")
public ResponseEntity<GoalResponse> updateGoalStatus(
        @PathVariable Long goalId,
        @RequestParam String status) {

    return ResponseEntity.ok(
            goalService.updateGoalStatus(goalId, status)
    );
}
}
