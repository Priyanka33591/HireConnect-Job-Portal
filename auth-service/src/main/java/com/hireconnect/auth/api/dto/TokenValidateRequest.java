package com.hireconnect.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenValidateRequest {
    @NotBlank
    private String token;
}

