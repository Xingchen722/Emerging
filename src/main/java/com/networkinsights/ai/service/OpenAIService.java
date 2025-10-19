package com.networkinsights.ai.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * OpenAI服务类
 * 集成OpenAI API进行智能对话和关键词提取
 */
@Service
public class OpenAIService {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
    
    private final String apiKey;
    private final String baseUrl;
    private final String apiVersion;
    private final String deploymentName;
    private final String model;
    private final int maxTokens;
    private final double temperature;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public OpenAIService(@Value("${openai.api.primary-key}") String primaryKey,
                        @Value("${openai.api.secondary-key}") String secondaryKey,
                        @Value("${openai.api.base-url}") String baseUrl,
                        @Value("${openai.api.api-version}") String apiVersion,
                        @Value("${openai.api.deployment-name}") String deploymentName,
                        @Value("${openai.api.model}") String model,
                        @Value("${openai.api.max-tokens}") int maxTokens,
                        @Value("${openai.api.temperature}") double temperature) {
        
        // 优先使用primary key，如果不可用则使用secondary key
        this.apiKey = primaryKey != null && !primaryKey.equals("your-primary-key-here") 
                     ? primaryKey : secondaryKey;
        
        this.baseUrl = baseUrl;
        this.apiVersion = apiVersion;
        this.deploymentName = deploymentName;
        this.model = model;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
        
        logger.info("Azure OpenAI服务初始化完成，使用模型: {}，部署名称: {}", model, deploymentName);
    }
    
    /**
     * 生成AI回复
     */
    public String generateResponse(String userMessage, String context, String language) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", temperature);
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 系统提示词
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", buildSystemPrompt(language));
            messages.add(systemMessage);
            
            // 上下文信息
            if (context != null && !context.isEmpty()) {
                Map<String, String> contextMessage = new HashMap<>();
                contextMessage.put("role", "user");
                contextMessage.put("content", "上下文信息：" + context);
                messages.add(contextMessage);
            }
            
            // 用户消息
            Map<String, String> userMessageMap = new HashMap<>();
            userMessageMap.put("role", "user");
            userMessageMap.put("content", userMessage);
            messages.add(userMessageMap);
            
            requestBody.put("messages", messages);
            
            // 发送HTTP请求
            String response = callOpenAIAPI(requestBody);
            
