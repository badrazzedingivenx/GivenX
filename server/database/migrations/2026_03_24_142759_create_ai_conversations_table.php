<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 10 — AI CONVERSATIONS
// ============================================================
// database/migrations/2024_01_01_000010_create_ai_conversations_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('ai_conversations', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->text('content')->nullable();  // dernier message affiché
            $table->enum('category', [
                'كراء',    // bail
                'شغل',     // travail
                'أسرة',    // famille
                'تجارة',   // commerce
                'إرث',     // héritage
                'سير',     // accidents
                'عقار',    // immobilier
                'أخرى',    // autre
            ])->default('أخرى');
            $table->integer('questions_count')->default(0);
            $table->boolean('is_archived')->default(false);
            $table->timestamps();

            $table->index(['user_id', 'is_archived']);
        });
    }
    public function down(): void { Schema::dropIfExists('ai_conversations'); }
};