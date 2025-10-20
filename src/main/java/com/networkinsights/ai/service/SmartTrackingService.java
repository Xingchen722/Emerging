package com.networkinsights.ai.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.networkinsights.ai.model.CargoTracking;

import jakarta.annotation.PostConstruct;

/**
 * Smart Tracking服务
 * 处理货物、路线和ETA的自然语言查询
 */
@Service
public class SmartTrackingService {

    private static final Logger logger = LoggerFactory.getLogger(SmartTrackingService.class);

    // 模拟货物追踪数据
    private Map<String, CargoTracking> cargoTrackingData = new HashMap<>();
    
    // 关键词模式
    private static final Pattern SHIPMENT_ID_PATTERN = Pattern.compile("#?(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern VESSEL_PATTERN = Pattern.compile("vessel\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern PORT_PATTERN = Pattern.compile("(singapore|rotterdam|hamburg|antwerp|shanghai|shenzhen|tianjin|qingdao|ningbo|guangzhou|los angeles|long beach|new york|savannah|felixstowe|busan|yokohama|tokyo|osaka|kobe)", Pattern.CASE_INSENSITIVE);
    
    @PostConstruct
    public void init() {
        logger.info("初始化Smart Tracking服务...");
        initializeMockData();
        logger.info("Smart Tracking服务初始化完成，共{}条货物追踪数据", cargoTrackingData.size());
    }
    
    /**
     * 初始化模拟数据
     */
    private void initializeMockData() {
        // 货物 #342
        CargoTracking cargo342 = new CargoTracking("#342", "Electronics", "MSC LORETO");
        cargo342.setVesselCode("MSC001");
        cargo342.setCurrentLocation("Pacific Ocean");
        cargo342.setCurrentPort("Singapore");
        cargo342.setCurrentPortCode("SGSIN");
        cargo342.setDestinationPort("Los Angeles");
        cargo342.setDestinationPortCode("USLAX");
        cargo342.setRoute("Singapore → Pacific Ocean → Los Angeles");
        cargo342.setEstimatedArrival(LocalDateTime.now().plusDays(5));
        cargo342.setStatus("In Transit");
        cargo342.setTrackingStatus("On Time");
        cargo342.getAdditionalInfo().put("Container Count", "45");
        cargo342.getAdditionalInfo().put("Weight", "2,500 tons");
        cargo342.getAdditionalInfo().put("Last Update", "2 hours ago");
        cargoTrackingData.put("342", cargo342);
        
        // 货物 #156
        CargoTracking cargo156 = new CargoTracking("#156", "Automotive Parts", "EVER GIVEN");
        cargo156.setVesselCode("EVER001");
        cargo156.setCurrentLocation("Mediterranean Sea");
        cargo156.setCurrentPort("Rotterdam");
        cargo156.setCurrentPortCode("NLRTM");
        cargo156.setDestinationPort("Hamburg");
        cargo156.setDestinationPortCode("DEHAM");
        cargo156.setRoute("Rotterdam → North Sea → Hamburg");
        cargo156.setEstimatedArrival(LocalDateTime.now().plusDays(2));
        cargo156.setStatus("At Port");
        cargo156.setTrackingStatus("On Time");
        cargo156.getAdditionalInfo().put("Container Count", "28");
        cargo156.getAdditionalInfo().put("Weight", "1,800 tons");
        cargo156.getAdditionalInfo().put("Berth Assignment", "Berth 3");
        cargoTrackingData.put("156", cargo156);
        
        // 货物 #789
        CargoTracking cargo789 = new CargoTracking("#789", "Textiles", "COSCO SHIPPING");
        cargo789.setVesselCode("COSCO001");
        cargo789.setCurrentLocation("Indian Ocean");
        cargo789.setCurrentPort("Shanghai");
        cargo789.setCurrentPortCode("CNSHA");
        cargo789.setDestinationPort("Felixstowe");
        cargo789.setDestinationPortCode("GBFXT");
        cargo789.setRoute("Shanghai → Indian Ocean → Suez Canal → Felixstowe");
        cargo789.setEstimatedArrival(LocalDateTime.now().plusDays(8));
        cargo789.setStatus("In Transit");
        cargo789.setTrackingStatus("Delayed");
        cargo789.getAdditionalInfo().put("Container Count", "62");
        cargo789.getAdditionalInfo().put("Weight", "3,200 tons");
        cargo789.getAdditionalInfo().put("Delay Reason", "Weather conditions");
        cargoTrackingData.put("789", cargo789);
        
        // 货物 #234
        CargoTracking cargo234 = new CargoTracking("#234", "Machinery", "MAERSK SINGAPORE");
        cargo234.setVesselCode("MAERSK001");
        cargo234.setCurrentLocation("Port of Singapore");
        cargo234.setCurrentPort("Singapore");
        cargo234.setCurrentPortCode("SGSIN");
        cargo234.setDestinationPort("Busan");
        cargo234.setDestinationPortCode("KRPUS");
        cargo234.setRoute("Singapore → South China Sea → Busan");
        cargo234.setEstimatedArrival(LocalDateTime.now().plusDays(3));
        cargo234.setStatus("Loading");
        cargo234.setTrackingStatus("On Time");
        cargo234.getAdditionalInfo().put("Container Count", "38");
        cargo234.getAdditionalInfo().put("Weight", "2,100 tons");
        cargo234.getAdditionalInfo().put("Loading Progress", "75%");
        cargoTrackingData.put("234", cargo234);
        
        // 货物 #567
        CargoTracking cargo567 = new CargoTracking("#567", "Chemicals", "HAPAG-LLOYD BERLIN");
        cargo567.setVesselCode("HAPAG001");
        cargo567.setCurrentLocation("Atlantic Ocean");
        cargo567.setCurrentPort("Antwerp");
        cargo567.setCurrentPortCode("BEANR");
        cargo567.setDestinationPort("New York");
        cargo567.setDestinationPortCode("USNYC");
        cargo567.setRoute("Antwerp → Atlantic Ocean → New York");
        cargo567.setEstimatedArrival(LocalDateTime.now().plusDays(6));
        cargo567.setStatus("In Transit");
        cargo567.setTrackingStatus("Early");
        cargo567.getAdditionalInfo().put("Container Count", "22");
        cargo567.getAdditionalInfo().put("Weight", "1,500 tons");
        cargo567.getAdditionalInfo().put("Speed", "18 knots");
        cargoTrackingData.put("567", cargo567);
    }
    
    /**
     * 解析用户查询，提取关键信息
     */
    public SmartTrackingQuery parseQuery(String userQuery) {
        SmartTrackingQuery query = new SmartTrackingQuery();
        query.setOriginalQuery(userQuery);
        
        // 提取货物编号
        Matcher shipmentMatcher = SHIPMENT_ID_PATTERN.matcher(userQuery);
        if (shipmentMatcher.find()) {
            query.setShipmentId(shipmentMatcher.group(1));
        }
        
        // 提取船舶名称
        Matcher vesselMatcher = VESSEL_PATTERN.matcher(userQuery);
        if (vesselMatcher.find()) {
            query.setVesselName(vesselMatcher.group(1));
        }
        
        // 提取港口名称
        Matcher portMatcher = PORT_PATTERN.matcher(userQuery);
        if (portMatcher.find()) {
            query.setPortName(portMatcher.group(1));
        }
        
        // 识别查询类型
        String lowerQuery = userQuery.toLowerCase();
        if (lowerQuery.contains("where") || lowerQuery.contains("location")) {
            query.setQueryType("LOCATION");
        } else if (lowerQuery.contains("eta") || lowerQuery.contains("arrival") || lowerQuery.contains("when")) {
            query.setQueryType("ETA");
        } else if (lowerQuery.contains("route") || lowerQuery.contains("path")) {
            query.setQueryType("ROUTE");
        } else if (lowerQuery.contains("status") || lowerQuery.contains("condition")) {
            query.setQueryType("STATUS");
        } else {
            query.setQueryType("GENERAL");
        }
        
        return query;
    }
    
    /**
     * 根据查询查找货物追踪信息
     */
    public List<CargoTracking> searchCargo(SmartTrackingQuery query) {
        List<CargoTracking> results = new ArrayList<>();
        
        // 如果有货物编号，直接查找
        if (query.getShipmentId() != null) {
            CargoTracking cargo = cargoTrackingData.get(query.getShipmentId());
            if (cargo != null) {
                results.add(cargo);
                return results;
            }
        }
        
        // 根据其他条件搜索
        for (CargoTracking cargo : cargoTrackingData.values()) {
            boolean matches = true;
            
            if (query.getVesselName() != null && 
                !cargo.getVesselName().toLowerCase().contains(query.getVesselName().toLowerCase())) {
                matches = false;
            }
            
            if (query.getPortName() != null) {
                String portLower = query.getPortName().toLowerCase();
                boolean portMatches = cargo.getCurrentPort().toLowerCase().contains(portLower) ||
                                    cargo.getDestinationPort().toLowerCase().contains(portLower);
                if (!portMatches) {
                    matches = false;
                }
            }
            
            if (matches) {
                results.add(cargo);
            }
        }
        
        return results;
    }
    
    /**
     * 生成智能回复
     */
    public String generateTrackingResponse(SmartTrackingQuery query, List<CargoTracking> cargoList, String language) {
        if (cargoList.isEmpty()) {
            return language.equals("zh") ? 
                "抱歉，未找到相关的货物追踪信息。请检查货物编号或尝试其他查询条件。" :
                "Sorry, no cargo tracking information found. Please check the shipment number or try other search criteria.";
        }
        
        StringBuilder response = new StringBuilder();
        
        if (language.equals("zh")) {
            response.append("📦 货物追踪信息：\n\n");
        } else {
            response.append("📦 Cargo Tracking Information:\n\n");
        }
        
        for (CargoTracking cargo : cargoList) {
            response.append(generateCargoDetails(cargo, language));
            response.append("\n");
        }
        
        return response.toString();
    }
    
    /**
     * 生成单个货物的详细信息
     */
    private String generateCargoDetails(CargoTracking cargo, String language) {
        StringBuilder details = new StringBuilder();
        
        if (language.equals("zh")) {
            details.append("🚢 货物编号: ").append(cargo.getShipmentId()).append("\n");
            details.append("📋 货物类型: ").append(cargo.getCargoType()).append("\n");
            details.append("⛵ 船舶: ").append(cargo.getVesselName()).append(" (").append(cargo.getVesselCode()).append(")\n");
            details.append("📍 当前位置: ").append(cargo.getCurrentLocation()).append("\n");
            details.append("🏢 当前港口: ").append(cargo.getCurrentPort()).append(" (").append(cargo.getCurrentPortCode()).append(")\n");
            details.append("🎯 目的港口: ").append(cargo.getDestinationPort()).append(" (").append(cargo.getDestinationPortCode()).append(")\n");
            details.append("🛣️ 路线: ").append(cargo.getRoute()).append("\n");
            details.append("⏰ 预计到达: ").append(formatDateTime(cargo.getEstimatedArrival())).append("\n");
            details.append("📊 状态: ").append(cargo.getStatus()).append("\n");
            details.append("🔍 追踪状态: ").append(cargo.getTrackingStatus()).append("\n");
            
            if (!cargo.getAdditionalInfo().isEmpty()) {
                details.append("ℹ️ 额外信息:\n");
                cargo.getAdditionalInfo().forEach((key, value) -> 
                    details.append("   • ").append(key).append(": ").append(value).append("\n"));
            }
        } else {
            details.append("🚢 Shipment ID: ").append(cargo.getShipmentId()).append("\n");
            details.append("📋 Cargo Type: ").append(cargo.getCargoType()).append("\n");
            details.append("⛵ Vessel: ").append(cargo.getVesselName()).append(" (").append(cargo.getVesselCode()).append(")\n");
            details.append("📍 Current Location: ").append(cargo.getCurrentLocation()).append("\n");
            details.append("🏢 Current Port: ").append(cargo.getCurrentPort()).append(" (").append(cargo.getCurrentPortCode()).append(")\n");
            details.append("🎯 Destination Port: ").append(cargo.getDestinationPort()).append(" (").append(cargo.getDestinationPortCode()).append(")\n");
            details.append("🛣️ Route: ").append(cargo.getRoute()).append("\n");
            details.append("⏰ ETA: ").append(formatDateTime(cargo.getEstimatedArrival())).append("\n");
            details.append("📊 Status: ").append(cargo.getStatus()).append("\n");
            details.append("🔍 Tracking Status: ").append(cargo.getTrackingStatus()).append("\n");
            
            if (!cargo.getAdditionalInfo().isEmpty()) {
                details.append("ℹ️ Additional Information:\n");
                cargo.getAdditionalInfo().forEach((key, value) -> 
                    details.append("   • ").append(key).append(": ").append(value).append("\n"));
            }
        }
        
        return details.toString();
    }
    
    /**
     * 格式化日期时间
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    /**
     * 获取所有货物追踪数据
     */
    public List<CargoTracking> getAllCargoTracking() {
        return new ArrayList<>(cargoTrackingData.values());
    }
    
    /**
     * 根据货物编号获取追踪信息
     */
    public CargoTracking getCargoByShipmentId(String shipmentId) {
        return cargoTrackingData.get(shipmentId);
    }
    
    /**
     * Smart Tracking查询对象
     */
    public static class SmartTrackingQuery {
        private String originalQuery;
        private String shipmentId;
        private String vesselName;
        private String portName;
        private String queryType; // LOCATION, ETA, ROUTE, STATUS, GENERAL
        
        // Getters and Setters
        public String getOriginalQuery() { return originalQuery; }
        public void setOriginalQuery(String originalQuery) { this.originalQuery = originalQuery; }
        
        public String getShipmentId() { return shipmentId; }
        public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
        
        public String getVesselName() { return vesselName; }
        public void setVesselName(String vesselName) { this.vesselName = vesselName; }
        
        public String getPortName() { return portName; }
        public void setPortName(String portName) { this.portName = portName; }
        
        public String getQueryType() { return queryType; }
        public void setQueryType(String queryType) { this.queryType = queryType; }
    }
}
