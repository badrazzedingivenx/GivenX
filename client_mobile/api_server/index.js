/**
 * GivenX REST API — Node.js / Express + MySQL
 * ─────────────────────────────────────────────
 * Endpoints:
 *
 *   POST /api/auth/login              – returns JWT + role
 *   POST /api/auth/register-user      – creates client account, returns JWT
 *   POST /api/auth/register-lawyer    – creates lawyer account, returns JWT
 *   GET  /api/auth/me                 – current user (protected)
 *
 *   GET  /api/lawyers/me              – authenticated lawyer profile (role=lawyer)
 *   GET  /api/lawyers/me/stats        – lawyer dashboard KPIs  (role=lawyer)
 *   GET  /api/lawyers                 – list (filter: ?domaine= &q= &page= &limit=)
 *   GET  /api/lawyers/:id             – single lawyer (protected)
 *
 *   POST /api/seed                    – DEV ONLY: inserts the two test accounts
 *
 * Test accounts (use POST /api/seed once to create them):
 *   CLIENT  tarik@example.com   / 123456
 *   LAWYER  yassine@example.com / 123456
 *
 * Setup:
 *   1. cp .env.example .env  and fill in your values
 *   2. npm install
 *   3. Run the SQL schema at the bottom of this file
 *   4. node index.js  (or: npm run dev)
 *   5. POST http://localhost:3000/api/seed   ← creates the two test users
 *
 * Required MySQL table structures are at the bottom of this file.
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
const PORT = process.env.PORT || 3000;

// ─── Security middleware ───────────────────────────────────────────────────────
app.use(helmet());
app.use(cors({ origin: '*' }));          // tighten origin in production
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
const JWT_SECRET     = process.env.JWT_SECRET;
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

/** Middleware: allow only users whose JWT role matches expectedRole */
function requireRole(expectedRole) {
    return (req, res, next) => {
        if (req.user?.role !== expectedRole) {
            return res.status(403).json({ error: 'Accès refusé — rôle insuffisant' });
        }
        next();
    };
}

// ─── Validation helper ────────────────────────────────────────────────────────
function validateRequest(req, res) {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        res.status(422).json({ error: 'Données invalides', details: errors.array() });
        return false;
    }
    return true;
}

// ──────────────────────────────────────────────────────────────────────────────
// Helpers — build the user/lawyer object sent to the Android app
// Field names are camelCase to match @SerializedName values in the DTOs.
// ──────────────────────────────────────────────────────────────────────────────

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
        specializations: u.specializations
            ? JSON.parse(u.specializations)
            : [],
        is_verified:     Boolean(u.is_verified),
        is_available:    Boolean(u.is_available ?? 1),
        rating:          parseFloat(u.rating   ?? 0),
        review_count:    u.review_count  ?? 0,
        client_count:    u.client_count  ?? 0,
        role:            'lawyer',
    };
}

// ══════════════════════════════════════════════════════════════════════════════
// AUTH ROUTES
// ══════════════════════════════════════════════════════════════════════════════

// POST /api/auth/register-user  (CLIENT)
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
                token,
                expires_in: 604800,
                user: formatUser({ id: userId, first_name, last_name, email, phone, address: '', photo_url: '', role: 'user' }),
            });
        } catch (err) {
            console.error('[register-user]', err);
            return res.status(500).json({ error: 'Erreur serveur' });
        }
    }
);

