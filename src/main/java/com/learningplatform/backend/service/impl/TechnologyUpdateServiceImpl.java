package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.TechnologyUpdateRequest;
import com.learningplatform.backend.dto.TechnologyUpdateResponse;
import com.learningplatform.backend.entity.Domain;
import com.learningplatform.backend.entity.TechnologyUpdate;
import com.learningplatform.backend.enums.TechnologyUpdateStatus;
import com.learningplatform.backend.exception.DomainNotFoundException;
import com.learningplatform.backend.exception.ResourceNotFoundException;
import com.learningplatform.backend.repository.DomainRepository;
import com.learningplatform.backend.repository.TechnologyUpdateRepository;
import com.learningplatform.backend.service.TechnologyUpdateService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TechnologyUpdateServiceImpl implements TechnologyUpdateService {

    private final TechnologyUpdateRepository updateRepository;
    private final DomainRepository domainRepository;

    public TechnologyUpdateServiceImpl(TechnologyUpdateRepository updateRepository,
                                       DomainRepository domainRepository) {
        this.updateRepository = updateRepository;
        this.domainRepository = domainRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnologyUpdateResponse> getAllUpdates() {
        return updateRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnologyUpdateResponse> getPublishedUpdates() {
        return updateRepository.findByStatusOrderByPublishedDateDesc(TechnologyUpdateStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnologyUpdateResponse> getPublishedUpdatesByDomain(Long domainId) {
        return updateRepository.findByDomainIdAndStatusOrderByPublishedDateDesc(domainId, TechnologyUpdateStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TechnologyUpdateResponse getById(Long id) {
        TechnologyUpdate update = updateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return mapToResponse(update);
    }

    @Override
    public TechnologyUpdateResponse create(TechnologyUpdateRequest request) {
        TechnologyUpdate update = new TechnologyUpdate();
        mapRequestToEntity(request, update);
        return mapToResponse(updateRepository.save(update));
    }

    @Override
    public TechnologyUpdateResponse update(Long id, TechnologyUpdateRequest request) {
        TechnologyUpdate update = updateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        mapRequestToEntity(request, update);
        return mapToResponse(updateRepository.save(update));
    }

    @Override
    public void delete(Long id) {
        if (!updateRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        updateRepository.deleteById(id);
    }

    private void mapRequestToEntity(TechnologyUpdateRequest request, TechnologyUpdate update) {
        update.setTitle(request.getTitle().trim());
        update.setSummary(request.getSummary());
        update.setContent(request.getContent());
        update.setSourceName(request.getSourceName());
        update.setSourceUrl(request.getSourceUrl());
        update.setPublishedDate(request.getPublishedDate());
        update.setTechnologyVersion(request.getTechnologyVersion());
        update.setUpdateType(request.getUpdateType());
        update.setExternalReference(request.getExternalReference());
        update.setStatus(request.getStatus() != null ? request.getStatus() : TechnologyUpdateStatus.DRAFT);

        if (request.getDomainId() != null) {
            Domain domain = domainRepository.findById(request.getDomainId())
                    .orElseThrow(() -> new DomainNotFoundException(request.getDomainId()));
            update.setDomain(domain);
        } else {
            update.setDomain(null);
        }
    }

    private TechnologyUpdateResponse mapToResponse(TechnologyUpdate update) {
        TechnologyUpdateResponse r = new TechnologyUpdateResponse();
        r.setId(update.getId());
        r.setTitle(update.getTitle());
        r.setSummary(update.getSummary());
        r.setContent(update.getContent());
        r.setSourceName(update.getSourceName());
        r.setSourceUrl(update.getSourceUrl());
        r.setPublishedDate(update.getPublishedDate());
        r.setTechnologyVersion(update.getTechnologyVersion());
        r.setUpdateType(update.getUpdateType());
        r.setExternalReference(update.getExternalReference());
        r.setStatus(update.getStatus());
        r.setCreatedAt(update.getCreatedAt());
        r.setUpdatedAt(update.getUpdatedAt());

        if (update.getDomain() != null) {
            r.setDomainId(update.getDomain().getId());
            r.setDomainName(update.getDomain().getName());
        }
        return r;
    }
}