#!/bin/bash

# Network Insights AI助手完整启动脚本
# 包含Google Cloud Translate集成和全站多语言支持

echo "🚀 启动Network Insights AI助手 (完整版)..."

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java。请先安装Java 17或更高版本。"
    exit 1
fi

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven。请先安装Maven。"
    echo "   运行: brew install maven"
    exit 1
fi

# 检查Google Cloud API密钥
if [ -z "$GOOGLE_APPLICATION_CREDENTIALS" ]; then
    echo "⚠️  警告: 未设置GOOGLE_APPLICATION_CREDENTIALS环境变量。"
    echo "   翻译功能将使用简化版本。"
    echo "   要启用完整翻译功能，请设置:"
    echo "   export GOOGLE_APPLICATION_CREDENTIALS=/path/to/your/gcp-key.json"
    echo ""
fi

# 停止可能运行的进程
echo "🛑 停止可能运行的进程..."
pkill -f "spring-boot:run" 2>/dev/null || true
pkill -f "python3 -m http.server" 2>/dev/null || true

# 启动后端服务器
echo "🌐 启动后端服务器..."
mvn spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!

# 等待后端启动
echo "⏳ 等待后端服务器启动..."
for i in {1..30}; do
    if curl -s http://localhost:8080/api/chat/languages > /dev/null 2>&1; then
        echo "✅ 后端服务器启动成功！"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "❌ 后端服务器启动失败，请检查日志: backend.log"
        kill $BACKEND_PID 2>/dev/null || true
        exit 1
    fi
    sleep 2
done

# 启动前端服务器
echo "🎨 启动前端服务器..."
python3 -m http.server 8081 > frontend.log 2>&1 &
FRONTEND_PID=$!

# 等待前端启动
sleep 2

echo ""
echo "✅ 启动完成！"
echo ""
echo "🌐 前端地址: http://localhost:8081"
echo "🔧 后端API: http://localhost:8080/api"
echo "📊 H2数据库控制台: http://localhost:8080/h2-console"
echo ""
echo "📝 功能特性:"
echo "   ✅ Google Cloud Translate集成"
echo "   ✅ 全站多语言支持 (中文/英文/德文)"
echo "   ✅ 智能AI聊天助手"
echo "   ✅ 实时语言切换"
echo "   ✅ 语言偏好保存"
echo ""
echo "💡 使用说明:"
echo "   1. 在浏览器中访问 http://localhost:8081"
echo "   2. 使用右上角的语言选择器切换语言"
echo "   3. 在聊天界面与AI助手对话"
echo "   4. 语言偏好会自动保存到本地"
echo ""
echo "按 Ctrl+C 停止所有服务"
echo ""

# 清理函数
cleanup() {
    echo ""
    echo "🛑 正在停止服务..."
    kill $BACKEND_PID 2>/dev/null || true
    kill $FRONTEND_PID 2>/dev/null || true
    echo "✅ 服务已停止"
    exit 0
}

# 捕获中断信号
trap cleanup SIGINT SIGTERM

# 等待用户中断
wait
