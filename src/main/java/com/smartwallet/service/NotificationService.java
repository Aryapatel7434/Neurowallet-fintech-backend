
package com.smartwallet.service;

import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.model.Notification;
import com.smartwallet.repository.NotificationRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository
    ) {
        this.notificationRepository = notificationRepository;
    }

    // ============================================================
    // GET AUTHENTICATED USER EMAIL
    // ============================================================

    private String getCurrentUserEmail() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    // ============================================================
    // CREATE NOTIFICATION
    // ============================================================

    @Transactional
    public void createNotification(
            String email,
            String message
    ) {

        Notification notification = new Notification();

        notification.setEmail(email);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    // ============================================================
    // GET USER NOTIFICATIONS
    // ============================================================

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(
            String email
    ) {

        return notificationRepository
                .findByEmailOrderByCreatedAtDesc(email);
    }

    // ============================================================
    // GET UNREAD NOTIFICATIONS
    // ============================================================

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications() {

        String email = getCurrentUserEmail();

        return notificationRepository
                .findByEmailAndIsReadFalseOrderByCreatedAtDesc(
                        email
                );
    }

    // ============================================================
    // MARK SINGLE NOTIFICATION AS READ
    // ============================================================

    @Transactional
    public void markAsRead(Long notificationId) {

        String email = getCurrentUserEmail();

        Notification notification = notificationRepository
                .findByIdAndEmail(notificationId, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found"
                        )
                );

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    // ============================================================
    // MARK ALL NOTIFICATIONS AS READ
    // ============================================================

    @Transactional
    public void markAllAsRead() {

        String email = getCurrentUserEmail();

        List<Notification> notifications =
                notificationRepository
                        .findByEmailAndIsReadFalse(email);

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);
    }
}
