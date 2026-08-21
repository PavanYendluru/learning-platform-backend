package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.ResourceRequest;
import com.learningplatform.backend.dto.ResourceResponse;
import com.learningplatform.backend.entity.Resource;
import com.learningplatform.backend.entity.Topic;
import com.learningplatform.backend.enums.ResourceStatus;
import com.learningplatform.backend.repository.ResourceRepository;
import com.learningplatform.backend.repository.TopicRepository;
import com.learningplatform.backend.service.ResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final TopicRepository topicRepository;

    public ResourceServiceImpl(ResourceRepository resourceRepository, TopicRepository topicRepository) {
        this.resourceRepository = resourceRepository;
        this.topicRepository = topicRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponse> getPublishedResources() {
        return resourceRepository.findByStatusOrderByCreatedAtDesc(ResourceStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponse> getPublishedResourcesByTopic(Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new RuntimeException("Topic not found with id: " + topicId);
        }
        return resourceRepository.findByTopicIdAndStatusOrderByCreatedAtDesc(topicId, ResourceStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
        return mapToResponse(resource);
    }

    @Override
    public ResourceResponse createResource(ResourceRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + request.getTopicId()));

        Resource resource = new Resource();
        resource.setTopic(topic);
        mapRequestToEntity(request, resource);

        // Default status is DRAFT if not provided
        if (request.getStatus() == null) {
            resource.setStatus(ResourceStatus.DRAFT);
        }

        Resource saved = resourceRepository.save(resource);
        return mapToResponse(saved);
    }

    @Override
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));

        // Allow changing the topic
        if (!resource.getTopic().getId().equals(request.getTopicId())) {
            Topic newTopic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Topic not found with id: " + request.getTopicId()));
            resource.setTopic(newTopic);
        }

        mapRequestToEntity(request, resource);
        Resource updated = resourceRepository.save(resource);
        return mapToResponse(updated);
    }

    @Override
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new RuntimeException("Resource not found with id: " + id);
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
        ResourceResponse response = new ResourceResponse();
        response.setId(resource.getId());
        response.setTopicId(resource.getTopic().getId());
        response.setTitle(resource.getTitle());
        response.setDescription(resource.getDescription());
        response.setResourceType(resource.getResourceType());
        response.setExternalUrl(resource.getExternalUrl());
        response.setThumbnailUrl(resource.getThumbnailUrl());
        response.setAuthor(resource.getAuthor());
        response.setPublisher(resource.getPublisher());
        response.setSourceName(resource.getSourceName());
        response.setDifficulty(resource.getDifficulty());
        response.setDurationMinutes(resource.getDurationMinutes());
        response.setLanguage(resource.getLanguage());
        response.setLicenseType(resource.getLicenseType());
        response.setLicenseUrl(resource.getLicenseUrl());
        response.setExternalResourceId(resource.getExternalResourceId());
        response.setDoi(resource.getDoi());
        response.setPublicationDate(resource.getPublicationDate());
        response.setLastVerifiedAt(resource.getLastVerifiedAt());
        response.setStatus(resource.getStatus());
        response.setCreatedAt(resource.getCreatedAt());
        response.setUpdatedAt(resource.getUpdatedAt());
        return response;
    }
}