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
        Schema::table('lawyers', function (Blueprint $table) {
            $table->unsignedBigInteger('user_id')->nullable()->after('id');
            $table->foreign('user_id')->references('id')->on('users')->cascadeOnDelete();
        });

        Schema::table('clients', function (Blueprint $table) {
            $table->unsignedBigInteger('user_id')->nullable()->after('id');
            $table->foreign('user_id')->references('id')->on('users')->cascadeOnDelete();
        });

        // Migrate data
        \Illuminate\Support\Facades\DB::statement('UPDATE lawyers JOIN profiles ON lawyers.profile_id = profiles.id SET lawyers.user_id = profiles.user_id');
        \Illuminate\Support\Facades\DB::statement('UPDATE clients JOIN profiles ON clients.profile_id = profiles.id SET clients.user_id = profiles.user_id');
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('lawyers', function (Blueprint $table) {
            $table->dropForeign(['user_id']);
            $table->dropColumn('user_id');
        });

        Schema::table('clients', function (Blueprint $table) {
            $table->dropForeign(['user_id']);
            $table->dropColumn('user_id');
        });
    }
};
