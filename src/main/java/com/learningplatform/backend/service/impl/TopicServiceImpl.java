package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.TopicRequest;
import com.learningplatform.backend.dto.TopicResponse;
import com.learningplatform.backend.entity.Domain;
import com.learningplatform.backend.entity.Resource;
import com.learningplatform.backend.entity.Topic;
import com.learningplatform.backend.enums.Status;
import com.learningplatform.backend.exception.ConflictException;
import com.learningplatform.backend.exception.DomainNotFoundException;
import com.learningplatform.backend.exception.TopicNotFoundException;
import com.learningplatform.backend.repository.BookmarkRepository;
import com.learningplatform.backend.repository.DomainRepository;
import com.learningplatform.backend.repository.ResourceRepository;
import com.learningplatform.backend.repository.RoadmapStepRepository;
import com.learningplatform.backend.repository.TopicRepository;
import com.learningplatform.backend.repository.UserProgressRepository;
import com.learningplatform.backend.repository.UserResourceProgressRepository;
import com.learningplatform.backend.service.TopicService;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final DomainRepository domainRepository;
    private final RoadmapStepRepository roadmapStepRepository;
    private final ResourceRepository resourceRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserProgressRepository userProgressRepository;
    private final UserResourceProgressRepository userResourceProgressRepository;

    public TopicServiceImpl(
            TopicRepository topicRepository,
            DomainRepository domainRepository,
            RoadmapStepRepository roadmapStepRepository,
            ResourceRepository resourceRepository,
            BookmarkRepository bookmarkRepository,
            UserProgressRepository userProgressRepository,
            UserResourceProgressRepository userResourceProgressRepository
    ) {
        this.topicRepository = topicRepository;
        this.domainRepository = domainRepository;
        this.roadmapStepRepository = roadmapStepRepository;
        this.resourceRepository = resourceRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.userProgressRepository = userProgressRepository;
        this.userResourceProgressRepository = userResourceProgressRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicResponse> getTopicsByDomain(Long domainId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new DomainNotFoundException(domainId));

        if (domain.getStatus() != Status.ACTIVE) {
            throw new DomainNotFoundException(domainId);
        }

        return topicRepository
                .findByDomainIdAndStatusOrderBySequenceNumberAsc(domainId, Status.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicResponse> getTopicsByDomainForAdmin(Long domainId) {
        if (!domainRepository.existsById(domainId)) {
            throw new DomainNotFoundException(domainId);
        }

        return topicRepository.findByDomainIdOrderBySequenceNumberAsc(domainId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicResponse> getAllTopicsForAdmin() {
        return topicRepository.findAll(Sort.by("domain.id").ascending().and(Sort.by("sequenceNumber").ascending()))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponse getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException(id));

        if (topic.getStatus() != Status.ACTIVE || topic.getDomain().getStatus() != Status.ACTIVE) {
            throw new TopicNotFoundException(id);
        }

        return mapToResponse(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponse getTopicByIdForAdmin(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException(id));

        return mapToResponse(topic);
    }

    @Override
    public TopicResponse createTopic(Long domainId, TopicRequest request) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new DomainNotFoundException(domainId));

        String slug = generateSlug(request.getName());

        if (topicRepository.existsByDomainIdAndSlugIgnoreCase(domainId, slug)) {
            throw new ConflictException("Topic with this name already exists in the domain");
        }

        Topic topic = new Topic();
        topic.setDomain(domain);
        topic.setName(request.getName().trim());
        topic.setSlug(slug);
        topic.setDescription(request.getDescription());
        topic.setSequenceNumber(request.getSequenceNumber() != null ? request.getSequenceNumber() : 1);
        topic.setDifficulty(request.getDifficulty());
        topic.setStatus(request.getStatus() != null ? request.getStatus() : Status.ACTIVE);

        Topic saved = topicRepository.save(topic);
        return mapToResponse(saved);
    }

    @Override
    public TopicResponse updateTopic(Long id, TopicRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException(id));

        String newSlug = generateSlug(request.getName());

        if (!topic.getSlug().equalsIgnoreCase(newSlug)
                && topicRepository.existsByDomainIdAndSlugIgnoreCase(topic.getDomain().getId(), newSlug)) {
            throw new ConflictException("Topic with this name already exists in the domain");
        }

        topic.setName(request.getName().trim());
        topic.setSlug(newSlug);
        topic.setDescription(request.getDescription());

        if (request.getSequenceNumber() != null) {
            topic.setSequenceNumber(request.getSequenceNumber());
        }

        topic.setDifficulty(request.getDifficulty());

        if (request.getStatus() != null) {
            topic.setStatus(request.getStatus());
        }

        Topic updated = topicRepository.save(topic);
        return mapToResponse(updated);
    }

    @Override
    public void deleteTopic(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new TopicNotFoundException(id);
        }

        // Data Integrity Rule #9: Remove roadmap step references before deleting topic
        roadmapStepRepository.deleteByTopicId(id);

        // Remove user progress references for this topic
        userProgressRepository.deleteByTopicId(id);

        // Remove all resources under this topic along with bookmarks and resource progress
        List<Resource> resources = resourceRepository.findByTopicId(id);
        for (Resource res : resources) {
            bookmarkRepository.deleteByResourceId(res.getId());
            userResourceProgressRepository.deleteByResourceId(res.getId());
            resourceRepository.delete(res);
        }

        topicRepository.deleteById(id);
    }

    @PostConstruct
    public void cleanupPredefinedFallbackTopics() {
        // Automatically purge predefined mock topics if they exist in DB
        List<Long> legacyIds = List.of(101L, 102L);
        for (Long legacyId : legacyIds) {
            if (topicRepository.existsById(legacyId)) {
                try {
                    deleteTopic(legacyId);
                } catch (Exception ignored) {
                }
            }
        }
        for (Topic t : topicRepository.findAll()) {
            if ("Spring Core & Dependency Injection".equalsIgnoreCase(t.getName())
                    || "Spring Security & JWT".equalsIgnoreCase(t.getName())
                    || "spring-core".equalsIgnoreCase(t.getSlug())
                    || "spring-security".equalsIgnoreCase(t.getSlug())) {
                try {
                    deleteTopic(t.getId());
                } catch (Exception ignored) {
                }
            }
        }
    }

    private String generateSlug(String name) {
        return name.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    private TopicResponse mapToResponse(Topic topic) {
        TopicResponse response = new TopicResponse();
        response.setId(topic.getId());
        response.setDomainId(topic.getDomain().getId());
        response.setName(topic.getName());
        response.setSlug(topic.getSlug());
        response.setDescription(topic.getDescription());
        response.setSequenceNumber(topic.getSequenceNumber());
        response.setDifficulty(topic.getDifficulty());
        response.setStatus(topic.getStatus());
        response.setCreatedAt(topic.getCreatedAt());
        response.setUpdatedAt(topic.getUpdatedAt());
        return response;
    }
}
