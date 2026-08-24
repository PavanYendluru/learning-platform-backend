package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.*;
import com.learningplatform.backend.entity.*;
import com.learningplatform.backend.enums.RoadmapStatus;
import com.learningplatform.backend.exception.DomainNotFoundException;
import com.learningplatform.backend.exception.ResourceNotFoundException;
import com.learningplatform.backend.repository.*;
import com.learningplatform.backend.service.RoadmapService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoadmapServiceImpl implements RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final DomainRepository domainRepository;
    private final TopicRepository topicRepository;

    public RoadmapServiceImpl(RoadmapRepository roadmapRepository,
                              DomainRepository domainRepository,
                              TopicRepository topicRepository) {
        this.roadmapRepository = roadmapRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException(id)); // reusing exception
        return mapToResponse(roadmap);
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
                .orElseThrow(() -> new ResourceNotFoundException(id));

        // Clear old steps
        roadmap.getSteps().clear();
        mapRequestToEntity(request, roadmap);

        return mapToResponse(roadmapRepository.save(roadmap));
    }

    @Override
    public void deleteRoadmap(Long id) {
        if (!roadmapRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        roadmapRepository.deleteById(id);
    }

    private void mapRequestToEntity(RoadmapRequest request, Roadmap roadmap) {
        roadmap.setTitle(request.getTitle().trim());
        roadmap.setDescription(request.getDescription());
        roadmap.setStatus(request.getStatus() != null ? request.getStatus() : RoadmapStatus.DRAFT);

        if (request.getDomainId() != null) {
            Domain domain = domainRepository.findById(request.getDomainId())
                    .orElseThrow(() -> new DomainNotFoundException(request.getDomainId()));
            roadmap.setDomain(domain);
        } else {
            roadmap.setDomain(null);
        }

        if (request.getSteps() != null) {
            for (RoadmapStepRequest stepReq : request.getSteps()) {
                RoadmapStep step = new RoadmapStep();
                step.setRoadmap(roadmap);
                step.setTitle(stepReq.getTitle().trim());
                step.setDescription(stepReq.getDescription());
                step.setStepOrder(stepReq.getStepOrder() != null ? stepReq.getStepOrder() : 1);

                if (stepReq.getTopicId() != null) {
                    Topic topic = topicRepository.findById(stepReq.getTopicId())
                            .orElseThrow(() -> new RuntimeException("Topic not found: " + stepReq.getTopicId()));
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

        List<RoadmapStepResponse> stepResponses = roadmap.getSteps().stream().map(step -> {
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
        }).toList();

        r.setSteps(stepResponses);
        return r;
    }
}