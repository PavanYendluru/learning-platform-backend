package com.learningplatform.backend.service;

import org.springframework.data.domain.Pageable;

import com.learningplatform.backend.dto.PagedResponse;
import com.learningplatform.backend.dto.ResourceRequest;
import com.learningplatform.backend.dto.ResourceResponse;
import com.learningplatform.backend.dto.ResourceVerificationRequest;
import com.learningplatform.backend.enums.ResourceType;
import com.learningplatform.backend.enums.ResourceStatus;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ResourceService {

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    PagedResponse<ResourceResponse> searchPublishedResources(
            Long topicId,
            ResourceType resourceType,
            String difficulty,
            String keyword,
            Pageable pageable
    );

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    ResourceResponse getResourceById(Long id);

    @PreAuthorize("hasRole('ADMIN')")
    PagedResponse<ResourceResponse> searchResources(
            ResourceStatus status,
            Long topicId,
            ResourceType resourceType,
            String difficulty,
            String keyword,
            Pageable pageable
    );

    @PreAuthorize("hasRole('ADMIN')")
    ResourceResponse getAdminResourceById(Long id);

    @PreAuthorize("hasRole('ADMIN')")
    ResourceResponse createResource(ResourceRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    ResourceResponse updateResource(Long id, ResourceRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    ResourceResponse submitForReview(Long id);
    @PreAuthorize("hasRole('ADMIN')")
    ResourceResponse approve(Long id);
    @PreAuthorize("hasRole('ADMIN')")
    ResourceResponse reject(Long id);
    @PreAuthorize("hasRole('ADMIN')")
    ResourceResponse updateVerification(Long id, ResourceVerificationRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    void deleteResource(Long id);
}
