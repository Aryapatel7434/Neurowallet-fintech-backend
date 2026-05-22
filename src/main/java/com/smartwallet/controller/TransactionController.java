package com.smartwallet.controller;

import com.smartwallet.dto.TransactionRequest;
import com.smartwallet.model.Transaction;
import com.smartwallet.service.TransactionService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @PostMapping("/send")
    @PreAuthorize("hasRole('USER')")
    public String sendMoney(
            @RequestBody TransactionRequest request) {

        return transactionService.sendMoney(request);
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public List<Transaction> getHistory() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return transactionService
                .getTransactionHistory(email);
    }
}