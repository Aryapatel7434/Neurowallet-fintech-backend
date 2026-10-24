
package com.smartwallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AccountDataExportResponse {

    private final ExportProfile profile;
    private final ExportWallet wallet;
    private final List<ExportTransaction> transactions;
    private final ExportPreferences preferences;
    private final ExportNotificationPreferences notificationPreferences;
    private final LocalDateTime exportedAt;

    // Transaction export completeness metadata
    private final boolean transactionsComplete;
    private final int exportedTransactionCount;
    private final int maxTransactions;

    public AccountDataExportResponse(
            ExportProfile profile,
            ExportWallet wallet,
            List<ExportTransaction> transactions,
            ExportPreferences preferences,
            ExportNotificationPreferences notificationPreferences,
            LocalDateTime exportedAt,
            boolean transactionsComplete,
            int exportedTransactionCount,
            int maxTransactions) {

        this.profile = profile;
        this.wallet = wallet;
        this.transactions = List.copyOf(transactions);
        this.preferences = preferences;
        this.notificationPreferences = notificationPreferences;
        this.exportedAt = exportedAt;
        this.transactionsComplete = transactionsComplete;
        this.exportedTransactionCount = exportedTransactionCount;
        this.maxTransactions = maxTransactions;
    }

    public ExportProfile getProfile() {
        return profile;
    }

    public ExportWallet getWallet() {
        return wallet;
    }

    public List<ExportTransaction> getTransactions() {
        return transactions;
    }

    public ExportPreferences getPreferences() {
        return preferences;
    }

    public ExportNotificationPreferences getNotificationPreferences() {
        return notificationPreferences;
    }

    public LocalDateTime getExportedAt() {
        return exportedAt;
    }

    public boolean isTransactionsComplete() {
        return transactionsComplete;
    }

    public int getExportedTransactionCount() {
        return exportedTransactionCount;
    }

    public int getMaxTransactions() {
        return maxTransactions;
    }

    public static class ExportProfile {

        private final int userId;
        private final String name;
        private final String email;
        private final String role;

        public ExportProfile(
                int userId,
                String name,
                String email,
                String role) {

            this.userId = userId;
            this.name = name;
            this.email = email;
            this.role = role;
        }

        public int getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }
    }

    public static class ExportWallet {

        private final Long walletId;
        private final BigDecimal balance;
        private final String currency;
        private final String status;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public ExportWallet(
                Long walletId,
                BigDecimal balance,
                String currency,
                String status,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {

            this.walletId = walletId;
            this.balance = balance;
            this.currency = currency;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public Long getWalletId() {
            return walletId;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public String getCurrency() {
            return currency;
        }

        public String getStatus() {
            return status;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
    }

    public static class ExportTransaction {

        private final Long transactionId;
        private final String senderEmail;
        private final String receiverEmail;
        private final BigDecimal amount;
        private final String status;
        private final String type;
        private final String category;
        private final LocalDateTime timestamp;

        public ExportTransaction(
                Long transactionId,
                String senderEmail,
                String receiverEmail,
                BigDecimal amount,
                String status,
                String type,
                String category,
                LocalDateTime timestamp) {

            this.transactionId = transactionId;
            this.senderEmail = senderEmail;
            this.receiverEmail = receiverEmail;
            this.amount = amount;
            this.status = status;
            this.type = type;
            this.category = category;
            this.timestamp = timestamp;
        }

        public Long getTransactionId() {
            return transactionId;
        }

        public String getSenderEmail() {
            return senderEmail;
        }

        public String getReceiverEmail() {
            return receiverEmail;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public String getStatus() {
            return status;
        }

        public String getType() {
            return type;
        }

        public String getCategory() {
            return category;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    public static class ExportPreferences {

        private final String theme;
        private final String preferredCurrency;
        private final String language;

        public ExportPreferences(
                String theme,
                String preferredCurrency,
                String language) {

            this.theme = theme;
            this.preferredCurrency = preferredCurrency;
            this.language = language;
        }

        public String getTheme() {
            return theme;
        }

        public String getPreferredCurrency() {
            return preferredCurrency;
        }

        public String getLanguage() {
            return language;
        }
    }

    public static class ExportNotificationPreferences {

        private final boolean emailNotifications;
        private final boolean transactionAlerts;
        private final boolean securityAlerts;

        public ExportNotificationPreferences(
                boolean emailNotifications,
                boolean transactionAlerts,
                boolean securityAlerts) {

            this.emailNotifications = emailNotifications;
            this.transactionAlerts = transactionAlerts;
            this.securityAlerts = securityAlerts;
        }

        public boolean isEmailNotifications() {
            return emailNotifications;
        }

        public boolean isTransactionAlerts() {
            return transactionAlerts;
        }

        public boolean isSecurityAlerts() {
            return securityAlerts;
        }
    }
}
