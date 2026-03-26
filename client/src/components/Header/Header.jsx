import "./Header.css";
import { NavLink } from "react-router-dom";

function Header() {

  return (

    <header className="header" dir="rtl">

      <div className="logo">
        <div className="logo-icon">⚖️</div>
        <h2 className="logo-text">
          <span className="green">حقي</span>
        </h2>
      </div>

      <nav className="nav">
        <NavLink to="/home">الرئيسية</NavLink>
        <NavLink to="/ai" className="ai-link">🤖 المساعد AI</NavLink>
        <NavLink to="/contracts">📄 العقود</NavLink>
        <NavLink to="/lawyers">⚖️ المحامون</NavLink>
        <NavLink to="/culture">🎬 ثقافة قانونية</NavLink>
        <NavLink to="/dashboard">📊 لوحتي</NavLink>
        <NavLink to="/business">💼 Business Model</NavLink>
      </nav>

      <div className="actions">
        <button className="start">ابدأ مجاناً</button>
        <button className="dashboard-btn">لوحتي</button>
        <div className="notifications">
          <span className="bell">🔔</span>
          <span className="notif-dot"></span>
        </div>
      </div>

    </header>

  )
}

export default Header