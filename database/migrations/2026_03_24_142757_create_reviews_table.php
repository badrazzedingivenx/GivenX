<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;


// ============================================================
// 08 — REVIEWS
// ============================================================
// database/migrations/2024_01_01_000008_create_reviews_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('reviews', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->foreignId('lawyer_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->tinyInteger('rating');        // 1 à 5
            $table->text('comment')->nullable();
            $table->timestamps();

            $table->unique(['user_id', 'lawyer_id']); // 1 avis par avocat
            $table->index('lawyer_id');
        });
    }
    public function down(): void { Schema::dropIfExists('reviews'); }
};
