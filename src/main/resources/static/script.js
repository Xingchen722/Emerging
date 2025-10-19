// Network Insights - 全球协调AI助手 JavaScript
// 与Java后端API集成

// API基础URL
const API_BASE_URL = 'http://localhost:8080/api';

// 当前选择的语言
let currentLanguage = 'zh';

// 支持的语言映射
const languageMap = {
    'zh': '中文',
    'en': 'English',
    'de': 'Deutsch',
    'fr': 'Français',
    'ja': '日本語',
    'ko': '한국어',
    'es': 'Español',
    'it': 'Italiano',
    'pt': 'Português',
    'ru': 'Русский'
};

// 多语言文本包
const i18nDict = {
    zh: {
        // 导航
        'nav.home': '首页',
        'nav.features': '功能',
        'nav.chat': 'AI助手',
        'nav.about': '关于',
        
        // 英雄区域
        'hero.title': '全球协调AI助手',
        'hero.subtitle': '让全球团队协作更智能、更高效。通过AI驱动的对话式界面，快速获取项目状态、联系团队成员、获取实时报告。',
        'hero.startChat': '开始对话',
        'hero.learnMore': '了解更多',
        
        // 功能区域
        'features.title': '核心功能',
        'features.smartQA.title': '智能问答',
        'features.smartQA.desc': '快速获取项目状态、货物位置、系统状态等实时信息',
        'features.teamCom.title': '团队沟通辅助',
        'features.teamCom.desc': '自动识别并连接相关团队成员，促进跨部门协作',
        'features.reports.title': '智能报告',
        'features.reports.desc': '自动生成每日/每周全球协调报告，识别瓶颈和机会',
        'features.translation.title': '语言辅助',
        'features.translation.desc': '实时翻译多语言消息，消除全球团队沟通障碍',
        'features.example': '示例：',
        
        // 聊天区域
        'chat.title': 'AI助手对话',
        'chat.aiAssistant': 'Network AI助手',
        'chat.online': '在线',
        'chat.selectLanguage': '选择语言:',
        'chat.inputPlaceholder': '输入你的问题...',
        'chat.quickActions.shipment': '查询货物状态',
        'chat.quickActions.contact': '联系团队',
        'chat.quickActions.report': '生成报告',
        'chat.welcome.hello': '你好！我是Network Insights AI助手。我可以帮你：',
        'chat.welcome.item1': '查询项目状态和货物位置',
        'chat.welcome.item2': '联系相关团队成员',
        'chat.welcome.item3': '生成协调报告',
        'chat.welcome.item4': '翻译多语言消息',
        'chat.welcome.ask': '有什么我可以帮助你的吗？',
        
        // 关于区域
        'about.title': '关于Network Insights',
        'about.desc1': 'Network Insights是一个专为跨国公司设计的智能协调平台。我们理解全球团队协作的挑战，因此开发了这个AI驱动的解决方案。',
        'about.desc2': '通过自然语言处理、实时数据分析和智能推荐，我们帮助全球团队更高效地沟通、协调和决策。',
        'about.stats.availability': '全天候服务',
        'about.stats.languages': '支持语言',
        'about.stats.uptime': '系统可用性',
        
        // 页脚
        'footer.tagline': '让全球协作更智能',
        'footer.product': '产品',
        'footer.features': '功能特性',
        'footer.aiAssistant': 'AI助手',
        'footer.about': '关于我们',
        'footer.support': '支持',
        'footer.help': '帮助中心',
        'footer.contact': '联系我们',
        'footer.privacy': '隐私政策',
        'footer.copyright': '© 2024 Network Insights. 保留所有权利。'
    },
    en: {
        // Navigation
        'nav.home': 'Home',
        'nav.features': 'Features',
        'nav.chat': 'AI Assistant',
        'nav.about': 'About',
        
        // Hero section
        'hero.title': 'Global Coordination AI Assistant',
        'hero.subtitle': 'Make global team collaboration smarter and more efficient. Through AI-driven conversational interface, quickly get project status, contact team members, and get real-time reports.',
        'hero.startChat': 'Start Chat',
        'hero.learnMore': 'Learn More',
        
        // Features section
        'features.title': 'Core Features',
        'features.smartQA.title': 'Smart Q&A',
        'features.smartQA.desc': 'Quickly get real-time information about project status, cargo location, system status, etc.',
        'features.teamCom.title': 'Team Communication Assistant',
        'features.teamCom.desc': 'Automatically identify and connect relevant team members to promote cross-departmental collaboration',
        'features.reports.title': 'Smart Reports',
        'features.reports.desc': 'Automatically generate daily/weekly global coordination reports to identify bottlenecks and opportunities',
        'features.translation.title': 'Language Assistant',
        'features.translation.desc': 'Real-time translation of multilingual messages to eliminate global team communication barriers',
        'features.example': 'Example:',
        
        // Chat section
        'chat.title': 'AI Assistant Chat',
        'chat.aiAssistant': 'Network AI Assistant',
        'chat.online': 'Online',
        'chat.selectLanguage': 'Select Language:',
        'chat.inputPlaceholder': 'Enter your question...',
        'chat.quickActions.shipment': 'Query Shipment Status',
        'chat.quickActions.contact': 'Contact Team',
        'chat.quickActions.report': 'Generate Report',
        'chat.welcome.hello': 'Hi! I am the Network Insights AI assistant. I can help you with:',
        'chat.welcome.item1': 'Checking project status and shipment location',
        'chat.welcome.item2': 'Contacting relevant team members',
        'chat.welcome.item3': 'Generating coordination reports',
        'chat.welcome.item4': 'Translating multilingual messages',
        'chat.welcome.ask': 'How can I help you today?',
        
        // About section
        'about.title': 'About Network Insights',
        'about.desc1': 'Network Insights is an intelligent coordination platform designed for multinational companies. We understand the challenges of global team collaboration, so we developed this AI-driven solution.',
        'about.desc2': 'Through natural language processing, real-time data analysis and intelligent recommendations, we help global teams communicate, coordinate and make decisions more efficiently.',
        'about.stats.availability': '24/7 Service',
        'about.stats.languages': 'Supported Languages',
        'about.stats.uptime': 'System Uptime',
        
        // Footer
        'footer.tagline': 'Making Global Collaboration Smarter',
        'footer.product': 'Product',
        'footer.features': 'Features',
        'footer.aiAssistant': 'AI Assistant',
        'footer.about': 'About Us',
        'footer.support': 'Support',
        'footer.help': 'Help Center',
        'footer.contact': 'Contact Us',
        'footer.privacy': 'Privacy Policy',
        'footer.copyright': '© 2024 Network Insights. All rights reserved.'
    },
    de: {
        // Navigation
        'nav.home': 'Start',
        'nav.features': 'Funktionen',
        'nav.chat': 'KI-Assistent',
        'nav.about': 'Über',
        
        // Hero section
        'hero.title': 'Globaler Koordinations-KI-Assistent',
        'hero.subtitle': 'Machen Sie die globale Teamzusammenarbeit intelligenter und effizienter. Durch KI-gesteuerte Gesprächsoberfläche schnell Projektstatus abrufen, Teammitglieder kontaktieren und Echtzeitberichte erhalten.',
        'hero.startChat': 'Chat starten',
        'hero.learnMore': 'Mehr erfahren',
        
        // Features section
        'features.title': 'Kernfunktionen',
        'features.smartQA.title': 'Intelligente Q&A',
        'features.smartQA.desc': 'Schnell Echtzeitinformationen über Projektstatus, Frachtstandort, Systemstatus usw. abrufen',
        'features.teamCom.title': 'Team-Kommunikationsassistent',
        'features.teamCom.desc': 'Automatisch relevante Teammitglieder identifizieren und verbinden, um bereichsübergreifende Zusammenarbeit zu fördern',
        'features.reports.title': 'Intelligente Berichte',
        'features.reports.desc': 'Automatisch tägliche/wöchentliche globale Koordinationsberichte generieren, um Engpässe und Chancen zu identifizieren',
        'features.translation.title': 'Sprachassistent',
        'features.translation.desc': 'Echtzeitübersetzung mehrsprachiger Nachrichten zur Beseitigung globaler Teamkommunikationsbarrieren',
        'features.example': 'Beispiel:',
        
        // Chat section
        'chat.title': 'KI-Assistenten-Chat',
        'chat.aiAssistant': 'Network KI-Assistent',
        'chat.online': 'Online',
        'chat.selectLanguage': 'Sprache wählen:',
        'chat.inputPlaceholder': 'Geben Sie Ihre Frage ein...',
        'chat.quickActions.shipment': 'Frachtstatus abfragen',
        'chat.quickActions.contact': 'Team kontaktieren',
        'chat.quickActions.report': 'Bericht generieren',
        'chat.welcome.hello': 'Hallo! Ich bin der Network Insights KI-Assistent. Ich kann dir helfen mit:',
        'chat.welcome.item1': 'Projektstatus und Frachtstandort prüfen',
        'chat.welcome.item2': 'Relevante Teammitglieder kontaktieren',
        'chat.welcome.item3': 'Koordinationsberichte erstellen',
        'chat.welcome.item4': 'Mehrsprachige Nachrichten übersetzen',
        'chat.welcome.ask': 'Wobei kann ich dir heute helfen?',
        
        // About section
        'about.title': 'Über Network Insights',
        'about.desc1': 'Network Insights ist eine intelligente Koordinationsplattform für multinationale Unternehmen. Wir verstehen die Herausforderungen der globalen Teamzusammenarbeit und haben daher diese KI-gesteuerte Lösung entwickelt.',
        'about.desc2': 'Durch natürliche Sprachverarbeitung, Echtzeitdatenanalyse und intelligente Empfehlungen helfen wir globalen Teams, effizienter zu kommunizieren, zu koordinieren und Entscheidungen zu treffen.',
        'about.stats.availability': '24/7-Service',
        'about.stats.languages': 'Unterstützte Sprachen',
        'about.stats.uptime': 'Systemverfügbarkeit',
        
        // Footer
        'footer.tagline': 'Globale Zusammenarbeit intelligenter machen',
        'footer.product': 'Produkt',
        'footer.features': 'Funktionen',
        'footer.aiAssistant': 'KI-Assistent',
        'footer.about': 'Über uns',
        'footer.support': 'Support',
        'footer.help': 'Hilfezentrum',
        'footer.contact': 'Kontakt',
        'footer.privacy': 'Datenschutz',
        'footer.copyright': '© 2024 Network Insights. Alle Rechte vorbehalten.'
    }
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    initializeLanguage();
    initializeChat();
    initializeNavigation();
    initializeAnimations();
    initializeNavLanguageDropdown();
});

