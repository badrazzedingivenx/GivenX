<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 12 — PACKS (abonnements)
// ============================================================
// database/migrations/2024_01_01_000012_create_packs_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('packs', function (Blueprint $table) {
            $table->id();
            $table->string('title');
            $table->text('description')->nullable();
            $table->decimal('price', 10, 2);
            $table->json('features')->nullable();  // liste des fonctionnalités
            $table->integer('ai_questions_limit')->default(3); // -1 = illimité
            $table->integer('contracts_limit')->default(1);    // -1 = illimité
            $table->boolean('is_active')->default(true);
            $table->timestamps();
        });
    }
    public function down(): void { Schema::dropIfExists('packs'); }
};