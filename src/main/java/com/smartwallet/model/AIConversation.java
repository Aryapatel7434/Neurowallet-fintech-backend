package com.smartwallet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "ai_conversations",
    indexes = {
        @Index(name = "idx_ai_conversation_user_updated", columnList = "user_id, updated_at")
    }
)
public class AIConversation {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ai_conversation_user"))
    private User user;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("createdAt ASC, id ASC")
    private List<AIConversationMessage> messages = new ArrayList<>();

    protected AIConversation() {
    }

    public AIConversation(User user, String title) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.title = title;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void touch() {
        updatedAt = LocalDateTime.now();
    }

    public void addMessage(AIConversationMessage message) {
        messages.add(message);
        message.setConversation(this);
        touch();
    }

    public String getId() { return id; }
    public User getUser() { return user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<AIConversationMessage> getMessages() { return messages; }
}
