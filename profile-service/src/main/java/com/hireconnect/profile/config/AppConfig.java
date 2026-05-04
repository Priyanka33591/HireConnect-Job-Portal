package com.hireconnect.profile.config;

import com.hireconnect.profile.security.AuthServiceProperties;
import com.hireconnect.profile.upload.UploadProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AuthServiceProperties.class, UploadProperties.class})
public class AppConfig {
}

