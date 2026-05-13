package com.hireconnect.analytics.service.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class UserCredentialDto {
    private Long id;
    private String email;
    private Instant createdAt;
}
