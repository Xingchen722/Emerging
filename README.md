# 🌐 Network Insights

An intelligent global coordination platform based on Java Spring Boot and modern web technologies, supporting multilingual AI conversations and real-time translation.

## 📋 Project Overview

Network Insights is an enterprise-grade global shipping coordination AI platform designed specifically for the maritime logistics industry. The platform integrates multiple AI capabilities including team coordination, data analysis, predictive analytics, historical case retrieval, automatic report generation, and real-time data visualization.

### 🌐 How to Access the Web Application

#### Local Deployment Access Methods

**Method 1: Using Startup Script (Recommended)**
```bash
# 1. Navigate to project directory
cd Emerging-new-try

# 2. Run startup script
./start.sh

# 3. Access application
# Main page: http://localhost:8000
```

**Method 2: Manual Startup**
```bash
# 1. Start backend service
mvn spring-boot:run

# 2. Open new terminal, start frontend service
python3 -m http.server 8000

# 3. Access application
# Main page: http://localhost:8000
# Backend API: http://localhost:8080/api
```

**Method 3: Direct HTML File Opening (Simple Preview)**
```bash
# 1. Double-click index.html file directly
# Or open file path in browser
open index.html  # macOS
start index.html # Windows
xdg-open index.html # Linux

# Note: This method only previews the interface, AI features require backend service support
```

#### Access URL Descriptions
- **🏠 Main Page**: `http://localhost:8000` - Complete AI assistant and feature showcase

#### System Requirements
- **Java 17+**: Run Spring Boot backend
- **Python 3.x**: Run frontend HTTP server
- **Modern Browser**: Chrome, Firefox, Safari, Edge, etc.
- **Network Connection**: For loading CDN resources (fonts, icons, etc.)

#### Troubleshooting

**Issue 1: Port Conflict**
```bash
# If port 8000 is occupied, you can modify to another port
python3 -m http.server 8001  # Use port 8001
# Then access http://localhost:8001
```

**Issue 2: Java Version Problem**
```bash
# Check Java version
java -version

# If version is below 17, install Java 17+
# macOS: brew install openjdk@17
# Ubuntu: sudo apt install openjdk-17-jdk
```

**Issue 3: Firewall Blocking**
```bash
# macOS: System Preferences > Security & Privacy > Firewall
# Windows: Windows Defender Firewall settings
# Linux: sudo ufw allow 8000
```

**Issue 4: Main Page Links Not Opening**
```bash
# Check if services are running
lsof -i :8000  # Check port 8000
lsof -i :8080  # Check port 8080

# If no services are running, restart
./start.sh

# Or start manually
mvn spring-boot:run &  # Background start backend
python3 -m http.server 8000 &  # Background start frontend
```

**Issue 5: AI Features Not Working**
- Ensure backend service is running on port 8080
- Check browser console for CORS errors
- Try refreshing page or clearing browser cache

**Issue 6: Page Styling Issues**
- Check network connection, ensure CDN resources load properly
- Try using different browsers
- Clear browser cache and cookies

## ✨ Main Features

### 🤖 Intelligent AI Assistant
- **Smart Q&A**: Query project status, cargo location, system status and other real-time information
- **Team Coordination**: Intelligently identify and connect relevant team members, support filtering by region and expertise
- **Analytics Reports**: Automatically generate daily/weekly global coordination reports and KPI analysis
- **Language Translation**: Support real-time translation for 30+ languages including Chinese, English, Japanese, etc.
- **Predictive Analytics**: AI predicts port delay risks and carbon emission overruns
- **Historical Cases**: RAG system retrieves similar historical cases and provides solutions
- **Auto Reports**: Intelligently generate professional reports on berth utilization, port efficiency, etc.

