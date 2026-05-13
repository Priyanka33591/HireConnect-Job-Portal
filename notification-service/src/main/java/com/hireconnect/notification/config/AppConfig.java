package com.hireconnect.notification.config;

import com.hireconnect.notification.security.AuthServiceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AuthServiceProperties.class)
public class AppConfig {
}

