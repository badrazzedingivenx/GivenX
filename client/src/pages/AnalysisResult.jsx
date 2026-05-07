import React from 'react';
import './AnalysisResult.css';

const AnalysisResult = () => {
    return (
        <div className="analysis-page" dir="rtl">
            <div className="analysis-container">
                <header className="analysis-header">
                    <span className="analysis-badge">تحليل الذكاء الاصطناعي</span>
                    <h1 className="analysis-title">نتائج تحليل عقد الكراء التجاري</h1>
                    <p className="analysis-subtitle">تمت المراجعة في 14 مارس 2025 بواسطة حقي AI</p>
                </header>

                <div className="analysis-grid">
                    {/* Summary Section */}
                    <div className="analysis-section summary">
                        <div className="section-icon">📜</div>
                        <div className="section-content">
                            <h3>ملخص العقد</h3>
                            <p>هذا العقد يخص كراء محل تجاري بمدينة الدار البيضاء. المدة المحددة هي 3 سنوات قابلة للتجديد.</p>
                        </div>
                    </div>

                    {/* Risks Section */}
                    <div className="analysis-section risks">
                        <div className="section-icon">⚠️</div>
                        <div className="section-content">
                            <h3>المخاطر القانونية</h3>
                            <ul className="risks-list">
                                <li className="risk-high">
                                    <strong>المادة 4:</strong> شرط فسخ العقد في حالة تأخر الأداء بـ 5 أيام فقط يعتبر تعسفياً.
                                </li>
                                <li className="risk-medium">
                                    <strong>المادة 12:</strong> لم يتم تحديد مسؤولية الإصلاحات الكبرى بشكل واضح.
                                </li>
                            </ul>
                        </div>
                    </div>

                    {/* Recommendations Section */}
                    <div className="analysis-section solutions">
                        <div className="section-icon">✅</div>
                        <div className="section-content">
                            <h3>توصيات قانونية</h3>
                            <p>نقترح تعديل المادة 4 لتصبح مدة الإنذار 15 يوماً على الأقل توافقاً مع القانون 49.16 المتعلق بكراء العقارات المخصصة للاستعمال التجاري.</p>
                        </div>
                    </div>

                    {/* Lawyer CTA */}
                    <div className="lawyer-cta-card">
                        <div className="cta-info">
                            <h3>هل تريد رأي محامي مختص؟</h3>
                            <p>يمكنك تحويل هذا التحليل لمحامي لمناقشته وتعديل العقد فوراً.</p>
                        </div>
                        <button className="cta-btn">تحدث مع محامي الآن</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AnalysisResult;
