
package com.smartwallet.repository;

import com.smartwallet.model.Wallet;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletRepository
        extends JpaRepository<Wallet, Long> {

    Wallet findByUserUserId(int userId);

    Wallet findByUserEmail(String email);

    /**
     * Retrieves a wallet with a database-level write lock.
     *
     * The lock is held until the surrounding transaction
     * commits or rolls back.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           SELECT w
           FROM Wallet w
           WHERE w.user.email = :email
           """)
    Wallet findByUserEmailForUpdate(
            @Param("email") String email
    );
}
