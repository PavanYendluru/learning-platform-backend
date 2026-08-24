package com.learningplatform.backend.entity;

import com.learningplatform.backend.enums.ResourceStatus;
import com.learningplatform.backend.enums.ResourceType;
import com.learningplatform.backend.enums.ResourceVerificationStatus;
import com.learningplatform.backend.enums.YouTubeContentType;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Metadata of a learning resource that belongs to a Topic.
 * We never store the actual video, PDF, or copyrighted content —
 * only legitimate external URLs and metadata.
 */
@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_resource_topic"))
    private Topic topic;

    @Column(nullable = false, length = 300)
    private String title;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ResourceVerificationStatus verificationStatus = ResourceVerificationStatus.UNVERIFIED;

    // already have lastVerifiedAt

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ResourceType resourceType;

    @Column(nullable = false, length = 1000)
    private String externalUrl;

    /** Display order within the topic; lower values appear first when requested. */
    @Column
    private Integer sequenceNumber = 1;

    /** Set for manually maintained YouTube links; null for non-YouTube resources. */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private YouTubeContentType youtubeContentType;

    @Column(length = 1000)
    private String thumbnailUrl;

    @Column(length = 200)
    private String author;

    @Column(length = 200)
    private String publisher;

    @Column(length = 150)
    private String sourceName;

    @Column(length = 50)
    private String difficulty;

    private Integer durationMinutes;

    @Column(length = 20)
    private String language = "en";

    @Column(length = 100)
    private String licenseType;

    @Column(length = 500)
    private String licenseUrl;

    /** Useful for YouTube videoId, DOI, GitHub repo id, etc. */
    @Column(length = 200)
    private String externalResourceId;

    @Column(length = 100)
    private String doi;

    private LocalDate publicationDate;

    private LocalDateTime lastVerifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResourceStatus status = ResourceStatus.DRAFT;

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

    // ----- Getters & Setters -----

    public Long getId() { return id; }

    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ResourceType getResourceType() { return resourceType; }
    public void setResourceType(ResourceType resourceType) { this.resourceType = resourceType; }

    public ResourceVerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(ResourceVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getExternalUrl() { return externalUrl; }
    public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }

    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public YouTubeContentType getYoutubeContentType() { return youtubeContentType; }
    public void setYoutubeContentType(YouTubeContentType youtubeContentType) { this.youtubeContentType = youtubeContentType; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }

    public String getLicenseUrl() { return licenseUrl; }
    public void setLicenseUrl(String licenseUrl) { this.licenseUrl = licenseUrl; }

    public String getExternalResourceId() { return externalResourceId; }
    public void setExternalResourceId(String externalResourceId) { this.externalResourceId = externalResourceId; }

    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public LocalDateTime getLastVerifiedAt() { return lastVerifiedAt; }
    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) { this.lastVerifiedAt = lastVerifiedAt; }

    public ResourceStatus getStatus() { return status; }
    public void setStatus(ResourceStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
