package com.hireconnect.profile.upload;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Set;

@RestController
@RequestMapping("/profiles/resume")
@RequiredArgsConstructor
public class ResumeUploadController {
    private static final Set<String> ALLOWED = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final UploadProperties props;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UploadResponse> upload(
            @RequestParam("userId") @NotNull Long userId,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File is required");
        if (file.getContentType() != null && !ALLOWED.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type: " + file.getContentType());
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "resume" : file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot > 0) ext = original.substring(dot);

        Path baseDir = Path.of(props.getDir(), "resumes", String.valueOf(userId)).toAbsolutePath().normalize();
        Files.createDirectories(baseDir);

        String filename = "resume-" + Instant.now().toEpochMilli() + ext;
        Path target = baseDir.resolve(filename).normalize();

        Files.copy(file.getInputStream(), target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        String urlPath = "/uploads/resumes/" + userId + "/" + filename;
        return ResponseEntity.ok(UploadResponse.builder()
                .userId(userId)
                .fileName(filename)
                .url(urlPath)
                .build());
    }

    @Data
    @Builder
    public static class UploadResponse {
        private Long userId;
        private String fileName;
        private String url;
    }
}

