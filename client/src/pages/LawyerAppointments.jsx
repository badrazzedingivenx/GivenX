import React from 'react';
import { Calendar, Clock, User, CheckCircle, XCircle, AlertCircle, Search, Filter, MoreHorizontal } from 'lucide-react';
import './LawyerAppointments.css';

const LawyerAppointments = () => {
  const appointments = [
    { id: 1, client: 'محمد أمين', type: 'استشارة قانون العمل', date: 'اليوم، 14:00', status: 'مؤكد', statusClass: 'confirmed', time: '45 دقيقة' },
    { id: 2, client: 'سارة العلمي', type: 'تأسيس شركة SARL', date: 'غداً، 11:00', status: 'قيد الانتظار', statusClass: 'pending', time: '60 دقيقة' },
    { id: 3, client: 'ياسين بنعلي', type: 'استشارة عقارية', date: 'الخميس، 16:30', status: 'مؤكد', statusClass: 'confirmed', time: '30 دقيقة' },
    { id: 4, client: 'ليلى منير', type: 'قانون الأسرة', date: 'الجمعة، 09:30', status: 'ملغى', statusClass: 'cancelled', time: '45 دقيقة' },
    { id: 5, client: 'كريم التازي', type: 'مراجعة عقد تجاري', date: 'السبت، 10:00', status: 'مؤكد', statusClass: 'confirmed', time: '60 دقيقة' },
  ];

  return (
    <div className="la-wrapper animate-fade-in" dir="rtl">
      <div className="la-header">
        <div>
          <h1>المواعيد</h1>
          <p>تتبع مواعيد الاستشارات القانونية مع عملائك</p>
        </div>
        <div className="la-actions">
           <button className="la-calendar-btn">
              <Calendar size={18} /> عرض التقويم
           </button>
        </div>
      </div>

      <div className="la-stats">
        <div className="la-stat-box">
          <span className="stat-label">مواعيد اليوم</span>
          <span className="stat-value">3</span>
        </div>
        <div className="la-stat-box">
          <span className="stat-label">قيد الانتظار</span>
          <span className="stat-value">5</span>
        </div>
        <div className="la-stat-box">
          <span className="stat-label">إجمالي الشهر</span>
          <span className="stat-value">42</span>
        </div>
      </div>

      <div className="la-toolbar">
        <div className="la-search">
          <Search size={18} />
          <input type="text" placeholder="البحث عن موعد أو عميل..." />
        </div>
        <div className="la-filters">
           <button className="filter-pill active">الكل</button>
           <button className="filter-pill">مؤكد</button>
           <button className="filter-pill">قيد الانتظار</button>
        </div>
      </div>

      <div className="la-table-container animate-slide-up">
        <table className="la-table">
          <thead>
            <tr>
              <th>العميل</th>
              <th>نوع الاستشارة</th>
              <th>التوقيت</th>
              <th>المدة</th>
              <th>الحالة</th>
              <th>إجراءات</th>
            </tr>
          </thead>
          <tbody>
            {appointments.map(app => (
              <tr key={app.id}>
                <td>
                  <div className="client-info">
                    <div className="client-avatar">{app.client.charAt(0)}</div>
                    <span>{app.client}</span>
                  </div>
                </td>
                <td>{app.type}</td>
                <td>
                  <div className="time-info">
                    <Calendar size={14} /> {app.date.split('،')[0]}
                    <Clock size={14} style={{ marginRight: '8px' }} /> {app.date.split('،')[1]}
                  </div>
                </td>
                <td>{app.time}</td>
                <td>
                  <span className={`status-badge ${app.statusClass}`}>
                    {app.status === 'مؤكد' && <CheckCircle size={12} />}
                    {app.status === 'قيد الانتظار' && <AlertCircle size={12} />}
                    {app.status === 'ملغى' && <XCircle size={12} />}
                    {app.status}
                  </span>
                </td>
                <td>
                  <button className="action-btn"><MoreHorizontal size={18} /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default LawyerAppointments;
