package com.networkinsights.ai.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 语言翻译请求模型
 */
public class LanguageRequest {
    
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("sourceLanguage")
    private String sourceLanguage;
    
    @JsonProperty("targetLanguage")
    private String targetLanguage;
    
    @JsonProperty("detectLanguage")
    private boolean detectLanguage = false;
    
    // 构造函数
    public LanguageRequest() {}
    
    public LanguageRequest(String text, String targetLanguage) {
        this.text = text;
        this.targetLanguage = targetLanguage;
        this.detectLanguage = true;
    }
    
    public LanguageRequest(String text, String sourceLanguage, String targetLanguage) {
        this.text = text;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.detectLanguage = false;
    }
    
    // Getters and Setters
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getSourceLanguage() {
        return sourceLanguage;
    }
    
    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }
    
    public String getTargetLanguage() {
        return targetLanguage;
    }
    
    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }
    
    public boolean isDetectLanguage() {
        return detectLanguage;
    }
    
    public void setDetectLanguage(boolean detectLanguage) {
        this.detectLanguage = detectLanguage;
    }
}
