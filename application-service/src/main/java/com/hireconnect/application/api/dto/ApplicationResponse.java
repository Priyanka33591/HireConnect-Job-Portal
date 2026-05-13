package com.hireconnect.application.api.dto;

import com.hireconnect.application.domain.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private Long candidateId;
    private Instant appliedAt;
    private ApplicationStatus status;
    private String coverLetter;
    private String resumeUrl;
    private String statusHistory;
}

