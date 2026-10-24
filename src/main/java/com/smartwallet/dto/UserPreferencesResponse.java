
package com.smartwallet.dto;

public class UserPreferencesResponse {

    private final String theme;
    private final String preferredCurrency;
    private final String language;

    public UserPreferencesResponse(
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
