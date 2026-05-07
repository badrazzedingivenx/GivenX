import { useState } from "react";
import "./Header.css";
import { useNavigate } from "react-router-dom";
import Sidebar from "../Sidebar/Sidebar";

function Header() {
  const navigate = useNavigate();
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  return (
    <>
      <header className="header" dir="rtl">
        <div className="header-right">
          <button className="hamburger" onClick={toggleSidebar}>
            <div className="bar"></div>
            <div className="bar"></div>
            <div className="bar"></div>
          </button>

          <div className="logo" onClick={() => navigate('/home')}>
            <div className="logo-icon">⚖️</div>
            <h2 className="logo-text">
              <span className="green">حقي</span>
            </h2>
          </div>
        </div>

        {/* Removed desktop nav as per user request to move everything to sidebar */}
        
        <div className="actions">
          <div className="notifications" onClick={() => navigate('/notifications')}>
            <span className="bell">🔔</span>
            <span className="notif-dot"></span>
          </div>
          <button className="dashboard-btn-header" onClick={() => navigate('/dashboard')}>لوحتي</button>
          <button className="start-btn-header">ابدأ</button>
        </div>
      </header>

      <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />
    </>
  );
}

export default Header;