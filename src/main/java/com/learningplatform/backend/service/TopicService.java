package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.TopicRequest;
import com.learningplatform.backend.dto.TopicResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

public interface TopicService {

    @PreAuthorize("isAuthenticated()")
    List<TopicResponse> getTopicsByDomain(Long domainId);

    @PreAuthorize("hasRole('ADMIN')")
    List<TopicResponse> getTopicsByDomainForAdmin(Long domainId);

    @PreAuthorize("hasRole('ADMIN')")
    List<TopicResponse> getAllTopicsForAdmin();

    @PreAuthorize("isAuthenticated()")
    TopicResponse getTopicById(Long id);

    @PreAuthorize("hasRole('ADMIN')")
    TopicResponse getTopicByIdForAdmin(Long id);

    @PreAuthorize("hasRole('ADMIN')")
    TopicResponse createTopic(Long domainId, TopicRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    TopicResponse updateTopic(Long id, TopicRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    void deleteTopic(Long id);
}
