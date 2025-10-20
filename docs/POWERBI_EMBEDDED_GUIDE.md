# Power BI Embedded 实时分析系统使用说明

## 概述

本系统实现了Power BI Embedded集成、实时数据获取和AI分析功能，为PSA港口运营提供智能化的数据分析和决策支持。

## 功能特性

### 🚀 核心功能

1. **Power BI Embedded集成**
   - 与Power BI仪表板直接交互
   - 支持工作区、报告、数据集管理
   - DAX查询执行
   - 实时数据缓存机制

2. **实时数据获取**
   - 港口运营数据实时监控
   - 船舶调度数据获取
   - 碳减排数据追踪
   - 智能缓存管理

3. **AI智能分析**
   - 基于OpenAI的智能分析
   - 港口运营效率分析
   - 船舶调度优化建议
   - 碳减排效果评估
   - 综合运营分析

## 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Dashboard │    │   REST API      │    │   Power BI      │
│                 │◄──►│                 │◄──►│   Embedded      │
│ powerbi-dashboard│    │ PowerBIController│    │   Service      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │  Real-time AI   │
                       │  Analysis       │
                       │  Service        │
                       └─────────────────┘
```

## API 端点

### Power BI 管理端点

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/powerbi/status` | GET | 获取服务状态 |
| `/api/powerbi/workspace` | GET | 获取工作区信息 |
| `/api/powerbi/reports` | GET | 获取报告列表 |
| `/api/powerbi/datasets` | GET | 获取数据集列表 |
| `/api/powerbi/query` | POST | 执行DAX查询 |

### 实时数据端点

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/powerbi/data/port/{portCode}` | GET | 获取指定港口实时数据 |
| `/api/powerbi/data/vessels` | GET | 获取船舶调度数据 |
| `/api/powerbi/data/carbon` | GET | 获取碳减排数据 |

### AI分析端点

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/powerbi/analysis/port/{portCode}` | GET | 港口运营分析 |
| `/api/powerbi/analysis/vessels` | GET | 船舶调度分析 |
| `/api/powerbi/analysis/carbon` | GET | 碳减排分析 |
| `/api/powerbi/analysis/comprehensive/{portCode}` | GET | 综合运营分析 |

### 缓存管理端点

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/powerbi/cache/stats` | GET | 获取缓存统计 |
| `/api/powerbi/cache/clean` | POST | 清理过期缓存 |

## 使用指南

### 1. 启动系统

```bash
# 启动Spring Boot应用
mvn spring-boot:run

# 或使用脚本启动
./start-simple.sh
```

### 2. 访问测试仪表板

打开浏览器访问：`http://localhost:8080/powerbi-dashboard.html`

### 3. API使用示例

#### 获取服务状态
```bash
curl http://localhost:8080/api/powerbi/status
```

#### 获取工作区信息
```bash
curl http://localhost:8080/api/powerbi/workspace
```

#### 获取实时港口数据
```bash
curl "http://localhost:8080/api/powerbi/data/port/SGSIN?dataType=port_efficiency"
```

#### 执行DAX查询
```bash
curl -X POST http://localhost:8080/api/powerbi/query \
  -H "Content-Type: application/json" \
  -d '{"query": "EVALUATE \u0027PortData\u0027"}'
```

#### 执行港口分析
```bash
curl "http://localhost:8080/api/powerbi/analysis/port/SGSIN?analysisType=port_efficiency"
```

## 配置说明

### Power BI配置

在 `application.yml` 中配置Power BI参数：

```yaml
powerbi:
  embedded:
    client-id: ${POWERBI_CLIENT_ID:demo-client-id}
    workspace-id: ${POWERBI_WORKSPACE_ID:demo-workspace-id}
    report-id: ${POWERBI_REPORT_ID:demo-report-id}
    client-secret: ${POWERBI_CLIENT_SECRET:demo-client-secret}
    tenant-id: ${POWERBI_TENANT_ID:demo-tenant-id}
    authority-url: ${POWERBI_AUTHORITY_URL:https://login.microsoftonline.com/demo-tenant-id}
    scope: https://analysis.windows.net/powerbi/api/.default
    dashboard-url: ${POWERBI_DASHBOARD_URL:https://demo-dashboard-url.com}
```

### 环境变量配置

在 `config.env` 中设置：

