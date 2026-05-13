package com.hireconnect.subscription.security;

import lombok.Data;

@Data
public class TokenValidateResponse {
    private boolean valid;
    private Long userId;
    private String email;
    private String role;
    private long expiresAtEpochSeconds;

    public boolean isValid() { return valid; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}

