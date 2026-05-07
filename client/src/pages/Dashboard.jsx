import React from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  FileText, 
  Calendar, 
  MessageSquare, 
  PlayCircle, 
  LogOut, 
  Plus, 
  TrendingUp, 
  Clock, 
  CheckCircle2, 
  ExternalLink,
  Crown,
  Search,
  Bell,
  Settings,
  ShieldCheck
} from 'lucide-react';
import './Dashboard.css';

const Dashboard = () => {
  const navigate = useNavigate();

  // Mock Data
  const stats = [
    { id: 1, label: 'العقود المولدة', value: '03', icon: <FileText size={20} />, color: '#064e3b', trend: '+12%' },
    { id: 2, label: 'أسئلة AI', value: '12', icon: <MessageSquare size={20} />, color: '#b8860b', trend: '+5%' },
    { id: 3, label: 'الاستشارات', value: '01', icon: <Calendar size={20} />, color: '#0369a1', trend: '0%' },
    { id: 4, label: 'الفيديوهات', value: '08', icon: <PlayCircle size={20} />, color: '#7c3aed', trend: '+20%' },
  ];

  const contracts = [
    { id: 1, title: 'عقد كراء - محل تجاري', date: '02 مارس 2025', status: 'مكتمل', statusType: 'success' },
    { id: 2, title: 'اتفاقية عدم الإفصاح', date: '28 فبراير 2025', status: 'قيد المراجعة', statusType: 'warning' },
  ];

  const consultations = [
    { id: 1, name: 'ذ. يوسف البقالي', date: 'الخميس 13 مارس، 14:00', status: 'مؤكد', statusType: 'success' },
    { id: 2, name: 'ذت. فاطمة الزهراء', date: '23 فبراير - منتهية', status: 'منتهية', statusType: 'neutral' },
  ];

  return (
    <div className="db-premium-container" dir="rtl">
      
      {/* Top Navigation Bar */}
      <nav className="db-top-nav">
        <div className="nav-left">
          <div className="search-bar">
            <Search size={18} />
            <input type="text" placeholder="البحث عن وثائق، محامين..." />
          </div>
        </div>
        <div className="nav-right">
          <div className="top-user-info">
            <span className="top-username">محمد</span>
            <div className="top-avatar">م</div>
          </div>
          <div className="nav-divider"></div>
          <button className="nav-icon-btn" onClick={() => navigate('/notifications')}>
            <Bell size={20} /><span className="notif-dot"></span>
          </button>
          <button className="nav-icon-btn" onClick={() => navigate('/settings')}>
            <Settings size={20} />
          </button>
          <button className="logout-minimal" onClick={() => {
            localStorage.removeItem('token');
            navigate('/login');
          }}>
            <LogOut size={18} />
          </button>
        </div>
      </nav>

      <div className="db-content-grid">
        
        {/* Main Content Area */}
        <div className="db-main-area">
          
          <header className="welcome-hero">
            <div className="hero-content">
              <div className="user-profile-badge">
                <div className="avatar-wrapper">
                  <span className="avatar-letter">م</span>
                  <div className="status-indicator"></div>
                </div>
                <div className="badge-text">
                  <h3>مرحباً، محمد</h3>
                  <p>عضو متميز في حقي</p>
                </div>
              </div>
              <div className="hero-message">
                <h1>لديك <span className="highlight-text">3 تنبيهات</span> جديدة بخصوص عقودك</h1>
                <p>تم تحديث حالة "اتفاقية عدم الإفصاح" بالأمس. هل ترغب في مراجعتها الآن؟</p>
                <div className="hero-actions">
                  <button className="btn-primary" onClick={() => navigate('/contracts')}>مراجعة العقود</button>
                  <button className="btn-outline">تجاهل</button>
                </div>
              </div>
            </div>
            <div className="hero-illustration">
              <ShieldCheck size={120} strokeWidth={1} />
            </div>
          </header>

          <section className="stats-grid">
            {stats.map(stat => (
              <div key={stat.id} className="modern-stat-card">
                <div className="stat-icon-bg" style={{ backgroundColor: `${stat.color}15`, color: stat.color }}>
                  {stat.icon}
                </div>
                <div className="stat-info">
                  <span className="stat-label">{stat.label}</span>
                  <div className="stat-row">
                    <h2 className="stat-number">{stat.value}</h2>
                    <span className={`stat-trend-tag ${stat.trend.startsWith('+') ? 'up' : ''}`}>
                      {stat.trend}
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </section>

          <div className="activity-grid">
            <div className="activity-card large">
              <div className="card-header">
                <h3>العقود الأخيرة</h3>
                <button className="text-link" onClick={() => navigate('/contracts')}>عرض الكل</button>
              </div>
              <div className="contract-table">
                {contracts.map(contract => (
                  <div key={contract.id} className="contract-row">
                    <div className="c-info">
                      <div className="c-icon"><FileText size={18} /></div>
                      <div>
                        <h4>{contract.title}</h4>
                        <span className="c-date">{contract.date}</span>
                      </div>
                    </div>
                    <div className={`c-status ${contract.statusType}`}>
                      {contract.status}
                    </div>
                    <button className="c-action-btn"><ExternalLink size={16} /></button>
                  </div>
                ))}
              </div>
            </div>

            <div className="activity-card small">
              <div className="card-header">
                <h3>حالة الاشتراك</h3>
              </div>
              <div className="sub-widget">
                <div className="sub-plan">
                  <div className="plan-icon"><Crown size={24} /></div>
                  <div>
                    <h4>الخطة المجانية</h4>
                    <span className="plan-exp">تنتهي خلال 12 يوم</span>
                  </div>
                </div>
                <div className="usage-stats">
                  <div className="usage-item">
                    <span>رصيد AI</span>
                    <span>12 / 15</span>
                  </div>
                  <div className="progress-bar-small">
                    <div className="progress-fill" style={{ width: '80%' }}></div>
                  </div>
                </div>
                <button className="btn-upgrade" onClick={() => navigate('/pricing')}>ترقية الحساب</button>
              </div>
            </div>
          </div>

        </div>

        {/* Side Widgets */}
        <div className="db-side-widgets">
          <div className="widget-card appointment-widget">
            <h3>المواعيد القادمة</h3>
            <div className="apt-list">
              {consultations.map(consult => (
                <div key={consult.id} className="apt-item">
                  <div className="apt-date-box">
                    <span className="day">13</span>
                    <span className="month">مارس</span>
                  </div>
                  <div className="apt-details">
                    <h4>{consult.name}</h4>
                    <span><Clock size={12} /> 14:00</span>
                  </div>
                </div>
              ))}
            </div>
            <button className="btn-block" onClick={() => navigate('/lawyers')}>حجز موعد جديد</button>
          </div>

          <div className="widget-card quick-actions">
            <h3>روابط سريعة</h3>
            <div className="action-btns">
              <button className="action-chip" onClick={() => navigate('/ai')}><MessageSquare size={16} /> اسأل المساعد</button>
              <button className="action-chip" onClick={() => navigate('/contracts')}><Plus size={16} /> توليد عقد</button>
              <button className="action-chip" onClick={() => navigate('/lawyers')}><Calendar size={16} /> تواصل مع محامٍ</button>
            </div>
          </div>

          <div className="widget-card help-box">
            <div className="help-icon"><CheckCircle2 size={32} /></div>
            <h4>هل تحتاج لمساعدة؟</h4>
            <p>فريق الدعم الفني جاهز للإجابة على استفساراتكم على مدار الساعة.</p>
            <button className="btn-text">تحدث معنا الآن</button>
          </div>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;
