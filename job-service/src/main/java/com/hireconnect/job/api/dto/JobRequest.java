package com.hireconnect.job.api.dto;

import com.hireconnect.job.domain.JobStatus;
import jakarta.validation.constraints.NotBlank;
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
public class JobRequest {
    @NotBlank
    @Size(max = 140)
    private String title;

    @Size(max = 160)
    private String companyName;

    @Size(max = 80)
    private String category;

    @Size(max = 50)
    private String type;

    @Size(max = 120)
    private String location;

    private Integer salaryMin;
    private Integer salaryMax;
    private String skills;
    @Size(max = 5000)
    private String description;
    private Integer experienceRequired;

    @NotNull
    private Long postedBy;

    private JobStatus status;
}

