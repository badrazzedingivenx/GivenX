<?php

require __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\User;

// Check if admin already exists
$admin = User::where('email', 'admin@haq.com')->first();

if ($admin) {
    echo "Admin user already exists:\n";
    echo "Email: admin@haq.com\n";
    echo "Password: admin123\n";
    echo "Role: " . $admin->role . "\n";
} else {
    // Create admin user
    $admin = User::create([
        'name' => 'Admin User',
        'email' => 'admin@haq.com',
        'password' => bcrypt('admin123'),
        'role' => 'ADMIN',
        'status' => 'active',
        'email_verified_at' => now(),
    ]);

    echo "Admin user created successfully!\n";
    echo "Email: admin@haq.com\n";
    echo "Password: admin123\n";
    echo "Role: ADMIN\n";
}

echo "\nYou can now login with these credentials to access admin endpoints.\n";
