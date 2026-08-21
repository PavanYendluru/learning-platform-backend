package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.dto.ResourceRequest;
import com.learningplatform.backend.dto.ResourceResponse;
import com.learningplatform.backend.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    /**
     * Public: Get all published resources
     * GET /api/resources
     */
    @GetMapping("/resources")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getPublishedResources() {
        List<ResourceResponse> resources = resourceService.getPublishedResources();
        return ResponseEntity.ok(ApiResponse.success("Published resources retrieved successfully", resources));
    }

    /**
     * Public: Get published resources of a specific topic
     * GET /api/topics/{topicId}/resources
     */
    @GetMapping("/topics/{topicId}/resources")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getResourcesByTopic(
            @PathVariable Long topicId) {
        List<ResourceResponse> resources = resourceService.getPublishedResourcesByTopic(topicId);
        return ResponseEntity.ok(ApiResponse.success("Resources retrieved successfully", resources));
    }

    /**
     * Public: Get a single resource by ID
     * GET /api/resources/{id}
     */
    @GetMapping("/resources/{id}")
    public ResponseEntity<ApiResponse<ResourceResponse>> getResourceById(@PathVariable Long id) {
        ResourceResponse resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(ApiResponse.success("Resource retrieved successfully", resource));
    }

    /**
     * Admin: Create a new resource
     * POST /api/admin/resources
     */
    @PostMapping("/admin/resources")
    public ResponseEntity<ApiResponse<ResourceResponse>> createResource(
            @Valid @RequestBody ResourceRequest request) {
        ResourceResponse created = resourceService.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resource created successfully", created));
    }

    /**
     * Admin: Update a resource
     * PUT /api/admin/resources/{id}
     */
    @PutMapping("/admin/resources/{id}")
    public ResponseEntity<ApiResponse<ResourceResponse>> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {
        ResourceResponse updated = resourceService.updateResource(id, request);
        return ResponseEntity.ok(ApiResponse.success("Resource updated successfully", updated));
    }

    /**
     * Admin: Delete a resource
     * DELETE /api/admin/resources/{id}
     */
    @DeleteMapping("/admin/resources/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(ApiResponse.success("Resource deleted successfully"));
    }
}