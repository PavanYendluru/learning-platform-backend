package com.learningplatform.backend.client;

import com.learningplatform.backend.dto.YouTubeVideoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class YouTubeClient {

    @Value("${app.youtube.api-key:}")
    private String apiKey;

    @Value("${app.youtube.base-url:https://www.googleapis.com/youtube/v3}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<YouTubeVideoResponse> search(String query, int maxResults) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("YouTube API key is not configured");
        }

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/search")
                .queryParam("part", "snippet")
                .queryParam("q", query)
                .queryParam("type", "video")
                .queryParam("maxResults", maxResults)
                .queryParam("key", apiKey)
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        List<YouTubeVideoResponse> results = new ArrayList<>();
        if (response != null && response.get("items") instanceof List<?> items) {
            for (Object item : items) {
                Map<String, Object> itemMap = (Map<String, Object>) item;
                Map<String, Object> id = (Map<String, Object>) itemMap.get("id");
                Map<String, Object> snippet = (Map<String, Object>) itemMap.get("snippet");
                Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
                Map<String, Object> medium = (Map<String, Object>) thumbnails.get("medium");

                YouTubeVideoResponse video = new YouTubeVideoResponse();
                video.setVideoId((String) id.get("videoId"));
                video.setTitle((String) snippet.get("title"));
                video.setDescription((String) snippet.get("description"));
                video.setChannelTitle((String) snippet.get("channelTitle"));
                video.setPublishedAt((String) snippet.get("publishedAt"));
                video.setThumbnailUrl((String) medium.get("url"));
                video.setVideoUrl("https://www.youtube.com/watch?v=" + video.getVideoId());
                results.add(video);
            }
        }
        return results;
    }
}