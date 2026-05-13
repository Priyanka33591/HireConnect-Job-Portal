package com.hireconnect.notification.api;

import com.hireconnect.notification.api.dto.NotificationRequest;
import com.hireconnect.notification.api.dto.NotificationResponse;
import com.hireconnect.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationsController {
    private final NotificationService notificationService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest req) {
        return ResponseEntity.ok(notificationService.create(req));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationResponse>> byUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.byUser(userId));
    }

    @PutMapping("/read/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markRead(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

