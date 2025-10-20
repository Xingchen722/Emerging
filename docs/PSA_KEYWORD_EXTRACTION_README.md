# PSA关键词提取和数据查询系统

## 🎯 系统概述

本系统实现了您要求的功能：**用户问一句话 → 从问题里提取关键词 → 在PSA官方数据中找到对应内容 → 返回AI生成的自然语言解释**。

## 🏗️ 系统架构

### 三层架构设计

| 层级 | 功能 | 说明 |
|------|------|------|
| **1️⃣ 前端** | 用户输入问题、展示回答 | HTML/JS界面，支持多语言 |
| **2️⃣ 后端逻辑** | 关键词提取 → 数据匹配 → AI回复 | Spring Boot + 智能服务 |
| **3️⃣ 数据层** | 存放PSA数据 | 模拟Power BI数据格式 |

## 🚀 核心功能

### 1. 智能关键词提取
- **PSA业务关键词**：berth_time（泊位时间）、carbon_savings（碳减排）、port_efficiency（港口效率）等
- **港口关键词**：新加坡港、鹿特丹港、汉堡港、上海港等
- **时间关键词**：今天、明天、本周、紧急等
- **状态关键词**：已到达、运输中、延误、完成等
- **货物ID提取**：自动识别货物编号（如#342）

### 2. PSA数据查询
- **泊位时间数据**：平均泊位时间、等待船舶数量、效率评分
- **碳减排数据**：CO2减排百分比、燃料节省、效率提升
- **港口效率数据**：每小时吞吐量、起重机利用率、泊位占用率
- **货物追踪数据**：货物状态、当前位置、预计到达时间
- **港口拥堵数据**：等待时间、排队船舶、拥堵等级
- **船舶调度数据**：计划到达/出发、下次到达时间

### 3. 多语言AI回复
- 支持中文、英文、德文等多种语言
- 根据数据类型生成结构化回复
- 包含实时数据和更新时间

## 📁 文件结构

```
src/main/java/com/networkinsights/ai/
├── service/
│   ├── KeywordExtractionService.java    # 关键词提取服务
│   ├── PSADataService.java             # PSA数据查询服务
│   ├── AIService.java                  # 增强的AI服务
│   └── TranslationService.java         # 翻译服务
├── model/
│   └── PSAData.java                    # PSA数据模型
└── controller/
    └── ChatController.java             # 聊天控制器（新增API端点）

前端文件：
├── index.html                          # 主页面
├── script.js                           # 增强的JavaScript（支持PSA数据）
├── style.css                           # 样式文件
└── demo.html                           # 关键词提取演示页面
```

## 🔧 新增API端点

### 关键词提取
```http
POST /api/chat/extract-keywords
Content-Type: application/json

"新加坡港的泊位时间是多少？"
```

### PSA数据查询
```http
POST /api/chat/query-by-question?question=新加坡港的泊位时间
POST /api/chat/query-psa-data
GET /api/chat/psa-data-types
GET /api/chat/psa-ports
GET /api/chat/psa-data/by-type/{dataType}
GET /api/chat/psa-data/by-port/{portName}
```

## 💡 使用示例

### 示例问题1：泊位时间查询
**用户输入**：`"新加坡港的泊位时间是多少？"`

**系统处理**：
1. 提取关键词：`berth_time`, `新加坡港`
2. 查询数据：找到新加坡港的泊位时间数据
3. 生成回复：
```
📊 新加坡港泊位时间信息：
• 平均泊位时间：4.2小时
• 当前等待船舶：2艘
• 效率评分：85分
• 更新时间：2024-01-14 14:30:00
```

### 示例问题2：碳减排数据
**用户输入**：`"查询鹿特丹港的碳减排数据"`

**系统处理**：
1. 提取关键词：`carbon_savings`, `鹿特丹港`
2. 查询数据：找到鹿特丹港的碳减排数据
3. 生成回复：
```
🌱 鹿特丹港碳减排成果：
• CO2减排：18.7%
• 燃料节省：11.2%
• 效率提升：15%
• 更新时间：2024-01-14 15:45:00
```

### 示例问题3：货物追踪
**用户输入**：`"货物#342的追踪状态"`

**系统处理**：
1. 提取关键词：`cargo_tracking`, `342`
2. 查询数据：找到货物#342的追踪数据
3. 生成回复：
```
🚢 货物#342追踪信息：
• 状态：in_transit
• 当前位置：Pacific Ocean
• 预计到达：2024-01-15 18:00
• 更新时间：2024-01-14 16:20:00
```

## 🚀 快速开始

### 1. 启动后端服务
```bash
# 编译项目
mvn clean compile

# 启动Spring Boot应用
mvn spring-boot:run
```

### 2. 访问演示页面
打开浏览器访问：`http://localhost:8080/demo.html`

### 3. 测试功能
1. 在输入框中输入问题
2. 点击"提取关键词"查看提取结果
3. 点击"查询PSA数据"查看数据查询结果

## 🔍 关键词提取规则

### PSA业务关键词映射
- `berth_time`: 泊位时间、靠泊时间、停靠时间
- `carbon_savings`: 碳减排、碳排放、环保、绿色
- `port_efficiency`: 港口效率、处理效率、装卸效率
- `cargo_tracking`: 货物追踪、货物状态、物流追踪
- `port_congestion`: 港口拥堵、港口延误、排队时间
- `vessel_scheduling`: 船舶调度、船期、航行计划

### 港口关键词
支持主要港口：新加坡港、鹿特丹港、汉堡港、上海港、洛杉矶港、长滩港、费利克斯托港、釜山港等

### 时间关键词
支持：今天、明天、昨天、本周、下周、上午、下午、晚上、紧急、立即等

## 📊 数据格式

### PSAData模型
```java
{
    "dataId": "BT001",
    "dataType": "berth_time",
    "portName": "新加坡港",
    "portCode": "SGSIN",
    "timestamp": "2024-01-14T14:30:00",
    "metrics": {
        "average_berth_time": "4.2",
        "current_waiting": "2",
        "efficiency_score": "85"
    },
    "description": "新加坡港当前平均泊位时间为4.2小时...",
    "source": "Mock Data",
    "tags": ["efficiency", "singapore", "berth"]
}
```

## 🌐 多语言支持

系统支持以下语言的AI回复：
- 中文（zh）
- 英文（en）
- 德文（de）
- 法文（fr）
- 日文（ja）
- 韩文（ko）
- 西班牙文（es）
- 意大利文（it）
- 葡萄牙文（pt）
- 俄文（ru）

## 🔧 扩展功能

### 1. 添加新的关键词类型
在`KeywordExtractionService.java`中的`initializePSAKeywords()`方法中添加新的关键词映射。

### 2. 添加新的数据类型
在`PSADataService.java`中的`initializeMockPSAData()`方法中添加新的数据类型和模拟数据。

### 3. 集成真实Power BI数据
替换`PSADataService`中的模拟数据，连接到真实的Power BI数据源。

## 🎯 下一步计划

1. **集成真实数据源**：连接Power BI或PSA官方API
2. **增强关键词提取**：使用NLP模型提高提取准确性
3. **添加更多港口**：扩展港口数据库
4. **实时数据更新**：实现数据的实时同步
5. **数据可视化**：添加图表和仪表板功能

## 📞 技术支持

如有问题或建议，请联系开发团队。

---

**系统特点**：
- ✅ 智能关键词提取
- ✅ 多语言支持
- ✅ 结构化数据查询
- ✅ 自然语言回复
- ✅ 易于扩展
- ✅ 演示友好
