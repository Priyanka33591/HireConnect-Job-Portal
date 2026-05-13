package com.hireconnect.interview.service;

import com.hireconnect.interview.api.dto.InterviewCreateRequest;
import com.hireconnect.interview.api.dto.InterviewRescheduleRequest;
import com.hireconnect.interview.api.dto.InterviewResponse;
import com.hireconnect.interview.domain.Interview;
import com.hireconnect.interview.domain.InterviewStatus;
import com.hireconnect.interview.repo.InterviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InterviewServiceTest {

    @Mock
    private InterviewRepository repo;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private InterviewService interviewService;

    private InterviewCreateRequest createRequest;
    private Interview interview;

    @BeforeEach
    void setUp() {
        createRequest = InterviewCreateRequest.builder()
                .applicationId(1L)
                .candidateId(2L)
                .scheduledAt(Instant.now().plusSeconds(3600))
                .mode("ONLINE")
                .meetLink("http://meet.com/test")
                .build();

        interview = Interview.builder()
                .id(100L)
                .applicationId(1L)
                .candidateId(2L)
                .scheduledAt(createRequest.getScheduledAt())
                .status(InterviewStatus.SCHEDULED)
                .build();
    }

    @Test
    void schedule_Success() {
        when(repo.save(any(Interview.class))).thenReturn(interview);

        InterviewResponse response = interviewService.schedule(createRequest);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(InterviewStatus.SCHEDULED, response.getStatus());
        verify(repo, times(1)).save(any(Interview.class));
    }

    @Test
    void confirm_Success() {
        when(repo.findById(100L)).thenReturn(Optional.of(interview));

        InterviewResponse response = interviewService.confirm(100L);

        assertEquals(InterviewStatus.CONFIRMED, response.getStatus());
    }

    @Test
    void reschedule_Success() {
        when(repo.findById(100L)).thenReturn(Optional.of(interview));
        Instant newTime = Instant.now().plusSeconds(7200);
        InterviewRescheduleRequest rescheduleRequest = InterviewRescheduleRequest.builder()
                .scheduledAt(newTime)
                .build();

        InterviewResponse response = interviewService.reschedule(100L, rescheduleRequest);

        assertEquals(InterviewStatus.RESCHEDULED, response.getStatus());
        assertEquals(newTime, response.getScheduledAt());
    }
}
