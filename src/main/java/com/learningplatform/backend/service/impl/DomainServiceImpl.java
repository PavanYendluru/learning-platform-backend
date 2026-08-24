package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.DomainRequest;
import com.learningplatform.backend.dto.DomainResponse;
import com.learningplatform.backend.entity.Domain;
import com.learningplatform.backend.enums.Status;
import com.learningplatform.backend.exception.ConflictException;
import com.learningplatform.backend.exception.DomainNotFoundException;
import com.learningplatform.backend.repository.DomainRepository;
import com.learningplatform.backend.service.DomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of Domain business logic.
 */
@Service
@Transactional
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;

    public DomainServiceImpl(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainResponse> getAllActiveDomains() {
        return domainRepository.findByStatusOrderByNameAsc(Status.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainResponse> getAllDomainsForAdmin() {
        return domainRepository.findAll(org.springframework.data.domain.Sort.by("name").ascending())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DomainResponse getDomainById(Long id) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException(id));

        if (domain.getStatus() != Status.ACTIVE) {
            throw new DomainNotFoundException(id);
        }

        return mapToResponse(domain);
    }

    @Override
    @Transactional(readOnly = true)
    public DomainResponse getDomainByIdForAdmin(Long id) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException(id));

        return mapToResponse(domain);
    }

    @Override
    public DomainResponse createDomain(DomainRequest request) {

        // Business rule: name must be unique
        if (domainRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ConflictException(
                    "Domain with name '" + request.getName() + "' already exists"
            );
        }

        Domain domain = new Domain();
        domain.setName(request.getName().trim());
        domain.setSlug(generateSlug(request.getName()));
        domain.setDescription(request.getDescription());
        domain.setIconUrl(request.getIconUrl());
        domain.setBannerUrl(request.getBannerUrl());
        domain.setDifficulty(request.getDifficulty());
        domain.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : Status.ACTIVE
        );

        Domain saved = domainRepository.save(domain);

        return mapToResponse(saved);
    }

    @Override
    public DomainResponse updateDomain(Long id, DomainRequest request) {

        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException(id));

        // If name is changing, check uniqueness
        if (!domain.getName().equalsIgnoreCase(request.getName().trim())
                && domainRepository.existsByNameIgnoreCase(request.getName().trim())) {

            throw new ConflictException(
                    "Domain with name '" + request.getName() + "' already exists"
            );
        }

        domain.setName(request.getName().trim());
        domain.setSlug(generateSlug(request.getName()));
        domain.setDescription(request.getDescription());
        domain.setIconUrl(request.getIconUrl());
        domain.setBannerUrl(request.getBannerUrl());
        domain.setDifficulty(request.getDifficulty());

        if (request.getStatus() != null) {
            domain.setStatus(request.getStatus());
        }

        Domain updated = domainRepository.save(domain);

        return mapToResponse(updated);
    }

    @Override
    public void deleteDomain(Long id) {

        if (!domainRepository.existsById(id)) {
            throw new DomainNotFoundException(id);
        }

        domainRepository.deleteById(id);
    }

    /**
     * Converts a name into a URL-friendly slug.
     * Example: "Spring Boot" -> "spring-boot"
     */
    private String generateSlug(String name) {
        return name.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    private DomainResponse mapToResponse(Domain domain) {

        DomainResponse response = new DomainResponse();

        response.setId(domain.getId());
        response.setName(domain.getName());
        response.setSlug(domain.getSlug());
        response.setDescription(domain.getDescription());
        response.setIconUrl(domain.getIconUrl());
        response.setBannerUrl(domain.getBannerUrl());
        response.setDifficulty(domain.getDifficulty());
        response.setStatus(domain.getStatus());
        response.setCreatedAt(domain.getCreatedAt());
        response.setUpdatedAt(domain.getUpdatedAt());

        return response;
    }
}
