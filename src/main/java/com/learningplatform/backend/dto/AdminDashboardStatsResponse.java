package com.learningplatform.backend.dto;

public class AdminDashboardStatsResponse {
    private long totalDomains;
    private long totalTopics;
    private long totalResources;
    private long publishedResources;
    private long pendingReviewResources;
    private long totalUsers;
    private long totalRoadmaps;
    private long totalTechnologyUpdates;

    // Getters & Setters
    public long getTotalDomains() { return totalDomains; }
    public void setTotalDomains(long totalDomains) { this.totalDomains = totalDomains; }
    public long getTotalTopics() { return totalTopics; }
    public void setTotalTopics(long totalTopics) { this.totalTopics = totalTopics; }
    public long getTotalResources() { return totalResources; }
    public void setTotalResources(long totalResources) { this.totalResources = totalResources; }
    public long getPublishedResources() { return publishedResources; }
    public void setPublishedResources(long publishedResources) { this.publishedResources = publishedResources; }
    public long getPendingReviewResources() { return pendingReviewResources; }
    public void setPendingReviewResources(long pendingReviewResources) { this.pendingReviewResources = pendingReviewResources; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalRoadmaps() { return totalRoadmaps; }
    public void setTotalRoadmaps(long totalRoadmaps) { this.totalRoadmaps = totalRoadmaps; }
    public long getTotalTechnologyUpdates() { return totalTechnologyUpdates; }
    public void setTotalTechnologyUpdates(long totalTechnologyUpdates) { this.totalTechnologyUpdates = totalTechnologyUpdates; }
}