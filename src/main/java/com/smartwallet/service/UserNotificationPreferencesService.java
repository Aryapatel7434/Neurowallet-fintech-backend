
package com.smartwallet.service;

import com.smartwallet.dto.NotificationPreferencesResponse;
import com.smartwallet.dto.UpdateNotificationPreferencesRequest;
import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.model.User;
import com.smartwallet.model.UserNotificationPreferences;
import com.smartwallet.repository.UserNotificationPreferencesRepository;
import com.smartwallet.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserNotificationPreferencesService {

    private final UserRepository userRepository;
    private final UserNotificationPreferencesRepository
            notificationPreferencesRepository;

    public UserNotificationPreferencesService(
            UserRepository userRepository,
            UserNotificationPreferencesRepository
                    notificationPreferencesRepository) {
        this.userRepository = userRepository;
        this.notificationPreferencesRepository =
                notificationPreferencesRepository;
    }

    @Transactional
    public NotificationPreferencesResponse getPreferences(String email) {
        User user = getUser(email);

        UserNotificationPreferences preferences =
                notificationPreferencesRepository
                        .findByUser_UserId(user.getUserId())
                        .orElseGet(() -> createDefaults(user));

        return toResponse(preferences);
    }

    @Transactional
    public NotificationPreferencesResponse updatePreferences(
            String email,
            UpdateNotificationPreferencesRequest request) {

        User user = getUser(email);

        UserNotificationPreferences preferences =
                notificationPreferencesRepository
                        .findByUser_UserId(user.getUserId())
                        .orElseGet(() -> createDefaults(user));

        preferences.setEmailNotifications(
                request.isEmailNotifications());

        preferences.setTransactionAlerts(
                request.isTransactionAlerts());

        preferences.setSecurityAlerts(
                request.isSecurityAlerts());

        UserNotificationPreferences saved =
                notificationPreferencesRepository.save(preferences);

        return toResponse(saved);
    }

    private User getUser(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        return user;
    }

    private UserNotificationPreferences createDefaults(User user) {
        UserNotificationPreferences preferences =
                new UserNotificationPreferences();

        preferences.setUser(user);
        preferences.setEmailNotifications(true);
        preferences.setTransactionAlerts(true);
        preferences.setSecurityAlerts(true);

        return notificationPreferencesRepository.save(preferences);
    }

    private NotificationPreferencesResponse toResponse(
            UserNotificationPreferences preferences) {

        return new NotificationPreferencesResponse(
                preferences.isEmailNotifications(),
                preferences.isTransactionAlerts(),
                preferences.isSecurityAlerts()
        );
    }
}
