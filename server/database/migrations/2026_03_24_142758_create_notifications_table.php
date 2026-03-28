<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 09 — NOTIFICATIONS
// ============================================================
// database/migrations/2024_01_01_000009_create_notifications_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('notifications', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->enum('type', [
                'booking_confirmed',
                'booking_cancelled',
                'payment_received',
                'payment_sent',
                'new_follower',
                'new_comment',
                'new_like',
                'review_received',
            ]);
            $table->string('title');
            $table->string('icon', 10)->nullable();
            $table->boolean('is_read')->default(false);
            $table->timestamp('read_at')->nullable();
            $table->json('data')->nullable(); // infos supplémentaires
            $table->timestamps();

            $table->index(['user_id', 'is_read']);
            $table->index('created_at');
        });
    }
    public function down(): void { Schema::dropIfExists('notifications'); }
};