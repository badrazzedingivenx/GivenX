<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;


// ============================================================
// 16 — BOOKINGS
// ============================================================
// database/migrations/2024_01_01_000016_create_bookings_table.php

return new class extends Migration {
    public function up(): void {
        Schema::create('bookings', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->foreignId('lawyer_id')
                  ->constrained('users')
                  ->onDelete('cascade');
            $table->enum('type', [
                'video',    // appel vidéo
                'voice',    // appel vocal
                'message',  // message écrit
            ]);
            $table->date('schedule_date');          // date du rdv
            $table->time('schedule_time');          // heure du rdv
            $table->text('description')->nullable(); // sujet de la consultation
            $table->enum('payment_method', [
                'visa_mastercard',
                'cmi_payzone',
                'virement',
            ])->nullable();
            $table->enum('payment_status', [
                'pending', 'paid', 'failed', 'refunded'
            ])->default('pending');
            $table->decimal('amount', 10, 2)->default(0);
            $table->enum('status', [
                'pending', 'confirmed', 'completed', 'cancelled'
            ])->default('pending');
            $table->string('booking_ref', 20)->unique();
            $table->string('meeting_url')->nullable();
            $table->timestamps();
            $table->softDeletes();

            $table->index(['user_id', 'status']);
            $table->index(['lawyer_id', 'status']);
            $table->index('schedule_date');
        });
    }
    public function down(): void { Schema::dropIfExists('bookings'); }
};
