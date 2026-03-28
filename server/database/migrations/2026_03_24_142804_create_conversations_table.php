<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 14 — CONVERSATIONS (messagerie user ↔ lawyer)
// ============================================================
// database/migrations/2024_01_01_000014_create_conversations_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('conversations', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->foreignId('lawyer_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->foreignId('booking_id')
                  ->nullable()
                  ->constrained('bookings')
                  ->onDelete('set null');
            $table->text('last_message')->nullable();
            $table->timestamp('last_message_at')->nullable();
            $table->integer('user_unread')->default(0);
            $table->integer('lawyer_unread')->default(0);
            $table->timestamps();

            $table->unique(['user_id', 'lawyer_id']);
            $table->index('last_message_at');
        });
    }
    public function down(): void { Schema::dropIfExists('conversations'); }
};