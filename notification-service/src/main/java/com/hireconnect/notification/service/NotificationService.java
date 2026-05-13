package com.hireconnect.notification.service;

import com.hireconnect.notification.api.dto.NotificationRequest;
import com.hireconnect.notification.api.dto.NotificationResponse;
import com.hireconnect.notification.domain.Notification;
import com.hireconnect.notification.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository repo;

    @Transactional
    public NotificationResponse create(NotificationRequest req) {
        Notification n = Notification.builder()
                .userId(req.getUserId())
                .message(req.getMessage())
                .type(req.getType())
                .isRead(false)
                .build();
        n = repo.save(n);

        // Email simulation (bonus): just log for now.
        log.info("[EMAIL_SIMULATION] To userId={} | type={} | message={}", n.getUserId(), n.getType(), n.getMessage());

        return toResponse(n);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> byUser(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public NotificationResponse markRead(Long id) {
        Notification n = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
        n.setRead(true);
        return toResponse(n);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Notification not found: " + id);
        repo.deleteById(id);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .message(n.getMessage())
                .type(n.getType())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}

