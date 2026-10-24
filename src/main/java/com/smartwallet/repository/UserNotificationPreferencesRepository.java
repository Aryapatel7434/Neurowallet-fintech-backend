
package com.smartwallet.repository;

import com.smartwallet.model.UserNotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserNotificationPreferencesRepository
        extends JpaRepository<UserNotificationPreferences, Long> {

    Optional<UserNotificationPreferences> findByUser_UserId(int userId);
}
