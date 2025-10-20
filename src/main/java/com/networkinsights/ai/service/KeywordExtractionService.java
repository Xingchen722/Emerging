package com.networkinsights.ai.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 关键词提取服务
 * 从用户问题中提取PSA相关的关键词，用于数据查询
 */
@Service
public class KeywordExtractionService {
    
    private static final Logger logger = LoggerFactory.getLogger(KeywordExtractionService.class);
    
    // PSA相关关键词映射
    private final Map<String, List<String>> psaKeywords;
    
    // 时间相关关键词
    private final List<String> timeKeywords;
    
    // 港口相关关键词
    private final List<String> portKeywords;
    
    // 货物状态相关关键词
    private final List<String> statusKeywords;
    
    public KeywordExtractionService() {
        this.psaKeywords = initializePSAKeywords();
        this.timeKeywords = initializeTimeKeywords();
        this.portKeywords = initializePortKeywords();
        this.statusKeywords = initializeStatusKeywords();
    }
    
    /**
     * 从用户问题中提取关键词
     */
    public ExtractedKeywords extractKeywords(String userQuestion) {
        String lowerQuestion = userQuestion.toLowerCase();
        
        ExtractedKeywords keywords = new ExtractedKeywords();
        
        // 提取PSA业务关键词
        extractPSAKeywords(lowerQuestion, keywords);
        
        // 提取时间关键词
        extractTimeKeywords(lowerQuestion, keywords);
        
        // 提取港口关键词
        extractPortKeywords(lowerQuestion, keywords);
        
        // 提取货物状态关键词
        extractStatusKeywords(lowerQuestion, keywords);
        
        // 提取数字和ID
        extractNumbersAndIds(lowerQuestion, keywords);
        
        // 提取Smart Tracking相关关键词
        extractSmartTrackingKeywords(lowerQuestion, keywords);
        
        // 提取Team Coordination相关关键词
        extractTeamCoordinationKeywords(lowerQuestion, keywords);
        
        logger.info("提取的关键词: {}", keywords);
        
        return keywords;
    }
    
    /**
     * 提取PSA业务关键词
     */
    private void extractPSAKeywords(String question, ExtractedKeywords keywords) {
        for (Map.Entry<String, List<String>> entry : psaKeywords.entrySet()) {
            String category = entry.getKey();
            List<String> categoryKeywords = entry.getValue();
            
            for (String keyword : categoryKeywords) {
                if (question.contains(keyword.toLowerCase())) {
                    keywords.addPSAKeyword(category, keyword);
                }
            }
        }
    }
    
    /**
     * 提取时间关键词
     */
    private void extractTimeKeywords(String question, ExtractedKeywords keywords) {
        for (String timeKeyword : timeKeywords) {
            if (question.contains(timeKeyword.toLowerCase())) {
                keywords.addTimeKeyword(timeKeyword);
            }
        }
        
        // 提取具体时间模式
        Pattern timePattern = Pattern.compile("(\\d{1,2}[:.]\\d{2}|\\d{1,2}点|\\d{1,2}时|am|pm|上午|下午|晚上|早晨|中午)");
        Matcher matcher = timePattern.matcher(question);
        while (matcher.find()) {
            keywords.addTimeKeyword(matcher.group());
        }
    }
    
    /**
     * 提取港口关键词
     */
    private void extractPortKeywords(String question, ExtractedKeywords keywords) {
        for (String portKeyword : portKeywords) {
            if (question.contains(portKeyword.toLowerCase())) {
                keywords.addPortKeyword(portKeyword);
            }
        }
    }
    
    /**
     * 提取货物状态关键词
     */
    private void extractStatusKeywords(String question, ExtractedKeywords keywords) {
        for (String statusKeyword : statusKeywords) {
            if (question.contains(statusKeyword.toLowerCase())) {
                keywords.addStatusKeyword(statusKeyword);
            }
        }
    }
    
