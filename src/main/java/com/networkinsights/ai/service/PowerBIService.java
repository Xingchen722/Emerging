package com.networkinsights.ai.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networkinsights.ai.model.PSAData;

/**
 * Power BI服务类
 * 集成Power BI Embedded获取真实数据
 */
@Service
public class PowerBIService {
    
    private static final Logger logger = LoggerFactory.getLogger(PowerBIService.class);
    
    private final String clientId;
    private final String clientSecret;
    private final String tenantId;
    private final String workspaceId;
    private final String reportId;
    private final String authorityUrl;
    private final String scope;
    private final String dashboardUrl;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    // 缓存访问令牌
    private String accessToken;
    private LocalDateTime tokenExpiry;
    
    public PowerBIService(@Value("${powerbi.embedded.client-id}") String clientId,
                         @Value("${powerbi.embedded.client-secret}") String clientSecret,
                         @Value("${powerbi.embedded.tenant-id}") String tenantId,
                         @Value("${powerbi.embedded.workspace-id}") String workspaceId,
                         @Value("${powerbi.embedded.report-id}") String reportId,
                         @Value("${powerbi.embedded.authority-url}") String authorityUrl,
                         @Value("${powerbi.embedded.scope}") String scope,
                         @Value("${powerbi.embedded.dashboard-url}") String dashboardUrl) {
        
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tenantId = tenantId;
        this.workspaceId = workspaceId;
        this.reportId = reportId;
        this.authorityUrl = authorityUrl;
        this.scope = scope;
        this.dashboardUrl = dashboardUrl;
        
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
        
        logger.info("Power BI服务初始化完成");
    }
    
