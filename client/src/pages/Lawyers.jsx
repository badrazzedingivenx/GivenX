import { useState } from "react";
import { Search, MapPin, Star, Calendar, Clock, Banknote, ShieldCheck } from "lucide-react";
import "./Lawyers.css";

/* ─── DATA ─── */
const lawyers = [
  {
    id: 1, initial: "ع", name: "عمر الحسيني",
    field: "قانون الأعمال", fieldKey: "الأعمال",
    city: "الدار البيضاء", rating: 5, reviews: 203,
    tags: ["الشركات", "العقود", "الإفلاس"],
    price: 500, available: false,
    avatarColor: "var(--primary)",
  },
  {
    id: 2, initial: "ف", name: "فاطمة الزهراء المرابطي",
    field: "مدونة الأسرة", fieldKey: "الأسرة",
    city: "الرباط", rating: 4, reviews: 89,
    tags: ["الطلاق", "الحضانة", "النفقة"],
    price: 350, available: true,
    avatarColor: "var(--gold)",
  },
  {
    id: 3, initial: "ي", name: "يوسف البكالي",
    field: "قانون الشغل", fieldKey: "الشغل",
    city: "الدار البيضاء", rating: 4, reviews: 127,
    tags: ["عقود الشغل", "الطرد التعسفي", "النزاعات"],
    price: 400, available: true,
    avatarColor: "#0369a1",
  },
  {
    id: 4, initial: "س", name: "سمية أوحمو",
    field: "قانون المقاولات", fieldKey: "الأعمال",
    city: "طنجة", rating: 4, reviews: 112,
    tags: ["المقاول الذاتي", "الضرائب", "العقود"],
    price: 480, available: true,
    avatarColor: "#b45309",
  },
  {
    id: 5, initial: "ك", name: "كريم التازي",
    field: "القانون الجنائي", fieldKey: "الجنائي",
    city: "فاس", rating: 5, reviews: 74,
    tags: ["الدفاع", "الطعون", "الاستئناف"],
    price: 600, available: true,
    avatarColor: "#9d174d",
  },
  {
    id: 6, initial: "ن", name: "نجوى بنسالم",
    field: "قانون العقار", fieldKey: "العقار",
    city: "مراكش", rating: 4, reviews: 156,
    tags: ["التسجيل", "النزاعات", "الكراء"],
    price: 450, available: true,
    avatarColor: "#0f766e",
  },
];

const FILTERS = ["الكل", "الأسرة", "الشغل", "الأعمال", "العقار", "الجنائي"];

const Stars = ({ count }) => (
  <div className="lw-stars">
    {[1,2,3,4,5].map(i => (
      <Star key={i} size={14} className={i <= count ? "star filled" : "star"} />
    ))}
  </div>
);

/* ─── BOOKING MODAL ─── */
function BookingModal({ lawyer, onClose }) {
  const [selectedDay, setSelectedDay] = useState(null);
  const [selectedTime, setSelectedTime] = useState(null);
  const [step, setStep] = useState(1);

  const times = ["09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00"];
  const days = [...Array(7)].map((_, i) => {
    const d = new Date();
    d.setDate(d.getDate() + i);
    return {
      day: d.toLocaleDateString("ar-MA", { weekday: "long" }),
      date: d.getDate(),
      month: d.toLocaleDateString("ar-MA", { month: "short" })
    };
  });

  return (
    <div className="bm-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="bm-modal glass" dir="rtl">
        <button className="bm-close" onClick={onClose}>✕</button>

        <h2 className="bm-title">حجز استشارة قانونية</h2>
        <p className="bm-subtitle">أنت بصدد الحجز مع {lawyer.name}</p>

        {step === 1 ? (
          <>
            <div className="bm-section">
              <p className="bm-label">اختر اليوم</p>
              <div className="bm-days">
                {days.map((d, i) => (
                  <div
                    key={i}
                    className={`bm-day ${selectedDay === i ? "active" : ""}`}
                    onClick={() => setSelectedDay(i)}
                  >
                    <span className="bm-day-name">{d.day}</span>
                    <span className="bm-day-date">{d.date} {d.month}</span>
                  </div>
                ))}
              </div>
            </div>

            <div className="bm-section">
              <p className="bm-label">الوقت المتاح</p>
              <div className="bm-times">
                {times.map(t => (
                  <button
                    key={t}
                    className={`bm-time ${selectedTime === t ? "active" : ""}`}
                    onClick={() => setSelectedTime(t)}
                  >
                    {t}
                  </button>
                ))}
              </div>
            </div>

            <button
              className={`bm-next-btn ${(!selectedDay && selectedDay !== 0) || !selectedTime ? "disabled" : ""}`}
              onClick={() => selectedTime && (selectedDay !== null) && setStep(2)}
            >
              متابعة الحجز
            </button>
          </>
        ) : (
          <div className="bm-confirm animate-fade-in">
             <div className="success-icon"><ShieldCheck size={48} /></div>
             <h3>تأكيد الطلب</h3>
             <div className="bm-confirm-details">
                <p><Calendar size={18} /> {days[selectedDay]?.day} {days[selectedDay]?.date} {days[selectedDay]?.month}</p>
                <p><Clock size={18} /> {selectedTime}</p>
                <p><Banknote size={18} /> {lawyer.price} MAD (دفع إلكتروني آمن)</p>
             </div>
             <button className="bm-confirm-btn">تأكيد وأداء</button>
             <button className="bm-back-link" onClick={() => setStep(1)}>الرجوع لتعديل الوقت</button>
          </div>
        )}
      </div>
    </div>
  );
}

