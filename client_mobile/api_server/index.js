/**
 * GivenX REST API — Node.js / Express + MySQL
 * ─────────────────────────────────────────────
 * Clean, production-like endpoints hitting MySQL database.
 */

'use strict';

require('dotenv').config();

const express        = require('express');
const mysql          = require('mysql2/promise');
const bcrypt         = require('bcryptjs');
const jwt            = require('jsonwebtoken');
const helmet         = require('helmet');
const cors           = require('cors');
const rateLimit      = require('express-rate-limit');
const { body, param, query, validationResult } = require('express-validator');

const app  = express();
const PORT = process.env.PORT || 3001; // Standardized to 3001 as per RetrofitClient

// ─── Security middleware ───────────────────────────────────────────────────────
app.use(helmet());
app.use(cors({ origin: '*' }));
app.use(express.json({ limit: '10kb' }));

const limiter = rateLimit({ windowMs: 15 * 60 * 1000, max: 100 });
app.use('/api/', limiter);

// ─── MySQL connection pool ─────────────────────────────────────────────────────
const pool = mysql.createPool({
    host:               process.env.DB_HOST,
    port:               parseInt(process.env.DB_PORT, 10) || 3306,
    database:           process.env.DB_NAME,
    user:               process.env.DB_USER,
    password:           process.env.DB_PASSWORD,
    waitForConnections: true,
    connectionLimit:    10,
    queueLimit:         0,
});

// ─── JWT helpers ──────────────────────────────────────────────────────────────
const JWT_SECRET     = process.env.JWT_SECRET || 'your_secret_key';
const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || '7d';

function signToken(payload) {
    return jwt.sign(payload, JWT_SECRET, { expiresIn: JWT_EXPIRES_IN });
}

function authMiddleware(req, res, next) {
    const header = req.headers.authorization;
    if (!header || !header.startsWith('Bearer ')) {
        return res.status(401).json({ error: 'Token manquant' });
    }
    const token = header.split(' ')[1];
    try {
        req.user = jwt.verify(token, JWT_SECRET);
        next();
    } catch (err) {
        return res.status(401).json({ error: 'Token invalide ou expiré' });
    }
}

function requireRole(expectedRole) {
    return (req, res, next) => {
        if (req.user?.role !== expectedRole) {
            return res.status(403).json({ error: 'Accès refusé — rôle insuffisant' });
        }
        next();
    };
}

function validateRequest(req, res) {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        res.status(422).json({ error: 'Données invalides', details: errors.array() });
        return false;
    }
    return true;
}

// ─── Formatters ──────────────────────────────────────────────────────────────

function formatUser(u) {
    return {
        id:        u.id.toString(),
        firstName: u.first_name,
        lastName:  u.last_name,
        email:     u.email,
        phone:     u.phone      ?? '',
        address:   u.address    ?? '',
        photoUrl:  u.photo_url  ?? '',
        role:      u.role       ?? 'user',
    };
}

function formatLawyerProfile(u) {
    return {
        id:              u.id.toString(),
        full_name:       `${u.first_name} ${u.last_name}`.trim(),
        email:           u.email,
        phone:           u.phone         ?? '',
        address:         u.address       ?? '',
        avatar_url:      u.photo_url     ?? '',
        bio:             u.bio           ?? '',
        speciality:      u.speciality    ?? '',
        bar_association: u.bar_association ?? '',
        bar_number:      u.bar_number    ?? '',
        years_experience: u.years_experience ?? 0,
        specializations: u.specializations ? JSON.parse(u.specializations) : [],
        is_verified:     Boolean(u.is_verified),
        is_available:    Boolean(u.is_available ?? 1),
        rating:          parseFloat(u.rating   ?? 0),
        review_count:    u.review_count  ?? 0,
        client_count:    u.client_count  ?? 0,
        role:            'lawyer',
    };
}

// ══════════════════════════════════════════════════════════════════════════════
// ROUTES
// ══════════════════════════════════════════════════════════════════════════════

