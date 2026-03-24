<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 02 — VIDEOS
// ============================================================
// database/migrations/2024_01_01_000002_create_videos_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('videos', function (Blueprint $table) {
            $table->id();
            $table->foreignId('lawyer_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->string('title');
            $table->text('description')->nullable();
            $table->json('tags')->nullable();
            $table->string('video_path');
            $table->string('thumbnail_path')->nullable();
            $table->integer('duration_seconds')->default(0);
            $table->bigInteger('views_count')->default(0);
            $table->bigInteger('likes_count')->default(0);
            $table->bigInteger('comments_count')->default(0);
            $table->bigInteger('shares_count')->default(0);
            $table->bigInteger('saves_count')->default(0);
            $table->enum('status', [
                'uploading', 'processing', 'published', 'draft', 'removed'
            ])->default('processing');
            $table->timestamp('published_at')->nullable();
            $table->timestamps();
            $table->softDeletes();

            $table->index(['lawyer_id', 'status']);
            $table->index('published_at');
        });
    }
    public function down(): void { Schema::dropIfExists('videos'); }
};