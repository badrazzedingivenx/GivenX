import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  User, 
  Shield, 
  Bell, 
  CreditCard, 
  ChevronRight, 
  Mail, 
  Phone, 
  Camera,
  LogOut
} from 'lucide-react';
import './UserSettings.css';

const UserSettings = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('profile');

  const tabs = [
    { id: 'profile', label: 'الملف الشخصي', icon: <User size={20} /> },
    { id: 'security', label: 'الأمان', icon: <Shield size={20} /> },
    { id: 'notifications', label: 'التنبيهات', icon: <Bell size={20} /> },
    { id: 'billing', label: 'الاشتراك والدفع', icon: <CreditCard size={20} /> },
  ];

  return (
    <div className="settings-page-container" dir="rtl">
      <div className="settings-header">
        <button className="back-btn" onClick={() => navigate('/dashboard')}>
          <ChevronRight size={20} /> العودة للوحة التحكم
        </button>
        <h1>إعدادات الحساب</h1>
        <p>إدارة معلوماتك الشخصية، الأمان، وتفضيلات الاشتراك.</p>
      </div>

      <div className="settings-content-wrapper">
        <aside className="settings-sidebar">
          {tabs.map(tab => (
            <button 
              key={tab.id} 
              className={`tab-btn ${activeTab === tab.id ? 'active' : ''}`}
              onClick={() => setActiveTab(tab.id)}
            >
              {tab.icon}
              <span>{tab.label}</span>
            </button>
          ))}
          <div className="sidebar-divider"></div>
          <button className="tab-btn logout-btn" onClick={() => navigate('/login')}>
            <LogOut size={20} />
            <span>تسجيل الخروج</span>
          </button>
        </aside>

        <main className="settings-main-panel">
          {activeTab === 'profile' && (
            <div className="settings-card">
              <h3>المعلومات الشخصية</h3>
              <div className="profile-upload">
                <div className="avatar-preview">م</div>
                <button className="upload-btn"><Camera size={16} /> تغيير الصورة</button>
              </div>
              
              <div className="form-grid">
                <div className="form-group">
                  <label>الاسم الكامل</label>
                  <div className="input-with-icon">
                    <User size={18} />
                    <input type="text" defaultValue="محمد بن علي" />
                  </div>
                </div>
                <div className="form-group">
                  <label>البريد الإلكتروني</label>
                  <div className="input-with-icon">
                    <Mail size={18} />
                    <input type="email" defaultValue="m.benali@example.com" />
                  </div>
                </div>
                <div className="form-group">
                  <label>رقم الهاتف</label>
                  <div className="input-with-icon">
                    <Phone size={18} />
                    <input type="tel" defaultValue="+212 600-000000" />
                  </div>
                </div>
              </div>
              <button className="btn-save">حفظ التغييرات</button>
            </div>
          )}

          {activeTab === 'billing' && (
            <div className="settings-card">
              <h3>حالة الاشتراك</h3>
              <div className="current-plan-banner">
                <div className="plan-info">
                  <h4>باقة المحترف (Pro)</h4>
                  <p>تجديد تلقائي في 15 مارس 2025</p>
                </div>
                <span className="plan-price">149 MAD/شهر</span>
              </div>
              <div className="billing-actions">
                <button className="btn-secondary" onClick={() => navigate('/pricing')}>تغيير الباقة</button>
                <button className="btn-outline-danger">إلغاء الاشتراك</button>
              </div>
            </div>
          )}

          {(activeTab === 'security' || activeTab === 'notifications') && (
            <div className="settings-placeholder">
              <h3>قريباً</h3>
              <p>هذه الإعدادات ستكون متاحة في التحديث القادم.</p>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default UserSettings;
