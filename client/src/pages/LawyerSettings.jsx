import React from 'react';
import { User, Shield, Bell, CreditCard, ExternalLink, Camera, Save } from 'lucide-react';
import './LawyerSettings.css';

const LawyerSettings = () => {
  const [activeTab, setActiveTab] = React.useState('profile');

  const renderContent = () => {
    switch (activeTab) {
      case 'profile':
        return (
          <>
            <section className="ls-section">
              <h3 className="section-title">الصورة الشخصية</h3>
              <div className="ls-avatar-edit">
                <div className="ls-avatar-big">ع</div>
                <div className="ls-avatar-actions">
                   <button className="ls-btn-outline"><Camera size={16} /> تغيير الصورة</button>
                   <span className="ls-hint">JPG، PNG بحد أقصى 2MB</span>
                </div>
              </div>
            </section>

            <section className="ls-section">
              <h3 className="section-title">المعلومات المهنية</h3>
              <div className="ls-form-grid">
                <div className="ls-form-group">
                  <label>الاسم الكامل</label>
                  <input type="text" defaultValue="عمر الحسيني" />
                </div>
                <div className="ls-form-group">
                  <label>التخصص الرئيسي</label>
                  <select defaultValue="الأعمال">
                    <option value="الأعمال">قانون الأعمال</option>
                    <option value="الأسرة">مدونة الأسرة</option>
                    <option value="الجنائي">القانون الجنائي</option>
                  </select>
                </div>
                <div className="ls-form-group">
                  <label>المدينة</label>
                  <input type="text" defaultValue="الدار البيضاء" />
                </div>
                <div className="ls-form-group">
                  <label>سعر الاستشارة (للساعة)</label>
                  <div className="input-with-label">
                    <input type="number" defaultValue="500" />
                    <span>MAD</span>
                  </div>
                </div>
                <div className="ls-form-group full-width">
                  <label>نبذة تعريفية</label>
                  <textarea rows="4" defaultValue="محامٍ متخصص في قضايا الشركات والعقود التجارية مع خبرة تزيد عن 10 سنوات في المحاكم المغربية."></textarea>
                </div>
              </div>
            </section>
          </>
        );
      case 'security':
        return (
          <section className="ls-section">
            <h3 className="section-title">تغيير كلمة المرور</h3>
            <div className="ls-form-grid">
              <div className="ls-form-group full-width">
                <label>كلمة المرور الحالية</label>
                <input type="password" placeholder="••••••••" />
              </div>
              <div className="ls-form-group">
                <label>كلمة المرور الجديدة</label>
                <input type="password" placeholder="••••••••" />
              </div>
              <div className="ls-form-group">
                <label>تأكيد كلمة المرور الجديدة</label>
                <input type="password" placeholder="••••••••" />
              </div>
            </div>
            <div style={{ marginTop: '32px' }}>
               <h3 className="section-title">المصادقة الثنائية (2FA)</h3>
               <p style={{ fontSize: '14px', color: 'var(--text-muted)', marginBottom: '16px' }}>قم بتأمين حسابك بشكل أقوى عبر تفعيل خاصية التحقق عبر الهاتف.</p>
               <button className="ls-btn-outline">تفعيل الآن</button>
            </div>
          </section>
        );
      case 'notifications':
        return (
          <section className="ls-section">
            <h3 className="section-title">تفضيلات التنبيهات</h3>
            <div className="ls-notif-list">
              <div className="ls-notif-item">
                <div>
                  <h4>تنبيهات البريد الإلكتروني</h4>
                  <p>استلام ملخص يومي للمواعيد والرسائل</p>
                </div>
                <input type="checkbox" defaultChecked />
              </div>
              <div className="ls-notif-item">
                <div>
                  <h4>إشعارات المتصفح</h4>
                  <p>تنبيهات فورية عند وصول رسالة جديدة أو موعد</p>
                </div>
                <input type="checkbox" defaultChecked />
              </div>
              <div className="ls-notif-item">
                <div>
                  <h4>تنبيهات الرسائل القصيرة (SMS)</h4>
                  <p>تذكير قبل الموعد بـ 30 دقيقة</p>
                </div>
                <input type="checkbox" />
              </div>
            </div>
          </section>
        );
      case 'payment':
        return (
          <section className="ls-section">
            <h3 className="section-title">طرق سحب الأرباح</h3>
            <div className="ls-card-item active">
               <div className="ls-card-info">
                 <CreditCard size={24} />
                 <div>
                   <h4>حساب بنكي (CIH Bank)</h4>
                   <p>رقم الحساب: **** **** **** 1234</p>
                 </div>
               </div>
               <span className="ls-tag-success">أساسي</span>
            </div>
            <button className="ls-btn-outline" style={{ width: '100%', marginTop: '20px' }}>إضافة طريقة سحب جديدة</button>
            
            <div style={{ marginTop: '40px' }}>
              <h3 className="section-title">الحد الأدنى للسحب</h3>
              <div className="ls-progress-box">
                 <div className="progress-info">
                   <span>240 د.م من 500 د.م</span>
                   <span>48%</span>
                 </div>
                 <div className="progress-bar-bg">
                    <div className="progress-bar-fill" style={{ width: '48%' }}></div>
                 </div>
              </div>
            </div>
          </section>
        );
      default:
        return null;
    }
  };

  return (
    <div className="ls-wrapper animate-fade-in" dir="rtl">
      <div className="ls-header">
        <h1>الإعدادات</h1>
        <p>إدارة ملفك الشخصي، التخصصات، وأسعار الخدمات</p>
      </div>

      <div className="ls-container">
        {/* Settings Nav */}
        <div className="ls-nav">
          <button 
            className={`ls-nav-item ${activeTab === 'profile' ? 'active' : ''}`}
            onClick={() => setActiveTab('profile')}
          >
            <User size={18} /> الحساب الشخصي
          </button>
          <button 
            className={`ls-nav-item ${activeTab === 'security' ? 'active' : ''}`}
            onClick={() => setActiveTab('security')}
          >
            <Shield size={18} /> الأمان
          </button>
          <button 
            className={`ls-nav-item ${activeTab === 'notifications' ? 'active' : ''}`}
            onClick={() => setActiveTab('notifications')}
          >
            <Bell size={18} /> التنبيهات
          </button>
          <button 
            className={`ls-nav-item ${activeTab === 'payment' ? 'active' : ''}`}
            onClick={() => setActiveTab('payment')}
          >
            <CreditCard size={18} /> الدفع والسحب
          </button>
        </div>

        {/* Settings Content */}
        <div className="ls-content">
          {renderContent()}

          <div className="ls-footer">
            <button className="ls-save-btn"><Save size={18} /> حفظ التغييرات</button>
          </div>
        </div>
      </div>
    </div>
  );
};


export default LawyerSettings;
