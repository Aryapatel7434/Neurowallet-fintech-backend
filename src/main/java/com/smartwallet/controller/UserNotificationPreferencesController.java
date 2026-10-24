
package com.smartwallet.controller;

import com.smartwallet.dto.NotificationPreferencesResponse;
import com.smartwallet.dto.UpdateNotificationPreferencesRequest;
import com.smartwallet.service.UserNotificationPreferencesService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/me/notification-preferences")
public class UserNotificationPreferencesController {

    private final UserNotificationPreferencesService preferencesService;

    public UserNotificationPreferencesController(
            UserNotificationPreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @GetMapping
    public NotificationPreferencesResponse getPreferences(
            Authentication authentication) {

        String email = getAuthenticatedEmail(authentication);

        return preferencesService.getPreferences(email);
    }

    @PutMapping
    public NotificationPreferencesResponse updatePreferences(
            @Valid @RequestBody
                    UpdateNotificationPreferencesRequest request,
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
