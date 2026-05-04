package com.hireconnect.job.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "hireconnect.subscription")
public class SubscriptionProperties {
    private String baseUrl;
}
