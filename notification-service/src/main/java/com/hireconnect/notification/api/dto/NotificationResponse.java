package com.hireconnect.notification.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String message;
    private String type;
    private boolean isRead;
    private Instant createdAt;
}

