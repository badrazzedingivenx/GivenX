const jsonServer = require('json-server');
const server = jsonServer.create();
const router = jsonServer.router('db.json');
const middlewares = jsonServer.defaults();

server.use(jsonServer.bodyParser);
server.use(middlewares);

// Custom Middleware باش نغلفو كاع الـ Responses بـ ApiResponse
router.render = (req, res) => {
    if (res.locals.data && res.locals.data.success !== undefined) {
        res.jsonp(res.locals.data);
    } else {
        res.jsonp({
            success: true,
            data: res.locals.data,
            message: "OK",
            total: Array.isArray(res.locals.data) ? res.locals.data.length : (res.locals.data ? 1 : 0)
        });
    }
};

// ── Auth & Profile ────────────────────────────────────────────────────────

server.post('/api/auth/login', (req, res) => {
    const { email, password } = req.body;
    const db = router.db;
    const user = db.get('users').find({ email, password }).value();

    if (user) {
        res.jsonp({
            success: true,
            data: {
                token: "mock-jwt-token-" + user.id,
                user: { ...user, fullName: user.email.split('@')[0] }
            }
        });
    } else {
        res.status(401).jsonp({ success: false, message: "Identifiants invalides" });
    }
});

// GET /api/auth/me - Authenticated user's profile
server.get('/api/auth/me', (req, res) => {
    const db = router.db;
    const user = db.get('users').find({ id: 6 }).value(); // Mocking as Alice (Client)
    const profile = db.get('profiles').find({ userId: 6 }).value();

    res.jsonp({
        success: true,
        data: {
            id: user.id,
            email: user.email,
            role: user.role,
            fullName: profile.full_name,
            phone: profile.phone,
            address: profile.address,
            avatarUrl: profile.avatar_url
        }
    });
});

// ── Lawyer Dashboard Endpoints ─────────────────────────────────────────────

server.get('/api/lawyers/me', (req, res) => {
    const db = router.db;
    const lawyer = db.get('lawyers').find({ id: 1 }).value();
    const profile = db.get('profiles').find({ id: lawyer.profileId }).value();

    res.jsonp({
        success: true,
        data: {
            ...lawyer,
            fullName: profile.full_name,
            email: "jean.dupont@law.com",
            phone: profile.phone,
            avatarUrl: profile.avatar_url,
            address: profile.address
        }
    });
});

server.get('/api/lawyers/me/stats', (req, res) => {
    res.jsonp({
        success: true,
        data: {
            total_clients: 125,
            active_clients: 45,
            new_requests: 8,
            closed_cases: 12,
            total_revenue_month: 15400,
            average_rating: 4.8,
            monthly_revenue: [
                { month: "Jan", amount: 12000 }, { month: "Feb", amount: 15000 },
                { month: "Mar", amount: 13500 }, { month: "Apr", amount: 18000 },
                { month: "May", amount: 14000 }, { month: "Jun", amount: 15400 }
            ]
        }
    });
});

server.get('/api/avocat/consultations/recent', (req, res) => {
    const db = router.db;
    const list = db.get('consultations').value().map(c => ({
        id: c.id,
        clientName: "Client " + c.clientId,
        date: c.date,
        status: c.status === "accepted" ? "terminé" : "en attente",
        subject: c.subject,
        type: "Vidéo",
        time: "10:30 AM"
    }));
    res.jsonp({ success: true, data: list });
});

// ── User / Client Specific Endpoints ────────────────────────────────────────

// GET /api/appointments/me
server.get('/api/appointments/me', (req, res) => {
    res.jsonp({
        success: true,
        data: [
            { id: 1, title: "Consultation Immobilière", lawyerName: "Jean Dupont", date: "2024-06-25", time: "14:00", status: "Confirmed" },
            { id: 2, title: "Conseil Juridique", lawyerName: "Marie Legrand", date: "2024-06-28", time: "10:30", status: "Pending" }
        ]
    });
});

// GET /api/billing/me
server.get('/api/billing/me', (req, res) => {
    res.jsonp({
        success: true,
        data: {
            balance: "1,250.00",
            currency: "DH",
            invoices: [
                { id: "INV-001", amount: "500.00", status: "Paid", date: "2024-05-15" },
                { id: "INV-002", amount: "750.00", status: "Unpaid", date: "2024-06-01" }
            ]
        }
    });
});

// GET /api/documents/vault
server.get('/api/documents/vault', (req, res) => {
    const db = router.db;
    res.jsonp({
        success: true,
        data: db.get('documents').value()
    });
});

// GET /api/dossiers/me
server.get('/api/dossiers/me', (req, res) => {
    const db = router.db;
    res.jsonp({
        success: true,
        data: db.get('dossiers').value()
    });
});

// ── Common Content ─────────────────────────────────────────────────────────

server.get('/api/notifications', (req, res) => {
    const db = router.db;
    res.jsonp({ success: true, data: db.get('notifications').value() });
});

server.get('/api/stories', (req, res) => {
    const db = router.db;
    res.jsonp({ success: true, data: db.get('stories').value() });
});

server.get('/api/reels', (req, res) => {
    const db = router.db;
    res.jsonp({ success: true, data: db.get('reels').value() });
});

server.get('/api/lives', (req, res) => {
    const db = router.db;
    res.jsonp({ success: true, data: db.get('lives').value() });
});

// Use default router for other /api routes
server.use('/api', router);

server.listen(3001, () => {
    console.log('JSON Server with User/Lawyer routes running on port 3001');
});