// 初始化语言设置
function initializeLanguage() {
    // 从localStorage获取保存的语言，否则使用浏览器语言或默认中文
    const savedLanguage = localStorage.getItem('language');
    const browserLanguage = navigator.language.split('-')[0];
    
    if (savedLanguage && i18nDict[savedLanguage]) {
        currentLanguage = savedLanguage;
    } else if (i18nDict[browserLanguage]) {
        currentLanguage = browserLanguage;
    } else {
        currentLanguage = 'zh'; // 默认中文
    }
    
    // 设置语言选择器
    const languageSelect = document.getElementById('languageSelect');
    if (languageSelect) {
        languageSelect.value = currentLanguage;
    }
    
    // 应用语言
    applyLanguage(currentLanguage);
}

// 应用语言到整个页面
function applyLanguage(lang) {
    // 更新所有带data-i18n的元素
    document.querySelectorAll('[data-i18n]').forEach(element => {
        const key = element.getAttribute('data-i18n');
        const text = i18nDict[lang]?.[key] || i18nDict['zh']?.[key] || element.textContent;
        element.textContent = text;
    });
    
    // 更新所有带data-i18n-placeholder的元素
    document.querySelectorAll('[data-i18n-placeholder]').forEach(element => {
        const key = element.getAttribute('data-i18n-placeholder');
        const placeholder = i18nDict[lang]?.[key] || i18nDict['zh']?.[key] || element.placeholder;
        element.placeholder = placeholder;
    });

    // 若欢迎消息存在，更新其中的列表与文本（已通过data-i18n会自动更新）
    const welcome = document.getElementById('welcomeMessage');
    if (welcome) {
        // 触发一次layout刷新，保证新语言立即展示
        welcome.style.opacity = '0.99';
        setTimeout(() => { welcome.style.opacity = '1'; }, 0);
    }
    
    // 更新当前语言变量
    currentLanguage = lang;
    
    // 保存到localStorage
    localStorage.setItem('language', lang);
}

