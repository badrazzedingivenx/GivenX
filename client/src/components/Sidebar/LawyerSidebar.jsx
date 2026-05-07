import { NavLink, useLocation, useNavigate } from "react-router-dom";
import { 
  Home, 
  Video, 
  CalendarCheck, 
  Wallet, 
  MessageSquare, 
  Settings, 
  LogOut, 
  Menu, 
  X,
  Bell,
  Scale,
  ChevronLeft,
  Sun,
  Moon
} from "lucide-react";
import "./Sidebar.css"; // We can reuse the same sidebar CSS

const LawyerSidebar = ({ isOpen, toggleSidebar, isDark, toggleDark }) => {
  const navigate = useNavigate();
  const location = useLocation();

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
          <div className="brand" onClick={() => navigate('/lawyer-dashboard')}>
             <span className="brand-logo">⚖️</span>
             <div className="brand-info">
               <h2 className="brand-name">حقي</h2>
               <span className="brand-badge">محامي</span>
             </div>
          </div>
          <button className="close-btn mobile-only" onClick={toggleSidebar}>
            <ChevronLeft size={20} />
          </button>
        </div>

        <div className="sidebar-section">
          <nav className="sidebar-nav">
            <NavLink to="/lawyer-dashboard" className="menu-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
              <Home size={18} /> <span>لوحة التحكم</span>
            </NavLink>
            <NavLink to="/lawyer-videos" className="menu-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
              <Video size={18} /> <span>المحتوى الخاص بي</span>
            </NavLink>
            <NavLink to="/lawyer-appointments" className="menu-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
              <CalendarCheck size={18} /> <span>المواعيد</span>
            </NavLink>
            <NavLink to="/lawyer-earnings" className="menu-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
              <Wallet size={18} /> <span>الأرباح</span>
            </NavLink>
            <NavLink to="/lawyer-messages" className="menu-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
              <MessageSquare size={18} /> <span>الرسائل</span>
            </NavLink>
          </nav>
        </div>

        <div className="sidebar-section utility">
          <NavLink to="/lawyer-settings" className="menu-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
            <Settings size={18} /> <span>الإعدادات</span>
          </NavLink>

          <NavLink to="/lawyer-notifications" className="menu-item notification-item" onClick={() => window.innerWidth < 768 && toggleSidebar()}>
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
          <button 
            className="logout-link" 
            onClick={() => {
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

export default LawyerSidebar;
