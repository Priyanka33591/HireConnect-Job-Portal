package com.hireconnect.profile.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
@RequiredArgsConstructor
public class UploadsWebConfig implements WebMvcConfigurer {
    private final UploadProperties props;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dirPath = Path.of(props.getDir()).toAbsolutePath().normalize();
        String location = dirPath.toUri().toString();
        if (!location.endsWith("/")) location += "/";
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}

