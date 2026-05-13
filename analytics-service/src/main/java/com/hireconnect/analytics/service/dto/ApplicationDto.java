package com.hireconnect.analytics.service.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ApplicationDto {
    private Long id;
    private Long jobId;
    private Long candidateId;
    private Instant appliedAt;
    private String status;
    private String coverLetter;
    private String resumeUrl;
}

