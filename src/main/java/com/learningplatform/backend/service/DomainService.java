package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.DomainRequest;
import com.learningplatform.backend.dto.DomainResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Business operations related to Domain.
 */
public interface DomainService {

    @PreAuthorize("isAuthenticated()")
    List<DomainResponse> getAllActiveDomains();

    @PreAuthorize("hasRole('ADMIN')")
    List<DomainResponse> getAllDomainsForAdmin();

    @PreAuthorize("isAuthenticated()")
    DomainResponse getDomainById(Long id);

    @PreAuthorize("hasRole('ADMIN')")
    DomainResponse getDomainByIdForAdmin(Long id);

    @PreAuthorize("hasRole('ADMIN')")
    DomainResponse createDomain(DomainRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    DomainResponse updateDomain(Long id, DomainRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    void deleteDomain(Long id);
}
