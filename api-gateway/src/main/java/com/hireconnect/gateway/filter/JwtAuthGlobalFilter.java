package com.hireconnect.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hireconnect.gateway.auth.AuthWebClient;
import com.hireconnect.gateway.auth.TokenValidateResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthGlobalFilter.class);

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/register"
    );

    private final AuthWebClient authWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Allow public auth endpoints + CORS preflight
        String method = exchange.getRequest().getMethod() == null ? "" : exchange.getRequest().getMethod().name();
        if (isPublicRequest(method, path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (token.isBlank()) return unauthorized(exchange, "Missing token");

        return authWebClient.validate(token)
                .flatMap(validated -> {
                    if (validated == null || !validated.isValid()) {
                        return unauthorized(exchange, "Invalid token");
                    }

                    // (Optional) propagate identity to downstream services
                    ServerWebExchange mutated = exchange.mutate()
                            .request(r -> r.headers(h -> {
                                h.add("X-Auth-UserId", String.valueOf(validated.getUserId()));
                                h.add("X-Auth-Email", validated.getEmail());
                                h.add("X-Auth-Role", validated.getRole());
                            }))
                            .build();

                    return chain.filter(mutated);
                })
                .onErrorResume(ex -> {
                    log.warn("Auth validation failed: {}", ex.getMessage());
                    return unauthorized(exchange, "Token validation failed");
                });
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        var res = exchange.getResponse();
        res.setStatusCode(HttpStatus.UNAUTHORIZED);
        res.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        GatewayErrorResponse body = GatewayErrorResponse.builder()
                .timestamp(Instant.now())
                .status(401)
                .error("Unauthorized")
                .message(message)
                .path(exchange.getRequest().getURI().getPath())
                .build();
        byte[] bytes = toJsonBytes(body);
        var buffer = res.bufferFactory().wrap(bytes);
        return res.writeWith(Mono.just(buffer));
    }

    private byte[] toJsonBytes(Object body) {
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            return ("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Unauthorized\"}")
                    .getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean isPublicRequest(String method, String path) {
        if ("OPTIONS".equalsIgnoreCase(method) || PUBLIC_PATHS.contains(path)) {
            return true;
        }
        // Public browsing/searching of jobs without login.
        if ("GET".equalsIgnoreCase(method) && 
           ("/api/jobs".equals(path) || path.startsWith("/api/jobs/") || "/api/subscriptions/current".equals(path))) {
            return true;
        }
        // Public access for static uploads (avatars, resumes)
        if (path.startsWith("/api/uploads/")) {
            return true;
        }

        // Swagger paths
        if (path.contains("/v3/api-docs") || path.contains("/swagger-ui") || "/swagger-ui.html".equals(path) || path.startsWith("/webjars/")) {
            return true;
        }
        return false;
    }
}

