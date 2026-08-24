package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.YouTubeVideoResponse;
import java.util.List;

public interface YouTubeService {
    List<YouTubeVideoResponse> search(String query, int maxResults);
}