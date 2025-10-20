#!/bin/bash

# Network Insights AI助手简化启动脚本
# 不依赖Maven，直接运行Java文件

echo "🚀 启动Network Insights AI助手 (简化版)..."

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java。请先安装Java 17或更高版本。"
    exit 1
fi

# 创建classes目录
mkdir -p classes

# 编译Java文件
echo "📦 编译Java文件..."
javac -cp ".:lib/*" -d classes src/main/java/com/networkinsights/ai/*.java src/main/java/com/networkinsights/ai/*/*.java 2>/dev/null

if [ $? -ne 0 ]; then
    echo "⚠️  注意: 由于缺少Spring Boot依赖，Java编译可能失败。"
    echo "   建议使用Maven或Gradle来管理依赖。"
    echo "   或者直接使用前端版本（不依赖后端）。"
    echo ""
fi

# 启动前端服务器
echo "🎨 启动前端服务器..."
python3 -m http.server 8000 &

echo ""
echo "✅ 前端启动完成！"
echo ""
echo "🌐 前端地址: http://localhost:8000"
echo ""
echo "📝 注意: 由于缺少Spring Boot依赖，后端功能暂时不可用。"
echo "   前端将使用模拟数据进行演示。"
echo ""
echo "💡 要启用完整功能，请："
echo "   1. 安装Maven: brew install maven"
echo "   2. 运行: mvn spring-boot:run"
echo ""
echo "按 Ctrl+C 停止服务"
echo ""

# 等待用户中断
wait
