package com.networkinsights.ai.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.networkinsights.ai.model.PSAData;

/**
 * PSA数据查询服务
 * 根据提取的关键词查询PSA官方数据或模拟数据
 */
@Service
public class PSADataService {
    
    private static final Logger logger = LoggerFactory.getLogger(PSADataService.class);
    
    // 模拟PSA数据存储
    private final Map<String, List<PSAData>> psaDataStore;
    
    public PSADataService() {
        this.psaDataStore = initializeMockPSAData();
    }
    
    /**
     * 根据关键词查询PSA数据
     */
    public List<PSAData> queryPSAData(KeywordExtractionService.ExtractedKeywords keywords) {
        List<PSAData> results = new ArrayList<>();

        // 根据PSA关键词查询
        for (String category : keywords.getPSAKeywords().keySet()) {
            List<PSAData> categoryData = psaDataStore.get(category);
            if (categoryData != null) {
                results.addAll(categoryData);
            }
        }

        // 如果没有PSA关键词但有港口关键词，则查询所有数据然后按港口过滤
        if (results.isEmpty() && !keywords.getPortKeywords().isEmpty()) {
            results = psaDataStore.values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }

        // 根据港口关键词过滤
        if (!keywords.getPortKeywords().isEmpty()) {
            results = results.stream()
                    .filter(data -> keywords.getPortKeywords().stream()
                            .anyMatch(port -> isPortMatch(data, port)))
                    .collect(Collectors.toList());
        }
        
        // 根据货物ID过滤
        if (!keywords.getShipmentIds().isEmpty()) {
            results = results.stream()
                    .filter(data -> keywords.getShipmentIds().stream()
                            .anyMatch(id -> data.getDataId().contains(id)))
                    .collect(Collectors.toList());
        }
        
        // 根据状态关键词过滤
        if (!keywords.getStatusKeywords().isEmpty()) {
            results = results.stream()
                    .filter(data -> keywords.getStatusKeywords().stream()
                            .anyMatch(status -> data.getDescription().toLowerCase().contains(status.toLowerCase())))
                    .collect(Collectors.toList());
        }
        
        // 如果没有找到特定数据，返回相关类别的数据
        if (results.isEmpty() && !keywords.getPSAKeywords().isEmpty()) {
            String firstCategory = keywords.getPSAKeywords().keySet().iterator().next();
            List<PSAData> categoryData = psaDataStore.get(firstCategory);
            if (categoryData != null) {
                results.addAll(categoryData.subList(0, Math.min(3, categoryData.size())));
            }
        }
        
        logger.info("查询到 {} 条PSA数据", results.size());
        return results;
    }
    
    /**
     * 根据数据类型查询数据
     */
    public List<PSAData> queryByDataType(String dataType) {
        return psaDataStore.getOrDefault(dataType, new ArrayList<>());
    }
    
