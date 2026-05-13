package com.hireconnect.interview.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class NotificationClient {
    private final NotificationProperties props;

    public void send(Long userId, String message, String type) {
        RestClient client = RestClient.create(props.getBaseUrl());
        NotificationRequest req = new NotificationRequest();
        req.setUserId(userId);
        req.setMessage(message);
        req.setType(type);
        client.post()
                .uri("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .toBodilessEntity();
    }

    @Data
    public static class NotificationRequest {
        private Long userId;
        private String message;
        private String type;
    }
}

