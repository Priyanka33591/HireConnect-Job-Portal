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
@Table(
        name = "job_bookmarks",
        uniqueConstraints = @UniqueConstraint(name = "uk_job_bookmarks_user_job", columnNames = {"userId", "jobId"}),
        indexes = {
                @Index(name = "idx_job_bookmarks_user_id", columnList = "userId"),
                @Index(name = "idx_job_bookmarks_job_id", columnList = "jobId")
        }
)
public class JobBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long jobId;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}

