package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.AuditLogResponse;
import com.learningplatform.backend.entity.AuditLog;
import com.learningplatform.backend.repository.AuditLogRepository;
import com.learningplatform.backend.service.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getRecentAuditLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getTitle(),
                        log.getDescription(),
                        log.getActivityType(),
                        log.getActorName(),
                        log.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void logActivity(String title, String description, String activityType, String actorName) {
        AuditLog auditLog = new AuditLog(title, description, activityType, actorName);
        auditLogRepository.save(auditLog);
    }
}
