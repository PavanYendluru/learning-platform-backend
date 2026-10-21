package com.learningplatform.backend.dto;

import java.util.List;

public class DashboardDataResponse {

    private DashboardStatsDto stats;
    private CourseProgressDto currentCourse;
    private List<CourseProgressDto> ongoingCourses;

    public DashboardDataResponse() {}

    public DashboardDataResponse(DashboardStatsDto stats, CourseProgressDto currentCourse, List<CourseProgressDto> ongoingCourses) {
        this.stats = stats;
        this.currentCourse = currentCourse;
        this.ongoingCourses = ongoingCourses;
    }

    public DashboardStatsDto getStats() {
        return stats;
    }

    public void setStats(DashboardStatsDto stats) {
        this.stats = stats;
    }

    public CourseProgressDto getCurrentCourse() {
        return currentCourse;
    }

    public void setCurrentCourse(CourseProgressDto currentCourse) {
        this.currentCourse = currentCourse;
    }

    public List<CourseProgressDto> getOngoingCourses() {
        return ongoingCourses;
    }

    public void setOngoingCourses(List<CourseProgressDto> ongoingCourses) {
        this.ongoingCourses = ongoingCourses;
    }

    public static class DashboardStatsDto {
        private int domainsExplored;
        private int topicsCompleted;
        private int resourcesSaved;
        private int averageProgress;

        public DashboardStatsDto() {}

        public DashboardStatsDto(int domainsExplored, int topicsCompleted, int resourcesSaved, int averageProgress) {
            this.domainsExplored = domainsExplored;
            this.topicsCompleted = topicsCompleted;
            this.resourcesSaved = resourcesSaved;
            this.averageProgress = averageProgress;
        }

        public int getDomainsExplored() {
            return domainsExplored;
        }

        public void setDomainsExplored(int domainsExplored) {
            this.domainsExplored = domainsExplored;
        }

        public int getTopicsCompleted() {
            return topicsCompleted;
        }

        public void setTopicsCompleted(int topicsCompleted) {
            this.topicsCompleted = topicsCompleted;
        }

        public int getResourcesSaved() {
            return resourcesSaved;
        }

        public void setResourcesSaved(int resourcesSaved) {
            this.resourcesSaved = resourcesSaved;
        }

        public int getAverageProgress() {
            return averageProgress;
        }

        public void setAverageProgress(int averageProgress) {
            this.averageProgress = averageProgress;
        }
    }

    public static class CourseProgressDto {
        private Long id;
        private String domain;
        private String topic;
        private String title;
        private String description;
        private int progress;
        private String thumbnailUrl;
        private String category;

        public CourseProgressDto() {}

        public CourseProgressDto(Long id, String domain, String topic, String title, String description, int progress, String thumbnailUrl, String category) {
            this.id = id;
            this.domain = domain;
            this.topic = topic;
            this.title = title;
            this.description = description;
            this.progress = progress;
            this.thumbnailUrl = thumbnailUrl;
            this.category = category;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}
