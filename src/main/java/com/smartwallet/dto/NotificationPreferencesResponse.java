
package com.smartwallet.dto;

public class NotificationPreferencesResponse {

    private final boolean emailNotifications;
    private final boolean transactionAlerts;
    private final boolean securityAlerts;

    public NotificationPreferencesResponse(
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
