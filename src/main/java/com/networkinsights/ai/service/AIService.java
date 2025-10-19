package com.networkinsights.ai.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.networkinsights.ai.model.ChatMessage;
import com.networkinsights.ai.model.PSAData;

/**
 * AI服务类
 * 处理智能问答、团队沟通、报告生成等功能
 */
@Service
public class AIService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    
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
    
    @Value("${app.ai.use-openai:false}")
    private boolean useOpenAI;
    
    @Value("${app.ai.use-powerbi:false}")
    private boolean usePowerBI;
    
    // AI回复模板数据
    private final Map<String, List<String>> responseTemplates;
    
    public AIService() {
        this.responseTemplates = initializeResponseTemplates();
    }
    
    /**
     * 处理用户消息并生成AI回复
     * 规则：
     * 1) 提取关键词并查询PSA数据
     * 2) 优先使用OpenAI生成智能回复
     * 3) 若OpenAI不可用，使用内置多语言模板
     * 4) 若 targetLanguage 未支持，尝试调用翻译服务；失败则回退英文模板
     */
    public ChatMessage processMessage(ChatMessage userMessage, String targetLanguage) {
        String content = userMessage.getContent();
        
        // 提取关键词
        KeywordExtractionService.ExtractedKeywords keywords = keywordExtractionService.extractKeywords(content);
        
        // 查询PSA数据（优先使用Power BI，后备使用模拟数据）
        List<PSAData> psaData = queryPSAData(keywords);
        
        // 生成AI回复
        String aiResponse = generateAIResponse(content, keywords, psaData, targetLanguage);

        ChatMessage aiMessage = new ChatMessage(aiResponse, "ai", targetLanguage);
        aiMessage.setOriginalContent(aiResponse);
        return aiMessage;
    }
    
    /**
     * 查询PSA数据（优先Power BI，后备模拟数据）
     */
    private List<PSAData> queryPSAData(KeywordExtractionService.ExtractedKeywords keywords) {
        List<PSAData> psaData = new ArrayList<>();
        
        // 优先使用Power BI
        if (usePowerBI && powerBIService.isServiceAvailable()) {
            try {
                psaData = powerBIService.queryPSADataByKeywords(keywords.getPSAKeywords());
                if (!psaData.isEmpty()) {
                    logger.info("使用Power BI数据，获取到 {} 条记录", psaData.size());
                    return psaData;
                }
            } catch (Exception e) {
                logger.warn("Power BI查询失败，使用模拟数据: {}", e.getMessage());
            }
        }
        
        // 后备使用模拟数据
        psaData = psaDataService.queryPSAData(keywords);
        logger.info("使用模拟数据，获取到 {} 条记录", psaData.size());
        return psaData;
    }
    
    /**
     * 生成AI回复
     */
    private String generateAIResponse(String userQuestion, 
                                    KeywordExtractionService.ExtractedKeywords keywords, 
                                    List<PSAData> psaData, 
                                    String targetLanguage) {
        
        // 优先使用OpenAI
        if (useOpenAI && openAIService.isServiceAvailable() && !psaData.isEmpty()) {
            try {
                // 将PSA数据转换为Map格式供OpenAI使用
                Map<String, Object> dataContext = buildDataContext(psaData);
                String aiResponse = openAIService.generateDataBasedResponse(userQuestion, dataContext, targetLanguage);
                if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                    logger.info("使用OpenAI生成回复");
                    return aiResponse;
                }
            } catch (Exception e) {
                logger.warn("OpenAI生成回复失败，使用传统方法: {}", e.getMessage());
            }
        }
        
        // 后备使用传统方法
        return generateResponseWithData(userQuestion, keywords, psaData, targetLanguage);
    }
    
    /**
     * 构建数据上下文
     */
    private Map<String, Object> buildDataContext(List<PSAData> psaData) {
        Map<String, Object> context = new HashMap<>();
        
        if (!psaData.isEmpty()) {
            PSAData latestData = psaData.get(0);
            context.put("portName", latestData.getPortName());
            context.put("dataType", latestData.getDataType());
            context.put("description", latestData.getDescription());
            context.put("timestamp", latestData.getTimestamp().toString());
            context.put("metrics", latestData.getMetrics());
            context.put("source", latestData.getSource());
        }
        
        return context;
    }
    
    /**
     * 根据关键词和PSA数据生成回复
     */
    private String generateResponseWithData(String userQuestion, 
                                          KeywordExtractionService.ExtractedKeywords keywords, 
                                          List<PSAData> psaData, 
                                          String targetLanguage) {
        
        // 如果有PSA数据，生成基于数据的回复
        if (!psaData.isEmpty()) {
            return generateDataBasedResponse(psaData, keywords, targetLanguage);
        }
        
        // 否则使用传统的关键词分类
        String category = categorize(userQuestion);
        String aiResponse = getTemplateResponse(category, targetLanguage);

        // 若返回为空，尝试翻译中文模板到目标语言
        if (aiResponse == null || aiResponse.isEmpty()) {
            String zhBase = getTemplateResponse(category, "zh");
            try {
                String translated = translationService.translateText(
                    new com.networkinsights.ai.model.LanguageRequest(zhBase, "zh", targetLanguage)
                );
                // 若翻译仍不可用，回退英文模板
                if (translated == null || translated.isBlank() || translated.contains("翻译服务暂时不可用")) {
                    String enBase = getTemplateResponse(category, "en");
                    aiResponse = (enBase == null || enBase.isBlank()) ? zhBase : enBase;
                } else {
                    aiResponse = translated;
                }
            } catch (Exception ignored) {
                String enBase = getTemplateResponse(category, "en");
                aiResponse = (enBase == null || enBase.isBlank()) ? zhBase : enBase;
            }
        }

        return aiResponse;
    }
    
    /**
     * 基于PSA数据生成回复
     */
    private String generateDataBasedResponse(List<PSAData> psaData, 
                                           KeywordExtractionService.ExtractedKeywords keywords, 
                                           String targetLanguage) {
        StringBuilder response = new StringBuilder();
        
        // 根据数据类型生成不同的回复
        Map<String, List<PSAData>> dataByType = psaData.stream()
                .collect(Collectors.groupingBy(PSAData::getDataType));
        
        for (Map.Entry<String, List<PSAData>> entry : dataByType.entrySet()) {
            String dataType = entry.getKey();
            List<PSAData> typeData = entry.getValue();
            
            switch (dataType) {
                case "berth_time":
                    response.append(generateBerthTimeResponse(typeData, targetLanguage));
                    break;
                case "carbon_savings":
                    response.append(generateCarbonSavingsResponse(typeData, targetLanguage));
                    break;
                case "port_efficiency":
                    response.append(generatePortEfficiencyResponse(typeData, targetLanguage));
                    break;
                case "cargo_tracking":
                    response.append(generateCargoTrackingResponse(typeData, targetLanguage));
                    break;
                case "port_congestion":
                    response.append(generatePortCongestionResponse(typeData, targetLanguage));
                    break;
                case "vessel_scheduling":
                    response.append(generateVesselSchedulingResponse(typeData, targetLanguage));
                    break;
                default:
                    response.append(generateGenericDataResponse(typeData, targetLanguage));
            }
            response.append("\n\n");
        }
        
        return response.toString().trim();
    }
    
    /**
     * 生成泊位时间相关回复
     */
    private String generateBerthTimeResponse(List<PSAData> data, String targetLanguage) {
        if (data.isEmpty()) return "";
        
        PSAData latestData = data.get(0);
        Map<String, Object> metrics = latestData.getMetrics();
        
        if (targetLanguage.equals("zh")) {
            return String.format("📊 %s泊位时间信息：\n" +
                    "• 平均泊位时间：%s小时\n" +
                    "• 当前等待船舶：%s艘\n" +
                    "• 效率评分：%s分\n" +
                    "• 更新时间：%s",
                    latestData.getPortName(),
                    metrics.get("average_berth_time"),
                    metrics.get("current_waiting"),
                    metrics.get("efficiency_score"),
                    latestData.getTimestamp().toString());
        } else {
            return String.format("📊 %s Berth Time Information:\n" +
                    "• Average Berth Time: %s hours\n" +
                    "• Current Waiting Vessels: %s\n" +
                    "• Efficiency Score: %s points\n" +
                    "• Last Updated: %s",
                    latestData.getPortName(),
                    metrics.get("average_berth_time"),
                    metrics.get("current_waiting"),
                    metrics.get("efficiency_score"),
                    latestData.getTimestamp().toString());
        }
    }
    
    /**
     * 生成碳减排相关回复
     */
    private String generateCarbonSavingsResponse(List<PSAData> data, String targetLanguage) {
        if (data.isEmpty()) return "";
        
        PSAData latestData = data.get(0);
        Map<String, Object> metrics = latestData.getMetrics();
        
        if (targetLanguage.equals("zh")) {
            return String.format("🌱 %s碳减排成果：\n" +
                    "• CO2减排：%s%%\n" +
                    "• 燃料节省：%s%%\n" +
                    "• 效率提升：%s%%\n" +
                    "• 更新时间：%s",
                    latestData.getPortName(),
                    metrics.get("co2_reduction"),
                    metrics.get("fuel_savings"),
                    metrics.get("efficiency_gain"),
                    latestData.getTimestamp().toString());
        } else {
            return String.format("🌱 %s Carbon Savings Achievements:\n" +
                    "• CO2 Reduction: %s%%\n" +
                    "• Fuel Savings: %s%%\n" +
                    "• Efficiency Gain: %s%%\n" +
                    "• Last Updated: %s",
                    latestData.getPortName(),
                    metrics.get("co2_reduction"),
                    metrics.get("fuel_savings"),
                    metrics.get("efficiency_gain"),
                    latestData.getTimestamp().toString());
        }
    }
    
    /**
     * 生成港口效率相关回复
     */
    private String generatePortEfficiencyResponse(List<PSAData> data, String targetLanguage) {
        if (data.isEmpty()) return "";
        
        PSAData latestData = data.get(0);
        Map<String, Object> metrics = latestData.getMetrics();
        
        if (targetLanguage.equals("zh")) {
            return String.format("⚡ %s港口效率指标：\n" +
                    "• 每小时吞吐量：%s个集装箱\n" +
                    "• 起重机利用率：%s%%\n" +
                    "• 泊位占用率：%s%%\n" +
                    "• 更新时间：%s",
                    latestData.getPortName(),
                    metrics.get("throughput_per_hour"),
                    metrics.get("crane_utilization"),
                    metrics.get("berth_occupancy"),
                    latestData.getTimestamp().toString());
        } else {
            return String.format("⚡ %s Port Efficiency Metrics:\n" +
                    "• Throughput per Hour: %s containers\n" +
                    "• Crane Utilization: %s%%\n" +
                    "• Berth Occupancy: %s%%\n" +
                    "• Last Updated: %s",
                    latestData.getPortName(),
                    metrics.get("throughput_per_hour"),
                    metrics.get("crane_utilization"),
                    metrics.get("berth_occupancy"),
                    latestData.getTimestamp().toString());
        }
    }
    
    /**
     * 生成货物追踪相关回复
     */
    private String generateCargoTrackingResponse(List<PSAData> data, String targetLanguage) {
        if (data.isEmpty()) return "";
        
        PSAData latestData = data.get(0);
        Map<String, Object> metrics = latestData.getMetrics();
        
        if (targetLanguage.equals("zh")) {
            return String.format("🚢 货物#%s追踪信息：\n" +
                    "• 状态：%s\n" +
                    "• 当前位置：%s\n" +
                    "• 预计到达：%s\n" +
                    "• 更新时间：%s",
                    metrics.get("shipment_id"),
                    metrics.get("status"),
                    metrics.get("location"),
                    metrics.get("eta"),
                    latestData.getTimestamp().toString());
        } else {
            return String.format("🚢 Shipment #%s Tracking Information:\n" +
                    "• Status: %s\n" +
                    "• Current Location: %s\n" +
                    "• ETA: %s\n" +
                    "• Last Updated: %s",
                    metrics.get("shipment_id"),
                    metrics.get("status"),
                    metrics.get("location"),
                    metrics.get("eta"),
                    latestData.getTimestamp().toString());
        }
    }
    
    /**
     * 生成港口拥堵相关回复
     */
    private String generatePortCongestionResponse(List<PSAData> data, String targetLanguage) {
        if (data.isEmpty()) return "";
        
        PSAData latestData = data.get(0);
        Map<String, Object> metrics = latestData.getMetrics();
        
        if (targetLanguage.equals("zh")) {
            return String.format("🚦 %s港口拥堵状况：\n" +
                    "• 等待时间：%s小时\n" +
                    "• 排队船舶：%s艘\n" +
                    "• 拥堵等级：%s\n" +
                    "• 更新时间：%s",
                    latestData.getPortName(),
                    metrics.get("waiting_time"),
                    metrics.get("queue_length"),
                    metrics.get("congestion_level"),
                    latestData.getTimestamp().toString());
        } else {
            return String.format("🚦 %s Port Congestion Status:\n" +
                    "• Waiting Time: %s hours\n" +
                    "• Queue Length: %s vessels\n" +
                    "• Congestion Level: %s\n" +
                    "• Last Updated: %s",
                    latestData.getPortName(),
                    metrics.get("waiting_time"),
                    metrics.get("queue_length"),
                    metrics.get("congestion_level"),
                    latestData.getTimestamp().toString());
        }
    }
    
    /**
     * 生成船舶调度相关回复
     */
    private String generateVesselSchedulingResponse(List<PSAData> data, String targetLanguage) {
        if (data.isEmpty()) return "";
        
        PSAData latestData = data.get(0);
        Map<String, Object> metrics = latestData.getMetrics();
        
        if (targetLanguage.equals("zh")) {
            return String.format("📅 %s船舶调度计划：\n" +
                    "• 计划到达：%s艘\n" +
                    "• 计划出发：%s艘\n" +
                    "• 下次到达：%s\n" +
                    "• 更新时间：%s",
                    latestData.getPortName(),
                    metrics.get("scheduled_arrivals"),
                    metrics.get("scheduled_departures"),
                    metrics.get("next_arrival"),
                    latestData.getTimestamp().toString());
        } else {
            return String.format("📅 %s Vessel Scheduling Plan:\n" +
                    "• Scheduled Arrivals: %s vessels\n" +
                    "• Scheduled Departures: %s vessels\n" +
                    "• Next Arrival: %s\n" +
                    "• Last Updated: %s",
                    latestData.getPortName(),
                    metrics.get("scheduled_arrivals"),
                    metrics.get("scheduled_departures"),
                    metrics.get("next_arrival"),
                    latestData.getTimestamp().toString());
        }
    }
    
    /**
     * 生成通用数据回复
     */
    private String generateGenericDataResponse(List<PSAData> data, String targetLanguage) {
        if (data.isEmpty()) return "";
        
        PSAData latestData = data.get(0);
        
        if (targetLanguage.equals("zh")) {
            return String.format("📈 %s数据信息：\n%s\n更新时间：%s",
                    latestData.getPortName(),
                    latestData.getDescription(),
                    latestData.getTimestamp().toString());
        } else {
            return String.format("📈 %s Data Information:\n%s\nLast Updated: %s",
                    latestData.getPortName(),
                    latestData.getDescription(),
                    latestData.getTimestamp().toString());
        }
    }
    
    /** 识别消息类别 */
    private String categorize(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        if (containsKeywords(lowerMessage, Arrays.asList("货物", "shipment", "物流", "cargo", "freight"))) {
            return "货物";
        } else if (containsKeywords(lowerMessage, Arrays.asList("联系", "contact", "团队", "team", "谁", "who"))) {
            return "联系";
        } else if (containsKeywords(lowerMessage, Arrays.asList("报告", "report", "总结", "summary", "状态", "status"))) {
            return "报告";
        } else if (containsKeywords(lowerMessage, Arrays.asList("翻译", "translate", "语言", "language"))) {
            return "翻译";
        } else {
            return "默认";
        }
    }
    
    /**
     * 检查消息是否包含关键词
     */
    private boolean containsKeywords(String message, List<String> keywords) {
        return keywords.stream().anyMatch(message::contains);
    }
    
    /**
     * 多语言模板优先：支持 zh / en / de
     */
    private String getTemplateResponse(String category, String language) {
        List<String> baseZh = responseTemplates.get(category);
        if (baseZh == null || baseZh.isEmpty()) baseZh = responseTemplates.get("默认");
        String zhPick = baseZh.get(ThreadLocalRandom.current().nextInt(baseZh.size()));

        switch (language) {
            case "zh":
                return zhPick;
            case "en":
                return translateToEnglish(zhPick);
            case "de":
                return translateToGerman(zhPick);
            default:
                return null; // 交由上层尝试Google翻译或英文回退
        }
    }
    
    /**
     * 根据语言调整回复内容
     */
    private String adjustResponseForLanguage(String response, String language) {
        // 这里可以根据不同语言调整回复格式
        switch (language) {
            case "zh":
                return response; // 中文回复
            case "en":
                return translateToEnglish(response);
            case "de":
                return translateToGerman(response);
            default:
                return response;
        }
    }
    
    /**
     * 翻译为英文（简化版本，实际应该调用翻译服务）
     */
    private String translateToEnglish(String response) {
        // 这里应该调用翻译服务，为了演示使用简单的映射
        Map<String, String> translations = new HashMap<>();
        translations.put("货物#342目前在新加坡港口，预计今晚9点启航。", 
                        "Shipment #342 is currently at Singapore port, scheduled to depart at 9 PM tonight.");
        translations.put("欧洲网络问题请联系Anna，她在法兰克福团队。", 
                        "For Europe network issues, please contact Anna from the Frankfurt team.");
        
        return translations.getOrDefault(response, response);
    }
    
    /**
     * 翻译为德文（简化版本）
     */
    private String translateToGerman(String response) {
        Map<String, String> translations = new HashMap<>();
        translations.put("货物#342目前在新加坡港口，预计今晚9点启航。", 
                        "Sendung #342 befindet sich derzeit im Hafen von Singapur und soll heute Abend um 21 Uhr abfahren.");
        translations.put("欧洲网络问题请联系Anna，她在法兰克福团队。", 
                        "Für Europa-Netzwerkprobleme wenden Sie sich bitte an Anna aus dem Frankfurter Team.");
        
        return translations.getOrDefault(response, response);
    }
    
    /**
     * 初始化回复模板
     */
    private Map<String, List<String>> initializeResponseTemplates() {
        Map<String, List<String>> templates = new HashMap<>();
        
        // 货物相关回复
        templates.put("货物", Arrays.asList(
            "货物#342目前在新加坡港口，预计今晚9点启航。",
            "货物#123已到达德国汉堡港，正在清关。",
            "货物#456目前在太平洋上，预计3天后到达洛杉矶。",
            "货物#789已从上海港出发，正在前往鹿特丹。",
            "货物#101目前在印度洋，预计5天后到达孟买。"
        ));
        
        // 团队联系回复
        templates.put("联系", Arrays.asList(
            "欧洲网络问题请联系Anna，她在法兰克福团队。我可以帮你发送消息给她。",
            "亚太地区物流问题请联系David，他在新加坡办公室。",
            "北美技术支持请联系Sarah，她在纽约团队。",
            "系统维护问题请联系Mike，他是我们的技术主管。",
            "财务相关问题请联系Lisa，她在伦敦财务部门。"
        ));
        
        // 报告相关回复
        templates.put("报告", Arrays.asList(
            "今天的全球协调报告：\n• 亚太地区延误率：5%\n• 欧洲地区沟通频率：+15%\n• 北美地区系统稳定性：99.8%\n• 建议关注：德国港口拥堵情况",
            "本周协调总结：\n• 全球项目完成率：92%\n• 跨时区沟通效率提升：20%\n• 语言翻译使用率：85%\n• 需要改进：时区协调会议安排",
            "实时状态更新：\n• 在线团队：45个\n• 活跃项目：128个\n• 待处理协调任务：12个\n• 系统负载：正常"
        ));
        
        // 翻译相关回复
        templates.put("翻译", Arrays.asList(
            "已为你翻译：\n英文：\"Good morning, how is the project going?\"\n中文：\"早上好，项目进展如何？\"",
            "语言检测：检测到德语消息\n翻译结果：\n\"Guten Morgen, wie läuft das Projekt?\"\n\"早上好，项目进展如何？\"",
            "多语言支持：已为你的团队启用中文、英文、德文、法文、日文翻译功能。"
        ));
        
        // 默认回复
        templates.put("默认", Arrays.asList(
            "我理解你的问题。让我为你查找相关信息...",
            "这是一个很好的问题！让我帮你分析一下。",
            "我正在处理你的请求，请稍等片刻。",
            "让我为你提供最准确的信息。",
            "感谢你的提问，我正在为你准备详细的回答。"
        ));
        
        return templates;
    }
}
