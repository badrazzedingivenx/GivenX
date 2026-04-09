/**
 * GivenX REST API — Node.js / Express + MySQL
 * ─────────────────────────────────────────────
 * Endpoints:
 *
 *   POST /api/auth/login           – returns JWT
 *   POST /api/auth/register        – creates user, returns JWT
 *   GET  /api/auth/me              – current user (protected)
 *
 *   GET  /api/lawyers              – list (filter: ?domaine= &q= &page= &limit=)
 *   GET  /api/lawyers/:id          – single lawyer (protected)
 *
 * Setup:
 *   1. cp .env.example .env  and fill in your values
 *   2. npm install
 *   3. Run the SQL in schema.sql to create the tables
 *   4. node index.js   (or: npm run dev)
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

// ─── Validation helper ────────────────────────────────────────────────────────
function validateRequest(req, res) {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        res.status(422).json({ error: 'Données invalides', details: errors.array() });
        return false;
    }
    return true;
}

// ══════════════════════════════════════════════════════════════════════════════
// AUTH ROUTES
// ══════════════════════════════════════════════════════════════════════════════

// POST /api/auth/register
app.post('/api/auth/register',
    body('email').isEmail().normalizeEmail(),
    body('password').isLength({ min: 8 }),
    body('first_name').trim().notEmpty(),
    body('last_name').trim().notEmpty(),
    async (req, res) => {
        if (!validateRequest(req, res)) return;

        const { first_name, last_name, email, password, phone = '' } = req.body;
        try {
            const [rows] = await pool.query('SELECT id FROM users WHERE email = ?', [email]);
            if (rows.length > 0) {
                return res.status(409).json({ error: 'Email déjà utilisé' });
            }

            const hash   = await bcrypt.hash(password, 12);
            const [result] = await pool.query(
                'INSERT INTO users (first_name, last_name, email, password_hash, phone) VALUES (?, ?, ?, ?, ?)',
                [first_name, last_name, email, hash, phone]
            );

            const userId = result.insertId.toString();
            const token  = signToken({ uid: userId, email });

            return res.status(201).json({
                token,
                expires_in: 604800,
                user: { id: userId, first_name, last_name, email, phone, address: '', photo_url: '' }
            });
        } catch (err) {
            console.error('[register]', err);
            return res.status(500).json({ error: 'Erreur serveur' });
        }
    }
);

// POST /api/auth/login
app.post('/api/auth/login',
    body('email').isEmail().normalizeEmail(),
    body('password').notEmpty(),
    async (req, res) => {
        if (!validateRequest(req, res)) return;

        const { email, password } = req.body;
        try {
            const [rows] = await pool.query(
                'SELECT id, first_name, last_name, email, password_hash, phone, address, photo_url FROM users WHERE email = ?',
                [email]
            );
            if (rows.length === 0) {
                return res.status(401).json({ error: 'Identifiants incorrects' });
            }

            const user = rows[0];
            const valid = await bcrypt.compare(password, user.password_hash);
            if (!valid) {
                return res.status(401).json({ error: 'Identifiants incorrects' });
            }

            const token = signToken({ uid: user.id.toString(), email: user.email });

            return res.json({
                token,
                expires_in: 604800,
                user: {
                    id:         user.id.toString(),
                    first_name: user.first_name,
                    last_name:  user.last_name,
                    email:      user.email,
                    phone:      user.phone      ?? '',
                    address:    user.address    ?? '',
                    photo_url:  user.photo_url  ?? '',
                }
            });
        } catch (err) {
            console.error('[login]', err);
            return res.status(500).json({ error: 'Erreur serveur' });
        }
    }
);

// GET /api/auth/me  (protected)
app.get('/api/auth/me', authMiddleware, async (req, res) => {
    try {
        const [rows] = await pool.query(
            'SELECT id, first_name, last_name, email, phone, address, photo_url FROM users WHERE id = ?',
            [req.user.uid]
        );
        if (rows.length === 0) return res.status(404).json({ error: 'Utilisateur introuvable' });

        const u = rows[0];
        return res.json({
            id:         u.id.toString(),
            first_name: u.first_name,
            last_name:  u.last_name,
            email:      u.email,
            phone:      u.phone     ?? '',
            address:    u.address   ?? '',
            photo_url:  u.photo_url ?? '',
        });
    } catch (err) {
        console.error('[me]', err);
        return res.status(500).json({ error: 'Erreur serveur' });
    }
});

// ══════════════════════════════════════════════════════════════════════════════
// LAWYER ROUTES
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
            let whereClauses = [];
            let params       = [];

            if (domaine) {
                whereClauses.push('domaine = ?');
                params.push(domaine);
            }
            if (search) {
                whereClauses.push('(name LIKE ? OR specialty LIKE ?)');
                params.push(`%${search}%`, `%${search}%`);
            }

            const whereSQL = whereClauses.length ? 'WHERE ' + whereClauses.join(' AND ') : '';

            const [rows]  = await pool.query(
                `SELECT id, name, specialty, location, experience, rating,
                        compatibility, review_count, bio, is_verified, domaine
                 FROM lawyers ${whereSQL}
                 ORDER BY rating DESC
                 LIMIT ? OFFSET ?`,
                [...params, limit, offset]
            );

            const [[{ total }]] = await pool.query(
                `SELECT COUNT(*) AS total FROM lawyers ${whereSQL}`,
                params
            );

            return res.json({
                data:  rows.map(formatLawyer),
                total: total,
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
                `SELECT id, name, specialty, location, experience, rating,
                        compatibility, review_count, bio, is_verified, domaine
                 FROM lawyers WHERE id = ?`,
                [req.params.id]
            );
            if (rows.length === 0) return res.status(404).json({ error: 'Avocat introuvable' });
            return res.json(formatLawyer(rows[0]));
        } catch (err) {
            console.error('[lawyer by id]', err);
            return res.status(500).json({ error: 'Erreur serveur' });
        }
    }
);

// ─── Row formatter (snake_case DB → camelCase for Android Gson) ───────────────
function formatLawyer(row) {
    return {
        id:            row.id.toString(),
        name:          row.name,
        specialty:     row.specialty,
        location:      row.location,
        experience:    row.experience,
        rating:        parseFloat(row.rating),
        compatibility: row.compatibility,
        review_count:  row.review_count,
        bio:           row.bio           ?? '',
        is_verified:   Boolean(row.is_verified),
        domaine:       row.domaine        ?? row.specialty,
    };
}

// ─── Start ────────────────────────────────────────────────────────────────────
app.listen(PORT, () => console.log(`GivenX API running on port ${PORT}`));

/*
 * ═══════════════════════════════════════════════════════════════════════════
 * MySQL SCHEMA (run once)
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * CREATE TABLE users (
 *     id             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
 *     first_name     VARCHAR(100)  NOT NULL,
 *     last_name      VARCHAR(100)  NOT NULL,
 *     email          VARCHAR(255)  NOT NULL UNIQUE,
 *     password_hash  VARCHAR(255)  NOT NULL,
 *     phone          VARCHAR(30)   DEFAULT '',
 *     address        TEXT          DEFAULT '',
 *     photo_url      TEXT          DEFAULT '',
 *     created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
 * );
 *
 * CREATE TABLE lawyers (
 *     id             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
 *     name           VARCHAR(150)  NOT NULL,
 *     specialty      VARCHAR(150)  NOT NULL,
 *     domaine        VARCHAR(150)  NOT NULL,
 *     location       VARCHAR(150)  DEFAULT '',
 *     experience     TINYINT UNSIGNED DEFAULT 0,
 *     rating         DECIMAL(3,1)  DEFAULT 0.0,
 *     compatibility  TINYINT UNSIGNED DEFAULT 0,
 *     review_count   SMALLINT UNSIGNED DEFAULT 0,
 *     bio            TEXT          DEFAULT '',
 *     is_verified    TINYINT(1)    DEFAULT 1,
 *     created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
 * );
 */
