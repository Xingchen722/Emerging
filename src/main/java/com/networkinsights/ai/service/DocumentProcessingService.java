package com.networkinsights.ai.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.networkinsights.ai.model.PSAData;

/**
 * 文档处理服务
 * 处理 PDF 文件上传、文本提取和数据解析
 */
@Service
public class DocumentProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentProcessingService.class);

    @Autowired
    private PSADataService psaDataService;

    // 数据解析模式
    private static final Pattern PORT_PATTERN = Pattern.compile("(新加坡港|鹿特丹港|汉堡港|上海港|深圳港|天津港|青岛港|宁波港|广州港|洛杉矶港|长滩港|纽约港|萨凡纳港|费利克斯托港|釜山港|横滨港|东京港|大阪港|神户港|绿色港口|Port of Singapore|Port of Rotterdam|Port of Hamburg|Port of Shanghai|Port of Shenzhen|Port of Tianjin|Port of Qingdao|Port of Ningbo|Port of Guangzhou|Port of Los Angeles|Port of Long Beach|Port of New York|Port of Savannah|Port of Felixstowe|Port of Busan|Port of Yokohama|Port of Tokyo|Port of Osaka|Port of Kobe|Singapore|Rotterdam|Hamburg|Shanghai|Shenzhen|Tianjin|Qingdao|Ningbo|Guangzhou|Los Angeles|Long Beach|New York|Savannah|Felixstowe|Busan|Yokohama|Tokyo|Osaka|Kobe|SGSIN|NLRTM|DEHAM|CNSHA|CNSZN|CNTJN|CNQNG|CNNGB|CNGZO|USLAX|USLGB|USNYC|USSAV|GBFXT|KRPUS|JPYOK|JPTYO|JPOSA|JPUKB|GRN|Green)", Pattern.CASE_INSENSITIVE);
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+\\.?\\d*");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}|\\d{2}/\\d{2}/\\d{4}|\\d{4}/\\d{2}/\\d{2}");
    private static final Pattern SHIPMENT_PATTERN = Pattern.compile("#(\\d+)|Shipment\\s*(\\d+)|货物\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    /**
     * 从 PDF 文件提取文本
     */
    public String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(document.getNumberOfPages());
            
            String text = pdfStripper.getText(document);
            logger.info("PDF文本提取完成，文本长度: {}", text.length());
            return text;
        }
    }

    /**
     * 解析并存储 PSA 数据
     */
    public int parseAndStorePSAData(String content) {
        List<PSAData> parsedData = new ArrayList<>();
        
        // 按行分割内容（PSA数据是表格格式）
        String[] lines = content.split("\\n");
        
        // 跳过标题行
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            
            logger.debug("正在解析第 {} 行: {}", i, line.substring(0, Math.min(100, line.length())));
            
            // 尝试解析每一行的 PSA 数据
            PSAData data = parsePSADataFromLine(line);
            if (data != null) {
                parsedData.add(data);
                logger.debug("成功解析第 {} 行数据: {}", i, data.getDataId());
            } else {
                logger.debug("第 {} 行解析失败", i);
            }
        }
        
        // 存储解析的数据
        if (!parsedData.isEmpty()) {
            psaDataService.addParsedData(parsedData);
            logger.info("成功解析并存储 {} 条 PSA 数据", parsedData.size());
        }
        
        return parsedData.size();
    }

    /**
     * 从表格行中解析 PSA 数据
     */
    private PSAData parsePSADataFromLine(String line) {
        // 使用正则表达式分割，处理包含空格的字段
        String[] fields = line.split("\\s+");
        
        logger.debug("解析行数据，字段数: {}, 内容: {}", fields.length, line.substring(0, Math.min(100, line.length())));
        
        if (fields.length < 10) {
            logger.debug("行数据字段不足，跳过: {} (字段数: {})", line.substring(0, Math.min(50, line.length())), fields.length);
            return null;
        }
        
        try {
            // 简化的解析逻辑 - 直接按位置提取字段
            String operator = fields[0];
            String service = fields[1];
            String dir = fields[2];
            String bu = fields[3];
            
            // 船舶名称（可能包含空格）
            StringBuilder vesselBuilder = new StringBuilder();
            int vesselStartIndex = 4;
            int imoIndex = -1;
            
            // 找到IMO号码的位置
            for (int i = vesselStartIndex; i < fields.length; i++) {
                if (fields[i].matches("\\d{7}")) {
                    imoIndex = i;
                    break;
                }
                if (i > vesselStartIndex) {
                    vesselBuilder.append(" ");
                }
                vesselBuilder.append(fields[i]);
            }
            
            logger.debug("IMO索引: {}, 字段数: {}", imoIndex, fields.length);
            
            if (imoIndex == -1 || imoIndex + 5 >= fields.length) {
                logger.debug("无法找到有效的IMO号码，跳过: {} (IMO索引: {}, 字段数: {})", line.substring(0, Math.min(50, line.length())), imoIndex, fields.length);
                return null;
            }
            
            String vessel = vesselBuilder.toString();
            String imo = fields[imoIndex];
            String rotationNo = fields[imoIndex + 1];
            String from = fields[imoIndex + 2];
            String to = fields[imoIndex + 3];
            String berth = fields[imoIndex + 4];
            String status = fields[imoIndex + 5];
            
            logger.debug("解析字段 - 运营商: {}, 船舶: {}, IMO: {}, 从: {}, 到: {}, 状态: {}", operator, vessel, imo, from, to, status);
            
            // 提取港口信息
            String portName = extractPortFromFields(from, to);
            String portCode = generatePortCode(portName);
            
            // 检测数据类型
            String dataType = detectDataTypeFromFields(operator, service, status);
            
            // 提取数值指标
            Map<String, Object> metrics = extractMetricsFromFields(fields);
            
            // 提取时间信息
            LocalDateTime timestamp = extractTimestampFromFields(fields);
            if (timestamp == null) {
                timestamp = LocalDateTime.now();
            }
            
            // 生成数据ID
            String dataId = generateDataId(dataType, portName);
            
            // 生成描述
            String description = generateDescriptionFromFields(operator, vessel, portName, status, metrics);
            
            // 生成标签
            List<String> tags = generateTagsFromFields(operator, service, portName, status);
            
            return new PSAData(dataId, dataType, portName, portCode, timestamp, metrics, description, "Uploaded Data", tags);
            
        } catch (Exception e) {
            logger.warn("解析行数据失败: {}, 错误: {}", line, e.getMessage());
            return null;
        }
    }

    /**
     * 从段落中解析 PSA 数据
     */
    private PSAData parsePSADataFromParagraph(String paragraph) {
        String lowerParagraph = paragraph.toLowerCase();
        
        // 检测数据类型
        String dataType = detectDataType(lowerParagraph);
        if (dataType == null) return null;
        
        // 提取港口信息
        String portName = extractPortName(paragraph);
        String portCode = generatePortCode(portName);
        
        // 提取数值数据
        Map<String, Object> metrics = extractMetrics(paragraph, dataType);
        
        // 提取时间信息
        LocalDateTime timestamp = extractTimestamp(paragraph);
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        
        // 提取货物ID
        String shipmentId = extractShipmentId(paragraph);
        
        // 生成数据ID
        String dataId = generateDataId(dataType, portName);
        
        // 生成描述
        String description = generateDescription(dataType, portName, metrics);
        
        // 生成标签
        List<String> tags = generateTags(dataType, portName, metrics);
        
        return new PSAData(dataId, dataType, portName, portCode, timestamp, metrics, description, "Uploaded Data", tags);
    }

    /**
     * 检测数据类型
     */
    private String detectDataType(String content) {
        String lowerContent = content.toLowerCase();
        
        // 泊位时间相关
        if (lowerContent.contains("berth") || lowerContent.contains("泊位") || lowerContent.contains("停靠") || 
            lowerContent.contains("docking") || lowerContent.contains("mooring") || lowerContent.contains("berthing")) {
            return "berth_time";
        } 
        // 碳减排相关
        else if (lowerContent.contains("carbon") || lowerContent.contains("co2") || lowerContent.contains("碳") || 
                 lowerContent.contains("减排") || lowerContent.contains("emission") || lowerContent.contains("sustainability")) {
            return "carbon_savings";
        } 
        // 港口效率相关
        else if (lowerContent.contains("efficiency") || lowerContent.contains("效率") || lowerContent.contains("throughput") || 
                 lowerContent.contains("吞吐") || lowerContent.contains("productivity") || lowerContent.contains("performance")) {
            return "port_efficiency";
        } 
        // 货物追踪相关
        else if (lowerContent.contains("tracking") || lowerContent.contains("track") || lowerContent.contains("追踪") || 
                 lowerContent.contains("货物") || lowerContent.contains("cargo") || lowerContent.contains("shipment")) {
            return "cargo_tracking";
        } 
        // 港口拥堵相关
        else if (lowerContent.contains("congestion") || lowerContent.contains("拥堵") || lowerContent.contains("waiting") || 
                 lowerContent.contains("等待") || lowerContent.contains("queue") || lowerContent.contains("delay")) {
            return "port_congestion";
        } 
        // 船舶调度相关
        else if (lowerContent.contains("schedule") || lowerContent.contains("调度") || lowerContent.contains("vessel") || 
                 lowerContent.contains("船舶") || lowerContent.contains("ship") || lowerContent.contains("arrival")) {
            return "vessel_scheduling";
        }
        // 如果包含港口名称但没有特定关键词，默认为港口效率数据
        else if (lowerContent.contains("port") || lowerContent.contains("港") || 
                 lowerContent.contains("singapore") || lowerContent.contains("rotterdam") || 
                 lowerContent.contains("hamburg") || lowerContent.contains("shanghai")) {
            return "port_efficiency";
        }
        
        return null;
    }

    /**
     * 提取港口名称
     */
    private String extractPortName(String content) {
        Matcher matcher = PORT_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "未知港口";
    }

    /**
     * 生成港口代码
     */
    private String generatePortCode(String portName) {
        Map<String, String> portCodeMap = new HashMap<>();
        // 中文港口名称
        portCodeMap.put("新加坡港", "SGSIN");
        portCodeMap.put("鹿特丹港", "NLRTM");
        portCodeMap.put("汉堡港", "DEHAM");
        portCodeMap.put("上海港", "CNSHA");
        portCodeMap.put("深圳港", "CNSZN");
        portCodeMap.put("天津港", "CNTJN");
        portCodeMap.put("青岛港", "CNQNG");
        portCodeMap.put("宁波港", "CNNGB");
        portCodeMap.put("广州港", "CNGZO");
        portCodeMap.put("洛杉矶港", "USLAX");
        portCodeMap.put("长滩港", "USLGB");
        portCodeMap.put("纽约港", "USNYC");
        portCodeMap.put("萨凡纳港", "USSAV");
        portCodeMap.put("费利克斯托港", "GBFXT");
        portCodeMap.put("釜山港", "KRPUS");
        portCodeMap.put("横滨港", "JPYOK");
        portCodeMap.put("东京港", "JPTYO");
        portCodeMap.put("大阪港", "JPOSA");
        portCodeMap.put("神户港", "JPUKB");
        portCodeMap.put("绿色港口", "GRN");
        
        // 英文港口名称
        portCodeMap.put("Port of Singapore", "SGSIN");
        portCodeMap.put("Port of Rotterdam", "NLRTM");
        portCodeMap.put("Port of Hamburg", "DEHAM");
        portCodeMap.put("Port of Shanghai", "CNSHA");
        portCodeMap.put("Port of Shenzhen", "CNSZN");
        portCodeMap.put("Port of Tianjin", "CNTJN");
        portCodeMap.put("Port of Qingdao", "CNQNG");
        portCodeMap.put("Port of Ningbo", "CNNGB");
        portCodeMap.put("Port of Guangzhou", "CNGZO");
        portCodeMap.put("Port of Los Angeles", "USLAX");
        portCodeMap.put("Port of Long Beach", "USLGB");
        portCodeMap.put("Port of New York", "USNYC");
        portCodeMap.put("Port of Savannah", "USSAV");
        portCodeMap.put("Port of Felixstowe", "GBFXT");
        portCodeMap.put("Port of Busan", "KRPUS");
        portCodeMap.put("Port of Yokohama", "JPYOK");
        portCodeMap.put("Port of Tokyo", "JPTYO");
        portCodeMap.put("Port of Osaka", "JPOSA");
        portCodeMap.put("Port of Kobe", "JPUKB");
        
        // 城市名称
        portCodeMap.put("Singapore", "SGSIN");
        portCodeMap.put("Rotterdam", "NLRTM");
        portCodeMap.put("Hamburg", "DEHAM");
        portCodeMap.put("Shanghai", "CNSHA");
        portCodeMap.put("Shenzhen", "CNSZN");
        portCodeMap.put("Tianjin", "CNTJN");
        portCodeMap.put("Qingdao", "CNQNG");
        portCodeMap.put("Ningbo", "CNNGB");
        portCodeMap.put("Guangzhou", "CNGZO");
        portCodeMap.put("Los Angeles", "USLAX");
        portCodeMap.put("Long Beach", "USLGB");
        portCodeMap.put("New York", "USNYC");
        portCodeMap.put("Savannah", "USSAV");
        portCodeMap.put("Felixstowe", "GBFXT");
        portCodeMap.put("Busan", "KRPUS");
        portCodeMap.put("Yokohama", "JPYOK");
        portCodeMap.put("Tokyo", "JPTYO");
        portCodeMap.put("Osaka", "JPOSA");
        portCodeMap.put("Kobe", "JPUKB");
        portCodeMap.put("GRN", "GRN");
        portCodeMap.put("Green", "GRN");
        
        // 港口代码
        portCodeMap.put("SGSIN", "SGSIN");
        portCodeMap.put("NLRTM", "NLRTM");
        portCodeMap.put("DEHAM", "DEHAM");
        portCodeMap.put("CNSHA", "CNSHA");
        portCodeMap.put("USLAX", "USLAX");
        portCodeMap.put("USLGB", "USLGB");
        portCodeMap.put("GBFXT", "GBFXT");
        
        return portCodeMap.getOrDefault(portName, "UNKNOWN");
    }

    /**
     * 提取数值指标
     */
    private Map<String, Object> extractMetrics(String content, String dataType) {
        Map<String, Object> metrics = new HashMap<>();
        Matcher numberMatcher = NUMBER_PATTERN.matcher(content);
        
        List<String> numbers = new ArrayList<>();
        while (numberMatcher.find()) {
            numbers.add(numberMatcher.group());
        }
        
        // 根据数据类型分配数值
        switch (dataType) {
            case "berth_time":
                if (numbers.size() >= 1) metrics.put("average_berth_time", numbers.get(0));
                if (numbers.size() >= 2) metrics.put("current_waiting", numbers.get(1));
                if (numbers.size() >= 3) metrics.put("efficiency_score", numbers.get(2));
                break;
            case "carbon_savings":
                if (numbers.size() >= 1) metrics.put("co2_reduction", numbers.get(0));
                if (numbers.size() >= 2) metrics.put("fuel_savings", numbers.get(1));
                if (numbers.size() >= 3) metrics.put("efficiency_gain", numbers.get(2));
                break;
            case "port_efficiency":
                if (numbers.size() >= 1) metrics.put("throughput_per_hour", numbers.get(0));
                if (numbers.size() >= 2) metrics.put("crane_utilization", numbers.get(1));
                if (numbers.size() >= 3) metrics.put("berth_occupancy", numbers.get(2));
                break;
            case "cargo_tracking":
                if (numbers.size() >= 1) metrics.put("shipment_id", numbers.get(0));
                break;
            case "port_congestion":
                if (numbers.size() >= 1) metrics.put("waiting_time", numbers.get(0));
                if (numbers.size() >= 2) metrics.put("queue_length", numbers.get(1));
                break;
            case "vessel_scheduling":
                if (numbers.size() >= 1) metrics.put("scheduled_arrivals", numbers.get(0));
                if (numbers.size() >= 2) metrics.put("scheduled_departures", numbers.get(1));
                break;
        }
        
        return metrics;
    }

    /**
     * 提取时间戳
     */
    private LocalDateTime extractTimestamp(String content) {
        Matcher dateMatcher = DATE_PATTERN.matcher(content);
        if (dateMatcher.find()) {
            try {
                String dateStr = dateMatcher.group();
                if (dateStr.contains("-")) {
                    return LocalDateTime.parse(dateStr + "T00:00:00");
                } else if (dateStr.contains("/")) {
                    String[] parts = dateStr.split("/");
                    if (parts.length == 3) {
                        String formattedDate = parts[2] + "-" + parts[1] + "-" + parts[0];
                        return LocalDateTime.parse(formattedDate + "T00:00:00");
                    }
                }
            } catch (Exception e) {
                logger.warn("时间解析失败: {}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * 提取货物ID
     */
    private String extractShipmentId(String content) {
        Matcher matcher = SHIPMENT_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
        }
        return null;
    }

    /**
     * 生成数据ID
     */
    private String generateDataId(String dataType, String portName) {
        String prefix = dataType.toUpperCase().substring(0, 2);
        String portPrefix = portName.substring(0, Math.min(2, portName.length()));
        return prefix + portPrefix + System.currentTimeMillis() % 10000;
    }

    /**
     * 生成描述
     */
    private String generateDescription(String dataType, String portName, Map<String, Object> metrics) {
        StringBuilder desc = new StringBuilder();
        desc.append(portName).append("的");
        
        switch (dataType) {
            case "berth_time":
                desc.append("泊位时间数据");
                if (metrics.containsKey("average_berth_time")) {
                    desc.append("，平均泊位时间").append(metrics.get("average_berth_time")).append("小时");
                }
                break;
            case "carbon_savings":
                desc.append("碳减排数据");
                if (metrics.containsKey("co2_reduction")) {
                    desc.append("，CO2减排").append(metrics.get("co2_reduction")).append("%");
                }
                break;
            case "port_efficiency":
                desc.append("港口效率数据");
                if (metrics.containsKey("throughput_per_hour")) {
                    desc.append("，每小时吞吐量").append(metrics.get("throughput_per_hour")).append("个集装箱");
                }
                break;
            case "cargo_tracking":
                desc.append("货物追踪数据");
                if (metrics.containsKey("shipment_id")) {
                    desc.append("，货物ID #").append(metrics.get("shipment_id"));
                }
                break;
            case "port_congestion":
                desc.append("港口拥堵数据");
                if (metrics.containsKey("waiting_time")) {
                    desc.append("，等待时间").append(metrics.get("waiting_time")).append("小时");
                }
                break;
            case "vessel_scheduling":
                desc.append("船舶调度数据");
                if (metrics.containsKey("scheduled_arrivals")) {
                    desc.append("，计划到达").append(metrics.get("scheduled_arrivals")).append("艘船");
                }
                break;
        }
        
        return desc.toString();
    }

    /**
     * 生成标签
     */
    private List<String> generateTags(String dataType, String portName, Map<String, Object> metrics) {
        List<String> tags = new ArrayList<>();
        tags.add(dataType);
        tags.add(portName.toLowerCase().replace("港", "").replace("port of ", ""));
        
        if (metrics.containsKey("shipment_id")) {
            tags.add("shipment");
        }
        if (metrics.containsKey("efficiency_score")) {
            tags.add("efficiency");
        }
        if (metrics.containsKey("co2_reduction")) {
            tags.add("sustainability");
        }
        
        return tags;
    }

    /**
     * 从字段中提取港口信息
     */
    private String extractPortFromFields(String from, String to) {
        // 优先使用to字段，如果为空则使用from字段
        String port = to != null && !to.isEmpty() ? to : from;
        
        // 港口代码映射
        Map<String, String> portCodeMap = new HashMap<>();
        portCodeMap.put("JPTYO", "东京港");
        portCodeMap.put("USSEA", "西雅图港");
        portCodeMap.put("USLAX", "洛杉矶港");
        portCodeMap.put("AEJEA", "杰贝阿里港");
        portCodeMap.put("ESALG", "阿尔赫西拉斯港");
        portCodeMap.put("PAPTY", "巴拿马港");
        portCodeMap.put("ITGIT", "伊斯坦布尔港");
        portCodeMap.put("KRPUS", "釜山港");
        portCodeMap.put("GRPIR", "比雷埃夫斯港");
        portCodeMap.put("BRSSZ", "桑托斯港");
        portCodeMap.put("VNSGN", "胡志明市港");
        portCodeMap.put("DEHAM", "汉堡港");
        portCodeMap.put("AEKHL", "哈利法港");
        portCodeMap.put("MYPKG", "巴生港");
        portCodeMap.put("CNTXG", "天津港");
        portCodeMap.put("ESVLC", "瓦伦西亚港");
        portCodeMap.put("GBSOU", "南安普顿港");
        portCodeMap.put("CNQDG", "青岛港");
        portCodeMap.put("BEANR", "安特卫普港");
        portCodeMap.put("NLRTM", "鹿特丹港");
        portCodeMap.put("CNSZX", "深圳港");
        portCodeMap.put("CNNBG", "宁波港");
        portCodeMap.put("CNXMN", "厦门港");
        portCodeMap.put("JPOSA", "大阪港");
        portCodeMap.put("HKHKG", "香港港");
        portCodeMap.put("CNSHA", "上海港");
        portCodeMap.put("THLCB", "林查班港");
        portCodeMap.put("TWKHH", "高雄港");
        portCodeMap.put("INMUM", "孟买港");
        portCodeMap.put("SGSIN", "新加坡港");
        
        return portCodeMap.getOrDefault(port, port);
    }

    /**
     * 从字段中检测数据类型
     */
    private String detectDataTypeFromFields(String operator, String service, String status) {
        // 根据运营商和服务类型判断数据类型
        if (operator.equals("GRN")) {
            return "carbon_savings"; // 绿色港口相关
        } else if (status.equals("DEPARTED")) {
            return "vessel_scheduling"; // 船舶调度
        } else if (service.contains("DF5") || service.contains("KP9")) {
            return "berth_time"; // 泊位时间
        } else if (service.contains("HWI") || service.contains("59H")) {
            return "port_efficiency"; // 港口效率
        } else if (service.contains("15P") || service.contains("5RC")) {
            return "cargo_tracking"; // 货物追踪
        } else {
            return "port_efficiency"; // 默认为港口效率
        }
    }

    /**
     * 从字段中提取数值指标
     */
    private Map<String, Object> extractMetricsFromFields(String[] fields) {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // 根据字段位置提取数值
            if (fields.length > 11) metrics.put("btr_hours", fields[11]);
            if (fields.length > 12) metrics.put("abt_hours", fields[12]);
            if (fields.length > 13) metrics.put("atb_hours", fields[13]);
            if (fields.length > 14) metrics.put("atu_hours", fields[14]);
            if (fields.length > 15) metrics.put("arrival_variance", fields[15]);
            if (fields.length > 16) metrics.put("arrival_accuracy", fields[16]);
            if (fields.length > 17) metrics.put("wait_time_atb_btr", fields[17]);
            if (fields.length > 18) metrics.put("wait_time_abt_btr", fields[18]);
            if (fields.length > 19) metrics.put("wait_time_atb_abt", fields[19]);
            if (fields.length > 20) metrics.put("berth_time", fields[20]);
            if (fields.length > 21) metrics.put("assured_port_time", fields[21]);
            if (fields.length > 22) metrics.put("bunker_saved_usd", fields[22]);
            if (fields.length > 23) metrics.put("carbon_abatement_tonnes", fields[23]);
            
            // 添加船舶信息
            if (fields.length > 4) metrics.put("vessel_name", fields[4]);
            if (fields.length > 5) metrics.put("imo", fields[5]);
            if (fields.length > 6) metrics.put("rotation_no", fields[6]);
            
        } catch (Exception e) {
            logger.warn("提取数值指标失败: {}", e.getMessage());
        }
        
        return metrics;
    }

    /**
     * 从字段中提取时间戳
     */
    private LocalDateTime extractTimestampFromFields(String[] fields) {
        try {
            // 尝试从BTR字段提取时间
            if (fields.length > 11) {
                String btrField = fields[11];
                if (btrField.contains("-") && btrField.contains(":")) {
                    // 解析格式如 "23-04-25 16:25"
                    String[] dateTimeParts = btrField.split(" ");
                    if (dateTimeParts.length == 2) {
                        String[] dateParts = dateTimeParts[0].split("-");
                        if (dateParts.length == 3) {
                            String year = "20" + dateParts[2];
                            String month = dateParts[1];
                            String day = dateParts[0];
                            String time = dateTimeParts[1];
                            
                            return LocalDateTime.parse(year + "-" + month + "-" + day + "T" + time);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("时间解析失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 从字段生成描述
     */
    private String generateDescriptionFromFields(String operator, String vessel, String portName, String status, Map<String, Object> metrics) {
        StringBuilder desc = new StringBuilder();
        desc.append(operator).append(" 运营的 ").append(vessel).append(" 在 ").append(portName);
        
        if (status.equals("DEPARTED")) {
            desc.append(" 已出发");
        } else {
            desc.append(" 状态: ").append(status);
        }
        
        if (metrics.containsKey("berth_time")) {
            desc.append("，泊位时间: ").append(metrics.get("berth_time")).append(" 小时");
        }
        
        if (metrics.containsKey("bunker_saved_usd")) {
            desc.append("，节省燃料费用: $").append(metrics.get("bunker_saved_usd"));
        }
        
        if (metrics.containsKey("carbon_abatement_tonnes")) {
            desc.append("，碳减排: ").append(metrics.get("carbon_abatement_tonnes")).append(" 吨");
        }
        
        return desc.toString();
    }

    /**
     * 从字段生成标签
     */
    private List<String> generateTagsFromFields(String operator, String service, String portName, String status) {
        List<String> tags = new ArrayList<>();
        tags.add(operator.toLowerCase());
        tags.add(service.toLowerCase());
        tags.add(portName.toLowerCase());
        tags.add(status.toLowerCase());
        
        if (operator.equals("GRN")) {
            tags.add("green");
            tags.add("sustainability");
        }
        
        return tags;
    }

    /**
     * 获取上传统计信息
     */
    public Map<String, Object> getUploadStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDataTypes", psaDataService.getAvailableDataTypes().size());
        stats.put("totalPorts", psaDataService.getAvailablePorts().size());
        stats.put("availableDataTypes", psaDataService.getAvailableDataTypes());
        stats.put("availablePorts", psaDataService.getAvailablePorts());
        return stats;
    }
}
