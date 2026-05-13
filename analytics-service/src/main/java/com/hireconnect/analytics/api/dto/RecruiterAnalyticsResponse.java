package com.hireconnect.analytics.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RecruiterAnalyticsResponse {
    private Long recruiterId;
    private long jobsPosted;
    private long totalApplications;
    private Map<String, Long> applicationsByStatus;
}

