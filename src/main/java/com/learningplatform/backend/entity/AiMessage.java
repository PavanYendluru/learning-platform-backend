package com.learningplatform.backend.entity;

import com.learningplatform.backend.enums.AiMessageRole;
import com.learningplatform.backend.enums.TeachingStyle;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_messages", indexes = {
        @Index(name = "idx_ai_msg_conv_created", columnList = "conversation_id, created_at ASC")
})
public class AiMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ai_msg_conv"))
    private AiConversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AiMessageRole role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TeachingStyle teachingStyle;

    @Column(length = 50)
    private String provider;

    @Column(columnDefinition = "TEXT")
    private String suggestedFollowUps;

    @Column(columnDefinition = "TEXT")
    private String referencedResources;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }

    public AiConversation getConversation() { return conversation; }
    public void setConversation(AiConversation conversation) { this.conversation = conversation; }

    public AiMessageRole getRole() { return role; }
    public void setRole(AiMessageRole role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public TeachingStyle getTeachingStyle() { return teachingStyle; }
    public void setTeachingStyle(TeachingStyle teachingStyle) { this.teachingStyle = teachingStyle; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getSuggestedFollowUps() { return suggestedFollowUps; }
    public void setSuggestedFollowUps(String suggestedFollowUps) { this.suggestedFollowUps = suggestedFollowUps; }

    public String getReferencedResources() { return referencedResources; }
    public void setReferencedResources(String referencedResources) { this.referencedResources = referencedResources; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
