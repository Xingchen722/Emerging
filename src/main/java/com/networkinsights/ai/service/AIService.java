package com.networkinsights.ai.service;

import com.networkinsights.ai.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * AI服务类
 * 处理智能问答、团队沟通、报告生成等功能
 */
@Service
public class AIService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    
    @Autowired
    private TranslationService translationService;
    
    // AI回复模板数据
    private final Map<String, List<String>> responseTemplates;
    
    public AIService() {
        this.responseTemplates = initializeResponseTemplates();
    }
    
    /**
     * 处理用户消息并生成AI回复
     * 规则：
     * 1) 优先使用内置多语言模板返回 targetLanguage 文本
     * 2) 若 targetLanguage 未支持，尝试调用翻译服务；失败则回退英文模板
     */
    public ChatMessage processMessage(ChatMessage userMessage, String targetLanguage) {
        String content = userMessage.getContent();
        String category = categorize(content);

        // 优先使用内置模板
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

        ChatMessage aiMessage = new ChatMessage(aiResponse, "ai", targetLanguage);
        aiMessage.setOriginalContent(aiResponse);
        return aiMessage;
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
