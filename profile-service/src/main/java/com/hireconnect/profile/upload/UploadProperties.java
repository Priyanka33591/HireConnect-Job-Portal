package com.hireconnect.profile.upload;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hireconnect.uploads")
public class UploadProperties {
    private String dir = "uploads";
}

