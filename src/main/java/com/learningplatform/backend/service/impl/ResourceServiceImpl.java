package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.PagedResponse;
import com.learningplatform.backend.dto.ResourceRequest;
import com.learningplatform.backend.dto.ResourceResponse;
import com.learningplatform.backend.dto.ResourceVerificationRequest;
import com.learningplatform.backend.entity.Resource;
import com.learningplatform.backend.entity.Topic;
import com.learningplatform.backend.enums.ResourceStatus;
import com.learningplatform.backend.enums.ResourceType;
import com.learningplatform.backend.enums.Status;
import com.learningplatform.backend.enums.YouTubeContentType;
import com.learningplatform.backend.exception.BadRequestException;
import com.learningplatform.backend.exception.ResourceNotFoundException;
import com.learningplatform.backend.exception.TopicNotFoundException;
import com.learningplatform.backend.repository.BookmarkRepository;
import com.learningplatform.backend.repository.ResourceRepository;
import com.learningplatform.backend.repository.TopicRepository;
import com.learningplatform.backend.repository.UserResourceProgressRepository;
import com.learningplatform.backend.service.ResourceService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@Transactional
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final TopicRepository topicRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserResourceProgressRepository userResourceProgressRepository;

    public ResourceServiceImpl(ResourceRepository resourceRepository,
                               TopicRepository topicRepository,
                               BookmarkRepository bookmarkRepository,
                               UserResourceProgressRepository userResourceProgressRepository) {
        this.resourceRepository = resourceRepository;
        this.topicRepository = topicRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.userResourceProgressRepository = userResourceProgressRepository;
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
                (keyword != null && !keyword.isBlank())
                        ? keyword.trim()
                        : null,
                true,
                pageable
        );

        return PagedResponse.from(page.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        if (resource.getStatus() != ResourceStatus.PUBLISHED
                || resource.getTopic().getStatus() != Status.ACTIVE
                || resource.getTopic().getDomain().getStatus() != Status.ACTIVE) {
            throw new ResourceNotFoundException(id);
        }

        return mapToResponse(resource);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceResponse getAdminResourceById(Long id) {
        return mapToResponse(getResourceEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ResourceResponse> searchResources(
            ResourceStatus status,
            Long topicId,
            ResourceType resourceType,
            String difficulty,
            String keyword,
            Pageable pageable) {

        Page<Resource> page = resourceRepository.search(
                status,
                topicId,
                resourceType,
                difficulty,
                (keyword != null && !keyword.isBlank()) ? keyword.trim() : null,
                false,
                pageable
        );

        return PagedResponse.from(page.map(this::mapToResponse));
    }

    @Override
    public ResourceResponse submitForReview(Long id) {

        Resource resource = getResourceEntity(id);

        if (resource.getStatus() != ResourceStatus.DRAFT
                && resource.getStatus() != ResourceStatus.REJECTED) {

            throw new BadRequestException(
                    "Only DRAFT or REJECTED resources can be submitted for review"
            );
        }

        resource.setStatus(ResourceStatus.PENDING_REVIEW);

        return mapToResponse(resourceRepository.save(resource));
    }

    @Override
    public ResourceResponse approve(Long id) {

        Resource resource = getResourceEntity(id);

        if (resource.getStatus() != ResourceStatus.PENDING_REVIEW) {

            throw new BadRequestException(
                    "Only PENDING_REVIEW resources can be approved"
            );
        }

        resource.setStatus(ResourceStatus.PUBLISHED);

        return mapToResponse(resourceRepository.save(resource));
    }

    @Override
    public ResourceResponse reject(Long id) {

        Resource resource = getResourceEntity(id);

        if (resource.getStatus() != ResourceStatus.PENDING_REVIEW) {

            throw new BadRequestException(
                    "Only PENDING_REVIEW resources can be rejected"
            );
        }

        resource.setStatus(ResourceStatus.REJECTED);

        return mapToResponse(resourceRepository.save(resource));
    }

    @Override
    public ResourceResponse updateVerification(
            Long id,
            ResourceVerificationRequest request) {

        Resource resource = getResourceEntity(id);

        resource.setVerificationStatus(request.getVerificationStatus());
        resource.setLastVerifiedAt(LocalDateTime.now());

        return mapToResponse(resourceRepository.save(resource));
    }

    private Resource getResourceEntity(Long id) {

        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    public ResourceResponse createResource(ResourceRequest request) {

        validateRequest(request);

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() ->
                        new TopicNotFoundException(request.getTopicId()));

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
    public ResourceResponse updateResource(
            Long id,
            ResourceRequest request) {

        validateRequest(request);

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        if (!resource.getTopic().getId().equals(request.getTopicId())) {

            Topic newTopic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() ->
                            new TopicNotFoundException(request.getTopicId()));

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

        bookmarkRepository.deleteByResourceId(id);
        userResourceProgressRepository.deleteByResourceId(id);
        resourceRepository.deleteById(id);
    }

    private void mapRequestToEntity(
            ResourceRequest request,
            Resource resource) {

        resource.setTitle(request.getTitle().trim());

        resource.setDescription(request.getDescription());

        resource.setResourceType(request.getResourceType());

        String externalUrl = request.getExternalUrl().trim();
        resource.setExternalUrl(externalUrl);

        if (request.getResourceType() == ResourceType.VIDEO) {
            validateYouTubeUrl(externalUrl, request.getYoutubeContentType());
            resource.setYoutubeContentType(request.getYoutubeContentType());
        } else {
            resource.setYoutubeContentType(null);
        }
        if (request.getSequenceNumber() != null) {
            resource.setSequenceNumber(request.getSequenceNumber());
        } else if (resource.getSequenceNumber() == null) {
            resource.setSequenceNumber(1);
        }

        resource.setThumbnailUrl(request.getThumbnailUrl());

        resource.setAuthor(request.getAuthor());

        resource.setPublisher(request.getPublisher());

        resource.setSourceName(request.getSourceName());

        resource.setDifficulty(request.getDifficulty());

        resource.setDurationMinutes(request.getDurationMinutes());

        resource.setLanguage(
                request.getLanguage() != null
                        ? request.getLanguage()
                        : "en"
        );

        resource.setLicenseType(request.getLicenseType());

        resource.setLicenseUrl(request.getLicenseUrl());

        resource.setExternalResourceId(
                request.getExternalResourceId()
        );

        resource.setDoi(request.getDoi());

        resource.setPublicationDate(
                request.getPublicationDate()
        );

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
        r.setVerificationStatus(resource.getVerificationStatus());

        r.setExternalUrl(resource.getExternalUrl());

        r.setYoutubeContentType(resource.getYoutubeContentType());
        r.setSequenceNumber(resource.getSequenceNumber());

        r.setThumbnailUrl(resource.getThumbnailUrl());

        r.setAuthor(resource.getAuthor());

        r.setPublisher(resource.getPublisher());

        r.setSourceName(resource.getSourceName());

        r.setDifficulty(resource.getDifficulty());

        r.setDurationMinutes(resource.getDurationMinutes());

        r.setLanguage(resource.getLanguage());

        r.setLicenseType(resource.getLicenseType());

        r.setLicenseUrl(resource.getLicenseUrl());

        r.setExternalResourceId(
                resource.getExternalResourceId()
        );

        r.setDoi(resource.getDoi());

        r.setPublicationDate(
                resource.getPublicationDate()
        );

        r.setLastVerifiedAt(
                resource.getLastVerifiedAt()
        );

        r.setStatus(
                resource.getStatus()
        );

        r.setCreatedAt(
                resource.getCreatedAt()
        );

        r.setUpdatedAt(
                resource.getUpdatedAt()
        );

        return r;
    }

    private void validateYouTubeUrl(String url, YouTubeContentType contentType) {
        if (contentType == null) {
            return;
        }

        URI uri;
        try {
            uri = new URI(url.trim());
        } catch (URISyntaxException ex) {
            throw new BadRequestException("A valid YouTube URL is required");
        }

        String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase();
        boolean youtubeHost = host.equals("youtube.com")
                || host.equals("www.youtube.com")
                || host.equals("m.youtube.com")
                || host.equals("music.youtube.com")
                || host.equals("youtu.be");
        if (!youtubeHost || !"https".equalsIgnoreCase(uri.getScheme())) {
            throw new BadRequestException("URL must be a valid HTTPS YouTube video or playlist URL");
        }

        String path = uri.getPath() == null ? "" : uri.getPath().toLowerCase();
        boolean video = (host.equals("youtu.be") && path.length() > 1)
                || (path.equals("/watch") && hasQueryParameter(uri, "v"))
                || hasPathValue(path, "/embed/")
                || hasPathValue(path, "/shorts/");
        boolean playlist = (path.equals("/playlist") || path.equals("/watch"))
                && hasQueryParameter(uri, "list");
        if (contentType == YouTubeContentType.VIDEO && !video) {
            throw new BadRequestException("URL must be a valid YouTube video URL");
        }
        if (contentType == YouTubeContentType.PLAYLIST && !playlist) {
            throw new BadRequestException("URL must be a valid YouTube playlist URL");
        }
    }

    private boolean hasQueryParameter(URI uri, String expectedName) {
        String rawQuery = uri.getRawQuery();
        if (rawQuery == null || rawQuery.isBlank()) {
            return false;
        }

        for (String parameter : rawQuery.split("&")) {
            String[] pair = parameter.split("=", 2);
            try {
                String name = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                if (expectedName.equalsIgnoreCase(name)
                        && pair.length == 2
                        && !URLDecoder.decode(pair[1], StandardCharsets.UTF_8).isBlank()) {
                    return true;
                }
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }
        return false;
    }

    private boolean hasPathValue(String path, String prefix) {
        return path.startsWith(prefix) && path.length() > prefix.length();
    }

    private void validateRequest(ResourceRequest request) {
        if (request == null) {
            throw new BadRequestException("Resource request is required");
        }
        if (request.getTopicId() == null) {
            throw new BadRequestException("Topic ID is required");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BadRequestException("Title is required");
        }
        if (request.getResourceType() == null) {
            throw new BadRequestException("Resource type is required");
        }
        if (request.getExternalUrl() == null || request.getExternalUrl().isBlank()) {
            throw new BadRequestException("External URL is required");
        }
        if (request.getResourceType() != ResourceType.VIDEO) {
            request.setYoutubeContentType(null);
        }
        if (request.getYoutubeContentType() != null
                && request.getResourceType() != ResourceType.VIDEO) {
            throw new BadRequestException("YouTube resources must use resourceType VIDEO");
        }
        if (request.getSequenceNumber() != null && request.getSequenceNumber() < 1) {
            throw new BadRequestException("Sequence number must be at least 1");
        }
    }
}
