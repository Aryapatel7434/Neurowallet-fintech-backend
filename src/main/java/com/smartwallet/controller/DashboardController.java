package com.smartwallet.controller;

import com.smartwallet.dto.DashboardInsightResponse;
import com.smartwallet.service.TransactionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final TransactionService transactionService;

    public DashboardController(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @GetMapping("/insights")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public DashboardInsightResponse getDashboardInsights() {

        return transactionService.getDashboardInsights();

    }

}