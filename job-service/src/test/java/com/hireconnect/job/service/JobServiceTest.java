package com.hireconnect.job.service;

import com.hireconnect.job.api.dto.JobRequest;
import com.hireconnect.job.api.dto.JobResponse;
import com.hireconnect.job.domain.Job;
import com.hireconnect.job.domain.JobStatus;
import com.hireconnect.job.repo.JobRepository;
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
public class JobServiceTest {

    @Mock
    private JobRepository repo;

    @Mock
    private SubscriptionClient subscriptionClient;

    @InjectMocks
    private JobService jobService;

    private JobRequest jobRequest;
    private SubscriptionClient.SubscriptionStatusResponse subResponse;

    @BeforeEach
    void setUp() {
        jobRequest = JobRequest.builder()
                .title("Software Engineer")
                .companyName("HireConnect")
                .category("IT")
                .type("FULL_TIME")
                .location("New York")
                .salaryMin(50000)
                .salaryMax(100000)
                .skills("Java, Spring Boot")
                .description("Sample job")
                .experienceRequired(2)
                .postedBy(1L)
                .build();

        subResponse = new SubscriptionClient.SubscriptionStatusResponse();
        subResponse.setPlan("FREE");
        subResponse.setStatus("ACTIVE");
    }

    @Test
    void createJob_Success() {
        when(subscriptionClient.getCurrentSubscription(anyLong(), anyString())).thenReturn(subResponse);
        when(repo.countByPostedByAndPostedAtAfter(anyLong(), any())).thenReturn(5L);
        when(repo.save(any(Job.class))).thenAnswer(invocation -> {
            Job job = invocation.getArgument(0);
            return Job.builder()
                    .id(100L)
                    .title(job.getTitle())
                    .companyName(job.getCompanyName())
                    .category(job.getCategory())
                    .type(job.getType())
                    .location(job.getLocation())
                    .salaryMin(job.getSalaryMin())
                    .salaryMax(job.getSalaryMax())
                    .skills(job.getSkills())
                    .description(job.getDescription())
                    .experienceRequired(job.getExperienceRequired())
                    .postedBy(job.getPostedBy())
                    .status(job.getStatus())
                    .build();
        });

        JobResponse response = jobService.create(jobRequest);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Software Engineer", response.getTitle());
        verify(repo, times(1)).save(any(Job.class));
    }

    @Test
    void createJob_LimitReached_ThrowsException() {
        when(subscriptionClient.getCurrentSubscription(anyLong(), anyString())).thenReturn(subResponse);
        when(repo.countByPostedByAndPostedAtAfter(anyLong(), any())).thenReturn(10L); // Limit is 10 for FREE

        RuntimeException exception = assertThrows(RuntimeException.class, () -> jobService.create(jobRequest));
        assertTrue(exception.getMessage().contains("limit reached"));
        verify(repo, never()).save(any(Job.class));
    }

    @Test
    void getJob_Success() {
        Job job = Job.builder().id(1L).title("Test Job").build();
        when(repo.findById(1L)).thenReturn(Optional.of(job));

        JobResponse response = jobService.get(1L);

        assertEquals("Test Job", response.getTitle());
    }

    @Test
    void getJob_NotFound_ThrowsException() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> jobService.get(1L));
    }
}
