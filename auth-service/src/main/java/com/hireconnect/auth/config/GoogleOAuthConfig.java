package com.hireconnect.auth.config;

// This file is intentionally empty.
// OAuth2 is now handled via Spring Security OAuth2 Client (Authorization Code flow).
// The JwtDecoder bean that previously verified Google ID Tokens is no longer needed
// because Spring Security exchanges the authorization code using the client secret.
public class GoogleOAuthConfig {
}
