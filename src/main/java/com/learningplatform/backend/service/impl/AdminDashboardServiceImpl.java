package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.AdminDashboardStatsResponse;
import com.learningplatform.backend.enums.ResourceStatus;
import com.learningplatform.backend.repository.*;
import com.learningplatform.backend.service.AdminDashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final DomainRepository domainRepository;
    private final TopicRepository topicRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final RoadmapRepository roadmapRepository;
    private final TechnologyUpdateRepository technologyUpdateRepository;

    public AdminDashboardServiceImpl(DomainRepository domainRepository,
                                     TopicRepository topicRepository,
                                     ResourceRepository resourceRepository,
                                     UserRepository userRepository,
                                     RoadmapRepository roadmapRepository,
                                     TechnologyUpdateRepository technologyUpdateRepository) {
        this.domainRepository = domainRepository;
        this.topicRepository = topicRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
        this.roadmapRepository = roadmapRepository;
        this.technologyUpdateRepository = technologyUpdateRepository;
    }

    @Override
    public AdminDashboardStatsResponse getStats() {
        AdminDashboardStatsResponse stats = new AdminDashboardStatsResponse();
        stats.setTotalDomains(domainRepository.count());
        stats.setTotalTopics(topicRepository.count());
        stats.setTotalResources(resourceRepository.count());
        stats.setPublishedResources(resourceRepository.countByStatus(ResourceStatus.PUBLISHED));
        stats.setPendingReviewResources(resourceRepository.countByStatus(ResourceStatus.PENDING_REVIEW));
        stats.setTotalUsers(userRepository.count());
        stats.setTotalRoadmaps(roadmapRepository.count());
        stats.setTotalTechnologyUpdates(technologyUpdateRepository.count());
        return stats;
    }
}