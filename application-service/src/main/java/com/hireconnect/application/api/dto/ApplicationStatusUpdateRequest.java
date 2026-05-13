package com.hireconnect.application.api.dto;

import com.hireconnect.application.domain.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationStatusUpdateRequest {
    @NotNull
    private ApplicationStatus status;
}

