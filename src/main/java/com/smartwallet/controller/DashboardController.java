package com.smartwallet.controller;

import com.smartwallet.dto.DashboardInsightResponse;
import com.smartwallet.exception.BadRequestException;
import com.smartwallet.service.TransactionService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public DashboardInsightResponse getDashboardInsights(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        if (fromDate != null
                && toDate != null
                && fromDate.isAfter(toDate)) {

            throw new BadRequestException(
                    "fromDate must not be after toDate");
        }

        LocalDateTime from =
                fromDate == null
                        ? null
                        : fromDate.atStartOfDay();

        LocalDateTime toExclusive =
                toDate == null
                        ? null
                        : toDate.plusDays(1).atStartOfDay();

        return transactionService.getDashboardInsights(
                from,
                toExclusive
        );
    }
}