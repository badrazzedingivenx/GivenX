<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('reels', function (Blueprint $table) {
            $table->string('id')->primary();

            $table->unsignedBigInteger('lawyer_user_id')->nullable();
            $table->string('lawyer_name')->nullable();

            $table->string('title')->nullable();
            $table->text('caption')->nullable();

            $table->string('video_url');
            $table->string('thumbnail_url')->nullable();

            $table->integer('likes_count')->default(0);
            $table->integer('views_count')->default(0);

            // Seed donne une durée au format "00:45"
            $table->string('duration')->nullable();
            $table->integer('duration_sec')->nullable();

            $table->string('trend')->nullable(); // up | down
            $table->string('status')->nullable();
            $table->string('domain')->nullable();

            $table->timestamps();

            $table->foreign('lawyer_user_id')->references('id')->on('users')->nullOnDelete();
        });

        Schema::create('reel_likes', function (Blueprint $table) {
            $table->id();

            $table->string('reel_id');
            $table->unsignedBigInteger('user_id');

            $table->timestamps();

            $table->foreign('reel_id')->references('id')->on('reels')->cascadeOnDelete();
            $table->foreign('user_id')->references('id')->on('users')->cascadeOnDelete();
            $table->unique(['reel_id', 'user_id']);
        });

        Schema::create('stories', function (Blueprint $table) {
            $table->string('id')->primary();

            $table->unsignedBigInteger('lawyer_user_id')->nullable();
            $table->string('lawyer_name')->nullable();

            $table->string('media_url');
            $table->text('caption')->nullable();

            $table->dateTime('expires_at')->nullable();
            $table->boolean('is_live')->default(false);

            $table->integer('views')->default(0);
            $table->string('time_left')->nullable();

            $table->timestamps();

            $table->foreign('lawyer_user_id')->references('id')->on('users')->nullOnDelete();
        });

        Schema::create('live_sessions', function (Blueprint $table) {
            $table->string('id')->primary();

            $table->unsignedBigInteger('lawyer_user_id')->nullable();
            $table->string('lawyer_name')->nullable();

            $table->string('topic');
            $table->text('description')->nullable();
            $table->string('domain')->nullable();
            $table->string('status')->nullable(); // LIVE | Scheduled | ended

            $table->integer('viewer_count')->default(0);
            $table->integer('participants')->default(0);

            $table->string('thumbnail_url')->nullable();
            $table->string('stream_url')->nullable();

            $table->dateTime('started_at')->nullable();
            $table->dateTime('scheduled_at')->nullable();
            $table->integer('duration_sec')->nullable();

            $table->timestamps();

            $table->foreign('lawyer_user_id')->references('id')->on('users')->nullOnDelete();
        });

        Schema::create('live_comments', function (Blueprint $table) {
            $table->id();

            $table->string('live_session_id');
            $table->unsignedBigInteger('author_id')->nullable();
            $table->string('author_name')->nullable();

            $table->text('content');
            $table->dateTime('sent_at');

            $table->timestamps();

            $table->foreign('live_session_id')->references('id')->on('live_sessions')->cascadeOnDelete();
            $table->foreign('author_id')->references('id')->on('users')->nullOnDelete();
            $table->index(['live_session_id', 'sent_at']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('live_comments');
        Schema::dropIfExists('live_sessions');
        Schema::dropIfExists('stories');
        Schema::dropIfExists('reel_likes');
        Schema::dropIfExists('reels');
    }
};

