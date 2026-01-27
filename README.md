# 🌐 Network Insights - Global Maritime Coordination AI Platform

An intelligent global coordination platform based on Java Spring Boot and modern web technologies, supporting multilingual AI conversations and real-time translation.

## Try it out
https://xingchen722.github.io/Emerging/index.html

## 📋 Project Overview

**Network Insights** is an enterprise-grade global maritime coordination AI platform designed specifically for the maritime logistics industry. The platform integrates multiple AI capabilities including team coordination, data analysis, predictive analytics, historical case retrieval, automatic report generation, and real-time data visualization.

### 🎯 Core Positioning

Network Insights is committed to providing intelligent coordination and management solutions for the global maritime logistics industry, helping teams achieve:

- 🌍 **Global Collaboration**: Cross-timezone, cross-language team coordination
- 📊 **Intelligent Analysis**: AI-powered analysis and prediction based on real-time data
- 🚢 **Professional Services**: Professional feature support for the maritime industry
- 🤖 **AI-Driven**: Integration of multiple AI services for intelligent experience

## ✨ Main Features

### 🤖 Intelligent AI Assistant

#### 1. Smart Q&A System
- **Real-time Queries**: Query project status, cargo location, system status, and other real-time information
- **Natural Language Processing**: Support for Chinese and English natural language conversations
- **Context Understanding**: Intelligent responses based on context
- **Fast Response**: Millisecond-level response time

#### 2. Team Coordination Features
- **Intelligent Identification**: Automatically identify and connect relevant team members
- **Regional Filtering**: Support filtering by region and expertise
- **Contact Information**: Provide detailed contact information for team members
- **Collaboration Suggestions**: Recommend best contacts based on query content

#### 3. Smart Cargo Tracking
- **Natural Language Queries**: Query cargo location, ETA, routes, and other information using natural language
- **Multi-dimensional Search**: Search by shipment ID, vessel name, port, etc.
- **Real-time Status**: Display real-time cargo location and status
- **Detailed Information**: Provide detailed information such as container count, weight, etc.

#### 4. Multilingual Translation
- **30+ Language Support**: Support for Chinese, English, Japanese, Korean, French, German, Spanish, and more
- **Real-time Translation**: Instant translation of conversation content
- **Language Detection**: Automatically detect input language
- **Professional Terminology**: Professional maritime industry terminology translation

#### 5. Predictive Analytics
- **Port Delay Prediction**: AI predicts port delay risks
- **Carbon Emission Prediction**: Predict carbon emission overrun risks
- **Trend Analysis**: Trend prediction based on historical data
- **Risk Assessment**: Multi-dimensional risk assessment reports

#### 6. Historical Case Retrieval (RAG)
- **Intelligent Retrieval**: Retrieve similar historical cases based on RAG system
- **Solution Recommendations**: Provide solutions for historical problems
- **Knowledge Base**: Accumulate and utilize historical experience
- **Best Practices**: Share cross-port best practices

#### 7. Automatic Report Generation
- **Intelligent Generation**: Automatically generate professional analysis reports
- **Multiple Report Types**: Support daily/weekly/monthly reports, KPI analysis, efficiency analysis, etc.
- **Visualization Charts**: Include rich charts and data visualization
- **Professional Format**: Industry-standard report format

### 📊 Data Analysis & Visualization

#### 1. Real-time KPI Dashboard
- **Dynamic Updates**: Real-time display of port efficiency, arrival accuracy, carbon reduction, and other metrics
- **Trend Indicators**: Display data trends and change directions
- **Multi-dimensional Metrics**: Support multi-dimensional KPI display
- **Interactive Operations**: Support click, zoom, hover, and other interactions

#### 2. Global Map Heatmap
- **Port Efficiency Visualization**: Display port efficiency on the map
- **Delay Situation Display**: Visualize delay situations
- **Carbon Emission Levels**: Display carbon emission level distribution
- **Real-time Vessel Positions**: Display real-time vessel positions and status

#### 3. Timeline Analysis
- **Historical Trend Comparison**: Compare historical data trends
- **AI Prediction Curves**: Display AI-predicted future trends
- **Multiple Time Dimensions**: Support daily/weekly/monthly/yearly time dimensions
- **Interactive Charts**: Support interactive timeline operations

#### 4. Multi-dimensional Drill-down Analysis
- **Hierarchical Analysis**: Hierarchical analysis from global → regional → port → specific vessels
- **Data Drill-down**: Support multi-level data drill-down
- **Correlation Analysis**: Analyze correlations between different levels
- **Flexible Navigation**: Flexible data navigation and exploration

### 🚢 Maritime Professional Features

