import "./HeroSection.css";
import categories from "../../data/legalCategories";

function HeroSection({ onAsk, chatRef }) {
  return (
    <section className="ai-hero" dir="rtl">
      <div className="ai-hero-content">
        <div className="hero-text-wrapper">
          <h1 className="hero-title">
            المساعد القانوني الذكي <span className="robot-emoji">🤖</span>
          </h1>
          <p className="hero-desc">
            اسأل بالدارجة أو العربية — مدرب على القانون المغربي
          </p>
        </div>
        <div className="ai-categories">
          {categories.map((cat, index) => (
            <button key={index} className="category-chip" onClick={() => onAsk(cat.question)}>
              <span className="cat-icon">{cat.icon}</span> {cat.name}
            </button>
          ))}
        </div>
      </div>
    </section>
  );
}

export default HeroSection;