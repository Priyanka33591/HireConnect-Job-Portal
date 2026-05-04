package com.hireconnect.profile.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "candidate_profiles", indexes = {
        @Index(name = "idx_candidate_profiles_user_id", columnList = "userId", unique = true)
})
public class CandidateProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String phone;

    private LocalDate dob;

    @Column(length = 20)
    private String gender;

    @Column(length = 120)
    private String currentLocation;

    @Column(length = 160)
    private String headline;

    @Lob
    private String profileSummary;

    @Lob
    private String skills; // JSON array string

    private Integer experienceYears;

    @Column(length = 500)
    private String resumeUrl;

    @Column(length = 255)
    private String linkedInUrl;

    @Column(length = 255)
    private String portfolioUrl;

    @Column(length = 255)
    private String githubUrl;

    @Column(length = 255)
    private String leetcodeUrl;

    @Column(length = 120)
    private String currentCompany;

    @Column(length = 120)
    private String currentRole;

    @Column(length = 120)
    private String highestEducation;

    @Column(length = 160)
    private String universityName;

    private Double graduationPercentage;
    private Double tenthPercentage;
    private Double twelfthPercentage;

    @Column(length = 120)
    private String preferredLocation;

    @Column(length = 60)
    private String preferredJobType;

    private Integer expectedSalary;

    @Lob
    private String educations; // JSON array string

    @Lob
    private String projects; // JSON array string

    @Lob
    private String certifications;

    @Lob
    private String languages; // JSON array string

    @Lob
    private String internships; // JSON array string

    @Lob
    private String achievements; // JSON array string

    @Lob
    private String preferredLocations; // JSON array string

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "line1", column = @Column(name = "addr_line1")),
            @AttributeOverride(name = "line2", column = @Column(name = "addr_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "addr_city")),
            @AttributeOverride(name = "state", column = @Column(name = "addr_state")),
            @AttributeOverride(name = "country", column = @Column(name = "addr_country")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "addr_postal_code"))
    })
    private Address address;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}

