package com.hireconnect.subscription.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions", indexes = {
        @Index(name = "idx_subscriptions_user_id", columnList = "userId")
})
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userRole; // "CANDIDATE" or "RECRUITER"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PlanType plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private Instant startedAt;

    @Column(nullable = false)
    private Instant renewedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (startedAt == null) startedAt = now;
        if (renewedAt == null) renewedAt = now;
        if (status == null) status = SubscriptionStatus.ACTIVE;
        if (plan == null) plan = PlanType.FREE;
    }
}

