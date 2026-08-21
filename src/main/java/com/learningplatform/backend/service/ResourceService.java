package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.ResourceRequest;
import com.learningplatform.backend.dto.ResourceResponse;
import java.util.List;

public interface ResourceService {

    List<ResourceResponse> getPublishedResources();

    List<ResourceResponse> getPublishedResourcesByTopic(Long topicId);

    ResourceResponse getResourceById(Long id);

    ResourceResponse createResource(ResourceRequest request);

    ResourceResponse updateResource(Long id, ResourceRequest request);

    void deleteResource(Long id);
}