package com.smartwallet.repository;

import com.smartwallet.model.Transaction;
import com.smartwallet.model.Wallet;
import com.smartwallet.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository
        extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction>
    findByWalletOrderByCreatedAtDesc(
            Wallet wallet
    );

    public void save(Transaction transaction);
}