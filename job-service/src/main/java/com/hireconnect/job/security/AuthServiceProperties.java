package com.hireconnect.job.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hireconnect.auth")
public class AuthServiceProperties {
    private String baseUrl;
}