```bash
# Power BI配置
POWERBI_CLIENT_ID=your-client-id
POWERBI_WORKSPACE_ID=your-workspace-id
POWERBI_REPORT_ID=your-report-id
POWERBI_CLIENT_SECRET=your-client-secret
POWERBI_TENANT_ID=your-tenant-id
POWERBI_AUTHORITY_URL=https://login.microsoftonline.com/your-tenant-id
POWERBI_DASHBOARD_URL=https://your-dashboard-url.com
```

## 演示模式

系统支持演示模式，当使用默认配置时会：

1. 返回模拟的Power BI数据
2. 提供演示用的工作区、报告、数据集信息
3. 生成模拟的港口运营数据
4. 执行AI分析并提供智能建议

## 数据格式

### PSA数据格式

```json
{
  "dataId": "PBI_PORT_SGSIN_1760920380860",
  "dataType": "port_efficiency",
  "portName": "新加坡港",
  "portCode": "SGSIN",
  "timestamp": "2025-10-20T08:33:00.861326",
  "metrics": {
    "泊位占用率": "75%",
    "吞吐量": "100 集装箱/小时",
    "起重机利用率": "85%",
    "效率评分": "80分"
  },
  "source": "Power BI Embedded (演示)"
}
```

### AI分析结果格式

```json
{
  "portCode": "SGSIN",
  "analysisType": "port_efficiency",
  "timestamp": "2025-10-20T08:33:07.430065",
  "dataCount": 5,
  "rawData": [...],
  "aiInsights": "AI分析结果...",
  "recommendations": ["建议1", "建议2", "建议3"],
  "trends": {
    "数据趋势": "基于5条数据进行分析",
    "时间范围": "最近24小时",
    "趋势方向": "稳定上升",
    "变化率": "+5.2%"
  },
  "alerts": ["📊 港口效率数据正常"]
}
```

## 支持的港口代码

| 港口代码 | 港口名称 | 国家 |
|----------|----------|------|
| SGSIN | 新加坡港 | 新加坡 |
| NLRTM | 鹿特丹港 | 荷兰 |
| DEHAM | 汉堡港 | 德国 |
| USLAX | 洛杉矶港 | 美国 |
| CNSHA | 上海港 | 中国 |

## 支持的数据类型

| 数据类型 | 描述 |
|----------|------|
| port_efficiency | 港口效率 |
| berth_time | 泊位时间 |
| carbon_savings | 碳减排 |
| vessel_scheduling | 船舶调度 |
| port_congestion | 港口拥堵 |
| cargo_tracking | 货物追踪 |

## 故障排除

### 常见问题

1. **API返回404错误**
   - 检查应用是否正常启动
   - 确认端口8080未被占用
   - 查看应用日志

2. **Power BI认证失败**
   - 检查配置参数是否正确
   - 确认Azure AD应用注册
   - 验证权限设置

3. **数据获取失败**
   - 检查网络连接
   - 验证Power BI服务状态
   - 查看缓存设置

### 日志查看

```bash
# 查看应用日志
tail -f logs/application.log

# 或查看控制台输出
mvn spring-boot:run
```

## 扩展开发

### 添加新的数据类型

1. 在 `PowerBIEmbeddedService` 中添加新的数据获取方法
2. 在 `RealTimeAIAnalysisService` 中添加对应的分析方法
3. 在 `PowerBIController` 中添加新的API端点
4. 更新测试页面

### 自定义AI分析

1. 修改 `RealTimeAIAnalysisService` 中的分析逻辑
2. 调整OpenAI提示词
3. 添加新的分析指标
4. 更新结果格式

## 性能优化

1. **缓存策略**
   - 数据缓存时间：5分钟
   - 自动清理过期缓存
   - 支持手动缓存管理

2. **并发处理**
   - 使用CompletableFuture异步处理
   - 支持高并发请求
   - 资源池管理

3. **错误处理**
   - 优雅降级机制
   - 详细错误日志
   - 用户友好的错误信息

## 安全考虑

1. **API密钥管理**
   - 使用环境变量存储敏感信息
   - 避免硬编码密钥
   - 定期轮换密钥

2. **访问控制**
   - 实施适当的权限验证
   - 限制API访问频率
   - 监控异常访问

3. **数据保护**
   - 敏感数据加密
   - 安全的传输协议
   - 数据访问审计

## 联系支持

如有问题或建议，请联系开发团队或查看项目文档。

---

**版本**: 1.0.0  
**最后更新**: 2025-10-20  
**维护者**: PSA开发团队
