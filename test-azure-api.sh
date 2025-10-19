#!/bin/bash

echo "🔍 测试Azure API Management连接..."

# 加载配置
source config.env

echo "📋 当前配置："
echo "  端点: $AZURE_OPENAI_ENDPOINT"
echo "  API版本: $AZURE_OPENAI_API_VERSION"
echo "  部署名称: $AZURE_OPENAI_DEPLOYMENT"
echo "  API密钥: ${OPENAI_PRIMARY_KEY:0:10}..."

echo ""
echo "🧪 测试不同的API路径..."

# 测试路径列表
declare -a api_paths=(
    "/openai/deployments/gpt-4-1-nano/chat/completions?api-version=2024-02-15-preview"
    "/openai/chat/completions"
    "/chat/completions"
    "/v1/chat/completions"
    "/api/openai/chat/completions"
    "/openai/v1/chat/completions"
)

for path in "${api_paths[@]}"; do
    echo ""
    echo "🔗 测试路径: $path"
    
    response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
        -X POST "$AZURE_OPENAI_ENDPOINT$path" \
        -H "api-key: $OPENAI_PRIMARY_KEY" \
        -H "Content-Type: application/json" \
        -d '{
            "messages": [{"role": "user", "content": "Hello"}],
            "max_tokens": 10
        }' 2>/dev/null)
    
    http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    body=$(echo "$response" | sed '/HTTP_STATUS:/d')
    
    echo "   状态码: $http_status"
    
    if [ "$http_status" = "200" ]; then
        echo "   ✅ 成功！"
        echo "   响应: $(echo "$body" | head -c 200)..."
        break
    elif [ "$http_status" = "401" ]; then
        echo "   ❌ 认证失败"
    elif [ "$http_status" = "404" ]; then
        echo "   ❌ 路径不存在"
    elif [ "$http_status" = "403" ]; then
        echo "   ❌ 权限不足"
    else
        echo "   ❌ 其他错误: $(echo "$body" | head -c 100)..."
    fi
done

echo ""
echo "🔍 检查API Management文档..."
curl -s "$AZURE_OPENAI_ENDPOINT" | grep -i "api" | head -5
