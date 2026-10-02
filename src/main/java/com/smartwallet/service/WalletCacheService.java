package com.smartwallet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class WalletCacheService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    WalletCacheService.class
            );

    /**
     * Removes stale wallet data from the cache
     * after a wallet balance-changing operation.
     *
     * MySQL remains the source of truth.
     */
    @CacheEvict(
            value = "myWallet",
            key = "#email"
    )
    public void clearWalletCache(String email) {

        if (email == null || email.isBlank()) {

            logger.warn(
                    "Wallet cache eviction skipped because email is missing"
            );

            return;
        }

        logger.debug(
                "Wallet cache cleared for user: {}",
                email
        );
    }
}