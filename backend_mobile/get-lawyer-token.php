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