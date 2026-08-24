package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.PagedResponse;
import com.learningplatform.backend.dto.ResourceRequest;
import com.learningplatform.backend.dto.ResourceResponse;
import com.learningplatform.backend.entity.Resource;
import com.learningplatform.backend.entity.Topic;
import com.learningplatform.backend.enums.ResourceStatus;
import com.learningplatform.backend.enums.ResourceType;
import com.learningplatform.backend.exception.ConflictException;
import com.learningplatform.backend.exception.ResourceNotFoundException;
import com.learningplatform.backend.exception.TopicNotFoundException;
import com.learningplatform.backend.repository.ResourceRepository;
import com.learningplatform.backend.repository.TopicRepository;
import com.learningplatform.backend.service.ResourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final TopicRepository topicRepository;

    public ResourceServiceImpl(ResourceRepository resourceRepository,
                               TopicRepository topicRepository) {
        this.resourceRepository = resourceRepository;
        this.topicRepository = topicRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ResourceResponse> searchPublishedResources(
            Long topicId,
            ResourceType resourceType,
            String difficulty,
            String keyword,
            Pageable pageable) {

        Page<Resource> page = resourceRepository.search(
                ResourceStatus.PUBLISHED,
                topicId,
                resourceType,
                difficulty,
                (keyword != null && !keyword.isBlank()) ? keyword.trim() : null,
                pageable
        );

        return PagedResponse.from(page.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return mapToResponse(resource);
    }

    @Override
    public ResourceResponse createResource(ResourceRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new TopicNotFoundException(request.getTopicId()));

        Resource resource = new Resource();
        resource.setTopic(topic);
        mapRequestToEntity(request, resource);

        if (request.getStatus() == null) {
            resource.setStatus(ResourceStatus.DRAFT);
        }

        Resource saved = resourceRepository.save(resource);
        return mapToResponse(saved);
    }

    @Override
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        if (!resource.getTopic().getId().equals(request.getTopicId())) {
            Topic newTopic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new TopicNotFoundException(request.getTopicId()));
            resource.setTopic(newTopic);
        }

        mapRequestToEntity(request, resource);
        return mapToResponse(resourceRepository.save(resource));
    }

    @Override
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        resourceRepository.deleteById(id);
    }

    private void mapRequestToEntity(ResourceRequest request, Resource resource) {
        resource.setTitle(request.getTitle().trim());
        resource.setDescription(request.getDescription());
        resource.setResourceType(request.getResourceType());
        resource.setExternalUrl(request.getExternalUrl().trim());
        resource.setThumbnailUrl(request.getThumbnailUrl());
        resource.setAuthor(request.getAuthor());
        resource.setPublisher(request.getPublisher());
        resource.setSourceName(request.getSourceName());
        resource.setDifficulty(request.getDifficulty());
        resource.setDurationMinutes(request.getDurationMinutes());
        resource.setLanguage(request.getLanguage() != null ? request.getLanguage() : "en");
        resource.setLicenseType(request.getLicenseType());
        resource.setLicenseUrl(request.getLicenseUrl());
        resource.setExternalResourceId(request.getExternalResourceId());
        resource.setDoi(request.getDoi());
        resource.setPublicationDate(request.getPublicationDate());
        if (request.getStatus() != null) {
            resource.setStatus(request.getStatus());
        }
    }

    private ResourceResponse mapToResponse(Resource resource) {
        ResourceResponse r = new ResourceResponse();
        r.setId(resource.getId());
        r.setTopicId(resource.getTopic().getId());
        r.setTitle(resource.getTitle());
        r.setDescription(resource.getDescription());
        r.setResourceType(resource.getResourceType());
        r.setExternalUrl(resource.getExternalUrl());
        r.setThumbnailUrl(resource.getThumbnailUrl());
        r.setAuthor(resource.getAuthor());
        r.setPublisher(resource.getPublisher());
        r.setSourceName(resource.getSourceName());
        r.setDifficulty(resource.getDifficulty());
        r.setDurationMinutes(resource.getDurationMinutes());
        r.setLanguage(resource.getLanguage());
        r.setLicenseType(resource.getLicenseType());
        r.setLicenseUrl(resource.getLicenseUrl());
        r.setExternalResourceId(resource.getExternalResourceId());
        r.setDoi(resource.getDoi());
        r.setPublicationDate(resource.getPublicationDate());
        r.setLastVerifiedAt(resource.getLastVerifiedAt());
        r.setStatus(resource.getStatus());
        r.setCreatedAt(resource.getCreatedAt());
        r.setUpdatedAt(resource.getUpdatedAt());
        return r;
    }
}