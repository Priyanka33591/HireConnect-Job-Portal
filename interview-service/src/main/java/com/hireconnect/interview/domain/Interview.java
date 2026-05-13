package com.hireconnect.interview.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interviews", indexes = {
        @Index(name = "idx_interviews_application_id", columnList = "applicationId")
})
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long applicationId;

    @Column(nullable = false)
    private Long candidateId;

    @Column(nullable = false)
    private Instant scheduledAt;

    @Column(length = 30)
    private String mode; // ONLINE / ONSITE / PHONE

    @Column(length = 500)
    private String meetLink;

    @Column(length = 200)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InterviewStatus status;

    @Lob
    private String notes;

    @PrePersist
    void onCreate() {
        if (status == null) status = InterviewStatus.SCHEDULED;
    }
}

