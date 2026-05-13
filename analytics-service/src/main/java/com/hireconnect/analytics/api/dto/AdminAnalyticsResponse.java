package com.hireconnect.analytics.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AdminAnalyticsResponse {
    private long totalJobs;
    private long totalApplications;
    private Map<String, Long> applicationsByStatus;
    private double growthRate;
    private double conversionRate;
    private double totalRevenue;

}

