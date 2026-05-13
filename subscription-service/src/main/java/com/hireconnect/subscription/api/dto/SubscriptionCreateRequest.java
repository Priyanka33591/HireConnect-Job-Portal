package com.hireconnect.subscription.api.dto;

import com.hireconnect.subscription.domain.PlanType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionCreateRequest {
    @NotNull
    private Long userId;

    @NotNull
    private String userRole;

    @NotNull
    private PlanType plan;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}

