#!/bin/bash

# Azure OpenAI API 测试脚本
# 用于测试API连接和配置

echo "🧪 Azure OpenAI API 连接测试"
echo "=============================="

# 加载配置
if [ -f "config.env" ]; then
    source config.env
    echo "✅ 已加载config.env配置"
else
    echo "❌ 找不到config.env文件"
    exit 1
fi

# 检查必要的环境变量
if [ -z "$OPENAI_PRIMARY_KEY" ] || [ "$OPENAI_PRIMARY_KEY" = "your-primary-key-here" ]; then
    echo "❌ 错误：OPENAI_PRIMARY_KEY未设置"
    exit 1
fi

if [ -z "$AZURE_OPENAI_ENDPOINT" ] || [ "$AZURE_OPENAI_ENDPOINT" = "https://your-actual-resource.openai.azure.com" ]; then
    echo "❌ 错误：AZURE_OPENAI_ENDPOINT未设置"
    exit 1
fi

echo "🔑 API密钥: ${OPENAI_PRIMARY_KEY:0:8}..."
echo "🌐 端点: $AZURE_OPENAI_ENDPOINT"
echo "🤖 部署: $AZURE_OPENAI_DEPLOYMENT"
echo "📅 版本: $AZURE_OPENAI_API_VERSION"
echo ""

# 构建API URL
API_URL="$AZURE_OPENAI_ENDPOINT/openai/deployments/$AZURE_OPENAI_DEPLOYMENT/chat/completions?api-version=$AZURE_OPENAI_API_VERSION"

echo "🔗 测试API连接..."
echo "URL: $API_URL"
echo ""

# 发送测试请求
curl -X POST "$API_URL" \
  -H "api-key: $OPENAI_PRIMARY_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "Hello, this is a test message. Please respond with a simple greeting."}],
    "max_tokens": 50,
    "temperature": 0.7
  }' \
  --connect-timeout 30 \
  --max-time 60

echo ""
echo ""

# 检查curl退出状态
if [ $? -eq 0 ]; then
    echo "✅ API连接测试完成"
else
    echo "❌ API连接测试失败"
    echo ""
    echo "🔍 故障排除建议："
    echo "1. 检查AZURE_OPENAI_ENDPOINT是否正确"
    echo "2. 检查API密钥是否有效"
    echo "3. 检查部署名称是否正确"
    echo "4. 检查网络连接"
    echo "5. 检查Azure OpenAI服务状态"
fi