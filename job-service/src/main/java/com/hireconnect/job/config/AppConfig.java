package com.hireconnect.job.config;

import com.hireconnect.job.security.AuthServiceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AuthServiceProperties.class)
public class AppConfig {
}

