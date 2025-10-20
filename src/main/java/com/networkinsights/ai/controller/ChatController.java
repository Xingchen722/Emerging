package com.networkinsights.ai.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.networkinsights.ai.model.ChatMessage;
import com.networkinsights.ai.model.LanguageRequest;
import com.networkinsights.ai.model.PSAData;
import com.networkinsights.ai.model.SupportedLanguage;
import com.networkinsights.ai.service.AIService;
import com.networkinsights.ai.service.KeywordExtractionService;
import com.networkinsights.ai.service.OpenAIService;
import com.networkinsights.ai.service.PSADataService;
import com.networkinsights.ai.service.PowerBIService;
import com.networkinsights.ai.service.TranslationService;

/**
 * 聊天控制器
 * 处理聊天相关的API请求
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    @Autowired
    private AIService aiService;
    
    @Autowired
    private TranslationService translationService;
    
    @Autowired
    private KeywordExtractionService keywordExtractionService;
    
    @Autowired
    private PSADataService psaDataService;
    
    @Autowired
    private OpenAIService openAIService;
    
    @Autowired
    private PowerBIService powerBIService;
    
    @Autowired
    private com.networkinsights.ai.service.DataReadingService dataReadingService;
    
    /**
     * 发送消息并获取AI回复（集成数据读取）
     */
    @PostMapping("/message-with-data")
    public ResponseEntity<ChatMessage> sendMessageWithData(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            String language = request.getOrDefault("language", "zh");
            
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorMessage("消息不能为空", language));
            }
            
            ChatMessage response = aiService.processMessageWithData(message, language);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("处理消息失败: {}", e.getMessage(), e);
            String language = request.getOrDefault("language", "zh");
            return ResponseEntity.ok(createErrorMessage("处理您的消息时出现了错误。请稍后重试。", language));
        }
    }
    
    /**
     * 获取数据统计信息
     */
    @GetMapping("/data/statistics")
    public ResponseEntity<Map<String, Object>> getDataStatistics() {
        try {
            Map<String, Object> statistics = dataReadingService.getDataStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("获取数据统计失败: {}", e.getMessage(), e);
            return ResponseEntity.ok(Collections.singletonMap("error", "获取数据统计失败"));
        }
    }
    
    /**
     * 根据关键字搜索数据
     */
    @PostMapping("/data/search")
    public ResponseEntity<List<PSAData>> searchData(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, List<String>> keywords = (Map<String, List<String>>) request.get("keywords");
            
            if (keywords == null || keywords.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }
            
            List<PSAData> results = dataReadingService.searchDataByKeywords(keywords);
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            logger.error("搜索数据失败: {}", e.getMessage(), e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }
    
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
    
    /**
     * 提取关键词接口
     */
    @PostMapping("/extract-keywords")
    public ResponseEntity<KeywordExtractionService.ExtractedKeywords> extractKeywords(
            @RequestBody String question) {
        
        KeywordExtractionService.ExtractedKeywords keywords = 
                keywordExtractionService.extractKeywords(question);
        
        return ResponseEntity.ok(keywords);
    }
    
    /**
     * 查询PSA数据接口
     */
    @PostMapping("/query-psa-data")
    public ResponseEntity<List<PSAData>> queryPSAData(
            @RequestBody KeywordExtractionService.ExtractedKeywords keywords) {
        
        List<PSAData> psaData = psaDataService.queryPSAData(keywords);
        
        return ResponseEntity.ok(psaData);
    }
    
    /**
     * 根据问题查询PSA数据接口（一步完成关键词提取和数据查询）
     */
    @PostMapping("/query-by-question")
    public ResponseEntity<List<PSAData>> queryByQuestion(
            @RequestParam String question) {
        
        KeywordExtractionService.ExtractedKeywords keywords = 
                keywordExtractionService.extractKeywords(question);
        List<PSAData> psaData = psaDataService.queryPSAData(keywords);
        
        return ResponseEntity.ok(psaData);
    }
    
    /**
     * 获取可用的数据类型
     */
    @GetMapping("/psa-data-types")
    public ResponseEntity<Set<String>> getAvailableDataTypes() {
        Set<String> dataTypes = psaDataService.getAvailableDataTypes();
        return ResponseEntity.ok(dataTypes);
    }
    
    /**
     * 获取可用的港口列表
     */
    @GetMapping("/psa-ports")
    public ResponseEntity<Set<String>> getAvailablePorts() {
        Set<String> ports = psaDataService.getAvailablePorts();
        return ResponseEntity.ok(ports);
    }
    
    /**
     * 根据数据类型查询数据
     */
    @GetMapping("/psa-data/by-type/{dataType}")
    public ResponseEntity<List<PSAData>> getDataByType(@PathVariable String dataType) {
        List<PSAData> data = psaDataService.queryByDataType(dataType);
        return ResponseEntity.ok(data);
    }
    
    /**
     * 根据港口查询数据
     */
    @GetMapping("/psa-data/by-port/{portName}")
    public ResponseEntity<List<PSAData>> getDataByPort(@PathVariable String portName) {
        List<PSAData> data = psaDataService.queryByPort(portName);
        return ResponseEntity.ok(data);
    }
    
    /**
     * OpenAI关键词提取接口
     */
    @PostMapping("/openai/extract-keywords")
    public ResponseEntity<String> extractKeywordsWithOpenAI(
            @RequestParam String question,
            @RequestParam(defaultValue = "zh") String language) {
        
        try {
            String keywords = openAIService.extractKeywords(question, language);
            return ResponseEntity.ok(keywords);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("关键词提取失败: " + e.getMessage());
        }
    }
    
    /**
     * OpenAI生成回复接口
     */
    @PostMapping("/openai/generate-response")
    public ResponseEntity<String> generateResponseWithOpenAI(
            @RequestParam String question,
            @RequestParam(defaultValue = "zh") String language) {
        
        try {
            String response = openAIService.generateResponse(question, null, language);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("AI回复生成失败: " + e.getMessage());
        }
    }
    
    /**
     * Power BI数据查询接口
     */
    @PostMapping("/powerbi/query")
    public ResponseEntity<List<PSAData>> queryPowerBIData(
            @RequestParam String query) {
        
        try {
            List<PSAData> data = powerBIService.queryPowerBIData(query);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }
    
    /**
     * Power BI服务状态检查
     */
    @GetMapping("/powerbi/status")
    public ResponseEntity<Map<String, Object>> getPowerBIStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("available", powerBIService.isServiceAvailable());
        status.put("dashboardUrl", powerBIService.getDashboardUrl());
        return ResponseEntity.ok(status);
    }
    
    /**
     * OpenAI服务状态检查
     */
    @GetMapping("/openai/status")
    public ResponseEntity<Map<String, Object>> getOpenAIStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("available", openAIService.isServiceAvailable());
        return ResponseEntity.ok(status);
    }
    
    /**
     * 系统状态检查
     */
    @GetMapping("/system/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("openai", openAIService.isServiceAvailable());
        status.put("powerbi", powerBIService.isServiceAvailable());
        status.put("translation", translationService != null);
        status.put("keywordExtraction", keywordExtractionService != null);
        status.put("psaData", psaDataService != null);
        return ResponseEntity.ok(status);
    }
    
    /**
     * 创建错误消息
     */
    private ChatMessage createErrorMessage(String message, String language) {
        ChatMessage errorMessage = new ChatMessage();
        errorMessage.setId(java.util.UUID.randomUUID().toString());
        errorMessage.setContent(message);
        errorMessage.setSender("ai");
        errorMessage.setTimestamp(java.time.LocalDateTime.now());
        errorMessage.setLanguage(language);
        return errorMessage;
    }
}
