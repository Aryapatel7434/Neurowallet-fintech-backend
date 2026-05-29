package com.smartwallet.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service

//This class remove old wallet data from redis cache.

//we clear cache after transaction
public class WalletCacheService {
    //Remove old cache data
    @CacheEvict(value = "myWallet", key = "#email")
    public void clearWalletCache(String email) {
        System.out.println("Wallet cache cleared for: " + email);
    }
}