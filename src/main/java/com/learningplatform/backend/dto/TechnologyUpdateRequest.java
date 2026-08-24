package com.learningplatform.backend.dto;

import com.learningplatform.backend.enums.TechnologyUpdateStatus;
import com.learningplatform.backend.enums.TechnologyUpdateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class TechnologyUpdateRequest {

    private Long domainId;

    @NotBlank(message = "Title is required")
    @Size(max = 300)
    private String title;

    private String summary;
    private String content;

    @Size(max = 150)
    private String sourceName;

    @Size(max = 1000)
    private String sourceUrl;

    private LocalDate publishedDate;

    @Size(max = 50)
    private String technologyVersion;

    @NotNull(message = "Update type is required")
    private TechnologyUpdateType updateType;

    @Size(max = 200)
    private String externalReference;

    private TechnologyUpdateStatus status;

    // Getters & Setters
    public Long getDomainId() { return domainId; }
    public void setDomainId(Long domainId) { this.domainId = domainId; }
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
}