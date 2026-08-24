package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.dto.DomainRequest;
import com.learningplatform.backend.dto.DomainResponse;
import com.learningplatform.backend.service.DomainService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for Domain.
 *
 * Public endpoints  → /api/domains
 * Admin endpoints   → /api/admin/domains  (will be protected in Phase 3)
 */
@RestController
@RequestMapping("/api")
public class DomainController {

    private final DomainService domainService;

    public DomainController(DomainService domainService) {
        this.domainService = domainService;
    }

    /**
     * Public: Get all active domains
     * GET /api/domains
     */
    @GetMapping("/domains")
    public ResponseEntity<ApiResponse<List<DomainResponse>>> getAllDomains() {
        List<DomainResponse> domains = domainService.getAllActiveDomains();
        return ResponseEntity.ok(ApiResponse.success("Domains retrieved successfully", domains));
    }

    /**
     * Public: Get domain by ID
     * GET /api/domains/{id}
     */
    @GetMapping("/domains/{id}")
    public ResponseEntity<ApiResponse<DomainResponse>> getDomainById(@PathVariable Long id) {
        DomainResponse domain = domainService.getDomainById(id);
        return ResponseEntity.ok(ApiResponse.success("Domain retrieved successfully", domain));
    }

    @GetMapping("/admin/domains")
    public ResponseEntity<ApiResponse<List<DomainResponse>>> getAllDomainsForAdmin() {
        return ResponseEntity.ok(ApiResponse.success("Domains retrieved successfully",
                domainService.getAllDomainsForAdmin()));
    }

    @GetMapping("/admin/domains/{id}")
    public ResponseEntity<ApiResponse<DomainResponse>> getDomainByIdForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Domain retrieved successfully",
                domainService.getDomainByIdForAdmin(id)));
    }

    /**
     * Admin: Create a new domain
     * POST /api/admin/domains
     */
    @PostMapping("/admin/domains")
    public ResponseEntity<ApiResponse<DomainResponse>> createDomain(
            @Valid @RequestBody DomainRequest request) {
        DomainResponse created = domainService.createDomain(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Domain created successfully", created));
    }

    /**
     * Admin: Update an existing domain
     * PUT /api/admin/domains/{id}
     */
    @PutMapping("/admin/domains/{id}")
    public ResponseEntity<ApiResponse<DomainResponse>> updateDomain(
            @PathVariable Long id,
            @Valid @RequestBody DomainRequest request) {
        DomainResponse updated = domainService.updateDomain(id, request);
        return ResponseEntity.ok(ApiResponse.success("Domain updated successfully", updated));
    }

    /**
     * Admin: Delete a domain
     * DELETE /api/admin/domains/{id}
     */
    @DeleteMapping("/admin/domains/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDomain(@PathVariable Long id) {
        domainService.deleteDomain(id);
        return ResponseEntity.ok(ApiResponse.success("Domain deleted successfully"));
    }
}