// Register User (Client)
app.post('/api/auth/register-user',
    body('email').isEmail().normalizeEmail(),
    body('password').isLength({ min: 6 }),
    body('first_name').trim().notEmpty(),
    body('last_name').trim().notEmpty(),
    async (req, res) => {
        if (!validateRequest(req, res)) return;
        const { first_name, last_name, email, password, phone = '' } = req.body;
        try {
            const [rows] = await pool.query('SELECT id FROM users WHERE email = ?', [email]);
            if (rows.length > 0) return res.status(409).json({ error: 'Email déjà utilisé' });

            const hash = await bcrypt.hash(password, 12);
            const [result] = await pool.query(
                'INSERT INTO users (first_name, last_name, email, password_hash, phone, role) VALUES (?, ?, ?, ?, ?, ?)',
                [first_name, last_name, email, hash, phone, 'user']
            );
            const userId = result.insertId.toString();
            const token  = signToken({ uid: userId, email, role: 'user' });

            return res.status(201).json({
                success: true,
                token,
                user: formatUser({ id: userId, first_name, last_name, email, phone, role: 'user' })
            });
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Erreur serveur' });
        }
    }
);

// Register Lawyer
app.post('/api/auth/register-lawyer',
    body('email').isEmail().normalizeEmail(),
    body('password').isLength({ min: 6 }),
    body('first_name').trim().notEmpty(),
    body('last_name').trim().notEmpty(),
    body('speciality').notEmpty(),
    async (req, res) => {
        if (!validateRequest(req, res)) return;
        const { first_name, last_name, email, password, speciality, phone = '', bar_association = '', bar_number = '' } = req.body;
        try {
            const [rows] = await pool.query('SELECT id FROM users WHERE email = ?', [email]);
            if (rows.length > 0) return res.status(409).json({ error: 'Email déjà utilisé' });

            const hash = await bcrypt.hash(password, 12);
            const [result] = await pool.query(
                'INSERT INTO users (first_name, last_name, email, password_hash, phone, role, speciality, bar_association, bar_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)',
                [first_name, last_name, email, hash, phone, 'lawyer', speciality, bar_association, bar_number]
            );
            const userId = result.insertId.toString();
            const token  = signToken({ uid: userId, email, role: 'lawyer' });

            return res.status(201).json({
                success: true,
                token,
                user: formatUser({ id: userId, first_name, last_name, email, phone, role: 'lawyer' })
            });
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Erreur serveur' });
        }
    }
);

// Login
app.post('/api/auth/login',
    body('email').isEmail().normalizeEmail(),
    body('password').notEmpty(),
    async (req, res) => {
        if (!validateRequest(req, res)) return;
        const { email, password } = req.body;
        try {
            const [rows] = await pool.query('SELECT * FROM users WHERE email = ?', [email]);
            if (rows.length === 0) return res.status(401).json({ error: 'Identifiants incorrects' });

            const user = rows[0];
            const valid = await bcrypt.compare(password, user.password_hash);
            if (!valid) return res.status(401).json({ error: 'Identifiants incorrects' });

            const token = signToken({ uid: user.id.toString(), email: user.email, role: user.role });
            res.json({
                success: true,
                token,
                role: user.role,
                user: formatUser(user)
            });
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Erreur serveur' });
        }
    }
);

// Get Me
app.get('/api/auth/me', authMiddleware, async (req, res) => {
    try {
        const [rows] = await pool.query('SELECT * FROM users WHERE id = ?', [req.user.uid]);
        if (rows.length === 0) return res.status(404).json({ error: 'Utilisateur introuvable' });
        res.json({ success: true, data: formatUser(rows[0]) });
    } catch (err) {
        res.status(500).json({ error: 'Erreur serveur' });
    }
});

