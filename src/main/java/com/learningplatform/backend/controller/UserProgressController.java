package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.dto.DashboardDataResponse;
import com.learningplatform.backend.dto.DomainProgressResponse;
import com.learningplatform.backend.dto.UserProgressRequest;
import com.learningplatform.backend.dto.UserProgressResponse;
import com.learningplatform.backend.service.UserProgressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserProgressController {

    private final UserProgressService progressService;

    public UserProgressController(UserProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/users/progress/{topicId}")
    public ResponseEntity<ApiResponse<UserProgressResponse>> saveProgress(
            @PathVariable Long topicId,
            @Valid @RequestBody UserProgressRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Progress saved successfully",
                progressService.saveProgress(topicId, request)));
    }

    @PutMapping("/users/progress/{topicId}")
    public ResponseEntity<ApiResponse<UserProgressResponse>> updateProgress(
            @PathVariable Long topicId,
            @Valid @RequestBody UserProgressRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Progress updated successfully",
                progressService.saveProgress(topicId, request)));
    }

    @GetMapping({"/progress/dashboard", "/users/progress/dashboard"})
    public ResponseEntity<ApiResponse<DashboardDataResponse>> getDashboardData() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved successfully",
                progressService.getDashboardData()));
    }

    @GetMapping("/users/progress")
    public ResponseEntity<ApiResponse<List<UserProgressResponse>>> getMyProgress() {
        return ResponseEntity.ok(ApiResponse.success("Progress retrieved successfully",
                progressService.getMyProgress()));
    }

    @PostMapping("/users/progress/resource/{resourceId}")
    public ResponseEntity<ApiResponse<UserProgressResponse>> completeResource(
            @PathVariable Long resourceId) {
        return ResponseEntity.ok(ApiResponse.success("Resource marked as completed",
                progressService.completeResource(resourceId)));
    }

    @DeleteMapping("/users/progress/resource/{resourceId}")
    public ResponseEntity<ApiResponse<UserProgressResponse>> uncompleteResource(
            @PathVariable Long resourceId) {
        return ResponseEntity.ok(ApiResponse.success("Resource marked as in progress",
                progressService.uncompleteResource(resourceId)));
    }

    @GetMapping("/users/completed-resources")
    public ResponseEntity<ApiResponse<List<Map<String, Long>>>> getCompletedResources() {
        List<Long> ids = progressService.getCompletedResourceIds();
        List<Map<String, Long>> mapped = ids.stream()
                .map(id -> Map.of("resourceId", id))
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Completed resources retrieved successfully", mapped));
    }

    @GetMapping("/users/progress/domain/{domainId}")
    public ResponseEntity<ApiResponse<DomainProgressResponse>> getDomainProgress(
            @PathVariable Long domainId) {
        return ResponseEntity.ok(ApiResponse.success("Domain progress retrieved successfully",
                progressService.getDomainProgress(domainId)));
    }
}
