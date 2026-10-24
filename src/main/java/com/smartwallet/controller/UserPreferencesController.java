
package com.smartwallet.controller;

import com.smartwallet.dto.UpdateUserPreferencesRequest;
import com.smartwallet.dto.UserPreferencesResponse;
import com.smartwallet.service.UserPreferencesService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users/me/preferences")
public class UserPreferencesController {

    private final UserPreferencesService preferencesService;

    public UserPreferencesController(
            UserPreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @GetMapping
    public UserPreferencesResponse getPreferences(
            Authentication authentication) {

        String email = getAuthenticatedEmail(authentication);
        return preferencesService.getPreferences(email);
    }

    @PutMapping
    public UserPreferencesResponse updatePreferences(
            @Valid @RequestBody UpdateUserPreferencesRequest request,
            Authentication authentication) {

        String email = getAuthenticatedEmail(authentication);

        return preferencesService.updatePreferences(email, request);
    }

    private String getAuthenticatedEmail(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        return authentication.getName();
    }
}
