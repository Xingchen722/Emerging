package com.networkinsights.ai.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.networkinsights.ai.model.PSAData;
import com.networkinsights.ai.service.PowerBIEmbeddedService;
import com.networkinsights.ai.service.RealTimeAIAnalysisService;

/**
 * Power BI控制器
 * 提供Power BI Embedded集成和实时AI分析的API
 */
@RestController
@RequestMapping("/api/powerbi")
@CrossOrigin(origins = "*")
public class PowerBIController {
    
    private static final Logger logger = LoggerFactory.getLogger(PowerBIController.class);
    
    @Autowired
    private PowerBIEmbeddedService powerBIEmbeddedService;
    
    @Autowired
    private RealTimeAIAnalysisService realTimeAIAnalysisService;
    
    /**
     * 获取工作区信息
     */
    @GetMapping("/workspace")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getWorkspaceInfo() {
        logger.info("获取Power BI工作区信息");
        
        return powerBIEmbeddedService.getWorkspaceInfo()
                .thenApply(workspaceInfo -> {
                    logger.info("工作区信息获取成功");
                    return ResponseEntity.ok(workspaceInfo);
                })
                .exceptionally(throwable -> {
                    logger.error("获取工作区信息失败: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.ok(Map.of("error", "获取工作区信息失败: " + throwable.getMessage()));
                });
    }
    
    /**
     * 获取报告列表
     */
    @GetMapping("/reports")
    public CompletableFuture<ResponseEntity<List<Map<String, Object>>>> getReports() {
        logger.info("获取Power BI报告列表");
        
        return powerBIEmbeddedService.getReports()
                .thenApply(reports -> {
                    logger.info("报告列表获取成功，共{}个报告", reports.size());
                    return ResponseEntity.ok(reports);
                })
                .exceptionally(throwable -> {
                    logger.error("获取报告列表失败: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.ok(List.of());
                });
    }
    
    /**
     * 获取数据集列表
     */
    @GetMapping("/datasets")
    public CompletableFuture<ResponseEntity<List<Map<String, Object>>>> getDatasets() {
        logger.info("获取Power BI数据集列表");
        
        return powerBIEmbeddedService.getDatasets()
                .thenApply(datasets -> {
                    logger.info("数据集列表获取成功，共{}个数据集", datasets.size());
                    return ResponseEntity.ok(datasets);
                })
                .exceptionally(throwable -> {
                    logger.error("获取数据集列表失败: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.ok(List.of());
                });
    }
    
    /**
     * 执行DAX查询
     */
    @PostMapping("/query")
    public CompletableFuture<ResponseEntity<List<PSAData>>> executeDAXQuery(@RequestBody Map<String, String> request) {
        String daxQuery = request.get("query");
        if (daxQuery == null || daxQuery.trim().isEmpty()) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(List.of()));
        }
        
        logger.info("执行DAX查询: {}", daxQuery);
        
        return powerBIEmbeddedService.executeDAXQuery(daxQuery)
                .thenApply(data -> {
                    logger.info("DAX查询执行成功，返回{}条数据", data.size());
                    return ResponseEntity.ok(data);
                })
                .exceptionally(throwable -> {
                    logger.error("DAX查询执行失败: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.ok(List.of());
                });
    }
    
    /**
     * 获取实时港口数据
     */
    @GetMapping("/data/port/{portCode}")
    public CompletableFuture<ResponseEntity<List<PSAData>>> getRealTimePortData(
            @PathVariable String portCode,
            @RequestParam(defaultValue = "port_efficiency") String dataType) {
        
        logger.info("获取实时港口数据: 港口={}, 类型={}", portCode, dataType);
        
        return powerBIEmbeddedService.getRealTimePortData(portCode, dataType)
                .thenApply(data -> {
                    logger.info("实时港口数据获取成功，返回{}条数据", data.size());
                    return ResponseEntity.ok(data);
                })
                .exceptionally(throwable -> {
                    logger.error("获取实时港口数据失败: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.ok(List.of());
                });
    }
    
    /**
     * 获取实时船舶数据
     */
    @GetMapping("/data/vessels")
    public CompletableFuture<ResponseEntity<List<PSAData>>> getRealTimeVesselData() {
        logger.info("获取实时船舶数据");
        
        return powerBIEmbeddedService.getRealTimeVesselData()
                .thenApply(data -> {
                    logger.info("实时船舶数据获取成功，返回{}条数据", data.size());
                    return ResponseEntity.ok(data);
                })
                .exceptionally(throwable -> {
                    logger.error("获取实时船舶数据失败: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.ok(List.of());
                });
    }
    
    /**
     * 获取实时碳减排数据
     */
    @GetMapping("/data/carbon")
    public CompletableFuture<ResponseEntity<List<PSAData>>> getRealTimeCarbonData() {
        logger.info("获取实时碳减排数据");
        
        return powerBIEmbeddedService.getRealTimeCarbonData()
                .thenApply(data -> {
                    logger.info("实时碳减排数据获取成功，返回{}条数据", data.size());
                    return ResponseEntity.ok(data);
                })
                .exceptionally(throwable -> {
                    logger.error("获取实时碳减排数据失败: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.ok(List.of());
                });
    }
    
    /**
     * 执行港口运营分析
     */
    @GetMapping("/analysis/port/{portCode}")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> analyzePortOperations(
            @PathVariable String portCode,
            @RequestParam(defaultValue = "port_efficiency") String analysisType) {
        
        logger.info("执行港口运营分析: 港口={}, 类型={}", portCode, analysisType);
        
        return realTimeAIAnalysisService.analyzePortOperations(portCode, analysisType)
                .thenApply(analysis -> {
                    logger.info("港口运营分析完成");
                    return ResponseEntity.ok(analysis);
                })
                .exceptionally(throwable -> {
                    logger.error("港口运营分析失败: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.ok(Map.of("error", "分析失败: " + throwable.getMessage()));
                });
    }
    
    /**
     * 执行船舶调度分析
     */
    @GetMapping("/analysis/vessels")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> analyzeVesselScheduling() {
        logger.info("执行船舶调度分析");
        
        return realTimeAIAnalysisService.analyzeVesselScheduling()
                .thenApply(analysis -> {
                    logger.info("船舶调度分析完成");
                    return ResponseEntity.ok(analysis);
                })
                .exceptionally(throwable -> {
                    logger.error("船舶调度分析失败: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.ok(Map.of("error", "分析失败: " + throwable.getMessage()));
                });
    }
    
    /**
     * 执行碳减排分析
     */
    @GetMapping("/analysis/carbon")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> analyzeCarbonReduction() {
        logger.info("执行碳减排分析");
        
        return realTimeAIAnalysisService.analyzeCarbonReduction()
                .thenApply(analysis -> {
                    logger.info("碳减排分析完成");
                    return ResponseEntity.ok(analysis);
                })
                .exceptionally(throwable -> {
                    logger.error("碳减排分析失败: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.ok(Map.of("error", "分析失败: " + throwable.getMessage()));
                });
    }
    
    /**
     * 执行综合运营分析
     */
    @GetMapping("/analysis/comprehensive/{portCode}")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> performComprehensiveAnalysis(
            @PathVariable String portCode) {
        
        logger.info("执行综合运营分析: 港口={}", portCode);
        
        return realTimeAIAnalysisService.performComprehensiveAnalysis(portCode)
                .thenApply(analysis -> {
                    logger.info("综合运营分析完成");
                    return ResponseEntity.ok(analysis);
                })
                .exceptionally(throwable -> {
                    logger.error("综合运营分析失败: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.ok(Map.of("error", "分析失败: " + throwable.getMessage()));
                });
    }
    
    /**
     * 获取缓存统计信息
     */
    @GetMapping("/cache/stats")
    public ResponseEntity<Map<String, Object>> getCacheStatistics() {
        logger.info("获取缓存统计信息");
        
        try {
            Map<String, Object> stats = powerBIEmbeddedService.getCacheStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("获取缓存统计信息失败: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of("error", "获取缓存统计信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 清理过期缓存
     */
    @PostMapping("/cache/clean")
    public ResponseEntity<Map<String, String>> cleanExpiredCache() {
        logger.info("清理过期缓存");
        
        try {
            powerBIEmbeddedService.cleanExpiredCache();
            return ResponseEntity.ok(Map.of("message", "缓存清理完成"));
        } catch (Exception e) {
            logger.error("清理缓存失败: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of("error", "清理缓存失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取服务状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getServiceStatus() {
        logger.info("获取Power BI服务状态");
        
        Map<String, Object> status = Map.of(
            "powerBIEmbedded", true,
            "realTimeAnalysis", true,
            "cacheEnabled", true,
            "timestamp", System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(status);
    }
}
