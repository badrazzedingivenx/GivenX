import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import "./LegalCulture.css";

/* ───── DATA ───── */
const posts = [
  {
    id: 1,
    initials: "ع",
    avatarColor: "linear-gradient(135deg,#16a34a,#15803d)",
    name: "عمر الحسيني",
    badge: "✓ قانون الأعمال",
    emoji: "🏢",
    gradient: "linear-gradient(160deg,#052e16 0%,#14532d 45%,#166534 100%)",
    title: "كيفاش تأسس شركة في المغرب في 2025؟",
    desc: "5 خطوات عملية لتسجيل شركتك SARL من الصفر – الوثائق والتكاليف الحقيقية",
    tags: ["#SARL", "#المقاولات"],
    likes: "4.8K", comments: "312", shares: "891", saves: "1.2K",
  },
  {
    id: 2,
    initials: "ف",
    avatarColor: "linear-gradient(135deg,#7c3aed,#5b21b6)",
    name: "فاطمة الزهراء بنعلي",
    badge: "✓ قانون الأسرة",
    emoji: "⚖️",
    gradient: "linear-gradient(160deg,#1e1b4b 0%,#3730a3 45%,#4f46e5 100%)",
    title: "حقوق الزوجة عند الطلاق – ما اللي كتعرفوش",
    desc: "مدونة الأسرة 2025: النفقة، الحضانة، السكن – كل واحدة بالتفصيل مع الأرقام الحقيقية",
    tags: ["#الطلاق", "#مدونة_الأسرة"],
    likes: "9.2K", comments: "1.1K", shares: "3.4K", saves: "5.7K",
  },
  {
    id: 3,
    initials: "ي",
    avatarColor: "linear-gradient(135deg,#0369a1,#0c4a6e)",
    name: "يوسف المنصوري",
    badge: "✓ قانون العمل",
    emoji: "⚡",
    gradient: "linear-gradient(160deg,#0c1a2e 0%,#0369a1 55%,#0284c7 100%)",
    title: "فصلوني من العمل – واش عندي حق نشكي؟",
    desc: "الفصل التعسفي بالقانون المغربي: الشروط، مدة الإشعار، والتعويضات التي يحق لك أخذها.",
    tags: ["#قانون_العمل", "#الفصل"],
    likes: "12.1K", comments: "943", shares: "4.2K", saves: "6.8K",
  },
];

/* ───── ICONS ───── */
const HeartIcon = ({ filled }) => (
  <svg viewBox="0 0 24 24" fill={filled ? "currentColor" : "none"} stroke="currentColor" strokeWidth="2">
    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
  </svg>
);
const CommentIcon = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
  </svg>
);
const ShareIcon = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <circle cx="18" cy="5" r="3" /><circle cx="6" cy="12" r="3" /><circle cx="18" cy="19" r="3" />
    <line x1="8.59" y1="13.51" x2="15.42" y2="17.49" />
    <line x1="15.41" y1="6.51" x2="8.59" y2="10.49" />
  </svg>
);
const SaveIcon = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" />
  </svg>
);
const CalendarIcon = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <rect x="3" y="4" width="18" height="18" rx="2" />
    <line x1="16" y1="2" x2="16" y2="6" /><line x1="8" y1="2" x2="8" y2="6" /><line x1="3" y1="10" x2="21" y2="10" />
  </svg>
);

/* ───── BOOKING MODAL ───── */
function BookingModal({ name, onClose }) {
  const [selectedDay, setSelectedDay] = useState(null);
  const [selectedTime, setSelectedTime] = useState(null);
  const times = ["09:00", "11:00", "14:00", "16:00", "17:00", "18:00", "19:00"];
  const days = [...Array(7)].map((_, i) => {
    const d = new Date(); d.setDate(d.getDate() + i);
    return { day: d.toLocaleDateString("ar-MA", { weekday: "long" }), date: d.getDate() };
  });
  return (
    <div className="rc-booking-overlay">
      <div className="rc-booking-modal">
        <button className="rc-close-btn" onClick={onClose}>✕</button>
        <h2 className="rc-booking-title">حجز مع {name}</h2>
        <div className="rc-booking-section">
          <p className="rc-booking-label">اختر التاريخ</p>
          <div className="rc-days-grid">
            {days.map((d, i) => (
              <div key={i} className={`rc-day-card ${selectedDay === i ? "active" : ""}`} onClick={() => setSelectedDay(i)}>
                <span className="rc-day-name">{d.day}</span>
                <span className="rc-day-date">{d.date}</span>
              </div>
            ))}
          </div>
        </div>
        <div className="rc-booking-section">
          <p className="rc-booking-label">اختر الوقت</p>
          <div className="rc-times-grid">
            {times.map((t) => (
              <button key={t} className={`rc-time-card ${selectedTime === t ? "active" : ""}`} onClick={() => setSelectedTime(t)}>{t}</button>
            ))}
          </div>
        </div>
        <button className="rc-next-btn">التالي ←</button>
      </div>
    </div>
  );
}

