package com.smartwallet.controller;

import com.smartwallet.dto.ScheduleTransactionRequest;
import com.smartwallet.dto.ScheduledTransactionResponse;
import com.smartwallet.service.ScheduledTransactionService;
import com.smartwallet.service.ScheduledTransactionService.ScheduledTransactionAnalytics;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scheduled-transactions")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class ScheduledTransactionController {

    private final ScheduledTransactionService service;

    public ScheduledTransactionController(
            ScheduledTransactionService service) {
        this.service = service;
    }

    // ============================================================
    // CREATE SCHEDULED PAYMENT
    // ============================================================

    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleTransaction(
            @Valid @RequestBody ScheduleTransactionRequest request) {

        String response = service.scheduleTransaction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ============================================================
    // LIST AUTHENTICATED USER'S SCHEDULED PAYMENTS
    // ============================================================

    @GetMapping
    public ResponseEntity<Page<ScheduledTransactionResponse>>
            getMyScheduledTransactions(
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.getMyScheduledTransactions(page, size)
        );
    }

    // ============================================================
    // GET ONE SCHEDULED PAYMENT
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<ScheduledTransactionResponse>
            getScheduledTransactionById(@PathVariable Long id) {

        return ResponseEntity.ok(
                service.getScheduledTransactionById(id)
        );
    }

    // ============================================================
    // CANCEL SCHEDULED PAYMENT
    // ============================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelScheduledTransaction(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.cancelScheduledTransaction(id)
        );
    }

    // ============================================================
    // GET SCHEDULED PAYMENT ANALYTICS
    // ============================================================

    @GetMapping("/analytics")
    public ResponseEntity<ScheduledTransactionAnalytics>
            getScheduledTransactionAnalytics() {

        return ResponseEntity.ok(
                service.getScheduledTransactionAnalytics()
        );
    }
}