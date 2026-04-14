const jsonServer = require('json-server');
const server = jsonServer.create();
server.use((req, res, next) => {
  console.log(`${req.method} ${req.url}`);
  next();
});
const router = jsonServer.router('db.json');
const middlewares = jsonServer.defaults();

// ضروري Middleware ديال JSON باش يقرا الـ body
server.use(jsonServer.bodyParser);
server.use(middlewares);

// 1. Custom Route للـ Login (مرة وحدة فقط)
server.post('/api/auth/login', (req, res) => {
    const { email, password } = req.body;
    const db = router.db; 
    
    // قلب على الـ user
    const user = db.get('users').find({ email: email, password: password }).value();
    
    if (user) {
        return res.status(200).json({ 
            message: "Login successful", 
            userId: user.id 
        });
    } else {
        return res.status(401).json({ message: "Invalid email or password" });
    }
});

// 2. Custom Route للـ Lawyers (مرة وحدة فقط)
server.get('/api/lawyers', (req, res) => {
    const db = router.db;
    const lawyers = db.get('lawyers').value(); 
    return res.status(200).json(lawyers);
});

// 3. الـ Router كيكون هو اللخر
server.use('/api', router);

server.listen(3001, () => {
    console.log('Server is running on port 3001');
});