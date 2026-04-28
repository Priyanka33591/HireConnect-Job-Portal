package com.hireconnect.auth.api;

import com.hireconnect.auth.api.dto.*;
import com.hireconnect.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenValidateResponse> validate(@Valid @RequestBody TokenValidateRequest req) {
        return ResponseEntity.ok(authService.validate(req.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody TokenRefreshRequest req) {
        return ResponseEntity.ok(authService.refresh(req.getRefreshToken()));
    }

    // NOTE: Google OAuth2 login is NOT handled here.
    // The flow is:  /oauth2/authorization/google  →  Google  →  /login/oauth2/code/google
    // Spring Security intercepts the callback and calls GoogleOAuth2SuccessHandler,
    // which issues HireConnect JWTs and redirects to the frontend /oauth2/callback page.

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserCredentialResponse>> users() {
        List<UserCredentialResponse> out = authService.listUsers().stream()
                .map(u -> UserCredentialResponse.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .role("ROLE_" + u.getRole().name())
                        .provider(u.getProvider().name())
                        .createdAt(u.getCreatedAt())
                        .suspended(u.isSuspended())
                        .build())
                .toList();
        return ResponseEntity.ok(out);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/suspend")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> suspendUser(@PathVariable Long id) {
        authService.suspendUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/unsuspend")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> unsuspendUser(@PathVariable Long id) {
        authService.unsuspendUser(id);
        return ResponseEntity.ok().build();
    }
}
