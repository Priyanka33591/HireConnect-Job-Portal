package com.hireconnect.job.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class BookmarkResponse {
    private Long id;
    private Long userId;
    private Long jobId;
    private Instant createdAt;
}

