package com.hireconnect.application.api;

import com.hireconnect.application.api.dto.ApplicationCreateRequest;
import com.hireconnect.application.api.dto.ApplicationResponse;
import com.hireconnect.application.api.dto.ApplicationStatusUpdateRequest;
import com.hireconnect.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationsController {
    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApplicationResponse> apply(@Valid @RequestBody ApplicationCreateRequest req) {
        return ResponseEntity.ok(applicationService.apply(req));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationResponse>> listAll() {
        return ResponseEntity.ok(applicationService.listAll());
    }


    @GetMapping("/candidate/{id}")
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationResponse>> byCandidate(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.byCandidate(id));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationResponse>> byJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.byJob(jobId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody ApplicationStatusUpdateRequest req) {
        return ResponseEntity.ok(applicationService.updateStatus(id, req.getStatus()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        applicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

