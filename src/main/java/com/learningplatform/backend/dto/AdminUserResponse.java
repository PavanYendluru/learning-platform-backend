package com.learningplatform.backend.dto;

import java.time.LocalDateTime;

public class AdminUserResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String status; // "ACTIVE" or "INACTIVE"
    private LocalDateTime createdAt;
    private String lastActiveAt;
    private UserSummary summary;

    public AdminUserResponse() {}

    public AdminUserResponse(Long id, String name, String email, String role, String status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.lastActiveAt = "Recently";
    }

    public static class UserSummary {
        private int domainsExplored;
        private int topicsCompleted;
        private int resourcesCompleted;
        private int resourcesSaved;

        public UserSummary() {}

        public UserSummary(int domainsExplored, int topicsCompleted, int resourcesCompleted, int resourcesSaved) {
            this.domainsExplored = domainsExplored;
            this.topicsCompleted = topicsCompleted;
            this.resourcesCompleted = resourcesCompleted;
            this.resourcesSaved = resourcesSaved;
        }

        public int getDomainsExplored() { return domainsExplored; }
        public void setDomainsExplored(int domainsExplored) { this.domainsExplored = domainsExplored; }

        public int getTopicsCompleted() { return topicsCompleted; }
        public void setTopicsCompleted(int topicsCompleted) { this.topicsCompleted = topicsCompleted; }

        public int getResourcesCompleted() { return resourcesCompleted; }
        public void setResourcesCompleted(int resourcesCompleted) { this.resourcesCompleted = resourcesCompleted; }

        public int getResourcesSaved() { return resourcesSaved; }
        public void setResourcesSaved(int resourcesSaved) { this.resourcesSaved = resourcesSaved; }
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(String lastActiveAt) { this.lastActiveAt = lastActiveAt; }

    public UserSummary getSummary() { return summary; }
    public void setSummary(UserSummary summary) { this.summary = summary; }
}
