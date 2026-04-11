import React from 'react';
import './Dashboard.css';

const Dashboard = () => {
  // Mock Data
  const stats = [
    { id: 4, label: 'فيديوهات', value: '8', icon: '🎬', iconBg: 'bg-indigo-50' },
    { id: 3, label: 'استشارات', value: '1', icon: '👤', iconBg: 'bg-orange-50' },
    { id: 2, label: 'أسئلة AI', value: '12', icon: '💬', iconBg: 'bg-purple-50' },
    { id: 1, label: 'عقود مولدة', value: '3', icon: '📄', iconBg: 'bg-gray-50' },
  ];

  const contracts = [
    { id: 1, title: 'عقد الكراء - درب عمر', date: '2 مارس 2025', status: 'مكتمل', statusBg: 'bg-[#e7f5ed]', statusColor: 'text-[#1a4d2e]', icon: '🏠' },
    { id: 2, title: 'عقد عمل - مطور ويب', date: '28 فبراير 2025', status: 'مراجعة', statusBg: 'bg-[#fcf3e8]', statusColor: 'text-[#9c6d2e]', icon: '💼' },
  ];

  const consultations = [
    { id: 1, name: 'ذ. يوسف البقالي', date: 'الخميس 13 مارس، 14:00', status: 'مؤكد', statusBg: 'bg-[#e7f5ed]', statusColor: 'text-[#1a4d2e]' },
    { id: 2, name: 'ذت. فاطمة الزهراء', date: '23 فبراير - منتهية', status: 'منتهية', statusBg: 'bg-gray-100', statusColor: 'text-gray-400' },
  ];

  const savedVideos = [
    { id: 1, title: 'كيفاش تأسس شركة في المغرب - عمر الحسني', time: 'محفوظ منذ يوم' },
    { id: 2, title: 'حقوق الطرد التعسفي - يوسف البقالي', time: 'محفوظ منذ 3 أيام' },
  ];

  return (
    <div className="dashboard-container" dir="rtl">
      <div className="dashboard-content">
        {/* Greeting Section */}
        <div className="greeting-section">
          <h1 className="greeting-title">
            مرحباً، محمد <span>👋</span>
          </h1>
          <p className="greeting-subtitle">ملخص حسابك القانوني - السبت 8 مارس 2025</p>
        </div>

        {/* Stats Row */}
        <div className="stats-grid">
          {stats.map((stat) => (
            <div key={stat.id} className="stat-card">
              <div className={`stat-icon-box ${stat.iconBg}`}>
                {stat.icon}
              </div>
              <span className="stat-number">{stat.value}</span>
              <span className="stat-label">{stat.label}</span>
            </div>
          ))}
        </div>

        {/* Main Grid */}
        <div className="main-dashboard-grid">
          
          {/* Right Side - Top: Contracts, Bottom: Videos */}
          <div className="column-stack">
            {/* Contracts Card */}
            <div className="dashboard-card h-full">
              <div className="card-top-bar">
                <h2 className="card-heading">
                  <span>📋</span>
                  عقودي
                </h2>
                <button className="card-action-btn">
                  <span className="ml-1 text-lg">+</span> جديد
                </button>
              </div>
              <div className="list-content">
                {contracts.map((contract) => (
                  <div key={contract.id} className="list-item">
                    <div className="item-info">
                      <div className="item-icon">
                        {contract.icon}
                      </div>
                      <div>
                        <h3 className="item-title">{contract.title}</h3>
                        <p className="item-subtitle">{contract.date}</p>
                      </div>
                    </div>
                    <span className={`status-badge ${contract.statusBg} ${contract.statusColor}`}>
                      {contract.status}
                    </span>
                  </div>
                ))}
              </div>
            </div>

            {/* Saved Videos Card */}
            <div className="dashboard-card min-h-[300px]">
              <div className="card-top-bar">
                <h2 className="card-heading">
                  <span>🎬</span>
                  فيديوهات حفظتها
                </h2>
                <button className="card-action-btn">عرض الكل</button>
              </div>
              <div className="video-list">
                {savedVideos.map((video) => (
                  <div key={video.id} className="video-item">
                    <div className="video-dot"></div>
                    <h3 className="video-title">{video.title}</h3>
                    <p className="video-time">{video.time}</p>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Left Side - Top: Consultations, Bottom: Subscription */}
          <div className="column-stack">
            {/* Consultations Card */}
            <div className="dashboard-card h-full">
              <div className="card-top-bar">
                <h2 className="card-heading">
                  <span>📅</span>
                  استشاراتي
                </h2>
                <button className="card-action-btn">
                  <span className="ml-1 text-lg">+</span> حجز
                </button>
              </div>
              <div className="list-content">
                {consultations.map((consultation) => (
                  <div key={consultation.id} className="list-item">
                    <div className="item-info">
                      <div className="item-icon">
                        👤
                      </div>
                      <div>
                        <h3 className="item-title">{consultation.name}</h3>
                        <p className="item-subtitle">{consultation.date}</p>
                      </div>
                    </div>
                    <span className={`status-badge ${consultation.statusBg} ${consultation.statusColor}`}>
                      {consultation.status}
                    </span>
                  </div>
                ))}
              </div>
            </div>

            {/* Subscription Card */}
            <div className="subscription-section">
              <div className="sub-header">
                <span>💳</span>
                <h2 className="sub-title">اشتراكي</h2>
              </div>
              
              <div className="premium-banner">
                <div className="banner-content">
                  <div className="banner-row">
                    <div>
                      <p className="plan-label">الخطة الحالية</p>
                      <h3 className="plan-name">مجاني</h3>
                    </div>
                    <p className="plan-info">سؤالان AI متبقيان هذا الشهر</p>
                  </div>
                  <div className="progress-container">
                    <div className="progress-track">
                      <div className="progress-fill" style={{ width: '40%' }}></div>
                    </div>
                  </div>
                </div>
                <div className="banner-decoration"></div>
              </div>

              <button className="upgrade-btn">
                ترقية إلى البريميوم — 149 MAD <span>🚀</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
