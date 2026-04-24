# 🎥 Guide Complet: Tester le Streaming Live

## Prérequis
(*********************************************************************)
### 1. Vérifier Docker installé

**Windows - PowerShell:**
```powershell
docker --version
```

**Si Docker n'est pas installé:**
- Télécharger: https://www.docker.com/products/docker-desktop/
- Installer et redémarrer le PC
- Lancer Docker Desktop

---

## Étape 1: Lancer MediaMTX (Serveur Streaming)

### Option A: Docker (Recommandé)

**1. Ouvrir PowerShell en administrateur**

**2. Vérifier si MediaMTX tourne déjà:**
```powershell
docker ps | Select-String "mediamtx"
```

**3. Si MediaMTX tourne déjà, le supprimer:**
```powershell
docker stop mediamtx
docker rm mediamtx
```

**4. Lancer MediaMTX:**
```powershell
docker run -d --name mediamtx --restart always -p 1935:1935 -p 8888:8888 bluenviron/mediamtx
```

**5. Vérifier que MediaMTX tourne:**
```powershell
docker ps
```

**Sortie attendue:**
```
CONTAINER ID   IMAGE                  COMMAND         CREATED         STATUS         PORTS                                            NAMES
abc123def456   bluenviron/mediamtx    "/mediamtx"    2 minutes ago   Up 2 minutes   0.0.0.0:1935->1935/tcp, 0.0.0.0:8888->8888/tcp   mediamtx
```

**6. Tester MediaMTX:**
```powershell
curl http://127.0.0.1:8888
```

**Si vous voyez une réponse JSON, MediaMTX fonctionne! ✅**

### Option B: Docker Compose

**1. Créer le fichier docker-compose.yml:**
```powershell
cd c:\wamp64\www\haq-backend\GivenX\backend_mobile
notepad docker-compose.yml
```

**Coller ce contenu:**
```yaml
version: '3.8'
services:
  mediamtx:
    image: bluenviron/mediamtx
    container_name: mediamtx
    restart: always
    ports:
      - "1935:1935"
      - "8888:8888"
```

**2. Lancer:**
```powershell
docker-compose up -d
```

**3. Vérifier:**
```powershell
docker ps
```

---

## Étape 2: Vérifier la Configuration Laravel

**1. Ouvrir le fichier .env:**
```powershell
cd c:\wamp64\www\haq-backend\GivenX\backend_mobile
notepad .env
```

**2. Vérifier ces lignes en bas du fichier:**
```env
# Live Streaming Configuration (MediaMTX)
STREAMING_HOST=127.0.0.1
STREAMING_HLS_PORT=8888
```

**3. Si elles n'existent pas, les ajouter et sauvegarder**

---

## Étape 3: Lancer le Backend Laravel

**1. Ouvrir PowerShell (nouvelle fenêtre):**

**2. Aller dans le dossier backend:**
```powershell
cd c:\wamp64\www\haq-backend\GivenX\backend_mobile
```

**3. Lancer le serveur Laravel:**
```powershell
php artisan serve
```

**Sortie attendue:**
```
INFO  Server running on [http://127.0.0.1:8000]
```

**⚠️ Ne pas fermer cette fenêtre!**

---

## Étape 4: Obtenir un Token Lawyer

### Option A: Via Postman (Recommandé)

**1. Ouvrir Postman**

**2. Créer une nouvelle requête:**
```
POST http://127.0.0.1:8000/api/v1/auth/login
Content-Type: application/json
```

**3. Body (JSON):**
```json
{
  "email": "lawyer@example.com",
  "password": "password123"
}
```

**4. Cliquer "Send"**

**5. Copier le token depuis la réponse:**
```json
{
  "success": true,
  "data": {
    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGc..."  ← COPIER CECI
  }
}
```

### Option B: Via Script PHP

**1. Créer un script:**
```powershell
cd c:\wamp64\www\haq-backend\GivenX\backend_mobile
notepad get-lawyer-token.php
```

