package com.smartwallet.controller;

import com.smartwallet.dto.AddMoneyRequest;
import com.smartwallet.dto.WithdrawRequest;
import com.smartwallet.model.Wallet;
import com.smartwallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.smartwallet.model.WalletTransaction;
import java.util.List;
import com.smartwallet.dto.TransferMoneyRequest;
@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Wallet getMyWallet() {
        return walletService.getMyWallet();
    }
        @GetMapping("/transactions")
@PreAuthorize(
        "hasAnyRole('USER','ADMIN')"
)
public List<WalletTransaction>
getWalletTransactions() {

    return walletService
            .getWalletTransactions();
}
    @PostMapping("/add-money")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Wallet addMoney(@Valid @RequestBody AddMoneyRequest request) {
        return walletService.addMoney(request);
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Wallet withdrawMoney(@Valid @RequestBody WithdrawRequest request) {
        return walletService.withdrawMoney(request);
    }
    @PostMapping("/transfer")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public String transferMoney(
        @Valid
        @RequestBody
        TransferMoneyRequest request
) {

    return walletService
            .transferMoney(
                    request
            );
}
}