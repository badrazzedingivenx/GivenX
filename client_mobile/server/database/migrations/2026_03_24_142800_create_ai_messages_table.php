<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 11 — AI MESSAGES (messages dans chaque conversation)
// ============================================================
// database/migrations/2024_01_01_000011_create_ai_messages_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('ai_messages', function (Blueprint $table) {
            $table->id();
            $table->foreignId('conversation_id')
                  ->constrained('ai_conversations')
                  ->onDelete('cascade');
            $table->enum('role', ['user', 'assistant']);
            $table->text('content');
            $table->integer('tokens_used')->default(0);
            $table->timestamps();

            $table->index(['conversation_id', 'created_at']);
        });
    }
    public function down(): void { Schema::dropIfExists('ai_messages'); }
};