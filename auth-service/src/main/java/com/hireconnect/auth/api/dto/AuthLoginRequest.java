package com.hireconnect.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthLoginRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}

