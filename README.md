# 🌐 Network Insights

一个基于Java Spring Boot和现代Web技术的智能全球协调平台，支持多语言AI对话和实时翻译。

## 📋 项目概览 (Summary)

Network Insights是一个企业级的全球航运协调AI平台，专为海事物流行业设计。平台集成了多种AI功能，包括团队协调、数据分析、预测分析、历史案例检索、自动报告生成和实时数据可视化等核心功能。

### 🎯 核心价值
- **智能化协调**: AI驱动的团队协调和沟通
- **数据驱动决策**: 实时数据分析和预测
- **多语言支持**: 全球团队无缝沟通
- **自动化报告**: 智能报告生成和分析
- **可视化洞察**: 实时数据仪表板和图表

### 🚀 最新更新 (Recent Updates)
- ✅ **团队协调功能**: 智能识别和连接相关团队成员
- ✅ **分析报告系统**: 自动生成周报和KPI分析
- ✅ **语言翻译增强**: 支持30+种语言的实时翻译
- ✅ **预测分析AI**: 港口延误和碳排放预测
- ✅ **历史案例检索**: RAG系统提供历史解决方案
- ✅ **自动报告生成**: 智能生成日报和周报
- ✅ **实时数据可视化**: 动态KPI仪表板和全球地图
- ✅ **海洋主题UI**: 专业的海事行业界面设计
- ✅ **多维度钻取**: 从全球到具体船只的层级分析
- ✅ **全屏聊天**: 增强的AI助手交互体验

### 🌐 如何访问Web应用

#### 本地部署访问方式

**方式一：使用启动脚本（推荐）**
```bash
# 1. 进入项目目录
cd Emerging-new-try

# 2. 运行启动脚本
./start.sh

# 3. 访问应用
# 主页面: http://localhost:8000
```

**方式二：手动启动**
```bash
# 1. 启动后端服务
mvn spring-boot:run

# 2. 新开终端，启动前端服务
python3 -m http.server 8000

# 3. 访问应用
# 主页面: http://localhost:8000
# 后端API: http://localhost:8080/api
```

**方式三：直接打开HTML文件（简单预览）**
```bash
# 1. 直接双击 index.html 文件
# 或者在浏览器中打开文件路径
open index.html  # macOS
start index.html # Windows
xdg-open index.html # Linux

# 注意：这种方式只能预览界面，AI功能需要后端服务支持
```

#### 访问地址说明
- **🏠 主页面**: `http://localhost:8000` - 完整的AI助手和功能展示

#### 系统要求
- **Java 17+**: 运行Spring Boot后端
- **Python 3.x**: 运行前端HTTP服务器
- **现代浏览器**: Chrome、Firefox、Safari、Edge等
- **网络连接**: 用于加载CDN资源（字体、图标等）

#### 故障排除

**问题1：端口冲突**
```bash
# 如果8000端口被占用，可以修改为其他端口
python3 -m http.server 8001  # 使用8001端口
# 然后访问 http://localhost:8001
```

**问题2：Java版本问题**
```bash
# 检查Java版本
java -version

# 如果版本低于17，需要安装Java 17+
# macOS: brew install openjdk@17
# Ubuntu: sudo apt install openjdk-17-jdk
```

**问题3：防火墙阻止**
```bash
# macOS: 系统偏好设置 > 安全性与隐私 > 防火墙
# Windows: Windows Defender防火墙设置
# Linux: sudo ufw allow 8000
```

**问题4：主页链接打不开**
```bash
# 检查服务是否运行
lsof -i :8000  # 检查8000端口
lsof -i :8080  # 检查8080端口

# 如果没有服务运行，重新启动
./start.sh

# 或者手动启动
mvn spring-boot:run &  # 后台启动后端
python3 -m http.server 8000 &  # 后台启动前端
```

**问题5：AI功能不工作**
- 确保后端服务在8080端口运行
- 检查浏览器控制台是否有CORS错误
- 尝试刷新页面或清除浏览器缓存

**问题6：页面样式异常**
- 检查网络连接，确保CDN资源能正常加载
- 尝试使用不同的浏览器
- 清除浏览器缓存和Cookie

## ✨ 主要功能