// 右上角语言下拉
function initializeNavLanguageDropdown() {
    const btn = document.getElementById('navLangBtn');
    const menu = document.getElementById('navLangMenu');
    if (!btn || !menu) return;

    btn.addEventListener('click', () => {
        const open = menu.style.display === 'block';
        menu.style.display = open ? 'none' : 'block';
        btn.setAttribute('aria-expanded', String(!open));
    });

    // 点击菜单项切换语言
    menu.querySelectorAll('.lang-item').forEach(item => {
        item.addEventListener('click', () => {
            const lang = item.getAttribute('data-lang');
            if (lang && i18nDict[lang]) {
                applyLanguage(lang);
                showLanguageChangeNotification();
            }
            menu.style.display = 'none';
            btn.setAttribute('aria-expanded', 'false');
        });
    });

    // 点击外部关闭
    document.addEventListener('click', (e) => {
        if (!menu.contains(e.target) && !btn.contains(e.target)) {
            menu.style.display = 'none';
            btn.setAttribute('aria-expanded', 'false');
        }
    });
}

// 初始化聊天功能
function initializeChat() {
    const messageInput = document.getElementById('messageInput');
    const sendButton = document.getElementById('sendButton');
    
    // 发送按钮点击事件
    sendButton.addEventListener('click', sendMessage);
    
    // 输入框回车事件
    messageInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            sendMessage();
        }
    });
    
    // 输入框聚焦效果
    messageInput.addEventListener('focus', function() {
        this.parentElement.style.transform = 'scale(1.02)';
    });
    
    messageInput.addEventListener('blur', function() {
        this.parentElement.style.transform = 'scale(1)';
    });
}

