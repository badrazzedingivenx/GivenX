<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 18 — VIDEO INTERESTS
// ============================================================
// database/migrations/2024_01_01_000018_create_video_interests_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('video_interests', function (Blueprint $table) {
            $table->id();
            $table->foreignId('video_id')
                  ->constrained('videos')
                  ->onDelete('cascade');
            $table->foreignId('interest_id')
                  ->constrained('interests')
                  ->onDelete('cascade');
            $table->timestamps();

            $table->unique(['video_id', 'interest_id']);
            $table->index('interest_id');
        });
    }
    public function down(): void { Schema::dropIfExists('video_interests'); }
};