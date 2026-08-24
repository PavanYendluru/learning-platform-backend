package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.RoadmapRequest;
import com.learningplatform.backend.dto.RoadmapResponse;
import java.util.List;

public interface RoadmapService {
    List<RoadmapResponse> getPublishedRoadmaps();
    RoadmapResponse getRoadmapById(Long id);
    RoadmapResponse createRoadmap(RoadmapRequest request);
    RoadmapResponse updateRoadmap(Long id, RoadmapRequest request);
    void deleteRoadmap(Long id);
}