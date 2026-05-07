import React, { useState } from 'react';
import { Send, Paperclip, MoreHorizontal, User, ShieldCheck, FileText, Download } from 'lucide-react';
import './Chat.css';

const Chat = () => {
    const [messages, setMessages] = useState([
        { id: 1, sender: 'lawyer', text: 'السلام عليكم محمد، شفت الملف ديالك بخصوص عقد الكراء التجاري.', time: '09:30 AM' },
        { id: 2, sender: 'user', text: 'وعليكم السلام ذ. يوسف، شكراً على الرد السريع. واش فيه شي مخاطر قانونية بالنسبة ليا كصاحب محل؟', time: '09:35 AM' },
        { id: 3, sender: 'lawyer', text: 'كاين واحد الشرط في المادة 4 بخصوص الزيادة في السومة الكرائية شوية غير متوازن، غادي نشرحو ليك بالتفصيل.', time: '09:45 AM' }
    ]);
    const [input, setInput] = useState('');

    const sendMessage = () => {
        if (!input.trim()) return;
        setMessages([...messages, { id: Date.now(), sender: 'user', text: input, time: 'الآن' }]);
        setInput('');
    };

    return (
        <div className="chat-container-premium">
            <div className="chat-layout-premium">
                
                {/* Chat Sidebar / Info Panel */}
                <div className="chat-info-panel glass animate-fade-in">
                    <div className="panel-lawyer-card">
                        <div className="premium-avatar-box">
                            <User size={32} />
                            <div className="online-indicator"></div>
                        </div>
                        <h2>ذ. يوسف البقالي</h2>
                        <span className="premium-badge"><ShieldCheck size={14} /> محامٍ معتمد</span>
                        <p className="panel-desc">خبير في قانون الأعمال والقانون التجاري، معتمد من هيئة المحامين بالدار البيضاء.</p>
                    </div>

                    <div className="panel-section">
                        <h3><Paperclip size={18} /> الملفات المرفقة (2)</h3>
                        <div className="premium-file-card">
                            <div className="file-icon"><FileText size={20} /></div>
                            <div className="file-info">
                                <p className="file-name">عقد_كراء_تجاري.pdf</p>
                                <p className="file-meta">2.4 MB • بريميوم</p>
                            </div>
                            <button className="download-btn"><Download size={16} /></button>
                        </div>
                    </div>
                </div>

                {/* Main Chat Area */}
                <div className="chat-message-center">
                    <div className="chat-view-header">
                        <div className="header-info">
                            <h3>المحادثة الاستشارية</h3>
                            <p>مسفرة ومشفرة بالكامل 🔒</p>
                        </div>
                        <button className="header-action-btn"><MoreHorizontal size={20} /></button>
                    </div>

                    <div className="premium-messages-list">
                        <div className="chat-date-divider"><span>اليوم</span></div>
                        
                        {messages.map((msg) => (
                            <div key={msg.id} className={`premium-msg-row ${msg.sender} animate-slide-up`}>
                                <div className="msg-bubble-premium">
                                    <p className="msg-text">{msg.text}</p>
                                    <span className="msg-time">{msg.time}</span>
                                </div>
                            </div>
                        ))}
                    </div>

                    <div className="premium-input-box-wrap">
                        <div className="premium-input-inner glass">
                            <button className="attach-btn"><Paperclip size={20} /></button>
                            <input 
                                type="text" 
                                placeholder="اكتب استشارتك أو سؤالك هنا..." 
                                value={input}
                                onChange={(e) => setInput(e.target.value)}
                                onKeyDown={(e) => e.key === 'Enter' && sendMessage()}
                            />
                            <button className="premium-send-btn" onClick={sendMessage}>
                                <Send size={20} />
                            </button>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    );
};

export default Chat;
