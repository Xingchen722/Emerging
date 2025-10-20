# PowerBI 配置指南

## 🎯 概述

本项目已集成PowerBI服务，支持从PowerBI获取真实的港口运营数据。目前配置为演示模式，使用模拟数据。

## 🔧 当前配置状态

### ✅ 已完成的配置：
- PowerBI服务类已实现
- 演示模式配置已设置
- 模拟数据生成功能已实现
- 错误处理机制已完善

### 📊 支持的PowerBI数据类型：
1. **泊位时间数据** (berth_time)
2. **碳减排数据** (carbon_savings)
3. **港口效率数据** (port_efficiency)
4. **货物追踪数据** (cargo_tracking)
5. **港口拥堵数据** (port_congestion)
6. **船舶调度数据** (vessel_scheduling)

## 🚀 当前功能

### 演示模式
- 使用模拟数据展示PowerBI功能
- 支持所有PSA业务类型查询
- 自动根据查询内容返回相关模拟数据

### 模拟数据示例
```json
{
  "dataId": "PBI_BT001",
  "dataType": "berth_time",
  "portName": "新加坡港",
  "portCode": "SGSIN",
  "description": "Power BI数据：新加坡港泊位时间信息",
  "source": "Power BI",
  "metrics": {
    "average_berth_time": "4.2",
    "current_waiting": "2",
    "efficiency_score": "85"
  }
}
```

## 🔧 配置真实PowerBI服务

如果您有真实的PowerBI服务，请按以下步骤配置：

### 1. 获取PowerBI配置信息

从您的Azure PowerBI服务中获取：
- **Client ID**: 应用程序ID
- **Client Secret**: 应用程序密钥
- **Tenant ID**: Azure AD租户ID
- **Workspace ID**: PowerBI工作区ID
- **Report ID**: 报告ID

### 2. 更新config.env文件

```bash
# 替换为您的真实PowerBI配置
POWERBI_CLIENT_ID=your-real-client-id
POWERBI_WORKSPACE_ID=your-real-workspace-id
POWERBI_REPORT_ID=your-real-report-id
POWERBI_CLIENT_SECRET=your-real-client-secret
POWERBI_TENANT_ID=your-real-tenant-id
POWERBI_AUTHORITY_URL=https://login.microsoftonline.com/your-real-tenant-id
POWERBI_DASHBOARD_URL=https://your-real-dashboard-url.com
```

### 3. 重启应用

```bash
./start-with-config.sh
```

## 🧪 测试PowerBI功能

### 1. 检查服务状态
```bash
curl http://localhost:8080/api/chat/system/status
```

### 2. 测试数据查询
```bash
curl -X POST http://localhost:8080/api/chat/message \
  -H "Content-Type: application/json" \
  -d '{"message": "查询港口效率数据", "language": "zh"}'
```

### 3. 测试关键词提取
```bash
curl -X POST http://localhost:8080/api/chat/extract-keywords \
  -H "Content-Type: application/json" \
  -d '{"message": "新加坡港的泊位时间如何？", "language": "zh"}'
```

## 📝 PowerBI数据模型

### PSAData实体结构
```java
public class PSAData {
    private String dataId;           // 数据ID
    private String dataType;         // 数据类型
    private String portName;         // 港口名称
    private String portCode;         // 港口代码
    private LocalDateTime timestamp; // 时间戳
    private Map<String, Object> metrics; // 指标数据
    private String description;      // 描述
    private String source;           // 数据源
    private List<String> tags;       // 标签
}
```

### 支持的查询类型
- `EVALUATE BerthTimeData` - 泊位时间数据
- `EVALUATE CarbonSavingsData` - 碳减排数据
- `EVALUATE PortEfficiencyData` - 港口效率数据
- `EVALUATE CargoTrackingData` - 货物追踪数据
- `EVALUATE PortCongestionData` - 港口拥堵数据
- `EVALUATE VesselSchedulingData` - 船舶调度数据

## 🔍 故障排除

### 常见问题

1. **PowerBI服务不可用**
   - 检查配置是否正确
   - 确认网络连接
   - 验证Azure AD权限

2. **认证失败**
   - 检查Client ID和Secret
   - 确认Tenant ID正确
   - 验证应用程序权限

3. **数据查询失败**
   - 检查Workspace ID和Report ID
   - 确认报告权限
   - 验证查询语法

### 调试步骤

1. 检查应用日志：
   ```bash
   tail -f logs/application.log
   ```

2. 测试PowerBI连接：
   ```bash
   curl -X POST http://localhost:8080/api/chat/message \
     -H "Content-Type: application/json" \
     -d '{"message": "测试PowerBI连接", "language": "zh"}'
   ```

## 🎉 成功标志

当您看到以下信息时，表示PowerBI配置成功：

```
✅ PowerBI服务: 可用 (真实模式)
✅ 数据查询: 正常
✅ 模拟数据: 已禁用
```

或者演示模式：

```
ℹ️ PowerBI服务: 演示模式
✅ 模拟数据: 可用
✅ 功能展示: 正常
```

## 📚 相关文档

- [Azure PowerBI REST API](https://docs.microsoft.com/en-us/rest/api/power-bi/)
- [PowerBI Embedded](https://docs.microsoft.com/en-us/power-bi/developer/embedded/)
- [Azure AD认证](https://docs.microsoft.com/en-us/azure/active-directory/)
