
package com.smartwallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateUserPreferencesRequest {

    @NotBlank(message = "Theme is required")
    @Pattern(
        regexp = "DARK|LIGHT",
        message = "Theme must be DARK or LIGHT"
    )
    private String theme;

    @NotBlank(message = "Currency is required")
    @Pattern(
        regexp = "INR|USD|EUR|GBP",
        message = "Unsupported currency"
    )
    private String preferredCurrency;

    @NotBlank(message = "Language is required")
    @Pattern(
        regexp = "EN",
        message = "Currently, only English is supported"
    )
    private String language;

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
