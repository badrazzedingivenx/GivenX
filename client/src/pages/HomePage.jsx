import { useNavigate } from "react-router-dom"
import { 
  Bot, 
  FileText, 
  Users, 
  Film, 
  CheckCircle2, 
  ArrowRight,
  Video
} from "lucide-react"
import Footer from "../components/Footer/Footer"
import "./HomePage.css"

function HomePage() {
  const navigate = useNavigate()
  
  return (
    <div className="home-container" dir="rtl">

      {/* ── HERO ── */}
      <section className="hero-modern">

        {/* LEFT — Text content */}
        <div className="hero-content">
          <div className="hero-badge">
            <span className="badge-icon">🇲🇦</span>
            <span className="badge-text">أول منصة قانونية ذكية في المغرب</span>
          </div>

          <h1 className="hero-title">
            حلول قانونية ذكية<br />
            <span className="text-gradient">بين يديك وفي كل لحظة</span>
          </h1>

          <p className="hero-subtitle">
            نجمع بين قوة الذكاء الاصطناعي وخبرة أفضل المحامين لنمنحك
            تجربة قانونية سريعة، موثوقة، وبأسعار شفافة.
          </p>

          <div className="hero-actions">
            <button className="cta-primary" onClick={() => navigate('/ai')}>
              جرب المساعد الذكي مجاناً <ArrowRight size={18} />
            </button>
            <button className="cta-secondary" onClick={() => navigate('/culture')}>
              اكتشف الثقافة القانونية <Film size={18} />
            </button>
          </div>

          <div className="hero-social-proof">
            <div className="avatars">
              <div className="avatar">⚖️</div>
              <div className="avatar">⚖️</div>
              <div className="avatar">⚖️</div>
            </div>
            <p className="proof-text">
              انضم إلى أكثر من <strong>50,000</strong> مستخدم يثقون في حقي
            </p>
          </div>
        </div>

        {/* RIGHT — Visual panel (NO floating/overlapping cards) */}
        <div className="hero-visual">

          {/* Row 1: Two stat cards */}
          <div className="visual-top-row">
            <div className="stat-card">
              <div className="stat-icon">
                <Bot size={18} />
              </div>
              <div className="stat-info">
                <span className="stat-label">المساعد الذكي</span>
                <span className="stat-val">إجابة فورية بالدارجة</span>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon gold">
                <FileText size={18} />
              </div>
              <div className="stat-info">
                <span className="stat-label">العقود</span>
                <span className="stat-val">توليد في دقائق</span>
              </div>
            </div>
          </div>

          {/* Row 2: Chat preview */}
          <div className="main-visual-box">
            <div className="chat-interface">
              <div className="chat-header-mock">
                <div className="status-dot"></div>
                <span>المساعد القانوني الذكي</span>
              </div>

              <div className="chat-body-mock">
                <div className="chat-line user">
                  أهلاً، واش عندي الحق نفسخ عقد كراء؟
                </div>
                <div className="chat-line ai">
                  أهلاً بك. نعم، وفق القانون 67.12 المتعلق بكراء المحلات
                  المخصصة للسكن، يمكنك إنهاء العقد في حالات محددة مثل توجيه
                  إشعار بالإفراغ...
                </div>
                <div className="chat-line user">
                  شنو هي الإجراءات المطلوبة؟
                </div>
              </div>

              <div className="chat-suggestions-mock">
                <span className="suggestion">كيفاش نكتب إشعار؟</span>
                <span className="suggestion">شنو هي مدة الإخطار؟</span>
              </div>

              <div className="chat-input-mock">
                <input
                  type="text"
                  placeholder="اسأل عن أي موضوع قانوني..."
                  readOnly
                />
                <button className="send-btn">
                  <ArrowRight size={16} />
                </button>
              </div>
            </div>
          </div>

        </div>
      </section>

      {/* ── SERVICES ── */}
      <section className="services-modern wrapper">
        <div className="section-header">
          <span className="label">خدماتنا</span>
          <h2 className="title">حلول متكاملة لجميع احتياجاتك</h2>
        </div>

        <div className="grid-container">
          <div className="modern-card" onClick={() => navigate('/ai')}>
            <div className="icon-box"><Bot size={30} /></div>
            <h3>مساعد AI قانوني</h3>
            <p>إجابات دقيقة مبنية على القانون المغربي ومتاحة بالدارجة المغربية لسهولة التواصل.</p>
            <button className="card-link">ابدأ المحادثة <ArrowRight size={15} /></button>
          </div>

          <div className="modern-card" onClick={() => navigate('/contracts')}>
            <div className="icon-box gold"><FileText size={30} /></div>
            <h3>توليد العقود الذكية</h3>
            <p>أنشئ عقودك المهنية والشخصية بضغطة زر مع ضمان مراجعتها من قبل خبراء.</p>
            <button className="card-link">توليد عقد <ArrowRight size={15} /></button>
          </div>

          <div className="modern-card" onClick={() => navigate('/lawyers')}>
            <div className="icon-box"><Users size={30} /></div>
            <h3>شبكة المحامين</h3>
            <p>تواصل مع نخبة من المحامين المعتمدين في جميع التخصصات عبر استشارات فيديو.</p>
            <button className="card-link">احجز استشارة <ArrowRight size={15} /></button>
          </div>

          <div className="modern-card" onClick={() => navigate('/culture')}>
            <div className="icon-box"><Video size={30} /></div>
            <h3>المحتوى التعليمي</h3>
            <p>تبسيط القوانين عبر فيديوهات قصيرة وممتعة تساعدك على معرفة حقوقك وواجباتك.</p>
            <button className="card-link">شاهد الفيديوهات <ArrowRight size={15} /></button>
          </div>
        </div>
      </section>

      {/* ── PRICING ── */}
      <section className="pricing-modern">
        <div className="wrapper">
          <div className="section-header light">
            <h2 className="title">اختر الباقة المناسبة لك</h2>
            <p className="subtitle">أسعار شفافة وبدون التزامات خفية</p>
          </div>

          <div className="pricing-tabs">
            <div className="pricing-card-modern">
              <div className="p-header">
                <h3>مجاني</h3>
                <div className="p-price">0 <span>MAD</span></div>
              </div>
              <ul className="p-features">
                <li><CheckCircle2 size={17} className="check" /> 3 أسئلة AI شهرياً</li>
                <li><CheckCircle2 size={17} className="check" /> تصفح العقود المجانية</li>
                <li><CheckCircle2 size={17} className="check" /> مشاهدة المحتوى التعليمي</li>
              </ul>
              <button className="p-btn">ابدأ الآن</button>
            </div>

            <div className="pricing-card-modern featured">
              <div className="featured-label">الأكثر مبيعاً</div>
              <div className="p-header">
                <h3>بريميوم</h3>
                <div className="p-price">149 <span>MAD</span></div>
              </div>
              <ul className="p-features">
                <li><CheckCircle2 size={17} className="check" /> أسئلة AI غير محدودة</li>
                <li><CheckCircle2 size={17} className="check" /> 10 عقود احترافية شهرياً</li>
                <li><CheckCircle2 size={17} className="check" /> مراجعة قانونية سريعة</li>
                <li><CheckCircle2 size={17} className="check" /> دعم فني مخصص</li>
              </ul>
              <button className="p-btn primary">اشترك الآن</button>
            </div>

            <div className="pricing-card-modern">
              <div className="p-header">
                <h3>للمقاولات</h3>
                <div className="p-price">499 <span>MAD</span></div>
              </div>
              <ul className="p-features">
                <li><CheckCircle2 size={17} className="check" /> استشارات قانونية غير محدودة</li>
                <li><CheckCircle2 size={17} className="check" /> توليد عقود B2B مخصصة</li>
                <li><CheckCircle2 size={17} className="check" /> مدير حساب قانوني مخصص</li>
              </ul>
              <button className="p-btn">احصل على الباقة</button>
            </div>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  )
}

export default HomePage
