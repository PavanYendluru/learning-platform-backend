package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.DomainRequest;
import com.learningplatform.backend.dto.DomainResponse;
import java.util.List;

/**
 * Business operations related to Domain.
 */
public interface DomainService {

    List<DomainResponse> getAllActiveDomains();

    DomainResponse getDomainById(Long id);

    DomainResponse createDomain(DomainRequest request);

    DomainResponse updateDomain(Long id, DomainRequest request);

    void deleteDomain(Long id);
}