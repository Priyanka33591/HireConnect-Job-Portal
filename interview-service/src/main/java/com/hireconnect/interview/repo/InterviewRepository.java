package com.hireconnect.interview.repo;

import com.hireconnect.interview.domain.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByApplicationIdOrderByScheduledAtDesc(Long applicationId);
}

