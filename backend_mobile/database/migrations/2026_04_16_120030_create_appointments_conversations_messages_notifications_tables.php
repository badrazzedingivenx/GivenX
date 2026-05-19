<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('appointments', function (Blueprint $table) {
            $table->string('id')->primary();

            $table->unsignedBigInteger('client_user_id');
            $table->unsignedBigInteger('lawyer_user_id');

            $table->date('date')->nullable();
            $table->time('time')->nullable();
            $table->integer('duration_min')->default(60);

            $table->string('type')->nullable(); // consultation | suivi | reunion | VIDEO (legacy)
            $table->string('status')->nullable();
            $table->text('notes')->nullable();

            $table->decimal('price', 12, 2)->nullable();

            // Pour debug/admin (opti)
            $table->timestamps();

            $table->foreign('client_user_id')->references('id')->on('users')->cascadeOnDelete();
            $table->foreign('lawyer_user_id')->references('id')->on('users')->cascadeOnDelete();
        });

        Schema::create('conversations', function (Blueprint $table) {
            $table->string('id')->primary();

            $table->unsignedBigInteger('lawyer_user_id');
            $table->unsignedBigInteger('client_user_id');

            $table->integer('unread_count_user')->default(0);
            $table->integer('unread_count_lawyer')->default(0);

            $table->text('last_message_content')->nullable();
            $table->dateTime('last_message_sent_at')->nullable();
            $table->unsignedBigInteger('last_message_sender_id')->nullable();

            $table->timestamps();

            $table->foreign('lawyer_user_id')->references('id')->on('users')->cascadeOnDelete();
            $table->foreign('client_user_id')->references('id')->on('users')->cascadeOnDelete();
            $table->foreign('last_message_sender_id')->references('id')->on('users')->nullOnDelete();
        });

        Schema::create('messages', function (Blueprint $table) {
            $table->string('id')->primary();

            $table->string('conversation_id');
            $table->unsignedBigInteger('sender_id');

            $table->text('content')->nullable();
            $table->string('type')->default('text'); // text | image | file
            $table->string('file_url')->nullable();
            $table->string('file_name')->nullable();

            $table->boolean('is_from_user')->default(true);
            $table->dateTime('sent_at');
            $table->string('sender_name')->nullable();

            $table->timestamps();

            $table->foreign('conversation_id')->references('id')->on('conversations')->cascadeOnDelete();
            $table->foreign('sender_id')->references('id')->on('users')->cascadeOnDelete();

            $table->index(['conversation_id', 'sent_at']);
        });

        Schema::create('notifications', function (Blueprint $table) {
            $table->id();

            $table->unsignedBigInteger('user_id');
            $table->string('type')->nullable(); // MESSAGE | DOCUMENT | ...
            $table->string('title')->nullable();
            $table->text('content')->nullable();

            $table->string('entity_type')->nullable();
            $table->string('entity_id')->nullable();

            $table->boolean('is_read')->default(false);
            $table->timestamps();

            $table->foreign('user_id')->references('id')->on('users')->cascadeOnDelete();
        });

        Schema::create('device_tokens', function (Blueprint $table) {
            $table->id();

            $table->unsignedBigInteger('user_id');
            $table->string('token')->unique();
            $table->string('platform', 20)->default('android'); // android | ios

            $table->timestamps();

            $table->foreign('user_id')->references('id')->on('users')->cascadeOnDelete();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('device_tokens');
        Schema::dropIfExists('notifications');
        Schema::dropIfExists('messages');
        Schema::dropIfExists('conversations');
        Schema::dropIfExists('appointments');
    }
};

