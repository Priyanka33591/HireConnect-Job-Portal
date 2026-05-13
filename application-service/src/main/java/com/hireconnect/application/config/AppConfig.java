package com.hireconnect.application.config;

import com.hireconnect.application.security.AuthServiceProperties;
import com.hireconnect.application.service.NotificationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AuthServiceProperties.class, NotificationProperties.class})
public class AppConfig {
}

