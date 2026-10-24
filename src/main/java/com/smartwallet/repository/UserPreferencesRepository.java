
package com.smartwallet.repository;

import com.smartwallet.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferencesRepository
        extends JpaRepository<UserPreferences, Long> {

    Optional<UserPreferences> findByUser_UserId(int userId);
}
