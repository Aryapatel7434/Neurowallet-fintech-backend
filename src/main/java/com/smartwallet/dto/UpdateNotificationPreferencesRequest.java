
package com.smartwallet.dto;

public class UpdateNotificationPreferencesRequest {

    private boolean emailNotifications;
    private boolean transactionAlerts;
    private boolean securityAlerts;

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public boolean isTransactionAlerts() {
        return transactionAlerts;
    }

    public void setTransactionAlerts(boolean transactionAlerts) {
        this.transactionAlerts = transactionAlerts;
    }

    public boolean isSecurityAlerts() {
        return securityAlerts;
    }

    public void setSecurityAlerts(boolean securityAlerts) {
        this.securityAlerts = securityAlerts;
    }
}
