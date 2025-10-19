package com.networkinsights.ai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * 聊天消息模型
 */
public class ChatMessage {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("content")
    private String content;
    
    @JsonProperty("sender")
    private String sender; // "user" or "ai"
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("language")
    private String language;
    
    @JsonProperty("originalContent")
    private String originalContent; // 原始语言内容
    
    // 构造函数
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ChatMessage(String content, String sender) {
        this();
        this.content = content;
        this.sender = sender;
    }
    
    public ChatMessage(String content, String sender, String language) {
        this(content, sender);
        this.language = language;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getOriginalContent() {
        return originalContent;
    }
    
    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }
}
