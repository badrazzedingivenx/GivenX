import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, Eye, EyeOff, ShieldCheck } from 'lucide-react';
import './Login.css';

const Login = () => {
    const navigate = useNavigate();
    const [showPassword, setShowPassword] = useState(false);

    const handleSubmit = (e) => {
        e.preventDefault();
        // Mock login
        localStorage.setItem('token', 'premium_session_active');
        navigate('/dashboard');
    };

    return (
        <div className="auth-page-wrapper">
            {/* Background Branding Elements */}
            <div className="auth-bg-motif">
                <ShieldCheck size={500} strokeWidth={0.5} className="bg-icon" />
            </div>

            <div className="auth-container animate-fade-in">
                <div className="auth-card glass">
                    {/* Header: Logo & Title */}
                    <div className="auth-header">
                        <div className="auth-logo-box">
                            <span className="logo-icon">⚖️</span>
                            <span className="logo-name">GIVENX</span>
                            <span className="logo-tag">حقي</span>
                        </div>
                        <h1 className="auth-title">Connexion</h1>
                        <p className="auth-subtitle">استمر في رحلتك القانونية الذكية</p>
                    </div>

                    {/* Form */}
                    <form className="auth-form" onSubmit={handleSubmit}>
                        <div className="auth-input-group">
                            <div className="input-wrap">
                                <Mail className="field-icon" size={20} />
                                <input type="email" placeholder="E-mail" required />
                            </div>
                        </div>

                        <div className="auth-input-group">
                            <div className="input-wrap">
                                <Lock className="field-icon" size={20} />
                                <input 
                                    type={showPassword ? "text" : "password"} 
                                    placeholder="Mot de passe" 
                                    required 
                                />
                                <button 
                                    type="button" 
                                    className="toggle-pass" 
                                    onClick={() => setShowPassword(!showPassword)}
                                >
                                    {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                                </button>
                            </div>
                        </div>

                        <div className="auth-extra">
                            <Link to="/forgot-password" className="forgot-link">Mot de passe oublié ?</Link>
                        </div>

                        <button type="submit" className="auth-submit-btn">Se connecter</button>
                    </form>

                    {/* Footer */}
                    <div className="auth-footer">
                        <p>Si vous n'avez pas de compte, <Link to="/register" className="cta-link">Créez un compte</Link></p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;
