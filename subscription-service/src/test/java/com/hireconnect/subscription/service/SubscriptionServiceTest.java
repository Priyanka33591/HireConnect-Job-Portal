package com.hireconnect.subscription.service;

import com.hireconnect.subscription.api.dto.SubscriptionCreateRequest;
import com.hireconnect.subscription.api.dto.SubscriptionResponse;
import com.hireconnect.subscription.domain.PlanType;
import com.hireconnect.subscription.domain.Subscription;
import com.hireconnect.subscription.domain.SubscriptionStatus;
import com.hireconnect.subscription.repo.InvoiceRepository;
import com.hireconnect.subscription.repo.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepo;

    @Mock
    private InvoiceRepository invoiceRepo;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void createFreeSubscription_Success() {
        SubscriptionCreateRequest req = SubscriptionCreateRequest.builder()
                .userId(1L)
                .userRole("RECRUITER")
                .plan(PlanType.FREE)
                .build();

        Subscription subscription = Subscription.builder()
                .id(100L)
                .userId(1L)
                .userRole("RECRUITER")
                .plan(PlanType.FREE)
                .status(SubscriptionStatus.ACTIVE)
                .build();

        when(subscriptionRepo.findByUserIdAndUserRole(1L, "RECRUITER")).thenReturn(Optional.empty());
        when(subscriptionRepo.save(any(Subscription.class))).thenReturn(subscription);

        SubscriptionResponse response = subscriptionService.create(req);

        assertNotNull(response);
        assertEquals(PlanType.FREE, response.getPlan());
        verify(subscriptionRepo, times(1)).save(any(Subscription.class));
        verify(invoiceRepo, times(1)).save(any());
    }

    @Test
    void cancelSubscription_Success() {
        Subscription subscription = Subscription.builder()
                .id(100L)
                .status(SubscriptionStatus.ACTIVE)
                .build();
        when(subscriptionRepo.findById(100L)).thenReturn(Optional.of(subscription));

        SubscriptionResponse response = subscriptionService.cancel(100L);

        assertEquals(SubscriptionStatus.CANCELLED, response.getStatus());
    }
}
