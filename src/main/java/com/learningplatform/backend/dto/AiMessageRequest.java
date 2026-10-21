package com.learningplatform.backend.dto;

import com.learningplatform.backend.enums.TeachingStyle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AiMessageRequest {

    @NotBlank(message = "Message content must not be blank")
    @Size(max = 4000, message = "Message must not exceed 4000 characters")
    private String content;

    private TeachingStyle teachingStyle = TeachingStyle.GENERAL;

    private Long contextTopicId;

    private String currentPage;

    public AiMessageRequest() {}

    public AiMessageRequest(String content, TeachingStyle teachingStyle, Long contextTopicId, String currentPage) {
        this.content = content;
        this.teachingStyle = teachingStyle != null ? teachingStyle : TeachingStyle.GENERAL;
        this.contextTopicId = contextTopicId;
        this.currentPage = currentPage;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public TeachingStyle getTeachingStyle() { return teachingStyle; }
    public void setTeachingStyle(TeachingStyle teachingStyle) { this.teachingStyle = teachingStyle; }

    public Long getContextTopicId() { return contextTopicId; }
    public void setContextTopicId(Long contextTopicId) { this.contextTopicId = contextTopicId; }

    public String getCurrentPage() { return currentPage; }
    public void setCurrentPage(String currentPage) { this.currentPage = currentPage; }
}
