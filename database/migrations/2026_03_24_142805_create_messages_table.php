<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 15 — MESSAGES
// ============================================================
// database/migrations/2024_01_01_000015_create_messages_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('messages', function (Blueprint $table) {
            $table->id();
            $table->foreignId('conversation_id')
                  ->constrained('conversations')
                  ->onDelete('cascade');
            $table->foreignId('sender_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->enum('sender_type', ['user', 'lawyer']);
            $table->text('content')->nullable();
            $table->string('attachment')->nullable();   // chemin fichier
            $table->enum('attachment_type', [
                'image', 'pdf', 'doc'
            ])->nullable();
            $table->boolean('is_read')->default(false);
            $table->timestamp('read_at')->nullable();
            $table->timestamps();
            $table->softDeletes();

            $table->index(['conversation_id', 'created_at']);
            $table->index('sender_id');
        });
    }
    public function down(): void { Schema::dropIfExists('messages'); }
};