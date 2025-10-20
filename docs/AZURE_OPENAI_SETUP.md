# Azure OpenAI 配置指南

## 🔧 配置步骤

### 1. 获取Azure OpenAI配置信息

从您的hackathon提供的Azure OpenAI服务中获取以下信息：

1. **API密钥**：`c4bf6f6756ec4ae1b703c755f03ee4c9` 和 `e84a603883ae4df0a1679c4e88bb30ef`
2. **端点URL**：类似 `https://your-resource-name.openai.azure.com`
3. **部署名称**：`gpt-4-1-nano`
4. **API版本**：`2024-02-15-preview`

### 2. 更新config.env文件

编辑 `config.env` 文件，将以下占位符替换为实际值：

```bash
# Azure OpenAI API配置
OPENAI_PRIMARY_KEY=c4bf6f6756ec4ae1b703c755f03ee4c9
OPENAI_SECONDARY_KEY=e84a603883ae4df0a1679c4e88bb30ef
AZURE_OPENAI_ENDPOINT=https://your-actual-resource.openai.azure.com
AZURE_OPENAI_API_VERSION=2024-02-15-preview
AZURE_OPENAI_DEPLOYMENT=gpt-4-1-nano
```

### 3. 重要配置说明

- **AZURE_OPENAI_ENDPOINT**：这是您的Azure OpenAI资源的完整URL
- **AZURE_OPENAI_DEPLOYMENT**：这是您部署的模型名称（gpt-4-1-nano）
- **API密钥**：不需要 `sk-` 前缀，直接使用提供的密钥

### 4. 启动应用

```bash
./start-with-config.sh
```

### 5. 验证配置

访问测试页面：http://localhost:8080/test-integration.html

检查系统状态：http://localhost:8080/api/chat/system/status

## 🔍 故障排除

### 常见问题

1. **401 未授权错误**
   - 检查API密钥是否正确
   - 确认端点URL格式正确

2. **404 未找到错误**
   - 检查部署名称是否正确
   - 确认API版本是否支持

3. **403 禁止访问错误**
   - 检查API密钥权限
   - 确认资源访问权限

### 测试API连接

```bash
curl -X POST "https://your-resource.openai.azure.com/openai/deployments/gpt-4-1-nano/chat/completions?api-version=2024-02-15-preview" \
  -H "api-key: c4bf6f6756ec4ae1b703c755f03ee4c9" \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "Hello"}],
    "max_tokens": 10
  }'
```

## 📝 注意事项

- Azure OpenAI使用 `api-key` 头部而不是 `Authorization: Bearer`
- URL格式为：`{endpoint}/openai/deployments/{deployment}/chat/completions?api-version={version}`
- 模型名称在URL中指定，不在请求体中
