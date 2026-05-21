package com.smartwallet.repository;

import com.smartwallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Wallet findByUserUserId(int userId);

    Wallet findByUserEmail(String email);
}