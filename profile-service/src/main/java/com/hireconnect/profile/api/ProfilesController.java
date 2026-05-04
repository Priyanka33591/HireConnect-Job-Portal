package com.hireconnect.profile.api;

import com.hireconnect.profile.api.dto.CandidateProfileRequest;
import com.hireconnect.profile.api.dto.ProfileResponse;
import com.hireconnect.profile.api.dto.RecruiterProfileRequest;
import com.hireconnect.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfilesController {
    private final ProfileService profileService;

    @PostMapping("/candidate")
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProfileResponse> createCandidate(@Valid @RequestBody CandidateProfileRequest req) {
        return ResponseEntity.ok(profileService.createCandidate(req));
    }

    @PostMapping("/recruiter")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProfileResponse> createRecruiter(@Valid @RequestBody RecruiterProfileRequest req) {
        return ResponseEntity.ok(profileService.createRecruiter(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getById(id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getByUserId(userId));
    }

    @PutMapping("/{id}/candidate")
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProfileResponse> updateCandidate(@PathVariable Long id, @Valid @RequestBody CandidateProfileRequest req) {
        return ResponseEntity.ok(profileService.updateCandidate(id, req));
    }

    @PutMapping("/{id}/recruiter")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProfileResponse> updateRecruiter(@PathVariable Long id, @Valid @RequestBody RecruiterProfileRequest req) {
        return ResponseEntity.ok(profileService.updateRecruiter(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        profileService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

