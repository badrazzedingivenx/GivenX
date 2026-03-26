import { useNavigate } from "react-router-dom"
import Footer from "../components/Footer/Footer"
import "./HomePage.css"

function HomePage() {
  const navigate = useNavigate()
  return (
    <>
    <div className="home-page" dir="rtl">
      <section className="hero-split">
        
        {/* Left Side: Mini Chat Preview */}
        <div className="hero-left">
          <div className="mini-chat-box">
            <div className="chat-header">
               <span className="chat-avatar">⚖️</span>
               <span className="chat-title">المساعد القانوني AI</span>
            </div>
            
            <div className="chat-body">
              <div className="preview-msg user">
                <p>واش عندي الحق نفسخ العقد قبل المدة؟</p>
              </div>
              <div className="preview-msg ai">
                <p>
                  نعم، يمكنك فسخ عقد الكراء بإشعار مسبق بشهر واحد بالبريد المضمون وفق القانون 67.12...
                </p>
              </div>
            </div>
            
            <div className="chat-footer">
              <div className="mock-input">
                <button className="mock-send">→</button>
                <span className="mock-placeholder">اكتب سؤالك القانوني...</span>
              </div>
            </div>
          </div>
        </div>

        {/* Right Side: Content Area */}
        <div className="hero-right">
          <div className="content-wrapper">
            <div className="top-badge">
               <span className="badge-text">المنصة القانونية الأولى بالمغرب</span>
               <span className="badge-flag">🇲🇦</span>
            </div>
            
            <h1 className="main-title">
              حقك القانوني <br /> 
              <span className="gold-text">في متناول يدك</span>
            </h1>
            
            <p className="main-subtitle">
              مساعد AI بالدارجة، عقود احترافية، محامون معتمدون، وفيديوهات قانونية تعليمية — كل ما تحتاجه في مكان واحد.
            </p>
            
            <div className="cta-buttons">
              <button className="btn-gold" onClick={() => navigate('/ai')}>جرب المساعد مجاناً</button>
              <button className="btn-outline" onClick={() => navigate('/culture')}>ثقافة قانونية 🎬</button>
            </div>
            
            <div className="stats-row">
              <div className="stat-item">
                <span className="stat-value">99 MAD</span>
                <span className="stat-label">ابتداءً من</span>
              </div>
              <div className="stat-item">
                <span className="stat-value">+36M</span>
                <span className="stat-label">مغربي</span>
              </div>
              <div className="stat-item">
                <span className="stat-value">12K+</span>
                <span className="stat-label">محامٍ معتمد</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Services Section */}
      <section className="services-section">
        <div className="services-header">
          <span className="section-label">الخدمات</span>
          <h2 className="section-title">كل ما تحتاجه قانونياً</h2>
          <p className="section-subtitle">
            منصة متكاملة تجمع الذكاء الاصطناعي والخبرة البشرية والمحتوى التعليمي في تجربة واحدة.
          </p>
        </div>

        <div className="services-grid">
          {/* Card 1: AI Assistant */}
          <div className="service-card">
            <div className="service-icon">🤖</div>
            <h3 className="service-title">مساعد AI قانوني</h3>
            <p className="service-desc">
              أسئلة قانونية بالدارجة والعربية مبنية على القانون المغربي بإجابات فورية.
            </p>
            <span className="service-tag free">مجاني للبدء</span>
          </div>

          {/* Card 2: Smart Contracts */}
          <div className="service-card">
            <div className="service-icon">📄</div>
            <h3 className="service-title">توليد العقود الذكية</h3>
            <p className="service-desc">
              عقود إيجار وعمل وشراكة في دقائق — تُراجع من محامٍ معتمد قبل التسليم.
            </p>
            <span className="service-tag price">من 49 MAD</span>
          </div>

          {/* Card 3: Lawyers Network */}
          <div className="service-card">
            <div className="service-icon">👨‍💼</div>
            <h3 className="service-title">شبكة المحامين</h3>
            <p className="service-desc">
              محامون معتمدون، استشارة فيديو في 24 ساعة، أسعار شفافة بدون مفاجآت.
            </p>
            <span className="service-tag response">24h استجابة</span>
          </div>

          {/* Card 4: Legal Culture */}
          <div className="service-card">
            <div className="service-icon">🎬</div>
            <h3 className="service-title">ثقافة قانونية</h3>
            <p className="service-desc">
              فيديوهات تعليمية من محامين حقيقيين — تابع واحجز مباشرة من الفيديو.
            </p>
            <span className="service-tag new">جديد 🔥</span>
          </div>

          {/* Card 5: B2B Business Packages */}
          <div className="service-card">
            <div className="service-icon">🏢</div>
            <h3 className="service-title">باقات المقاولات B2B</h3>
            <p className="service-desc">
              حلول للمقاول الذاتي والـ TPE: عقود غير محدودة واستشارات شهرية ثابتة.
            </p>
            <span className="service-tag b2b">B2B</span>
          </div>

          {/* Card 6: Electronic Signature */}
          <div className="service-card">
            <div className="service-icon">✍️</div>
            <h3 className="service-title">التوقيع الإلكتروني</h3>
            <p className="service-desc">
              وقع وثائقك وفق القانون 53.05 مع تخزين آمن ومشفر.
            </p>
            <span className="service-tag soon">قريباً</span>
          </div>
        </div>
      </section>

      {/* Pricing Section */}
      <section className="pricing-section">
        <div className="pricing-header">
          <span className="section-label dark">الأسعار</span>
          <h2 className="section-title">شفاف بدون مفاجآت</h2>
          <p className="section-subtitle">
            ابدأ مجاناً واشترك عندما تجد القيمة الحقيقية.
          </p>
        </div>

        <div className="pricing-grid" dir="rtl">
          {/* Plan: Free */}
          <div className="pricing-card side">
            <div className="card-header">
              <span className="plan-name">مجاني</span>
              <div className="plan-price">
                <span className="price-value">0</span>
                <span className="price-currency">MAD / شهر</span>
              </div>
            </div>
            <ul className="feature-list">
              <li><span className="check">✓</span> 3 أسئلة AI / شهر</li>
              <li><span className="check">✓</span> قالب عقد مجاني</li>
              <li><span className="check">✓</span> مشاهدة الفيديوهات</li>
              <li><span className="cross">✕</span> استشارات المحامين</li>
            </ul>
            <button className="btn-pricing-outline">ابدأ مجاناً</button>
          </div>

          {/* Plan: Premium (Featured) */}
          <div className="pricing-card featured">
            <div className="featured-badge">الأكثر شيوعاً</div>
            <div className="card-header">
              <span className="plan-name">بريميوم</span>
              <div className="plan-price">
                <span className="price-value">149</span>
                <span className="price-currency">MAD / شهر</span>
              </div>
            </div>
            <div className="card-divider"></div>
            <ul className="feature-list">
              <li><span className="check">✓</span> AI غير محدود</li>
              <li><span className="check">✓</span> 10 عقود / شهر</li>
              <li><span className="check">✓</span> متابعة محامين + حجز</li>
              <li><span className="check">✓</span> تخزين الوثائق</li>
            </ul>
            <button className="btn-pricing-gold">اشترك الآن</button>
          </div>

          {/* Plan: B2B */}
          <div className="pricing-card side">
            <div className="card-header">
              <span className="plan-name">مقاولات B2B</span>
              <div className="plan-price">
                <span className="price-value">499</span>
                <span className="price-currency">MAD / شهر</span>
              </div>
            </div>
            <ul className="feature-list">
              <li><span className="check">✓</span> عقود غير محدودة</li>
              <li><span className="check">✓</span> استشارتان مشمولتان</li>
              <li><span className="check">✓</span> مدير حساب مخصص</li>
            </ul>
            <button className="btn-pricing-outline">تواصل معنا</button>
          </div>
        </div>
      </section>
    </div>
    <Footer />
  </>
  )
}

export default HomePage
