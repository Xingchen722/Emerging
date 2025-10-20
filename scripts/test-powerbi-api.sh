#!/bin/bash

# Power BI Embedded 功能测试脚本
# 用于快速验证所有API端点功能

echo "🚢 Power BI Embedded 功能测试"
echo "=============================="

BASE_URL="http://localhost:8080/api/powerbi"

# 检查服务是否运行
echo "📊 检查服务状态..."
curl -s "$BASE_URL/status" | jq .
echo ""

# 测试工作区管理
echo "🏢 测试工作区管理..."
echo "获取工作区信息:"
curl -s "$BASE_URL/workspace" | jq .
echo ""

echo "获取报告列表:"
curl -s "$BASE_URL/reports" | jq .
echo ""

echo "获取数据集列表:"
curl -s "$BASE_URL/datasets" | jq .
echo ""

# 测试DAX查询
echo "🔍 测试DAX查询..."
curl -s -X POST "$BASE_URL/query" \
  -H "Content-Type: application/json" \
  -d '{"query": "EVALUATE \u0027PortData\u0027"}' | jq .
echo ""

# 测试实时数据获取
echo "⚓ 测试实时数据获取..."
echo "获取新加坡港效率数据:"
curl -s "$BASE_URL/data/port/SGSIN?dataType=port_efficiency" | jq .
echo ""

echo "获取船舶数据:"
curl -s "$BASE_URL/data/vessels" | jq .
echo ""

echo "获取碳减排数据:"
curl -s "$BASE_URL/data/carbon" | jq .
echo ""

# 测试AI分析
echo "🤖 测试AI分析..."
echo "新加坡港效率分析:"
curl -s "$BASE_URL/analysis/port/SGSIN?analysisType=port_efficiency" | jq .
echo ""

echo "船舶调度分析:"
curl -s "$BASE_URL/analysis/vessels" | jq .
echo ""

echo "碳减排分析:"
curl -s "$BASE_URL/analysis/carbon" | jq .
echo ""

echo "综合运营分析:"
curl -s "$BASE_URL/analysis/comprehensive/SGSIN" | jq .
echo ""

# 测试缓存管理
echo "💾 测试缓存管理..."
echo "获取缓存统计:"
curl -s "$BASE_URL/cache/stats" | jq .
echo ""

echo "清理过期缓存:"
curl -s -X POST "$BASE_URL/cache/clean" | jq .
echo ""

echo "✅ 所有测试完成！"
echo ""
echo "🌐 访问测试仪表板: http://localhost:8080/powerbi-dashboard.html"