### 📊 Data Analysis & Visualization
- **Real-time KPI Dashboard**: Dynamically display port efficiency, arrival accuracy, carbon reduction and other metrics
- **Global Map Heatmap**: Visualize port efficiency, delay situations, carbon emission levels
- **Timeline Analysis**: Historical trend comparison and AI prediction curves
- **Multi-dimensional Drill-down**: Hierarchical analysis from global → regional → port → specific vessels
- **Interactive Charts**: Support click, zoom, hover and other interactive operations

### 🚢 Maritime Professional Features
- **Vessel Tracking**: Real-time vessel position and status monitoring
- **Port Management**: Berth utilization, crane status, weather conditions
- **Carbon Emission Analysis**: Fuel efficiency, renewable energy usage, emission targets
- **Cost Optimization**: Operational cost analysis and optimization recommendations
- **Emergency Response**: Emergency situation handling and contact information
- **Compliance Check**: Regulatory compliance check and audit status

### 🌍 Multilingual Support
- 🇨🇳 Chinese (中文)
- 🇺🇸 English (English)  
- 🇩🇪 German (Deutsch)
- 🇫🇷 French (Français)
- 🇯🇵 Japanese (日本語)
- 🇰🇷 Korean (한국어)
- 🇪🇸 Spanish (Español)
- 🇮🇹 Italian (Italiano)
- 🇵🇹 Portuguese (Português)
- 🇷🇺 Russian (Русский)
- And more...

## 🏗️ Technical Architecture

### Backend (Java)
- **Spring Boot 3.2.0**: Main framework
- **Spring Web**: RESTful API
- **Spring Data JPA**: Data persistence
- **Google Cloud Translate**: AI translation service
- **H2 Database**: In-memory database
- **Maven**: Dependency management
- **Team Coordination Service**: Team coordination service
- **Analytics Service**: Data analysis service
- **RAG Memory Service**: Historical case retrieval service

### Frontend (HTML/CSS/JavaScript)
- **Responsive Design**: Support for mobile and desktop
- **Modern UI**: Maritime theme, gradient backgrounds, animation effects
- **Real-time Communication**: Integration with Java backend API
- **Multilingual Interface**: Dynamic language switching
- **Chart.js**: Interactive chart library
- **Leaflet**: Global map visualization
- **Three.js**: 3D data visualization
- **Fullscreen Chat**: Enhanced user interaction experience

### Data Visualization
- **Dynamic KPI Dashboard**: Real-time data updates and trend indicators
- **Global Map Heatmap**: Port performance visualization
- **Timeline Analysis**: Historical trends and prediction curves
- **Multi-dimensional Drill-down**: Hierarchical data navigation
- **Interactive Charts**: Support for multiple chart types

## 🚀 Quick Start

### Environment Requirements
- Java 17+
- Maven 3.6+
- Python 3.x (for frontend server)

### Installation Steps

1. **Clone Project**
```bash
git clone <repository-url>
cd Emerging
```

2. **Set Google Translate API Key** (Optional)
```bash
export GOOGLE_TRANSLATE_API_KEY=your-api-key-here
```

3. **Start Application**
```bash
./start.sh
```

Or start manually:

```bash
# Start backend
mvn spring-boot:run

# Start frontend (new terminal)
python3 -m http.server 8000
```

4. **Access Application**
- Frontend: http://localhost:8000
- Backend API: http://localhost:8080/api
- Database Console: http://localhost:8080/h2-console

## 📡 API Interface

### Chat Related
- `POST /api/chat/message` - Send message and get AI response
- `POST /api/chat/message-with-data` - Send message and get structured data
- `GET /api/chat/languages` - Get supported language list
- `POST /api/chat/detect-language` - Detect text language
- `POST /api/chat/translate` - Translate text
- `POST /api/chat/quick-question` - Quick question

### Team Coordination
- `POST /api/team-coordination/query` - Team coordination query
- `GET /api/team-coordination/members` - Get team member list
- `GET /api/team-coordination/members/{id}` - Get specific member information
- `POST /api/team-coordination/search` - Search team members

