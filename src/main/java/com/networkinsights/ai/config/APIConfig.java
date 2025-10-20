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
    private AzureOpenAI azureOpenAI = new AzureOpenAI();
    
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
    
    public AzureOpenAI getAzureOpenAI() {
        return azureOpenAI;
    }
    
    public void setAzureOpenAI(AzureOpenAI azureOpenAI) {
        this.azureOpenAI = azureOpenAI;
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
    
    public static class AzureOpenAI {
        private String primaryKey;
        private String secondaryKey;
        private String endpoint;
        private String apiVersion;
        private String deploymentName;
        private String model;
        private int maxTokens = 1000;
        private double temperature = 0.7;
        
        public String getPrimaryKey() {
            return primaryKey;
        }
        
        public void setPrimaryKey(String primaryKey) {
            this.primaryKey = primaryKey;
        }
        
        public String getSecondaryKey() {
            return secondaryKey;
        }
        
        public void setSecondaryKey(String secondaryKey) {
            this.secondaryKey = secondaryKey;
        }
        
        public String getEndpoint() {
            return endpoint;
        }
        
        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
        
        public String getApiVersion() {
            return apiVersion;
        }
        
        public void setApiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
        }
        
        public String getDeploymentName() {
            return deploymentName;
        }
        
        public void setDeploymentName(String deploymentName) {
            this.deploymentName = deploymentName;
        }
        
        public String getModel() {
            return model;
        }
        
        public void setModel(String model) {
            this.model = model;
        }
        
        public int getMaxTokens() {
            return maxTokens;
        }
        
        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }
        
        public double getTemperature() {
            return temperature;
        }
        
        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }
    }
}