// 发送消息函数
async function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const message = messageInput.value.trim();
    
    if (message === '') return;
    
    // 添加用户消息到聊天界面
    addMessageToChat(message, 'user');
    
    // 清空输入框
    messageInput.value = '';
    
    // 显示加载状态
    showTypingIndicator();
    
    try {
        // 尝试调用Java后端API
        const response = await fetch(`${API_BASE_URL}/chat/message?language=${currentLanguage}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                content: message,
                sender: 'user',
                language: currentLanguage
            })
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const aiMessage = await response.json();
        
        // 移除加载状态
        hideTypingIndicator();
        
        // 添加AI回复到聊天界面
        addMessageToChat(aiMessage.content, 'ai');
        
    } catch (error) {
        console.log('后端服务不可用，使用模拟数据:', error.message);
        hideTypingIndicator();
        
        // 使用模拟AI回复
        const aiResponse = generateMockAIResponse(message);
        addMessageToChat(aiResponse, 'ai');
    }
}

// 新增：关键词提取功能
async function extractKeywords(question) {
    try {
        const response = await fetch(`${API_BASE_URL}/chat/extract-keywords`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(question)
        });
        
        if (response.ok) {
            return await response.json();
        }
    } catch (error) {
        console.error('关键词提取失败:', error);
    }
    return null;
}

// 新增：查询PSA数据功能
async function queryPSAData(question) {
    try {
        const response = await fetch(`${API_BASE_URL}/chat/query-by-question?question=${encodeURIComponent(question)}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        
        if (response.ok) {
            return await response.json();
        }
    } catch (error) {
        console.error('PSA数据查询失败:', error);
    }
    return [];
}

// 新增：获取可用数据类型
async function getAvailableDataTypes() {
    try {
        const response = await fetch(`${API_BASE_URL}/chat/psa-data-types`);
        if (response.ok) {
            return await response.json();
        }
    } catch (error) {
        console.error('获取数据类型失败:', error);
    }
    return [];
}

// 新增：获取可用港口列表
async function getAvailablePorts() {
    try {
        const response = await fetch(`${API_BASE_URL}/chat/psa-ports`);
        if (response.ok) {
            return await response.json();
        }
    } catch (error) {
        console.error('获取港口列表失败:', error);
    }
    return [];
}

// 新增：增强的模拟AI回复，包含PSA数据
async function generateEnhancedMockAIResponse(message) {
    // 首先尝试提取关键词
    const keywords = await extractKeywords(message);
    
    // 然后查询PSA数据
    const psaData = await queryPSAData(message);
    
    // 如果有PSA数据，生成基于数据的回复
    if (psaData && psaData.length > 0) {
        return generatePSADataResponse(psaData, currentLanguage);
    }
    
    // 否则使用传统的关键词匹配
    return generateMockAIResponse(message);
}

// 新增：基于PSA数据生成回复
function generatePSADataResponse(psaData, language) {
    let response = '';
    
    // 按数据类型分组
    const dataByType = {};
    psaData.forEach(data => {
        if (!dataByType[data.dataType]) {
            dataByType[data.dataType] = [];
        }
        dataByType[data.dataType].push(data);
    });
    
    // 为每种数据类型生成回复
    Object.entries(dataByType).forEach(([dataType, dataList]) => {
        const data = dataList[0]; // 取第一个数据
        const metrics = data.metrics || {};
        
        switch (dataType) {
            case 'berth_time':
                response += generateBerthTimeResponse(data, metrics, language);
                break;
            case 'carbon_savings':
                response += generateCarbonSavingsResponse(data, metrics, language);
                break;
            case 'port_efficiency':
                response += generatePortEfficiencyResponse(data, metrics, language);
                break;
            case 'cargo_tracking':
                response += generateCargoTrackingResponse(data, metrics, language);
                break;
            case 'port_congestion':
                response += generatePortCongestionResponse(data, metrics, language);
                break;
            case 'vessel_scheduling':
                response += generateVesselSchedulingResponse(data, metrics, language);
                break;
            default:
                response += generateGenericDataResponse(data, language);
        }
        response += '\n\n';
    });
    
    return response.trim();
}

