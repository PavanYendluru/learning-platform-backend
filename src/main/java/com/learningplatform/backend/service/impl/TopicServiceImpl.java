package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.TopicRequest;
import com.learningplatform.backend.dto.TopicResponse;
import com.learningplatform.backend.entity.Domain;
import com.learningplatform.backend.entity.Topic;
import com.learningplatform.backend.enums.Status;
import com.learningplatform.backend.exception.ConflictException;
import com.learningplatform.backend.exception.DomainNotFoundException;
import com.learningplatform.backend.exception.TopicNotFoundException;
import com.learningplatform.backend.repository.DomainRepository;
import com.learningplatform.backend.repository.TopicRepository;
import com.learningplatform.backend.service.TopicService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final DomainRepository domainRepository;

    public TopicServiceImpl(
            TopicRepository topicRepository,
            DomainRepository domainRepository
    ) {
        this.topicRepository = topicRepository;
        this.domainRepository = domainRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicResponse> getTopicsByDomain(Long domainId) {

        // Verify domain exists
        if (!domainRepository.existsById(domainId)) {
            throw new DomainNotFoundException(domainId);
        }

        return topicRepository
                .findByDomainIdAndStatusOrderBySequenceNumberAsc(
                        domainId,
                        Status.ACTIVE
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponse getTopicById(Long id) {

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
            throw new ConflictException(
                    "Topic with this name already exists in the domain"
            );
        }

        Topic topic = new Topic();

        topic.setDomain(domain);
        topic.setName(request.getName().trim());
        topic.setSlug(slug);
        topic.setDescription(request.getDescription());
        topic.setSequenceNumber(
                request.getSequenceNumber() != null
                        ? request.getSequenceNumber()
                        : 1
        );
        topic.setDifficulty(request.getDifficulty());
        topic.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : Status.ACTIVE
        );

        Topic saved = topicRepository.save(topic);

        return mapToResponse(saved);
    }

    @Override
    public TopicResponse updateTopic(Long id, TopicRequest request) {

        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException(id));

        String newSlug = generateSlug(request.getName());

        // Check slug uniqueness only if it changed
        if (!topic.getSlug().equalsIgnoreCase(newSlug)
                && topicRepository.existsByDomainIdAndSlugIgnoreCase(
                        topic.getDomain().getId(),
                        newSlug
                )) {

            throw new ConflictException(
                    "Topic with this name already exists in the domain"
            );
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

        topicRepository.deleteById(id);
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