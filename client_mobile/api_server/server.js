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

// 3. Lawyer Stats — computed dynamically from reservations
server.get('/api/lawyers/me/stats', (req, res) => {
    const authHeader = req.headers.authorization;
    let userId = null;
    if (authHeader && authHeader.includes('mock-jwt-token-for-')) {
        userId = parseInt(authHeader.split('mock-jwt-token-for-')[1]);
    }
    const profile = userId ? router.db.get('profiles').find({ userId }).value() : null;
    if (!profile || profile.role !== 'LAWYER') {
        return res.status(404).json(wrapResponse(null, 'Lawyer profile not found', false));
    }
    const lawyer = router.db.get('lawyers').find({ profileId: profile.id }).value();
    if (!lawyer) {
        return res.status(404).json(wrapResponse(null, 'Lawyer not found', false));
    }
    const lawyerId = lawyer.id;
    const allRes = router.db.get('reservations').filter({ lawyerId: String(lawyerId) }).value().concat(
        router.db.get('reservations').filter({ lawyerId: Number(lawyerId) }).value()
    );
    const paidRes = allRes.filter(r => r.paymentStatus === 'paid');
    const revenue = paidRes.reduce((sum, r) => sum + (Number(r.price) || 0), 0);
    const activeClientIds = new Set(allRes
        .filter(r => r.status === 'accepted' || r.paymentStatus === 'paid')
        .map(r => String(r.clientId))
    );
    const pendingRequests = allRes.filter(r => r.status === 'pending').length;
    const acceptedCount = allRes.filter(r => r.status === 'accepted').length;
    const monthNames = ['Jan','Fév','Mar','Avr','Mai','Juin','Juil','Août','Sep','Oct','Nov','Déc'];
    const monthlyRevenue = monthNames.map((name, idx) => {
        const monthTotal = paidRes
            .filter(r => {
                const d = new Date(r.createdAt);
                return d.getMonth() === idx;
            })
            .reduce((sum, r) => sum + (Number(r.price) || 0), 0);
        return { month: name, amount: monthTotal };
    });
    console.log(`[STATS] lawyerId=${lawyerId} reservations=${allRes.length} paid=${paidRes.length} revenue=${revenue} pending=${pendingRequests}`);
    res.json(wrapResponse({
        total_clients: activeClientIds.size,
        active_clients: activeClientIds.size,
        audiences_today: 0,
        new_requests: pendingRequests,
        closed_cases: paidRes.length,
        dossiers_gagnes: paidRes.length,
        total_revenue_month: revenue,
        total_revenue_year: revenue,
        average_rating: lawyer.rating || 0,
        revenue_change: 0,
        clients_change: 0,
        rating_change: 0,
        requests_change: 0,
        monthly_revenue: monthlyRevenue
    }));
});

// 4. Recent Consultations (Avocat) — last 5 reservations sorted by createdAt DESC
server.get('/api/avocat/consultations/recent', (req, res) => {
    const authHeader = req.headers.authorization;
    let userId = null;
    if (authHeader && authHeader.includes('mock-jwt-token-for-')) {
        userId = parseInt(authHeader.split('mock-jwt-token-for-')[1]);
    }
    const profile = userId ? router.db.get('profiles').find({ userId }).value() : null;
    if (!profile || profile.role !== 'LAWYER') {
        return res.json(wrapResponse([]));
    }
    const lawyer = router.db.get('lawyers').find({ profileId: profile.id }).value();
    if (!lawyer) return res.json(wrapResponse([]));
    const lawyerId = lawyer.id;
    const allRes = router.db.get('reservations').filter({ lawyerId: String(lawyerId) }).value().concat(
        router.db.get('reservations').filter({ lawyerId: Number(lawyerId) }).value()
    );
    const sorted = allRes.sort((a, b) => (b.createdAt || '').localeCompare(a.createdAt || ''));
    const recent = sorted.slice(0, 5);
    const consultations = recent.map(r => ({
        id: r.id,
        client_name: r.fullName || `Client #${r.clientId}`,
        legal_case: r.domain || '',
        date: r.createdAt || '',
        price: Number(r.price) || 0,
        status: r.status === 'accepted' ? 'Terminé' :
                r.status === 'pending' ? 'En attente' :
                r.status === 'rejected' ? 'Refusé' : r.status
    }));
    console.log(`[CONSULTATIONS] lawyerId=${lawyerId} returning ${consultations.length} items`);
    res.json(wrapResponse(consultations));
});

