
package com.smartwallet.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(
    name = "user_notification_preferences",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_notification_preferences_user",
            columnNames = "user_id"
        )
    }
)
public class UserNotificationPreferences implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        unique = true
    )
    private User user;

    @Column(nullable = false)
    private boolean emailNotifications = true;

    @Column(nullable = false)
    private boolean transactionAlerts = true;

    @Column(nullable = false)
    private boolean securityAlerts = true;

    public UserNotificationPreferences() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
