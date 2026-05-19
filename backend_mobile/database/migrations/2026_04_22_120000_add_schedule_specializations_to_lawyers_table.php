<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('lawyers', function (Blueprint $table) {
            $table->json('specializations')->nullable()->after('domaine');
            $table->json('schedule')->nullable()->after('specializations');
            $table->integer('slot_duration_min')->default(60)->after('schedule');
            $table->integer('buffer_between_min')->default(15)->after('slot_duration_min');
        });
    }

    public function down(): void
    {
        Schema::table('lawyers', function (Blueprint $table) {
            $table->dropColumn(['specializations', 'schedule', 'slot_duration_min', 'buffer_between_min']);
        });
    }
};
