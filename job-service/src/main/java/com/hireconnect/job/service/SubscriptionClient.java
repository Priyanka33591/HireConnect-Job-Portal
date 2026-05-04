package com.hireconnect.job.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class SubscriptionClient {
    private final SubscriptionProperties props;

    public SubscriptionStatusResponse getCurrentSubscription(Long userId, String userRole) {
        RestClient client = RestClient.create(props.getBaseUrl());
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/subscriptions/current")
                        .queryParam("userId", userId)
                        .queryParam("userRole", userRole)
                        .build())
                .retrieve()
                .body(SubscriptionStatusResponse.class);
    }

    @Data
    public static class SubscriptionStatusResponse {
        private String plan; // FREE, MONTHLY_99, MONTHLY_199
        private String status; // ACTIVE, CANCELLED
    }
}
