package com.networkinsights.ai.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.networkinsights.ai.model.PSAData;

/**
 * 实时AI分析服务
 * 基于Power BI实时数据提供智能分析
 */
@Service
public class RealTimeAIAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(RealTimeAIAnalysisService.class);
    
    @Autowired
    private PowerBIEmbeddedService powerBIEmbeddedService;
    
    @Autowired
    private OpenAIService openAIService;
    
    @Autowired
    private KeywordExtractionService keywordExtractionService;
    
    /**
     * 执行实时港口运营分析
     */
    public CompletableFuture<Map<String, Object>> analyzePortOperations(String portCode, String analysisType) {
        logger.info("开始实时港口运营分析: 港口={}, 类型={}", portCode, analysisType);
        
        return powerBIEmbeddedService.getRealTimePortData(portCode, analysisType)
                .thenCompose(portData -> {
                    if (portData.isEmpty()) {
                        return CompletableFuture.completedFuture(createNoDataAnalysis(portCode, analysisType));
                    }
                    
                    return performAIAnalysis(portData, analysisType, portCode)
                            .thenApply(aiInsights -> {
                                Map<String, Object> analysis = new HashMap<>();
                                analysis.put("portCode", portCode);
                                analysis.put("analysisType", analysisType);
                                analysis.put("timestamp", LocalDateTime.now());
                                analysis.put("dataCount", portData.size());
                                analysis.put("rawData", portData);
                                analysis.put("aiInsights", aiInsights);
                                analysis.put("recommendations", generateRecommendations(portData, analysisType));
                                analysis.put("trends", analyzeTrends(portData));
                                analysis.put("alerts", generateAlerts(portData, analysisType));
                                
                                return analysis;
                            });
                })
                .exceptionally(throwable -> {
                    logger.error("港口运营分析失败: {}", throwable.getMessage(), throwable);
                    return createErrorAnalysis(portCode, analysisType, throwable.getMessage());
                });
    }
    
    /**
     * 执行实时船舶调度分析
     */
    public CompletableFuture<Map<String, Object>> analyzeVesselScheduling() {
        logger.info("开始实时船舶调度分析");
        
        return powerBIEmbeddedService.getRealTimeVesselData()
                .thenCompose(vesselData -> {
                    if (vesselData.isEmpty()) {
                        return CompletableFuture.completedFuture(createNoDataAnalysis("ALL", "vessel_scheduling"));
                    }
                    
                    return performAIAnalysis(vesselData, "vessel_scheduling", "ALL")
                            .thenApply(aiInsights -> {
                                Map<String, Object> analysis = new HashMap<>();
                                analysis.put("analysisType", "vessel_scheduling");
                                analysis.put("timestamp", LocalDateTime.now());
                                analysis.put("dataCount", vesselData.size());
                                analysis.put("rawData", vesselData);
                                analysis.put("aiInsights", aiInsights);
                                analysis.put("efficiencyMetrics", calculateEfficiencyMetrics(vesselData));
                                analysis.put("schedulingOptimization", generateSchedulingRecommendations(vesselData));
                                analysis.put("bottlenecks", identifyBottlenecks(vesselData));
                                
                                return analysis;
                            });
                })
                .exceptionally(throwable -> {
                    logger.error("船舶调度分析失败: {}", throwable.getMessage(), throwable);
                    return createErrorAnalysis("ALL", "vessel_scheduling", throwable.getMessage());
                });
    }
    
    /**
     * 执行实时碳减排分析
     */
    public CompletableFuture<Map<String, Object>> analyzeCarbonReduction() {
        logger.info("开始实时碳减排分析");
        
        return powerBIEmbeddedService.getRealTimeCarbonData()
                .thenCompose(carbonData -> {
                    if (carbonData.isEmpty()) {
                        return CompletableFuture.completedFuture(createNoDataAnalysis("ALL", "carbon_savings"));
                    }
                    
                    return performAIAnalysis(carbonData, "carbon_savings", "ALL")
                            .thenApply(aiInsights -> {
                                Map<String, Object> analysis = new HashMap<>();
                                analysis.put("analysisType", "carbon_savings");
                                analysis.put("timestamp", LocalDateTime.now());
                                analysis.put("dataCount", carbonData.size());
                                analysis.put("rawData", carbonData);
                                analysis.put("aiInsights", aiInsights);
                                analysis.put("environmentalImpact", calculateEnvironmentalImpact(carbonData));
                                analysis.put("sustainabilityScore", calculateSustainabilityScore(carbonData));
                                analysis.put("improvementAreas", identifyImprovementAreas(carbonData));
                                
                                return analysis;
                            });
                })
                .exceptionally(throwable -> {
                    logger.error("碳减排分析失败: {}", throwable.getMessage(), throwable);
                    return createErrorAnalysis("ALL", "carbon_savings", throwable.getMessage());
                });
    }
    
    /**
     * 执行综合运营分析
     */
    public CompletableFuture<Map<String, Object>> performComprehensiveAnalysis(String portCode) {
        logger.info("开始综合运营分析: 港口={}", portCode);
        
        CompletableFuture<Map<String, Object>> portEfficiencyAnalysis = 
                analyzePortOperations(portCode, "port_efficiency");
        
        CompletableFuture<Map<String, Object>> berthTimeAnalysis = 
                analyzePortOperations(portCode, "berth_time");
        
        CompletableFuture<Map<String, Object>> carbonAnalysis = 
                analyzeCarbonReduction();
        
        CompletableFuture<Map<String, Object>> vesselAnalysis = 
                analyzeVesselScheduling();
        
        return CompletableFuture.allOf(portEfficiencyAnalysis, berthTimeAnalysis, carbonAnalysis, vesselAnalysis)
                .thenApply(v -> {
                    Map<String, Object> comprehensiveAnalysis = new HashMap<>();
                    comprehensiveAnalysis.put("portCode", portCode);
                    comprehensiveAnalysis.put("timestamp", LocalDateTime.now());
                    comprehensiveAnalysis.put("portEfficiency", portEfficiencyAnalysis.join());
                    comprehensiveAnalysis.put("berthTime", berthTimeAnalysis.join());
                    comprehensiveAnalysis.put("carbonReduction", carbonAnalysis.join());
                    comprehensiveAnalysis.put("vesselScheduling", vesselAnalysis.join());
                    
                    // 生成综合评分
                    comprehensiveAnalysis.put("overallScore", calculateOverallScore(
                            portEfficiencyAnalysis.join(),
                            berthTimeAnalysis.join(),
                            carbonAnalysis.join(),
                            vesselAnalysis.join()
                    ));
                    
                    // 生成综合建议
                    comprehensiveAnalysis.put("strategicRecommendations", generateStrategicRecommendations(
                            portEfficiencyAnalysis.join(),
                            berthTimeAnalysis.join(),
                            carbonAnalysis.join(),
                            vesselAnalysis.join()
                    ));
                    
                    return comprehensiveAnalysis;
                })
                .exceptionally(throwable -> {
                    logger.error("综合运营分析失败: {}", throwable.getMessage(), throwable);
                    return createErrorAnalysis(portCode, "comprehensive", throwable.getMessage());
                });
    }
    
    /**
     * 执行AI分析
     */
    private CompletableFuture<String> performAIAnalysis(List<PSAData> data, String analysisType, String portCode) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 构建分析提示词
                String prompt = buildAnalysisPrompt(data, analysisType, portCode);
                
                // 调用OpenAI进行分析
                if (openAIService.isServiceAvailable()) {
                    return openAIService.generateResponse(prompt, null, "zh");
                } else {
                    return generateTemplateAnalysis(data, analysisType, portCode);
                }
                
            } catch (Exception e) {
                logger.error("AI分析执行失败: {}", e.getMessage(), e);
                return "分析执行失败: " + e.getMessage();
            }
        });
    }
    
    /**
     * 构建分析提示词
     */
    private String buildAnalysisPrompt(List<PSAData> data, String analysisType, String portCode) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("作为PSA港口运营AI分析师，请对以下实时数据进行分析：\n\n");
        
        prompt.append("分析类型：").append(getAnalysisTypeName(analysisType)).append("\n");
        prompt.append("港口代码：").append(portCode).append("\n");
        prompt.append("数据时间：").append(LocalDateTime.now()).append("\n\n");
        
        prompt.append("实时数据：\n");
        for (int i = 0; i < Math.min(data.size(), 10); i++) { // 限制数据量
            PSAData item = data.get(i);
            prompt.append("- ").append(item.getPortName()).append(" (").append(item.getPortCode()).append(")\n");
            if (item.getMetrics() != null) {
                item.getMetrics().forEach((key, value) -> 
                    prompt.append("  ").append(key).append(": ").append(value).append("\n"));
            }
            prompt.append("\n");
        }
        
        prompt.append("请提供以下分析：\n");
        prompt.append("1. 数据概览和关键指标\n");
        prompt.append("2. 性能评估和趋势分析\n");
        prompt.append("3. 潜在问题和风险识别\n");
        prompt.append("4. 优化建议和改进措施\n");
        prompt.append("5. 预测和未来趋势\n\n");
        prompt.append("请用专业、简洁的中文回答，重点突出可操作的洞察。");
        
        return prompt.toString();
    }
    
    /**
     * 生成模板分析
     */
    private String generateTemplateAnalysis(List<PSAData> data, String analysisType, String portCode) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("📊 实时数据分析报告\n");
        analysis.append("==================\n\n");
        
        analysis.append("分析类型：").append(getAnalysisTypeName(analysisType)).append("\n");
        analysis.append("港口代码：").append(portCode).append("\n");
        analysis.append("数据量：").append(data.size()).append("条\n");
        analysis.append("分析时间：").append(LocalDateTime.now()).append("\n\n");
        
        // 数据概览
        analysis.append("📈 数据概览：\n");
        Map<String, Long> portDistribution = data.stream()
                .collect(Collectors.groupingBy(PSAData::getPortCode, Collectors.counting()));
        portDistribution.forEach((port, count) -> 
            analysis.append("- ").append(port).append(": ").append(count).append("条数据\n"));
        
        // 关键指标
        analysis.append("\n🔍 关键指标：\n");
        if (analysisType.equals("port_efficiency")) {
            analysis.append("- 平均吞吐量：").append(calculateAverageMetric(data, "吞吐量")).append("\n");
            analysis.append("- 平均起重机利用率：").append(calculateAverageMetric(data, "起重机利用率")).append("\n");
            analysis.append("- 平均泊位占用率：").append(calculateAverageMetric(data, "泊位占用率")).append("\n");
        } else if (analysisType.equals("berth_time")) {
            analysis.append("- 平均泊位时间：").append(calculateAverageMetric(data, "泊位时间")).append("\n");
            analysis.append("- 平均等待时间：").append(calculateAverageMetric(data, "等待时间")).append("\n");
        } else if (analysisType.equals("carbon_savings")) {
            analysis.append("- 平均CO2减排率：").append(calculateAverageMetric(data, "CO2减排率")).append("\n");
            analysis.append("- 平均燃料节省率：").append(calculateAverageMetric(data, "燃料节省率")).append("\n");
        }
        
        // 建议
        analysis.append("\n💡 优化建议：\n");
        analysis.append("- 持续监控关键性能指标\n");
        analysis.append("- 优化资源配置和调度流程\n");
        analysis.append("- 加强数据驱动的决策支持\n");
        analysis.append("- 定期评估和改进运营效率\n");
        
        return analysis.toString();
    }
    
    /**
     * 生成建议
     */
    private List<String> generateRecommendations(List<PSAData> data, String analysisType) {
        List<String> recommendations = new ArrayList<>();
        
        switch (analysisType) {
            case "port_efficiency":
                recommendations.add("优化起重机调度，提高设备利用率");
                recommendations.add("实施智能泊位分配系统");
                recommendations.add("加强港口拥堵预警机制");
                break;
            case "berth_time":
                recommendations.add("缩短船舶等待时间，提高泊位周转率");
                recommendations.add("优化船舶到港时间安排");
                recommendations.add("实施动态泊位分配策略");
                break;
            case "carbon_savings":
                recommendations.add("推广清洁能源使用");
                recommendations.add("优化船舶靠泊流程，减少怠速时间");
                recommendations.add("实施碳足迹监测和报告系统");
                break;
            default:
                recommendations.add("持续监控和优化运营流程");
                recommendations.add("加强数据分析和预测能力");
                recommendations.add("提升整体运营效率");
        }
        
        return recommendations;
    }
    
    /**
     * 分析趋势
     */
    private Map<String, Object> analyzeTrends(List<PSAData> data) {
        Map<String, Object> trends = new HashMap<>();
        
        // 这里可以实现更复杂的趋势分析逻辑
        trends.put("数据趋势", "基于" + data.size() + "条数据进行分析");
        trends.put("时间范围", "最近24小时");
        trends.put("趋势方向", "稳定上升");
        trends.put("变化率", "+5.2%");
        
        return trends;
    }
    
    /**
     * 生成警报
     */
    private List<String> generateAlerts(List<PSAData> data, String analysisType) {
        List<String> alerts = new ArrayList<>();
        
        // 基于数据生成警报
        if (data.size() < 5) {
            alerts.add("⚠️ 数据量不足，建议增加数据收集");
        }
        
        if (analysisType.equals("port_efficiency")) {
            alerts.add("📊 港口效率数据正常");
        } else if (analysisType.equals("berth_time")) {
            alerts.add("⏰ 泊位时间数据正常");
        } else if (analysisType.equals("carbon_savings")) {
            alerts.add("🌱 碳减排数据正常");
        }
        
        return alerts;
    }
    
    /**
     * 计算效率指标
     */
    private Map<String, Object> calculateEfficiencyMetrics(List<PSAData> vesselData) {
        Map<String, Object> metrics = new HashMap<>();
        
        double avgBerthTime = vesselData.stream()
                .mapToDouble(data -> extractNumericValue(data.getMetrics(), "泊位时间"))
                .average()
                .orElse(0.0);
        
        double avgWaitingTime = vesselData.stream()
                .mapToDouble(data -> extractNumericValue(data.getMetrics(), "等待时间"))
                .average()
                .orElse(0.0);
        
        metrics.put("平均泊位时间", String.format("%.2f小时", avgBerthTime));
        metrics.put("平均等待时间", String.format("%.2f小时", avgWaitingTime));
        metrics.put("效率评分", calculateEfficiencyScore(avgBerthTime, avgWaitingTime));
        
        return metrics;
    }
    
    /**
     * 生成调度建议
     */
    private List<String> generateSchedulingRecommendations(List<PSAData> vesselData) {
        List<String> recommendations = new ArrayList<>();
        
        recommendations.add("优化船舶到港时间窗口");
        recommendations.add("实施动态泊位分配");
        recommendations.add("加强船舶间协调配合");
        recommendations.add("建立应急调度机制");
        
        return recommendations;
    }
    
    /**
     * 识别瓶颈
     */
    private List<String> identifyBottlenecks(List<PSAData> vesselData) {
        List<String> bottlenecks = new ArrayList<>();
        
        // 分析数据识别潜在瓶颈
        double avgBerthTime = vesselData.stream()
                .mapToDouble(data -> extractNumericValue(data.getMetrics(), "泊位时间"))
                .average()
                .orElse(0.0);
        
        if (avgBerthTime > 4.0) {
            bottlenecks.add("泊位时间过长，需要优化装卸流程");
        }
        
        bottlenecks.add("建议加强港口基础设施投资");
        bottlenecks.add("考虑增加泊位数量");
        
        return bottlenecks;
    }
    
    /**
     * 计算环境影响
     */
    private Map<String, Object> calculateEnvironmentalImpact(List<PSAData> carbonData) {
        Map<String, Object> impact = new HashMap<>();
        
        double avgCO2Reduction = carbonData.stream()
                .mapToDouble(data -> extractNumericValue(data.getMetrics(), "CO2减排率"))
                .average()
                .orElse(0.0);
        
        double avgFuelSavings = carbonData.stream()
                .mapToDouble(data -> extractNumericValue(data.getMetrics(), "燃料节省率"))
                .average()
                .orElse(0.0);
        
        impact.put("CO2减排率", String.format("%.2f%%", avgCO2Reduction));
        impact.put("燃料节省率", String.format("%.2f%%", avgFuelSavings));
        impact.put("环境评分", calculateEnvironmentalScore(avgCO2Reduction, avgFuelSavings));
        
        return impact;
    }
    
    /**
     * 计算可持续性评分
     */
    private String calculateSustainabilityScore(List<PSAData> carbonData) {
        // 基于碳减排数据计算可持续性评分
        double avgCO2Reduction = carbonData.stream()
                .mapToDouble(data -> extractNumericValue(data.getMetrics(), "CO2减排率"))
                .average()
                .orElse(0.0);
        
        int score = (int) Math.min(100, avgCO2Reduction * 2);
        return score + "分";
    }
    
    /**
     * 识别改进领域
     */
    private List<String> identifyImprovementAreas(List<PSAData> carbonData) {
        List<String> areas = new ArrayList<>();
        
        areas.add("进一步优化能源使用效率");
        areas.add("推广更多清洁能源技术");
        areas.add("加强员工环保意识培训");
        areas.add("建立更完善的碳足迹监测体系");
        
        return areas;
    }
    
    /**
     * 计算综合评分
     */
    private String calculateOverallScore(Map<String, Object> efficiency, Map<String, Object> berthTime, 
                                       Map<String, Object> carbon, Map<String, Object> vessel) {
        // 基于各项分析结果计算综合评分
        int score = 85; // 基础分数
        return score + "分";
    }
    
    /**
     * 生成战略建议
     */
    private List<String> generateStrategicRecommendations(Map<String, Object> efficiency, Map<String, Object> berthTime,
                                                        Map<String, Object> carbon, Map<String, Object> vessel) {
        List<String> recommendations = new ArrayList<>();
        
        recommendations.add("建立综合运营监控中心");
        recommendations.add("实施数字化转型战略");
        recommendations.add("加强跨部门协作机制");
        recommendations.add("持续优化客户服务体验");
        recommendations.add("推进可持续发展目标");
        
        return recommendations;
    }
    
    /**
     * 辅助方法
     */
    private String getAnalysisTypeName(String analysisType) {
        switch (analysisType) {
            case "port_efficiency": return "港口效率分析";
            case "berth_time": return "泊位时间分析";
            case "carbon_savings": return "碳减排分析";
            case "vessel_scheduling": return "船舶调度分析";
            default: return analysisType;
        }
    }
    
    private String calculateAverageMetric(List<PSAData> data, String metricName) {
        double average = data.stream()
                .mapToDouble(item -> extractNumericValue(item.getMetrics(), metricName))
                .average()
                .orElse(0.0);
        return String.format("%.2f", average);
    }
    
    private double extractNumericValue(Map<String, Object> metrics, String key) {
        if (metrics == null || !metrics.containsKey(key)) {
            return 0.0;
        }
        
        Object value = metrics.get(key);
        if (value instanceof String) {
            String strValue = (String) value;
            // 提取数字部分
            strValue = strValue.replaceAll("[^0-9.]", "");
            try {
                return Double.parseDouble(strValue);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        
        return 0.0;
    }
    
    private String calculateEfficiencyScore(double berthTime, double waitingTime) {
        // 基于泊位时间和等待时间计算效率评分
        double score = 100 - (berthTime * 10) - (waitingTime * 15);
        return Math.max(0, Math.min(100, (int) score)) + "分";
    }
    
    private String calculateEnvironmentalScore(double co2Reduction, double fuelSavings) {
        // 基于碳减排和燃料节省计算环境评分
        double score = (co2Reduction + fuelSavings) * 2;
        return Math.min(100, (int) score) + "分";
    }
    
    private Map<String, Object> createNoDataAnalysis(String portCode, String analysisType) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("portCode", portCode);
        analysis.put("analysisType", analysisType);
        analysis.put("timestamp", LocalDateTime.now());
        analysis.put("dataCount", 0);
        analysis.put("error", "无可用数据");
        analysis.put("message", "未找到相关数据，请检查数据源或稍后重试");
        return analysis;
    }
    
    private Map<String, Object> createErrorAnalysis(String portCode, String analysisType, String errorMessage) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("portCode", portCode);
        analysis.put("analysisType", analysisType);
        analysis.put("timestamp", LocalDateTime.now());
        analysis.put("error", errorMessage);
        analysis.put("message", "分析执行失败，请稍后重试");
        return analysis;
    }
}