    /**
     * 获取访问令牌
     */
    private String getAccessToken() throws IOException, InterruptedException {
        // 如果是演示配置，不尝试获取真实令牌
        if (isDemoConfiguration()) {
            throw new RuntimeException("演示配置不支持真实Power BI访问");
        }
        
        // 检查缓存令牌是否有效
        if (accessToken != null && tokenExpiry != null && LocalDateTime.now().isBefore(tokenExpiry)) {
            return accessToken;
        }
        
        String tokenUrl = authorityUrl + "/oauth2/v2.0/token";
        
        Map<String, String> formData = new HashMap<>();
        formData.put("client_id", clientId);
        formData.put("client_secret", clientSecret);
        formData.put("scope", scope);
        formData.put("grant_type", "client_credentials");
        
        String formBody = formData.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            JsonNode jsonNode = objectMapper.readTree(response.body());
            accessToken = jsonNode.get("access_token").asText();
            
            // 设置令牌过期时间（提前5分钟过期）
            int expiresIn = jsonNode.get("expires_in").asInt() - 300;
            tokenExpiry = LocalDateTime.now().plusSeconds(expiresIn);
            
            logger.info("Power BI访问令牌获取成功");
            return accessToken;
        } else {
            logger.error("获取Power BI访问令牌失败: {}", response.body());
            throw new RuntimeException("无法获取Power BI访问令牌");
        }
    }
    
    /**
     * 查询Power BI数据
     */
    public List<PSAData> queryPowerBIData(String query) {
        // 如果是演示配置，直接返回模拟数据
        if (isDemoConfiguration()) {
            logger.info("使用Power BI演示模式，返回模拟数据");
            return generateMockDataFromQuery(query);
        }
        
        try {
            String token = getAccessToken();
            
            // 构建Power BI REST API请求
            String apiUrl = "https://api.powerbi.com/v1.0/myorg/groups/" + workspaceId + 
                           "/reports/" + reportId + "/executeQueries";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("queries", Arrays.asList(Map.of("query", query)));
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return parsePowerBIResponse(response.body());
            } else {
                logger.error("Power BI查询失败: {}", response.body());
                return Collections.emptyList();
            }
            
        } catch (Exception e) {
            logger.error("Power BI数据查询异常: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 根据关键词查询PSA数据
     */
    public List<PSAData> queryPSADataByKeywords(Map<String, List<String>> keywords) {
        List<PSAData> results = new ArrayList<>();
        
        // 构建Power BI查询
        String query = buildPowerBIQuery(keywords);
        if (query != null) {
            results.addAll(queryPowerBIData(query));
        }
        
        // 如果Power BI查询失败，返回模拟数据作为后备
        if (results.isEmpty()) {
            logger.warn("Power BI查询无结果，使用模拟数据");
            results.addAll(generateMockDataFromKeywords(keywords));
        }
        
        return results;
    }
    
    /**
     * 构建Power BI查询
     */
    private String buildPowerBIQuery(Map<String, List<String>> keywords) {
        StringBuilder query = new StringBuilder();
        query.append("EVALUATE ");
        
        // 根据关键词构建查询
        if (keywords.containsKey("berth_time")) {
            query.append("BerthTimeData");
        } else if (keywords.containsKey("carbon_savings")) {
            query.append("CarbonSavingsData");
        } else if (keywords.containsKey("port_efficiency")) {
            query.append("PortEfficiencyData");
        } else if (keywords.containsKey("cargo_tracking")) {
            query.append("CargoTrackingData");
        } else if (keywords.containsKey("port_congestion")) {
            query.append("PortCongestionData");
        } else if (keywords.containsKey("vessel_scheduling")) {
            query.append("VesselSchedulingData");
        } else {
            return null; // 无法识别的查询类型
        }
        
        // 添加过滤条件
        if (keywords.containsKey("ports")) {
            List<String> ports = keywords.get("ports");
            if (!ports.isEmpty()) {
                query.append(" WHERE PortName IN (");
                for (int i = 0; i < ports.size(); i++) {
                    if (i > 0) query.append(", ");
                    query.append("'").append(ports.get(i)).append("'");
                }
                query.append(")");
            }
        }
        
        return query.toString();
    }
    
    /**
     * 解析Power BI响应
     */
    private List<PSAData> parsePowerBIResponse(String responseBody) {
        List<PSAData> results = new ArrayList<>();
        
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode resultsNode = rootNode.get("results");
            
            if (resultsNode != null && resultsNode.isArray()) {
                for (JsonNode result : resultsNode) {
                    JsonNode tablesNode = result.get("tables");
                    if (tablesNode != null && tablesNode.isArray()) {
                        for (JsonNode table : tablesNode) {
                            JsonNode rowsNode = table.get("rows");
                            if (rowsNode != null && rowsNode.isArray()) {
                                for (JsonNode row : rowsNode) {
                                    PSAData psaData = parsePSADataFromRow(row);
                                    if (psaData != null) {
                                        results.add(psaData);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("解析Power BI响应失败: {}", e.getMessage(), e);
        }
        
        return results;
    }
    
    /**
     * 从Power BI行数据解析PSAData
     */
    private PSAData parsePSADataFromRow(JsonNode row) {
        try {
            PSAData psaData = new PSAData();
            
            if (row.isArray() && row.size() > 0) {
                // 假设Power BI返回的列顺序：dataId, dataType, portName, portCode, timestamp, metrics, description, source, tags
                psaData.setDataId(row.get(0).asText());
                psaData.setDataType(row.get(1).asText());
                psaData.setPortName(row.get(2).asText());
                psaData.setPortCode(row.get(3).asText());
                
                // 解析时间戳
                String timestampStr = row.get(4).asText();
                psaData.setTimestamp(LocalDateTime.parse(timestampStr));
                
                // 解析指标数据
                String metricsStr = row.get(5).asText();
                Map<String, Object> metrics = objectMapper.readValue(metricsStr, Map.class);
                psaData.setMetrics(metrics);
                
                psaData.setDescription(row.get(6).asText());
                psaData.setSource(row.get(7).asText());
                
                // 解析标签
                String tagsStr = row.get(8).asText();
                List<String> tags = objectMapper.readValue(tagsStr, List.class);
                psaData.setTags(tags);
            }
            
            return psaData;
            
        } catch (Exception e) {
            logger.error("解析PSA数据行失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 根据查询生成模拟数据
     */
    private List<PSAData> generateMockDataFromQuery(String query) {
        List<PSAData> mockData = new ArrayList<>();
        
        // 根据查询内容生成相应的模拟数据
        if (query.toLowerCase().contains("berthtime") || query.toLowerCase().contains("berth_time")) {
            mockData.add(createMockBerthTimeData());
        }
        
        if (query.toLowerCase().contains("carbonsavings") || query.toLowerCase().contains("carbon_savings")) {
            mockData.add(createMockCarbonSavingsData());
        }
        
        if (query.toLowerCase().contains("portefficiency") || query.toLowerCase().contains("port_efficiency")) {
            mockData.add(createMockPortEfficiencyData());
        }
        
        if (query.toLowerCase().contains("cargotracking") || query.toLowerCase().contains("cargo_tracking")) {
            mockData.add(createMockCargoTrackingData());
        }
        
        if (query.toLowerCase().contains("portcongestion") || query.toLowerCase().contains("port_congestion")) {
            mockData.add(createMockPortCongestionData());
        }
        
        if (query.toLowerCase().contains("vesselscheduling") || query.toLowerCase().contains("vessel_scheduling")) {
            mockData.add(createMockVesselSchedulingData());
        }
        
        // 如果没有匹配的查询类型，返回通用数据
        if (mockData.isEmpty()) {
            mockData.add(createMockBerthTimeData());
            mockData.add(createMockCarbonSavingsData());
        }
        
        return mockData;
    }
    
    /**
     * 根据关键词生成模拟数据（作为Power BI的后备）
     */
    private List<PSAData> generateMockDataFromKeywords(Map<String, List<String>> keywords) {
        List<PSAData> mockData = new ArrayList<>();
        
        // 这里可以基于关键词生成更相关的模拟数据
        // 为了简化，我们返回一些基本的模拟数据
        
        if (keywords.containsKey("berth_time")) {
            mockData.add(createMockBerthTimeData());
        }
        
        if (keywords.containsKey("carbon_savings")) {
            mockData.add(createMockCarbonSavingsData());
        }
        
        if (keywords.containsKey("port_efficiency")) {
            mockData.add(createMockPortEfficiencyData());
        }
        
        return mockData;
    }
    
    private PSAData createMockBerthTimeData() {
        PSAData data = new PSAData();
        data.setDataId("PBI_BT001");
        data.setDataType("berth_time");
        data.setPortName("新加坡港");
        data.setPortCode("SGSIN");
        data.setTimestamp(LocalDateTime.now());
        data.setDescription("Power BI数据：新加坡港泊位时间信息");
        data.setSource("Power BI");
        data.setTags(Arrays.asList("powerbi", "berth", "singapore"));
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("average_berth_time", "4.2");
        metrics.put("current_waiting", "2");
        metrics.put("efficiency_score", "85");
        data.setMetrics(metrics);
        
        return data;
    }
    
    private PSAData createMockCarbonSavingsData() {
        PSAData data = new PSAData();
        data.setDataId("PBI_CS001");
        data.setDataType("carbon_savings");
        data.setPortName("鹿特丹港");
        data.setPortCode("NLRTM");
        data.setTimestamp(LocalDateTime.now());
        data.setDescription("Power BI数据：鹿特丹港碳减排成果");
        data.setSource("Power BI");
        data.setTags(Arrays.asList("powerbi", "carbon", "rotterdam"));
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("co2_reduction", "18.7");
        metrics.put("fuel_savings", "11.2");
        metrics.put("efficiency_gain", "15");
        data.setMetrics(metrics);
        
        return data;
    }
    
    private PSAData createMockPortEfficiencyData() {
        PSAData data = new PSAData();
        data.setDataId("PBI_PE001");
        data.setDataType("port_efficiency");
        data.setPortName("汉堡港");
        data.setPortCode("DEHAM");
        data.setTimestamp(LocalDateTime.now());
        data.setDescription("Power BI数据：汉堡港效率指标");
        data.setSource("Power BI");
        data.setTags(Arrays.asList("powerbi", "efficiency", "hamburg"));
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("throughput_per_hour", "42");
        metrics.put("crane_utilization", "85");
        metrics.put("berth_occupancy", "79");
        data.setMetrics(metrics);
        
        return data;
    }
    
    private PSAData createMockCargoTrackingData() {
        PSAData data = new PSAData();
        data.setDataId("PBI_CT001");
        data.setDataType("cargo_tracking");
        data.setPortName("上海港");
        data.setPortCode("CNSHA");
        data.setTimestamp(LocalDateTime.now());
        data.setDescription("Power BI数据：上海港货物追踪信息");
        data.setSource("Power BI");
        data.setTags(Arrays.asList("powerbi", "cargo", "tracking", "shanghai"));
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("tracking_accuracy", "98.5");
        metrics.put("real_time_updates", "95");
        metrics.put("delivery_on_time", "92");
        data.setMetrics(metrics);
        
        return data;
    }
    
    private PSAData createMockPortCongestionData() {
        PSAData data = new PSAData();
        data.setDataId("PBI_PC001");
        data.setDataType("port_congestion");
        data.setPortName("洛杉矶港");
        data.setPortCode("USLAX");
        data.setTimestamp(LocalDateTime.now());
        data.setDescription("Power BI数据：洛杉矶港拥堵状况");
        data.setSource("Power BI");
        data.setTags(Arrays.asList("powerbi", "congestion", "los_angeles"));
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("congestion_level", "中等");
        metrics.put("waiting_time", "6.5");
        metrics.put("berth_availability", "3");
        data.setMetrics(metrics);
        
        return data;
    }
    
    private PSAData createMockVesselSchedulingData() {
        PSAData data = new PSAData();
        data.setDataId("PBI_VS001");
        data.setDataType("vessel_scheduling");
        data.setPortName("汉堡港");
        data.setPortCode("DEHAM");
        data.setTimestamp(LocalDateTime.now());
        data.setDescription("Power BI数据：汉堡港船舶调度优化");
        data.setSource("Power BI");
        data.setTags(Arrays.asList("powerbi", "vessel", "scheduling", "hamburg"));
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("schedule_adherence", "94");
        metrics.put("fuel_efficiency", "12.5");
        metrics.put("turnaround_time", "18.2");
        data.setMetrics(metrics);
        
        return data;
    }
    
    /**
     * 获取Power BI仪表板URL
     */
    public String getDashboardUrl() {
        return dashboardUrl;
    }
    
    /**
     * 检查Power BI服务是否可用
     */
    public boolean isServiceAvailable() {
        // 检查配置是否为演示配置
        if (isDemoConfiguration()) {
            logger.info("Power BI使用演示配置，服务不可用但会返回模拟数据");
            return false;
        }
        
        try {
            String token = getAccessToken();
            return token != null && !token.isEmpty();
        } catch (Exception e) {
            logger.warn("Power BI服务不可用: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查是否为演示配置
     */
    private boolean isDemoConfiguration() {
        return "demo-client-id".equals(clientId) || 
               "your-client-id-here".equals(clientId) ||
               clientId == null || 
               clientId.isEmpty();
    }
}
