package com.hireconnect.job.api;

import com.hireconnect.job.api.dto.JobRequest;
import com.hireconnect.job.api.dto.JobResponse;
import com.hireconnect.job.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobsController {
    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_RECRUITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<JobResponse> create(@Valid @RequestBody JobRequest req) {
        return ResponseEntity.ok(jobService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> list() {
        return ResponseEntity.ok(jobService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.get(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<JobResponse> update(@PathVariable Long id, @Valid @RequestBody JobRequest req) {
        return ResponseEntity.ok(jobService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer salary,
            @RequestParam(required = false) Long postedBy
    ) {
        return ResponseEntity.ok(jobService.search(title, location, salary, postedBy));
    }
}

