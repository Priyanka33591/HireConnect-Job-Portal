package com.hireconnect.job.repo;

import com.hireconnect.job.domain.JobBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobBookmarkRepository extends JpaRepository<JobBookmark, Long> {
    List<JobBookmark> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<JobBookmark> findByUserIdAndJobId(Long userId, Long jobId);

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    void deleteByUserIdAndJobId(Long userId, Long jobId);
}

