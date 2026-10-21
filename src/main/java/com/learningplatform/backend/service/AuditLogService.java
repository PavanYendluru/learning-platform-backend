package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.AuditLogResponse;
import java.util.List;

public interface AuditLogService {
    List<AuditLogResponse> getRecentAuditLogs();
    void logActivity(String title, String description, String activityType, String actorName);
}
