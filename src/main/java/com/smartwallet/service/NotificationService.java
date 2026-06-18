package com.smartwallet.service;

import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.model.Notification;
import com.smartwallet.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository
            notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository
    ) {

        this.notificationRepository =
                notificationRepository;
    }

    public void createNotification(

            String email,

            String message

    ) {

        Notification notification =
                new Notification();

        notification.setEmail(email);

        notification.setMessage(message);

        notification.setRead(false);

        notification.setCreatedAt(
                LocalDateTime.now()
        );

        notificationRepository.save(
                notification
        );
    }

    public List<Notification>
    getUserNotifications(
            String email
    ) {

        return notificationRepository
                .findByEmailOrderByCreatedAtDesc(
                        email
                );
    }
    public List<Notification>
getUnreadNotifications() {

    String email =
            SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();

    return notificationRepository
            .findByEmailAndIsReadFalseOrderByCreatedAtDesc(
                    email
            );
}
@Transactional
public void markAsRead(
        Long notificationId
) {

    Notification notification =
            notificationRepository
                    .findById(notificationId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Notification not found"
                            ));

    notification.setRead(true);

    notificationRepository.save(
            notification
    );
}
@Transactional
public void markAllAsRead() {

    String email =
            SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();

    List<Notification>
            notifications =
            notificationRepository
                    .findByEmailAndIsReadFalse(
                            email
                    );

    for (
            Notification notification
            : notifications
    ) {

        notification.setRead(
                true
        );
    }

    notificationRepository.saveAll(
            notifications
    );
}
}