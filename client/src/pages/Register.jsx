import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  User, 
  Mail, 
  Phone, 
  Lock, 
  Eye, 
  EyeOff, 
  MapPin, 
  Briefcase, 
  ChevronDown,
  ShieldCheck 
} from 'lucide-react';
import './Register.css';

const Register = () => {
    const navigate = useNavigate();
    const [showPass, setShowPass] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [specialty, setSpecialty] = useState('');

    const specialties = [
        'Généraliste',
        'Droit des Affaires',
        'Droit Pénal',
        'Droit de la Famille',
        'Droit du Travail',
        'Droit Immobilier',
        'Autre'
    ];

    const handleSubmit = (e) => {
        e.preventDefault();
        navigate('/login');
    };

    return (
        <div className="auth-page-wrapper">
            <div className="auth-bg-motif">
                <ShieldCheck size={500} strokeWidth={0.5} className="bg-icon" />
            </div>

            <div className="auth-container animate-fade-in">
                <div className="auth-card glass register">
                    <div className="auth-header">
                        <div className="auth-logo-box">
                            <span className="logo-icon">⚖️</span>
                            <span className="logo-name">GIVENX</span>
                            <span className="logo-tag">حقي</span>
                        </div>
                        <h1 className="auth-title">S'inscrire</h1>
                        <p className="auth-subtitle">انضم إلى مستقبل المحاماة الذكي</p>
                    </div>

                    <form className="auth-form" onSubmit={handleSubmit}>
                        <div className="auth-input-group">
                            <div className="input-wrap">
                                <User className="field-icon" size={18} />
                                <input type="text" placeholder="Nom complet" required />
                            </div>
                        </div>

                        <div className="auth-grid-split">
                            <div className="auth-input-group">
                                <div className="input-wrap">
                                    <Mail className="field-icon" size={18} />
                                    <input type="email" placeholder="E-mail" required />
                                </div>
                            </div>
                            <div className="auth-input-group">
                                <div className="input-wrap">
                                    <Phone className="field-icon" size={18} />
                                    <input type="tel" placeholder="Téléphone" required />
                                </div>
                            </div>
                        </div>

                        <div className="auth-grid-split">
                            <div className="auth-input-group">
                                <div className="input-wrap">
                                    <Lock className="field-icon" size={18} />
                                    <input type={showPass ? "text" : "password"} placeholder="Mot de passe" required />
                                    <button type="button" className="toggle-pass" onClick={() => setShowPass(!showPass)}>
                                        {showPass ? <EyeOff size={16} /> : <Eye size={16} />}
                                    </button>
                                </div>
                            </div>
                            <div className="auth-input-group">
                                <div className="input-wrap">
                                    <Lock className="field-icon" size={18} />
                                    <input type={showConfirm ? "text" : "password"} placeholder="Confirmé" required />
                                    <button type="button" className="toggle-pass" onClick={() => setShowConfirm(!showConfirm)}>
                                        {showConfirm ? <EyeOff size={16} /> : <Eye size={16} />}
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div className="auth-input-group">
                            <div className="input-wrap">
                                <MapPin className="field-icon" size={18} />
                                <input type="text" placeholder="Adresse complète" required />
                            </div>
                        </div>

                        <div className="auth-input-group">
                            <div className="input-wrap select-wrap">
                                <Briefcase className="field-icon" size={18} />
                                <select 
                                    value={specialty} 
                                    onChange={(e) => setSpecialty(e.target.value)}
                                    required
                                >
                                    <option value="" disabled selected>Sélectionnez votre spécialité</option>
                                    {specialties.map(s => <option key={s} value={s}>{s}</option>)}
                                </select>
                                <ChevronDown className="select-arrow" size={16} />
                            </div>
                        </div>

                        <button type="submit" className="auth-submit-btn">S'inscrire</button>
                    </form>

                    <div className="auth-footer">
                        <p>J'ai déjà un compte, <Link to="/login" className="cta-link">Connect</Link></p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Register;
