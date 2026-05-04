package com.hireconnect.profile.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recruiter_profiles", indexes = {
        @Index(name = "idx_recruiter_profiles_user_id", columnList = "userId", unique = true)
})
public class RecruiterProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 120)
    private String recruiterName;

    @Column(nullable = false, length = 160)
    private String companyName;

    @Column(length = 20)
    private String gender;

    private java.time.LocalDate dob;

    @Lob
    private String companyLocations;

    @Column(length = 200)
    private String companyWebsite;

    @Column(length = 30)
    private String phone;

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