### Data Analysis
- `POST /api/analytics/report` - Generate analysis report
- `GET /api/analytics/kpi` - Get KPI data
- `POST /api/analytics/predict` - AI predictive analysis

### Historical Case Retrieval
- `POST /api/rag/search` - Search historical cases
- `GET /api/rag/cases` - Get case list

### Auto Reports
- `POST /api/reports/generate` - Generate auto report
- `GET /api/reports/templates` - Get report templates

### Request Examples

**Send Message:**
```bash
curl -X POST http://localhost:8080/api/chat/message?language=en \
  -H "Content-Type: application/json" \
  -d '{"content":"Where is shipment #342 now?","sender":"user","language":"en"}'
```

**Team Coordination Query:**
```bash
curl -X POST http://localhost:8080/api/team-coordination/query \
  -H "Content-Type: application/json" \
  -d '{"message":"Who owns Europe network issues?"}'
```

**Generate Analysis Report:**
```bash
curl -X POST http://localhost:8080/api/analytics/report \
  -H "Content-Type: application/json" \
  -d '{"query":"Weekly delay analysis for Asian routes","type":"weekly"}'
```

**Translate Text:**
```bash
curl -X POST http://localhost:8080/api/chat/translate \
  -H "Content-Type: application/json" \
  -d '{"text":"Hello team, meeting at 3PM","sourceLanguage":"en","targetLanguage":"zh"}'
```

**AI Predictive Analysis:**
```bash
curl -X POST http://localhost:8080/api/analytics/predict \
  -H "Content-Type: application/json" \
  -d '{"query":"Forecast next week delay risk for Asia-Europe route","type":"delay"}'
```

## 🎨 Interface Features

### Maritime Theme Design
- **Ocean Color Scheme**: Deep blue to light blue gradient backgrounds
- **Maritime Elements**: Dynamic container ships, cargo ships, anchors, compasses and other maritime icons
- **Wave Animations**: Three-layer wave effects creating ocean atmosphere
- **Container Colors**: Orange, red, blue, green, yellow and other container theme colors
- **Gradient Backgrounds and Glass Effects**: Professional maritime theme visual effects
- **Smooth Animations and Transitions**: Ship sailing, floating, rotating animations
- **Responsive Layout**: Perfect adaptation for mobile and desktop
- **Fullscreen Chat Mode**: Enhanced AI assistant interaction experience

## 🔧 Configuration

### Application Configuration (application.yml)
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

### Environment Variables
- `GOOGLE_TRANSLATE_API_KEY`: Google Translate API key
- `SERVER_PORT`: Server port (default 8080)

## 📁 Project Structure

