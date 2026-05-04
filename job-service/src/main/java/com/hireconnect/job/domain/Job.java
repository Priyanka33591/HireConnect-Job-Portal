package com.hireconnect.job.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_jobs_title", columnList = "title"),
        @Index(name = "idx_jobs_location", columnList = "location"),
        @Index(name = "idx_jobs_posted_by", columnList = "postedBy"),
        @Index(name = "idx_jobs_status", columnList = "status")
})
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String title;

    @Column(length = 160)
    private String companyName;

    @Column(length = 80)
    private String category;

    @Column(length = 50)
    private String type; // Full-time, Part-time, Contract, etc.

    @Column(length = 120)
    private String location;

    private Integer salaryMin;
    private Integer salaryMax;

    @Lob
    private String skills; // comma-separated for simplicity

    @Lob
    private String description;

    private Integer experienceRequired; // years

    @Column(nullable = false)
    private Long postedBy; // recruiter userId

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private JobStatus status;

    @Column(nullable = false)
    private Instant postedAt;

    @PrePersist
    void onCreate() {
        if (postedAt == null) postedAt = Instant.now();
        if (status == null) status = JobStatus.OPEN;
    }
}

