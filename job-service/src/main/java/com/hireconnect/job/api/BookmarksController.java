package com.hireconnect.job.api;

import com.hireconnect.job.api.dto.BookmarkRequest;
import com.hireconnect.job.api.dto.BookmarkResponse;
import com.hireconnect.job.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs/bookmarks")
@RequiredArgsConstructor
public class BookmarksController {
    private final BookmarkService bookmarkService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BookmarkResponse> create(@Valid @RequestBody BookmarkRequest req) {
        return ResponseEntity.ok(bookmarkService.create(req));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<BookmarkResponse>> byUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookmarkService.byUser(userId));
    }

    @DeleteMapping("/user/{userId}/job/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long jobId) {
        bookmarkService.delete(userId, jobId);
        return ResponseEntity.noContent().build();
    }
}

