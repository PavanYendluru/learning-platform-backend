package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.*;
import com.learningplatform.backend.service.RoadmapService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoadmapController {

    private final RoadmapService roadmapService;

    public RoadmapController(RoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @GetMapping("/roadmaps")
    public ResponseEntity<ApiResponse<List<RoadmapResponse>>> getPublishedRoadmaps() {
        return ResponseEntity.ok(ApiResponse.success("Roadmaps retrieved successfully",
                roadmapService.getPublishedRoadmaps()));
    }

    @GetMapping("/roadmaps/{id}")
    public ResponseEntity<ApiResponse<RoadmapResponse>> getRoadmapById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Roadmap retrieved successfully",
                roadmapService.getRoadmapById(id)));
    }

    @GetMapping("/domains/{domainId}/roadmap")
    public ResponseEntity<ApiResponse<RoadmapResponse>> getRoadmapByDomain(@PathVariable Long domainId) {
        return ResponseEntity.ok(ApiResponse.success("Domain roadmap retrieved successfully",
                roadmapService.getRoadmapByDomain(domainId)));
    }

    @GetMapping("/admin/domains/{domainId}/roadmap")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoadmapResponse>> getAdminRoadmapByDomain(@PathVariable Long domainId) {
        return ResponseEntity.ok(ApiResponse.success("Domain roadmap retrieved successfully",
                roadmapService.getAdminRoadmapByDomain(domainId)));
    }

    @PostMapping({"/admin/domains/{domainId}/roadmap", "/domains/{domainId}/roadmap"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoadmapResponse>> createOrUpdateDomainRoadmap(
            @PathVariable Long domainId,
            @Valid @RequestBody RoadmapRequest request) {
        request.setDomainId(domainId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Domain roadmap saved successfully",
                        roadmapService.createRoadmap(request)));
    }

    @PostMapping("/admin/roadmaps")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoadmapResponse>> createRoadmap(
            @Valid @RequestBody RoadmapRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Roadmap created successfully",
                        roadmapService.createRoadmap(request)));
    }

    @PutMapping({"/admin/roadmaps/{id}", "/roadmaps/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoadmapResponse>> updateRoadmap(
            @PathVariable Long id,
            @Valid @RequestBody RoadmapRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Roadmap updated successfully",
                roadmapService.updateRoadmap(id, request)));
    }

    @DeleteMapping({"/admin/roadmaps/{id}", "/roadmaps/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRoadmap(@PathVariable Long id) {
        roadmapService.deleteRoadmap(id);
        return ResponseEntity.ok(ApiResponse.success("Roadmap deleted successfully"));
    }

    @PostMapping({"/admin/roadmaps/{roadmapId}/steps", "/roadmaps/{roadmapId}/steps"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoadmapStepResponse>> addStep(
            @PathVariable Long roadmapId,
            @Valid @RequestBody RoadmapStepRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Roadmap step added successfully",
                        roadmapService.addStep(roadmapId, request)));
    }

    @PutMapping({"/admin/roadmap-steps/{stepId}", "/roadmap-steps/{stepId}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoadmapStepResponse>> updateStep(
            @PathVariable Long stepId,
            @Valid @RequestBody RoadmapStepRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Roadmap step updated successfully",
                roadmapService.updateStep(stepId, request)));
    }

    @DeleteMapping({"/admin/roadmap-steps/{stepId}", "/roadmap-steps/{stepId}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStep(@PathVariable Long stepId) {
        roadmapService.deleteStep(stepId);
        return ResponseEntity.ok(ApiResponse.success("Roadmap step deleted successfully"));
    }

    @PutMapping({"/admin/roadmaps/{roadmapId}/reorder", "/roadmaps/{roadmapId}/reorder"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoadmapResponse>> reorderSteps(
            @PathVariable Long roadmapId,
            @Valid @RequestBody RoadmapReorderRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Roadmap steps reordered successfully",
                roadmapService.reorderSteps(roadmapId, request.getStepIds())));
    }
}
