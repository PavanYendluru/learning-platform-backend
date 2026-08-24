package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.client.YouTubeClient;
import com.learningplatform.backend.dto.YouTubeVideoResponse;
import com.learningplatform.backend.exception.BadRequestException;
import com.learningplatform.backend.service.YouTubeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YouTubeServiceImpl implements YouTubeService {

    private final YouTubeClient youTubeClient;

    public YouTubeServiceImpl(YouTubeClient youTubeClient) {
        this.youTubeClient = youTubeClient;
    }

    @Override
    public List<YouTubeVideoResponse> search(String query, int maxResults) {
        if (query == null || query.isBlank()) {
            throw new BadRequestException("Search query is required");
        }
        if (maxResults < 1 || maxResults > 25) {
            maxResults = 10;
        }
        return youTubeClient.search(query.trim(), maxResults);
    }
}