package com.hireconnect.auth.security;

import com.hireconnect.auth.config.JwtProperties;
import com.hireconnect.auth.domain.UserCredential;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties props;

    private SecretKey key() {
        // For production: set hireconnect.jwt.secret to a strong base64 string (>= 256 bits)
        byte[] bytes = Decoders.BASE64.decode(props.getSecret());
        return Keys.hmacShaKeyFor(bytes);
    }

    public String mintAccessToken(UserCredential user) {
        return mintToken(user, JwtTokenType.ACCESS, Duration.ofMinutes(props.getAccessTokenMinutes()));
    }

    public String mintRefreshToken(UserCredential user) {
        return mintToken(user, JwtTokenType.REFRESH, Duration.ofDays(props.getRefreshTokenDays()));
    }

    private String mintToken(UserCredential user, JwtTokenType type, Duration ttl) {
        Instant now = Instant.now();
        Instant exp = now.plus(ttl);

        Map<String, Object> claims = Map.of(
                "uid", user.getId(),
                "email", user.getEmail(),
                "role", "ROLE_" + user.getRole().name(),
                "typ", type.name()
        );

        return Jwts.builder()
                .issuer(props.getIssuer())
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(claims)
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseAndValidate(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .requireIssuer(props.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public JwtTokenType tokenType(Claims claims) {
        String typ = claims.get("typ", String.class);
        return JwtTokenType.valueOf(typ);
    }

    public long expiresAtEpochSeconds(Claims claims) {
        return claims.getExpiration().toInstant().getEpochSecond();
    }
}

