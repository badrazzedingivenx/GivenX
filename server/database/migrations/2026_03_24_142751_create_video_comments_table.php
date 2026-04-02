<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;


// ============================================================
// 04 — VIDEO COMMENTS
// ============================================================
// database/migrations/2024_01_01_000004_create_video_comments_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('video_comments', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->foreignId('video_id')
                  ->constrained('videos')
                  ->onDelete('cascade');
            $table->foreignId('parent_id')
                  ->nullable()
                  ->constrained('video_comments')
                  ->onDelete('cascade');
            $table->text('content');
            $table->integer('likes_count')->default(0);
            $table->boolean('is_pinned')->default(false);
            $table->boolean('is_hidden')->default(false);
            $table->timestamps();
            $table->softDeletes();

            $table->index(['video_id', 'parent_id']);
            $table->index('user_id');
        });
    }
    public function down(): void { Schema::dropIfExists('video_comments'); }
};