package com.hireconnect.profile.service;

import com.hireconnect.profile.api.dto.*;
import com.hireconnect.profile.domain.Address;
import com.hireconnect.profile.domain.CandidateProfile;
import com.hireconnect.profile.domain.RecruiterProfile;
import com.hireconnect.profile.repo.CandidateProfileRepository;
import com.hireconnect.profile.repo.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final CandidateProfileRepository candidateRepo;
    private final RecruiterProfileRepository recruiterRepo;

    @Transactional
    public ProfileResponse createCandidate(CandidateProfileRequest req) {
        CandidateProfile profile = CandidateProfile.builder()
                .userId(req.getUserId())
                .fullName(req.getFullName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .dob(req.getDob())
                .gender(req.getGender())
                .currentLocation(req.getCurrentLocation())
                .headline(req.getHeadline())
                .profileSummary(req.getProfileSummary())
                .skills(req.getSkills())
                .experienceYears(req.getExperienceYears())
                .resumeUrl(req.getResumeUrl())
                .linkedInUrl(req.getLinkedInUrl())
                .portfolioUrl(req.getPortfolioUrl())
                .githubUrl(req.getGithubUrl())
                .leetcodeUrl(req.getLeetcodeUrl())
                .currentCompany(req.getCurrentCompany())
                .currentRole(req.getCurrentRole())
                .highestEducation(req.getHighestEducation())
                .universityName(req.getUniversityName())
                .graduationPercentage(req.getGraduationPercentage())
                .tenthPercentage(req.getTenthPercentage())
                .twelfthPercentage(req.getTwelfthPercentage())
                .preferredLocation(req.getPreferredLocation())
                .preferredJobType(req.getPreferredJobType())
                .expectedSalary(req.getExpectedSalary())
                .educations(req.getEducations())
                .projects(req.getProjects())
                .certifications(req.getCertifications())
                .languages(req.getLanguages())
                .internships(req.getInternships())
                .achievements(req.getAchievements())
                .preferredLocations(req.getPreferredLocations())
                .address(toAddress(req.getAddress()))
                .build();
        profile = candidateRepo.save(profile);
        return toResponse(profile);
    }

    @Transactional
    public ProfileResponse createRecruiter(RecruiterProfileRequest req) {
        RecruiterProfile profile = RecruiterProfile.builder()
                .userId(req.getUserId())
                .recruiterName(req.getRecruiterName())
                .companyName(req.getCompanyName())
                .gender(req.getGender())
                .dob(req.getDob())
                .companyLocations(req.getCompanyLocations())
                .companyWebsite(req.getCompanyWebsite())
                .phone(req.getPhone())
                .address(toAddress(req.getAddress()))
                .build();
        profile = recruiterRepo.save(profile);
        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getById(Long id) {
        return candidateRepo.findById(id).map(this::toResponse)
                .or(() -> recruiterRepo.findById(id).map(this::toResponse))
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + id));
    }

    @Transactional(readOnly = true)
    public ProfileResponse getByUserId(Long userId) {
        return candidateRepo.findByUserId(userId).map(this::toResponse)
                .or(() -> recruiterRepo.findByUserId(userId).map(this::toResponse))
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for userId: " + userId));
    }

    @Transactional
    public ProfileResponse updateCandidate(Long id, CandidateProfileRequest req) {
        CandidateProfile profile = candidateRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Candidate profile not found: " + id));
        profile.setFullName(req.getFullName());
        profile.setEmail(req.getEmail());
        profile.setPhone(req.getPhone());
        profile.setDob(req.getDob());
        profile.setGender(req.getGender());
        profile.setCurrentLocation(req.getCurrentLocation());
        profile.setHeadline(req.getHeadline());
        profile.setProfileSummary(req.getProfileSummary());
        profile.setSkills(req.getSkills());
        profile.setExperienceYears(req.getExperienceYears());
        profile.setResumeUrl(req.getResumeUrl());
        profile.setLinkedInUrl(req.getLinkedInUrl());
        profile.setPortfolioUrl(req.getPortfolioUrl());
        profile.setGithubUrl(req.getGithubUrl());
        profile.setLeetcodeUrl(req.getLeetcodeUrl());
        profile.setCurrentCompany(req.getCurrentCompany());
        profile.setCurrentRole(req.getCurrentRole());
        profile.setHighestEducation(req.getHighestEducation());
        profile.setUniversityName(req.getUniversityName());
        profile.setGraduationPercentage(req.getGraduationPercentage());
        profile.setTenthPercentage(req.getTenthPercentage());
        profile.setTwelfthPercentage(req.getTwelfthPercentage());
        profile.setPreferredLocation(req.getPreferredLocation());
        profile.setPreferredJobType(req.getPreferredJobType());
        profile.setExpectedSalary(req.getExpectedSalary());
        profile.setEducations(req.getEducations());
        profile.setProjects(req.getProjects());
        profile.setCertifications(req.getCertifications());
        profile.setLanguages(req.getLanguages());
        profile.setInternships(req.getInternships());
        profile.setAchievements(req.getAchievements());
        profile.setPreferredLocations(req.getPreferredLocations());
        profile.setAddress(toAddress(req.getAddress()));
        return toResponse(profile);
    }

    @Transactional
    public ProfileResponse updateRecruiter(Long id, RecruiterProfileRequest req) {
        RecruiterProfile profile = recruiterRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recruiter profile not found: " + id));
        profile.setRecruiterName(req.getRecruiterName());
        profile.setCompanyName(req.getCompanyName());
        profile.setGender(req.getGender());
        profile.setDob(req.getDob());
        profile.setCompanyLocations(req.getCompanyLocations());
        profile.setCompanyWebsite(req.getCompanyWebsite());
        profile.setPhone(req.getPhone());
        profile.setAddress(toAddress(req.getAddress()));
        return toResponse(profile);
    }

    @Transactional
    public void deleteById(Long id) {
        if (candidateRepo.existsById(id)) {
            candidateRepo.deleteById(id);
            return;
        }
        if (recruiterRepo.existsById(id)) {
            recruiterRepo.deleteById(id);
            return;
        }
        throw new IllegalArgumentException("Profile not found: " + id);
    }

    private Address toAddress(AddressDto dto) {
        if (dto == null) return null;
        return Address.builder()
                .line1(dto.getLine1())
                .line2(dto.getLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .postalCode(dto.getPostalCode())
                .build();
    }

    private AddressDto toAddressDto(Address addr) {
        if (addr == null) return null;
        AddressDto dto = new AddressDto();
        dto.setLine1(addr.getLine1());
        dto.setLine2(addr.getLine2());
        dto.setCity(addr.getCity());
        dto.setState(addr.getState());
        dto.setCountry(addr.getCountry());
        dto.setPostalCode(addr.getPostalCode());
        return dto;
    }

    private ProfileResponse toResponse(CandidateProfile p) {
        return ProfileResponse.builder()
                .id(p.getId())
                .type("CANDIDATE")
                .userId(p.getUserId())
                .fullName(p.getFullName())
                .email(p.getEmail())
                .phone(p.getPhone())
                .dob(p.getDob())
                .gender(p.getGender())
                .currentLocation(p.getCurrentLocation())
                .headline(p.getHeadline())
                .profileSummary(p.getProfileSummary())
                .skills(p.getSkills())
                .experienceYears(p.getExperienceYears())
                .resumeUrl(p.getResumeUrl())
                .linkedInUrl(p.getLinkedInUrl())
                .portfolioUrl(p.getPortfolioUrl())
                .githubUrl(p.getGithubUrl())
                .leetcodeUrl(p.getLeetcodeUrl())
                .currentCompany(p.getCurrentCompany())
                .currentRole(p.getCurrentRole())
                .highestEducation(p.getHighestEducation())
                .universityName(p.getUniversityName())
                .graduationPercentage(p.getGraduationPercentage())
                .tenthPercentage(p.getTenthPercentage())
                .twelfthPercentage(p.getTwelfthPercentage())
                .preferredLocation(p.getPreferredLocation())
                .preferredJobType(p.getPreferredJobType())
                .expectedSalary(p.getExpectedSalary())
                .educations(p.getEducations())
                .projects(p.getProjects())
                .certifications(p.getCertifications())
                .languages(p.getLanguages())
                .internships(p.getInternships())
                .achievements(p.getAchievements())
                .preferredLocations(p.getPreferredLocations())
                .address(toAddressDto(p.getAddress()))
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private ProfileResponse toResponse(RecruiterProfile p) {
        return ProfileResponse.builder()
                .id(p.getId())
                .type("RECRUITER")
                .userId(p.getUserId())
                .recruiterName(p.getRecruiterName())
                .companyName(p.getCompanyName())
                .gender(p.getGender())
                .dob(p.getDob())
                .companyLocations(p.getCompanyLocations())
                .companyWebsite(p.getCompanyWebsite())
                .phone(p.getPhone())
                .address(toAddressDto(p.getAddress()))
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}

