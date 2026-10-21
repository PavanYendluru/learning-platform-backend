package com.learningplatform.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiStreamEventDto {

    private String type; // "start", "chunk", "provider_switch", "reset", "complete", "error"
    private String provider;
    private String model;
    private String text;
    private String from;
    private String to;
    private String reason;
    private String message;
    private Long messageId;
    private List<String> suggestedFollowUps;
    private List<ReferencedResourceDto> referencedResources;

    public AiStreamEventDto() {}

    public static AiStreamEventDto start(String provider, String model) {
        AiStreamEventDto dto = new AiStreamEventDto();
        dto.type = "start";
        dto.provider = provider;
        dto.model = model;
        return dto;
    }

    public static AiStreamEventDto chunk(String text) {
        AiStreamEventDto dto = new AiStreamEventDto();
        dto.type = "chunk";
        dto.text = text;
        return dto;
    }

    public static AiStreamEventDto providerSwitch(String from, String to, String reason) {
        AiStreamEventDto dto = new AiStreamEventDto();
        dto.type = "provider_switch";
        dto.from = from;
        dto.to = to;
        dto.reason = reason;
        return dto;
    }

    public static AiStreamEventDto reset(String reason) {
        AiStreamEventDto dto = new AiStreamEventDto();
        dto.type = "reset";
        dto.reason = reason;
        return dto;
    }

    public static AiStreamEventDto complete(Long messageId, String provider, String model,
                                            List<String> suggestedFollowUps,
                                            List<ReferencedResourceDto> referencedResources) {
        AiStreamEventDto dto = new AiStreamEventDto();
        dto.type = "complete";
        dto.messageId = messageId;
        dto.provider = provider;
        dto.model = model;
        dto.suggestedFollowUps = suggestedFollowUps;
        dto.referencedResources = referencedResources;
        return dto;
    }

    public static AiStreamEventDto error(String message) {
        AiStreamEventDto dto = new AiStreamEventDto();
        dto.type = "error";
        dto.message = message;
        return dto;
    }

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public List<String> getSuggestedFollowUps() { return suggestedFollowUps; }
    public void setSuggestedFollowUps(List<String> suggestedFollowUps) { this.suggestedFollowUps = suggestedFollowUps; }

    public List<ReferencedResourceDto> getReferencedResources() { return referencedResources; }
    public void setReferencedResources(List<ReferencedResourceDto> referencedResources) { this.referencedResources = referencedResources; }
}
