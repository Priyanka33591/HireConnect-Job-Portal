package com.hireconnect.application.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applications", indexes = {
        @Index(name = "idx_applications_job_id", columnList = "jobId"),
        @Index(name = "idx_applications_candidate_id", columnList = "candidateId"),
        @Index(name = "idx_applications_status", columnList = "status")
})
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long jobId;

    @Column(nullable = false)
    private Long candidateId;

    @Column(nullable = false)
    private Instant appliedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApplicationStatus status;

    @Lob
    private String coverLetter;

    @Column(length = 500)
    private String resumeUrl;

    @Column(length = 1000)
    private String statusHistory;

    @PrePersist
    void onCreate() {
        if (appliedAt == null) appliedAt = Instant.now();
        if (status == null) status = ApplicationStatus.APPLIED;
    }
}

