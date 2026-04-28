package com.hireconnect.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Single security filter chain that handles both:
 *
 *  A) Google OAuth2 Authorization Code flow (needs a brief session for the CSRF state param)
 *       GET  /oauth2/authorization/google   → Spring redirects browser to Google consent page
 *       GET  /login/oauth2/code/google      → Spring exchanges code + client_secret for tokens,
 *                                             then calls GoogleOAuth2SuccessHandler which issues
 *                                             HireConnect JWTs and redirects to the React frontend
 *
 *  B) Stateless JWT API (all other /auth/** endpoints)
 *       The BearerJwtAuthFilter sets authentication from the JWT.
 *       Sessions are IF_REQUIRED — created only during the OAuth2 handshake, never for JWT calls.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final BearerJwtAuthFilter bearerJwtAuthFilter;
    private final GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler;

    public SecurityConfig(BearerJwtAuthFilter bearerJwtAuthFilter,
                          GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler) {
        this.bearerJwtAuthFilter = bearerJwtAuthFilter;
        this.googleOAuth2SuccessHandler = googleOAuth2SuccessHandler;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                // IF_REQUIRED: sessions are only created when Spring Security needs them
                // (briefly during OAuth2 state/CSRF token exchange — never for JWT API calls)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers(
                                // Email/password auth endpoints
                                "/auth/register",
                                "/auth/login",
                                "/auth/validate",
                                "/auth/refresh",
                                // Google OAuth2 redirect endpoints (handled by Spring Security internally)
                                "/oauth2/**",
                                "/login/oauth2/**",
                                // API docs
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Google OAuth2 Authorization Code flow
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(googleOAuth2SuccessHandler)
                        .failureUrl("http://localhost:5173/login?error=google_auth_failed")
                )
                // Stateless JWT filter (runs on every request; sets auth if valid Bearer token present)
                .addFilterBefore(bearerJwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
