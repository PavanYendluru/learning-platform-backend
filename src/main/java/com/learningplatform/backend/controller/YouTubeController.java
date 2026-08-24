package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.dto.YouTubeVideoResponse;
import com.learningplatform.backend.service.YouTubeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/youtube")
public class YouTubeController {

    private final YouTubeService youTubeService;

    public YouTubeController(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    /**
     * Public search (API key stays on the server)
     * GET /api/youtube/search?q=Spring+Boot&maxResults=10
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<YouTubeVideoResponse>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int maxResults) {

        List<YouTubeVideoResponse> results = youTubeService.search(q, maxResults);
        return ResponseEntity.ok(ApiResponse.success("YouTube search completed", results));
    }
}