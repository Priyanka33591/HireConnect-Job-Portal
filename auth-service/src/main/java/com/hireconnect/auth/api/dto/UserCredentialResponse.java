package com.hireconnect.auth.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserCredentialResponse {
    private Long id;
    private String email;
    private String role;
    private String provider;
    private Instant createdAt;
    private boolean suspended;
}

