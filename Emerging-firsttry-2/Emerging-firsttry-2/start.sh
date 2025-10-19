#!/bin/bash

# Network Insights AI助手启动脚本

echo "🚀 启动Network Insights AI助手..."

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java。请先安装Java 17或更高版本。"
    exit 1
fi

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven。请先安装Maven。"
    exit 1
fi

# 设置Google Cloud API密钥（可选）
if [ -z "$GOOGLE_TRANSLATE_API_KEY" ]; then
    echo "⚠️  警告: 未设置GOOGLE_TRANSLATE_API_KEY环境变量。"
    echo "   翻译功能将使用简化版本。"
    echo "   要启用完整翻译功能，请设置:"
    echo "   export GOOGLE_TRANSLATE_API_KEY=your-api-key"
    echo ""
fi

# 编译和运行Java应用
echo "📦 编译Java应用..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ 编译失败"
    exit 1
fi

echo "🌐 启动后端服务器..."
mvn spring-boot:run &

# 等待后端启动
echo "⏳ 等待后端服务器启动..."
sleep 10

# 启动前端服务器
echo "🎨 启动前端服务器..."
cd /Users/guoxingchen/Emerging
python3 -m http.server 8000 &

echo ""
echo "✅ 启动完成！"
echo ""
echo "🌐 前端地址: http://localhost:8000"
echo "🔧 后端API: http://localhost:8080/api"
echo "📊 H2数据库控制台: http://localhost:8080/h2-console"
echo ""
echo "按 Ctrl+C 停止所有服务"
echo ""

# 等待用户中断
wait
