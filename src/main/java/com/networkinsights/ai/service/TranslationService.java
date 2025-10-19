package com.networkinsights.ai.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.Detection;
import com.networkinsights.ai.model.LanguageRequest;
import com.networkinsights.ai.model.SupportedLanguage;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 翻译服务类
 * 集成Google Cloud Translate API进行多语言翻译
 */
@Service
public class TranslationService {
    
    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);
    
    private final Translate translate;
    
    // 支持的语言列表
    private final Map<String, SupportedLanguage> supportedLanguages;
    
    public TranslationService() {
        // 初始化Google Translate客户端
        this.translate = TranslateOptions.getDefaultInstance().getService();
        
        // 初始化支持的语言
        this.supportedLanguages = initializeSupportedLanguages();
    }
    
    /**
     * 翻译文本
     */
    public String translateText(LanguageRequest request) {
        try {
            String sourceLang = request.getSourceLanguage();
            String targetLang = request.getTargetLanguage();
            String text = request.getText();
            
            // 如果启用自动检测语言
            if (request.isDetectLanguage()) {
                Detection detection = translate.detect(text);
                sourceLang = detection.getLanguage();
                logger.info("检测到语言: {}", sourceLang);
            }
            
            // 如果源语言和目标语言相同，直接返回原文
            if (sourceLang.equals(targetLang)) {
                return text;
            }
            
            // 执行翻译
            Translation translation = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage(sourceLang),
                Translate.TranslateOption.targetLanguage(targetLang)
            );
            
            return translation.getTranslatedText();
            
        } catch (Exception e) {
            logger.error("翻译失败: {}", e.getMessage(), e);
            return "翻译服务暂时不可用，请稍后重试。";
        }
    }
    
    /**
     * 检测文本语言
     */
    public String detectLanguage(String text) {
        try {
            Detection detection = translate.detect(text);
            return detection.getLanguage();
        } catch (Exception e) {
            logger.error("语言检测失败: {}", e.getMessage(), e);
            return "zh"; // 默认返回中文
        }
    }
    
    
    /**
     * 获取支持的语言列表
     */
    public List<SupportedLanguage> getSupportedLanguages() {
        return new ArrayList<>(supportedLanguages.values());
    }
    
    /**
     * 根据语言代码获取语言信息
     */
    public SupportedLanguage getLanguageByCode(String code) {
        return supportedLanguages.get(code);
    }
    
    /**
     * 初始化支持的语言列表
     */
    private Map<String, SupportedLanguage> initializeSupportedLanguages() {
        Map<String, SupportedLanguage> languages = new HashMap<>();
        
        // 添加主要语言
        languages.put("zh", new SupportedLanguage("zh", "Chinese", "中文", "🇨🇳"));
        languages.put("en", new SupportedLanguage("en", "English", "English", "🇺🇸"));
        languages.put("de", new SupportedLanguage("de", "German", "Deutsch", "🇩🇪"));
        languages.put("fr", new SupportedLanguage("fr", "French", "Français", "🇫🇷"));
        languages.put("ja", new SupportedLanguage("ja", "Japanese", "日本語", "🇯🇵"));
        languages.put("ko", new SupportedLanguage("ko", "Korean", "한국어", "🇰🇷"));
        languages.put("es", new SupportedLanguage("es", "Spanish", "Español", "🇪🇸"));
        languages.put("it", new SupportedLanguage("it", "Italian", "Italiano", "🇮🇹"));
        languages.put("pt", new SupportedLanguage("pt", "Portuguese", "Português", "🇵🇹"));
        languages.put("ru", new SupportedLanguage("ru", "Russian", "Русский", "🇷🇺"));
        languages.put("ar", new SupportedLanguage("ar", "Arabic", "العربية", "🇸🇦"));
        languages.put("hi", new SupportedLanguage("hi", "Hindi", "हिन्दी", "🇮🇳"));
        languages.put("th", new SupportedLanguage("th", "Thai", "ไทย", "🇹🇭"));
        languages.put("vi", new SupportedLanguage("vi", "Vietnamese", "Tiếng Việt", "🇻🇳"));
        languages.put("nl", new SupportedLanguage("nl", "Dutch", "Nederlands", "🇳🇱"));
        languages.put("sv", new SupportedLanguage("sv", "Swedish", "Svenska", "🇸🇪"));
        languages.put("no", new SupportedLanguage("no", "Norwegian", "Norsk", "🇳🇴"));
        languages.put("da", new SupportedLanguage("da", "Danish", "Dansk", "🇩🇰"));
        languages.put("fi", new SupportedLanguage("fi", "Finnish", "Suomi", "🇫🇮"));
        languages.put("pl", new SupportedLanguage("pl", "Polish", "Polski", "🇵🇱"));
        languages.put("tr", new SupportedLanguage("tr", "Turkish", "Türkçe", "🇹🇷"));
        
        return languages;
    }
    
    /**
     * 检查语言是否支持
     */
    public boolean isLanguageSupported(String languageCode) {
        return supportedLanguages.containsKey(languageCode);
    }
    
}
