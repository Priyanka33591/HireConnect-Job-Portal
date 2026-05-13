package com.hireconnect.subscription.api.dto;

import com.hireconnect.subscription.domain.PlanType;
import com.hireconnect.subscription.domain.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SubscriptionResponse {
    private Long id;
    private Long userId;
    private String userRole;
    private PlanType plan;
    private SubscriptionStatus status;
    private Instant startedAt;
    private Instant renewedAt;
}

