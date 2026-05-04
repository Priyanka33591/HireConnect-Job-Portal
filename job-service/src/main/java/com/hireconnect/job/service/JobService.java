package com.hireconnect.job.service;

import com.hireconnect.job.api.dto.JobRequest;
import com.hireconnect.job.api.dto.JobResponse;
import com.hireconnect.job.domain.Job;
import com.hireconnect.job.domain.JobStatus;
import com.hireconnect.job.repo.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class JobService {
    private final JobRepository repo;
    private final SubscriptionClient subscriptionClient;

    @Transactional
    public JobResponse create(JobRequest req) {
        // Enforcement
        checkLimit(req.getPostedBy());

        Job job = Job.builder()
                .title(req.getTitle())
                .companyName(req.getCompanyName())
                .category(req.getCategory())
                .type(req.getType())
                .location(req.getLocation())
                .salaryMin(req.getSalaryMin())
                .salaryMax(req.getSalaryMax())
                .skills(req.getSkills())
                .description(req.getDescription())
                .experienceRequired(req.getExperienceRequired())
                .postedBy(req.getPostedBy())
                .status(req.getStatus() == null ? JobStatus.OPEN : req.getStatus())
                .build();
        job = repo.save(job);
        return toResponse(job);
    }

    private void checkLimit(Long recruiterId) {
        int limit = 10; // Default fallback limit
        try {
            SubscriptionClient.SubscriptionStatusResponse sub = subscriptionClient.getCurrentSubscription(recruiterId, "RECRUITER");
            limit = switch (sub.getPlan()) {
                case "FREE" -> 10;
                case "MONTHLY_99" -> 20;
                case "MONTHLY_199" -> 50;
                default -> 10;
            };
        } catch (Exception e) {
            log.error("Failed to check subscription limit for recruiter {}: {}", recruiterId, e.getMessage());
        }

        java.time.Instant startOfMonth = java.time.ZonedDateTime.now()
                .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
                .toInstant();
        
        long count = repo.countByPostedByAndPostedAtAfter(recruiterId, startOfMonth);
        
        if (count >= limit) {
            throw new IllegalArgumentException("your free trail is over you have to but a plan");
        }
    }

    @Transactional(readOnly = true)
    public List<JobResponse> list() {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "postedAt"))
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public JobResponse get(Long id) {
        return repo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + id));
    }

    @Transactional
    public JobResponse update(Long id, JobRequest req) {
        Job job = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Job not found: " + id));
        job.setTitle(req.getTitle());
        job.setCompanyName(req.getCompanyName());
        job.setCategory(req.getCategory());
        job.setType(req.getType());
        job.setLocation(req.getLocation());
        job.setSalaryMin(req.getSalaryMin());
        job.setSalaryMax(req.getSalaryMax());
        job.setSkills(req.getSkills());
        job.setDescription(req.getDescription());
        job.setExperienceRequired(req.getExperienceRequired());
        job.setPostedBy(req.getPostedBy());
        if (req.getStatus() != null) job.setStatus(req.getStatus());
        return toResponse(job);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Job not found: " + id);
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> search(String title, String location, Integer salary, Long postedBy) {
        Specification<Job> spec = null;

        if (title != null && !title.isBlank()) {
            String t = title.trim().toLowerCase();
            Specification<Job> titleSpec = (root, q, cb) -> cb.like(cb.lower(root.get("title")), "%" + t + "%");
            spec = (spec == null) ? titleSpec : spec.and(titleSpec);
        }
        if (location != null && !location.isBlank()) {
            String l = location.trim().toLowerCase();
            Specification<Job> locationSpec = (root, q, cb) -> cb.like(cb.lower(root.get("location")), "%" + l + "%");
            spec = (spec == null) ? locationSpec : spec.and(locationSpec);
        }
        if (salary != null) {
            Specification<Job> salarySpec = (root, q, cb) -> cb.or(
                    cb.isNull(root.get("salaryMin")),
                    cb.lessThanOrEqualTo(root.get("salaryMin"), salary)
            );
            spec = (spec == null) ? salarySpec : spec.and(salarySpec);
        }
        if (postedBy != null) {
            Specification<Job> postedBySpec = (root, q, cb) -> cb.equal(root.get("postedBy"), postedBy);
            spec = (spec == null) ? postedBySpec : spec.and(postedBySpec);
        }

        if (spec == null) {
            return repo.findAll(Sort.by(Sort.Direction.DESC, "postedAt"))
                    .stream().map(this::toResponse).toList();
        }

        return repo.findAll(spec, Sort.by(Sort.Direction.DESC, "postedAt"))
                .stream().map(this::toResponse).toList();
    }

    private JobResponse toResponse(Job j) {
        return JobResponse.builder()
                .id(j.getId())
                .title(j.getTitle())
                .companyName(j.getCompanyName())
                .category(j.getCategory())
                .type(j.getType())
                .location(j.getLocation())
                .salaryMin(j.getSalaryMin())
                .salaryMax(j.getSalaryMax())
                .skills(j.getSkills())
                .description(j.getDescription())
                .experienceRequired(j.getExperienceRequired())
                .postedBy(j.getPostedBy())
                .status(j.getStatus())
                .postedAt(j.getPostedAt())
                .build();
    }
}

