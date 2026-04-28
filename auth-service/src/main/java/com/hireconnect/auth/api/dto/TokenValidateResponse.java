package com.hireconnect.auth.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenValidateResponse {
    private boolean valid;
    private Long userId;
    private String email;
    private String role;
    private long expiresAtEpochSeconds;
}

