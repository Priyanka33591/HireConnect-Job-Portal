package com.hireconnect.profile.service;

import com.hireconnect.profile.api.dto.CandidateProfileRequest;
import com.hireconnect.profile.api.dto.ProfileResponse;
import com.hireconnect.profile.api.dto.RecruiterProfileRequest;
import com.hireconnect.profile.domain.CandidateProfile;
import com.hireconnect.profile.domain.RecruiterProfile;
import com.hireconnect.profile.repo.CandidateProfileRepository;
import com.hireconnect.profile.repo.RecruiterProfileRepository;
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
public class ProfileServiceTest {

    @Mock
    private CandidateProfileRepository candidateRepo;

    @Mock
    private RecruiterProfileRepository recruiterRepo;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void createCandidate_Success() {
        CandidateProfileRequest req = CandidateProfileRequest.builder()
                .userId(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .build();

        CandidateProfile profile = CandidateProfile.builder()
                .id(100L)
                .userId(1L)
                .fullName("John Doe")
                .build();

        when(candidateRepo.save(any(CandidateProfile.class))).thenReturn(profile);

        ProfileResponse response = profileService.createCandidate(req);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("CANDIDATE", response.getType());
    }

    @Test
    void createRecruiter_Success() {
        RecruiterProfileRequest req = RecruiterProfileRequest.builder()
                .userId(2L)
                .recruiterName("Jane Recruiter")
                .companyName("HireConnect")
                .build();

        RecruiterProfile profile = RecruiterProfile.builder()
                .id(200L)
                .userId(2L)
                .recruiterName("Jane Recruiter")
                .build();

        when(recruiterRepo.save(any(RecruiterProfile.class))).thenReturn(profile);

        ProfileResponse response = profileService.createRecruiter(req);

        assertNotNull(response);
        assertEquals(200L, response.getId());
        assertEquals("RECRUITER", response.getType());
    }

    @Test
    void getByUserId_CandidateFound() {
        CandidateProfile profile = CandidateProfile.builder()
                .id(100L)
                .userId(1L)
                .fullName("John Doe")
                .build();

        when(candidateRepo.findByUserId(1L)).thenReturn(Optional.of(profile));

        ProfileResponse response = profileService.getByUserId(1L);

        assertEquals(100L, response.getId());
        assertEquals("CANDIDATE", response.getType());
    }
}