/* ─── LAWYER CARD ─── */
function LawyerCard({ lawyer, onBook }) {
  return (
    <div className="lawyer-card-premium animate-slide-up">
      <div className="card-top-info">
          <div className="lawyer-meta">
              <div className="avatar-box" style={{ background: lawyer.avatarColor }}>
                  {lawyer.initial}
              </div>
              <div className="ratings">
                  <Stars count={lawyer.rating} />
                  <span className="review-count">({lawyer.reviews} مراجعة)</span>
              </div>
          </div>
          <div className="lawyer-identity">
              <h3>{lawyer.name}</h3>
              <p className="field-tag">{lawyer.field}</p>
          </div>
      </div>

      <div className="card-body">
          <div className="info-row">
              <MapPin size={16} /> <span>{lawyer.city}</span>
          </div>
          <div className="tags-container">
              {lawyer.tags.map(t => <span key={t} className="l-tag">{t}</span>)}
          </div>
      </div>

      <div className="card-footer-premium">
          <div className="price-box">
              <span className="price-val">{lawyer.price} MAD</span>
              <span className="price-label">/ ساعة</span>
          </div>
          <button 
              className={`booking-btn ${!lawyer.available ? 'busy' : ''}`}
              onClick={() => lawyer.available && onBook(lawyer)}
          >
              {lawyer.available ? 'احجز الآن' : 'غير متوفر'}
          </button>
      </div>
    </div>
  );
}

/* ─── PAGE ─── */
export default function Lawyers() {
  const [search, setSearch] = useState("");
  const [activeFilter, setActiveFilter] = useState("الكل");
  const [bookingLawyer, setBookingLawyer] = useState(null);

  const filtered = lawyers.filter(l => {
    const matchSearch = !search || l.name.includes(search) || l.field.includes(search) || l.city.includes(search);
    const matchFilter = activeFilter === "الكل" || l.fieldKey === activeFilter;
    return matchSearch && matchFilter;
  });

  return (
    <div className="lawyers-page-premium">
      <div className="page-wrapper">
          <div className="lawyers-header animate-fade-in">
              <div className="badge-premium">🏛️ شبكة المحامين</div>
              <h1>ابحث عن استشارك القانوني</h1>
              <p>مئات المحامين المعتمدين والمتحقق من هويتهم لمساعدتك في قضاياك.</p>
          </div>

          <div className="lawyers-controls animate-slide-up">
              <div className="search-container-premium">
                  <Search className="search-icon" size={20} />
                  <input
                      type="text"
                      placeholder="ابحث بالاسم، المدينة أو التخصص..."
                      value={search}
                      onChange={e => setSearch(e.target.value)}
                  />
              </div>
              <div className="filters-premium">
                  {FILTERS.map(f => (
                      <button
                          key={f}
                          className={`filter-pill-modern ${activeFilter === f ? "active" : ""}`}
                          onClick={() => setActiveFilter(f)}
                      >
                          {f}
                      </button>
                  ))}
              </div>
          </div>

          <div className="results-count animate-fade-in">
              {filtered.length} محامي متاح لمساعدتك
          </div>

          <div className="lawyers-grid-premium">
              {filtered.length > 0 ? (
                  filtered.map(l => <LawyerCard key={l.id} lawyer={l} onBook={setBookingLawyer} />)
              ) : (
                  <div className="empty-results">
                      <p>لا توجد نتائج تطابق بحثك حالياً.</p>
                  </div>
              )}
          </div>
      </div>

      {bookingLawyer && (
        <BookingModal lawyer={bookingLawyer} onClose={() => setBookingLawyer(null)} />
      )}
    </div>
  );
}