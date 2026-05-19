<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('live_sessions', function (Blueprint $table) {
            $table->string('stream_key')->nullable()->after('stream_url');
            $table->string('rtmp_url')->nullable()->after('stream_key');
            $table->string('playback_url')->nullable()->after('rtmp_url');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('live_sessions', function (Blueprint $table) {
            $table->dropColumn(['stream_key', 'rtmp_url', 'playback_url']);
        });
    }
};
