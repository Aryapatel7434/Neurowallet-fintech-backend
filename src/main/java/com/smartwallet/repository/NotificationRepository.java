
package com.smartwallet.repository;

import com.smartwallet.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByEmailOrderByCreatedAtDesc(
            String email
    );

    List<Notification> findByEmailAndIsReadFalseOrderByCreatedAtDesc(
            String email
    );

    // Owner-scoped notification lookup
    Optional<Notification> findByIdAndEmail(
            Long id,
            String email
    );

    List<Notification> findByEmailAndIsReadFalse(
            String email
    );
}
