package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.PagedResponse;
import com.learningplatform.backend.dto.ResourceRequest;
import com.learningplatform.backend.dto.ResourceResponse;
import com.learningplatform.backend.enums.ResourceType;
import org.springframework.data.domain.Pageable;

public interface ResourceService {

    PagedResponse<ResourceResponse> searchPublishedResources(
            Long topicId,
            ResourceType resourceType,
            String difficulty,
            String keyword,
            Pageable pageable
    );

    ResourceResponse getResourceById(Long id);

    ResourceResponse createResource(ResourceRequest request);

    ResourceResponse updateResource(Long id, ResourceRequest request);

    void deleteResource(Long id);
}