package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.dto.TopicRequest;
import com.learningplatform.backend.dto.TopicResponse;
import com.learningplatform.backend.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    /**
     * Public: Get all active topics of a domain (ordered by sequenceNumber)
     * GET /api/domains/{domainId}/topics
     */
    @GetMapping("/domains/{domainId}/topics")
    public ResponseEntity<ApiResponse<List<TopicResponse>>> getTopicsByDomain(
            @PathVariable Long domainId) {
        List<TopicResponse> topics = topicService.getTopicsByDomain(domainId);
        return ResponseEntity.ok(ApiResponse.success("Topics retrieved successfully", topics));
    }

    @GetMapping("/admin/domains/{domainId}/topics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TopicResponse>>> getTopicsByDomainForAdmin(
            @PathVariable Long domainId) {
        return ResponseEntity.ok(ApiResponse.success("Topics retrieved successfully",
                topicService.getTopicsByDomainForAdmin(domainId)));
    }

    @GetMapping("/admin/topics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TopicResponse>>> getAllTopicsForAdmin() {
        return ResponseEntity.ok(ApiResponse.success("All topics retrieved successfully",
                topicService.getAllTopicsForAdmin()));
    }

    /**
     * Public: Get a single topic by ID
     * GET /api/topics/{id}
     */
    @GetMapping("/topics/{id}")
    public ResponseEntity<ApiResponse<TopicResponse>> getTopicById(@PathVariable Long id) {
        TopicResponse topic = topicService.getTopicById(id);
        return ResponseEntity.ok(ApiResponse.success("Topic retrieved successfully", topic));
    }

    @GetMapping("/admin/topics/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TopicResponse>> getTopicByIdForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Topic retrieved successfully",
                topicService.getTopicByIdForAdmin(id)));
    }

    /**
     * Admin: Create a new topic under a domain
     * POST /api/admin/domains/{domainId}/topics or /api/domains/{domainId}/topics
     */
    @PostMapping({"/admin/domains/{domainId}/topics", "/domains/{domainId}/topics"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TopicResponse>> createTopic(
            @PathVariable Long domainId,
            @Valid @RequestBody TopicRequest request) {
        TopicResponse created = topicService.createTopic(domainId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Topic created successfully", created));
    }

    /**
     * Admin: Update a topic
     * PUT /api/admin/topics/{id} or /api/topics/{id}
     */
    @PutMapping({"/admin/topics/{id}", "/topics/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TopicResponse>> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicRequest request) {
        TopicResponse updated = topicService.updateTopic(id, request);
        return ResponseEntity.ok(ApiResponse.success("Topic updated successfully", updated));
    }

    /**
     * Admin: Delete a topic
     * DELETE /api/admin/topics/{id} or /api/topics/{id}
     */
    @DeleteMapping({"/admin/topics/{id}", "/topics/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok(ApiResponse.success("Topic deleted successfully"));
    }
}