**2. Coller ce code:**
```php
<?php
require __DIR__ . '/vendor/autoload.php';
$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\User;
use Tymon\JWTAuth\Facades\JWTAuth;

$lawyer = User::where('role', 'LAWYER')->where('status', 'active')->first();

if (!$lawyer) {
    echo "❌ Aucun lawyer actif trouvé!\n";
    exit(1);
}

$token = JWTAuth::fromUser($lawyer);

echo "=== LAWYER TOKEN ===\n";
echo "Name: {$lawyer->full_name}\n";
echo "Email: {$lawyer->email}\n";
echo "Token: {$token}\n";
echo "===================\n";
```

**3. Exécuter:**
```powershell
php get-lawyer-token.php
```

**4. Copier le token affiché**

---

## Étape 5: Créer une Session Live

### Via Postman

**1. Nouvelle requête:**
```
POST http://127.0.0.1:8000/api/v1/live-sessions
Content-Type: application/json
Authorization: Bearer VOTRE_TOKEN_ICI
```

**2. Body (JSON):**
```json
{
  "topic": "Introduction au Droit du Travail",
  "description": "Session sur les droits des employés",
  "domain": "Droit du Travail"
}
```

**3. Cliquer "Send"**

**4. Sauvegarder la réponse:**
```json
{
  "success": true,
  "data": {
    "session_id": "live_YEWgLHPKW4KG",
    "stream_key": "7p5wGt3OjEx5qjOm5l0DfJm851dxYQKG",
    "rtmp_url": "rtmp://127.0.0.1/live",
    "playback_url": "http://127.0.0.1:8888/7p5wGt3OjEx5qjOm5l0DfJm851dxYQKG/index.m3u8"
  }
}
```

**⚠️ IMPORTANT: Copier `stream_key` pour les étapes suivantes!**

### Via Script PHP

**1. Créer le script:**
```powershell
notepad create-live-session.php
```

**2. Coller:**
```php
<?php
require __DIR__ . '/vendor/autoload.php';
$app = require_once __DIR__ . '/bootstrap/app.php';
$kernel = $app->make('Illuminate\Contracts\Http\Kernel');
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\User;
use Tymon\JWTAuth\Facades\JWTAuth;

$lawyer = User::where('role', 'LAWYER')->where('status', 'active')->first();
$token = JWTAuth::fromUser($lawyer);

echo "Creating live session for: {$lawyer->full_name}\n\n";

$req = Illuminate\Http\Request::create('/api/v1/live-sessions', 'POST', [], [], [], [
    'CONTENT_TYPE' => 'application/json',
    'HTTP_AUTHORIZATION' => 'Bearer ' . $token,
    'HTTP_ACCEPT' => 'application/json',
], json_encode([
    'topic' => 'Test Live Session',
    'description' => 'Testing streaming setup',
    'domain' => 'Droit du Travail'
]));

$res = $kernel->handle($req);
$data = json_decode($res->getContent(), true);

if ($data['success']) {
    echo "✅ Session créée!\n\n";
    echo "Session ID: {$data['data']['session_id']}\n";
    echo "Stream Key: {$data['data']['stream_key']}\n";
    echo "RTMP URL: {$data['data']['rtmp_url']}\n";
    echo "Playback URL: {$data['data']['playback_url']}\n";
} else {
    echo "❌ Erreur: " . ($data['message'] ?? 'Unknown') . "\n";
}
```

**3. Exécuter:**
```powershell
php create-live-session.php
```

---

## Étape 6: Configurer OBS Studio

### 1. Télécharger OBS Studio

**Lien:** https://obsproject.com/fr/download

**Installer et lancer OBS**

### 2. Configurer le Stream

**1. Cliquer "Paramètres" (Settings)**

**2. Aller dans "Stream" (diffusion)**

