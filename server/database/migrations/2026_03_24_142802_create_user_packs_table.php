<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 13 — USER PACKS (souscription d'un user à un pack)
// ============================================================
// database/migrations/2024_01_01_000013_create_user_packs_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('user_packs', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->foreignId('pack_id')
                  ->constrained('packs')
                  ->onDelete('restrict');
            $table->enum('status', ['active', 'cancelled', 'expired'])->default('active');
            $table->timestamp('started_at')->useCurrent();
            $table->timestamp('ends_at')->nullable();
            $table->timestamps();

            $table->index(['user_id', 'status']);
        });
    }
    public function down(): void { Schema::dropIfExists('user_packs'); }
};
