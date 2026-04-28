package com.hireconnect.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleTokenRequest {
    /**
     * The raw Google ID Token (JWT) returned by Google Identity Services on the frontend.
     * The backend verifies this token against Google's JWKS before trusting any claims.
     */
    @NotBlank(message = "idToken must not be blank")
    private String idToken;
}
