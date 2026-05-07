import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Notifications.css';

const Notifications = () => {
    const navigate = useNavigate();

    const stats = [
        { label: 'الكل', count: 4, icon: '🔔' },
        { label: 'غير مقروء', count: 2, icon: '🔵' },
        { label: 'مهم', count: 1, icon: '⭐' }
    ];

    const notifications = [
        {
            id: 1,
            type: 'message',
            title: 'رسالة جديدة من المحامي',
            content: 'ذ. يوسف البقالي أرسل لك تفاصيل المشاورة القانونية الخاصة بك.',
            time: '09:45 AM',
            date: 'اليوم',
            unread: true,
            icon: '💬',
            action: 'عرض الرسالة',
            path: '/chat'
        },
        {
            id: 2,
            type: 'ai',
            title: 'الذكاء الاصطناعي جاهز',
            content: 'تم تحليل العقد الخاص بك بنجاح. يمكنك الآن مراجعة الملاحظات.',
            time: '08:20 AM',
            date: 'اليوم',
            unread: true,
            icon: '🤖',
            action: 'مراجعة التحليل',
            path: '/analysis-result'
        },
        {
            id: 3,
            type: 'system',
            title: 'تحديث النظام',
            content: 'تم إضافة خدمات جديدة في قسم العقود العقارية.',
            time: 'أمس',
            date: 'أمس',
            unread: false,
            icon: '⚙️',
            action: 'اكتشف المزيد',
            path: '/discover'
        },
        {
            id: 4,
            type: 'document',
            title: 'تم تحديث العقد',
            content: 'قام المحامي بتعديل بنود "عقد الكراء - درب عمر".',
            time: '04:15 PM',
            date: '12 مارس',
            unread: false,
            icon: '📄',
            action: 'تحميل العقد',
            path: '/contracts'
        }
    ];

    return (
        <div className="notifications-page alternative-design" dir="rtl">
            <div className="notifications-wrapper">
                {/* Stats Bar */}
                <div className="notifications-stats">
                    {stats.map((stat, i) => (
                        <div key={i} className="stat-pill">
                            <span className="stat-icon">{stat.icon}</span>
                            <span className="stat-label">{stat.label}</span>
                            <span className="stat-count">{stat.count}</span>
                        </div>
                    ))}
                    <button className="clear-all-btn">تصفية الكل</button>
                </div>

                <div className="timeline-container">
                    <div className="timeline-line"></div>
                    
                    {notifications.map((notif, index) => (
                        <div key={notif.id} className={`timeline-item ${notif.unread ? 'is-unread' : ''}`}>
                            <div className="timeline-dot"></div>
                            
                            <div className="notification-card">
                                <div className="card-header">
                                    <div className="card-type">
                                        <span className="type-icon">{notif.icon}</span>
                                        <span className="type-text">{notif.title}</span>
                                    </div>
                                    <span className="card-time">{notif.time} • {notif.date}</span>
                                </div>
                                
                                <div className="card-body">
                                    <p className="card-text">{notif.content}</p>
                                    <button 
                                        className="card-action-link"
                                        onClick={() => navigate(notif.path)}
                                    >
                                        {notif.action}
                                        <span className="arrow-icon">←</span>
                                    </button>
                                </div>

                                {notif.unread && <span className="unread-badge">جديد</span>}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default Notifications;
