<?php

require __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use Illuminate\Support\Facades\Mail;

$testEmail = 'testusage26@gmail.com';

try {
    Mail::raw('This is a test email from HEQQI Legal Services', function($message) use ($testEmail) {
        $message->to($testEmail)
                ->subject('Test Email - HEQQI');
    });
    
    echo "✅ Email sent successfully to: {$testEmail}\n";
} catch (\Exception $e) {
    echo "❌ Error sending email: " . $e->getMessage() . "\n";
    echo "Exception type: " . get_class($e) . "\n";
}