#### 1. Vessel Tracking
- **Real-time Position Monitoring**: Real-time monitoring of vessel positions and status
- **Route Information**: Display vessel routes and destinations
- **Estimated Time of Arrival**: Provide accurate ETA information
- **Vessel Details**: Provide detailed vessel information

#### 2. Port Management
- **Berth Utilization**: Monitor berth usage
- **Crane Status**: Display crane operational status
- **Weather Conditions**: Provide port weather information
- **Operational Efficiency**: Analyze port operational efficiency

#### 3. Carbon Emission Analysis
- **Fuel Efficiency**: Analyze fuel usage efficiency
- **Renewable Energy Usage**: Monitor renewable energy usage
- **Emission Targets**: Track carbon emission target completion
- **Optimization Recommendations**: Provide emission reduction optimization recommendations

#### 4. Cost Optimization
- **Operational Cost Analysis**: Analyze various operational costs
- **Optimization Recommendations**: Provide cost optimization recommendations
- **Cost Prediction**: Predict future cost trends
- **ROI Analysis**: Return on investment analysis

#### 5. Emergency Response
- **Emergency Situation Handling**: Quick response to emergencies
- **Contact Information**: Provide relevant contact information
- **Emergency Plans**: Provide emergency plan guidance
- **Status Updates**: Real-time emergency status updates

#### 6. Compliance Check
- **Regulatory Compliance**: Check regulatory compliance status
- **Audit Status**: Track audit status
- **Compliance Reports**: Generate compliance reports
- **Risk Alerts**: Identify compliance risks

### 🌍 Multilingual Support
Support 30+ languages

## 🏗️ Technical Architecture

### Backend Technology Stack
The backend is built with Java 17, using Spring Boot 3.2.0 as the main framework, and integrates the following components and services:

1. Spring Web: For building RESTful APIs.
2. Spring Data JPA: Provides data persistence support.
3. H2 Database: In-memory database used for development.
4. Google Cloud Translate: AI-powered translation service.
5. Azure OpenAI: Integration with OpenAI services.
6. Power BI Embedded: Embedded Power BI data visualization.
7. Maven: Dependency management tool.
8. Jackson: JSON processing library.

### Frontend Technology Stack
The frontend is developed using HTML5, CSS3, and JavaScript ES6, featuring the following:

1. Responsive Design: Adaptable for both mobile and desktop devices.
2. CSS Grid & Flexbox: For modern layout design.
3. CSS Animations & Transitions: Provides smooth animation effects.
4. Fetch API: For making HTTP requests.
5. Chart.js: Interactive chart library.
6. Leaflet: Global map visualization.
7. Three.js: 3D data visualization.
8. Font Awesome: Icon library.

### Core Service Components
#### 1. AI Service Layer
- **AIService**: Core AI service for intelligent conversations
- **OpenAIService**: Azure OpenAI integration service
- **TranslationService**: Multilingual translation service
- **RealTimeAIAnalysisService**: Real-time AI analysis service

#### 2. Business Service Layer
- **TeamCoordinationService**: Team coordination service
- **SmartTrackingService**: Smart cargo tracking service
- **PSADataService**: PSA data processing service
- **DocumentProcessingService**: Document processing service
- **KeywordExtractionService**: Keyword extraction service

#### 3. Data Service Layer
- **PowerBIService**: Power BI data service
- **PowerBIEmbeddedService**: Power BI embedded service
- **DataReadingService**: Data reading service

#### 4. API Controller Layer
- **ChatController**: Chat API controller
- **TeamCoordinationController**: Team coordination API
- **SmartTrackingController**: Smart tracking API
- **PowerBIController**: Power BI API
- **FileUploadController**: File upload API

## 🎨 Interface Features

### Maritime Theme Design

- **Ocean Color Scheme**: Deep blue to light blue gradient backgrounds
- **Maritime Elements**: Dynamic container ships, cargo ships, anchors, compasses, and other maritime icons
- **Wave Animations**: Three-layer wave effects creating ocean atmosphere
- **Container Colors**: Orange, red, blue, green, yellow, and other container theme colors
- **Gradient Backgrounds and Glass Effects**: Professional maritime theme visual effects
- **Smooth Animations and Transitions**: Ship sailing, floating, rotating animations
- **Responsive Layout**: Perfect adaptation for mobile and desktop
- **Fullscreen Chat Mode**: Immersive AI assistant interaction experience

### User Experience Optimization

- **Real-time Typing Indicator**: Display AI typing status
- **Language Switch Notification**: Friendly notification when switching languages
- **Smooth Scroll Navigation**: Smooth page navigation experience
- **Error Handling**: Friendly error prompts and handling
- **Loading States**: Clear loading state indicators
- **Responsive Design**: Adapt to various screen sizes
