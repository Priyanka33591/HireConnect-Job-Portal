package com.hireconnect.interview.config;

import com.hireconnect.interview.security.AuthServiceProperties;
import com.hireconnect.interview.service.NotificationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AuthServiceProperties.class, NotificationProperties.class})
public class AppConfig {
}

