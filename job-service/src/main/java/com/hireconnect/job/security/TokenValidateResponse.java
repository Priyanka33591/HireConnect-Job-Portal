package com.hireconnect.job.security;

import lombok.Data;

@Data
public class TokenValidateResponse {
    private boolean valid;
    private Long userId;
    private String email;
    private String role;
    private long expiresAtEpochSeconds;
}

