const jsonServer = require('json-server');
const server = jsonServer.create();
const router = jsonServer.router('db.json');
const middlewares = jsonServer.defaults();

server.use(jsonServer.bodyParser);
server.use(middlewares);

// Logger l-m-tewwer: kiy-affichi l-JSON li rje3 m3a l-status
server.use((req, res, next) => {
  // Kan-akhdo l-copy dial l-fonction original bach ma-n-khesrouhach
  const oldSend = res.send;

  // Kan-bedlo res.send bach n-cheffo l-data mlli t-koun kharja
  res.send = function (data) {
    console.log("\n" + "┈".repeat(40)); // Line bach n-farqo bin les logs
    console.log(`[${new Date().toLocaleTimeString()}] ${req.method} ${req.originalUrl}`);
    
    // 1. Check dial l-Status
    if (res.statusCode >= 200 && res.statusCode < 300) {
      console.log(`Status: ✅ ${res.statusCode} OK`);
    } else {
      console.log(`Status: ❌ ${res.statusCode} Error`);
    }

    // 2. Affichagi dial l-JSON (l-data li jab l-API)
    if (data) {
      try {
        const body = JSON.parse(data);
        console.log("Response Body:");
        console.log(JSON.stringify(body, null, 2)); // null, 2 kat-khallih y-ban m-sttef (Pretty Print)
      } catch (e) {
        // Ila makanch JSON (mathalan string)
        console.log("Response Body:", data);
      }
    }

    console.log("┈".repeat(40) + "\n");
    
    // Darori n-rej3o l-fonction l-aslha bach res.send t-kemmel khdmtha l-app dialek
    return oldSend.apply(res, arguments);
  };

  next();
});

/**
 * Custom Response Wrapper
 * Ensures every response follows the { success, data, message } format
 */
const wrapResponse = (data, message = "OK", success = true) => ({
  success,
  data,
  message,
  total: Array.isArray(data) ? data.length : (data ? 1 : 0)
});

// 1. Auth Login (Custom POST)
server.post('/api/auth/login', (req, res) => {
    const { email, password } = req.body;
    const user = router.db.get('users').find({ email: email.toLowerCase(), password }).value();
    if (user) {
        res.status(200).json(wrapResponse({
            token: "mock-jwt-token-for-" + user.id,
            user: user
        }, "Login successful"));
    } else {
        res.status(401).json(wrapResponse(null, "Invalid credentials", false));
    }
});

// 2. Auth Me (Authenticated User Profile)
server.get('/api/auth/me', (req, res) => {
    // In a real app, we'd extract the user ID from the JWT
    // For this mock, we'll return the first user or a specific one if a header is present
    const authHeader = req.headers.authorization;
    let userId = 6; // Default to a client for testing
    if (authHeader && authHeader.includes('mock-jwt-token-for-')) {
        userId = parseInt(authHeader.split('mock-jwt-token-for-')[1]);
    }

    const user = router.db.get('users').find({ id: userId }).value();
    if (!user) return res.status(404).json(wrapResponse(null, "User not found", false));

    const profile = router.db.get('profiles').find({ userId: user.id }).value();

    // Combine for legacy UI support if needed, or just return UserDto shape
    const responseData = {
        ...user,
        fullName: profile?.full_name,
        avatarUrl: profile?.avatar_url,
        phone: profile?.phone,
        address: profile?.address
    };

    if (user.role === 'LAWYER') {
        const lawyer = router.db.get('lawyers').find({ profileId: profile.id }).value();
        responseData.specialty = lawyer?.speciality;
        responseData.barNumber = lawyer?.bar_number;
    }

    res.json(wrapResponse(responseData));
});

// 3. Lawyer Stats
server.get('/api/lawyers/me/stats', (req, res) => {
    res.json(wrapResponse({
        total_clients: 15,
        active_dossiers: 8,
        pending_consultations: 3,
        revenue: "12,500 DH"
    }));
});

// 4. Recent Consultations (Avocat)
server.get('/api/avocat/consultations/recent', (req, res) => {
    const consultations = router.db.get('consultations').take(3).value();
    res.json(wrapResponse(consultations));
});

// 5. Dossiers "Me"
server.get('/api/dossiers/me', (req, res) => {
    const dossiers = router.db.get('dossiers').value();
    res.json(wrapResponse(dossiers));
});

// 6. Notifications Unread Count
server.get('/api/notifications/unread-count', (req, res) => {
    const authHeader = req.headers.authorization;
    let userId = 6;
    if (authHeader && authHeader.includes('mock-jwt-token-for-')) {
        userId = parseInt(authHeader.split('mock-jwt-token-for-')[1]);
    }
    const notifications = router.db.get('notifications').filter({ userId, isRead: false }).value();
    res.json({ success: true, data: { unreadCount: notifications.length }, message: "OK" });
});

// 6. Generic wrapper for standard json-server routes
// This catches GET requests to /api/users, /api/profiles, /api/lawyers etc.
router.render = (req, res) => {
  const data = res.locals.data;
  // Check if it's already wrapped or if it's an error
  if (data && data.success !== undefined) {
    res.jsonp(data);
  } else {
    res.jsonp(wrapResponse(data));
  }
};

// Use default router for other /api routes
server.use('/api', router);

server.listen(3001, () => {
    console.log('Server is running on port 3001 with ApiResponse wrapping');
});
