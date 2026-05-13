package com.hireconnect.interview.service;

import com.hireconnect.interview.api.dto.InterviewCreateRequest;
import com.hireconnect.interview.api.dto.InterviewRescheduleRequest;
import com.hireconnect.interview.api.dto.InterviewResponse;
import com.hireconnect.interview.domain.Interview;
import com.hireconnect.interview.domain.InterviewStatus;
import com.hireconnect.interview.repo.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {
    private final InterviewRepository repo;
    private final NotificationClient notificationClient;

    @Transactional
    public InterviewResponse schedule(InterviewCreateRequest req) {
        Interview interview = Interview.builder()
                .applicationId(req.getApplicationId())
                .candidateId(req.getCandidateId())
                .scheduledAt(req.getScheduledAt())
                .mode(req.getMode())
                .meetLink(req.getMeetLink())
                .location(req.getLocation())
                .status(InterviewStatus.SCHEDULED)
                .notes(req.getNotes())
                .build();
        interview = repo.save(interview);

        try {
            notificationClient.send(req.getCandidateId(),
                    "Interview scheduled for applicationId=" + req.getApplicationId() + " at " + req.getScheduledAt(),
                    "INTERVIEW");
        } catch (Exception ex) {
            log.warn("Failed to send schedule notification for interview {}", interview.getId(), ex);
        }
        return toResponse(interview);
    }

    @Transactional
    public InterviewResponse confirm(Long id) {
        Interview interview = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Interview not found: " + id));
        interview.setStatus(InterviewStatus.CONFIRMED);
        try {
            notificationClient.send(interview.getCandidateId(),
                    "Interview confirmed (interviewId=" + interview.getId() + ") for " + interview.getScheduledAt(),
                    "INTERVIEW");
        } catch (Exception ex) {
            log.warn("Failed to send confirm notification for interview {}", interview.getId(), ex);
        }
        return toResponse(interview);
    }

    @Transactional
    public InterviewResponse reschedule(Long id, InterviewRescheduleRequest req) {
        Interview interview = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Interview not found: " + id));
        interview.setScheduledAt(req.getScheduledAt());
        if (req.getMeetLink() != null) interview.setMeetLink(req.getMeetLink());
        if (req.getLocation() != null) interview.setLocation(req.getLocation());
        interview.setStatus(InterviewStatus.RESCHEDULED);

        try {
            notificationClient.send(interview.getCandidateId(),
                    "Interview rescheduled (interviewId=" + interview.getId() + ") to " + req.getScheduledAt(),
                    "INTERVIEW");
        } catch (Exception ex) {
            log.warn("Failed to send reschedule notification for interview {}", interview.getId(), ex);
        }
        return toResponse(interview);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Interview not found: " + id);
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> byApplication(Long applicationId) {
        return repo.findByApplicationIdOrderByScheduledAtDesc(applicationId).stream().map(this::toResponse).toList();
    }

    private InterviewResponse toResponse(Interview i) {
        return InterviewResponse.builder()
                .id(i.getId())
                .applicationId(i.getApplicationId())
                .candidateId(i.getCandidateId())
                .scheduledAt(i.getScheduledAt())
                .mode(i.getMode())
                .meetLink(i.getMeetLink())
                .location(i.getLocation())
                .status(i.getStatus())
                .notes(i.getNotes())
                .build();
    }
}

