package com.smartwallet.service;

import com.smartwallet.model.Wallet;
import com.smartwallet.repository.WalletRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service//Spring automatically creates object of this class.
public class WalletService {

    private final WalletRepository walletRepository; //used to access wallet table

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }
    //Return wallet of currently logged in user
    public Wallet getMyWallet() {

        String email = SecurityContextHolder //Global Current User Storage
                .getContext()//gets current security context
                .getAuthentication()//Return Authentication object
                .getName();//Usename/Email

        return walletRepository.findByUserEmail(email);//Repository fetches wallet using email
    }
}