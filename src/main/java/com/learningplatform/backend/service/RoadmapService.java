package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.RoadmapRequest;
import com.learningplatform.backend.dto.RoadmapResponse;
import com.learningplatform.backend.dto.RoadmapStepRequest;
import com.learningplatform.backend.dto.RoadmapStepResponse;
import java.util.List;

public interface RoadmapService {
    List<RoadmapResponse> getPublishedRoadmaps();
    RoadmapResponse getRoadmapById(Long id);
    RoadmapResponse getRoadmapByDomain(Long domainId);
    RoadmapResponse getAdminRoadmapByDomain(Long domainId);
    RoadmapResponse createRoadmap(RoadmapRequest request);
    RoadmapResponse updateRoadmap(Long id, RoadmapRequest request);
    void deleteRoadmap(Long id);

    RoadmapStepResponse addStep(Long roadmapId, RoadmapStepRequest request);
    RoadmapStepResponse updateStep(Long stepId, RoadmapStepRequest request);
    void deleteStep(Long stepId);
    RoadmapResponse reorderSteps(Long roadmapId, List<Long> stepIdsInOrder);
}
