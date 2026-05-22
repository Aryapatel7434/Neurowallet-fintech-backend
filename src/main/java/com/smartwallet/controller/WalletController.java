package com.smartwallet.controller;

import com.smartwallet.dto.AddMoneyRequest;
import com.smartwallet.model.Wallet;
import com.smartwallet.service.WalletService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.smartwallet.dto.WithdrawRequest;
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
    //it allows Logged-in users to add money into their wallet
    @PostMapping("/add-money")
    @PreAuthorize("hasRole('USER')")//this is security line only ROLE_USRR can access this API
    public Wallet addMoney(@RequestBody AddMoneyRequest request){//This Method Return wallet object
        return walletService.addMoney(request);
    }
    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('USER')")
    
    public Wallet withdrawMoney(@RequestBody WithdrawRequest request){
        return walletService.withdrawMoney(request);
    }
} 