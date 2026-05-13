package com.hireconnect.interview.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewCreateRequest {
    @NotNull
    private Long applicationId;

    @NotNull
    private Long candidateId;

    @NotNull
    private Instant scheduledAt;

    @Size(max = 30)
    private String mode;

    @Size(max = 500)
    private String meetLink;

    @Size(max = 200)
    private String location;

    private String notes;
}

