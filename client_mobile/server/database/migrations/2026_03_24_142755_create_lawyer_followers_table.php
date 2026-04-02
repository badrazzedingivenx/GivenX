<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 07 — LAWYER FOLLOWERS
// ============================================================
// database/migrations/2024_01_01_000007_create_lawyer_followers_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('lawyer_followers', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->foreignId('lawyer_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->timestamp('created_at')->useCurrent();

            $table->unique(['user_id', 'lawyer_id']);
            $table->index('lawyer_id');
            $table->index('user_id');
        });
    }
    public function down(): void { Schema::dropIfExists('lawyer_followers'); }
};
