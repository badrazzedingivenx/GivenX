import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { 
  Home, 
  Bot, 
  FileText, 
  Scale, 
  Film, 
  LayoutDashboard, 
  Bell, 
  LogOut,
  ChevronLeft,
  Moon,
  Sun
} from 'lucide-react';
import './Sidebar.css';

const Sidebar = ({ isOpen, toggleSidebar, isDark, toggleDark }) => {
  const navigate = useNavigate();

  return (
    <>
      {/* Mobile Toggle Button */}
      {!isOpen && (
        <button className="mobile-toggle" onClick={toggleSidebar}>
          <span></span>
          <span></span>
          <span></span>
        </button>
      )}

      {/* Sidebar Content */}
      <div className={`sidebar ${isOpen ? 'open' : ''}`} dir="rtl">
        <div className="sidebar-top">
          <div className="brand" onClick={() => navigate('/home')}>
             <span className="brand-logo">⚖️</span>
             <div className="brand-info">
               <h2 className="brand-name">حقي</h2>
               <span className="brand-badge">AI قانوني</span>
             </div>
          </div>
          <button className="close-btn mobile-only" onClick={toggleSidebar}>
            <ChevronLeft size={20} />
          </button>
        </div>

        <div className="sidebar-section">
          <nav className="sidebar-nav">
            <NavLink to="/home" className="menu-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
              <Home size={18} /> <span>الرئيسية</span>
            </NavLink>
            <NavLink to="/ai" className="menu-item ai-link" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
              <Bot size={18} /> <span>المساعد AI</span>
            </NavLink>
            <NavLink to="/contracts" className="menu-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
              <FileText size={18} /> <span>العقود</span>
            </NavLink>
            <NavLink to="/lawyers" className="menu-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
              <Scale size={18} /> <span>المحامون</span>
            </NavLink>
            <NavLink to="/culture" className="menu-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
              <Film size={18} /> <span>ثقافة قانونية</span>
            </NavLink>
            <NavLink to="/dashboard" className="menu-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
              <LayoutDashboard size={18} /> <span>لوحتي</span>
            </NavLink>
          </nav>
        </div>

        <div className="sidebar-section utility">
          <NavLink to="/notifications" className="menu-item notification-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
            <div className="notif-content">
              <Bell size={18} /> <span>التنبيهات</span>
            </div>
            <span className="notif-dot-small"></span>
          </NavLink>

          <button className="menu-item dark-mode-menu-item" onClick={toggleDark}>
            {isDark ? <Sun size={18} /> : <Moon size={18} />} <span>{isDark ? 'الوضع النهاري' : 'الوضع الليلي'}</span>
            <div className={`toggle-pill ${isDark ? 'on' : ''}`}>
              <div className="toggle-thumb"></div>
            </div>
          </button>
        </div>

        <div className="sidebar-cta">
          <div className="sidebar-divider"></div>
          <button className="cta-btn">ابدأ مجاناً</button>
          
          <div className="sidebar-divider"></div>
          <button 
            className="logout-link" 
            onClick={() => {
              localStorage.removeItem('token');
              navigate('/login');
              window.innerWidth < 768 && toggleSidebar();
            }}
          >
            <LogOut size={16} /> تسجيل الخروج
          </button>
        </div>
      </div>
    </>
  );
};

export default Sidebar;
