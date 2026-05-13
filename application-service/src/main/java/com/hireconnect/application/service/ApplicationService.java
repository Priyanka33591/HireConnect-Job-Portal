package com.hireconnect.application.service;

import com.hireconnect.application.api.dto.ApplicationCreateRequest;
import com.hireconnect.application.api.dto.ApplicationResponse;
import com.hireconnect.application.domain.Application;
import com.hireconnect.application.domain.ApplicationStatus;
import com.hireconnect.application.repo.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {
    private final ApplicationRepository repo;
    private final NotificationClient notificationClient;
    private final SubscriptionClient subscriptionClient;

    @Transactional
    public ApplicationResponse apply(ApplicationCreateRequest req) {
        // Enforcement
        checkLimit(req.getCandidateId());

        Application app = Application.builder()
                .jobId(req.getJobId())
                .candidateId(req.getCandidateId())
                .coverLetter(req.getCoverLetter())
                .resumeUrl(req.getResumeUrl())
                .status(ApplicationStatus.APPLIED)
                .statusHistory("APPLIED")
                .build();
        app = repo.save(app);

        try {
            notificationClient.send(req.getCandidateId(),
                    "Application submitted for jobId=" + req.getJobId() + " (applicationId=" + app.getId() + ")",
                    "APPLICATION");
        } catch (Exception ex) {
            log.warn("Application {} saved, but notification dispatch failed: {}", app.getId(), ex.getMessage());
        }

        return toResponse(app);
    }

    private void checkLimit(Long candidateId) {
        int limit = 10; // Default fallback limit
        try {
            SubscriptionClient.SubscriptionStatusResponse sub = subscriptionClient.getCurrentSubscription(candidateId, "CANDIDATE");
            limit = switch (sub.getPlan()) {
                case "FREE" -> 10;
                case "MONTHLY_99" -> 20;
                case "MONTHLY_199" -> 50;
                default -> 10;
            };
        } catch (Exception e) {
            log.error("Failed to check subscription limit for candidate {}: {}", candidateId, e.getMessage());
        }

        java.time.Instant startOfMonth = java.time.ZonedDateTime.now()
                .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
                .toInstant();

        long count = repo.countByCandidateIdAndAppliedAtAfter(candidateId, startOfMonth);

        if (count >= limit) {
            throw new IllegalArgumentException("your free trail is over you have to but a plan");
        }
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> byCandidate(Long candidateId) {
        return repo.findByCandidateIdOrderByAppliedAtDesc(candidateId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> listAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }


    @Transactional(readOnly = true)
    public List<ApplicationResponse> byJob(Long jobId) {
        return repo.findByJobIdOrderByAppliedAtDesc(jobId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ApplicationResponse updateStatus(Long id, ApplicationStatus next) {
        Application app = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));
        validateTransition(app.getStatus(), next);
        app.setStatus(next);
        String history = app.getStatusHistory();
        if (history == null || history.isEmpty()) history = "APPLIED";
        app.setStatusHistory(history + " -> " + next);

        try {
            notificationClient.send(app.getCandidateId(),
                    "Application status updated to " + next + " (applicationId=" + app.getId() + ")",
                    "APPLICATION_STATUS");
        } catch (Exception ex) {
            log.warn("Application {} status updated, but notification dispatch failed: {}", app.getId(), ex.getMessage());
        }

        return toResponse(app);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Application not found: " + id);
        repo.deleteById(id);
    }

    private void validateTransition(ApplicationStatus current, ApplicationStatus next) {
        if (current == next) return;
        boolean ok = switch (current) {
            case APPLIED -> next == ApplicationStatus.SHORTLISTED || next == ApplicationStatus.REJECTED;
            case SHORTLISTED -> next == ApplicationStatus.INTERVIEW || next == ApplicationStatus.REJECTED;
            case INTERVIEW -> next == ApplicationStatus.OFFERED || next == ApplicationStatus.REJECTED;
            case OFFERED, REJECTED -> false;
        };
        if (!ok) throw new IllegalArgumentException("Invalid status transition: " + current + " -> " + next);
    }

    private ApplicationResponse toResponse(Application a) {
        return ApplicationResponse.builder()
                .id(a.getId())
                .jobId(a.getJobId())
                .candidateId(a.getCandidateId())
                .appliedAt(a.getAppliedAt())
                .status(a.getStatus())
                .coverLetter(a.getCoverLetter())
                .resumeUrl(a.getResumeUrl())
                .statusHistory(a.getStatusHistory())
                .build();
    }
}

