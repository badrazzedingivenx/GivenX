import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./DiscoverPage.css";

/* ─── DATA ─── */
const topLawyers = [
  { id: 1, initial: "ي", name: "يوسف البكالي",   field: "قانون الشغل",      followers: "3.8K", color: "linear-gradient(135deg,#0369a1,#0c4a6e)" },
  { id: 2, initial: "ف", name: "فاطمة الزهراء",  field: "مدونة الأسرة",     followers: "5.2K", color: "linear-gradient(135deg,#7c3aed,#5b21b6)" },
  { id: 3, initial: "ع", name: "عمر الحسيني",    field: "قانون الأعمال",    followers: "8.0K", color: "linear-gradient(135deg,#1a4731,#14532d)" },
  { id: 4, initial: "ن", name: "نجوى بنسالم",    field: "قانون العقار",     followers: "4.3K", color: "linear-gradient(135deg,#0f766e,#0d9488)" },
  { id: 5, initial: "ك", name: "كريم التازي",    field: "القانون الجنائي",  followers: "2.1K", color: "linear-gradient(135deg,#9d174d,#be185d)" },
  { id: 6, initial: "س", name: "سمية أوحمو",     field: "قانون المقاولات",  followers: "6.7K", color: "linear-gradient(135deg,#b45309,#d97706)" },
];

const videos = [
  { id: 1, emoji: "🏢", title: "كيفاش تأسس شركة في المغرب في 2025؟",              views: "14.5K", gradient: "linear-gradient(160deg,#052e16,#14532d)" },
  { id: 2, emoji: "⚖️", title: "واش عندك الحق في التعويض عند الطرد؟",            views: "21.7K", gradient: "linear-gradient(160deg,#0c4a6e,#0369a1)" },
  { id: 3, emoji: "👨‍👩‍👧", title: "الحضانة في القانون المغربي – كل شيء في دقيقتين",  views: "27.3K", gradient: "linear-gradient(160deg,#4c1d95,#7c3aed)" },
  { id: 4, emoji: "💼", title: "المقاول الذاتي: حقوقك وواجباتك القانونية",        views: "16.8K", gradient: "linear-gradient(160deg,#78350f,#d97706)" },
  { id: 5, emoji: "🚗", title: "حادثة سير – ماذا تفعل في الثواني الأولى؟",        views: "26.7K", gradient: "linear-gradient(160deg,#9d174d,#ec4899)" },
  { id: 6, emoji: "🏠", title: "عقد الكراء: 7 بنود يجب التحقق منها قبل التوقيع", views: "18.9K", gradient: "linear-gradient(160deg,#134e4a,#0d9488)" },
];

const FILTERS = ["الكل", "الأسرة", "الشغل", "الأعمال", "العقار", "الجنائي"];

export default function DiscoverPage() {
  const [search, setSearch] = useState("");
  const [active, setActive] = useState("الكل");
  const navigate = useNavigate();

  return (
    <div className="dp-page" dir="rtl">

      {/* ── Header ── */}
      <div className="dp-header">
        <h1 className="dp-title">اكتشف المحامين 🔍</h1>

        {/* Search */}
        <div className="dp-search-row">
          <div className="dp-search-icon">🔍</div>
          <input
            className="dp-search-input"
            placeholder="ابحث عن محام أو موضوع..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>

        {/* Filters */}
        <div className="dp-filters">
          {FILTERS.map(f => (
            <button
              key={f}
              className={`dp-pill ${active === f ? "active" : ""}`}
              onClick={() => setActive(f)}
            >
              {f}
            </button>
          ))}
        </div>
      </div>

      {/* ── Top Lawyers ── */}
      <section className="dp-section">
        <div className="dp-section-header">
          <span className="dp-section-title">المحامون الأكثر متابعةً</span>
        </div>
        <div className="dp-lawyers-row">
          {topLawyers.map(l => (
            <div key={l.id} className="dp-lawyer-card" onClick={() => navigate("/lawyers")}>
              <div className="dp-lawyer-avatar" style={{ background: l.color }}>{l.initial}</div>
              <div className="dp-lawyer-name">{l.name}</div>
              <div className="dp-lawyer-field">{l.field}</div>
              <div className="dp-lawyer-followers">
                <span className="dp-followers-icon">👥</span>
                {l.followers}
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* ── Trending Videos ── */}
      <section className="dp-section">
        <div className="dp-section-header">
          <span className="dp-section-title">فيديوهات رائجة هذا الأسبوع</span>
        </div>
        <div className="dp-videos-grid">
          {videos.map(v => (
            <div key={v.id} className="dp-video-card" style={{ background: v.gradient }}
              onClick={() => navigate("/culture")}>
              <div className="dp-video-emoji">{v.emoji}</div>
              <div className="dp-video-bottom">
                <div className="dp-video-title">{v.title}</div>
                <div className="dp-video-views">👁 {v.views}</div>
              </div>
            </div>
          ))}
        </div>
      </section>

    </div>
  );
}
