package com.learningplatform.backend.dto;

import com.learningplatform.backend.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body used when creating or updating a Domain.
 * Validation annotations ensure data quality before it reaches the service.
 */
public class DomainRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 1000, message = "Description is too long")
    private String description;

    @Size(max = 500, message = "Icon URL is too long")
    private String iconUrl;

    @Size(max = 500, message = "Banner URL is too long")
    private String bannerUrl;

    @Size(max = 50, message = "Difficulty must not exceed 50 characters")
    private String difficulty;

    private Status status;

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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
}