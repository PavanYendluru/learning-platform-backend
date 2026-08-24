package com.learningplatform.backend.dto;

import com.learningplatform.backend.enums.TechnologyUpdateStatus;
import com.learningplatform.backend.enums.TechnologyUpdateType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TechnologyUpdateResponse {
    private Long id;
    private Long domainId;
    private String domainName;
    private String title;
    private String summary;
    private String content;
    private String sourceName;
    private String sourceUrl;
    private LocalDate publishedDate;
    private String technologyVersion;
    private TechnologyUpdateType updateType;
    private String externalReference;
    private TechnologyUpdateStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDomainId() { return domainId; }
    public void setDomainId(Long domainId) { this.domainId = domainId; }
    public String getDomainName() { return domainName; }
    public void setDomainName(String domainName) { this.domainName = domainName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }
    public String getTechnologyVersion() { return technologyVersion; }
    public void setTechnologyVersion(String technologyVersion) { this.technologyVersion = technologyVersion; }
    public TechnologyUpdateType getUpdateType() { return updateType; }
    public void setUpdateType(TechnologyUpdateType updateType) { this.updateType = updateType; }
    public String getExternalReference() { return externalReference; }
    public void setExternalReference(String externalReference) { this.externalReference = externalReference; }
    public TechnologyUpdateStatus getStatus() { return status; }
    public void setStatus(TechnologyUpdateStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}