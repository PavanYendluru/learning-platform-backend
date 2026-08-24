package com.learningplatform.backend.dto;

import com.learningplatform.backend.enums.RoadmapStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class RoadmapRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    private String description;

    private Long domainId;

    private RoadmapStatus status;

    @Valid
    private List<RoadmapStepRequest> steps = new ArrayList<>();

    // Getters & Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getDomainId() { return domainId; }
    public void setDomainId(Long domainId) { this.domainId = domainId; }
    public RoadmapStatus getStatus() { return status; }
    public void setStatus(RoadmapStatus status) { this.status = status; }
    public List<RoadmapStepRequest> getSteps() { return steps; }
    public void setSteps(List<RoadmapStepRequest> steps) { this.steps = steps; }
}