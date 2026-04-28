package com.hireconnect.auth.security;

import com.hireconnect.auth.domain.AuthProvider;
import com.hireconnect.auth.domain.UserCredential;
import com.hireconnect.auth.domain.UserRole;
import com.hireconnect.auth.repo.UserCredentialRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

/**
 * Called by Spring Security after Google successfully authenticates the user
 * via the Authorization Code flow.
 *
 * NOTE: Do NOT use @RequiredArgsConstructor here — Lombok's generated constructor
 * does not inject @Value fields (Spring injects them via field injection after
 * the constructor runs). We use explicit @Autowired / field injection instead.
 */
@Slf4j
@Component
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserCredentialRepository repo;
    private final JwtService jwtService;

    // @Value is injected by Spring AFTER constructor — must NOT be in @RequiredArgsConstructor
    @Value("${hireconnect.frontend-url}")
    private String frontendUrl;

    // Explicit constructor so Spring injects the final fields properly
    public GoogleOAuth2SuccessHandler(UserCredentialRepository repo, JwtService jwtService) {
        this.repo = repo;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // ── 1. Extract user info from the Google OIDC response ──────────────
        String email;
        String googleSub;

        Object principal = authentication.getPrincipal();
        if (principal instanceof OidcUser oidcUser) {
            email     = oidcUser.getEmail();
            googleSub = oidcUser.getSubject();

            if (!Boolean.TRUE.equals(oidcUser.getEmailVerified())) {
                redirectWithError(response, "Google account email is not verified");
                return;
            }
        } else if (principal instanceof OAuth2User oauth2User) {
            email     = oauth2User.getAttribute("email");
            googleSub = oauth2User.getAttribute("sub");
        } else {
            redirectWithError(response, "Unrecognised Google principal type");
            return;
        }

        if (email == null || email.isBlank() || googleSub == null) {
            redirectWithError(response, "Google did not return an email address");
            return;
        }

        // ── 2. Find or create the local HireConnect account ─────────────────
        UserCredential user = findOrCreateUser(email.trim().toLowerCase(), googleSub);

        // ── 3. Issue HireConnect JWT pair ────────────────────────────────────
        String accessToken  = jwtService.mintAccessToken(user);
        String refreshToken = jwtService.mintRefreshToken(user);

        // ── 4. Redirect the browser to the frontend callback page ────────────
        String callbackUrl = frontendUrl + "/oauth2/callback"
                + "?accessToken="  + encode(accessToken)
                + "&refreshToken=" + encode(refreshToken)
                + "&role="         + encode("ROLE_" + user.getRole().name())
                + "&userId="       + user.getId()
                + "&email="        + encode(user.getEmail());

        log.info("Google OAuth2 success — user={}, frontendUrl={}, redirecting",
                user.getEmail(), frontendUrl);
        response.sendRedirect(callbackUrl);
    }

    private UserCredential findOrCreateUser(String email, String googleSub) {
        Optional<UserCredential> byGoogleSub = repo.findByGoogleSub(googleSub);
        if (byGoogleSub.isPresent()) {
            return byGoogleSub.get();
        }

        Optional<UserCredential> byEmail = repo.findByEmailIgnoreCase(email);
        if (byEmail.isPresent()) {
            UserCredential existing = byEmail.get();
            existing.setGoogleSub(googleSub);
            existing.setProvider(AuthProvider.GOOGLE);
            return repo.save(existing);
        }

        UserCredential newUser = UserCredential.builder()
                .email(email)
                .passwordHash("")       // empty — Google users have no password
                .googleSub(googleSub)
                .role(UserRole.CANDIDATE)
                .provider(AuthProvider.GOOGLE)
                .createdAt(Instant.now())
                .build();
        return repo.save(newUser);
    }

    private void redirectWithError(HttpServletResponse response, String message) throws IOException {
        String url = frontendUrl + "/login?error=" + encode(message);
        response.sendRedirect(url);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