/* ───── REEL CARD ───── */
function ReelCard({ post }) {
  const [liked, setLiked] = useState(false);
  const [saved, setSaved] = useState(false);
  const [following, setFollowing] = useState(false);
  const [showBooking, setShowBooking] = useState(false);
  const navigate = useNavigate();

  return (
    <div className="rc-reel-wrap">
      {/* ── Video Player ── */}
      <div className="rc-player" style={{ background: post.gradient }}>

        {/* Discover Pill */}
        <div className="rc-discover-pill" onClick={() => navigate("/discover")} style={{ cursor: "pointer" }}>
          <span className="rc-discover-dot">🔍</span> اكتشف
        </div>

        {/* Central Illustration */}
        <div className="rc-illustration">{post.emoji}</div>

        {/* Progress Bar */}
        <div className="rc-progress-bar">
          <div className="rc-progress-fill" />
        </div>

        {/* Bottom Overlay */}
        <div className="rc-bottom-overlay">
          <div className="rc-creator-row">
            <div className="rc-avatar" style={{ background: post.avatarColor }}>{post.initials}</div>
            <div className="rc-creator-info">
              <span className="rc-creator-name">{post.name}</span>
              <span className="rc-creator-badge">{post.badge}</span>
            </div>
            <button className={`rc-follow-btn ${following ? "following" : ""}`} onClick={() => setFollowing(!following)}>
              {following ? "✓ متابَع" : "+ تابع"}
            </button>
          </div>

          <div className="rc-post-title">{post.title}</div>
          <div className="rc-post-desc">{post.desc}</div>

          <div className="rc-tags">
            {post.tags.map((t) => (
              <NavLink key={t} to={`/topic/${t.replace("#", "")}`} className="rc-tag">{t}</NavLink>
            ))}
          </div>
        </div>
      </div>

      {/* ── Side Actions ── */}
      <div className="rc-side-actions">
        <div className={`rc-action ${liked ? "active" : ""}`} onClick={() => setLiked(!liked)}>
          <div className="rc-action-icon"><HeartIcon filled={liked} /></div>
          <span>{post.likes}</span>
        </div>
        <div className="rc-action">
          <div className="rc-action-icon"><CommentIcon /></div>
          <span>{post.comments}</span>
        </div>
        <div className="rc-action">
          <div className="rc-action-icon"><ShareIcon /></div>
          <span>{post.shares}</span>
        </div>
        <div className={`rc-action ${saved ? "active" : ""}`} onClick={() => setSaved(!saved)}>
          <div className="rc-action-icon"><SaveIcon /></div>
          <span>{post.saves}</span>
        </div>
        <div className="rc-action rc-action-book" onClick={() => setShowBooking(true)}>
          <div className="rc-action-icon"><CalendarIcon /></div>
          <span>احجز</span>
        </div>
      </div>

      {showBooking && <BookingModal name={post.name} onClose={() => setShowBooking(false)} />}
    </div>
  );
}

/* ───── PAGE ───── */
export default function LegalCulture() {
  const [activeTab, setActiveTab] = useState("لك");

  return (
    <div className="rc-page" dir="rtl">

      {/* Right Tabs Panel */}
      <div className="rc-right-panel">
        {["لك", "متابعون", "رانج"].map((tab) => (
          <button key={tab} className={`rc-tab ${activeTab === tab ? "active" : ""}`} onClick={() => setActiveTab(tab)}>
            {tab}
          </button>
        ))}
      </div>

      {/* Feed */}
      <div className="rc-feed">
        {posts.map((p) => <ReelCard key={p.id} post={p} />)}
      </div>
    </div>
  );
}