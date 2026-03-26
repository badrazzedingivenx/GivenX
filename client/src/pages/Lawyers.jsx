import { useState } from "react";
import "./Lawyers.css";

/* ─── DATA ─── */
const lawyers = [
  {
    id: 1, initial: "ع", name: "عمر الحسيني",
    field: "قانون الأعمال", fieldKey: "الأعمال",
    city: "الدار البيضاء", rating: 4, reviews: 203,
    tags: ["الشركات", "العقود", "الإفلاس"],
    price: 500, available: false,
    avatarColor: "linear-gradient(135deg,#1a4731,#14532d)",
  },
  {
    id: 2, initial: "ف", name: "فاطمة الزهراء المرابطي",
    field: "مدونة الأسرة", fieldKey: "الأسرة",
    city: "الرباط", rating: 4, reviews: 89,
    tags: ["الطلاق", "الحضانة", "النفقة"],
    price: 350, available: true,
    avatarColor: "linear-gradient(135deg,#7c3aed,#5b21b6)",
  },
  {
    id: 3, initial: "ي", name: "يوسف البكالي",
    field: "قانون الشغل", fieldKey: "الشغل",
    city: "الدار البيضاء", rating: 4, reviews: 127,
    tags: ["عقود الشغل", "الطرد التعسفي", "النزاعات"],
    price: 400, available: true,
    avatarColor: "linear-gradient(135deg,#0369a1,#0c4a6e)",
  },
  {
    id: 4, initial: "س", name: "سمية أوحمو",
    field: "قانون المقاولات", fieldKey: "الأعمال",
    city: "طنجة", rating: 4, reviews: 112,
    tags: ["المقاول الذاتي", "الضرائب", "العقود"],
    price: 480, available: true,
    avatarColor: "linear-gradient(135deg,#b45309,#d97706)",
  },
  {
    id: 5, initial: "ك", name: "كريم التازي",
    field: "القانون الجنائي", fieldKey: "الجنائي",
    city: "فاس", rating: 4, reviews: 74,
    tags: ["الدفاع", "الطعون", "الاستئناف"],
    price: 600, available: true,
    avatarColor: "linear-gradient(135deg,#9d174d,#be185d)",
  },
  {
    id: 6, initial: "ن", name: "نجوى بنسالم",
    field: "قانون العقار", fieldKey: "العقار",
    city: "مراكش", rating: 4, reviews: 156,
    tags: ["التسجيل", "النزاعات", "الكراء"],
    price: 450, available: true,
    avatarColor: "linear-gradient(135deg,#0f766e,#0d9488)",
  },
];

const FILTERS = ["الكل", "الأسرة", "الشغل", "الأعمال", "العقار", "الجنائي"];

/* Slots randomly marked as booked for demo */
const BOOKED_SLOTS = ["10:00", "15:00"];

const Stars = ({ count }) => (
  <span className="lw-stars">
    {[1,2,3,4,5].map(i => (
      <span key={i} className={i <= count ? "star filled" : "star"}>★</span>
    ))}
  </span>
);

