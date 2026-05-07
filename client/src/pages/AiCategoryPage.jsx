import React, { useRef, useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  ChevronRight, 
  MessageSquare, 
  FileText, 
  Gavel, 
  Info, 
  ArrowLeft,
  Sparkles
} from 'lucide-react';
import ChatBox from '../components/Chat/ChatBox';
import categories from '../data/legalCategories';
import './AiCategoryPage.css';

const AiCategoryPage = () => {
  const { category } = useParams();
  const navigate = useNavigate();
  const chatRef = useRef();
  const [catData, setCatData] = useState(null);

  useEffect(() => {
    const found = categories.find(c => c.name === category);
    if (found) {
      setCatData(found);
    }
  }, [category]);

  if (!catData) return <div className="loading">جاري التحميل...</div>;

  const quickQuestions = [
    `ما هي أهم الخطوات في قضايا ${catData.name}؟`,
    `كيف أحمي حقوقي في موضوع ${catData.name}؟`,
    `ما هي الوثائق المطلوبة لـ ${catData.name}؟`,
    `هل أحتاج لمحامٍ في قضية ${catData.name}؟`
  ];

  return (
    <div className="cat-page-wrapper" dir="rtl">
      <header className="cat-header">
        <div className="header-top">
          <button className="back-btn" onClick={() => navigate('/ai')}>
            <ChevronRight size={20} /> العودة للمساعد العام
          </button>
          <div className="cat-badge">قسـم {catData.name}</div>
        </div>
        
        <div className="cat-hero-content">
          <div className="cat-icon-lg">{catData.icon}</div>
          <div className="cat-text">
            <h1>المساعد القانوني لقضايا <span className="highlight">{catData.name}</span></h1>
            <p>أنا هنا للإجابة على كل استفساراتك المتعلقة بـ {catData.name} في القانون المغربي.</p>
          </div>
        </div>
      </header>

      <div className="cat-layout">
        <aside className="cat-sidebar">
          <div className="sidebar-card">
            <h3><Info size={18} /> نصائح سريعة</h3>
            <ul className="tips-list">
              <li>احتفظ دائماً بنسخ من جميع الوثائق.</li>
              <li>التزم بالآجال القانونية المحددة.</li>
              <li>استشر محامياً مختصاً قبل التوقيع.</li>
            </ul>
          </div>

          <div className="sidebar-card">
            <h3><FileText size={18} /> نماذج عقود</h3>
            <div className="template-list">
              <button className="template-item">
                <span>نموذج عقد {catData.name} (أساسي)</span>
                <ChevronRight size={14} />
              </button>
              <button className="template-item">
                <span>إشهاد في موضوع {catData.name}</span>
                <ChevronRight size={14} />
              </button>
            </div>
          </div>
        </aside>

        <main className="cat-main">
          <div className="quick-q-grid">
            {quickQuestions.map((q, idx) => (
              <button key={idx} className="q-card" onClick={() => chatRef.current?.sendMessage(q)}>
                <MessageSquare size={16} />
                <span>{q}</span>
              </button>
            ))}
          </div>

          <div className="chat-container-cat">
            <div className="chat-welcome">
              <Sparkles size={20} className="sparkle-icon" />
              <span>تحدث مع المساعد حول {catData.name} الآن</span>
            </div>
            <ChatBox ref={chatRef} />
          </div>
        </main>
      </div>
    </div>
  );
};

export default AiCategoryPage;
