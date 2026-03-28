package com.networkinsights.ai.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 支持的语言模型
 */
public class SupportedLanguage {
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("nativeName")
    private String nativeName;
    
    @JsonProperty("flag")
    private String flag; // 国旗emoji
    
    // 构造函数
    public SupportedLanguage() {}
    
    public SupportedLanguage(String code, String name, String nativeName, String flag) {
        this.code = code;
        this.name = name;
        this.nativeName = nativeName;
        this.flag = flag;
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getNativeName() {
        return nativeName;
    }
    
    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }
    
    public String getFlag() {
        return flag;
    }
    
    public void setFlag(String flag) {
        this.flag = flag;
    }
}
