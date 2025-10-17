# 🌐 Network Insights - 全球协调AI助手

一个基于Java Spring Boot和现代Web技术的智能全球协调平台，支持多语言AI对话和实时翻译。

## ✨ 主要功能

### 🤖 智能AI助手
- **智能问答**: 查询项目状态、货物位置、系统状态等实时信息
- **团队沟通辅助**: 自动识别并连接相关团队成员
- **智能报告**: 自动生成每日/每周全球协调报告
- **语言辅助**: 支持20+种语言的实时翻译

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
- **Google Cloud Translate**: AI翻译服务
- **H2 Database**: 内存数据库
- **Maven**: 依赖管理

### 前端 (HTML/CSS/JavaScript)
- **响应式设计**: 支持移动端和桌面端
- **现代UI**: 渐变背景、动画效果
- **实时通信**: 与Java后端API集成
- **多语言界面**: 动态语言切换

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
- `GET /api/chat/languages` - 获取支持的语言列表
- `POST /api/chat/detect-language` - 检测文本语言
- `POST /api/chat/translate` - 翻译文本
- `POST /api/chat/quick-question` - 快速提问

### 请求示例

**发送消息:**
```bash
curl -X POST http://localhost:8080/api/chat/message?language=zh \
  -H "Content-Type: application/json" \
  -d '{"content":"货物#342现在在哪里？","sender":"user","language":"zh"}'
```

**翻译文本:**
```bash
curl -X POST http://localhost:8080/api/chat/translate \
  -H "Content-Type: application/json" \
  -d '{"text":"Hello World","sourceLanguage":"en","targetLanguage":"zh"}'
```

## 🎨 界面特性

### 现代化设计
- 渐变背景和毛玻璃效果
- 流畅的动画和过渡
- 响应式布局
- 深色/浅色主题支持

### 交互功能
- 实时打字指示器
- 语言切换通知
- 平滑滚动导航
- 悬停动画效果

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
Emerging/
├── src/main/java/com/networkinsights/ai/
│   ├── NetworkInsightsApplication.java    # 主应用类
│   ├── controller/
│   │   └── ChatController.java           # 聊天控制器
│   ├── service/
│   │   ├── AIService.java               # AI服务
│   │   └── TranslationService.java      # 翻译服务
│   ├── model/
│   │   ├── ChatMessage.java             # 聊天消息模型
│   │   ├── LanguageRequest.java         # 语言请求模型
│   │   └── SupportedLanguage.java       # 支持语言模型
│   └── config/
│       └── CorsConfig.java              # CORS配置
├── src/main/resources/
│   └── application.yml                  # 应用配置
├── index.html                           # 前端页面
├── style.css                           # 样式文件
├── script.js                           # JavaScript逻辑
├── pom.xml                             # Maven配置
├── start.sh                            # 启动脚本
└── README.md                           # 项目说明
```

## 🌟 特色亮点

1. **完全中文化**: 界面和文档完全中文化
2. **AI驱动**: 集成Google翻译API进行智能翻译
3. **实时交互**: 前后端实时通信
4. **多语言支持**: 支持20+种语言
5. **现代化UI**: 使用最新的Web技术
6. **易于扩展**: 模块化设计，易于添加新功能

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目链接: [https://github.com/your-username/network-insights-ai](https://github.com/your-username/network-insights-ai)
- 问题反馈: [Issues](https://github.com/your-username/network-insights-ai/issues)

---

**Network Insights** - 让全球协作更智能 🚀