### 🤖 智能AI助手
- **智能问答**: 查询项目状态、货物位置、系统状态等实时信息
- **团队协调**: 智能识别并连接相关团队成员，支持按地区、专业领域筛选
- **分析报告**: 自动生成每日/每周全球协调报告和KPI分析
- **语言翻译**: 支持30+种语言的实时翻译，包括中文、英文、日文等
- **预测分析**: AI预测港口延误风险和碳排放超标
- **历史案例**: RAG系统检索相似历史案例并提供解决方案
- **自动报告**: 智能生成泊位利用率、港口效率等专业报告

### 📊 数据分析与可视化
- **实时KPI仪表板**: 动态显示港口效率、到达准确率、碳减排等指标
- **全球地图热力图**: 可视化各港口效率、延误情况、碳排放水平
- **时间轴分析**: 历史趋势对比和AI预测曲线
- **多维度钻取**: 从全球→区域→港口→具体船只的层级分析
- **交互式图表**: 支持点击、缩放、悬停等交互操作

### 🚢 海事专业功能
- **船舶跟踪**: 实时船舶位置和状态监控
- **港口管理**: 泊位利用率、起重机状态、天气条件
- **碳排放分析**: 燃料效率、可再生能源使用、排放目标
- **成本优化**: 运营成本分析和优化建议
- **应急响应**: 紧急情况处理和联系信息
- **合规检查**: 法规合规性检查和审计状态

### 🌍 多语言支持
- 🇨🇳 中文 (Chinese)
- 🇺🇸 英语 (English)  
- 🇩🇪 德语 (Deutsch)
- 🇫🇷 法语 (Français)
- 🇯🇵 日语 (日本語)
- 🇰🇷 韩语 (한국어)
- 🇪🇸 西班牙语 (Español)
- 🇮🇹 意大利语 (Italiano)
- 🇵🇹 葡萄牙语 (Português)
- 🇷🇺 俄语 (Русский)
- 以及更多...

## 🏗️ 技术架构

### 后端 (Java)
- **Spring Boot 3.2.0**: 主框架
- **Spring Web**: RESTful API
- **Spring Data JPA**: 数据持久化
- **Google Cloud Translate**: AI翻译服务
- **H2 Database**: 内存数据库
- **Maven**: 依赖管理
- **Team Coordination Service**: 团队协调服务
- **Analytics Service**: 数据分析服务
- **RAG Memory Service**: 历史案例检索服务

### 前端 (HTML/CSS/JavaScript)
- **响应式设计**: 支持移动端和桌面端
- **现代UI**: 海洋主题、渐变背景、动画效果
- **实时通信**: 与Java后端API集成
- **多语言界面**: 动态语言切换
- **Chart.js**: 交互式图表库
- **Leaflet**: 全球地图可视化
- **Three.js**: 3D数据可视化
- **全屏聊天**: 增强的用户交互体验

### 数据可视化
- **动态KPI仪表板**: 实时数据更新和趋势指示
- **全球地图热力图**: 港口性能可视化
- **时间轴分析**: 历史趋势和预测曲线
- **多维度钻取**: 层级数据导航
- **交互式图表**: 支持多种图表类型

## 🚀 快速开始

### 环境要求
- Java 17+
- Maven 3.6+
- Python 3.x (用于前端服务器)

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd Emerging
```

2. **设置Google翻译API密钥** (可选)
```bash
export GOOGLE_TRANSLATE_API_KEY=your-api-key-here
```

3. **启动应用**
```bash
./start.sh
```

或者手动启动：

```bash
# 启动后端
mvn spring-boot:run

# 启动前端 (新终端)
python3 -m http.server 8000
```

4. **访问应用**
- 前端: http://localhost:8000
- 后端API: http://localhost:8080/api
- 数据库控制台: http://localhost:8080/h2-console

## 📡 API接口

### 聊天相关
- `POST /api/chat/message` - 发送消息获取AI回复
- `POST /api/chat/message-with-data` - 发送消息并获取结构化数据
- `GET /api/chat/languages` - 获取支持的语言列表
- `POST /api/chat/detect-language` - 检测文本语言
- `POST /api/chat/translate` - 翻译文本
- `POST /api/chat/quick-question` - 快速提问

### 团队协调
- `POST /api/team-coordination/query` - 团队协调查询
- `GET /api/team-coordination/members` - 获取团队成员列表
- `GET /api/team-coordination/members/{id}` - 获取特定成员信息
- `POST /api/team-coordination/search` - 搜索团队成员

### 数据分析
- `POST /api/analytics/report` - 生成分析报告
- `GET /api/analytics/kpi` - 获取KPI数据
- `POST /api/analytics/predict` - AI预测分析

### 历史案例检索
- `POST /api/rag/search` - 搜索历史案例
- `GET /api/rag/cases` - 获取案例列表

### 自动报告
- `POST /api/reports/generate` - 生成自动报告
- `GET /api/reports/templates` - 获取报告模板

### 请求示例

**发送消息:**
```bash
curl -X POST http://localhost:8080/api/chat/message?language=en \
  -H "Content-Type: application/json" \
  -d '{"content":"Where is shipment #342 now?","sender":"user","language":"en"}'
