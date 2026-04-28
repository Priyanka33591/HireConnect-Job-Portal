package com.hireconnect.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hireconnect.jwt")
public class JwtProperties {
    private String issuer;
    private String secret;
    private long accessTokenMinutes;
    private long refreshTokenDays;
}

