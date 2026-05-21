package com.smartwallet.controller;

import com.smartwallet.model.Wallet;
import com.smartwallet.service.WalletService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController//Tells spring this class handle rest API
@RequestMapping("/api/wallet")//Base URL
public class WalletController {

    private final WalletService walletService;//controller deplends on service layer
    //Costructtor injection
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/me")//Getmapping we are Fetching wallet Data
    @PreAuthorize("hasRole('USER')")//only Authenticated user can access this api
    public Wallet getMyWallet() {
        return walletService.getMyWallet();
    }
}