```

**团队协调查询:**
```bash
curl -X POST http://localhost:8080/api/team-coordination/query \
  -H "Content-Type: application/json" \
  -d '{"message":"Who owns Europe network issues?"}'
```

**生成分析报告:**
```bash
curl -X POST http://localhost:8080/api/analytics/report \
  -H "Content-Type: application/json" \
  -d '{"query":"Weekly delay analysis for Asian routes","type":"weekly"}'
```

**翻译文本:**
```bash
curl -X POST http://localhost:8080/api/chat/translate \
  -H "Content-Type: application/json" \
  -d '{"text":"Hello team, meeting at 3PM","sourceLanguage":"en","targetLanguage":"zh"}'
```

**AI预测分析:**
```bash
curl -X POST http://localhost:8080/api/analytics/predict \
  -H "Content-Type: application/json" \
  -d '{"query":"Forecast next week delay risk for Asia-Europe route","type":"delay"}'
```

## 🎨 界面特性

### 海洋主题设计
- **海洋色彩方案**: 深蓝到浅蓝的渐变背景
- **船舶元素**: 动态集装箱船、货船、锚、指南针等海洋图标
- **波浪动画**: 三层波浪效果营造海洋氛围
- **集装箱色彩**: 橙色、红色、蓝色、绿色、黄色等集装箱主题色

### 现代化设计
- **渐变背景和毛玻璃效果**: 专业的海洋主题视觉效果
- **流畅的动画和过渡**: 船舶航行、浮动、旋转等动画
- **响应式布局**: 完美适配移动端和桌面端
- **全屏聊天模式**: 增强的AI助手交互体验

### 交互功能
- **实时打字指示器**: 显示AI正在思考
- **语言切换通知**: 多语言界面无缝切换
- **平滑滚动导航**: 流畅的页面导航体验
- **悬停动画效果**: 丰富的交互反馈
- **快捷按钮**: 一键访问常用功能
- **全屏模式**: 沉浸式的聊天体验

### 数据可视化
- **动态KPI仪表板**: 实时更新的关键指标
- **全球地图热力图**: 交互式港口性能地图
- **时间轴分析**: 历史趋势和预测曲线
- **多维度钻取**: 层级数据导航和分析
- **交互式图表**: 支持点击、缩放、悬停操作

## 🔧 配置说明

### 应用配置 (application.yml)
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password

google:
  cloud:
    translate:
      api-key: ${GOOGLE_TRANSLATE_API_KEY:your-api-key-here}
```

### 环境变量
- `GOOGLE_TRANSLATE_API_KEY`: Google翻译API密钥
- `SERVER_PORT`: 服务器端口 (默认8080)

## 📁 项目结构

