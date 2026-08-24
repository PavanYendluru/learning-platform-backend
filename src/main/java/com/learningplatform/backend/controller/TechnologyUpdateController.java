package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.dto.TechnologyUpdateRequest;
import com.learningplatform.backend.dto.TechnologyUpdateResponse;
import com.learningplatform.backend.service.TechnologyUpdateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TechnologyUpdateController {

    private final TechnologyUpdateService updateService;

    public TechnologyUpdateController(TechnologyUpdateService updateService) {
        this.updateService = updateService;
    }

    @GetMapping("/technology-updates")
    public ResponseEntity<ApiResponse<List<TechnologyUpdateResponse>>> getPublishedUpdates() {
        return ResponseEntity.ok(ApiResponse.success("Technology updates retrieved successfully",
                updateService.getPublishedUpdates()));
    }

    @GetMapping("/domains/{domainId}/technology-updates")
    public ResponseEntity<ApiResponse<List<TechnologyUpdateResponse>>> getByDomain(
            @PathVariable Long domainId) {
        return ResponseEntity.ok(ApiResponse.success("Technology updates retrieved successfully",
                updateService.getPublishedUpdatesByDomain(domainId)));
    }

    @GetMapping("/technology-updates/{id}")
    public ResponseEntity<ApiResponse<TechnologyUpdateResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Technology update retrieved successfully",
                updateService.getById(id)));
    }

    @PostMapping("/admin/technology-updates")
    public ResponseEntity<ApiResponse<TechnologyUpdateResponse>> create(
            @Valid @RequestBody TechnologyUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Technology update created successfully",
                        updateService.create(request)));
    }

    @PutMapping("/admin/technology-updates/{id}")
    public ResponseEntity<ApiResponse<TechnologyUpdateResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TechnologyUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Technology update updated successfully",
                updateService.update(id, request)));
    }

    @DeleteMapping("/admin/technology-updates/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        updateService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Technology update deleted successfully"));
    }
}