```
Emerging-new-try/
├── README.md                                 # Project documentation
├── index.html                                # Main page
├── dashboard.html                            # Real-time data visualization dashboard
├── script.js                                 # Frontend JavaScript
├── pom.xml                                   # Maven configuration
├── docs/                                     # Documentation folder
│   ├── AZURE_OPENAI_INTEGRATION.md          # Azure OpenAI integration guide
│   ├── AZURE_OPENAI_SETUP.md                # Azure OpenAI setup
│   ├── POWERBI_EMBEDDED_GUIDE.md            # Power BI embedded guide
│   ├── POWERBI_SETUP.md                     # Power BI setup
│   ├── PROJECT_SUMMARY.md                   # Project summary
│   ├── PSA_KEYWORD_EXTRACTION_README.md     # PSA keyword extraction guide
│   ├── SETUP.md                             # Setup guide
│   └── SMART_TRACKING_SUMMARY.md            # Smart tracking summary
├── demos/                                    # Demo pages folder
│   ├── advanced-visualization-demo.html      # Advanced visualization demo
│   ├── debug-translation.html               # Translation debug page
│   ├── enhanced-index-demo.html             # Enhanced homepage demo
│   ├── information-architecture-demo.html   # Information architecture demo
│   ├── maritime-theme-demo.html             # Maritime theme UI demo
│   └── quick-test.html                      # Quick test page
├── scripts/                                  # Scripts folder
│   ├── start.sh                             # Startup script
│   ├── start-full.sh                        # Full startup script
│   ├── start-simple.sh                      # Simple startup script
│   ├── start-with-config.sh                 # Config startup script
│   ├── test-*.sh                            # Various test scripts
│   └── config.env                           # Environment configuration file
├── data/                                    # Data folder
│   ├── psa-complete-data.txt                # PSA complete data
│   ├── psa-extended-data.txt                # PSA extended data
│   └── psa-raw-data.txt                     # PSA raw data
├── tests/                                    # Test folder
│   ├── test-all-buttons-undefined-fix.html   # Button undefined fix test
│   ├── test-all-quick-buttons.html           # All quick buttons test
│   ├── test-auto-report-generator.html       # Auto report generator test
│   ├── test-button-undefined-fix.html        # Button undefined fix test
│   ├── test-enhanced-ui.html                # Enhanced UI test
│   ├── test-fullscreen-chat.html             # Fullscreen chat test
│   ├── test-fullscreen-display-fix.html     # Fullscreen display fix test
│   ├── test-index-translation.html           # Homepage translation test
│   ├── test-language-default-english.html    # Default English test
│   ├── test-new-ai-features.html             # New AI features test
│   ├── test-predictive-analytics.html        # Predictive analytics test
│   ├── test-rag-historical-memory.html       # RAG historical memory test
│   ├── test-root-translation.html            # Root directory translation test
│   ├── test-translation-undefined-fix.html   # Translation undefined fix test
│   └── test-translation.html                 # Translation function test
├── src/main/java/com/networkinsights/ai/     # Java backend code
│   ├── NetworkInsightsApplication.java       # Main application class
│   ├── controller/                          # REST API controllers
│   │   ├── ChatController.java               # Chat controller
│   │   ├── TeamCoordinationController.java   # Team coordination controller
│   │   ├── SmartTrackingController.java     # Smart tracking controller
│   │   └── AnalyticsController.java          # Analytics controller
│   ├── service/                             # Business logic services
│   │   ├── AIService.java                   # AI service
│   │   ├── TranslationService.java          # Translation service
│   │   ├── TeamCoordinationService.java     # Team coordination service
│   │   ├── DocumentProcessingService.java   # Document processing service
│   │   └── AnalyticsService.java            # Analytics service
│   ├── model/                               # Data models
│   │   ├── ChatMessage.java                 # Chat message model
│   │   ├── TeamMember.java                  # Team member model
│   │   ├── LanguageRequest.java             # Language request model
│   │   └── SupportedLanguage.java           # Supported language model
│   └── config/                              # Configuration classes
│       └── CorsConfig.java                  # CORS configuration
└── src/main/resources/                     # Resource files
    ├── static/                              # Static resources
    │   ├── index.html                       # Main page (Spring Boot service)
    │   └── dashboard.html                   # Data dashboard (Spring Boot service)
    └── application.yml                      # Application configuration
```

## 🌟 Key Highlights

1. **Maritime Theme Design**: Professional maritime industry interface with ocean colors and ship elements
2. **AI-Driven**: Integrated multiple AI services for intelligent analysis and prediction
3. **Real-time Interaction**: Frontend-backend real-time communication with dynamic data updates
4. **Multilingual Support**: Real-time translation support for 30+ languages
5. **Modern UI**: Using latest web technologies and maritime theme design
6. **Data Visualization**: Dynamic KPI dashboard, global maps, timeline analysis
7. **Team Coordination**: Intelligently identify and connect relevant team members
8. **Predictive Analytics**: AI predicts port delays and carbon emission risks
9. **Historical Cases**: RAG system provides historical solutions
10. **Auto Reports**: Intelligently generate professional analysis reports
11. **Easy to Extend**: Modular design, easy to add new features
12. **Fullscreen Experience**: Immersive AI assistant interaction

**Network Insights**