package com.hireconnect.application.repo;

import com.hireconnect.application.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCandidateIdOrderByAppliedAtDesc(Long candidateId);
    List<Application> findByJobIdOrderByAppliedAtDesc(Long jobId);
    long countByCandidateIdAndAppliedAtAfter(Long candidateId, java.time.Instant since);
}

