import { BrowserRouter as Router, Routes, Route, useLocation } from "react-router-dom";
import { useState } from "react";
import "./App.css";
import { useDarkMode } from "./hooks/useDarkMode";

import Sidebar from "./components/Sidebar/Sidebar";
import LawyerSidebar from "./components/Sidebar/LawyerSidebar";

import AiPage from "./pages/AiPage";
import HomePage from "./pages/HomePage";
import LegalCulture from "./pages/LegalCulture";
import Trending from "./pages/Trending";
import Following from "./pages/Following";
import TopicPage from "./pages/TopicPage";
import Lawyers from "./pages/Lawyers";
import DiscoverPage from "./pages/DiscoverPage";
import Dashboard from "./pages/Dashboard";
import Contracts from "./pages/Contracts";
import Pricing from "./pages/Pricing";
import UserSettings from "./pages/UserSettings";
import AiCategoryPage from "./pages/AiCategoryPage";
import LegalFieldPage from "./pages/LegalFieldPage";
import LawyerDashboard from "./pages/LawyerDashboard";
import LawyerVideos from "./pages/LawyerVideos";
import LawyerAppointments from "./pages/LawyerAppointments";
import LawyerEarnings from "./pages/LawyerEarnings";
import LawyerMessages from "./pages/LawyerMessages";
import LawyerSettings from "./pages/LawyerSettings";
import LawyerNotifications from "./pages/LawyerNotifications";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Notifications from "./pages/Notifications";
import Chat from "./pages/Chat";
import AnalysisResult from "./pages/AnalysisResult";
import ForgotPassword from "./pages/ForgotPassword";

function AppContent() {
  const location = useLocation();
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const { isDark, toggle: toggleDark } = useDarkMode();

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const isAuthPage = location.pathname === "/login" || location.pathname === "/register" || location.pathname === "/forgot-password";
  const lawyerRoutes = ["/lawyer-dashboard", "/lawyer-videos", "/lawyer-appointments", "/lawyer-earnings", "/lawyer-messages", "/lawyer-settings", "/lawyer-notifications"];
  const isLawyerPage = lawyerRoutes.some(route => location.pathname.startsWith(route));

  return (
    <div className={`app-container ${isAuthPage ? 'auth-mode' : ''}`} dir="rtl">
      {!isAuthPage && !isLawyerPage && (
        <Sidebar
          isOpen={isSidebarOpen}
          toggleSidebar={toggleSidebar}
          isDark={isDark}
          toggleDark={toggleDark}
        />
      )}
      {!isAuthPage && isLawyerPage && (
        <LawyerSidebar
          isOpen={isSidebarOpen}
          toggleSidebar={toggleSidebar}
          isDark={isDark}
          toggleDark={toggleDark}
        />
      )}
      
      <main className="main-content">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/home" element={<HomePage />} />
          <Route path="/ai" element={<AiPage />} />
          <Route path="/ai/:category" element={<AiCategoryPage />} />
          <Route path="/field/:fieldName" element={<LegalFieldPage />} />
          <Route path="/culture" element={<LegalCulture />} />
          <Route path="/trending" element={<Trending />} />
          <Route path="/following" element={<Following />} />
          <Route path="/topic/:name" element={<TopicPage />} />
          <Route path="/lawyers" element={<Lawyers />} />
          <Route path="/discover" element={<DiscoverPage />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/lawyer-dashboard" element={<LawyerDashboard />} />
          <Route path="/lawyer-videos" element={<LawyerVideos />} />
          <Route path="/lawyer-appointments" element={<LawyerAppointments />} />
          <Route path="/lawyer-earnings" element={<LawyerEarnings />} />
          <Route path="/lawyer-messages" element={<LawyerMessages />} />
          <Route path="/lawyer-settings" element={<LawyerSettings />} />
          <Route path="/lawyer-notifications" element={<LawyerNotifications />} />
          <Route path="/contracts" element={<Contracts />} />
          <Route path="/pricing" element={<Pricing />} />
          <Route path="/settings" element={<UserSettings />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/notifications" element={<Notifications />} />
          <Route path="/chat" element={<Chat />} />
          <Route path="/analysis-result" element={<AnalysisResult />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
        </Routes>
      </main>
    </div>
  );
}

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;