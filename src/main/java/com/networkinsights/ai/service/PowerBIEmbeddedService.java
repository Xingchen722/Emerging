package com.networkinsights.ai.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networkinsights.ai.model.PSAData;

/**
 * Power BI Embedded服务
 * 专注于与Power BI仪表板直接交互，获取实时数据
 */
@Service
public class PowerBIEmbeddedService {
    
    private static final Logger logger = LoggerFactory.getLogger(PowerBIEmbeddedService.class);
    
    // Power BI Embedded配置
    private final String clientId;
    private final String clientSecret;
    private final String tenantId;
    private final String workspaceId;
    private final String reportId;
    private final String authorityUrl;
    private final String scope;
    private final String dashboardUrl;
    
    // Power BI API端点
    private static final String POWER_BI_BASE_URL = "https://api.powerbi.com/v1.0/myorg";
    private static final String POWER_BI_GROUPS_URL = POWER_BI_BASE_URL + "/groups";
    private static final String POWER_BI_REPORTS_URL = POWER_BI_BASE_URL + "/reports";
    private static final String POWER_BI_DATASETS_URL = POWER_BI_BASE_URL + "/datasets";
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    // 缓存管理
    private final Map<String, Object> dataCache = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> cacheTimestamps = new ConcurrentHashMap<>();
    private static final Duration CACHE_DURATION = Duration.ofMinutes(5);
    
    // 访问令牌管理
    private String accessToken;
    private LocalDateTime tokenExpiry;
    
