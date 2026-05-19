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