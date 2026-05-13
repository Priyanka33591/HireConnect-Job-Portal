package com.hireconnect.gateway.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthWebClient {
    private final AuthProperties props;

    public Mono<TokenValidateResponse> validate(String token) {
        TokenValidateRequest req = new TokenValidateRequest();
        req.setToken(token);

        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .build()
                .post()
                .uri("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(TokenValidateResponse.class);
    }
}

