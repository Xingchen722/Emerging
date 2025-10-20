package com.networkinsights.ai.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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

import com.networkinsights.ai.model.PSAData;

/**
 * 数据读取服务
 * 负责读取和处理PSA数据文件
 */
@Service
public class DataReadingService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataReadingService.class);
    
    // 数据文件路径
    private static final String RAW_DATA_PATH = "psa-raw-data.txt";
    private static final String EXTENDED_DATA_PATH = "psa-extended-data.txt";
    private static final String COMPLETE_DATA_PATH = "psa-complete-data.txt";
    
    // 港口代码映射
    private static final Map<String, String> PORT_CODE_MAP = new HashMap<>();
    static {
        PORT_CODE_MAP.put("SGSIN", "新加坡港");
        PORT_CODE_MAP.put("NLRTM", "鹿特丹港");
        PORT_CODE_MAP.put("DEHAM", "汉堡港");
        PORT_CODE_MAP.put("USLAX", "洛杉矶港");
        PORT_CODE_MAP.put("CNSHA", "上海港");
        PORT_CODE_MAP.put("JPTYO", "东京港");
        PORT_CODE_MAP.put("AEJEA", "杰贝阿里港");
        PORT_CODE_MAP.put("ESALG", "阿尔赫西拉斯港");
        PORT_CODE_MAP.put("TWKHH", "高雄港");
        PORT_CODE_MAP.put("PAPTY", "巴拿马港");
        PORT_CODE_MAP.put("ITGIT", "的里雅斯特港");
        PORT_CODE_MAP.put("GRPIR", "比雷埃夫斯港");
        PORT_CODE_MAP.put("KRPUS", "釜山港");
        PORT_CODE_MAP.put("BRSSZ", "桑托斯港");
        PORT_CODE_MAP.put("VNSGN", "胡志明市港");
        PORT_CODE_MAP.put("AEKHL", "哈利法港");
        PORT_CODE_MAP.put("MUMBAI", "孟买港");
        PORT_CODE_MAP.put("CNTXG", "天津港");
        PORT_CODE_MAP.put("GBFXT", "费利克斯托港");
        PORT_CODE_MAP.put("GBSOU", "南安普顿港");
        PORT_CODE_MAP.put("MYPKG", "巴生港");
        PORT_CODE_MAP.put("CNQDG", "青岛港");
        PORT_CODE_MAP.put("BEANR", "安特卫普港");
        PORT_CODE_MAP.put("THLCB", "林查班港");
    }
    
    /**
     * 读取所有PSA数据
     */
    public List<PSAData> readAllPSAData() {
        List<PSAData> allData = new ArrayList<>();
        
        try {
            // 读取原始数据
            allData.addAll(readRawData());
            
            // 读取扩展数据
            allData.addAll(readExtendedData());
            
            // 读取完整数据
            allData.addAll(readCompleteData());
            
            logger.info("成功读取 {} 条PSA数据", allData.size());
            
        } catch (Exception e) {
            logger.error("读取PSA数据失败: {}", e.getMessage(), e);
        }
        
        return allData;
    }
    
    /**
     * 读取原始数据文件
     */
    private List<PSAData> readRawData() throws IOException {
        List<PSAData> dataList = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(RAW_DATA_PATH));
        
        for (String line : lines) {
            if (line.contains("港") && line.contains("数据：")) {
                // 解析港口数据
                PSAData data = parsePortData(line, lines);
                if (data != null) {
                    dataList.add(data);
                }
            }
        }
        
        return dataList;
    }
    
    /**
     * 读取扩展数据文件
     */
    private List<PSAData> readExtendedData() throws IOException {
        List<PSAData> dataList = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(EXTENDED_DATA_PATH));
        
        for (String line : lines) {
            if (line.contains("港") && line.contains("运营数据：")) {
                // 解析港口运营数据
                PSAData data = parsePortData(line, lines);
                if (data != null) {
                    dataList.add(data);
                }
            }
        }
        
        return dataList;
    }
    
    /**
     * 读取完整数据文件（CSV格式）
     */
    private List<PSAData> readCompleteData() throws IOException {
        List<PSAData> dataList = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(COMPLETE_DATA_PATH));
        
        if (lines.size() < 2) {
            return dataList;
        }
        
        // 跳过标题行
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (!line.isEmpty()) {
                PSAData data = parseCSVData(line);
                if (data != null) {
                    dataList.add(data);
                }
            }
        }
        
        return dataList;
    }
    
    /**
     * 解析港口数据
     */
    private PSAData parsePortData(String headerLine, List<String> lines) {
        try {
            // 提取港口信息
            Pattern portPattern = Pattern.compile("(\\w+港)\\s*\\((\\w+)\\)");
            Matcher matcher = portPattern.matcher(headerLine);
            
            if (!matcher.find()) {
                return null;
            }
            
            String portName = matcher.group(1);
            String portCode = matcher.group(2);
            
            PSAData data = new PSAData();
            data.setDataId("RAW_" + portCode + "_" + System.currentTimeMillis());
            data.setPortName(portName);
            data.setPortCode(portCode);
            data.setTimestamp(LocalDateTime.now());
            data.setSource("PSA原始数据");
            data.setTags(Arrays.asList("psa", "raw", portCode.toLowerCase()));
            
            Map<String, Object> metrics = new HashMap<>();
            
            // 查找后续行中的指标数据
            int startIndex = lines.indexOf(headerLine);
            for (int i = startIndex + 1; i < Math.min(startIndex + 10, lines.size()); i++) {
                String line = lines.get(i);
                if (line.startsWith("- ")) {
                    parseMetrics(line, metrics);
                } else if (line.trim().isEmpty() || line.contains("港")) {
                    break;
                }
            }
            
            data.setMetrics(metrics);
            
            // 根据指标内容确定数据类型
            if (metrics.containsKey("泊位时间")) {
                data.setDataType("berth_time");
                data.setDescription("泊位时间数据：" + portName);
            } else if (metrics.containsKey("碳减排")) {
                data.setDataType("carbon_savings");
                data.setDescription("碳减排数据：" + portName);
            } else if (metrics.containsKey("港口效率")) {
                data.setDataType("port_efficiency");
                data.setDescription("港口效率数据：" + portName);
            } else if (metrics.containsKey("货物追踪")) {
                data.setDataType("cargo_tracking");
                data.setDescription("货物追踪数据：" + portName);
            } else if (metrics.containsKey("船舶调度")) {
                data.setDataType("vessel_scheduling");
                data.setDescription("船舶调度数据：" + portName);
            } else {
                data.setDataType("general");
                data.setDescription("港口运营数据：" + portName);
            }
            
            return data;
            
        } catch (Exception e) {
            logger.error("解析港口数据失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 解析指标数据
     */
    private void parseMetrics(String line, Map<String, Object> metrics) {
        try {
            // 泊位时间
            if (line.contains("泊位时间")) {
                Pattern pattern = Pattern.compile("平均(\\d+\\.\\d+)小时");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("平均泊位时间", matcher.group(1) + "小时");
                }
                
                pattern = Pattern.compile("效率评分(\\d+)分");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("效率评分", matcher.group(1) + "分");
                }
                
                pattern = Pattern.compile("当前等待船舶(\\d+)艘");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("等待船舶数", matcher.group(1) + "艘");
                }
            }
            
            // 碳减排
            if (line.contains("碳减排")) {
                Pattern pattern = Pattern.compile("CO2减排(\\d+\\.\\d+)%");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("CO2减排率", matcher.group(1) + "%");
                }
                
                pattern = Pattern.compile("燃料节省(\\d+\\.\\d+)%");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("燃料节省率", matcher.group(1) + "%");
                }
                
                pattern = Pattern.compile("效率提升(\\d+)%");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("效率提升率", matcher.group(1) + "%");
                }
            }
            
            // 港口效率
            if (line.contains("港口效率")) {
                Pattern pattern = Pattern.compile("每小时吞吐量(\\d+)个集装箱");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("每小时吞吐量", matcher.group(1) + "个集装箱");
                }
                
                pattern = Pattern.compile("起重机利用率(\\d+)%");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("起重机利用率", matcher.group(1) + "%");
                }
                
                pattern = Pattern.compile("泊位占用率(\\d+)%");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("泊位占用率", matcher.group(1) + "%");
                }
            }
            
            // 货物追踪
            if (line.contains("货物追踪")) {
                Pattern pattern = Pattern.compile("货物#(\\d+)");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("货物编号", "#" + matcher.group(1));
                }
                
                if (line.contains("正在航行中")) {
                    metrics.put("货物状态", "航行中");
                } else if (line.contains("已到达")) {
                    metrics.put("货物状态", "已到达");
                } else if (line.contains("正在清关")) {
                    metrics.put("货物状态", "清关中");
                }
            }
            
            // 船舶调度
            if (line.contains("船舶调度")) {
                Pattern pattern = Pattern.compile("今日计划到达(\\d+)艘船");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("计划到达船舶", matcher.group(1) + "艘");
                }
                
                pattern = Pattern.compile("计划出发(\\d+)艘船");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("计划出发船舶", matcher.group(1) + "艘");
                }
                
                pattern = Pattern.compile("下次到达时间(\\d+:\\d+)");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    metrics.put("下次到达时间", matcher.group(1));
                }
            }
            
        } catch (Exception e) {
            logger.error("解析指标数据失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 解析CSV格式数据
     */
    private PSAData parseCSVData(String line) {
        try {
            String[] fields = line.split("\\s+");
            if (fields.length < 20) {
                return null;
            }
            
            PSAData data = new PSAData();
            data.setDataId("CSV_" + fields[0] + "_" + System.currentTimeMillis());
            data.setDataType("vessel_scheduling");
            data.setTimestamp(LocalDateTime.now());
            data.setSource("PSA完整数据");
            data.setTags(Arrays.asList("psa", "csv", "vessel"));
            
            // 提取港口信息
            String fromPort = fields[4];
            String toPort = fields[5];
            String portCode = fromPort;
            String portName = PORT_CODE_MAP.getOrDefault(portCode, portCode + "港");
            
            data.setPortName(portName);
            data.setPortCode(portCode);
            
            // 构建指标数据
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("船舶名称", fields[2]);
            metrics.put("IMO号", fields[3]);
            metrics.put("出发港", fromPort);
            metrics.put("目的港", toPort);
            metrics.put("泊位时间", fields[11] + "小时");
            metrics.put("等待时间", fields[12] + "小时");
            metrics.put("燃料节省", "$" + fields[19]);
            metrics.put("碳减排", fields[20] + "吨");
            metrics.put("到达准确率", fields[10]);
            
            data.setMetrics(metrics);
            data.setDescription("船舶调度数据：" + portName + " - " + fields[2]);
            
            return data;
            
        } catch (Exception e) {
            logger.error("解析CSV数据失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 根据关键词搜索数据
     */
    public List<PSAData> searchDataByKeywords(Map<String, List<String>> keywords) {
        List<PSAData> allData = readAllPSAData();
        List<PSAData> results = new ArrayList<>();
        
        for (PSAData data : allData) {
            if (matchesKeywords(data, keywords)) {
                results.add(data);
            }
        }
        
        logger.info("根据关键词搜索到 {} 条数据", results.size());
        return results;
    }
    
    /**
     * 检查数据是否匹配关键词
     */
    private boolean matchesKeywords(PSAData data, Map<String, List<String>> keywords) {
        for (Map.Entry<String, List<String>> entry : keywords.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            
            // 检查数据类型
            if ("berth_time".equals(key) && "berth_time".equals(data.getDataType())) {
                return true;
            }
            if ("carbon_savings".equals(key) && "carbon_savings".equals(data.getDataType())) {
                return true;
            }
            if ("port_efficiency".equals(key) && "port_efficiency".equals(data.getDataType())) {
                return true;
            }
            if ("cargo_tracking".equals(key) && "cargo_tracking".equals(data.getDataType())) {
                return true;
            }
            if ("vessel_scheduling".equals(key) && "vessel_scheduling".equals(data.getDataType())) {
                return true;
            }
            
            // 检查港口名称
            if ("ports".equals(key)) {
                for (String port : values) {
                    if (data.getPortName().contains(port) || data.getPortCode().contains(port)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * 获取数据统计信息
     */
    public Map<String, Object> getDataStatistics() {
        List<PSAData> allData = readAllPSAData();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("总数据量", allData.size());
        
        Map<String, Integer> typeCount = new HashMap<>();
        Map<String, Integer> portCount = new HashMap<>();
        
        for (PSAData data : allData) {
            typeCount.put(data.getDataType(), typeCount.getOrDefault(data.getDataType(), 0) + 1);
            portCount.put(data.getPortName(), portCount.getOrDefault(data.getPortName(), 0) + 1);
        }
        
        stats.put("数据类型分布", typeCount);
        stats.put("港口分布", portCount);
        
        return stats;
    }
}
