package com.hireconnect.analytics.service.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class JobDto {
    private Long id;
    private String title;
    private String category;
    private String type;
    private String location;
    private Integer salaryMin;
    private Integer salaryMax;
    private String skills;
    private Integer experienceRequired;
    private Long postedBy;
    private String status;
    private Instant postedAt;
}

