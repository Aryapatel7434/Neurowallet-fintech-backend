package com.smartwallet.controller;

import com.smartwallet.dto.TransactionAnalyticsResponse;
import com.smartwallet.dto.TransactionRequest;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.service.TransactionService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String sendMoney(@Valid @RequestBody TransactionRequest request) {
        return transactionService.sendMoney(request);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<Transaction> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return transactionService.getTransactionHistory(email, page, size);
    }

    @GetMapping("/sent")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<Transaction> getSentTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return transactionService.getSentTransactions(email, page, size);
    }

    @GetMapping("/received")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<Transaction> getReceivedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return transactionService.getReceivedTransactions(email, page, size);
    }

    @GetMapping("/admin/failed")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Transaction> getFailedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return transactionService.getTransactionsByStatus(
                TransactionStatus.FAILED,
                page,
                size
        );
    }

    @GetMapping("/admin/success")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Transaction> getSuccessTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return transactionService.getTransactionsByStatus(
                TransactionStatus.SUCCESS,
                page,
                size
        );
    }

    @GetMapping("/admin/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public TransactionAnalyticsResponse getAnalytics() {
        return transactionService.getTransactionAnalytics();
    }

    @GetMapping("/admin/search")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Transaction> searchTransactionsByEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return transactionService.searchTransactionsByEmail(
                email,
                page,
                size
        );
    }

    @GetMapping("/admin/amount-range")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Transaction> getTransactionsByAmountRange(
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return transactionService.getTransactionsByAmountRange(
                minAmount,
                maxAmount,
                page,
                size
        );
    }
}