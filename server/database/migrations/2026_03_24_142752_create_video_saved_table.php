<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;


// ============================================================
// 05 — VIDEO SAVED
// ============================================================
// database/migrations/2024_01_01_000005_create_video_saved_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('video_saved', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->foreignId('video_id')
                  ->constrained('videos')
                  ->onDelete('cascade');
            $table->timestamp('created_at')->useCurrent();

            $table->unique(['user_id', 'video_id']);
            $table->index('video_id');
        });
    }
    public function down(): void { Schema::dropIfExists('video_saved'); }
};