package com.learningplatform.backend.dto;

import com.learningplatform.backend.enums.AiMessageRole;
import com.learningplatform.backend.enums.TeachingStyle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AiMessageResponse {

    private Long id;
    private AiMessageRole role;
    private String content;
    private TeachingStyle teachingStyle;
    private String provider;
    private List<String> suggestedFollowUps = new ArrayList<>();
    private List<ReferencedResourceDto> referencedResources = new ArrayList<>();
    private LocalDateTime createdAt;

    public AiMessageResponse() {}

    public AiMessageResponse(Long id, AiMessageRole role, String content, TeachingStyle teachingStyle,
                             List<String> suggestedFollowUps, List<ReferencedResourceDto> referencedResources,
                             LocalDateTime createdAt) {
        this(id, role, content, teachingStyle, null, suggestedFollowUps, referencedResources, createdAt);
    }

    public AiMessageResponse(Long id, AiMessageRole role, String content, TeachingStyle teachingStyle,
                             String provider, List<String> suggestedFollowUps,
                             List<ReferencedResourceDto> referencedResources, LocalDateTime createdAt) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.teachingStyle = teachingStyle;
        this.provider = provider;
        this.suggestedFollowUps = suggestedFollowUps != null ? suggestedFollowUps : new ArrayList<>();
        this.referencedResources = referencedResources != null ? referencedResources : new ArrayList<>();
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AiMessageRole getRole() { return role; }
    public void setRole(AiMessageRole role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public TeachingStyle getTeachingStyle() { return teachingStyle; }
    public void setTeachingStyle(TeachingStyle teachingStyle) { this.teachingStyle = teachingStyle; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public List<String> getSuggestedFollowUps() { return suggestedFollowUps; }
    public void setSuggestedFollowUps(List<String> suggestedFollowUps) { this.suggestedFollowUps = suggestedFollowUps; }

    public List<ReferencedResourceDto> getReferencedResources() { return referencedResources; }
    public void setReferencedResources(List<ReferencedResourceDto> referencedResources) { this.referencedResources = referencedResources; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
