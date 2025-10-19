package com.networkinsights.ai.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * PSA数据模型
 * 用于存储从Power BI或官方数据源获取的PSA相关数据
 */
public class PSAData {
    
    private String dataId;
    private String dataType; // berth_time, carbon_savings, port_efficiency, etc.
    private String portName;
    private String portCode;
    private LocalDateTime timestamp;
    private Map<String, Object> metrics;
    private String description;
    private String source; // Power BI, API, Mock Data
    private List<String> tags;
    
    public PSAData() {}
    
    public PSAData(String dataId, String dataType, String portName, String portCode, 
                   LocalDateTime timestamp, Map<String, Object> metrics, 
                   String description, String source, List<String> tags) {
        this.dataId = dataId;
        this.dataType = dataType;
        this.portName = portName;
        this.portCode = portCode;
        this.timestamp = timestamp;
        this.metrics = metrics;
        this.description = description;
        this.source = source;
        this.tags = tags;
    }
    
    // Getters and Setters
    public String getDataId() { return dataId; }
    public void setDataId(String dataId) { this.dataId = dataId; }
    
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    
    public String getPortName() { return portName; }
    public void setPortName(String portName) { this.portName = portName; }
    
    public String getPortCode() { return portCode; }
    public void setPortCode(String portCode) { this.portCode = portCode; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Map<String, Object> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    @Override
    public String toString() {
        return "PSAData{" +
                "dataId='" + dataId + '\'' +
                ", dataType='" + dataType + '\'' +
                ", portName='" + portName + '\'' +
                ", portCode='" + portCode + '\'' +
                ", timestamp=" + timestamp +
                ", metrics=" + metrics +
                ", description='" + description + '\'' +
                ", source='" + source + '\'' +
                ", tags=" + tags +
                '}';
    }
}
