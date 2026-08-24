package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.dto.RoadmapRequest;
import com.learningplatform.backend.dto.RoadmapResponse;
import com.learningplatform.backend.service.RoadmapService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/admin/roadmaps")
    public ResponseEntity<ApiResponse<RoadmapResponse>> createRoadmap(
            @Valid @RequestBody RoadmapRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Roadmap created successfully",
                        roadmapService.createRoadmap(request)));
    }

    @PutMapping("/admin/roadmaps/{id}")
    public ResponseEntity<ApiResponse<RoadmapResponse>> updateRoadmap(
            @PathVariable Long id,
            @Valid @RequestBody RoadmapRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Roadmap updated successfully",
                roadmapService.updateRoadmap(id, request)));
    }

    @DeleteMapping("/admin/roadmaps/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoadmap(@PathVariable Long id) {
        roadmapService.deleteRoadmap(id);
        return ResponseEntity.ok(ApiResponse.success("Roadmap deleted successfully"));
    }
}