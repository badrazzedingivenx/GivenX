import "./Footer.css";

function Footer() {
  return (
    <footer className="footer" dir="rtl">
      <div className="footer-main">
        {/* Column 1: Brand */}
        <div className="footer-col footer-brand">
          <span className="footer-logo">حقّي.</span>
          <p className="footer-tagline">
            المنصة القانونية الرقمية الأولى المصممة للسوق المغربي — تجمع الذكاء الاصطناعي والخبرة البشرية والمحتوى التعليمي.
          </p>
        </div>

        {/* Column 2: Services */}
        <div className="footer-col">
          <h4 className="footer-heading">الخدمات</h4>
          <ul className="footer-links">
            <li><a href="/ai">المساعد AI</a></li>
            <li><a href="#">العقود</a></li>
            <li><a href="#">المحامون</a></li>
            <li><a href="/culture">ثقافة قانونية</a></li>
          </ul>
        </div>

        {/* Column 3: Company */}
        <div className="footer-col">
          <h4 className="footer-heading">الشركة</h4>
          <ul className="footer-links">
            <li><a href="#">من نحن</a></li>
            <li><a href="#">المدونة</a></li>
            <li><a href="#">انضم كمحامٍ</a></li>
          </ul>
        </div>

        {/* Column 4: Support */}
        <div className="footer-col">
          <h4 className="footer-heading">الدعم</h4>
          <ul className="footer-links">
            <li><a href="#">FAQ</a></li>
            <li><a href="#">تواصل معنا</a></li>
            <li><a href="#">الخصوصية</a></li>
          </ul>
        </div>
      </div>

      <div className="footer-bottom">
        <span>© 2025 حقّي – جميع الحقوق محفوظة</span>
        <span>صنع بـ 🖤 في المغرب 🇲🇦</span>
      </div>
    </footer>
  );
}

export default Footer;
