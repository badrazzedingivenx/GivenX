<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 19 — USER INTERESTS
// ============================================================
// database/migrations/2024_01_01_000019_create_user_interests_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('user_interests', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->foreignId('interest_id')
                  ->constrained('interests')
                  ->onDelete('cascade');
            $table->timestamps();

            $table->unique(['user_id', 'interest_id']);
            $table->index('user_id');
        });
    }
    public function down(): void { Schema::dropIfExists('user_interests'); }
};