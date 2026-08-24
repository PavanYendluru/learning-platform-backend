package com.learningplatform.backend.entity;

import com.learningplatform.backend.enums.TechnologyUpdateStatus;
import com.learningplatform.backend.enums.TechnologyUpdateType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "technology_updates")
public class TechnologyUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id")
    private Domain domain;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 150)
    private String sourceName;

    @Column(length = 1000)
    private String sourceUrl;

    private LocalDate publishedDate;

    @Column(length = 50)
    private String technologyVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TechnologyUpdateType updateType;

    @Column(length = 200)
    private String externalReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TechnologyUpdateStatus status = TechnologyUpdateStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public Domain getDomain() { return domain; }
    public void setDomain(Domain domain) { this.domain = domain; }
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
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}