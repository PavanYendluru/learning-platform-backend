package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.dto.PagedResponse;
import com.learningplatform.backend.dto.ResourceRequest;
import com.learningplatform.backend.dto.ResourceResponse;
import com.learningplatform.backend.dto.ResourceVerificationRequest;
import com.learningplatform.backend.enums.ResourceType;
import com.learningplatform.backend.enums.ResourceStatus;
import com.learningplatform.backend.service.ResourceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    /**
     * Public search + filter + pagination
     * GET /api/resources?page=0&size=10&topicId=1&resourceType=VIDEO&difficulty=BEGINNER&q=spring
     */
    @GetMapping("/resources")
    public ResponseEntity<ApiResponse<PagedResponse<ResourceResponse>>> searchResources(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(50) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) ResourceType resourceType,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String q
    ) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<ResourceResponse> result = resourceService.searchPublishedResources(
                topicId, resourceType, difficulty, q, pageable
        );

        return ResponseEntity.ok(ApiResponse.success("Resources retrieved successfully", result));
    }

    @GetMapping("/resources/{id}")
    public ResponseEntity<ApiResponse<ResourceResponse>> getResourceById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Resource retrieved successfully", resourceService.getResourceById(id))
        );
    }

    /** Admin-only resource listing, including non-published statuses. */
    @GetMapping("/admin/resources")
    public ResponseEntity<ApiResponse<PagedResponse<ResourceResponse>>> searchAdminResources(
            @RequestParam(required = false) ResourceStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(50) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) ResourceType resourceType,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String q) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(ApiResponse.success("Resources retrieved successfully",
                resourceService.searchResources(status, topicId, resourceType, difficulty, q, pageable)));
    }

    @GetMapping("/admin/resources/{id}")
    public ResponseEntity<ApiResponse<ResourceResponse>> getAdminResourceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Resource retrieved successfully",
                resourceService.getAdminResourceById(id)));
    }
    @PostMapping("/admin/resources/{id}/submit-for-review")
    public ResponseEntity<ApiResponse<ResourceResponse>> submitForReview(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Resource submitted for review",
                resourceService.submitForReview(id)));
    }

    @PostMapping("/admin/resources/{id}/approve")
    public ResponseEntity<ApiResponse<ResourceResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Resource approved and published",
                resourceService.approve(id)));
    }

    @PostMapping("/admin/resources/{id}/reject")
    public ResponseEntity<ApiResponse<ResourceResponse>> reject(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Resource rejected",
                resourceService.reject(id)));
    }

    @PutMapping("/admin/resources/{id}/verification")
    public ResponseEntity<ApiResponse<ResourceResponse>> updateVerification(
            @PathVariable Long id,
            @Valid @RequestBody ResourceVerificationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Verification status updated",
                resourceService.updateVerification(id, request)));
    }

    @PostMapping("/admin/resources")
    public ResponseEntity<ApiResponse<ResourceResponse>> createResource(
            @Valid @RequestBody ResourceRequest request) {
        ResourceResponse created = resourceService.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resource created successfully", created));
    }

    @PutMapping("/admin/resources/{id}")
    public ResponseEntity<ApiResponse<ResourceResponse>> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Resource updated successfully",
                        resourceService.updateResource(id, request))
        );
    }

    @DeleteMapping("/admin/resources/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(ApiResponse.success("Resource deleted successfully"));
    }
}
