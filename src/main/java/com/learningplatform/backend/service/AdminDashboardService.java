package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.AdminDashboardStatsResponse;

public interface AdminDashboardService {
    AdminDashboardStatsResponse getStats();
}