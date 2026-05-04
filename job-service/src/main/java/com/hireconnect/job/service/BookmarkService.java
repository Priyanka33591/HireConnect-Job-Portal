package com.hireconnect.job.service;

import com.hireconnect.job.api.dto.BookmarkRequest;
import com.hireconnect.job.api.dto.BookmarkResponse;
import com.hireconnect.job.domain.JobBookmark;
import com.hireconnect.job.repo.JobBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final JobBookmarkRepository repo;

    @Transactional
    public BookmarkResponse create(BookmarkRequest req) {
        if (repo.existsByUserIdAndJobId(req.getUserId(), req.getJobId())) {
            return repo.findByUserIdAndJobId(req.getUserId(), req.getJobId())
                    .map(this::toResponse)
                    .orElseThrow();
        }
        JobBookmark b = JobBookmark.builder()
                .userId(req.getUserId())
                .jobId(req.getJobId())
                .build();
        b = repo.save(b);
        return toResponse(b);
    }

    @Transactional(readOnly = true)
    public List<BookmarkResponse> byUser(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public void delete(Long userId, Long jobId) {
        repo.deleteByUserIdAndJobId(userId, jobId);
    }

    private BookmarkResponse toResponse(JobBookmark b) {
        return BookmarkResponse.builder()
                .id(b.getId())
                .userId(b.getUserId())
                .jobId(b.getJobId())
                .createdAt(b.getCreatedAt())
                .build();
    }
}