// Lawyers List
app.get('/api/lawyers', async (req, res) => {
    const { domaine, q } = req.query;
    try {
        let sql = "SELECT * FROM users WHERE role = 'lawyer'";
        const params = [];
        if (domaine) { sql += " AND speciality = ?"; params.push(domaine); }
        if (q) { sql += " AND (first_name LIKE ? OR last_name LIKE ?)"; params.push(`%${q}%`, `%${q}%`); }

        const [rows] = await pool.query(sql, params);
        res.json({ success: true, data: rows.map(r => ({
            id:            r.id.toString(),
            name:          `${r.first_name ?? ''} ${r.last_name ?? ''}`.trim(),
            full_name:     `${r.first_name ?? ''} ${r.last_name ?? ''}`.trim(),
            specialty:     r.speciality   ?? '',
            speciality:    r.speciality   ?? '',
            domaine:       r.domaine      ?? r.speciality ?? '',
            location:      r.city         ?? r.address    ?? '',
            city:          r.city         ?? '',
            avatar_url:    r.photo_url    ?? r.avatar_url ?? '',
            avatarUrl:     r.photo_url    ?? r.avatar_url ?? '',
            rating:        parseFloat(r.rating       ?? 0),
            review_count:  r.review_count ?? 0,
            reviewCount:   r.review_count ?? 0,
            experience:    r.years_experience ?? r.experience ?? 0,
            years_experience: r.years_experience ?? 0,
            is_verified:   !!r.is_verified,
            isVerified:    !!r.is_verified,
            is_available:  r.is_available !== undefined ? !!r.is_available : true,
            bio:           r.bio          ?? '',
            status:        r.status       ?? ''
        })) });
    } catch (err) {
        res.status(500).json({ error: 'Erreur serveur' });
    }
});

// Lawyer Profile
app.get('/api/lawyers/me', authMiddleware, requireRole('lawyer'), async (req, res) => {
    try {
        const [rows] = await pool.query('SELECT * FROM users WHERE id = ?', [req.user.uid]);
        res.json({ success: true, data: formatLawyerProfile(rows[0]) });
    } catch (err) {
        res.status(500).json({ error: 'Erreur serveur' });
    }
});

// Lawyer Stats
app.get('/api/lawyers/me/stats', authMiddleware, requireRole('lawyer'), async (req, res) => {
    try {
        const [rows] = await pool.query('SELECT client_count, rating, review_count FROM users WHERE id = ?', [req.user.uid]);
        const u = rows[0];
        res.json({
            success: true,
            data: {
                total_clients: u.client_count,
                average_rating: parseFloat(u.rating),
                review_count: u.review_count,
                active_cases: 5, // Simplified
                revenue_this_month: 15000
            }
        });
    } catch (err) {
        res.status(500).json({ error: 'Erreur serveur' });
    }
});

// Seed
app.post('/api/seed', async (req, res) => {
    try {
        const hash = await bcrypt.hash('123456', 12);
        await pool.query("INSERT IGNORE INTO users (first_name, last_name, email, password_hash, role) VALUES ('Tarik', 'Haq', 'tarik@example.com', ?, 'user')", [hash]);
        await pool.query("INSERT IGNORE INTO users (first_name, last_name, email, password_hash, role, speciality) VALUES ('Yassine', 'Lawyer', 'yassine@example.com', ?, 'lawyer', 'Droit Pénal')", [hash]);
        res.json({ success: true, message: "Seed complete" });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Payments
app.get('/api/payments', authMiddleware, async (req, res) => {
    const { clientId, lawyerId } = req.query;
    try {
        let sql = 'SELECT * FROM payments WHERE 1=1';
        const params = [];

        if (clientId) {
            sql += ' AND client_id = ?';
            params.push(clientId);
        }
        if (lawyerId) {
            sql += ' AND lawyer_id = ?';
            params.push(lawyerId);
        }

        // If neither provided, and not admin, restrict to own payments
        if (!clientId && !lawyerId) {
            if (req.user.role === 'lawyer') {
                sql += ' AND lawyer_id = ?';
                params.push(req.user.uid);
            } else {
                sql += ' AND client_id = ?';
                params.push(req.user.uid);
            }
        }

        const [rows] = await pool.query(sql, params);
        res.json({
            success: true,
            data: rows.map(r => ({
                id: r.id.toString(),
                clientId: r.client_id,
                lawyerId: r.lawyer_id,
                date: r.date,
                amount: r.amount,
                status: r.status,
                subject: r.subject,
                method: r.method
            }))
        });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Erreur serveur' });
    }
});

app.listen(PORT, () => console.log(`API running on port ${PORT}`));
