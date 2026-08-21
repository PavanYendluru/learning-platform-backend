package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.TopicRequest;
import com.learningplatform.backend.dto.TopicResponse;
import java.util.List;

public interface TopicService {

    List<TopicResponse> getTopicsByDomain(Long domainId);

    TopicResponse getTopicById(Long id);

    TopicResponse createTopic(Long domainId, TopicRequest request);

    TopicResponse updateTopic(Long id, TopicRequest request);

    void deleteTopic(Long id);
}