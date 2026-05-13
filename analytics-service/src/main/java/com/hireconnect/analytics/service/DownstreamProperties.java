package com.hireconnect.analytics.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hireconnect")
public class DownstreamProperties {
    private ServiceUrl job = new ServiceUrl();
    private ServiceUrl application = new ServiceUrl();
    private ServiceUrl subscription = new ServiceUrl();
    private ServiceUrl auth = new ServiceUrl();



    @Data
    public static class ServiceUrl {
        private String baseUrl;
    }
}

