package com.hireconnect.subscription.config;

import com.hireconnect.subscription.security.AuthServiceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AuthServiceProperties.class)
public class AppConfig {
}

