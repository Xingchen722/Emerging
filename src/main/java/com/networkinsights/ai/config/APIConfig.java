package com.networkinsights.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * API配置类
 * 管理OpenAI和Power BI的配置信息
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class APIConfig {
    
    private AI ai = new AI();
    private Translation translation = new Translation();
    
    public AI getAi() {
        return ai;
    }
    
    public void setAi(AI ai) {
        this.ai = ai;
    }
    
    public Translation getTranslation() {
        return translation;
    }
    
    public void setTranslation(Translation translation) {
        this.translation = translation;
    }
    
    public static class AI {
        private boolean useOpenAI = false;
        private boolean usePowerBI = false;
        private int responseDelayMin = 1000;
        private int responseDelayMax = 3000;
        
        public boolean isUseOpenAI() {
            return useOpenAI;
        }
        
        public void setUseOpenAI(boolean useOpenAI) {
            this.useOpenAI = useOpenAI;
        }
        
        public boolean isUsePowerBI() {
            return usePowerBI;
        }
        
        public void setUsePowerBI(boolean usePowerBI) {
            this.usePowerBI = usePowerBI;
        }
        
        public int getResponseDelayMin() {
            return responseDelayMin;
        }
        
        public void setResponseDelayMin(int responseDelayMin) {
            this.responseDelayMin = responseDelayMin;
        }
        
        public int getResponseDelayMax() {
            return responseDelayMax;
        }
        
        public void setResponseDelayMax(int responseDelayMax) {
            this.responseDelayMax = responseDelayMax;
        }
    }
    
    public static class Translation {
        private String defaultSourceLanguage = "auto";
        private String defaultTargetLanguage = "zh";
        
        public String getDefaultSourceLanguage() {
            return defaultSourceLanguage;
        }
        
        public void setDefaultSourceLanguage(String defaultSourceLanguage) {
            this.defaultSourceLanguage = defaultSourceLanguage;
        }
        
        public String getDefaultTargetLanguage() {
            return defaultTargetLanguage;
        }
        
        public void setDefaultTargetLanguage(String defaultTargetLanguage) {
            this.defaultTargetLanguage = defaultTargetLanguage;
        }
    }
}
