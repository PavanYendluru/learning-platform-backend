package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.TechnologyUpdateRequest;
import com.learningplatform.backend.dto.TechnologyUpdateResponse;
import java.util.List;

public interface TechnologyUpdateService {
    List<TechnologyUpdateResponse> getPublishedUpdates();
    List<TechnologyUpdateResponse> getPublishedUpdatesByDomain(Long domainId);
    TechnologyUpdateResponse getById(Long id);
    TechnologyUpdateResponse create(TechnologyUpdateRequest request);
    TechnologyUpdateResponse update(Long id, TechnologyUpdateRequest request);
    void delete(Long id);
}