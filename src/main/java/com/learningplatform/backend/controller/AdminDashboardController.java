package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.AdminDashboardStatsResponse;
import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<AdminDashboardStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved successfully",
                dashboardService.getStats()));
    }
}