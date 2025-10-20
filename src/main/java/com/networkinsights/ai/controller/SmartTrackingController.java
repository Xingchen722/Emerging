package com.networkinsights.ai.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.networkinsights.ai.model.CargoTracking;
import com.networkinsights.ai.model.ChatMessage;
import com.networkinsights.ai.service.SmartTrackingService;

/**
 * Smart Tracking控制器
 * 提供货物追踪相关的REST API端点
 */
@RestController
@RequestMapping("/api/smart-tracking")
@CrossOrigin(origins = "*")
public class SmartTrackingController {

    private static final Logger logger = LoggerFactory.getLogger(SmartTrackingController.class);

    @Autowired
    private SmartTrackingService smartTrackingService;

    /**
     * 自然语言查询货物追踪信息
     * 示例: "Where is shipment #342 now?"
     */
    @PostMapping("/query")
    public ResponseEntity<ChatMessage> queryCargoTracking(@RequestBody Map<String, String> request) {
        try {
            String query = request.get("query");
            String language = request.getOrDefault("language", "en");
            
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorMessage("Query cannot be empty", language));
            }
            
            logger.info("Smart Tracking查询: {}", query);
            
            // 解析查询
            SmartTrackingService.SmartTrackingQuery parsedQuery = smartTrackingService.parseQuery(query);
            
            // 搜索货物追踪信息
            List<CargoTracking> cargoList = smartTrackingService.searchCargo(parsedQuery);
            
            // 生成回复
            String response = smartTrackingService.generateTrackingResponse(parsedQuery, cargoList, language);
            
            // 创建聊天消息
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setId(java.util.UUID.randomUUID().toString());
            chatMessage.setContent(response);
            chatMessage.setSender("ai");
            chatMessage.setTimestamp(java.time.LocalDateTime.now());
            chatMessage.setLanguage(language);
            chatMessage.setOriginalContent("Smart Tracking查询结果");
            
            return ResponseEntity.ok(chatMessage);
            
        } catch (Exception e) {
            logger.error("Smart Tracking查询失败: {}", e.getMessage(), e);
            String language = request.getOrDefault("language", "en");
            return ResponseEntity.status(500).body(createErrorMessage("Smart Tracking查询时发生错误: " + e.getMessage(), language));
        }
    }

    /**
     * 根据货物编号获取追踪信息
     */
    @GetMapping("/cargo/{shipmentId}")
    public ResponseEntity<CargoTracking> getCargoByShipmentId(@PathVariable String shipmentId) {
        try {
            CargoTracking cargo = smartTrackingService.getCargoByShipmentId(shipmentId);
            if (cargo != null) {
                return ResponseEntity.ok(cargo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取货物追踪信息失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取所有货物追踪数据
     */
    @GetMapping("/cargo/all")
    public ResponseEntity<List<CargoTracking>> getAllCargoTracking() {
        try {
            List<CargoTracking> allCargo = smartTrackingService.getAllCargoTracking();
            return ResponseEntity.ok(allCargo);
        } catch (Exception e) {
            logger.error("获取所有货物追踪数据失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    /**
     * 根据船舶名称搜索货物
     */
    @GetMapping("/cargo/vessel/{vesselName}")
    public ResponseEntity<List<CargoTracking>> getCargoByVessel(@PathVariable String vesselName) {
        try {
            SmartTrackingService.SmartTrackingQuery query = new SmartTrackingService.SmartTrackingQuery();
            query.setVesselName(vesselName);
            
            List<CargoTracking> cargoList = smartTrackingService.searchCargo(query);
            return ResponseEntity.ok(cargoList);
        } catch (Exception e) {
            logger.error("根据船舶名称搜索货物失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    /**
     * 根据港口搜索货物
     */
    @GetMapping("/cargo/port/{portName}")
    public ResponseEntity<List<CargoTracking>> getCargoByPort(@PathVariable String portName) {
        try {
            SmartTrackingService.SmartTrackingQuery query = new SmartTrackingService.SmartTrackingQuery();
            query.setPortName(portName);
            
            List<CargoTracking> cargoList = smartTrackingService.searchCargo(query);
            return ResponseEntity.ok(cargoList);
        } catch (Exception e) {
            logger.error("根据港口搜索货物失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    /**
     * 获取Smart Tracking服务状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getServiceStatus() {
        try {
            Map<String, Object> status = Map.of(
                "service", "Smart Tracking",
                "status", "active",
                "totalCargo", smartTrackingService.getAllCargoTracking().size(),
                "features", List.of(
                    "Natural Language Query",
                    "Cargo Tracking",
                    "Route Information",
                    "ETA Calculation",
                    "Vessel Tracking"
                )
            );
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("获取Smart Tracking服务状态失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Service status unavailable"));
        }
    }

    /**
     * 获取支持的查询示例
     */
    @GetMapping("/examples")
    public ResponseEntity<Map<String, List<String>>> getQueryExamples() {
        try {
            Map<String, List<String>> examples = Map.of(
                "location_queries", List.of(
                    "Where is shipment #342 now?",
                    "What is the current location of cargo #156?",
                    "Where is vessel MSC LORETO?"
                ),
                "eta_queries", List.of(
                    "What is the ETA for shipment #789?",
                    "When will cargo #234 arrive?",
                    "What time is vessel EVER GIVEN expected?"
                ),
                "route_queries", List.of(
                    "What is the route for shipment #567?",
                    "Show me the journey path for cargo #342",
                    "What ports will vessel COSCO SHIPPING visit?"
                ),
                "status_queries", List.of(
                    "What is the status of shipment #156?",
                    "Is cargo #789 on time?",
                    "What is the condition of vessel MAERSK SINGAPORE?"
                )
            );
            return ResponseEntity.ok(examples);
        } catch (Exception e) {
            logger.error("获取查询示例失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Collections.emptyMap());
        }
    }

    /**
     * 创建错误消息
     */
    private ChatMessage createErrorMessage(String message, String language) {
        ChatMessage errorMessage = new ChatMessage();
        errorMessage.setId(java.util.UUID.randomUUID().toString());
        errorMessage.setContent(message);
        errorMessage.setSender("ai");
        errorMessage.setTimestamp(java.time.LocalDateTime.now());
        errorMessage.setLanguage(language);
        return errorMessage;
    }
}
