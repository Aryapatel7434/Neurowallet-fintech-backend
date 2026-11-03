
package com.smartwallet.controller;

import com.smartwallet.dto.BudgetResponse;
import com.smartwallet.dto.BudgetSummaryResponse;
import com.smartwallet.dto.BudgetStatusUpdateRequest;
import com.smartwallet.dto.CreateBudgetRequest;
import com.smartwallet.dto.UpdateBudgetRequest;

import com.smartwallet.exception.BadRequestException;

import com.smartwallet.service.BudgetService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // =========================================================
    // CREATE BUDGET
    // POST /api/budgets
    // =========================================================

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody CreateBudgetRequest request
    ) {

        BudgetResponse response =
                budgetService.createBudget(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // GET ALL USER BUDGETS
    // GET /api/budgets?page=0&size=10
    // =========================================================

    @GetMapping
    public ResponseEntity<Page<BudgetResponse>> getMyBudgets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        validatePagination(page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        return ResponseEntity.ok(
                budgetService.getMyBudgets(pageable)
        );
    }

    // =========================================================
    // GET BUDGET SUMMARY
    // GET /api/budgets/summary
    // =========================================================

    @GetMapping("/summary")
    public ResponseEntity<BudgetSummaryResponse> getBudgetSummary() {

        return ResponseEntity.ok(
                budgetService.getBudgetSummary()
        );
    }

    // =========================================================
    // GET ACTIVE BUDGETS
    // GET /api/budgets/active
    // =========================================================

    @GetMapping("/active")
    public ResponseEntity<List<BudgetResponse>> getActiveBudgets() {

        return ResponseEntity.ok(
                budgetService.getActiveBudgets()
        );
    }

    // =========================================================
    // GET SINGLE BUDGET
    // GET /api/budgets/{budgetId}
    // =========================================================

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @PathVariable Long budgetId
    ) {

        return ResponseEntity.ok(
                budgetService.getBudgetById(budgetId)
        );
    }

    // =========================================================
    // UPDATE BUDGET
    // PUT /api/budgets/{budgetId}
    // =========================================================

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long budgetId,
            @Valid @RequestBody UpdateBudgetRequest request
    ) {

        return ResponseEntity.ok(
                budgetService.updateBudget(
                        budgetId,
                        request
                )
        );
    }

    // =========================================================
    // DELETE BUDGET
    // DELETE /api/budgets/{budgetId}
    // =========================================================

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long budgetId
    ) {

        budgetService.deleteBudget(budgetId);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // UPDATE BUDGET STATUS
    // PATCH /api/budgets/{budgetId}/status
    // =========================================================

    @PatchMapping("/{budgetId}/status")
    public ResponseEntity<BudgetResponse> updateBudgetStatus(
            @PathVariable Long budgetId,
            @Valid @RequestBody BudgetStatusUpdateRequest request
    ) {

        return ResponseEntity.ok(
                budgetService.updateBudgetStatus(
                        budgetId,
                        request.getStatus()
                )
        );
    }

    // =========================================================
    // PAGINATION VALIDATION
    // =========================================================

    private void validatePagination(
            int page,
            int size
    ) {

        if (page < 0) {
            throw new BadRequestException(
                    "Page number cannot be negative."
            );
        }

        if (size < 1 || size > 100) {
            throw new BadRequestException(
                    "Page size must be between 1 and 100."
            );
        }
    }
}
