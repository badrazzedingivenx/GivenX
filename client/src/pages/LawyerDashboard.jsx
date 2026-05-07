import React from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Video, 
  CalendarCheck, 
  Wallet, 
  TrendingUp, 
  Plus, 
  Eye, 
  Heart,
  MessageCircle,
  MoreVertical,
  Star
} from 'lucide-react';
import './LawyerDashboard.css';

const LawyerDashboard = () => {
  const navigate = useNavigate();

  // Mock Data
  const stats = [
    { id: 1, label: 'إجمالي المشاهدات', value: '14.5K', icon: <Eye size={20} />, color: '#7c3aed' },
    { id: 2, label: 'المواعيد القادمة', value: '4', icon: <CalendarCheck size={20} />, color: '#0369a1' },
    { id: 3, label: 'أرباح الشهر', value: '4,500 د.م', icon: <Wallet size={20} />, color: '#16a34a' },
    { id: 4, label: 'تقييم الحساب', value: '4.9/5', icon: <Star size={20} />, color: '#d97706' },
  ];

  const videos = [
    { id: 1, title: 'كيفاش تأسس شركة في المغرب', date: 'منذ يومين', views: '2.4K', likes: '450', thumbnail: 'linear-gradient(135deg, #1e1b4b, #4f46e5)' },
    { id: 2, title: 'الفصل التعسفي بالقانون', date: 'منذ أسبوع', views: '12K', likes: '1.2K', thumbnail: 'linear-gradient(135deg, #0f172a, #0369a1)' },
    { id: 3, title: 'إجراءات الطلاق الشقاق', date: 'منذ أسبوعين', views: '8K', likes: '890', thumbnail: 'linear-gradient(135deg, #052e16, #15803d)' },
  ];

  const appointments = [
    { id: 1, clientName: 'محمد أمين', type: 'استشارة قانون العمل', date: 'اليوم، 14:00', status: 'مؤكد', statusType: 'success' },
    { id: 2, clientName: 'سارة العلمي', type: 'تأسيس شركة SARL', date: 'غداً، 11:00', status: 'قيد الانتظار', statusType: 'warning' },
    { id: 3, clientName: 'ياسين بنعلي', type: 'استشارة عقارية', date: 'الخميس، 16:30', status: 'مؤكد', statusType: 'success' },
  ];

  return (
    <div className="lawyer-db-wrapper" dir="rtl">
      
      {/* Header Area */}
      <header className="ldb-header animate-fade-in">
        <div className="ldb-welcome">
          <h1>مرحباً، <span className="text-primary">ذ. عمر الحسيني</span> 👋</h1>
          <p>إليك ملخص أداء محتواك ومواعيدك اليوم.</p>
        </div>
        <div className="ldb-actions">
           <button className="ldb-upload-btn">
              <Plus size={18} /> رفع فيديو جديد
           </button>
        </div>
      </header>

      {/* Stats Section */}
      <section className="ldb-stats-section animate-slide-up">
        {stats.map(stat => (
          <div key={stat.id} className="ldb-stat-card">
            <div className="stat-card-top">
              <div className="stat-icon-wrapper" style={{ backgroundColor: `${stat.color}15`, color: stat.color }}>
                {stat.icon}
              </div>
              <div className="stat-trend"><TrendingUp size={12} /> +8%</div>
            </div>
            <div className="stat-card-body">
              <h3 className="stat-val">{stat.value}</h3>
              <p className="stat-name">{stat.label}</p>
            </div>
          </div>
        ))}
      </section>

      {/* Main Grid */}
      <div className="ldb-main-grid animate-slide-up-delay">
        
        {/* Videos Management */}
        <div className="ldb-card animate-slide-up">
          <div className="ldb-card-header">
            <div>
              <h3>المحتوى الخاص بك (Reels)</h3>
              <p>تتبع أداء الفيديوهات التي قمت بنشرها</p>
            </div>
          </div>
          <div className="ldb-video-list">
            {videos.map((vid) => (
              <div key={vid.id} className="ldb-video-item">
                <div className="video-thumb" style={{ background: vid.thumbnail }}>
                  <Video size={20} color="rgba(255,255,255,0.7)" />
                </div>
                <div className="video-info">
                   <h4>{vid.title}</h4>
                   <span className="video-date">{vid.date}</span>
                </div>
                <div className="video-metrics">
                   <span className="metric"><Eye size={14} /> {vid.views}</span>
                   <span className="metric"><Heart size={14} /> {vid.likes}</span>
                </div>
                <button className="video-more-btn"><MoreVertical size={18} /></button>
              </div>
            ))}
          </div>
          <button className="ldb-view-all">عرض جميع الفيديوهات</button>
        </div>

        {/* Upcoming Appointments */}
        <div className="ldb-card animate-slide-up">
          <div className="ldb-card-header">
            <div>
              <h3>المواعيد القادمة</h3>
              <p>جدول الاستشارات مع العملاء</p>
            </div>
          </div>
          <div className="ldb-list">
            {appointments.map(app => (
              <div key={app.id} className="ldb-list-item">
                <div className="item-main">
                   <div className="item-avatar">{app.clientName.charAt(0)}</div>
                   <div className="item-text">
                      <h4>{app.clientName}</h4>
                      <div className="item-meta">
                        <span>{app.type}</span>
                        <span className="meta-dot">•</span>
                        <span>{app.date}</span>
                      </div>
                   </div>
                </div>
                <div className="item-actions">
                  <div className={`status-pill ${app.statusType}`}>{app.status}</div>
                  <button className="join-call-btn" disabled={app.status !== 'مؤكد'}>
                    دخول للمكالمة
                  </button>
                </div>
              </div>
            ))}
          </div>
          <button className="ldb-view-all">جدول المواعيد الكامل</button>
        </div>

      </div>
    </div>
  );
};

export default LawyerDashboard;
