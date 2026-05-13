package com.hireconnect.notification.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notifications_user_id", columnList = "userId"),
        @Index(name = "idx_notifications_is_read", columnList = "isRead")
})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Lob
    private String message;

    @Column(length = 50)
    private String type;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}

