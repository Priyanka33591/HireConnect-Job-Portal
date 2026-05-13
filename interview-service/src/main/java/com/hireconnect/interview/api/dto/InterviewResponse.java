package com.hireconnect.interview.api.dto;

import com.hireconnect.interview.domain.InterviewStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class InterviewResponse {
    private Long id;
    private Long applicationId;
    private Long candidateId;
    private Instant scheduledAt;
    private String mode;
    private String meetLink;
    private String location;
    private InterviewStatus status;
    private String notes;
}

