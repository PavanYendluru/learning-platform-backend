package com.learningplatform.backend.dto;

import java.io.Serializable;

public class ReferencedResourceDto implements Serializable {

    private Long id;
    private String title;
    private String resourceType;
    private String externalUrl;
    private String author;
    private String difficulty;
    private Integer durationMinutes;
    private String explanation;

    public ReferencedResourceDto() {}

    public ReferencedResourceDto(Long id, String title, String resourceType, String externalUrl,
                                 String author, String difficulty, Integer durationMinutes, String explanation) {
        this.id = id;
        this.title = title;
        this.resourceType = resourceType;
        this.externalUrl = externalUrl;
        this.author = author;
        this.difficulty = difficulty;
        this.durationMinutes = durationMinutes;
        this.explanation = explanation;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getExternalUrl() { return externalUrl; }
    public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
