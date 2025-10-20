# PSA AI Chatbot Setup Guide

## 项目概述
这是一个基于Spring Boot的PSA（Port of Singapore Authority）AI聊天机器人，支持多语言查询和数据分析。

## 功能特性
- 多语言支持（中文、英文、德文等）
- PSA数据解析和存储
- 智能关键词提取
- 基于上传数据的AI回答生成
- 文件上传支持（PDF、文本文件）
- Azure OpenAI集成
- Power BI数据集成

## 环境要求
- Java 17+
- Maven 3.6+
- Spring Boot 3.2.0

## 配置说明

### 1. 复制配置文件
```bash
cp src/main/resources/application-template.yml src/main/resources/application.yml
```

### 2. 设置环境变量
创建 `.env` 文件并设置以下环境变量：

```bash
# Azure OpenAI配置
OPENAI_PRIMARY_KEY=your-primary-key-here
OPENAI_SECONDARY_KEY=your-secondary-key-here
AZURE_OPENAI_ENDPOINT=https://your-resource.openai.azure.com
AZURE_OPENAI_API_VERSION=2025-01-01-preview
AZURE_OPENAI_DEPLOYMENT=gpt-4.1-nano

# Power BI配置
POWERBI_CLIENT_ID=your-client-id-here
POWERBI_WORKSPACE_ID=your-workspace-id-here
POWERBI_REPORT_ID=your-report-id-here
POWERBI_CLIENT_SECRET=your-client-secret-here
POWERBI_TENANT_ID=your-tenant-id-here
POWERBI_AUTHORITY_URL=https://login.microsoftonline.com/your-tenant-id
POWERBI_DASHBOARD_URL=https://your-dashboard-url.com

# Google Cloud翻译API
GOOGLE_TRANSLATE_API_KEY=your-google-api-key-here
```

### 3. 运行应用
```bash
mvn spring-boot:run
```

## API端点

### 聊天接口
- `POST /api/chat/message` - 发送消息
- `GET /api/chat/languages` - 获取支持的语言

### 文件上传
- `POST /api/upload/pdf` - 上传PDF文件
- `POST /api/upload/text` - 上传文本文件
- `GET /api/upload/stats` - 获取数据统计

### 系统状态
- `GET /api/chat/system/status` - 系统状态检查

## 使用说明

1. 启动应用后访问 `http://localhost:8080`
2. 上传PSA数据文件（PDF或文本格式）
3. 在聊天界面中询问关于港口数据的问题
4. 支持中英文查询

## 数据格式
系统支持解析以下格式的PSA数据：
- 表格格式的港口运营数据
- 包含港口代码、船舶信息、时间戳等字段
- 支持多种数据类型：泊位时间、碳减排、港口效率、货物追踪等

## 注意事项
- 请确保API密钥的安全性
- 上传的数据文件会被解析并存储在内存中
- 重启应用后数据会丢失（可扩展为数据库存储）
