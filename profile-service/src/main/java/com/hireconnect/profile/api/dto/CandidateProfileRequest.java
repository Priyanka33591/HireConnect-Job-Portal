package com.hireconnect.profile.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfileRequest {
    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 120)
    private String fullName;

    @Size(max = 255)
    private String email;

    @Size(max = 30)
    private String phone;

    private LocalDate dob;

    @Size(max = 20)
    private String gender;

    @Size(max = 120)
    private String currentLocation;

    @Size(max = 160)
    private String headline;

    private String profileSummary;
    private String skills;
    private Integer experienceYears;

    @Size(max = 500)
    private String resumeUrl;

    @Size(max = 255)
    private String linkedInUrl;

    @Size(max = 255)
    private String portfolioUrl;

    @Size(max = 255)
    private String githubUrl;

    @Size(max = 255)
    private String leetcodeUrl;

    @Size(max = 120)
    private String currentCompany;

    @Size(max = 120)
    private String currentRole;

    @Size(max = 120)
    private String highestEducation;

    @Size(max = 160)
    private String universityName;

    private Double graduationPercentage;
    private Double tenthPercentage;
    private Double twelfthPercentage;

    @Size(max = 120)
    private String preferredLocation;

    @Size(max = 60)
    private String preferredJobType;

    private Integer expectedSalary;

    private String educations;
    private String projects;
    private String certifications;
    private String languages;
    private String internships;
    private String achievements;
    private String preferredLocations;

    private AddressDto address;
}

