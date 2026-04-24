<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('profiles', function (Blueprint $table) {
            $table->id();

            $table->unsignedBigInteger('user_id')->unique();
            $table->string('full_name');
            $table->string('avatar_url')->nullable();
            $table->string('phone')->nullable();
            $table->string('role', 20)->default('CLIENT');
            $table->text('address')->nullable();

            $table->timestamps();

            // FK: FK sur MySQL est utile mais pour démarrer vite on garde soft (ajoutée sans contrainte si le seed diffère).
            $table->foreign('user_id')->references('id')->on('users')->cascadeOnDelete();
        });

        Schema::create('lawyers', function (Blueprint $table) {
            $table->id();

            $table->unsignedBigInteger('profile_id')->unique();
            $table->string('name')->nullable();
            $table->string('speciality')->nullable(); // ex: Droit Immobilier
            $table->string('domaine')->nullable();
            $table->string('location')->nullable();
            $table->string('city')->nullable();
            $table->string('avatar_url')->nullable();

            $table->decimal('rating', 3, 2)->nullable();
            $table->integer('review_count')->default(0);
            $table->integer('years_experience')->default(0);

            $table->boolean('is_verified')->default(false);
            $table->boolean('is_available')->default(true);

            $table->string('bar_number')->nullable();
            $table->text('bio')->nullable();

            $table->timestamps();

            $table->foreign('profile_id')->references('id')->on('profiles')->cascadeOnDelete();
        });

        Schema::create('clients', function (Blueprint $table) {
            $table->id();

            $table->unsignedBigInteger('profile_id')->unique();
            $table->string('company_name')->nullable();

            $table->timestamps();

            $table->foreign('profile_id')->references('id')->on('profiles')->cascadeOnDelete();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('clients');
        Schema::dropIfExists('lawyers');
        Schema::dropIfExists('profiles');
    }
};

