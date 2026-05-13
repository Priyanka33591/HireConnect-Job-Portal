package com.hireconnect.analytics.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecruiterStatDto {
    private Long recruiterId;
    private long totalJobs;
    private long activeJobs;
}
