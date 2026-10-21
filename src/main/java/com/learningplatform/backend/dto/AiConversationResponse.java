package com.learningplatform.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AiConversationResponse {

    private Long id;
    private String title;
    private Long contextTopicId;
    private String contextTopicName;
    private Integer messageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AiMessageResponse> messages = new ArrayList<>();

    public AiConversationResponse() {}

    public AiConversationResponse(Long id, String title, Long contextTopicId, String contextTopicName,
                                  Integer messageCount, LocalDateTime createdAt, LocalDateTime updatedAt,
                                  List<AiMessageResponse> messages) {
        this.id = id;
        this.title = title;
        this.contextTopicId = contextTopicId;
        this.contextTopicName = contextTopicName;
        this.messageCount = messageCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.messages = messages != null ? messages : new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getContextTopicId() { return contextTopicId; }
    public void setContextTopicId(Long contextTopicId) { this.contextTopicId = contextTopicId; }

    public String getContextTopicName() { return contextTopicName; }
    public void setContextTopicName(String contextTopicName) { this.contextTopicName = contextTopicName; }

    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<AiMessageResponse> getMessages() { return messages; }
    public void setMessages(List<AiMessageResponse> messages) { this.messages = messages; }
}
