package com.networkinsights.ai.controller;

import com.networkinsights.ai.model.ChatMessage;
import com.networkinsights.ai.model.LanguageRequest;
import com.networkinsights.ai.model.SupportedLanguage;
import com.networkinsights.ai.service.AIService;
import com.networkinsights.ai.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 聊天控制器
 * 处理聊天相关的API请求
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    @Autowired
    private AIService aiService;
    
    @Autowired
    private TranslationService translationService;
    
    /**
     * 发送消息并获取AI回复
     */
    @PostMapping("/message")
    public ResponseEntity<ChatMessage> sendMessage(
            @RequestBody ChatMessage userMessage,
            @RequestParam(defaultValue = "zh") String language) {
        
        try {
            // 设置消息ID
            userMessage.setId(UUID.randomUUID().toString());
            
            // 处理消息并获取AI回复
            ChatMessage aiResponse = aiService.processMessage(userMessage, language);
            aiResponse.setId(UUID.randomUUID().toString());
            
            return ResponseEntity.ok(aiResponse);
            
        } catch (Exception e) {
            // 返回错误消息
            ChatMessage errorMessage = new ChatMessage(
                "抱歉，处理您的消息时出现了错误。请稍后重试。", 
                "ai", 
                language
            );
            errorMessage.setId(UUID.randomUUID().toString());
            return ResponseEntity.ok(errorMessage);
        }
    }
    
    /**
     * 获取支持的语言列表
     */
    @GetMapping("/languages")
    public ResponseEntity<List<SupportedLanguage>> getSupportedLanguages() {
        List<SupportedLanguage> languages = translationService.getSupportedLanguages();
        return ResponseEntity.ok(languages);
    }
    
    /**
     * 检测文本语言
     */
    @PostMapping("/detect-language")
    public ResponseEntity<String> detectLanguage(@RequestBody String text) {
        String detectedLanguage = translationService.detectLanguage(text);
        return ResponseEntity.ok(detectedLanguage);
    }
    
    /**
     * 翻译文本
     */
    @PostMapping("/translate")
    public ResponseEntity<String> translateText(@RequestBody LanguageRequest request) {
        String translatedText = translationService.translateText(request);
        return ResponseEntity.ok(translatedText);
    }
    
    /**
     * 快速提问接口
     */
    @PostMapping("/quick-question")
    public ResponseEntity<ChatMessage> quickQuestion(
            @RequestParam String question,
            @RequestParam(defaultValue = "zh") String language) {
        
        ChatMessage userMessage = new ChatMessage(question, "user", language);
        userMessage.setId(UUID.randomUUID().toString());
        
        ChatMessage aiResponse = aiService.processMessage(userMessage, language);
        aiResponse.setId(UUID.randomUUID().toString());
        
        return ResponseEntity.ok(aiResponse);
    }
}