    /**
     * 提取数字和ID
     */
    private void extractNumbersAndIds(String question, ExtractedKeywords keywords) {
        // 提取货物ID模式
        Pattern shipmentIdPattern = Pattern.compile("(货物|shipment|cargo|freight)[#\\s]*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher shipmentMatcher = shipmentIdPattern.matcher(question);
        while (shipmentMatcher.find()) {
            keywords.addShipmentId(shipmentMatcher.group(2));
        }
        
        // 提取一般数字
        Pattern numberPattern = Pattern.compile("\\b\\d+\\b");
        Matcher numberMatcher = numberPattern.matcher(question);
        while (numberMatcher.find()) {
            keywords.addNumber(numberMatcher.group());
        }
    }
    
    /**
     * 提取Smart Tracking相关关键词
     */
    private void extractSmartTrackingKeywords(String question, ExtractedKeywords keywords) {
        // 提取货物编号模式 (如 #342, shipment 342, cargo 342)
        Pattern cargoIdPattern = Pattern.compile("#?(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher cargoMatcher = cargoIdPattern.matcher(question);
        while (cargoMatcher.find()) {
            keywords.addCargoId(cargoMatcher.group(1));
        }
        
        // 提取船舶名称模式
        Pattern vesselPattern = Pattern.compile("(vessel|ship|boat)\\s+([a-zA-Z0-9\\s]+)", Pattern.CASE_INSENSITIVE);
        Matcher vesselMatcher = vesselPattern.matcher(question);
        while (vesselMatcher.find()) {
            keywords.addVesselName(vesselMatcher.group(2).trim());
        }
        
        // 提取查询类型关键词
        if (question.contains("where") || question.contains("location") || question.contains("position")) {
            keywords.addQueryType("LOCATION");
        }
        if (question.contains("eta") || question.contains("arrival") || question.contains("when") || question.contains("time")) {
            keywords.addQueryType("ETA");
        }
        if (question.contains("route") || question.contains("path") || question.contains("journey")) {
            keywords.addQueryType("ROUTE");
        }
        if (question.contains("status") || question.contains("condition") || question.contains("state")) {
            keywords.addQueryType("STATUS");
        }
        
        // 提取Smart Tracking特定关键词
        if (question.contains("track") || question.contains("追踪") || question.contains("跟踪")) {
            keywords.addSmartTrackingKeyword("tracking");
        }
        if (question.contains("cargo") || question.contains("货物") || question.contains("freight")) {
            keywords.addSmartTrackingKeyword("cargo");
        }
        if (question.contains("shipment") || question.contains("装运") || question.contains("运输")) {
            keywords.addSmartTrackingKeyword("shipment");
        }
    }
    
    /**
    /**
     * 提取Team Coordination相关关键词
     */
    private void extractTeamCoordinationKeywords(String question, ExtractedKeywords keywords) {
        // 提取地区关键词
        if (question.contains("europe") || question.contains("欧洲")) {
            keywords.addTeamCoordinationKeyword("region", "europe");
        }
        if (question.contains("asia") || question.contains("亚洲")) {
            keywords.addTeamCoordinationKeyword("region", "asia");
        }
        if (question.contains("america") || question.contains("americas") || question.contains("美洲")) {
            keywords.addTeamCoordinationKeyword("region", "america");
        }
        if (question.contains("apac") || question.contains("亚太")) {
            keywords.addTeamCoordinationKeyword("region", "apac");
        }
        if (question.contains("emea") || question.contains("欧非中东")) {
            keywords.addTeamCoordinationKeyword("region", "emea");
        }
        
        // 提取问题类型关键词
        if (question.contains("network") || question.contains("网络")) {
            keywords.addTeamCoordinationKeyword("issue", "network");
        }
        if (question.contains("logistics") || question.contains("物流")) {
            keywords.addTeamCoordinationKeyword("issue", "logistics");
        }
        if (question.contains("support") || question.contains("支持")) {
            keywords.addTeamCoordinationKeyword("issue", "support");
        }
        if (question.contains("security") || question.contains("安全")) {
            keywords.addTeamCoordinationKeyword("issue", "security");
        }
        if (question.contains("finance") || question.contains("财务")) {
            keywords.addTeamCoordinationKeyword("issue", "finance");
        }
        if (question.contains("legal") || question.contains("法律")) {
            keywords.addTeamCoordinationKeyword("issue", "legal");
        }
        if (question.contains("marketing") || question.contains("营销")) {
            keywords.addTeamCoordinationKeyword("issue", "marketing");
        }
        if (question.contains("infrastructure") || question.contains("基础设施")) {
            keywords.addTeamCoordinationKeyword("issue", "infrastructure");
        }
        
        // 提取角色级别关键词
        if (question.contains("manager") || question.contains("经理")) {
            keywords.addTeamCoordinationKeyword("role", "manager");
        }
        if (question.contains("director") || question.contains("总监")) {
            keywords.addTeamCoordinationKeyword("role", "director");
        }
        if (question.contains("engineer") || question.contains("工程师")) {
            keywords.addTeamCoordinationKeyword("role", "engineer");
        }
        if (question.contains("specialist") || question.contains("专家")) {
            keywords.addTeamCoordinationKeyword("role", "specialist");
        }
        if (question.contains("lead") || question.contains("负责人")) {
            keywords.addTeamCoordinationKeyword("role", "lead");
        }
        
        // 提取Team Coordination特定关键词
        if (question.contains("who") || question.contains("谁")) {
            keywords.addTeamCoordinationKeyword("query_type", "who");
        }
        if (question.contains("owns") || question.contains("负责")) {
            keywords.addTeamCoordinationKeyword("query_type", "owns");
        }
        if (question.contains("contact") || question.contains("联系")) {
            keywords.addTeamCoordinationKeyword("query_type", "contact");
        }
        if (question.contains("team") || question.contains("团队")) {
            keywords.addTeamCoordinationKeyword("query_type", "team");
        }
        if (question.contains("coordination") || question.contains("协调")) {
            keywords.addTeamCoordinationKeyword("query_type", "coordination");
        }
    }
    
    /**
     * 初始化PSA关键词
     */
    private Map<String, List<String>> initializePSAKeywords() {
        Map<String, List<String>> keywords = new HashMap<>();
        
        // 泊位时间相关
        keywords.put("berth_time", Arrays.asList(
            "berth time", "泊位时间", "靠泊时间", "停靠时间", "港口时间",
            "docking time", "mooring time", "arrival time", "departure time"
        ));
        
        // 碳减排相关
        keywords.put("carbon_savings", Arrays.asList(
            "carbon savings", "碳减排", "碳排放", "环保", "绿色",
            "co2 reduction", "carbon footprint", "sustainability", "environmental"
        ));
        
        // 港口效率相关
        keywords.put("port_efficiency", Arrays.asList(
            "port efficiency", "港口效率", "处理效率", "装卸效率",
            "throughput", "turnaround time", "port performance"
        ));
        
        // 货物追踪相关
        keywords.put("cargo_tracking", Arrays.asList(
            "cargo tracking", "货物追踪", "货物状态", "物流追踪",
            "shipment status", "freight tracking", "container tracking"
        ));
        
        // 港口拥堵相关
        keywords.put("port_congestion", Arrays.asList(
            "port congestion", "港口拥堵", "港口延误", "排队时间",
            "waiting time", "delay", "bottleneck", "traffic jam"
        ));
        
        // 船舶调度相关
        keywords.put("vessel_scheduling", Arrays.asList(
            "vessel scheduling", "船舶调度", "船期", "航行计划",
            "sailing schedule", "voyage planning", "route optimization"
        ));
        
        return keywords;
    }
    
    /**
     * 初始化时间关键词
     */
    private List<String> initializeTimeKeywords() {
        return Arrays.asList(
            "today", "today", "今天", "tomorrow", "明天", "yesterday", "昨天",
            "this week", "本周", "next week", "下周", "last week", "上周",
            "this month", "本月", "next month", "下月", "last month", "上月",
            "morning", "早晨", "afternoon", "下午", "evening", "晚上", "night", "夜晚",
            "urgent", "紧急", "asap", "immediately", "立即", "soon", "很快"
        );
    }
    
    /**
     * 初始化港口关键词
     */
    private List<String> initializePortKeywords() {
        return Arrays.asList(
            "singapore", "新加坡", "singapore port", "新加坡港", "sgsin",
            "rotterdam", "鹿特丹", "rotterdam port", "鹿特丹港", "nlrtm",
            "hamburg", "汉堡", "hamburg port", "汉堡港", "deham",
            "antwerp", "安特卫普", "antwerp port", "安特卫普港", "bean",
            "shanghai", "上海", "shanghai port", "上海港", "cnsha",
            "shenzhen", "深圳", "shenzhen port", "深圳港", "cnszn",
            "tianjin", "天津", "tianjin port", "天津港", "cntjn",
            "qingdao", "青岛", "qingdao port", "青岛港", "cnqng",
            "ningbo", "宁波", "ningbo port", "宁波港", "cnngb",
            "guangzhou", "广州", "guangzhou port", "广州港", "cngzo",
            "los angeles", "洛杉矶", "la port", "洛杉矶港", "uslax",
            "long beach", "长滩", "long beach port", "长滩港", "uslgb",
            "new york", "纽约", "new york port", "纽约港", "usnyc",
            "savannah", "萨凡纳", "savannah port", "萨凡纳港", "ussav",
            "felixstowe", "费利克斯托", "felixstowe port", "费利克斯托港", "gbfxt",
            "busan", "釜山", "busan port", "釜山港", "krpus",
            "yokohama", "横滨", "yokohama port", "横滨港", "jpyok",
            "tokyo", "东京", "tokyo port", "东京港", "jptyo",
            "osaka", "大阪", "osaka port", "大阪港", "jposa",
            "kobe", "神户", "kobe port", "神户港", "jpukb",
            "grn", "green", "green port", "绿色港口"
        );
    }
    
    /**
     * 初始化状态关键词
     */
    private List<String> initializeStatusKeywords() {
        return Arrays.asList(
            "arrived", "已到达", "departed", "已出发", "departure", "出发",
            "loading", "装货", "unloading", "卸货", "discharging", "卸货",
            "waiting", "等待", "queuing", "排队", "in transit", "运输中",
            "delayed", "延误", "on time", "准时", "early", "提前",
            "completed", "完成", "finished", "结束", "cancelled", "取消"
        );
    }
    
    /**
     * 关键词提取结果类
     */
    public static class ExtractedKeywords {
        private Map<String, List<String>> psaKeywords = new HashMap<>();
        private List<String> timeKeywords = new ArrayList<>();
        private List<String> portKeywords = new ArrayList<>();
        private List<String> statusKeywords = new ArrayList<>();
        private List<String> shipmentIds = new ArrayList<>();
        private List<String> numbers = new ArrayList<>();
        
        // Smart Tracking相关字段
        private List<String> cargoIds = new ArrayList<>();
        private List<String> vesselNames = new ArrayList<>();
        private List<String> queryTypes = new ArrayList<>();
        private List<String> smartTrackingKeywords = new ArrayList<>();
        
        // Team Coordination相关字段
        private Map<String, List<String>> teamCoordinationKeywords = new HashMap<>();
        
        public void addPSAKeyword(String category, String keyword) {
            psaKeywords.computeIfAbsent(category, k -> new ArrayList<>()).add(keyword);
        }
        
        public void addTimeKeyword(String keyword) {
            if (!timeKeywords.contains(keyword)) {
                timeKeywords.add(keyword);
            }
        }
        
        public void addPortKeyword(String keyword) {
            if (!portKeywords.contains(keyword)) {
                portKeywords.add(keyword);
            }
        }
        
        public void addStatusKeyword(String keyword) {
            if (!statusKeywords.contains(keyword)) {
                statusKeywords.add(keyword);
            }
        }
        
        public void addShipmentId(String id) {
            if (!shipmentIds.contains(id)) {
                shipmentIds.add(id);
            }
        }
        
        public void addNumber(String number) {
            if (!numbers.contains(number)) {
                numbers.add(number);
            }
        }
        
        // Smart Tracking相关方法
        public void addCargoId(String cargoId) {
            if (!cargoIds.contains(cargoId)) {
                cargoIds.add(cargoId);
            }
        }
        
        public void addVesselName(String vesselName) {
            if (!vesselNames.contains(vesselName)) {
                vesselNames.add(vesselName);
            }
        }
        
        public void addQueryType(String queryType) {
            if (!queryTypes.contains(queryType)) {
                queryTypes.add(queryType);
            }
        }
        
        public void addSmartTrackingKeyword(String keyword) {
            if (!smartTrackingKeywords.contains(keyword)) {
                smartTrackingKeywords.add(keyword);
            }
        }
        
        // Team Coordination相关方法
        public void addTeamCoordinationKeyword(String category, String keyword) {
            teamCoordinationKeywords.computeIfAbsent(category, k -> new ArrayList<>()).add(keyword);
        }
        
        // Getters
        public Map<String, List<String>> getPSAKeywords() { return psaKeywords; }
        public List<String> getTimeKeywords() { return timeKeywords; }
        public List<String> getPortKeywords() { return portKeywords; }
        public List<String> getStatusKeywords() { return statusKeywords; }
        public List<String> getShipmentIds() { return shipmentIds; }
        public List<String> getNumbers() { return numbers; }
        
        // Smart Tracking Getters
        public List<String> getCargoIds() { return cargoIds; }
        public List<String> getVesselNames() { return vesselNames; }
        public List<String> getQueryTypes() { return queryTypes; }
        public List<String> getSmartTrackingKeywords() { return smartTrackingKeywords; }
        
        // Team Coordination Getters
        public Map<String, List<String>> getTeamCoordinationKeywords() { return teamCoordinationKeywords; }
        
        public boolean hasKeywords() {
            return !psaKeywords.isEmpty() || !timeKeywords.isEmpty() || 
                   !portKeywords.isEmpty() || !statusKeywords.isEmpty() ||
                   !shipmentIds.isEmpty() || !numbers.isEmpty() ||
                   !cargoIds.isEmpty() || !vesselNames.isEmpty() || 
                   !queryTypes.isEmpty() || !smartTrackingKeywords.isEmpty() ||
                   !teamCoordinationKeywords.isEmpty();
        }
        
        public boolean hasSmartTrackingKeywords() {
            return !cargoIds.isEmpty() || !vesselNames.isEmpty() ||
                   !queryTypes.isEmpty() || !smartTrackingKeywords.isEmpty();
        }
        
        public boolean hasTeamCoordinationKeywords() {
            return !teamCoordinationKeywords.isEmpty();
        }
        
        @Override
        public String toString() {
            return "ExtractedKeywords{" +
                    "psaKeywords=" + psaKeywords +
                    ", timeKeywords=" + timeKeywords +
                    ", portKeywords=" + portKeywords +
                    ", statusKeywords=" + statusKeywords +
                    ", shipmentIds=" + shipmentIds +
                    ", numbers=" + numbers +
                    ", cargoIds=" + cargoIds +
                    ", vesselNames=" + vesselNames +
                    ", queryTypes=" + queryTypes +
                    ", smartTrackingKeywords=" + smartTrackingKeywords +
                    ", teamCoordinationKeywords=" + teamCoordinationKeywords +
                    '}';
        }
    }
}
