import React from 'react';
import { Search, FileText, Briefcase, Users, Lock, ShoppingCart, ClipboardCheck, ArrowLeft, Wand2 } from 'lucide-react';
import './Contracts.css';

const Contracts = () => {
    const categories = [
        {
            id: 1,
            title: 'عقد الكراء السكني',
            description: 'متوافق مع القانون 67.12 المنظم للعلاقات الكرائية بالمغرب.',
            icon: <FileText size={24} />,
            color: '#eef2ff',
        },
        {
            id: 2,
            title: 'عقد العمل',
            description: 'CDD أو CDI وفق مدونة الشغل المغربية – كامل الشروط القانونية.',
            icon: <Briefcase size={24} />,
            color: '#fff7ed',
        },
        {
            id: 3,
            title: 'عقد الشركة التجارية',
            description: 'حصص وأرباح ومسؤوليات واضحة بين الشركاء التجاريين.',
            icon: <Users size={24} />,
            color: '#f0fdf4',
        },
        {
            id: 4,
            title: 'اتفاقية السرية (NDA)',
            description: 'حماية المعلومات السرية والملكية الفكرية بين الأطراف.',
            icon: <Lock size={24} />,
            color: '#fff1f2',
        },
        {
            id: 5,
            title: 'عقد البيع',
            description: 'بيع عقار أو منقول مع الشروط والضمانات القانونية الكاملة.',
            icon: <ShoppingCart size={24} />,
            color: '#f0f9ff',
        },
        {
            id: 6,
            title: 'عقد الوكالة',
            description: 'توكيل رسمي يمنح صلاحيات محددة للتصرف نيابة عنك.',
            icon: <ClipboardCheck size={24} />,
            color: '#f5f3ff',
        },
    ];

    return (
        <div className="contracts-premium-page">
            <div className="page-wrapper">
                {/* Minimalist Hero */}
                <section className="contracts-hero animate-fade-in">
                    <div className="badge-premium">📄 المولد الذكي</div>
                    <h1>صياغة قانونية محترفة <span className="text-gradient">في متناول يدك</span></h1>
                    <p className="hero-subtext">اختر القالب المناسب، املأ البيانات، واحصل على عقد جاهز للمباراة في دقائق.</p>
                    
                    <div className="search-bar-premium animate-slide-up">
                        <Search size={20} className="search-icon" />
                        <input type="text" placeholder="ما هو العقد الذي تبحث عنه؟" />
                        <button className="search-btn-cta">إبحث الآن</button>
                    </div>
                </section>

                {/* Catalog Grid */}
                <section className="catalog-section">
                    <div className="catalog-grid-modern">
                        {categories.map((cat) => (
                            <div key={cat.id} className="modern-catalog-card animate-slide-up">
                                <div className="card-top">
                                    <div className="icon-circle" style={{ background: cat.color }}>
                                        {cat.icon}
                                    </div>
                                    <div className="card-status">محدث 2024</div>
                                </div>
                                <h3>{cat.title}</h3>
                                <p>{cat.description}</p>
                                <div className="card-footer">
                                    <span className="card-tag">قالب جاهز</span>
                                    <button className="use-btn">استخدام القالب <ArrowLeft size={16} /></button>
                                </div>
                            </div>
                        ))}
                    </div>
                </section>

                {/* AI Feature Callout */}
                <section className="ai-callout-box glass animate-slide-up-long">
                    <div className="ai-icon-burst"><Wand2 size={32} /></div>
                    <div className="ai-content">
                        <h2>هل تبحث عن عقد مخصص؟</h2>
                        <p>صف عقدك بكلماتك البسيطة، وسيتولى مساعدنا الذكي كتابة مسودة قانونية دقيقة تلبي احتياجاتك الخاصة فوراً.</p>
                    </div>
                    <button className="cta-ai-btn">ابدأ الصياغة الذكية</button>
                </section>
            </div>
        </div>
    );
};

export default Contracts;