// 新增：生成泊位时间回复
function generateBerthTimeResponse(data, metrics, language) {
    if (language === 'zh') {
        return `📊 ${data.portName}泊位时间信息：\n` +
               `• 平均泊位时间：${metrics.average_berth_time}小时\n` +
               `• 当前等待船舶：${metrics.current_waiting}艘\n` +
               `• 效率评分：${metrics.efficiency_score}分\n` +
               `• 更新时间：${new Date(data.timestamp).toLocaleString()}`;
    } else {
        return `📊 ${data.portName} Berth Time Information:\n` +
               `• Average Berth Time: ${metrics.average_berth_time} hours\n` +
               `• Current Waiting Vessels: ${metrics.current_waiting}\n` +
               `• Efficiency Score: ${metrics.efficiency_score} points\n` +
               `• Last Updated: ${new Date(data.timestamp).toLocaleString()}`;
    }
}

// 新增：生成碳减排回复
function generateCarbonSavingsResponse(data, metrics, language) {
    if (language === 'zh') {
        return `🌱 ${data.portName}碳减排成果：\n` +
               `• CO2减排：${metrics.co2_reduction}%\n` +
               `• 燃料节省：${metrics.fuel_savings}%\n` +
               `• 效率提升：${metrics.efficiency_gain}%\n` +
               `• 更新时间：${new Date(data.timestamp).toLocaleString()}`;
    } else {
        return `🌱 ${data.portName} Carbon Savings Achievements:\n` +
               `• CO2 Reduction: ${metrics.co2_reduction}%\n` +
               `• Fuel Savings: ${metrics.fuel_savings}%\n` +
               `• Efficiency Gain: ${metrics.efficiency_gain}%\n` +
               `• Last Updated: ${new Date(data.timestamp).toLocaleString()}`;
    }
}

// 新增：生成港口效率回复
function generatePortEfficiencyResponse(data, metrics, language) {
    if (language === 'zh') {
        return `⚡ ${data.portName}港口效率指标：\n` +
               `• 每小时吞吐量：${metrics.throughput_per_hour}个集装箱\n` +
               `• 起重机利用率：${metrics.crane_utilization}%\n` +
               `• 泊位占用率：${metrics.berth_occupancy}%\n` +
               `• 更新时间：${new Date(data.timestamp).toLocaleString()}`;
    } else {
        return `⚡ ${data.portName} Port Efficiency Metrics:\n` +
               `• Throughput per Hour: ${metrics.throughput_per_hour} containers\n` +
               `• Crane Utilization: ${metrics.crane_utilization}%\n` +
               `• Berth Occupancy: ${metrics.berth_occupancy}%\n` +
               `• Last Updated: ${new Date(data.timestamp).toLocaleString()}`;
    }
}

// 新增：生成货物追踪回复
function generateCargoTrackingResponse(data, metrics, language) {
    if (language === 'zh') {
        return `🚢 货物#${metrics.shipment_id}追踪信息：\n` +
               `• 状态：${metrics.status}\n` +
               `• 当前位置：${metrics.location}\n` +
               `• 预计到达：${metrics.eta}\n` +
               `• 更新时间：${new Date(data.timestamp).toLocaleString()}`;
    } else {
        return `🚢 Shipment #${metrics.shipment_id} Tracking Information:\n` +
               `• Status: ${metrics.status}\n` +
               `• Current Location: ${metrics.location}\n` +
               `• ETA: ${metrics.eta}\n` +
               `• Last Updated: ${new Date(data.timestamp).toLocaleString()}`;
    }
}