```
Emerging-new-try/
├── README.md                                 # 项目说明文档
├── index.html                                # 主页面
├── dashboard.html                            # 实时数据可视化仪表板
├── script.js                                 # 前端JavaScript
├── pom.xml                                   # Maven配置
├── docs/                                     # 文档文件夹
│   ├── AZURE_OPENAI_INTEGRATION.md          # Azure OpenAI集成指南
│   ├── AZURE_OPENAI_SETUP.md                # Azure OpenAI设置
│   ├── POWERBI_EMBEDDED_GUIDE.md            # Power BI嵌入指南
│   ├── POWERBI_SETUP.md                     # Power BI设置
│   ├── PROJECT_SUMMARY.md                   # 项目总结
│   ├── PSA_KEYWORD_EXTRACTION_README.md     # PSA关键词提取说明
│   ├── SETUP.md                             # 设置指南
│   └── SMART_TRACKING_SUMMARY.md            # 智能追踪总结
├── demos/                                    # 演示页面文件夹
│   ├── advanced-visualization-demo.html      # 高级可视化演示
│   ├── debug-translation.html               # 翻译调试页面
│   ├── enhanced-index-demo.html             # 增强首页演示
│   ├── information-architecture-demo.html   # 信息架构改进演示
│   ├── maritime-theme-demo.html             # 海洋主题UI演示
│   └── quick-test.html                      # 快速测试页面
├── scripts/                                  # 脚本文件夹
│   ├── start.sh                             # 启动脚本
│   ├── start-full.sh                        # 完整启动脚本
│   ├── start-simple.sh                      # 简单启动脚本
│   ├── start-with-config.sh                 # 配置启动脚本
│   ├── test-*.sh                            # 各种测试脚本
│   └── config.env                           # 环境配置文件
├── data/                                    # 数据文件夹
│   ├── psa-complete-data.txt                # PSA完整数据
│   ├── psa-extended-data.txt                # PSA扩展数据
│   └── psa-raw-data.txt                     # PSA原始数据
├── tests/                                    # 测试文件夹
│   ├── test-all-buttons-undefined-fix.html   # 按钮undefined修复测试
│   ├── test-all-quick-buttons.html           # 所有快捷按钮测试
│   ├── test-auto-report-generator.html       # 自动报告生成器测试
│   ├── test-button-undefined-fix.html        # 按钮undefined修复测试
│   ├── test-enhanced-ui.html                # 增强UI测试
│   ├── test-fullscreen-chat.html             # 全屏聊天测试
│   ├── test-fullscreen-display-fix.html     # 全屏显示修复测试
│   ├── test-index-translation.html           # 首页翻译测试
│   ├── test-language-default-english.html    # 默认英语测试
│   ├── test-new-ai-features.html             # 新AI功能测试
│   ├── test-predictive-analytics.html        # 预测分析测试
│   ├── test-rag-historical-memory.html       # RAG历史记忆测试
│   ├── test-root-translation.html            # 根目录翻译测试
│   ├── test-translation-undefined-fix.html   # 翻译undefined修复测试
│   └── test-translation.html                 # 翻译功能测试
├── src/main/java/com/networkinsights/ai/     # Java后端代码
│   ├── NetworkInsightsApplication.java       # 主应用类
│   ├── controller/                          # REST API控制器
│   │   ├── ChatController.java               # 聊天控制器
│   │   ├── TeamCoordinationController.java   # 团队协调控制器
│   │   ├── SmartTrackingController.java     # 智能跟踪控制器
│   │   └── AnalyticsController.java          # 分析控制器
│   ├── service/                             # 业务逻辑服务
│   │   ├── AIService.java                   # AI服务
│   │   ├── TranslationService.java          # 翻译服务
│   │   ├── TeamCoordinationService.java     # 团队协调服务
│   │   ├── DocumentProcessingService.java   # 文档处理服务
│   │   └── AnalyticsService.java            # 分析服务
│   ├── model/                               # 数据模型
│   │   ├── ChatMessage.java                 # 聊天消息模型
│   │   ├── TeamMember.java                  # 团队成员模型
│   │   ├── LanguageRequest.java             # 语言请求模型
│   │   └── SupportedLanguage.java           # 支持语言模型
│   └── config/                              # 配置类
│       └── CorsConfig.java                  # CORS配置
└── src/main/resources/                     # 资源文件
    ├── static/                              # 静态资源
    │   ├── index.html                       # 主页面（Spring Boot服务）
    │   └── dashboard.html                   # 数据仪表板（Spring Boot服务）
    └── application.yml                      # 应用配置
```

## 🌟 特色亮点

1. **海洋主题设计**: 专业的海事行业界面，海洋色彩和船舶元素
2. **AI驱动**: 集成多种AI服务进行智能分析和预测
3. **实时交互**: 前后端实时通信，动态数据更新
4. **多语言支持**: 支持30+种语言的实时翻译
5. **现代化UI**: 使用最新的Web技术和海洋主题设计
6. **数据可视化**: 动态KPI仪表板、全球地图、时间轴分析
7. **团队协调**: 智能识别和连接相关团队成员
8. **预测分析**: AI预测港口延误和碳排放风险
9. **历史案例**: RAG系统提供历史解决方案
10. **自动报告**: 智能生成专业分析报告
11. **易于扩展**: 模块化设计，易于添加新功能
12. **全屏体验**: 沉浸式的AI助手交互


**Network Insights** - 让全球协作更智能 🚀
