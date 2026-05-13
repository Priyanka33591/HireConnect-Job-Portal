package com.hireconnect.analytics.config;

import com.hireconnect.analytics.security.AuthServiceProperties;
import com.hireconnect.analytics.service.DownstreamProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AuthServiceProperties.class, DownstreamProperties.class})
public class AppConfig {
}

