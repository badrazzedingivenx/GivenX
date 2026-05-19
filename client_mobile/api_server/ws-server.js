const WebSocket = require("ws");

const wss = new WebSocket.Server({ port: 3002 });

console.log("WebSocket running on ws://localhost:3002");

wss.on("connection", (ws) => {
  console.log("Client connected ✅");

  ws.on("message", (message) => {
    console.log("Received:", message.toString());

    // broadcast لجميع clients
    wss.clients.forEach((client) => {
      if (client.readyState === WebSocket.OPEN) {
        client.send(message.toString());
      }
    });
  });

  ws.send("Welcome 👋");
});