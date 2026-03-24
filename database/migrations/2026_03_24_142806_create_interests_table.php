<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 17 — INTERESTS
// ============================================================
// database/migrations/2024_01_01_000017_create_interests_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('interests', function (Blueprint $table) {
            $table->id();
            $table->string('name');          // "قانون الشغل"
            $table->string('icon')->nullable(); // "💼"
            $table->timestamps();
        });
    }
    public function down(): void { Schema::dropIfExists('interests'); }
};