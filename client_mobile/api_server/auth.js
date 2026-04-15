module.exports = (req, res, next) => {
  if (req.path === '/login' && req.method === 'POST') {
    const { email, password } = req.body;
    const db = require('./db.json');
    // كنقلبو واش كاين شي واحد بهاد المعلومات
    const user = db.profiles.find(u => u.email === email); 
    
    if (user) {
      return res.status(200).json({ token: "fake-jwt-token-123", user });
    } else {
      return res.status(401).json({ message: "Invalid email or password" });
    }
  }
  next();
}