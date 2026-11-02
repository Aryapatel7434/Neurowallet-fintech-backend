
package com.smartwallet.controller;

import com.smartwallet.dto.ScheduleTransactionRequest;
import com.smartwallet.dto.ScheduledTransactionResponse;
import com.smartwallet.service.ScheduledTransactionService;

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

    // Create scheduled payment
    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleTransaction(
            @Valid @RequestBody ScheduleTransactionRequest request) {

        String response = service.scheduleTransaction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // List authenticated user's scheduled payments
    @GetMapping
    public ResponseEntity<Page<ScheduledTransactionResponse>>
            getMyScheduledTransactions(
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.getMyScheduledTransactions(page, size)
        );
    }

    // Get one scheduled payment owned by authenticated user
    @GetMapping("/{id}")
    public ResponseEntity<ScheduledTransactionResponse>
            getScheduledTransactionById(@PathVariable Long id) {

        return ResponseEntity.ok(
                service.getScheduledTransactionById(id)
        );
    }

    // Cancel one pending scheduled payment
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelScheduledTransaction(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.cancelScheduledTransaction(id)
        );
    }
}
