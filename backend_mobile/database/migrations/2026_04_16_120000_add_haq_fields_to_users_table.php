<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('users', function (Blueprint $table) {
            // Rôle pour le frontend mobile: "LAWYER" | "CLIENT"
            $table->string('role', 20)->default('CLIENT')->index();

            // Statut: active | pending_verification | suspended
            $table->string('status', 30)->default('active')->index();

            $table->string('phone')->nullable();
            $table->text('address')->nullable();
            $table->string('avatar_url')->nullable();

            // Optionnel: certaines réponses API exposent full_name.
            // On peut mapper "users.name" vers full_name côté application.
            $table->string('full_name')->nullable();
        });
    }

    public function down(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->dropColumn(['role', 'status', 'phone', 'address', 'avatar_url', 'full_name']);
        });
    }
};

