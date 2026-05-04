package com.hireconnect.profile.api.dto;

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
public class RecruiterProfileRequest {
    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 120)
    private String recruiterName;

    @NotBlank
    @Size(max = 160)
    private String companyName;

    private String gender;
    private java.time.LocalDate dob;
    private String companyLocations;

    @Size(max = 200)
    private String companyWebsite;

    @Size(max = 30)
    private String phone;

    private AddressDto address;
}

