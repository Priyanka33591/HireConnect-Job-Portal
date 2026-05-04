package com.hireconnect.job.repo;

import com.hireconnect.job.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    long countByPostedByAndPostedAtAfter(Long postedBy, java.time.Instant since);
}

