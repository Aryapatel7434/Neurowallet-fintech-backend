
package com.smartwallet.service;

import com.smartwallet.dto.UpdateUserPreferencesRequest;
import com.smartwallet.dto.UserPreferencesResponse;
import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.model.User;
import com.smartwallet.model.UserPreferences;
import com.smartwallet.repository.UserPreferencesRepository;
import com.smartwallet.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPreferencesService {

    private final UserRepository userRepository;
    private final UserPreferencesRepository preferencesRepository;

    public UserPreferencesService(
            UserRepository userRepository,
            UserPreferencesRepository preferencesRepository) {
        this.userRepository = userRepository;
        this.preferencesRepository = preferencesRepository;
    }

    @Transactional
    public UserPreferencesResponse getPreferences(String email) {
        User user = getUser(email);

        UserPreferences preferences =
                preferencesRepository.findByUser_UserId(user.getUserId())
                        .orElseGet(() -> createDefaults(user));

        return toResponse(preferences);
    }

    @Transactional
    public UserPreferencesResponse updatePreferences(
            String email,
            UpdateUserPreferencesRequest request) {

        User user = getUser(email);

        UserPreferences preferences =
                preferencesRepository.findByUser_UserId(user.getUserId())
                        .orElseGet(() -> createDefaults(user));

        preferences.setTheme(request.getTheme());
        preferences.setPreferredCurrency(
                request.getPreferredCurrency());
        preferences.setLanguage(request.getLanguage());

        UserPreferences saved =
                preferencesRepository.save(preferences);

        return toResponse(saved);
    }

    private User getUser(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        return user;
    }

    private UserPreferences createDefaults(User user) {
        UserPreferences preferences = new UserPreferences();
        preferences.setUser(user);
        preferences.setTheme("DARK");
        preferences.setPreferredCurrency("INR");
        preferences.setLanguage("EN");

        return preferencesRepository.save(preferences);
    }

    private UserPreferencesResponse toResponse(
            UserPreferences preferences) {
        return new UserPreferencesResponse(
                preferences.getTheme(),
                preferences.getPreferredCurrency(),
                preferences.getLanguage()
        );
    }
}