/* ─── BOOKING MODAL ─── */
function BookingModal({ lawyer, onClose }) {
  const [selectedDay, setSelectedDay] = useState(null);
  const [selectedTime, setSelectedTime] = useState(null);
  const [step, setStep] = useState(1); // 1 = pick date/time, 2 = confirm

  const times = ["09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00"];
  const days = [...Array(7)].map((_, i) => {
    const d = new Date();
    d.setDate(d.getDate() + i);
    return {
      day: d.toLocaleDateString("ar-MA", { weekday: "long" }),
      date: d.getDate(),
    };
  });

  return (
    <div className="bm-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="bm-modal" dir="rtl">

        {/* Progress bar */}
        <div className="bm-progress">
          <div className="bm-progress-fill" style={{ width: step === 1 ? "50%" : "100%" }} />
        </div>

        {/* Close */}
        <button className="bm-close" onClick={onClose}>✕</button>

        <h2 className="bm-title">حجز مع {lawyer.name}</h2>

        {step === 1 ? (
          <>
            {/* Date Picker */}
            <div className="bm-section">
              <p className="bm-label">اختر التاريخ</p>
              <div className="bm-days">
                {days.map((d, i) => (
                  <div
                    key={i}
                    className={`bm-day ${selectedDay === i ? "active" : ""}`}
                    onClick={() => setSelectedDay(i)}
                  >
                    <span className="bm-day-name">{d.day}</span>
                    <span className="bm-day-date">{d.date}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Time Picker */}
            <div className="bm-section">
              <p className="bm-label">اختر الوقت</p>
              <div className="bm-times">
                {times.map(t => {
                  const booked = BOOKED_SLOTS.includes(t);
                  return (
                    <button
                      key={t}
                      className={`bm-time ${selectedTime === t ? "active" : ""} ${booked ? "booked" : ""}`}
                      onClick={() => !booked && setSelectedTime(t)}
                      disabled={booked}
                    >
                      {t}
                      {booked && <span className="bm-booked-label">محجوز</span>}
                    </button>
                  );
                })}
              </div>
            </div>

            <button
              className={`bm-next-btn ${(!selectedDay && selectedDay !== 0) || !selectedTime ? "disabled" : ""}`}
              onClick={() => selectedTime && (selectedDay !== null) && setStep(2)}
            >
              التالي ←
            </button>
          </>
        ) : (
          /* Confirmation Step */
          <div className="bm-confirm">
            <div className="bm-confirm-avatar" style={{ background: lawyer.avatarColor }}>
              {lawyer.initial}
            </div>
            <div className="bm-confirm-name">{lawyer.name}</div>
            <div className="bm-confirm-field">{lawyer.field}</div>

            <div className="bm-confirm-details">
              <div className="bm-confirm-row">
                <span className="bm-confirm-icon">📅</span>
                <span>{days[selectedDay]?.day}، {days[selectedDay]?.date}</span>
              </div>
              <div className="bm-confirm-row">
                <span className="bm-confirm-icon">🕐</span>
                <span>{selectedTime}</span>
              </div>
              <div className="bm-confirm-row">
                <span className="bm-confirm-icon">💰</span>
                <span>{lawyer.price} MAD / ساعة</span>
              </div>
            </div>

            <div className="bm-confirm-btns">
              <button className="bm-back-btn" onClick={() => setStep(1)}>← رجوع</button>
              <button className="bm-confirm-btn">✓ تأكيد الحجز</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

/* ─── LAWYER CARD ─── */
function LawyerCard({ lawyer, onBook }) {
  return (
    <div className="lw-card">
      {/* Top */}
      <div className="lw-card-top">
        <div className="lw-info">
          <div className="lw-rating-row">
            <Stars count={lawyer.rating} />
            <span className="lw-reviews">({lawyer.reviews})</span>
          </div>
          <div className="lw-name">{lawyer.name}</div>
        </div>
        <div className="lw-avatar" style={{ background: lawyer.avatarColor }}>
          {lawyer.initial}
        </div>
      </div>

      {/* Middle */}
      <div className="lw-card-mid">
        <span className="lw-field-tag">{lawyer.field}</span>
        <div className="lw-location">
          <span className="lw-loc-icon">📍</span>
          <span>{lawyer.city}</span>
        </div>
        <div className="lw-tags">
          {lawyer.tags.map(t => <span key={t} className="lw-tag">{t}</span>)}
        </div>
      </div>

      {/* Bottom */}
      <div className="lw-card-bottom">
        <div className="lw-bottom-right">
          <div className="lw-price">{lawyer.price} MAD<span>/ساعة</span></div>
          <div className={`lw-status ${lawyer.available ? "available" : "busy"}`}>
            <span className="lw-dot" />
            {lawyer.available ? "متاح الآن" : "غير متاح"}
          </div>
        </div>
        <button
          className={`lw-book-btn ${!lawyer.available ? "disabled" : ""}`}
          disabled={!lawyer.available}
          onClick={() => lawyer.available && onBook(lawyer)}
        >
          {lawyer.available ? "📅 احجز" : "غير متاح"}
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
    <div className="lw-page" dir="rtl">

      {/* ── Hero & Search ── */}
      <section className="lw-hero">
        <div className="lw-hero-content">
          <div className="lw-hero-badge">🏛️ محامون معتمدون</div>
          <h1 className="lw-hero-title">شبكة المحامين المعتمدين 🏛️</h1>
          <p className="lw-hero-sub">محامون متحقق منهم — احجز استشارة في ظرف 24 ساعة</p>

          <div className="lw-search-bar">
            <input
              type="text"
              className="lw-search-input"
              placeholder="ابحث بالاسم أو التخصص أو المدينة..."
              value={search}
              onChange={e => setSearch(e.target.value)}
            />
            <button className="lw-search-btn">🔍 بحث</button>
          </div>

          <div className="lw-filters">
            {FILTERS.map(f => (
              <button
                key={f}
                className={`lw-filter-pill ${activeFilter === f ? "active" : ""}`}
                onClick={() => setActiveFilter(f)}
              >
                {f}
              </button>
            ))}
          </div>
        </div>
      </section>

      {/* ── Grid ── */}
      <section className="lw-grid-section">
        <div className="lw-results-info">
          <span>{filtered.length} محامٍ متاح</span>
        </div>
        <div className="lw-grid">
          {filtered.length > 0
            ? filtered.map(l => <LawyerCard key={l.id} lawyer={l} onBook={setBookingLawyer} />)
            : <p className="lw-empty">لا يوجد محامون مطابقون للبحث</p>
          }
        </div>
      </section>

      {/* ── Booking Modal ── */}
      {bookingLawyer && (
        <BookingModal lawyer={bookingLawyer} onClose={() => setBookingLawyer(null)} />
      )}
    </div>
  );
}