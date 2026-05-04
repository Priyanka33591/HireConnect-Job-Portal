package com.hireconnect.profile.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class ProfileResponse {
    private Long id;
    private String type; // CANDIDATE or RECRUITER
    private Long userId;

    // candidate fields
    private String fullName;
    private String email;
    private LocalDate dob;
    private String gender;
    private String currentLocation;
    private String headline;
    private String profileSummary;
    private String skills;
    private Integer experienceYears;
    private String resumeUrl;
    private String linkedInUrl;
    private String portfolioUrl;
    private String githubUrl;
    private String leetcodeUrl;
    private String currentCompany;
    private String currentRole;
    private String highestEducation;
    private String universityName;
    private Double graduationPercentage;
    private Double tenthPercentage;
    private Double twelfthPercentage;
    private String preferredLocation;
    private String preferredJobType;
    private Integer expectedSalary;
    private String educations;
    private String projects;
    private String certifications;
    private String languages;
    private String internships;
    private String achievements;
    private String preferredLocations;

    // recruiter fields
    private String recruiterName;
    private String companyName;
    private String companyWebsite;
    private String companyLocations;

    // shared
    private String phone;
    private AddressDto address;
    private Instant createdAt;
    private Instant updatedAt;
}