// 新增：生成港口拥堵回复
function generatePortCongestionResponse(data, metrics, language) {
    if (language === 'zh') {
        return `🚦 ${data.portName}港口拥堵状况：\n` +
               `• 等待时间：${metrics.waiting_time}小时\n` +
               `• 排队船舶：${metrics.queue_length}艘\n` +
               `• 拥堵等级：${metrics.congestion_level}\n` +
               `• 更新时间：${new Date(data.timestamp).toLocaleString()}`;
    } else {
        return `🚦 ${data.portName} Port Congestion Status:\n` +
               `• Waiting Time: ${metrics.waiting_time} hours\n` +
               `• Queue Length: ${metrics.queue_length} vessels\n` +
               `• Congestion Level: ${metrics.congestion_level}\n` +
               `• Last Updated: ${new Date(data.timestamp).toLocaleString()}`;
    }
}

// 新增：生成船舶调度回复
function generateVesselSchedulingResponse(data, metrics, language) {
    if (language === 'zh') {
        return `📅 ${data.portName}船舶调度计划：\n` +
               `• 计划到达：${metrics.scheduled_arrivals}艘\n` +
               `• 计划出发：${metrics.scheduled_departures}艘\n` +
               `• 下次到达：${metrics.next_arrival}\n` +
               `• 更新时间：${new Date(data.timestamp).toLocaleString()}`;
    } else {
        return `📅 ${data.portName} Vessel Scheduling Plan:\n` +
               `• Scheduled Arrivals: ${metrics.scheduled_arrivals} vessels\n` +
               `• Scheduled Departures: ${metrics.scheduled_departures} vessels\n` +
               `• Next Arrival: ${metrics.next_arrival}\n` +
               `• Last Updated: ${new Date(data.timestamp).toLocaleString()}`;
    }
}

// 新增：生成通用数据回复
function generateGenericDataResponse(data, language) {
    if (language === 'zh') {
        return `📈 ${data.portName}数据信息：\n${data.description}\n更新时间：${new Date(data.timestamp).toLocaleString()}`;
    } else {
        return `📈 ${data.portName} Data Information:\n${data.description}\nLast Updated: ${new Date(data.timestamp).toLocaleString()}`;
    }
}

// 生成模拟AI回复
function generateMockAIResponse(message) {
    const lowerMessage = message.toLowerCase();
    
    // 根据关键词匹配回复类型
    if (containsKeywords(lowerMessage, ['货物', 'shipment', '物流', 'cargo', 'freight'])) {
        return getMockResponse('货物');
    } else if (containsKeywords(lowerMessage, ['联系', 'contact', '团队', 'team', '谁', 'who'])) {
        return getMockResponse('联系');
    } else if (containsKeywords(lowerMessage, ['报告', 'report', '总结', 'summary', '状态', 'status'])) {
        return getMockResponse('报告');
    } else if (containsKeywords(lowerMessage, ['翻译', 'translate', '语言', 'language'])) {
        return getMockResponse('翻译');
    } else {
        return getMockResponse('默认');
    }
}

// 检查消息是否包含关键词
function containsKeywords(message, keywords) {
    return keywords.some(keyword => message.includes(keyword));
}

// 获取模拟回复
function getMockResponse(category) {
    const responses = {
        '货物': [
            '货物#342目前在新加坡港口，预计今晚9点启航。',
            '货物#123已到达德国汉堡港，正在清关。',
            '货物#456目前在太平洋上，预计3天后到达洛杉矶。',
            '货物#789已从上海港出发，正在前往鹿特丹。'
        ],
        '联系': [
            '欧洲网络问题请联系Anna，她在法兰克福团队。我可以帮你发送消息给她。',
            '亚太地区物流问题请联系David，他在新加坡办公室。',
            '北美技术支持请联系Sarah，她在纽约团队。',
            '系统维护问题请联系Mike，他是我们的技术主管。'
        ],
        '报告': [
            '今天的全球协调报告：\n• 亚太地区延误率：5%\n• 欧洲地区沟通频率：+15%\n• 北美地区系统稳定性：99.8%\n• 建议关注：德国港口拥堵情况',
            '本周协调总结：\n• 全球项目完成率：92%\n• 跨时区沟通效率提升：20%\n• 语言翻译使用率：85%\n• 需要改进：时区协调会议安排',
            '实时状态更新：\n• 在线团队：45个\n• 活跃项目：128个\n• 待处理协调任务：12个\n• 系统负载：正常'
        ],
        '翻译': [
            '已为你翻译：\n英文："Good morning, how is the project going?"\n中文："早上好，项目进展如何？"',
            '语言检测：检测到德语消息\n翻译结果：\n"Guten Morgen, wie läuft das Projekt?"\n"早上好，项目进展如何？"',
            '多语言支持：已为你的团队启用中文、英文、德文、法文、日文翻译功能。'
        ],
        '默认': [
            '我理解你的问题。让我为你查找相关信息...',
            '这是一个很好的问题！让我帮你分析一下。',
            '我正在处理你的请求，请稍等片刻。',
            '让我为你提供最准确的信息。'
        ]
    };
    
    const categoryResponses = responses[category] || responses['默认'];
    return categoryResponses[Math.floor(Math.random() * categoryResponses.length)];
}

