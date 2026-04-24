package com.hireconnect.auth.service;

import com.hireconnect.auth.api.dto.AuthLoginRequest;
import com.hireconnect.auth.api.dto.AuthRegisterRequest;
import com.hireconnect.auth.api.dto.AuthResponse;
import com.hireconnect.auth.api.dto.TokenValidateResponse;
import com.hireconnect.auth.domain.AuthProvider;
import com.hireconnect.auth.domain.UserCredential;
import com.hireconnect.auth.repo.UserCredentialRepository;
import com.hireconnect.auth.security.JwtService;
import com.hireconnect.auth.security.JwtTokenType;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserCredentialRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;

    @Transactional
    public AuthResponse register(AuthRegisterRequest req) {
        if (repo.existsByEmailIgnoreCase(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        UserCredential user = UserCredential.builder()
                .email(req.getEmail().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .provider(AuthProvider.LOCAL)
                .createdAt(Instant.now())
                .build();

        user = repo.save(user);
        return issueTokens(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthLoginRequest req) {
        UserCredential user = repo.findByEmailIgnoreCase(req.getEmail().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return issueTokens(user);
    }

    @Transactional(readOnly = true)
    public TokenValidateResponse validate(String token) {
        Claims claims = jwt.parseAndValidate(token);
        if (jwt.tokenType(claims) != JwtTokenType.ACCESS) {
            throw new IllegalArgumentException("Token is not an access token");
        }

        Long uid = claims.get("uid", Long.class);
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        return TokenValidateResponse.builder()
                .valid(true)
                .userId(uid)
                .email(email)
                .role(role)
                .expiresAtEpochSeconds(jwt.expiresAtEpochSeconds(claims))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(String refreshToken) {
        Claims claims = jwt.parseAndValidate(refreshToken);
        if (jwt.tokenType(claims) != JwtTokenType.REFRESH) {
            throw new IllegalArgumentException("Token is not a refresh token");
        }

        String email = claims.get("email", String.class);
        UserCredential user = repo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        // Note: For stricter security, persist refresh token IDs and revoke on logout/rotation.
        return issueTokens(user);
    }

    private AuthResponse issueTokens(UserCredential user) {
        String access = jwt.mintAccessToken(user);
        String refresh = jwt.mintRefreshToken(user);

        Claims accessClaims = jwt.parseAndValidate(access);
        long expEpochSeconds = jwt.expiresAtEpochSeconds(accessClaims);
        long nowEpochSeconds = Instant.now().getEpochSecond();
        long expiresIn = Math.max(0, expEpochSeconds - nowEpochSeconds);

        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresInSeconds(expiresIn)
                .userId(user.getId())
                .email(user.getEmail())
                .role("ROLE_" + user.getRole().name())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserCredential> listUsers() {
        return repo.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("User not found: " + id);
        repo.deleteById(id);
    }
}