// POST /api/auth/register-lawyer  (LAWYER)
app.post('/api/auth/register-lawyer',
    body('email').isEmail().normalizeEmail(),
    body('password').isLength({ min: 6 }),
    body('first_name').trim().notEmpty(),
    body('last_name').trim().notEmpty(),
    body('speciality').trim().notEmpty(),
    body('bar_association').trim().notEmpty(),
    body('bar_number').trim().notEmpty(),
    async (req, res) => {
        if (!validateRequest(req, res)) return;
        const {
            first_name, last_name, email, password,
            phone = '', address = '', bio = '',
            speciality, bar_association, bar_number,
            years_experience = 0, specializations = [],
        } = req.body;
        try {
            const [rows] = await pool.query('SELECT id FROM users WHERE email = ?', [email]);
            if (rows.length > 0) return res.status(409).json({ error: 'Email déjà utilisé' });

            const hash = await bcrypt.hash(password, 12);
            const [result] = await pool.query(
                `INSERT INTO users
                   (first_name, last_name, email, password_hash, phone, address,
                    role, bio, speciality, bar_association, bar_number,
                    years_experience, specializations)
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
                [
                    first_name, last_name, email, hash, phone, address,
                    'lawyer', bio, speciality, bar_association, bar_number,
                    years_experience, JSON.stringify(specializations),
                ]
            );
            const userId = result.insertId.toString();
            const token  = signToken({ uid: userId, email, role: 'lawyer' });

            return res.status(201).json({
                token,
                expires_in: 604800,
                user: formatUser({ id: userId, first_name, last_name, email, phone, address, photo_url: '', role: 'lawyer' }),
            });
        } catch (err) {
            console.error('[register-lawyer]', err);
            return res.status(500).json({ error: 'Erreur serveur' });
        }
    }
);

// POST /api/auth/login    (CLIENT + LAWYER)
app.post('/api/auth/login',
    body('email').isEmail().normalizeEmail(),
    body('password').notEmpty(),
    async (req, res) => {
        if (!validateRequest(req, res)) return;
        const { email, password } = req.body;
        try {
            const [rows] = await pool.query(
                `SELECT id, first_name, last_name, email, password_hash,
                        phone, address, photo_url, role
                 FROM users WHERE email = ?`,
                [email]
            );
            if (rows.length === 0) return res.status(401).json({ error: 'Identifiants incorrects' });

            const u     = rows[0];
            const valid = await bcrypt.compare(password, u.password_hash);
            if (!valid) return res.status(401).json({ error: 'Identifiants incorrects' });

            const role  = u.role ?? 'user';
            const token = signToken({ uid: u.id.toString(), email: u.email, role });

            return res.json({
                token,
                expires_in: 604800,
                user: formatUser(u),
            });
        } catch (err) {
            console.error('[login]', err);
            return res.status(500).json({ error: 'Erreur serveur' });
        }
    }
);

// GET /api/auth/me  (protected — CLIENT + LAWYER)
app.get('/api/auth/me', authMiddleware, async (req, res) => {
    try {
        const [rows] = await pool.query(
            `SELECT id, first_name, last_name, email,
                    phone, address, photo_url, role
             FROM users WHERE id = ?`,
            [req.user.uid]
        );
        if (rows.length === 0) return res.status(404).json({ error: 'Utilisateur introuvable' });
        return res.json(formatUser(rows[0]));
    } catch (err) {
        console.error('[me]', err);
        return res.status(500).json({ error: 'Erreur serveur' });
    }
});

// ══════════════════════════════════════════════════════════════════════════════
// LAWYER PROFILE ROUTES  (role=lawyer only)
// ══════════════════════════════════════════════════════════════════════════════

// GET /api/lawyers/me  — full profile
app.get('/api/lawyers/me', authMiddleware, requireRole('lawyer'), async (req, res) => {
    try {
        const [rows] = await pool.query(
            `SELECT id, first_name, last_name, email, phone, address,
                    photo_url, bio, speciality, bar_association, bar_number,
                    years_experience, specializations, is_verified, is_available,
                    rating, review_count, client_count
             FROM users WHERE id = ?`,
            [req.user.uid]
        );
        if (rows.length === 0) return res.status(404).json({ error: 'Avocat introuvable' });
        return res.json(formatLawyerProfile(rows[0]));
    } catch (err) {
        console.error('[lawyers/me]', err);
        return res.status(500).json({ error: 'Erreur serveur' });
    }
});

// GET /api/lawyers/me/stats  — dashboard KPIs
app.get('/api/lawyers/me/stats', authMiddleware, requireRole('lawyer'), async (req, res) => {
    try {
        const [rows] = await pool.query(
            `SELECT client_count, rating, review_count FROM users WHERE id = ?`,
            [req.user.uid]
        );
        if (rows.length === 0) return res.status(404).json({ error: 'Avocat introuvable' });
        const u = rows[0];
        // Dummy but consistent stats — replace with real queries when the
        // dossiers / appointments tables are available.
        return res.json({
            total_clients:       u.client_count  ?? 0,
            active_clients:      u.client_count  ?? 0,
            audiences_today:     2,
            new_requests:        3,
            closed_cases:        u.client_count  ?? 0,
            total_revenue_month: 18500.0,
            total_revenue_year:  142000.0,
            average_rating:      parseFloat(u.rating ?? 0),
        });
    } catch (err) {
        console.error('[lawyers/me/stats]', err);
        return res.status(500).json({ error: 'Erreur serveur' });
    }
});

// ══════════════════════════════════════════════════════════════════════════════
// LAWYER DISCOVERY ROUTES  (public listing — role=user)
// ══════════════════════════════════════════════════════════════════════════════

// GET /api/lawyers
app.get('/api/lawyers',
    query('page').optional().isInt({ min: 1 }).toInt(),
    query('limit').optional().isInt({ min: 1, max: 100 }).toInt(),
    async (req, res) => {
        const domaine = req.query.domaine?.toString().trim() || null;
        const search  = req.query.q?.toString().trim()      || null;
        const page    = parseInt(req.query.page,  10) || 1;
        const limit   = parseInt(req.query.limit, 10) || 50;
        const offset  = (page - 1) * limit;

        try {
            let whereClauses = ['role = ?'];
            let params       = ['lawyer'];

            if (domaine) {
                whereClauses.push('speciality = ?');
                params.push(domaine);
            }
            if (search) {
                whereClauses.push('(CONCAT(first_name, " ", last_name) LIKE ? OR speciality LIKE ?)');
                params.push(`%${search}%`, `%${search}%`);
            }

            const whereSQL = 'WHERE ' + whereClauses.join(' AND ');

            const [rows] = await pool.query(
                `SELECT id, first_name, last_name, speciality,
                        address, years_experience, rating,
                        review_count, bio, is_verified, is_available
                 FROM users ${whereSQL}
                 ORDER BY rating DESC
                 LIMIT ? OFFSET ?`,
                [...params, limit, offset]
            );

            const [[{ total }]] = await pool.query(
                `SELECT COUNT(*) AS total FROM users ${whereSQL}`,
                params
            );

            return res.json({
                data:  rows.map(r => ({
                    id:              r.id.toString(),
                    name:            `${r.first_name} ${r.last_name}`.trim(),
                    specialty:       r.speciality    ?? '',
                    location:        r.address       ?? '',
                    experience:      r.years_experience ?? 0,
                    rating:          parseFloat(r.rating ?? 0),
                    compatibility:   0,
                    review_count:    r.review_count  ?? 0,
                    bio:             r.bio           ?? '',
                    is_verified:     Boolean(r.is_verified),
                    domaine:         r.speciality    ?? '',
                })),
                total,
            });
        } catch (err) {
            console.error('[lawyers list]', err);
            return res.status(500).json({ error: 'Erreur serveur' });
        }
    }
);

// GET /api/lawyers/:id  (protected)
app.get('/api/lawyers/:id',
    authMiddleware,
    param('id').isInt({ min: 1 }).toInt(),
    async (req, res) => {
        if (!validateRequest(req, res)) return;
        try {
            const [rows] = await pool.query(
                `SELECT id, first_name, last_name, speciality,
                        address, years_experience, rating,
                        review_count, bio, is_verified, is_available
                 FROM users WHERE id = ? AND role = 'lawyer'`,
                [req.params.id]
            );
            if (rows.length === 0) return res.status(404).json({ error: 'Avocat introuvable' });
            const r = rows[0];
            return res.json({
                id:          r.id.toString(),
                name:        `${r.first_name} ${r.last_name}`.trim(),
                specialty:   r.speciality    ?? '',
                location:    r.address       ?? '',
                experience:  r.years_experience ?? 0,
                rating:      parseFloat(r.rating ?? 0),
                compatibility: 0,
                review_count: r.review_count ?? 0,
                bio:         r.bio           ?? '',
                is_verified: Boolean(r.is_verified),
                domaine:     r.speciality    ?? '',
            });
        } catch (err) {
            console.error('[lawyer by id]', err);
            return res.status(500).json({ error: 'Erreur serveur' });
        }
    }
);

// ══════════════════════════════════════════════════════════════════════════════
// SEED ROUTE  — DEV ONLY: inserts the two test accounts
// ══════════════════════════════════════════════════════════════════════════════
app.post('/api/seed', async (req, res) => {
    if (process.env.NODE_ENV === 'production') {
        return res.status(403).json({ error: 'Désactivé en production' });
    }
    try {
        const clientHash = await bcrypt.hash('123456', 12);
        const lawyerHash = await bcrypt.hash('123456', 12);

        // CLIENT — tarik@example.com
        await pool.query(
            `INSERT INTO users
               (first_name, last_name, email, password_hash, phone, role)
             VALUES ('Tarik', 'Haq', 'tarik@example.com', ?, '+212600000001', 'user')
             ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash), role = 'user'`,
            [clientHash]
        );

        // LAWYER — yassine@example.com
        await pool.query(
            `INSERT INTO users
               (first_name, last_name, email, password_hash, phone,
                role, speciality, bar_association, bar_number,
                years_experience, specializations,
                bio, is_verified, is_available, rating, review_count, client_count)
             VALUES (
               'Yassine', 'El Amrani', 'yassine@example.com', ?, '+212661234567',
               'lawyer', 'Droit Pénal', 'Casablanca', 'C-2541',
               12, '["Droit Pénal","Droit Civil","Droit des Affaires"]',
               'Maître El Amrani est spécialisé en droit pénal avec plus de 12 ans d\'expérience.',
               1, 1, 4.9, 87, 28
             )
             ON DUPLICATE KEY UPDATE
               password_hash = VALUES(password_hash),
               role = 'lawyer',
               speciality = VALUES(speciality)`,
            [lawyerHash]
        );

        return res.json({ message: 'Comptes de test créés avec succès.' });
    } catch (err) {
        console.error('[seed]', err);
        return res.status(500).json({ error: 'Erreur lors du seed', detail: err.message });
    }
});

// ─── Start ────────────────────────────────────────────────────────────────────
app.listen(PORT, () => console.log(`GivenX API running on port ${PORT}`));

/*
 * ═══════════════════════════════════════════════════════════════════════════
 * MySQL SCHEMA (run once)
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * CREATE TABLE users (
 *     id                 INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
 *     first_name         VARCHAR(100)  NOT NULL,
 *     last_name          VARCHAR(100)  NOT NULL,
 *     email              VARCHAR(255)  NOT NULL UNIQUE,
 *     password_hash      VARCHAR(255)  NOT NULL,
 *     phone              VARCHAR(30)   DEFAULT '',
 *     address            TEXT          DEFAULT '',
 *     photo_url          TEXT          DEFAULT '',
 *     role               ENUM('user','lawyer') NOT NULL DEFAULT 'user',
 *
 *     -- Lawyer-specific fields (NULL for clients)
 *     bio                TEXT          DEFAULT NULL,
 *     speciality         VARCHAR(150)  DEFAULT NULL,
 *     bar_association    VARCHAR(150)  DEFAULT NULL,
 *     bar_number         VARCHAR(50)   DEFAULT NULL,
 *     years_experience   TINYINT UNSIGNED DEFAULT 0,
 *     specializations    JSON          DEFAULT NULL,   -- e.g. ["Droit Pénal","Droit Civil"]
 *     is_verified        TINYINT(1)    DEFAULT 0,
 *     is_available       TINYINT(1)    DEFAULT 1,
 *     rating             DECIMAL(3,1)  DEFAULT 0.0,
 *     review_count       SMALLINT UNSIGNED DEFAULT 0,
 *     client_count       SMALLINT UNSIGNED DEFAULT 0,
 *
 *     created_at         TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
 * );
 */
