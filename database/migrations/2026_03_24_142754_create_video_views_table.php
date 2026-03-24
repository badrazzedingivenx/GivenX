<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 06 — VIDEO VIEWS
// ============================================================
// database/migrations/2024_01_01_000006_create_video_views_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('video_views', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')
                  ->nullable()
                  ->constrained('users')
                  ->onDelete('set null');
            $table->foreignId('video_id')
                  ->constrained('videos')
                  ->onDelete('cascade');
            $table->integer('watched_seconds')->default(0);
            $table->string('ip_address', 45)->nullable();
            $table->timestamps();

            $table->index(['video_id', 'created_at']);
            $table->index('user_id');
        });
    }
    public function down(): void { Schema::dropIfExists('video_views'); }
};
