package com.hireconnect.interview.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hireconnect.notification")
public class NotificationProperties {
    private String baseUrl;
}

