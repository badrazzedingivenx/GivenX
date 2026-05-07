import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, ArrowRight, ShieldCheck, CheckCircle } from 'lucide-react';
import './ForgotPassword.css';

const ForgotPassword = () => {
    const navigate = useNavigate();
    const [submitted, setSubmitted] = useState(false);

    const handleSubmit = (e) => {
        e.preventDefault();
        setSubmitted(true);
    };

    return (
        <div className="auth-page-wrapper">
            <div className="auth-bg-motif">
                <ShieldCheck size={500} strokeWidth={0.5} className="bg-icon" />
            </div>

            <div className="auth-container animate-fade-in">
                <div className="auth-card glass">
                    <div className="auth-header">
                        <div className="auth-logo-box">
                            <span className="logo-icon">⚖️</span>
                            <span className="logo-name">GIVENX</span>
                            <span className="logo-tag">حقي</span>
                        </div>
                        
                        {!submitted ? (
                            <>
                                <h1 className="auth-title">Oublié ?</h1>
                                <p className="auth-subtitle">أدخل بريدك الإلكتروني لاستعادة الوصول</p>
                            </>
                        ) : (
                            <div className="success-state animate-bounce-in">
                                <CheckCircle size={60} color="#22c55e" strokeWidth={1.5} />
                                <h1 className="auth-title mt-4">Vérifiez vos e-mails</h1>
                                <p className="auth-subtitle">لقد أرسلنا تعليمات الاسترداد إلى بريدك</p>
                            </div>
                        )}
                    </div>

                    {!submitted ? (
                        <form className="auth-form" onSubmit={handleSubmit}>
                            <div className="auth-input-group">
                                <div className="input-wrap">
                                    <Mail className="field-icon" size={20} />
                                    <input type="email" placeholder="E-mail" required />
                                </div>
                            </div>

                            <button type="submit" className="auth-submit-btn">
                                Envoyer le lien <ArrowRight size={18} className="ml-2" />
                            </button>
                        </form>
                    ) : (
                        <div className="auth-extra text-center mt-6">
                            <p className="no-receive">Vous n'avez pas reçu l'e-mail ?</p>
                            <button className="resend-btn" onClick={() => setSubmitted(false)}>Renvoyer</button>
                        </div>
                    )}

                    <div className="auth-footer">
                        <Link to="/login" className="back-link">
                             Retour à la connexion
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ForgotPassword;