// 快速提问函数
async function askQuestion(question) {
    const messageInput = document.getElementById('messageInput');
    messageInput.value = question;
    await sendMessage();
}

// 语言切换函数
function changeLanguage() {
    const languageSelect = document.getElementById('languageSelect');
    const newLanguage = languageSelect.value;
    
    // 应用新语言到整个页面
    applyLanguage(newLanguage);
    
    // 显示语言切换通知
    showLanguageChangeNotification();
}

// 显示语言切换通知
function showLanguageChangeNotification() {
    const notification = document.createElement('div');
    notification.className = 'language-notification';
    notification.textContent = `语言已切换为: ${languageMap[currentLanguage]}`;
    
    document.body.appendChild(notification);
    
    // 3秒后移除通知
    setTimeout(() => {
        if (notification.parentNode) {
            notification.parentNode.removeChild(notification);
        }
    }, 3000);
}

// 显示打字指示器
function showTypingIndicator() {
    const chatMessages = document.getElementById('chatMessages');
    const typingDiv = document.createElement('div');
    typingDiv.id = 'typingIndicator';
    typingDiv.className = 'chat-message ai-message typing-indicator';
    
    typingDiv.innerHTML = `
        <div class="message-avatar ai-avatar">
            <i class="fas fa-robot"></i>
        </div>
        <div class="message-content">
            <div class="typing-dots">
                <span></span>
                <span></span>
                <span></span>
            </div>
        </div>
    `;
    
    chatMessages.appendChild(typingDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// 隐藏打字指示器
function hideTypingIndicator() {
    const typingIndicator = document.getElementById('typingIndicator');
    if (typingIndicator) {
        typingIndicator.remove();
    }
}

// 添加消息到聊天界面
function addMessageToChat(message, sender) {
    const chatMessages = document.getElementById('chatMessages');
    const messageDiv = document.createElement('div');
    messageDiv.className = `chat-message ${sender}-message`;
    
    const avatar = document.createElement('div');
    avatar.className = 'message-avatar';
    if (sender === 'ai') {
        avatar.className += ' ai-avatar';
        avatar.innerHTML = '<i class="fas fa-robot"></i>';
    } else {
        avatar.innerHTML = '<i class="fas fa-user"></i>';
    }
    
    const content = document.createElement('div');
    content.className = 'message-content';
    content.textContent = message;
    
    messageDiv.appendChild(avatar);
    messageDiv.appendChild(content);
    
    // 添加动画效果
    messageDiv.style.opacity = '0';
    messageDiv.style.transform = 'translateY(20px)';
    
    chatMessages.appendChild(messageDiv);
    
    // 滚动到底部
    chatMessages.scrollTop = chatMessages.scrollHeight;
    
    // 触发动画
    setTimeout(() => {
        messageDiv.style.transition = 'all 0.3s ease';
        messageDiv.style.opacity = '1';
        messageDiv.style.transform = 'translateY(0)';
    }, 100);
}

// 检测语言函数
async function detectLanguage(text) {
    try {
        const response = await fetch(`${API_BASE_URL}/chat/detect-language`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(text)
        });
        
        if (response.ok) {
            return await response.text();
        }
    } catch (error) {
        console.error('语言检测失败:', error);
    }
    return 'zh'; // 默认返回中文
}

// 翻译文本函数
async function translateText(text, sourceLang, targetLang) {
    try {
        const response = await fetch(`${API_BASE_URL}/chat/translate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                text: text,
                sourceLanguage: sourceLang,
                targetLanguage: targetLang
            })
        });
        
        if (response.ok) {
            return await response.text();
        }
    } catch (error) {
        console.error('翻译失败:', error);
    }
    return text; // 翻译失败时返回原文
}

// 初始化导航功能
function initializeNavigation() {
    // 平滑滚动到指定区域
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href').substring(1);
            const targetElement = document.getElementById(targetId);
            if (targetElement) {
                targetElement.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
}

// 滚动到聊天区域
function scrollToChat() {
    const chatSection = document.getElementById('chat');
    chatSection.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
    });
    
    // 聚焦到输入框
    setTimeout(() => {
        const messageInput = document.getElementById('messageInput');
        messageInput.focus();
    }, 1000);
}

// 滚动到功能区域
function scrollToFeatures() {
    const featuresSection = document.getElementById('features');
    featuresSection.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
    });
}

// 初始化动画效果
function initializeAnimations() {
    // 观察器用于触发动画
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.animation = 'slideInUp 0.6s ease-out';
                entry.target.style.opacity = '1';
            }
        });
    }, {
        threshold: 0.1
    });
    
    // 观察所有功能卡片
    const featureCards = document.querySelectorAll('.feature-card');
    featureCards.forEach(card => {
        card.style.opacity = '0';
        observer.observe(card);
    });
    
    // 观察统计数字
    const statNumbers = document.querySelectorAll('.stat-number');
    statNumbers.forEach(stat => {
        observer.observe(stat);
        stat.addEventListener('animationstart', () => {
            animateNumber(stat);
        });
    });
}

// 数字动画效果
function animateNumber(element) {
    const target = parseInt(element.textContent.replace(/[^\d]/g, ''));
    const duration = 2000;
    const start = performance.now();
    
    function updateNumber(currentTime) {
        const elapsed = currentTime - start;
        const progress = Math.min(elapsed / duration, 1);
        const current = Math.floor(progress * target);
        
        if (element.textContent.includes('24/7')) {
            element.textContent = '24/7';
        } else if (element.textContent.includes('50+')) {
            element.textContent = '50+';
        } else if (element.textContent.includes('99.9%')) {
            element.textContent = '99.9%';
        } else {
            element.textContent = current;
        }
        
        if (progress < 1) {
            requestAnimationFrame(updateNumber);
        }
    }
    
    requestAnimationFrame(updateNumber);
}

// 添加一些交互式效果
document.addEventListener('DOMContentLoaded', function() {
    // 为功能卡片添加悬停效果
    const featureCards = document.querySelectorAll('.feature-card');
    featureCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-10px) scale(1.02)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });
    
    // 为按钮添加点击效果
    const buttons = document.querySelectorAll('button');
    buttons.forEach(button => {
        button.addEventListener('click', function() {
            this.style.transform = 'scale(0.95)';
            setTimeout(() => {
                this.style.transform = 'scale(1)';
            }, 150);
        });
    });
    
    // 添加打字机效果到AI消息
    const aiMessages = document.querySelectorAll('.ai-message .message-content');
    aiMessages.forEach(message => {
        if (message.textContent.length > 50) {
            const text = message.textContent;
            message.textContent = '';
            typeText(message, text, 30);
        }
    });
});

// 打字机效果
function typeText(element, text, speed) {
    let i = 0;
    function type() {
        if (i < text.length) {
            element.textContent += text.charAt(i);
            i++;
            setTimeout(type, speed);
        }
    }
    type();
}

// 添加一些实用的工具函数
const NetworkInsights = {
    // 模拟获取实时数据
    getRealTimeData: function() {
        return {
            onlineTeams: Math.floor(Math.random() * 20) + 30,
            activeProjects: Math.floor(Math.random() * 50) + 100,
            systemLoad: Math.random() * 100,
            lastUpdate: new Date().toLocaleTimeString()
        };
    },
    
    // 模拟发送通知
    sendNotification: function(message) {
        if ('Notification' in window) {
            if (Notification.permission === 'granted') {
                new Notification('Network Insights', {
                    body: message,
                    icon: '/favicon.ico'
                });
            } else if (Notification.permission !== 'denied') {
                Notification.requestPermission().then(permission => {
                    if (permission === 'granted') {
                        new Notification('Network Insights', {
                            body: message,
                            icon: '/favicon.ico'
                        });
                    }
                });
            }
        }
    },
    
    // 模拟多语言检测
    detectLanguage: function(text) {
        const chineseRegex = /[\u4e00-\u9fff]/;
        const englishRegex = /[a-zA-Z]/;
        const germanRegex = /[äöüßÄÖÜ]/;
        
        if (chineseRegex.test(text)) return '中文';
        if (germanRegex.test(text)) return '德文';
        if (englishRegex.test(text)) return '英文';
        return '未知';
    }
};

// 导出到全局作用域
window.NetworkInsights = NetworkInsights;
window.scrollToChat = scrollToChat;
window.scrollToFeatures = scrollToFeatures;
window.askQuestion = askQuestion;
window.sendMessage = sendMessage;
