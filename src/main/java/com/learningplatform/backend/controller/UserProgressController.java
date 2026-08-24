package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.dto.UserProgressRequest;
import com.learningplatform.backend.dto.UserProgressResponse;
import com.learningplatform.backend.service.UserProgressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/progress")
public class UserProgressController {

    private final UserProgressService progressService;

    public UserProgressController(UserProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/{topicId}")
    public ResponseEntity<ApiResponse<UserProgressResponse>> saveProgress(
            @PathVariable Long topicId,
            @Valid @RequestBody UserProgressRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Progress saved successfully",
                progressService.saveProgress(topicId, request)));
    }

    @PutMapping("/{topicId}")
    public ResponseEntity<ApiResponse<UserProgressResponse>> updateProgress(
            @PathVariable Long topicId,
            @Valid @RequestBody UserProgressRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Progress updated successfully",
                progressService.saveProgress(topicId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserProgressResponse>>> getMyProgress() {
        return ResponseEntity.ok(ApiResponse.success("Progress retrieved successfully",
                progressService.getMyProgress()));
    }
}