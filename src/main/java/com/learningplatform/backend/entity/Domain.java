package com.learningplatform.backend.entity;

import com.learningplatform.backend.enums.Status;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a technology / learning domain.
 * Examples: Java, Spring Boot, React, Python, System Design, etc.
 *
 * This class is mapped by JPA/Hibernate to the "domains" table.
 */
@Entity
@Table(
    name = "domains",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_domain_name", columnNames = "name"),
        @UniqueConstraint(name = "uk_domain_slug", columnNames = "slug")
    }
)
public class Domain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 120)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String iconUrl;

    @Column(length = 500)
    private String bannerUrl;

    @Column(length = 50)
    private String difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Automatically set timestamps before insert/update
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}