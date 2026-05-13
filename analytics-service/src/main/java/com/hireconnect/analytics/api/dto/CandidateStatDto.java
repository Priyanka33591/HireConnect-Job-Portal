package com.hireconnect.analytics.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandidateStatDto {
    private Long candidateId;
    private Integer totalApplications;
}