// 5. Dossiers "Me"
server.get('/api/dossiers/me', (req, res) => {
    const dossiers = router.db.get('dossiers').value();
    res.json(wrapResponse(dossiers));
});

// 6. Notifications — filtered by role (lawyerId for lawyers, userId for clients)
server.get('/api/notifications', (req, res) => {
    const authHeader = req.headers.authorization;
    let userId = null;
    if (authHeader && authHeader.includes('mock-jwt-token-for-')) {
        userId = parseInt(authHeader.split('mock-jwt-token-for-')[1]);
    }
    const profile = userId ? router.db.get('profiles').find({ userId }).value() : null;
    const isLawyer = profile?.role === 'LAWYER';
    let allNotifs = router.db.get('notifications').value();
    let filtered;
    if (isLawyer) {
        const lawyer = router.db.get('lawyers').find({ profileId: profile.id }).value();
        const lawyerId = lawyer ? String(lawyer.id) : null;
        filtered = allNotifs.filter(n => String(n.lawyerId) === lawyerId);
    } else if (userId) {
        filtered = allNotifs.filter(n => String(n.userId) === String(userId));
    } else {
        filtered = allNotifs;
    }
    console.log(`[NOTIFICATIONS] isLawyer=${isLawyer} total=${filtered.length}`);
    res.json(wrapResponse(filtered));
});

// 6b. Notifications Unread Count
server.get('/api/notifications/unread-count', (req, res) => {
    const authHeader = req.headers.authorization;
    let userId = null;
    if (authHeader && authHeader.includes('mock-jwt-token-for-')) {
        userId = parseInt(authHeader.split('mock-jwt-token-for-')[1]);
    }
    const profile = userId ? router.db.get('profiles').find({ userId }).value() : null;
    const isLawyer = profile?.role === 'LAWYER';
    let notifications = router.db.get('notifications').value();
    let unreadCount = 0;
    if (isLawyer) {
        const lawyer = router.db.get('lawyers').find({ profileId: profile.id }).value();
        if (lawyer) {
            unreadCount = notifications.filter(n => String(n.lawyerId) === String(lawyer.id) && !n.isRead).length;
        }
    } else if (userId) {
        unreadCount = notifications.filter(n => String(n.userId) === String(userId) && !n.isRead).length;
    }
    res.json({ success: true, data: { unreadCount }, message: "OK" });
});

// 7. Debug logging for reservation queries
server.use((req, res, next) => {
  if (req.method === 'GET' && req.path === '/api/reservations') {
    console.log(`[RESERVATIONS QUERY] lawyerId=${req.query.lawyerId}, clientId=${req.query.clientId}`);
  }
  if (req.method === 'POST' && req.path === '/api/reservations') {
    console.log(`[RESERVATIONS CREATE] body:`, JSON.stringify(req.body, null, 2));
  }
  next();
});

// 8. Payment endpoint — simulate payment on a reservation
server.patch('/api/reservations/:id/pay', (req, res) => {
  const { id } = req.params;
  console.log(`[PAYMENT] Updating reservation ${id} to paid`);
  const reservation = router.db.get('reservations').find({ id: String(id) }).value();
  if (!reservation) {
    return res.status(404).json(wrapResponse(null, 'Reservation not found', false));
  }
  if (reservation.status !== 'accepted') {
    return res.status(400).json(wrapResponse(null, 'Reservation must be accepted before payment', false));
  }
  router.db.get('reservations').find({ id: String(id) }).assign({ paymentStatus: 'paid' }).write();
  const updated = router.db.get('reservations').find({ id: String(id) }).value();
  console.log(`[PAYMENT] Reservation ${id} now paid`);
  res.json(wrapResponse(updated, 'Payment successful'));
});

