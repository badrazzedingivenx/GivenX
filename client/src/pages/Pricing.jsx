import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Check, Shield, Zap, Crown, ChevronRight } from 'lucide-react';
import './Pricing.css';

const Pricing = () => {
  const navigate = useNavigate();

  const plans = [
    {
      name: 'الخطة المجانية',
      price: '0',
      period: '/ شهرياً',
      desc: 'مثالية للأفراد الذين يحتاجون لاستشارات بسيطة من حين لآخر.',
      features: [
        '15 سؤال للمساعد AI شهرياً',
        'توليد عقد واحد (نموذج أساسي)',
        'تصفح المحامين والمراجعات',
        'تخزين محدود للمستندات',
      ],
      btnText: 'خطتك الحالية',
      highlight: false,
      isCurrent: true
    },
    {
      name: 'باقة المحترف (Pro)',
      price: '149',
      period: '/ شهرياً',
      desc: 'الخيار الأفضل للأفراد والمهنيين الذين يحتاجون دعماً قانونياً دائماً.',
      features: [
        'أسئلة غير محدودة للمساعد AI',
        'توليد عقود غير محدودة (نماذج متقدمة)',
        'أولوية التواصل مع المحامين',
        'تخزين سحابي مشفر وآمن',
        'دعم فني مخصص 24/7',
        'تنبيهات قانونية ذكية'
      ],
      btnText: 'ابدأ التجربة المجانية',
      highlight: true,
      isCurrent: false
    },
    {
      name: 'باقة الشركات',
      price: '599',
      period: '/ شهرياً',
      desc: 'حلول قانونية متكاملة للشركات والمنظمات الناشئة.',
      features: [
        'كل مميزات باقة المحترف',
        'حسابات متعددة للفريق',
        'صياغة عقود مخصصة لشركتك',
        'مراجعة قانونية سنوية شاملة',
        'استشارات قانونية مباشرة شهرياً',
        'إدارة كاملة للمستندات القانونية'
      ],
      btnText: 'تواصل معنا',
      highlight: false,
      isCurrent: false
    }
  ];

  return (
    <div className="pricing-page-wrapper" dir="rtl">
      <div className="pricing-header">
        <button className="back-btn" onClick={() => navigate('/dashboard')}>
           <ChevronRight size={20} /> العودة للوحة التحكم
        </button>
        <h1>اختر الخطة <span className="highlight">المناسبة</span> لاحتياجاتك</h1>
        <p>استثمر في حمايتك القانونية مع باقات حقي المرنة والقابلة للإلغاء في أي وقت.</p>
      </div>

      <div className="plans-container">
        {plans.map((plan, index) => (
          <div key={index} className={`plan-card ${plan.highlight ? 'highlighted' : ''}`}>
            {plan.highlight && <div className="popular-badge">الأكثر شعبية</div>}
            <div className="plan-header">
              <h3>{plan.name}</h3>
              <div className="plan-price">
                <span className="currency">MAD</span>
                <span className="amount">{plan.price}</span>
                <span className="period">{plan.period}</span>
              </div>
              <p className="plan-desc">{plan.desc}</p>
            </div>

            <div className="plan-features">
              {plan.features.map((feature, idx) => (
                <div key={idx} className="feature-item">
                  <div className="check-icon"><Check size={14} /></div>
                  <span>{feature}</span>
                </div>
              ))}
            </div>

            <button className={`plan-btn ${plan.highlight ? 'btn-gold' : ''} ${plan.isCurrent ? 'btn-disabled' : ''}`}>
              {plan.btnText}
            </button>
          </div>
        ))}
      </div>

      <div className="pricing-footer">
        <div className="trust-badges">
          <div className="badge-item">
             <Shield size={24} />
             <span>دفع آمن ومضمون</span>
          </div>
          <div className="badge-item">
             <Zap size={24} />
             <span>تفعيل فوري للميزات</span>
          </div>
          <div className="badge-item">
             <Crown size={24} />
             <span>ضمان استرداد الأموال</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Pricing;
