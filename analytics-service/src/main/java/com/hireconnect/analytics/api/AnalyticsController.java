package com.hireconnect.analytics.api;

import com.hireconnect.analytics.api.dto.AdminAnalyticsResponse;
import com.hireconnect.analytics.api.dto.RecruiterAnalyticsResponse;
import com.hireconnect.analytics.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/recruiter/{id}")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RecruiterAnalyticsResponse> recruiter(@PathVariable Long id, HttpServletRequest req) {
        String bearer = req.getHeader(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(analyticsService.recruiterAnalytics(id, bearer));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdminAnalyticsResponse> admin(HttpServletRequest req) {
        String bearer = req.getHeader(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(analyticsService.adminAnalytics(bearer));
    }

    @GetMapping("/admin/recruiter-stats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<java.util.List<com.hireconnect.analytics.api.dto.RecruiterStatDto>> recruiterStats(HttpServletRequest req) {
        String bearer = req.getHeader(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(analyticsService.recruiterStats(bearer));
    }

    @GetMapping("/admin/candidate-stats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<java.util.List<com.hireconnect.analytics.api.dto.CandidateStatDto>> candidateStats(HttpServletRequest req) {
        String bearer = req.getHeader(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(analyticsService.candidateStats(bearer));
    }

    @GetMapping(value = "/admin/export", produces = "text/csv")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<byte[]> exportReport(HttpServletRequest req) {
        String bearer = req.getHeader(HttpHeaders.AUTHORIZATION);
        AdminAnalyticsResponse data = analyticsService.adminAnalytics(bearer);
        
        StringBuilder csv = new StringBuilder();
        csv.append("Metric,Value\n");
        csv.append("Total Jobs,").append(data.getTotalJobs()).append("\n");
        csv.append("Total Applications,").append(data.getTotalApplications()).append("\n");
        if (data.getApplicationsByStatus() != null) {
            data.getApplicationsByStatus().forEach((status, count) -> {
                csv.append("Status: ").append(status).append(",").append(count).append("\n");
            });
        }
        
        byte[] bytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"platform_report.csv\"")
                .body(bytes);
    }

    @GetMapping("/admin/health")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> systemHealth(HttpServletRequest req) {
        String bearer = req.getHeader(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(analyticsService.checkSystemHealth(bearer));
    }
}

