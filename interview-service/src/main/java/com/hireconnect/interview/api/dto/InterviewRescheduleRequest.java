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
public class InterviewRescheduleRequest {
    @NotNull
    private Instant scheduledAt;

    @Size(max = 500)
    private String meetLink;

    @Size(max = 200)
    private String location;
}

