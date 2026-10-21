package com.learningplatform.backend.dto;

import jakarta.validation.constraints.Size;

public class CreateConversationRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private Long contextTopicId;

    private AiMessageRequest initialMessage;

    public CreateConversationRequest() {}

    public CreateConversationRequest(String title, Long contextTopicId, AiMessageRequest initialMessage) {
        this.title = title;
        this.contextTopicId = contextTopicId;
        this.initialMessage = initialMessage;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getContextTopicId() { return contextTopicId; }
    public void setContextTopicId(Long contextTopicId) { this.contextTopicId = contextTopicId; }

    public AiMessageRequest getInitialMessage() { return initialMessage; }
    public void setInitialMessage(AiMessageRequest initialMessage) { this.initialMessage = initialMessage; }
}
