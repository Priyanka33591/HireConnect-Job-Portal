package com.hireconnect.profile.api.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}

