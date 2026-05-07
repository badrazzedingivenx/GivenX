import React from 'react';
import { Wallet, TrendingUp, ArrowDownCircle, ArrowUpCircle, Download, Calendar, DollarSign } from 'lucide-react';
import './LawyerEarnings.css';

const LawyerEarnings = () => {
  const [view, setView] = React.useState('أسبوعي');

  const weeklyData = [
    { label: 'إثن', height: '40%' },
    { label: 'ثلا', height: '60%' },
    { label: 'أرب', height: '30%' },
    { label: 'خمي', height: '85%', active: true },
    { label: 'جمع', height: '50%' },
    { label: 'سبت', height: '70%' },
    { label: 'أحد', height: '45%' },
  ];

  const monthlyData = [
    { label: 'يناير', height: '30%' },
    { label: 'فبراير', height: '45%' },
    { label: 'مارس', height: '60%' },
    { label: 'أبريل', height: '80%', active: true },
    { label: 'ماي', height: '20%' },
    { label: 'يونيو', height: '0%' },
  ];

  const currentData = view === 'أسبوعي' ? weeklyData : monthlyData;

  const transactions = [
    { id: 1, type: 'استشارة', client: 'محمد أمين', amount: '+450 د.م', date: '02 ماي 2024', status: 'مكتمل' },
    { id: 2, type: 'استشارة', client: 'ياسين بنعلي', amount: '+300 د.م', date: '01 ماي 2024', status: 'مكتمل' },
    { id: 3, type: 'سحب', client: 'حساب بنكي (****1234)', amount: '-2,000 د.م', date: '28 أبريل 2024', status: 'قيد المعالجة' },
    { id: 4, type: 'استشارة', client: 'سمية أوحمو', amount: '+500 د.م', date: '25 أبريل 2024', status: 'مكتمل' },
    { id: 5, type: 'استشارة', client: 'كريم التازي', amount: '+600 د.م', date: '20 أبريل 2024', status: 'مكتمل' },
  ];

  return (
    <div className="le-wrapper animate-fade-in" dir="rtl">
      <div className="le-header">
        <div>
          <h1>الأرباح والمحفظة</h1>
          <p>تتبع مداخيلك من الاستشارات القانونية وقم بسحب أرباحك</p>
        </div>
        <button className="le-withdraw-btn">
          <ArrowDownCircle size={18} /> طلب سحب الأرباح
        </button>
      </div>

      <div className="le-main-stats">
        <div className="le-stat-card primary">
          <div className="stat-card-info">
            <span className="stat-label">الرصيد المتاح</span>
            <h2 className="stat-value">3,240.00 د.م</h2>
            <div className="stat-badge"><TrendingUp size={12} /> +12.5% عن الشهر الماضي</div>
          </div>
          <div className="stat-icon-bg">
            <Wallet size={48} />
          </div>
        </div>

        <div className="le-secondary-stats">
          <div className="le-small-stat">
            <div className="small-stat-icon green"><ArrowUpCircle size={20} /></div>
            <div>
              <span className="small-label">إجمالي الأرباح</span>
              <span className="small-value">14,500 د.م</span>
            </div>
          </div>
          <div className="le-small-stat">
            <div className="small-stat-icon blue"><Calendar size={20} /></div>
            <div>
              <span className="small-label">أرباح هذا الشهر</span>
              <span className="small-value">4,200 د.م</span>
            </div>
          </div>
        </div>
      </div>

      <div className="le-content-grid">
        <div className="le-chart-card">
          <div className="card-header">
            <h3>مخطط الأرباح ({view === 'أسبوعي' ? 'آخر 7 أيام' : 'هذه السنة'})</h3>
            <select className="le-select" value={view} onChange={(e) => setView(e.target.value)}>
              <option value="أسبوعي">أسبوعي</option>
              <option value="شهري">شهري</option>
            </select>
          </div>
          <div className="le-mock-chart">
            {currentData.map((d, i) => (
              <div key={i} className={`chart-bar ${d.active ? 'active' : ''}`} style={{ height: d.height }}>
                <span className="bar-label">{d.label}</span>
              </div>
            ))}
          </div>
        </div>


        <div className="le-history-card">
          <div className="card-header">
            <h3>سجل العمليات</h3>
            <button className="le-export-btn"><Download size={16} /> تصدير PDF</button>
          </div>
          <div className="le-transactions">
            {transactions.map(t => (
              <div key={t.id} className="le-transaction-item">
                <div className={`t-icon ${t.amount.startsWith('+') ? 'income' : 'outcome'}`}>
                  {t.amount.startsWith('+') ? <ArrowUpCircle size={18} /> : <ArrowDownCircle size={18} />}
                </div>
                <div className="t-info">
                  <h4>{t.client}</h4>
                  <span>{t.type} • {t.date}</span>
                </div>
                <div className="t-amount-box">
                  <span className={`t-amount ${t.amount.startsWith('+') ? 'positive' : 'negative'}`}>
                    {t.amount}
                  </span>
                  <span className="t-status">{t.status}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default LawyerEarnings;
