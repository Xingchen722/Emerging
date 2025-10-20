#!/bin/bash

echo "🚢 Smart Tracking功能测试脚本"
echo "=============================="

BASE_URL="http://localhost:8080/api/smart-tracking"

# 测试服务状态
echo ""
echo "1. 测试服务状态..."
curl -s "${BASE_URL}/status" | jq .
echo ""

# 测试自然语言查询
echo "2. 测试自然语言查询..."
echo "查询: Where is shipment #342 now?"
curl -s -X POST "${BASE_URL}/query" \
  -H "Content-Type: application/json" \
  -d '{"query": "Where is shipment #342 now?", "language": "en"}' | jq -r '.content'
echo ""

echo "查询: What is the ETA for shipment #156?"
curl -s -X POST "${BASE_URL}/query" \
  -H "Content-Type: application/json" \
  -d '{"query": "What is the ETA for shipment #156?", "language": "en"}' | jq -r '.content'
echo ""

echo "查询: What is the route for vessel MSC LORETO?"
curl -s -X POST "${BASE_URL}/query" \
  -H "Content-Type: application/json" \
  -d '{"query": "What is the route for vessel MSC LORETO?", "language": "en"}' | jq -r '.content'
echo ""

echo "查询: 货物#789的状态是什么？"
curl -s -X POST "${BASE_URL}/query" \
  -H "Content-Type: application/json" \
  -d '{"query": "货物#789的状态是什么？", "language": "zh"}' | jq -r '.content'
echo ""

# 测试直接API访问
echo "3. 测试直接API访问..."
echo "根据货物编号获取信息:"
curl -s "${BASE_URL}/cargo/342" | jq .
echo ""

echo "获取所有货物信息:"
curl -s "${BASE_URL}/cargo/all" | jq '.[0]'  # 只显示第一个
echo ""

# 测试船舶搜索
echo "4. 测试船舶搜索..."
echo "搜索船舶 MSC LORETO:"
curl -s "${BASE_URL}/cargo/vessel/MSC%20LORETO" | jq '.[0]'
echo ""

# 测试港口搜索
echo "5. 测试港口搜索..."
echo "搜索港口 Singapore:"
curl -s "${BASE_URL}/cargo/port/Singapore" | jq '.[0]'
echo ""

# 测试查询示例
echo "6. 测试查询示例..."
curl -s "${BASE_URL}/examples" | jq .
echo ""

# 测试聊天机器人集成
echo "7. 测试聊天机器人集成..."
echo "通过聊天机器人API查询:"
curl -s -X POST "http://localhost:8080/api/chat/message-with-data" \
  -H "Content-Type: application/json" \
  -d '{"message": "Where is shipment #342 now?", "language": "en"}' | jq -r '.content'
echo ""

echo "✅ Smart Tracking功能测试完成！"
echo ""
echo "🌐 网页界面访问地址: http://localhost:8080/smart-tracking.html"
echo "📚 API文档: 查看SmartTrackingController.java"
echo "🔧 服务状态: http://localhost:8080/api/smart-tracking/status"
