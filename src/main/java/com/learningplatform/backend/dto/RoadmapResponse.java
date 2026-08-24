package com.learningplatform.backend.dto;

import com.learningplatform.backend.enums.RoadmapStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoadmapResponse {
    private Long id;
    private String title;
    private String description;
    private Long domainId;
    private String domainName;
    private RoadmapStatus status;
    private List<RoadmapStepResponse> steps = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getDomainId() { return domainId; }
    public void setDomainId(Long domainId) { this.domainId = domainId; }
    public String getDomainName() { return domainName; }
    public void setDomainName(String domainName) { this.domainName = domainName; }
    public RoadmapStatus getStatus() { return status; }
    public void setStatus(RoadmapStatus status) { this.status = status; }
    public List<RoadmapStepResponse> getSteps() { return steps; }
    public void setSteps(List<RoadmapStepResponse> steps) { this.steps = steps; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}