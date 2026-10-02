package com.smartwallet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
@Entity
@Table(
        name = "wallet",
        indexes = {
                @Index(
                        name = "idx_wallet_status",
                        columnList = "status"
                )
        }
)
public class Wallet implements Serializable {

    public Wallet(
            int userId,
            double par,
            String walletAddress) {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    @JsonIgnore
    private User user;

    @Column(
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(
            nullable = false,
            length = 3
    )
    private String currency = "INR";

    @Column(
            nullable = false,
            length = 20
    )
    private String status = "ACTIVE";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
    private static final long serialVersionUID = 1L;
    public Wallet() {
    }

    @PrePersist
    public void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (balance == null) {
            balance = BigDecimal.ZERO;
        }

        if (currency == null
                || currency.isBlank()) {

            currency = "INR";
        }

        if (status == null
                || status.isBlank()) {

            status = "ACTIVE";
        }
    }

    @PreUpdate
    public void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}