package com.hireconnect.job.api.dto;

import com.hireconnect.job.domain.JobStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String companyName;
    private String category;
    private String type;
    private String location;
    private Integer salaryMin;
    private Integer salaryMax;
    private String skills;
    private String description;
    private Integer experienceRequired;
    private Long postedBy;
    private JobStatus status;
    private Instant postedAt;
}

