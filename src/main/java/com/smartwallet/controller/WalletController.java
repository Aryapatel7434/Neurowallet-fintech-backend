package com.smartwallet.controller;

import com.smartwallet.dto.AddMoneyRequest;
import com.smartwallet.dto.WithdrawRequest;
import com.smartwallet.model.Wallet;
import com.smartwallet.service.WalletService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController//This tells spring this class handel rest apis
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    //constructor injection
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/me")//get wallet
    //Return currently logged-in user's wallet
    @PreAuthorize("hasAnyRole('USER','ADMIN')")//only Role user and Role admin can access this Apis.
    public Wallet getMyWallet() {
        return walletService.getMyWallet();
    }

    @PostMapping("/add-money")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Wallet addMoney(@RequestBody AddMoneyRequest request) {
        return walletService.addMoney(request);
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Wallet withdrawMoney(@RequestBody WithdrawRequest request) {
        return walletService.withdrawMoney(request);
    }
}