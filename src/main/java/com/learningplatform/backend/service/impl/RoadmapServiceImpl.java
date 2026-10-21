package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.*;
import com.learningplatform.backend.entity.*;
import com.learningplatform.backend.enums.RoadmapStatus;
import com.learningplatform.backend.enums.Status;
import com.learningplatform.backend.exception.BadRequestException;
import com.learningplatform.backend.exception.DomainNotFoundException;
import com.learningplatform.backend.exception.ResourceNotFoundException;
import com.learningplatform.backend.exception.TopicNotFoundException;
import com.learningplatform.backend.repository.*;
import com.learningplatform.backend.service.RoadmapService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class RoadmapServiceImpl implements RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapStepRepository roadmapStepRepository;
    private final DomainRepository domainRepository;
    private final TopicRepository topicRepository;

    public RoadmapServiceImpl(RoadmapRepository roadmapRepository,
                              RoadmapStepRepository roadmapStepRepository,
                              DomainRepository domainRepository,
                              TopicRepository topicRepository) {
        this.roadmapRepository = roadmapRepository;
        this.roadmapStepRepository = roadmapStepRepository;
        this.domainRepository = domainRepository;
        this.topicRepository = topicRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadmapResponse> getPublishedRoadmaps() {
        return roadmapRepository.findByStatusOrderByTitleAsc(RoadmapStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoadmapResponse getRoadmapById(Long id) {
        Roadmap roadmap = roadmapRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found with id: " + id));
        return mapToResponse(roadmap);
    }

    @Override
    @Transactional(readOnly = true)
    public RoadmapResponse getRoadmapByDomain(Long domainId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new DomainNotFoundException(domainId));

        if (domain.getStatus() != Status.ACTIVE) {
            throw new DomainNotFoundException(domainId);
        }

        Roadmap roadmap = roadmapRepository.findFirstByDomainIdAndStatus(domainId, RoadmapStatus.PUBLISHED)
                .orElseGet(() -> roadmapRepository.findFirstByDomainIdOrderByCreatedAtDesc(domainId)
                        .orElse(null));

        return roadmap != null ? mapToResponse(roadmap) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public RoadmapResponse getAdminRoadmapByDomain(Long domainId) {
        if (!domainRepository.existsById(domainId)) {
            throw new DomainNotFoundException(domainId);
        }

        Roadmap roadmap = roadmapRepository.findFirstByDomainIdOrderByCreatedAtDesc(domainId)
                .orElse(null);

        return roadmap != null ? mapToResponse(roadmap) : null;
    }

    @Override
    public RoadmapResponse createRoadmap(RoadmapRequest request) {
        Roadmap roadmap = new Roadmap();
        mapRequestToEntity(request, roadmap);
        Roadmap saved = roadmapRepository.save(roadmap);
        return mapToResponse(saved);
    }

    @Override
    public RoadmapResponse updateRoadmap(Long id, RoadmapRequest request) {
        Roadmap roadmap = roadmapRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found with id: " + id));

        // Clear existing steps
        roadmap.getSteps().clear();
        mapRequestToEntity(request, roadmap);

        return mapToResponse(roadmapRepository.save(roadmap));
    }

    @Override
    public void deleteRoadmap(Long id) {
        if (!roadmapRepository.existsById(id)) {
            throw new ResourceNotFoundException("Roadmap not found with id: " + id);
        }
        roadmapRepository.deleteById(id);
    }

    @Override
    public RoadmapStepResponse addStep(Long roadmapId, RoadmapStepRequest request) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found with id: " + roadmapId));

        RoadmapStep step = new RoadmapStep();
        step.setRoadmap(roadmap);
        step.setTitle(request.getTitle().trim());
        step.setDescription(request.getDescription());

        int order = request.getStepOrder() != null && request.getStepOrder() > 0
                ? request.getStepOrder()
                : roadmap.getSteps().size() + 1;
        step.setStepOrder(order);

        if (request.getTopicId() != null) {
            Topic topic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new TopicNotFoundException(request.getTopicId()));

            // Validation Rule #5: Roadmap.domainId == Topic.domainId
            if (roadmap.getDomain() != null && !topic.getDomain().getId().equals(roadmap.getDomain().getId())) {
                throw new BadRequestException("Topic '" + topic.getName() + "' belongs to domain '"
                        + topic.getDomain().getName() + "', not roadmap domain '"
                        + roadmap.getDomain().getName() + "'");
            }

            // Validation Rule #6: Prevent duplicate topics in roadmap steps
            boolean duplicate = roadmap.getSteps().stream()
                    .anyMatch(s -> s.getTopic() != null && s.getTopic().getId().equals(topic.getId()));
            if (duplicate) {
                throw new BadRequestException("Topic '" + topic.getName() + "' is already included in this roadmap.");
            }

            step.setTopic(topic);
        }

        roadmap.getSteps().add(step);
        RoadmapStep savedStep = roadmapStepRepository.save(step);
        return mapToStepResponse(savedStep);
    }

    @Override
    public RoadmapStepResponse updateStep(Long stepId, RoadmapStepRequest request) {
        RoadmapStep step = roadmapStepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap step not found with id: " + stepId));

        Roadmap roadmap = step.getRoadmap();

        step.setTitle(request.getTitle().trim());
        step.setDescription(request.getDescription());
        if (request.getStepOrder() != null && request.getStepOrder() > 0) {
            step.setStepOrder(request.getStepOrder());
        }

        if (request.getTopicId() != null) {
            Topic topic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new TopicNotFoundException(request.getTopicId()));

            if (roadmap.getDomain() != null && !topic.getDomain().getId().equals(roadmap.getDomain().getId())) {
                throw new BadRequestException("Topic '" + topic.getName() + "' belongs to domain '"
                        + topic.getDomain().getName() + "', not roadmap domain '"
                        + roadmap.getDomain().getName() + "'");
            }

            boolean duplicate = roadmap.getSteps().stream()
                    .anyMatch(s -> !s.getId().equals(stepId) && s.getTopic() != null && s.getTopic().getId().equals(topic.getId()));
            if (duplicate) {
                throw new BadRequestException("Topic '" + topic.getName() + "' is already included in this roadmap.");
            }

            step.setTopic(topic);
        } else {
            step.setTopic(null);
        }

        RoadmapStep updated = roadmapStepRepository.save(step);
        return mapToStepResponse(updated);
    }

    @Override
    public void deleteStep(Long stepId) {
        RoadmapStep step = roadmapStepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap step not found with id: " + stepId));

        Roadmap roadmap = step.getRoadmap();
        roadmap.getSteps().remove(step);
        roadmapStepRepository.delete(step);

        // Normalize step ordering
        int order = 1;
        for (RoadmapStep s : roadmap.getSteps()) {
            s.setStepOrder(order++);
        }
        roadmapRepository.save(roadmap);
    }

    @Override
    public RoadmapResponse reorderSteps(Long roadmapId, List<Long> stepIdsInOrder) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found with id: " + roadmapId));

        if (stepIdsInOrder == null || stepIdsInOrder.isEmpty()) {
            return mapToResponse(roadmap);
        }

        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < stepIdsInOrder.size(); i++) {
            orderMap.put(stepIdsInOrder.get(i), i + 1);
        }

        for (RoadmapStep step : roadmap.getSteps()) {
            if (orderMap.containsKey(step.getId())) {
                step.setStepOrder(orderMap.get(step.getId()));
            }
        }

        roadmap.getSteps().sort(Comparator.comparingInt(RoadmapStep::getStepOrder));
        Roadmap saved = roadmapRepository.save(roadmap);
        return mapToResponse(saved);
    }

    private void mapRequestToEntity(RoadmapRequest request, Roadmap roadmap) {
        roadmap.setTitle(request.getTitle().trim());
        roadmap.setDescription(request.getDescription());
        roadmap.setStatus(request.getStatus() != null ? request.getStatus() : RoadmapStatus.DRAFT);

        Domain domain = null;
        if (request.getDomainId() != null) {
            domain = domainRepository.findById(request.getDomainId())
                    .orElseThrow(() -> new DomainNotFoundException(request.getDomainId()));
            roadmap.setDomain(domain);
        } else {
            roadmap.setDomain(null);
        }

        if (request.getSteps() != null) {
            Set<Long> seenTopicIds = new HashSet<>();
            int currentOrder = 1;

            for (RoadmapStepRequest stepReq : request.getSteps()) {
                RoadmapStep step = new RoadmapStep();
                step.setRoadmap(roadmap);
                step.setTitle(stepReq.getTitle().trim());
                step.setDescription(stepReq.getDescription());
                step.setStepOrder(stepReq.getStepOrder() != null && stepReq.getStepOrder() > 0
                        ? stepReq.getStepOrder()
                        : currentOrder++);

                if (stepReq.getTopicId() != null) {
                    Topic topic = topicRepository.findById(stepReq.getTopicId())
                            .orElseThrow(() -> new TopicNotFoundException(stepReq.getTopicId()));

                    // Validation Rule #5: Cross-domain validation
                    if (domain != null && !topic.getDomain().getId().equals(domain.getId())) {
                        throw new BadRequestException("Topic '" + topic.getName() + "' does not belong to domain '"
                                + domain.getName() + "'");
                    }

                    // Validation Rule #6: Duplicate topic prevention
                    if (!seenTopicIds.add(topic.getId())) {
                        throw new BadRequestException("Topic '" + topic.getName() + "' is already included in this roadmap");
                    }

                    step.setTopic(topic);
                }
                roadmap.getSteps().add(step);
            }
        }
    }

    private RoadmapResponse mapToResponse(Roadmap roadmap) {
        RoadmapResponse r = new RoadmapResponse();
        r.setId(roadmap.getId());
        r.setTitle(roadmap.getTitle());
        r.setDescription(roadmap.getDescription());
        r.setStatus(roadmap.getStatus());
        r.setCreatedAt(roadmap.getCreatedAt());
        r.setUpdatedAt(roadmap.getUpdatedAt());

        if (roadmap.getDomain() != null) {
            r.setDomainId(roadmap.getDomain().getId());
            r.setDomainName(roadmap.getDomain().getName());
        }

        List<RoadmapStepResponse> stepResponses = roadmap.getSteps().stream().map(this::mapToStepResponse).toList();
        r.setSteps(stepResponses);
        return r;
    }

    private RoadmapStepResponse mapToStepResponse(RoadmapStep step) {
        RoadmapStepResponse s = new RoadmapStepResponse();
        s.setId(step.getId());
        s.setTitle(step.getTitle());
        s.setDescription(step.getDescription());
        s.setStepOrder(step.getStepOrder());
        if (step.getTopic() != null) {
            s.setTopicId(step.getTopic().getId());
            s.setTopicName(step.getTopic().getName());
        }
        return s;
    }
}
