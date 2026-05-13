package com.hireconnect.analytics.service.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class InvoiceDto {
    private Long id;
    private int amountCents;
    private Instant createdAt;
}