    /**
     * 根据港口查询数据
     */
    public List<PSAData> queryByPort(String portName) {
        return psaDataStore.values().stream()
                .flatMap(List::stream)
                .filter(data -> data.getPortName().toLowerCase().contains(portName.toLowerCase()) ||
                              data.getPortCode().toLowerCase().contains(portName.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有可用的数据类型
     */
    public Set<String> getAvailableDataTypes() {
        return psaDataStore.keySet();
    }
    
    /**
     * 获取所有港口列表
     */
    public Set<String> getAvailablePorts() {
        return psaDataStore.values().stream()
                .flatMap(List::stream)
                .map(PSAData::getPortName)
                .collect(Collectors.toSet());
    }

    /**
     * 添加解析的数据
     */
    public void addParsedData(List<PSAData> newData) {
        for (PSAData data : newData) {
            String dataType = data.getDataType();
            psaDataStore.computeIfAbsent(dataType, k -> new ArrayList<>()).add(data);
        }
        logger.info("添加了 {} 条解析的数据", newData.size());
    }

    /**
     * 根据关键词搜索数据（支持模糊搜索）
     */
    public List<PSAData> searchData(String keyword) {
        List<PSAData> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        
        for (List<PSAData> dataList : psaDataStore.values()) {
            for (PSAData data : dataList) {
                if (data.getPortName().toLowerCase().contains(lowerKeyword) ||
                    data.getDescription().toLowerCase().contains(lowerKeyword) ||
                    data.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(lowerKeyword))) {
                    results.add(data);
                }
            }
        }
        
        logger.info("关键词 '{}' 搜索到 {} 条数据", keyword, results.size());
        return results;
    }

    /**
     * 检查港口是否匹配
     */
    private boolean isPortMatch(PSAData data, String portKeyword) {
        String lowerKeyword = portKeyword.toLowerCase();
        String portName = data.getPortName().toLowerCase();
        String portCode = data.getPortCode().toLowerCase();
        
        // 直接匹配
        if (portName.contains(lowerKeyword) || portCode.contains(lowerKeyword)) {
            return true;
        }
        
        // 港口名称映射匹配
        Map<String, String> portMappings = new HashMap<>();
        portMappings.put("singapore", "新加坡港");
        portMappings.put("rotterdam", "鹿特丹港");
        portMappings.put("hamburg", "汉堡港");
        portMappings.put("shanghai", "上海港");
        portMappings.put("shenzhen", "深圳港");
        portMappings.put("tianjin", "天津港");
        portMappings.put("qingdao", "青岛港");
        portMappings.put("ningbo", "宁波港");
        portMappings.put("guangzhou", "广州港");
        portMappings.put("los angeles", "洛杉矶港");
        portMappings.put("long beach", "长滩港");
        portMappings.put("new york", "纽约港");
        portMappings.put("savannah", "萨凡纳港");
        portMappings.put("felixstowe", "费利克斯托港");
        portMappings.put("busan", "釜山港");
        portMappings.put("yokohama", "横滨港");
        portMappings.put("tokyo", "东京港");
        portMappings.put("osaka", "大阪港");
        portMappings.put("kobe", "神户港");
        portMappings.put("grn", "绿色港口");
        portMappings.put("green", "绿色港口");
        
        String mappedPort = portMappings.get(lowerKeyword);
        if (mappedPort != null && portName.contains(mappedPort)) {
            return true;
        }
        
        return false;
    }

    /**
     * 获取数据统计信息
     */
    public Map<String, Object> getDataStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDataCount", psaDataStore.values().stream().mapToInt(List::size).sum());
        stats.put("dataTypeCount", psaDataStore.size());
        stats.put("portCount", getAvailablePorts().size());
        
        Map<String, Integer> typeStats = new HashMap<>();
        psaDataStore.forEach((type, data) -> typeStats.put(type, data.size()));
        stats.put("typeStats", typeStats);
        
        return stats;
    }
    
    /**
     * 初始化模拟PSA数据
     */
    private Map<String, List<PSAData>> initializeMockPSAData() {
        Map<String, List<PSAData>> dataStore = new HashMap<>();
        
        // 泊位时间数据
        List<PSAData> berthTimeData = new ArrayList<>();
        berthTimeData.add(new PSAData("BT001", "berth_time", "新加坡港", "SGSIN", 
                       LocalDateTime.now().minusHours(2),
                       Map.of("average_berth_time", "4.2", "current_waiting", "2", "efficiency_score", "85"),
                       "新加坡港当前平均泊位时间为4.2小时，等待船舶2艘，效率评分85分", "Mock Data",
                       new ArrayList<>(Arrays.asList("efficiency", "singapore", "berth"))));
        berthTimeData.add(new PSAData("BT002", "berth_time", "鹿特丹港", "NLRTM", 
                       LocalDateTime.now().minusHours(1),
                       Map.of("average_berth_time", "3.8", "current_waiting", "1", "efficiency_score", "92"),
                       "鹿特丹港平均泊位时间3.8小时，等待船舶1艘，效率评分92分", "Mock Data",
                       new ArrayList<>(Arrays.asList("efficiency", "rotterdam", "berth"))));
        berthTimeData.add(new PSAData("BT003", "berth_time", "汉堡港", "DEHAM", 
                       LocalDateTime.now().minusMinutes(30),
                       Map.of("average_berth_time", "5.1", "current_waiting", "3", "efficiency_score", "78"),
                       "汉堡港平均泊位时间5.1小时，等待船舶3艘，效率评分78分", "Mock Data",
                       new ArrayList<>(Arrays.asList("efficiency", "hamburg", "berth"))));
        dataStore.put("berth_time", berthTimeData);
        
        // 碳减排数据
        List<PSAData> carbonData = new ArrayList<>();
        carbonData.add(new PSAData("CS001", "carbon_savings", "新加坡港", "SGSIN", 
                       LocalDateTime.now().minusHours(3),
                       Map.of("co2_reduction", "15.2", "fuel_savings", "8.5", "efficiency_gain", "12"),
                       "新加坡港本月CO2减排15.2%，燃料节省8.5%，效率提升12%", "Mock Data",
                       new ArrayList<>(Arrays.asList("carbon", "sustainability", "singapore"))));
        carbonData.add(new PSAData("CS002", "carbon_savings", "鹿特丹港", "NLRTM", 
                       LocalDateTime.now().minusHours(2),
                       Map.of("co2_reduction", "18.7", "fuel_savings", "11.2", "efficiency_gain", "15"),
                       "鹿特丹港本月CO2减排18.7%，燃料节省11.2%，效率提升15%", "Mock Data",
                       new ArrayList<>(Arrays.asList("carbon", "sustainability", "rotterdam"))));
        carbonData.add(new PSAData("CS003", "carbon_savings", "上海港", "CNSHA", 
                       LocalDateTime.now().minusHours(1),
                       Map.of("co2_reduction", "12.8", "fuel_savings", "6.9", "efficiency_gain", "9"),
                       "上海港本月CO2减排12.8%，燃料节省6.9%，效率提升9%", "Mock Data",
                       new ArrayList<>(Arrays.asList("carbon", "sustainability", "shanghai"))));
        dataStore.put("carbon_savings", carbonData);
        
        // 港口效率数据
        List<PSAData> efficiencyData = new ArrayList<>();
        efficiencyData.add(new PSAData("PE001", "port_efficiency", "新加坡港", "SGSIN", 
                       LocalDateTime.now().minusMinutes(45),
                       Map.of("throughput_per_hour", "45", "crane_utilization", "88", "berth_occupancy", "76"),
                       "新加坡港每小时吞吐量45个集装箱，起重机利用率88%，泊位占用率76%", "Mock Data",
                       new ArrayList<>(Arrays.asList("efficiency", "throughput", "singapore"))));
        efficiencyData.add(new PSAData("PE002", "port_efficiency", "洛杉矶港", "USLAX", 
                       LocalDateTime.now().minusMinutes(30),
                       Map.of("throughput_per_hour", "38", "crane_utilization", "82", "berth_occupancy", "84"),
                       "洛杉矶港每小时吞吐量38个集装箱，起重机利用率82%，泊位占用率84%", "Mock Data",
                       new ArrayList<>(Arrays.asList("efficiency", "throughput", "los_angeles"))));
        efficiencyData.add(new PSAData("PE003", "port_efficiency", "汉堡港", "DEHAM", 
                       LocalDateTime.now().minusMinutes(15),
                       Map.of("throughput_per_hour", "42", "crane_utilization", "85", "berth_occupancy", "79"),
                       "汉堡港每小时吞吐量42个集装箱，起重机利用率85%，泊位占用率79%", "Mock Data",
                       new ArrayList<>(Arrays.asList("efficiency", "throughput", "hamburg"))));
        dataStore.put("port_efficiency", efficiencyData);
        
        // 货物追踪数据
        List<PSAData> trackingData = new ArrayList<>();
        trackingData.add(new PSAData("CT001", "cargo_tracking", "新加坡港", "SGSIN", 
                       LocalDateTime.now().minusMinutes(20),
                       Map.of("shipment_id", "342", "status", "in_transit", "location", "Pacific Ocean", "eta", "2024-01-15 18:00"),
                       "货物#342正在太平洋航行中，预计1月15日18:00到达", "Mock Data",
                       new ArrayList<>(Arrays.asList("tracking", "shipment", "singapore"))));
        trackingData.add(new PSAData("CT002", "cargo_tracking", "鹿特丹港", "NLRTM", 
                       LocalDateTime.now().minusMinutes(10),
                       Map.of("shipment_id", "123", "status", "arrived", "location", "Rotterdam Port", "eta", "2024-01-14 14:30"),
                       "货物#123已到达鹿特丹港，正在清关", "Mock Data",
                       new ArrayList<>(Arrays.asList("tracking", "shipment", "rotterdam"))));
        trackingData.add(new PSAData("CT003", "cargo_tracking", "上海港", "CNSHA", 
                       LocalDateTime.now().minusMinutes(5),
                       Map.of("shipment_id", "456", "status", "loading", "location", "Shanghai Port", "eta", "2024-01-16 09:00"),
                       "货物#456正在上海港装货，预计1月16日09:00出发", "Mock Data",
                       new ArrayList<>(Arrays.asList("tracking", "shipment", "shanghai"))));
        dataStore.put("cargo_tracking", trackingData);
        
        // 港口拥堵数据
        List<PSAData> congestionData = new ArrayList<>();
        congestionData.add(new PSAData("PC001", "port_congestion", "洛杉矶港", "USLAX", 
                       LocalDateTime.now().minusMinutes(25),
                       Map.of("waiting_time", "8.5", "queue_length", "12", "congestion_level", "high"),
                       "洛杉矶港当前等待时间8.5小时，排队船舶12艘，拥堵等级：高", "Mock Data",
                       new ArrayList<>(Arrays.asList("congestion", "waiting", "los_angeles"))));
        congestionData.add(new PSAData("PC002", "port_congestion", "长滩港", "USLGB", 
                       LocalDateTime.now().minusMinutes(20),
                       Map.of("waiting_time", "6.2", "queue_length", "8", "congestion_level", "medium"),
                       "长滩港当前等待时间6.2小时，排队船舶8艘，拥堵等级：中等", "Mock Data",
                       new ArrayList<>(Arrays.asList("congestion", "waiting", "long_beach"))));
        congestionData.add(new PSAData("PC003", "port_congestion", "费利克斯托港", "GBFXT", 
                       LocalDateTime.now().minusMinutes(15),
                       Map.of("waiting_time", "3.1", "queue_length", "4", "congestion_level", "low"),
                       "费利克斯托港当前等待时间3.1小时，排队船舶4艘，拥堵等级：低", "Mock Data",
                       new ArrayList<>(Arrays.asList("congestion", "waiting", "felixstowe"))));
        dataStore.put("port_congestion", congestionData);
        
        // 船舶调度数据
        List<PSAData> schedulingData = new ArrayList<>();
        schedulingData.add(new PSAData("VS001", "vessel_scheduling", "新加坡港", "SGSIN", 
                       LocalDateTime.now().minusMinutes(40),
                       Map.of("scheduled_arrivals", "8", "scheduled_departures", "6", "next_arrival", "2024-01-14 16:30"),
                       "新加坡港今日计划到达8艘船，计划出发6艘船，下次到达时间16:30", "Mock Data",
                       new ArrayList<>(Arrays.asList("scheduling", "vessel", "singapore"))));
        schedulingData.add(new PSAData("VS002", "vessel_scheduling", "鹿特丹港", "NLRTM", 
                       LocalDateTime.now().minusMinutes(35),
                       Map.of("scheduled_arrivals", "5", "scheduled_departures", "7", "next_arrival", "2024-01-14 20:15"),
                       "鹿特丹港今日计划到达5艘船，计划出发7艘船，下次到达时间20:15", "Mock Data",
                       new ArrayList<>(Arrays.asList("scheduling", "vessel", "rotterdam"))));
        schedulingData.add(new PSAData("VS003", "vessel_scheduling", "汉堡港", "DEHAM", 
                       LocalDateTime.now().minusMinutes(30),
                       Map.of("scheduled_arrivals", "6", "scheduled_departures", "4", "next_arrival", "2024-01-14 22:45"),
                       "汉堡港今日计划到达6艘船，计划出发4艘船，下次到达时间22:45", "Mock Data",
                       new ArrayList<>(Arrays.asList("scheduling", "vessel", "hamburg"))));
        dataStore.put("vessel_scheduling", schedulingData);
        
        return dataStore;
    }
}