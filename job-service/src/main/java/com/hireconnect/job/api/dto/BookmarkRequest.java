package com.hireconnect.job.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookmarkRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long jobId;
}

