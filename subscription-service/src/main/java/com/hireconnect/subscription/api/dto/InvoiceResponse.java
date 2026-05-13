package com.hireconnect.subscription.api.dto;

import com.hireconnect.subscription.domain.PlanType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class InvoiceResponse {
    private Long id;
    private Long userId;
    private String userRole;
    private Long subscriptionId;
    private PlanType plan;
    private int amountCents;
    private String razorpayOrderId;
    private Instant createdAt;
}

