package com.learningplatform.backend.entity;

import com.learningplatform.backend.enums.Status;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a learning topic that belongs to a Domain.
 * Example: Domain "Spring Boot" → Topics: "Spring Core", "Spring MVC", "Spring Security"
 */
@Entity
@Table(
    name = "topics",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_topic_domain_slug", columnNames = {"domain_id", "slug"})
    }
)
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many topics belong to one domain.
     * FetchType.LAZY = domain is loaded only when we explicitly access it.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "domain_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_topic_domain"))
    private Domain domain;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 160)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Used to display topics in a logical order inside a domain.
     */
    @Column(nullable = false)
    private Integer sequenceNumber = 1;

    @Column(length = 50)
    private String difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

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

    public Domain getDomain() { return domain; }
    public void setDomain(Domain domain) { this.domain = domain; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}