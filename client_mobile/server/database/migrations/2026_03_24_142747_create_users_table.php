
<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

// ============================================================
// 01 — USERS
// ============================================================
// database/migrations/2024_01_01_000001_create_users_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('users', function (Blueprint $table) {
            $table->id();
            $table->string('username')->unique();
            $table->string('full_name');
            $table->string('email')->unique();
            $table->string('password');
            $table->tinyInteger('age')->unsigned()->nullable();
            $table->string('phone', 20)->nullable()->unique();
            $table->string('image')->nullable();
            $table->enum('role', ['user', 'lawyer', 'admin'])->default('user');
            $table->string('city', 100)->nullable();
            // Champs lawyer (NULL si role = user/admin)
            $table->integer('experience')->nullable();
            $table->string('office_address')->nullable();
            $table->string('syndicate')->nullable();
            $table->string('specialisation')->nullable();
            // Auth
            $table->string('google_id')->nullable()->unique();
            $table->timestamp('email_verified_at')->nullable();
            $table->boolean('is_active')->default(true);
            $table->rememberToken();
            $table->timestamps();
            $table->softDeletes();

            $table->index('role');
            $table->index('city');
            $table->index('specialisation');
        });
    }
    public function down(): void { Schema::dropIfExists('users'); }
};