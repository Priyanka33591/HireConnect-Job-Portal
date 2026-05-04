package com.hireconnect.profile.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AuthClient {
    private final AuthServiceProperties props;

    public TokenValidateResponse validateAccessToken(String token) {
        RestClient client = RestClient.create(props.getBaseUrl());
        TokenValidateRequest req = new TokenValidateRequest();
        req.setToken(token);
        return client.post()
                .uri("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(TokenValidateResponse.class);
    }
}