**3. Configurer:**
- **Service:** `Personnalisé` (Custom)
- **Serveur:** `rtmp://127.0.0.1/live`
- **Clé de stream:** `7p5wGt3OjEx5qjOm5l0DfJm851dxYQKG` (copier depuis l'API)

**4. Cliquer "Appliquer"**

### 3. Ajouter une Source

**1. Dans la section "Sources" (en bas)**

**2. Cliquer "+"**

**3. Choisir:**
- **"Capture d'écran"** pour streamer tout l'écran
- OU **"Capture de fenêtre"** pour une fenêtre spécifique
- OU **"Source média"** pour un fichier vidéo

**4. Nommer la source (ex: "Test")**

**5. Configurer selon votre choix**

**6. Cliquer "OK"**

### 4. Démarrer le Stream

**1. Cliquer "Démarrer la diffusion" (Start Streaming)**

**2. Vérifier en bas à droite:**
- Débit (bitrate) devrait être > 1000 kbps
- ✅ Vert = ça stream!

---

## Étape 7: Voir le Stream Live

### Option A: VLC Media Player

**1. Installer VLC:** https://www.videolan.org/vlc/

**2. Ouvrir VLC**

**3. Média → Ouvrir un flux réseau**
   - Ou: Ctrl + N

**4. Coller l'URL:**
```
http://127.0.0.1:8888/emT65tH8DVZRojbZfGJcEnQyYtmKy70H/index.m3u8
```
*(Remplacer par VOTRE stream_key)*

**5. Cliquer "Jouer"**

**6. Attendre 5-10 secondes**

**✅ Vous devriez voir votre stream!**

### Option B: Navigateur Web (Chrome/Firefox)

**1. Créer le fichier HTML:**
```powershell
cd c:\wamp64\www\haq-backend\GivenX\backend_mobile
notepad view-stream.html
```

**2. Coller ce code:**
```html
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HAQ Live Stream</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 1000px;
            margin: 0 auto;
            padding: 20px;
            background: #1a1a1a;
            color: white;
        }
        h1 { color: #4CAF50; }
        video {
            width: 100%;
            max-width: 900px;
            background: #000;
            border-radius: 8px;
        }
        .info {
            background: #333;
            padding: 15px;
            border-radius: 5px;
            margin: 20px 0;
        }
        .url {
            background: #222;
            padding: 10px;
            border-radius: 3px;
            font-family: monospace;
            word-break: break-all;
        }
    </style>
</head>
<body>
    <h1>🎥 HAQ Live Session</h1>
    
    <div class="info">
        <h3>Instructions:</h3>
        <p>1. Remplacer YOUR_STREAM_KEY par votre clé</p>
        <p>2. Ouvrir ce fichier dans Chrome/Firefox</p>
    </div>

    <h3>Stream Player:</h3>
    <video id="video" controls autoplay></video>

    <div class="info">
        <h3>URL du stream:</h3>
        <div class="url" id="streamUrl"></div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/hls.js@latest"></script>
    <script>
        // ⚠️ REMPLACER PAR VOTRE STREAM KEY
        const STREAM_KEY = 'YOUR_STREAM_KEY';
        
        const streamUrl = `http://127.0.0.1:8888/${STREAM_KEY}/index.m3u8`;
        document.getElementById('streamUrl').textContent = streamUrl;

        const video = document.getElementById('video');

        if (Hls.isSupported()) {
            const hls = new Hls();
            hls.loadSource(streamUrl);
            hls.attachMedia(video);
            hls.on(Hls.Events.MANIFEST_PARSED, function() {
                console.log('✅ Stream loaded!');
                video.play();
            });
            hls.on(Hls.Events.ERROR, function(event, data) {
                console.error('❌ HLS Error:', data);
            });
        } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
            video.src = streamUrl;
            video.addEventListener('loadedmetadata', function() {
                video.play();
            });
        } else {
            alert('Your browser does not support HLS. Use Chrome or Firefox.');
        }
    </script>
