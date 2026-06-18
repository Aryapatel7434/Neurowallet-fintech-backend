package com.smartwallet.repository;

import com.smartwallet.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification>
    findByEmailOrderByCreatedAtDesc(
            String email
    );
    List<Notification>
        findByEmailAndIsReadFalseOrderByCreatedAtDesc(
        String email
);
        Optional<Notification>
        findById(Long id);
        
        
        List<Notification>
           findByEmailAndIsReadFalse(
        String email
);
}