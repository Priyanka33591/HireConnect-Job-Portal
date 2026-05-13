package com.hireconnect.gateway.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hireconnect.auth")
public class AuthProperties {
    private String baseUrl;
}

