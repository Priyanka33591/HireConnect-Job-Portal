package com.hireconnect.profile.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}

