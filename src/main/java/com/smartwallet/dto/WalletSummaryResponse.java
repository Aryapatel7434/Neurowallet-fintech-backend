package com.smartwallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletSummaryResponse {

    private BigDecimal totalCredit;

    private BigDecimal totalDebit;

    private BigDecimal highestCredit;

    private BigDecimal highestDebit;

    private long totalTransactions;

    private String walletStatus;

    private String financialHealth;

    private LocalDateTime lastUpdated;

    private BigDecimal totalTransfer;

private long successfulTransfers;
    public WalletSummaryResponse(
        BigDecimal totalCredit,
        BigDecimal totalDebit,
        BigDecimal highestCredit,
        BigDecimal highestDebit,
        long totalTransactions,
        String walletStatus,
        String financialHealth,
        LocalDateTime lastUpdated,
        BigDecimal totalTransfer,
        long successfulTransfers
) {

    this.totalCredit = totalCredit;
    this.totalDebit = totalDebit;
    this.highestCredit = highestCredit;
    this.highestDebit = highestDebit;
    this.totalTransactions = totalTransactions;
    this.walletStatus = walletStatus;
    this.financialHealth = financialHealth;
    this.lastUpdated = lastUpdated;

    // ✅ These two lines were missing
    this.totalTransfer = totalTransfer;
    this.successfulTransfers = successfulTransfers;
}
    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(BigDecimal totalCredit) {
        this.totalCredit = totalCredit;
    }

    public BigDecimal getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(BigDecimal totalDebit) {
        this.totalDebit = totalDebit;
    }

    public BigDecimal getHighestCredit() {
        return highestCredit;
    }

    public void setHighestCredit(BigDecimal highestCredit) {
        this.highestCredit = highestCredit;
    }

    public BigDecimal getHighestDebit() {
        return highestDebit;
    }

    public void setHighestDebit(BigDecimal highestDebit) {
        this.highestDebit = highestDebit;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public String getWalletStatus() {
        return walletStatus;
    }

    public void setWalletStatus(String walletStatus) {
        this.walletStatus = walletStatus;
    }

    public String getFinancialHealth() {
        return financialHealth;
    }

    public void setFinancialHealth(String financialHealth) {
        this.financialHealth = financialHealth;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    public BigDecimal getTotalTransfer() {
    return totalTransfer;
}

public void setTotalTransfer(BigDecimal totalTransfer) {
    this.totalTransfer = totalTransfer;
}

public long getSuccessfulTransfers() {
    return successfulTransfers;
}

public void setSuccessfulTransfers(long successfulTransfers) {
    this.successfulTransfers = successfulTransfers;
}
}