            logger.info("OpenAI回复生成成功");
            return response;
            
        } catch (Exception e) {
            logger.error("OpenAI API调用失败: {}", e.getMessage(), e);
            return "抱歉，AI服务暂时不可用，请稍后重试。";
        }
    }
    
    /**
     * 提取关键词
     */
    public String extractKeywords(String userMessage, String language) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.3);
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 系统提示词
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", buildKeywordExtractionPrompt(language));
            messages.add(systemMessage);
            
            // 用户消息
            Map<String, String> userMessageMap = new HashMap<>();
            userMessageMap.put("role", "user");
            userMessageMap.put("content", userMessage);
            messages.add(userMessageMap);
            
            requestBody.put("messages", messages);
            
            // 发送HTTP请求
            String response = callOpenAIAPI(requestBody);
            
            logger.info("关键词提取成功");
            return response;
            
        } catch (Exception e) {
            logger.error("关键词提取失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 基于PSA数据生成智能回复
     */
    public String generateDataBasedResponse(String userMessage, 
                                          Map<String, Object> psaData, 
                                          String language) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", temperature);
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 系统提示词
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", buildDataBasedPrompt(language));
            messages.add(systemMessage);
            
            // 添加PSA数据上下文
            String dataContext = buildDataContext(psaData, language);
            Map<String, String> dataMessage = new HashMap<>();
            dataMessage.put("role", "user");
            dataMessage.put("content", "PSA数据：" + dataContext + "\n\n用户问题：" + userMessage);
            messages.add(dataMessage);
            
            requestBody.put("messages", messages);
            
            // 发送HTTP请求
            String response = callOpenAIAPI(requestBody);
            
            logger.info("基于PSA数据的AI回复生成成功");
            return response;
            
        } catch (Exception e) {
            logger.error("基于PSA数据的AI回复生成失败: {}", e.getMessage(), e);
            return "抱歉，无法基于数据生成回复，请稍后重试。";
        }
    }
    
    /**
     * 调用Azure OpenAI API
     */
    private String callOpenAIAPI(Map<String, Object> requestBody) throws IOException, InterruptedException {
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);
        
        // 根据Swagger文档，gpt-4.1-nano只支持聊天完成API
        String[] apiPaths = {
            baseUrl + "/openai/deployments/" + deploymentName + "/chat/completions?api-version=" + apiVersion
        };
        
        String[][] authHeaders = {
            {"api-key", apiKey}  // 使用正确的认证头部
        };
        
        for (String apiUrl : apiPaths) {
            for (String[] authHeader : authHeaders) {
                try {
                    logger.info("尝试API路径: {} 使用认证: {}", apiUrl, authHeader[0]);
                    
                    // 使用原始请求体（聊天完成API格式）
                    String adjustedRequestBodyJson = objectMapper.writeValueAsString(requestBody);
                    
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(apiUrl))
                            .header(authHeader[0], authHeader[1])
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(adjustedRequestBodyJson))
                            .build();
                
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    
                    if (response.statusCode() == 200) {
                        JsonNode jsonNode = objectMapper.readTree(response.body());
                        logger.info("成功连接到API路径: {} 使用认证: {}", apiUrl, authHeader[0]);
                        
                        // 解析聊天完成API响应
                        return jsonNode.get("choices").get(0).get("message").get("content").asText();
                    } else {
                        logger.warn("API路径 {} 使用认证 {} 返回状态码: {} - {}", apiUrl, authHeader[0], response.statusCode(), response.body().substring(0, Math.min(200, response.body().length())));
                    }
                } catch (Exception e) {
                    logger.warn("API路径 {} 使用认证 {} 连接失败: {}", apiUrl, authHeader[0], e.getMessage());
                }
            }
        }
        
        throw new RuntimeException("所有API路径和认证方式都无法连接");
    }
    
    
    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt(String language) {
        if ("zh".equals(language)) {
            return """
                你是PSA（新加坡港务集团）的AI助手，专门帮助用户查询港口运营数据。
                
                你的职责：
                1. 理解用户关于港口运营的问题
                2. 提供准确、专业的港口数据信息
                3. 用友好、专业的语调回复
                4. 如果数据不足，请说明并提供相关建议
                
                请用中文回复，保持专业和友好的语调。
                """;
        } else {
            return """
                You are an AI assistant for PSA (Port of Singapore Authority), specialized in helping users query port operation data.
                
                Your responsibilities:
                1. Understand user questions about port operations
                2. Provide accurate, professional port data information
                3. Reply in a friendly, professional tone
                4. If data is insufficient, explain and provide relevant suggestions
                
                Please reply in English with a professional and friendly tone.
                """;
        }
    }
    
    /**
     * 构建关键词提取提示词
     */
    private String buildKeywordExtractionPrompt(String language) {
        if ("zh".equals(language)) {
            return """
                请从用户问题中提取PSA相关的关键词，包括：
                
                1. PSA业务类型：berth_time（泊位时间）、carbon_savings（碳减排）、port_efficiency（港口效率）、cargo_tracking（货物追踪）、port_congestion（港口拥堵）、vessel_scheduling（船舶调度）
                2. 港口名称：新加坡港、鹿特丹港、汉堡港、上海港、洛杉矶港等
                3. 时间信息：今天、明天、本周、上午、下午等
                4. 货物ID：如#342、#123等
                5. 状态信息：已到达、运输中、延误、完成等
                
                请以JSON格式返回提取的关键词，格式如下：
                {
                    "psa_keywords": ["berth_time", "carbon_savings"],
                    "ports": ["新加坡港", "鹿特丹港"],
                    "time_keywords": ["今天", "上午"],
                    "shipment_ids": ["342", "123"],
                    "status_keywords": ["已到达", "运输中"]
                }
                """;
        } else {
            return """
                Please extract PSA-related keywords from the user's question, including:
                
                1. PSA business types: berth_time, carbon_savings, port_efficiency, cargo_tracking, port_congestion, vessel_scheduling
                2. Port names: Singapore Port, Rotterdam Port, Hamburg Port, Shanghai Port, Los Angeles Port, etc.
                3. Time information: today, tomorrow, this week, morning, afternoon, etc.
                4. Shipment IDs: such as #342, #123, etc.
                5. Status information: arrived, in transit, delayed, completed, etc.
                
                Please return the extracted keywords in JSON format as follows:
                {
                    "psa_keywords": ["berth_time", "carbon_savings"],
                    "ports": ["Singapore Port", "Rotterdam Port"],
                    "time_keywords": ["today", "morning"],
                    "shipment_ids": ["342", "123"],
                    "status_keywords": ["arrived", "in transit"]
                }
                """;
        }
    }
    
    /**
     * 构建基于数据的提示词
     */
    private String buildDataBasedPrompt(String language) {
        if ("zh".equals(language)) {
            return """
                你是PSA的AI助手，现在需要基于提供的PSA数据来回答用户问题。
                
                请：
                1. 仔细分析提供的PSA数据
                2. 根据数据内容回答用户问题
                3. 如果数据中有具体数值，请准确引用
                4. 用专业但易懂的语言解释数据
                5. 如果数据不足，请说明并提供建议
                
                请用中文回复，保持专业和友好的语调。
                """;
        } else {
            return """
                You are an AI assistant for PSA, now you need to answer user questions based on the provided PSA data.
                
                Please:
                1. Carefully analyze the provided PSA data
                2. Answer user questions based on the data content
                3. If there are specific values in the data, please quote them accurately
                4. Explain the data in professional but understandable language
                5. If data is insufficient, explain and provide suggestions
                
                Please reply in English with a professional and friendly tone.
                """;
        }
    }
    
    /**
     * 构建数据上下文
     */
    private String buildDataContext(Map<String, Object> psaData, String language) {
        StringBuilder context = new StringBuilder();
        
        if (psaData.containsKey("portName")) {
            context.append("港口：").append(psaData.get("portName")).append("\n");
        }
        
        if (psaData.containsKey("dataType")) {
            context.append("数据类型：").append(psaData.get("dataType")).append("\n");
        }
        
        if (psaData.containsKey("description")) {
            context.append("描述：").append(psaData.get("description")).append("\n");
        }
        
        if (psaData.containsKey("metrics")) {
            context.append("指标数据：\n");
            @SuppressWarnings("unchecked")
            Map<String, Object> metrics = (Map<String, Object>) psaData.get("metrics");
            metrics.forEach((key, value) -> 
                context.append("  ").append(key).append(": ").append(value).append("\n"));
        }
        
        if (psaData.containsKey("timestamp")) {
            context.append("更新时间：").append(psaData.get("timestamp")).append("\n");
        }
        
        return context.toString();
    }
    
    /**
     * 检查OpenAI服务是否可用
     */
    public boolean isServiceAvailable() {
        // 检查基本配置是否正确
        return apiKey != null && 
               !apiKey.equals("your-primary-key-here") && 
               !apiKey.equals("your-secondary-key-here") &&
               baseUrl != null && 
               !baseUrl.equals("https://your-resource.openai.azure.com") &&
               deploymentName != null &&
               !deploymentName.equals("gpt-4-1-nano");
    }
}