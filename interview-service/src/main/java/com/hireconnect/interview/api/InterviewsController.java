package com.hireconnect.interview.api;

import com.hireconnect.interview.api.dto.InterviewCreateRequest;
import com.hireconnect.interview.api.dto.InterviewRescheduleRequest;
import com.hireconnect.interview.api.dto.InterviewResponse;
import com.hireconnect.interview.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
public class InterviewsController {
    private final InterviewService interviewService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_RECRUITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<InterviewResponse> schedule(@Valid @RequestBody InterviewCreateRequest req) {
        return ResponseEntity.ok(interviewService.schedule(req));
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<InterviewResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(interviewService.confirm(id));
    }

    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<InterviewResponse> reschedule(@PathVariable Long id, @Valid @RequestBody InterviewRescheduleRequest req) {
        return ResponseEntity.ok(interviewService.reschedule(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        interviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/application/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InterviewResponse>> byApplication(@PathVariable("id") Long applicationId) {
        return ResponseEntity.ok(interviewService.byApplication(applicationId));
    }
}

