package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.dto.ResearchPaperResponse;
import com.learningplatform.backend.service.ResearchPaperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/research")
public class ResearchPaperController {

    private final ResearchPaperService researchPaperService;

    public ResearchPaperController(ResearchPaperService researchPaperService) {
        this.researchPaperService = researchPaperService;
    }

    /**
     * Public search via Crossref
     * GET /api/research/search?q=machine+learning&rows=10
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ResearchPaperResponse>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int rows) {

        List<ResearchPaperResponse> results = researchPaperService.search(q, rows);
        return ResponseEntity.ok(ApiResponse.success("Research papers retrieved successfully", results));
    }
}