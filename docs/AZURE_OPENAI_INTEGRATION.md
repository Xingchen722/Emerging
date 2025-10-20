# Azure OpenAI 集成指南

## 🎯 概述

本项目已成功集成Azure OpenAI API，支持智能对话、关键词提取和基于PSA数据的智能回复功能。

## 🔧 配置步骤

### 1. 更新config.env文件

编辑 `config.env` 文件，将以下占位符替换为您的实际Azure OpenAI配置：

```bash
# Azure OpenAI API配置
OPENAI_PRIMARY_KEY=c4bf6f6756ec4ae1b703c755f03ee4c9
OPENAI_SECONDARY_KEY=e84a603883ae4df0a1679c4e88bb30ef
AZURE_OPENAI_ENDPOINT=https://your-actual-resource.openai.azure.com  # 替换为您的实际端点
AZURE_OPENAI_API_VERSION=2024-02-15-preview
AZURE_OPENAI_DEPLOYMENT=gpt-4-1-nano
```

**重要**：请将 `AZURE_OPENAI_ENDPOINT` 替换为您从hackathon获得的实际Azure OpenAI端点URL。

### 2. 启动应用

使用以下命令启动应用：

```bash
./start-with-config.sh
```

### 3. 测试API连接

在启动应用之前，可以先测试API连接：

```bash
./test-azure-api.sh
```

## 🚀 功能特性

### 1. 智能对话
- 支持中英文对话
- 专业的PSA港口运营助手
- 上下文感知的回复

### 2. 关键词提取
- 自动提取PSA相关关键词
- 支持港口名称、业务类型、时间信息等
- JSON格式输出

### 3. 基于数据的智能回复
- 结合PSA数据进行智能分析
- 提供数据驱动的专业建议
- 支持多语言输出

## 🔍 API端点

### 聊天接口
```
POST /api/chat/message
Content-Type: application/json

{
  "message": "用户消息",
  "language": "zh",
  "context": "上下文信息（可选）"
}
```

### 关键词提取
```
POST /api/chat/extract-keywords
Content-Type: application/json

{
  "message": "用户消息",
  "language": "zh"
}
```

### 系统状态检查
```
GET /api/chat/system/status
```

## 🛠️ 技术实现

### 1. 配置管理
- 使用Spring Boot的`@ConfigurationProperties`进行配置管理
- 支持环境变量和配置文件两种方式
- 自动验证配置完整性

### 2. API调用
- 使用Java 11的HttpClient进行API调用
- 支持Azure OpenAI的认证方式（api-key头部）
- 完善的错误处理和重试机制

### 3. 响应解析
- 使用Jackson进行JSON解析
- 支持Azure OpenAI的响应格式
- 优雅的错误处理

## 🔧 故障排除

### 常见问题

1. **401 未授权错误**
   - 检查API密钥是否正确
   - 确认端点URL格式正确

2. **404 未找到错误**
   - 检查部署名称是否正确（gpt-4-1-nano）
   - 确认API版本是否支持（2024-02-15-preview）

3. **403 禁止访问错误**
   - 检查API密钥权限
   - 确认资源访问权限

4. **连接超时**
   - 检查网络连接
   - 确认防火墙设置

### 调试步骤

1. 运行测试脚本：
   ```bash
   ./test-azure-api.sh
   ```

2. 检查应用日志：
   ```bash
   tail -f logs/application.log
   ```

3. 验证配置：
   ```bash
   curl http://localhost:8080/api/chat/system/status
   ```

## 📝 注意事项

- Azure OpenAI使用 `api-key` 头部而不是 `Authorization: Bearer`
- URL格式为：`{endpoint}/openai/deployments/{deployment}/chat/completions?api-version={version}`
- 模型名称在URL中指定，不在请求体中
- 支持的主要模型：gpt-4-1-nano

## 🎉 成功标志

当您看到以下信息时，表示集成成功：

```
✅ 配置验证通过
🔑 使用API密钥: c4bf6f67...
🌐 端点: https://your-resource.openai.azure.com
🤖 部署名称: gpt-4-1-nano
📅 API版本: 2024-02-15-preview
✅ 编译成功
🚀 启动Spring Boot应用...
```

访问 http://localhost:8080/test-integration.html 进行功能测试。
