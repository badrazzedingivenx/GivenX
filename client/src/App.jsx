import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Header from "./components/Header/Header";


import AiPage from "./pages/AiPage";
import HomePage from "./pages/HomePage";

import LegalCulture from "./pages/LegalCulture";
import Trending from "./pages/Trending";
import Following from "./pages/Following";
import TopicPage from "./pages/TopicPage";
import Lawyers from "./pages/Lawyers";
import DiscoverPage from "./pages/DiscoverPage";
import Dashboard from "./pages/Dashboard";

function App() {
  return (
    <Router>
      <Header />

      <Routes>
  <Route path="/" element={<HomePage />} />
  <Route path="/home" element={<HomePage />} />
        <Route path="/ai" element={<AiPage />} />

        <Route path="/culture" element={<LegalCulture />} />
        <Route path="/trending" element={<Trending />} />
        <Route path="/following" element={<Following />} />
        <Route path="/topic/:name" element={<TopicPage />} />
        <Route path="/lawyers" element={<Lawyers />} />
        <Route path="/discover" element={<DiscoverPage />} />
        <Route path="/dashboard" element={<Dashboard />} />
      </Routes>
    </Router>
  );
}

export default App;