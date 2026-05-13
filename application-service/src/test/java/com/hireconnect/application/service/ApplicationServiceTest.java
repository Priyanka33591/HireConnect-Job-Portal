package com.hireconnect.application.service;

import com.hireconnect.application.api.dto.ApplicationCreateRequest;
import com.hireconnect.application.api.dto.ApplicationResponse;
import com.hireconnect.application.domain.Application;
import com.hireconnect.application.domain.ApplicationStatus;
import com.hireconnect.application.repo.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository repo;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private SubscriptionClient subscriptionClient;

    @InjectMocks
    private ApplicationService applicationService;

    private ApplicationCreateRequest createRequest;
    private SubscriptionClient.SubscriptionStatusResponse subResponse;

    @BeforeEach
    void setUp() {
        createRequest = ApplicationCreateRequest.builder()
                .jobId(101L)
                .candidateId(202L)
                .coverLetter("Test cover letter")
                .resumeUrl("http://resume.com/202")
                .build();

        subResponse = new SubscriptionClient.SubscriptionStatusResponse();
        subResponse.setPlan("FREE");
        subResponse.setStatus("ACTIVE");
    }

    @Test
    void apply_Success() {
        when(subscriptionClient.getCurrentSubscription(anyLong(), anyString())).thenReturn(subResponse);
        when(repo.countByCandidateIdAndAppliedAtAfter(anyLong(), any())).thenReturn(5L);
        when(repo.save(any(Application.class))).thenAnswer(invocation -> {
            Application app = invocation.getArgument(0);
            return Application.builder()
                    .id(500L)
                    .jobId(app.getJobId())
                    .candidateId(app.getCandidateId())
                    .status(app.getStatus())
                    .build();
        });

        ApplicationResponse response = applicationService.apply(createRequest);

        assertNotNull(response);
        assertEquals(500L, response.getId());
        assertEquals(ApplicationStatus.APPLIED, response.getStatus());
        verify(repo, times(1)).save(any(Application.class));
    }

    @Test
    void apply_LimitReached_ThrowsException() {
        when(subscriptionClient.getCurrentSubscription(anyLong(), anyString())).thenReturn(subResponse);
        when(repo.countByCandidateIdAndAppliedAtAfter(anyLong(), any())).thenReturn(10L);

        assertThrows(RuntimeException.class, () -> applicationService.apply(createRequest));
    }

    @Test
    void updateStatus_Success() {
        Application app = Application.builder()
                .id(1L)
                .status(ApplicationStatus.APPLIED)
                .build();
        when(repo.findById(1L)).thenReturn(Optional.of(app));

        ApplicationResponse response = applicationService.updateStatus(1L, ApplicationStatus.SHORTLISTED);

        assertEquals(ApplicationStatus.SHORTLISTED, response.getStatus());
    }

    @Test
    void updateStatus_InvalidTransition_ThrowsException() {
        Application app = Application.builder()
                .id(1L)
                .status(ApplicationStatus.APPLIED)
                .build();
        when(repo.findById(1L)).thenReturn(Optional.of(app));

        assertThrows(IllegalArgumentException.class, () -> applicationService.updateStatus(1L, ApplicationStatus.INTERVIEW));
    }
}