// 9. Message validation middleware — block sending if reservation not accepted+paid
server.use((req, res, next) => {
  if (req.method === 'POST' && req.path === '/api/messages') {
    const { reservationId, senderId, receiverId } = req.body;
    console.log(`[MESSAGE VALIDATION] reservationId=${reservationId}, senderId=${senderId}, receiverId=${receiverId}`);

    if (!reservationId) {
      return res.status(400).json(wrapResponse(null, 'Missing reservationId', false));
    }
    const reservation = router.db.get('reservations').find({ id: String(reservationId) }).value();
    if (!reservation) {
      console.log(`[MESSAGE VALIDATION] FAILED: reservation ${reservationId} not found`);
      return res.status(404).json(wrapResponse(null, 'Reservation not found', false));
    }

    console.log(`[MESSAGE VALIDATION] Found reservation: status=${reservation.status}, paymentStatus=${reservation.paymentStatus}, clientId=${reservation.clientId}, lawyerId=${reservation.lawyerId}`);

    // Verify sender is a participant in this reservation
    const senderIdNum = Number(senderId);
    const receiverIdNum = Number(receiverId);
    const isParticipant = senderIdNum === reservation.clientId || senderIdNum === reservation.lawyerId;
    if (!isParticipant) {
      console.log(`[MESSAGE VALIDATION] FAILED: sender ${senderId} is not a participant in reservation ${reservationId}`);
      return res.status(403).json(wrapResponse(null, 'Sender is not a participant of this reservation', false));
    }

    // Verify receiver is the other participant
    const expectedReceiver = senderIdNum === reservation.clientId ? reservation.lawyerId : reservation.clientId;
    if (receiverIdNum !== expectedReceiver) {
      console.log(`[MESSAGE VALIDATION] FAILED: receiver ${receiverId} does not match expected ${expectedReceiver}`);
      return res.status(403).json(wrapResponse(null, 'Invalid receiver for this reservation', false));
    }

    if (reservation.status !== 'accepted') {
      console.log(`[MESSAGE VALIDATION] FAILED: status=${reservation.status} (requires "accepted")`);
      return res.status(403).json(wrapResponse(null, 'Reservation is not accepted. Cannot send messages.', false));
    }
    if (reservation.paymentStatus !== 'paid') {
      console.log(`[MESSAGE VALIDATION] FAILED: paymentStatus=${reservation.paymentStatus} (requires "paid")`);
      return res.status(403).json(wrapResponse(null, 'Payment required before sending messages. Please complete payment.', false));
    }

    console.log(`[MESSAGE VALIDATION] PASSED — allowing message`);
  }
  next();
});

// 10. Notification creation middleware — when a reservation is created
server.use((req, res, next) => {
  if (req.method === 'POST' && req.path === '/api/reservations') {
    const originalJson = res.json.bind(res);
    res.json = function (body) {
      if (body && body.success && body.data && body.data.id) {
        const r = body.data;
        const existing = router.db.get('notifications').value();
        const newId = 'notif_' + (existing.length + 1) + '_' + Date.now();
        const notification = {
          id: newId,
          lawyerId: Number(r.lawyerId) || String(r.lawyerId),
          userId: null,
          title: "Nouvelle réservation",
          description: (r.fullName || 'Un client') + " a fait une demande de réservation en " + (r.domain || 'droit'),
          type: "CASE_UPDATE",
          isRead: false,
          time: new Date().toISOString()
        };
        router.db.get('notifications').push(notification).write();
        console.log(`[NOTIFICATION] Created for lawyerId=${notification.lawyerId} reservationId=${r.id}`);
      }
      return originalJson(body);
    };
  }
  next();
});

// 11. Generic wrapper for standard json-server routes
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
