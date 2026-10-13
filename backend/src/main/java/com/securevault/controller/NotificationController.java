package com.securevault.controller;

import com.securevault.dto.NotificationDTO;
import com.securevault.entity.User;
import com.securevault.service.NotificationService;
import com.securevault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        User currentUser = userService.getAuthenticatedUser();
        List<NotificationDTO> notifications = notificationService.getUserNotifications(currentUser);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable("id") Long id) {
        User currentUser = userService.getAuthenticatedUser();
        NotificationDTO updated = notificationService.markAsRead(id, currentUser);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead() {
        User currentUser = userService.getAuthenticatedUser();
        notificationService.markAllAsRead(currentUser);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read."));
    }
}
