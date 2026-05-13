package com.hireconnect.notification.service;

import com.hireconnect.notification.api.dto.NotificationRequest;
import com.hireconnect.notification.api.dto.NotificationResponse;
import com.hireconnect.notification.domain.Notification;
import com.hireconnect.notification.repo.NotificationRepository;
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
public class NotificationServiceTest {

    @Mock
    private NotificationRepository repo;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationRequest createRequest;
    private Notification notification;

    @BeforeEach
    void setUp() {
        createRequest = NotificationRequest.builder()
                .userId(1L)
                .message("Test message")
                .type("TEST")
                .build();

        notification = Notification.builder()
                .id(100L)
                .userId(1L)
                .message("Test message")
                .type("TEST")
                .isRead(false)
                .build();
    }

    @Test
    void create_Success() {
        when(repo.save(any(Notification.class))).thenReturn(notification);

        NotificationResponse response = notificationService.create(createRequest);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertFalse(response.isRead());
        verify(repo, times(1)).save(any(Notification.class));
    }

    @Test
    void markRead_Success() {
        when(repo.findById(100L)).thenReturn(Optional.of(notification));

        NotificationResponse response = notificationService.markRead(100L);

        assertTrue(response.isRead());
    }
}
