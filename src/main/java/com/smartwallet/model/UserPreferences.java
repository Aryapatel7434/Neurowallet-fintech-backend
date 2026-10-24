
package com.smartwallet.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(
    name = "user_preferences",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_preferences_user",
            columnNames = "user_id"
        )
    }
)
public class UserPreferences implements Serializable {

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

    @Column(nullable = false, length = 10)
    private String theme = "DARK";

    @Column(nullable = false, length = 3)
    private String preferredCurrency = "INR";

    @Column(nullable = false, length = 10)
    private String language = "EN";

    public UserPreferences() {
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

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