</body>
</html>
```

**3. Modifier la ligne:**
```javascript
const STREAM_KEY = 'YOUR_STREAM_KEY';  // ← Remplacer ici!
```

**Par exemple:**
```javascript
const STREAM_KEY = '7p5wGt3OjEx5qjOm5l0DfJm851dxYQKG';
```

**4. Sauvegarder**

**5. Ouvrir dans Chrome:**
```powershell
start chrome view-stream.html
```

**Ou double-cliquer sur le fichier**

**✅ Le stream devrait jouer!**

---

## Étape 8: Tester les Commentaires

### Via Postman

**1. Nouvelle requête:**
```
POST http://127.0.0.1:8000/api/v1/live-sessions/live_YEWgLHPKW4KG/comments
Content-Type: application/json
Authorization: Bearer VOTRE_TOKEN_USER
```

**2. Body:**
```json
{
  "content": "Très intéressant! Merci pour ces informations"
}
```

**3. Send**

### Récupérer les commentaires:

```
GET http://127.0.0.1:8000/api/v1/live-sessions/live_YEWgLHPKW4KG/comments
Authorization: Bearer VOTRE_TOKEN
```

---

## Étape 9: Terminer la Session

### Via Postman

```
PATCH http://127.0.0.1:8000/api/v1/live-sessions/live_YEWgLHPKW4KG/end
Authorization: Bearer TOKEN_LAWYER_QUI_A_CREE
```

### Via OBS

Cliquer **"Arrêter la diffusion"**

---

## Checklist de Dépannage

### ❌ MediaMTX ne démarre pas

**Vérifier les ports:**
```powershell
netstat -ano | findstr :1935
netstat -ano | findstr :8888
```

**Si un processus utilise le port:**
```powershell
taskkill /PID <PID> /F
```

**Ou changer les ports dans .env:**
```env
STREAMING_HLS_PORT=8889
```

### ❌ OBS ne se connecte pas

**1. Vérifier MediaMTX:**
```powershell
docker logs mediamtx
```

**2. Vérifier la configuration OBS:**
- ✅ Server: `rtmp://127.0.0.1/live` (pas `rtmp://127.0.0.1:1935/live`)
- ✅ Stream Key: Exactement copié depuis l'API

**3. Tester la connexion:**
```powershell
Test-NetConnection -ComputerName 127.0.0.1 -Port 1935
```

### ❌ VLC ne joue pas

**1. Vérifier l'URL:**
```
http://127.0.0.1:8888/VOTRE_STREAM_KEY/index.m3u8
```

**2. Tester dans le navigateur:**
```
http://127.0.0.1:8888/
```
*(Devrait afficher JSON de MediaMTX)*

**3. Vérifier qu'OBS stream:**
- Bitrate > 0 en bas à droite
- Attendre 5-10 secondes après start

### ❌ Laravel retourne une erreur

**1. Vérifier le serveur:**
```powershell
cd c:\wamp64\www\haq-backend\GivenX\backend_mobile
php artisan serve
```

**2. Vérifier les logs:**
```powershell
Get-Content storage\logs\laravel.log -Tail 50
```

**3. Vider le cache:**
```powershell
php artisan config:clear
php artisan cache:clear
```

---

## Récapitulatif des Commandes

```powershell
# 1. Lancer MediaMTX
docker run -d --name mediamtx --restart always -p 1935:1935 -p 8888:8888 bluenviron/mediamtx

# 2. Lancer Laravel
cd c:\wamp64\www\haq-backend\GivenX\backend_mobile
php artisan serve

# 3. Créer session live (via script)
php create-live-session.php

# 4. Voir les containers Docker
docker ps

# 5. Voir logs MediaMTX
docker logs mediamtx

# 6. Arrêter MediaMTX
docker stop mediamtx

# 7. Supprimer MediaMTX
docker rm mediamtx

# 8. Tester connexion
curl http://127.0.0.1:8888
```

---

## URLs à Retenir

| Service | URL |
|---------|-----|
| **Laravel API** | http://127.0.0.1:8000 |
| **MediaMTX Admin** | http://127.0.0.1:8888 |
| **RTMP Server** | rtmp://127.0.0.1/live |
| **HLS Playback** | http://127.0.0.1:8888/{stream_key}/index.m3u8 |

---

## Prochaines Étapes

- [ ] Tester avec un vrai téléphone (même réseau WiFi)
- [ ] Intégrer le player HLS dans l'app mobile
- [ ] Ajouter WebSocket pour commentaires temps réel
- [ ] Déployer MediaMTX sur un serveur production
- [ ] Configurer HTTPS pour production

---

**🎉 Félicitations! Vous avez configuré le streaming live!**
