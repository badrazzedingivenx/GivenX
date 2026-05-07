import React, { useState } from 'react';
import { Search, Send, Paperclip, MoreVertical, Phone, Video, Info } from 'lucide-react';
import './LawyerMessages.css';

const LawyerMessages = () => {
  const [activeChat, setActiveChat] = useState(1);

  const chats = [
    { id: 1, name: 'محمد أمين', lastMsg: 'شكرا جزيلا أستاذ على النصيحة', time: '14:20', unread: 0, status: 'online' },
    { id: 2, name: 'سارة العلمي', lastMsg: 'هل يمكننا تأجيل الموعد؟', time: 'أمس', unread: 2, status: 'offline' },
    { id: 3, name: 'ياسين بنعلي', lastMsg: 'أرسلت لك الملفات المطلوبة', time: 'الأربعاء', unread: 0, status: 'online' },
    { id: 4, name: 'ليلى منير', lastMsg: 'متى سيتم النطق بالحكم؟', time: '29/04', unread: 0, status: 'offline' },
  ];

  return (
    <div className="lm-wrapper animate-fade-in" dir="rtl">
      <div className="lm-container">
        {/* Sidebar: Chat List */}
        <div className="lm-sidebar">
          <div className="lm-sidebar-header">
            <h3>الرسائل</h3>
            <div className="lm-search">
              <Search size={18} />
              <input type="text" placeholder="البحث في المحادثات..." />
            </div>
          </div>
          <div className="lm-chat-list">
            {chats.map(chat => (
              <div 
                key={chat.id} 
                className={`lm-chat-item ${activeChat === chat.id ? 'active' : ''}`}
                onClick={() => setActiveChat(chat.id)}
              >
                <div className="chat-avatar">
                  {chat.name.charAt(0)}
                  <span className={`status-dot ${chat.status}`}></span>
                </div>
                <div className="chat-preview">
                  <div className="chat-name-row">
                    <h4>{chat.name}</h4>
                    <span className="chat-time">{chat.time}</span>
                  </div>
                  <p className="chat-last-msg">{chat.lastMsg}</p>
                </div>
                {chat.unread > 0 && <span className="unread-badge">{chat.unread}</span>}
              </div>
            ))}
          </div>
        </div>

        {/* Main Content: Chat Window */}
        <div className="lm-main">
          <div className="lm-chat-header">
            <div className="chat-user-info">
              <div className="chat-avatar">{chats.find(c => c.id === activeChat)?.name.charAt(0)}</div>
              <div>
                <h4>{chats.find(c => c.id === activeChat)?.name}</h4>
                <span className="user-status">نشط الآن</span>
              </div>
            </div>
            <div className="chat-actions">
              <button className="chat-action-btn"><Phone size={20} /></button>
              <button className="chat-action-btn"><Video size={20} /></button>
              <button className="chat-action-btn"><Info size={20} /></button>
            </div>
          </div>

          <div className="lm-chat-messages">
            <div className="msg-date"><span>اليوم</span></div>
            
            <div className="message received">
              <div className="msg-content">
                <p>السلام عليكم أستاذ، بخصوص قضية تأسيس الشركة، هل الملفات التي أرسلتها كافية؟</p>
                <span className="msg-time">10:15 صباحاً</span>
              </div>
            </div>

            <div className="message sent">
              <div className="msg-content">
                <p>وعليكم السلام سيدي. نعم، الملفات كافية حالياً، أحتاج فقط لنسخة مصادق عليها من بطاقة التعريف.</p>
                <span className="msg-time">10:30 صباحاً</span>
              </div>
            </div>

            <div className="message received">
              <div className="msg-content">
                <p>سأقوم بإرسالها لك في الحين. شكراً جزيلاً.</p>
                <span className="msg-time">10:32 صباحاً</span>
              </div>
            </div>
          </div>

          <div className="lm-chat-input">
            <button className="input-btn"><Paperclip size={20} /></button>
            <input type="text" placeholder="اكتب رسالتك هنا..." />
            <button className="send-btn"><Send size={20} /></button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LawyerMessages;
