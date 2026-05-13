package com.hireconnect.application.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationCreateRequest {
    @NotNull
    private Long jobId;

    @NotNull
    private Long candidateId;

    private String coverLetter;

    @Size(max = 500)
    private String resumeUrl;
}

