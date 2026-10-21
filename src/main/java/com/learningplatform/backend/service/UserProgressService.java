package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.DashboardDataResponse;
import com.learningplatform.backend.dto.DomainProgressResponse;
import com.learningplatform.backend.dto.UserProgressRequest;
import com.learningplatform.backend.dto.UserProgressResponse;
import java.util.List;

public interface UserProgressService {
    UserProgressResponse saveProgress(Long topicId, UserProgressRequest request);
    List<UserProgressResponse> getMyProgress();
    UserProgressResponse completeResource(Long resourceId);
    UserProgressResponse uncompleteResource(Long resourceId);
    List<Long> getCompletedResourceIds();
    DomainProgressResponse getDomainProgress(Long domainId);
    DashboardDataResponse getDashboardData();
}
