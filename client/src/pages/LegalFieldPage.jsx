import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  Scale, 
  Users, 
  PlayCircle, 
  Bot, 
  ChevronRight, 
  CheckCircle2,
  Calendar,
  MessageSquare,
  FileText
} from 'lucide-react';
import legalFieldsData from '../data/legalFieldsData';
import './LegalFieldPage.css';

const LegalFieldPage = () => {
  const { fieldName } = useParams();
  const navigate = useNavigate();
  const [data, setData] = useState(null);

  useEffect(() => {
    if (legalFieldsData[fieldName]) {
      setData(legalFieldsData[fieldName]);
    }
  }, [fieldName]);

  if (!data) return <div className="field-loading">جاري تحضير الملفات...</div>;

  return (
    <div className="field-hub-container" dir="rtl">
      
      {/* Header Hub */}
      <header className="field-header">
        <button className="hub-back-btn" onClick={() => navigate('/discover')}>
          <ChevronRight size={20} /> العودة للاكتشاف
        </button>
        <div className="field-title-area">
          <div className="field-main-icon">{data.icon}</div>
          <div>
            <h1>قسـم <span className="highlight">{fieldName}</span></h1>
            <p>{data.description}</p>
          </div>
        </div>
      </header>

      <div className="field-hub-grid">
        
        {/* Main Hub Content */}
        <div className="hub-main">
          
          {/* AI Assistant Section */}
          <section className="hub-card ai-hub">
            <div className="hub-card-header">
              <div className="title-with-icon">
                <Bot size={24} className="icon-gold" />
                <h3>المساعد الذكي لـ {fieldName}</h3>
              </div>
              <button className="btn-hub-primary" onClick={() => navigate(`/ai/${fieldName}`)}>ابدأ الاستشارة</button>
            </div>
            <p className="hub-card-desc">اطرح أسئلتك القانونية حول {fieldName} واحصل على إجابات فورية مدعمة بالنصوص القانونية المغربية.</p>
            <div className="ai-prompts-row">
              {data.prompts.map((p, idx) => (
                <span key={idx}>{p}</span>
              ))}
            </div>
          </section>

          {/* Lawyers Section */}
          <section className="hub-card">
            <div className="hub-card-header">
              <div className="title-with-icon">
                <Users size={24} className="icon-blue" />
                <h3>أبرز المحامين في {fieldName}</h3>
              </div>
              <button className="btn-hub-link" onClick={() => navigate('/lawyers')}>عرض الكل</button>
            </div>
            <div className="hub-lawyers-list">
              {data.lawyers.map(lawyer => (
                <div key={lawyer.id} className="hub-lawyer-item">
                  <div className="lawyer-hub-avatar">{lawyer.avatar}</div>
                  <div className="lawyer-hub-info">
                    <h4>{lawyer.name}</h4>
                    <div className="lawyer-meta">
                      <span><CheckCircle2 size={12} /> {lawyer.experience} خبرة</span>
                      <span>⭐ {lawyer.rating}</span>
                    </div>
                  </div>
                  <button className="btn-book-small"><Calendar size={14} /> حجز</button>
                </div>
              ))}
            </div>
          </section>
        </div>

        {/* Sidebar Hub Content */}
        <aside className="hub-sidebar">
          
          {/* Legal Culture Section */}
          <section className="hub-card culture-hub">
            <div className="hub-card-header">
              <div className="title-with-icon">
                <PlayCircle size={24} className="icon-purple" />
                <h3>ثقافة قانونية</h3>
              </div>
            </div>
            <div className="hub-topics-list">
              {data.topics.map((topic, idx) => (
                <div key={idx} className="hub-topic-item">
                  <div className="topic-play-icon"><PlayCircle size={16} /></div>
                  <div className="topic-hub-details">
                    <h5>{topic.title}</h5>
                    <span>{topic.views} مشاهدة • {topic.duration}</span>
                  </div>
                </div>
              ))}
            </div>
            <button className="btn-hub-full" onClick={() => navigate('/culture')}>شاهد المزيد</button>
          </section>

          {/* Quick Resources */}
          <section className="hub-card resource-hub">
            <h3>مصادر سريعة</h3>
            <div className="resource-btns">
              <button className="res-btn"><FileText size={18} /> تحميل نماذج عقود</button>
              <button className="res-btn"><MessageSquare size={18} /> استشارة مكتوبة</button>
            </div>
          </section>

        </aside>

      </div>
    </div>
  );
};

export default LegalFieldPage;