    public PowerBIEmbeddedService(@Value("${powerbi.embedded.client-id}") String clientId,
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
        
        logger.info("Power BI Embedded服务初始化完成");
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
    
    /**
     * 获取访问令牌
     */
    private CompletableFuture<String> getAccessTokenAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 如果是演示配置，返回模拟令牌
                if (isDemoConfiguration()) {
                    logger.info("使用Power BI演示模式");
                    return "demo-access-token";
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
                    JsonNode jsonResponse = objectMapper.readTree(response.body());
                    accessToken = jsonResponse.get("access_token").asText();
                    int expiresIn = jsonResponse.get("expires_in").asInt();
                    tokenExpiry = LocalDateTime.now().plusSeconds(expiresIn - 60); // 提前1分钟过期
                    
                    logger.info("Power BI访问令牌获取成功");
                    return accessToken;
                } else {
                    logger.error("获取Power BI访问令牌失败: {}", response.body());
                    throw new RuntimeException("获取Power BI访问令牌失败: " + response.statusCode());
                }
                
            } catch (Exception e) {
                logger.error("获取Power BI访问令牌异常: {}", e.getMessage(), e);
                throw new RuntimeException("获取Power BI访问令牌异常", e);
            }
        });
    }
    
    /**
     * 获取工作区信息
     */
    public CompletableFuture<Map<String, Object>> getWorkspaceInfo() {
        return getAccessTokenAsync().thenCompose(token -> {
            if (isDemoConfiguration()) {
                return CompletableFuture.completedFuture(createDemoWorkspaceInfo());
            }
            
            return CompletableFuture.supplyAsync(() -> {
                try {
                    String url = POWER_BI_GROUPS_URL + "/" + workspaceId;
                    
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .GET()
                            .build();
                    
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    
                    if (response.statusCode() == 200) {
                        JsonNode jsonResponse = objectMapper.readTree(response.body());
                        Map<String, Object> workspaceInfo = new HashMap<>();
                        workspaceInfo.put("id", jsonResponse.get("id").asText());
                        workspaceInfo.put("name", jsonResponse.get("name").asText());
                        workspaceInfo.put("isReadOnly", jsonResponse.get("isReadOnly").asBoolean());
                        workspaceInfo.put("isOnDedicatedCapacity", jsonResponse.get("isOnDedicatedCapacity").asBoolean());
                        
                        logger.info("获取工作区信息成功: {}", workspaceInfo.get("name"));
                        return workspaceInfo;
                    } else {
                        logger.error("获取工作区信息失败: {}", response.body());
                        throw new RuntimeException("获取工作区信息失败: " + response.statusCode());
                    }
                    
                } catch (Exception e) {
                    logger.error("获取工作区信息异常: {}", e.getMessage(), e);
                    throw new RuntimeException("获取工作区信息异常", e);
                }
            });
        });
    }
    
    /**
     * 获取报告列表
     */
    public CompletableFuture<List<Map<String, Object>>> getReports() {
        return getAccessTokenAsync().thenCompose(token -> {
            if (isDemoConfiguration()) {
                return CompletableFuture.completedFuture(createDemoReports());
            }
            
            return CompletableFuture.supplyAsync(() -> {
                try {
                    String url = POWER_BI_GROUPS_URL + "/" + workspaceId + "/reports";
                    
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .GET()
                            .build();
                    
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    
                    if (response.statusCode() == 200) {
                        JsonNode jsonResponse = objectMapper.readTree(response.body());
                        List<Map<String, Object>> reports = new ArrayList<>();
                        
                        for (JsonNode report : jsonResponse.get("value")) {
                            Map<String, Object> reportInfo = new HashMap<>();
                            reportInfo.put("id", report.get("id").asText());
                            reportInfo.put("name", report.get("name").asText());
                            reportInfo.put("webUrl", report.get("webUrl").asText());
                            reportInfo.put("embedUrl", report.get("embedUrl").asText());
                            reports.add(reportInfo);
                        }
                        
                        logger.info("获取报告列表成功，共{}个报告", reports.size());
                        return reports;
                    } else {
                        logger.error("获取报告列表失败: {}", response.body());
                        throw new RuntimeException("获取报告列表失败: " + response.statusCode());
                    }
                    
                } catch (Exception e) {
                    logger.error("获取报告列表异常: {}", e.getMessage(), e);
                    throw new RuntimeException("获取报告列表异常", e);
                }
            });
        });
    }
    
    /**
     * 获取数据集信息
     */
    public CompletableFuture<List<Map<String, Object>>> getDatasets() {
        return getAccessTokenAsync().thenCompose(token -> {
            if (isDemoConfiguration()) {
                return CompletableFuture.completedFuture(createDemoDatasets());
            }
            
            return CompletableFuture.supplyAsync(() -> {
                try {
                    String url = POWER_BI_GROUPS_URL + "/" + workspaceId + "/datasets";
                    
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .GET()
                            .build();
                    
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    
                    if (response.statusCode() == 200) {
                        JsonNode jsonResponse = objectMapper.readTree(response.body());
                        List<Map<String, Object>> datasets = new ArrayList<>();
                        
                        for (JsonNode dataset : jsonResponse.get("value")) {
                            Map<String, Object> datasetInfo = new HashMap<>();
                            datasetInfo.put("id", dataset.get("id").asText());
                            datasetInfo.put("name", dataset.get("name").asText());
                            datasetInfo.put("isRefreshable", dataset.get("isRefreshable").asBoolean());
                            datasetInfo.put("isEffectiveIdentityRequired", dataset.get("isEffectiveIdentityRequired").asBoolean());
                            datasets.add(datasetInfo);
                        }
                        
                        logger.info("获取数据集列表成功，共{}个数据集", datasets.size());
                        return datasets;
                    } else {
                        logger.error("获取数据集列表失败: {}", response.body());
                        throw new RuntimeException("获取数据集列表失败: " + response.statusCode());
                    }
                    
                } catch (Exception e) {
                    logger.error("获取数据集列表异常: {}", e.getMessage(), e);
                    throw new RuntimeException("获取数据集列表异常", e);
                }
            });
        });
    }
    
    /**
     * 执行DAX查询获取实时数据
     */
    public CompletableFuture<List<PSAData>> executeDAXQuery(String daxQuery) {
        return getAccessTokenAsync().thenCompose(token -> {
            if (isDemoConfiguration()) {
                return CompletableFuture.completedFuture(createMockDataFromDAXQuery(daxQuery));
            }
            
            return CompletableFuture.supplyAsync(() -> {
                try {
                    // 首先获取数据集ID
                    String datasetsUrl = POWER_BI_GROUPS_URL + "/" + workspaceId + "/datasets";
                    
                    HttpRequest datasetsRequest = HttpRequest.newBuilder()
                            .uri(URI.create(datasetsUrl))
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .GET()
                            .build();
                    
                    HttpResponse<String> datasetsResponse = httpClient.send(datasetsRequest, HttpResponse.BodyHandlers.ofString());
                    
                    if (datasetsResponse.statusCode() != 200) {
                        throw new RuntimeException("获取数据集失败: " + datasetsResponse.statusCode());
                    }
                    
                    JsonNode datasetsJson = objectMapper.readTree(datasetsResponse.body());
                    String datasetId = datasetsJson.get("value").get(0).get("id").asText();
                    
                    // 执行DAX查询
                    String queryUrl = POWER_BI_DATASETS_URL + "/" + datasetId + "/executeQueries";
                    
                    Map<String, Object> queryRequest = new HashMap<>();
                    queryRequest.put("queries", List.of(Map.of("query", daxQuery)));
                    
                    String requestBody = objectMapper.writeValueAsString(queryRequest);
                    
                    HttpRequest queryHttpRequest = HttpRequest.newBuilder()
                            .uri(URI.create(queryUrl))
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                            .build();
                    
                    HttpResponse<String> queryResponse = httpClient.send(queryHttpRequest, HttpResponse.BodyHandlers.ofString());
                    
                    if (queryResponse.statusCode() == 200) {
                        JsonNode queryJson = objectMapper.readTree(queryResponse.body());
                        List<PSAData> results = parseDAXQueryResults(queryJson);
                        
                        logger.info("DAX查询执行成功，返回{}条数据", results.size());
                        return results;
                    } else {
                        logger.error("DAX查询执行失败: {}", queryResponse.body());
                        throw new RuntimeException("DAX查询执行失败: " + queryResponse.statusCode());
                    }
                    
                } catch (Exception e) {
                    logger.error("执行DAX查询异常: {}", e.getMessage(), e);
                    throw new RuntimeException("执行DAX查询异常", e);
                }
            });
        });
    }
    
    /**
     * 获取实时港口运营数据
     */
    public CompletableFuture<List<PSAData>> getRealTimePortData(String portCode, String dataType) {
        String cacheKey = "port_data_" + portCode + "_" + dataType;
        
        // 检查缓存
        if (dataCache.containsKey(cacheKey)) {
            LocalDateTime cacheTime = cacheTimestamps.get(cacheKey);
            if (cacheTime != null && LocalDateTime.now().isBefore(cacheTime.plus(CACHE_DURATION))) {
                logger.info("从缓存获取港口数据: {}", cacheKey);
                return CompletableFuture.completedFuture((List<PSAData>) dataCache.get(cacheKey));
            }
        }
        
        return executeDAXQuery(buildPortDataQuery(portCode, dataType))
                .thenApply(data -> {
                    // 缓存数据
                    dataCache.put(cacheKey, data);
                    cacheTimestamps.put(cacheKey, LocalDateTime.now());
                    return data;
                });
    }
    
    /**
     * 获取实时船舶调度数据
     */
    public CompletableFuture<List<PSAData>> getRealTimeVesselData() {
        String cacheKey = "vessel_data";
        
        // 检查缓存
        if (dataCache.containsKey(cacheKey)) {
            LocalDateTime cacheTime = cacheTimestamps.get(cacheKey);
            if (cacheTime != null && LocalDateTime.now().isBefore(cacheTime.plus(CACHE_DURATION))) {
                logger.info("从缓存获取船舶数据");
                return CompletableFuture.completedFuture((List<PSAData>) dataCache.get(cacheKey));
            }
        }
        
        String daxQuery = """
            EVALUATE
            SUMMARIZECOLUMNS(
                'VesselData'[VesselName],
                'VesselData'[PortCode],
                'VesselData'[ArrivalTime],
                'VesselData'[DepartureTime],
                'VesselData'[BerthTime],
                'VesselData'[Status],
                'VesselData'[Efficiency]
            )
            """;
        
        return executeDAXQuery(daxQuery)
                .thenApply(data -> {
                    // 缓存数据
                    dataCache.put(cacheKey, data);
                    cacheTimestamps.put(cacheKey, LocalDateTime.now());
                    return data;
                });
    }
    
    /**
     * 获取实时碳减排数据
     */
    public CompletableFuture<List<PSAData>> getRealTimeCarbonData() {
        String cacheKey = "carbon_data";
        
        // 检查缓存
        if (dataCache.containsKey(cacheKey)) {
            LocalDateTime cacheTime = cacheTimestamps.get(cacheKey);
            if (cacheTime != null && LocalDateTime.now().isBefore(cacheTime.plus(CACHE_DURATION))) {
                logger.info("从缓存获取碳减排数据");
                return CompletableFuture.completedFuture((List<PSAData>) dataCache.get(cacheKey));
            }
        }
        
        String daxQuery = """
            EVALUATE
            SUMMARIZECOLUMNS(
                'CarbonData'[PortCode],
                'CarbonData'[Date],
                'CarbonData'[CO2Reduction],
                'CarbonData'[FuelSavings],
                'CarbonData'[EfficiencyGain]
            )
            """;
        
        return executeDAXQuery(daxQuery)
                .thenApply(data -> {
                    // 缓存数据
                    dataCache.put(cacheKey, data);
                    cacheTimestamps.put(cacheKey, LocalDateTime.now());
                    return data;
                });
    }
    
    /**
     * 构建港口数据查询
     */
    private String buildPortDataQuery(String portCode, String dataType) {
        switch (dataType.toLowerCase()) {
            case "berth_time":
                return String.format("""
                    EVALUATE
                    FILTER(
                        SUMMARIZECOLUMNS(
                            'PortData'[PortCode],
                            'PortData'[BerthTime],
                            'PortData'[Efficiency],
                            'PortData'[WaitingVessels]
                        ),
                        'PortData'[PortCode] = "%s"
                    )
                    """, portCode);
            
            case "port_efficiency":
                return String.format("""
                    EVALUATE
                    FILTER(
                        SUMMARIZECOLUMNS(
                            'PortData'[PortCode],
                            'PortData'[Throughput],
                            'PortData'[CraneUtilization],
                            'PortData'[BerthOccupancy]
                        ),
                        'PortData'[PortCode] = "%s"
                    )
                    """, portCode);
            
            case "carbon_savings":
                return String.format("""
                    EVALUATE
                    FILTER(
                        SUMMARIZECOLUMNS(
                            'PortData'[PortCode],
                            'PortData'[CO2Reduction],
                            'PortData'[FuelSavings],
                            'PortData'[EfficiencyGain]
                        ),
                        'PortData'[PortCode] = "%s"
                    )
                    """, portCode);
            
            default:
                return String.format("""
                    EVALUATE
                    FILTER(
                        'PortData',
                        'PortData'[PortCode] = "%s"
                    )
                    """, portCode);
        }
    }
    
    /**
     * 解析DAX查询结果
     */
    private List<PSAData> parseDAXQueryResults(JsonNode queryJson) {
        List<PSAData> results = new ArrayList<>();
        
        try {
            JsonNode resultsArray = queryJson.get("results").get(0).get("tables").get(0).get("rows");
            
            for (JsonNode row : resultsArray) {
                PSAData data = new PSAData();
                data.setDataId("PBI_" + System.currentTimeMillis() + "_" + results.size());
                data.setTimestamp(LocalDateTime.now());
                data.setSource("Power BI Embedded");
                
                Map<String, Object> metrics = new HashMap<>();
                
                // 解析行数据
                JsonNode values = row.get("values");
                for (int i = 0; i < values.size(); i++) {
                    String value = values.get(i).asText();
                    metrics.put("column_" + i, value);
                }
                
                data.setMetrics(metrics);
                results.add(data);
            }
            
        } catch (Exception e) {
            logger.error("解析DAX查询结果异常: {}", e.getMessage(), e);
        }
        
        return results;
    }
    
    /**
     * 创建演示工作区信息
     */
    private Map<String, Object> createDemoWorkspaceInfo() {
        Map<String, Object> workspaceInfo = new HashMap<>();
        workspaceInfo.put("id", "demo-workspace-id");
        workspaceInfo.put("name", "PSA港口运营演示工作区");
        workspaceInfo.put("isReadOnly", false);
        workspaceInfo.put("isOnDedicatedCapacity", true);
        return workspaceInfo;
    }
    
    /**
     * 创建演示报告列表
     */
    private List<Map<String, Object>> createDemoReports() {
        List<Map<String, Object>> reports = new ArrayList<>();
        
        Map<String, Object> report1 = new HashMap<>();
        report1.put("id", "demo-report-1");
        report1.put("name", "港口运营效率报告");
        report1.put("webUrl", "https://demo-dashboard-url.com/report1");
        report1.put("embedUrl", "https://demo-dashboard-url.com/embed/report1");
        reports.add(report1);
        
        Map<String, Object> report2 = new HashMap<>();
        report2.put("id", "demo-report-2");
        report2.put("name", "船舶调度分析报告");
        report2.put("webUrl", "https://demo-dashboard-url.com/report2");
        report2.put("embedUrl", "https://demo-dashboard-url.com/embed/report2");
        reports.add(report2);
        
        return reports;
    }
    
    /**
     * 创建演示数据集列表
     */
    private List<Map<String, Object>> createDemoDatasets() {
        List<Map<String, Object>> datasets = new ArrayList<>();
        
        Map<String, Object> dataset1 = new HashMap<>();
        dataset1.put("id", "demo-dataset-1");
        dataset1.put("name", "港口运营数据集");
        dataset1.put("isRefreshable", true);
        dataset1.put("isEffectiveIdentityRequired", false);
        datasets.add(dataset1);
        
        Map<String, Object> dataset2 = new HashMap<>();
        dataset2.put("id", "demo-dataset-2");
        dataset2.put("name", "船舶调度数据集");
        dataset2.put("isRefreshable", true);
        dataset2.put("isEffectiveIdentityRequired", false);
        datasets.add(dataset2);
        
        return datasets;
    }
    
    /**
     * 根据DAX查询创建模拟数据
     */
    private List<PSAData> createMockDataFromDAXQuery(String daxQuery) {
        List<PSAData> mockData = new ArrayList<>();
        
        // 根据查询内容生成相应的模拟数据
        if (daxQuery.toLowerCase().contains("vessel")) {
            mockData.addAll(createMockVesselData());
        } else if (daxQuery.toLowerCase().contains("carbon")) {
            mockData.addAll(createMockCarbonData());
        } else {
            mockData.addAll(createMockPortData());
        }
        
        return mockData;
    }
    
    /**
     * 创建模拟港口数据
     */
    private List<PSAData> createMockPortData() {
        List<PSAData> data = new ArrayList<>();
        
        String[] ports = {"SGSIN", "NLRTM", "DEHAM", "USLAX", "CNSHA"};
        String[] portNames = {"新加坡港", "鹿特丹港", "汉堡港", "洛杉矶港", "上海港"};
        
        for (int i = 0; i < ports.length; i++) {
            PSAData portData = new PSAData();
            portData.setDataId("PBI_PORT_" + ports[i] + "_" + System.currentTimeMillis());
            portData.setPortCode(ports[i]);
            portData.setPortName(portNames[i]);
            portData.setDataType("port_efficiency");
            portData.setTimestamp(LocalDateTime.now());
            portData.setSource("Power BI Embedded (演示)");
            
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("吞吐量", (i + 1) * 100 + " 集装箱/小时");
            metrics.put("起重机利用率", (85 + i * 2) + "%");
            metrics.put("泊位占用率", (75 + i * 3) + "%");
            metrics.put("效率评分", (80 + i * 4) + "分");
            
            portData.setMetrics(metrics);
            data.add(portData);
        }
        
        return data;
    }
    
    /**
     * 创建模拟船舶数据
     */
    private List<PSAData> createMockVesselData() {
        List<PSAData> data = new ArrayList<>();
        
        String[] vessels = {"MV RAPID VOYAGER", "MV SOUTHERN SEAWAY", "MV WESTERN AURORA"};
        String[] ports = {"SGSIN", "NLRTM", "DEHAM"};
        
        for (int i = 0; i < vessels.length; i++) {
            PSAData vesselData = new PSAData();
            vesselData.setDataId("PBI_VESSEL_" + (i + 1) + "_" + System.currentTimeMillis());
            vesselData.setPortCode(ports[i]);
            vesselData.setDataType("vessel_scheduling");
            vesselData.setTimestamp(LocalDateTime.now());
            vesselData.setSource("Power BI Embedded (演示)");
            
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("船舶名称", vessels[i]);
            metrics.put("泊位时间", (3.5 + i * 0.5) + "小时");
            metrics.put("等待时间", (1.2 + i * 0.3) + "小时");
            metrics.put("燃料节省", "$" + (15000 + i * 5000));
            metrics.put("碳减排", (0.2 + i * 0.1) + "吨");
            
            vesselData.setMetrics(metrics);
            data.add(vesselData);
        }
        
        return data;
    }
    
    /**
     * 创建模拟碳减排数据
     */
    private List<PSAData> createMockCarbonData() {
        List<PSAData> data = new ArrayList<>();
        
        String[] ports = {"SGSIN", "NLRTM", "DEHAM"};
        String[] portNames = {"新加坡港", "鹿特丹港", "汉堡港"};
        
        for (int i = 0; i < ports.length; i++) {
            PSAData carbonData = new PSAData();
            carbonData.setDataId("PBI_CARBON_" + ports[i] + "_" + System.currentTimeMillis());
            carbonData.setPortCode(ports[i]);
            carbonData.setPortName(portNames[i]);
            carbonData.setDataType("carbon_savings");
            carbonData.setTimestamp(LocalDateTime.now());
            carbonData.setSource("Power BI Embedded (演示)");
            
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("CO2减排率", (15.2 + i * 2.5) + "%");
            metrics.put("燃料节省率", (8.5 + i * 1.5) + "%");
            metrics.put("效率提升率", (12 + i * 2) + "%");
            metrics.put("月度减排量", (150 + i * 25) + "吨");
            
            carbonData.setMetrics(metrics);
            data.add(carbonData);
        }
        
        return data;
    }
    
    /**
     * 清理过期缓存
     */
    public void cleanExpiredCache() {
        LocalDateTime now = LocalDateTime.now();
        cacheTimestamps.entrySet().removeIf(entry -> 
            now.isAfter(entry.getValue().plus(CACHE_DURATION)));
        
        dataCache.entrySet().removeIf(entry -> 
            !cacheTimestamps.containsKey(entry.getKey()));
        
        logger.info("清理过期缓存完成，当前缓存大小: {}", dataCache.size());
    }
    
    /**
     * 获取缓存统计信息
     */
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("缓存大小", dataCache.size());
        stats.put("缓存键", dataCache.keySet());
        stats.put("缓存时间戳", cacheTimestamps);
        return stats;
    }
}
