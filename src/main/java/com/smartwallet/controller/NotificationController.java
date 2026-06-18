package com.smartwallet.controller;

import com.smartwallet.model.Notification;
import com.smartwallet.service.NotificationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService
            notificationService;

    public NotificationController(
            NotificationService notificationService
    ) {

        this.notificationService =
                notificationService;
    }

    @GetMapping
    public List<Notification>
    getMyNotifications() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return notificationService
                .getUserNotifications(
                        email
                );
    }
    @GetMapping("/unread")
public List<Notification>
getUnreadNotifications() {

    return notificationService
            .getUnreadNotifications();
}
@PutMapping("/{id}/read")
public ResponseEntity<String>
markAsRead(
        @PathVariable Long id
) {

    notificationService
            .markAsRead(id);

    return ResponseEntity.ok(
            "Notification marked as read"
    );
}
@PutMapping("/read-all")
public ResponseEntity<String>
markAllAsRead() {

    notificationService
            .markAllAsRead();

    return ResponseEntity.ok(
            "All notifications marked as read"
    );
}
}