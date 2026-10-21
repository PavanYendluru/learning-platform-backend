package com.learningplatform.backend.dto;

public class DomainProgressResponse {
    private Long domainId;
    private String domainName;
    private int progressPercentage;
    private long completedTopics;
    private long totalTopics;
    private long completedResources;
    private long totalResources;

    public DomainProgressResponse() {}

    public DomainProgressResponse(Long domainId, String domainName, int progressPercentage,
                                  long completedTopics, long totalTopics,
                                  long completedResources, long totalResources) {
        this.domainId = domainId;
        this.domainName = domainName;
        this.progressPercentage = progressPercentage;
        this.completedTopics = completedTopics;
        this.totalTopics = totalTopics;
        this.completedResources = completedResources;
        this.totalResources = totalResources;
    }

    public Long getDomainId() { return domainId; }
    public void setDomainId(Long domainId) { this.domainId = domainId; }

    public String getDomainName() { return domainName; }
    public void setDomainName(String domainName) { this.domainName = domainName; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public long getCompletedTopics() { return completedTopics; }
    public void setCompletedTopics(long completedTopics) { this.completedTopics = completedTopics; }

    public long getTotalTopics() { return totalTopics; }
    public void setTotalTopics(long totalTopics) { this.totalTopics = totalTopics; }

    public long getCompletedResources() { return completedResources; }
    public void setCompletedResources(long completedResources) { this.completedResources = completedResources; }

    public long getTotalResources() { return totalResources; }
    public void setTotalResources(long totalResources) { this.totalResources = totalResources; }
}
