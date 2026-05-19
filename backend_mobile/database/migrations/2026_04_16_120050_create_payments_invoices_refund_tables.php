<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('payments', function (Blueprint $table) {
            $table->string('id')->primary();

            // Legacy mobile seed: clientId/lawyerId peuvent pointer vers clients/lawyers
            $table->unsignedBigInteger('client_id')->nullable();
            $table->unsignedBigInteger('lawyer_id')->nullable();

            // Optionnel si on rattache au booking
            $table->string('appointment_id')->nullable();

            $table->date('date')->nullable();
            $table->string('status')->nullable(); // Completed | Pending | Failed
            $table->string('subject')->nullable();
            $table->string('method')->nullable();

            $table->string('amount_text')->nullable(); // ex: "1 500 DH" ou "2,500 DH"
            $table->decimal('amount', 12, 2)->nullable();
            $table->string('currency')->default('MAD');

            $table->dateTime('paid_at')->nullable();
            $table->string('invoice_url')->nullable();

            $table->timestamps();

            $table->foreign('client_id')->references('id')->on('clients')->nullOnDelete();
            $table->foreign('lawyer_id')->references('id')->on('lawyers')->nullOnDelete();
            $table->foreign('appointment_id')->references('id')->on('appointments')->nullOnDelete();
        });

        Schema::create('invoices', function (Blueprint $table) {
            $table->string('id')->primary(); // inv_001 ...
            $table->string('payment_id')->nullable();

            $table->date('date')->nullable();
            $table->string('status')->nullable();

            $table->string('amount_text')->nullable();
            $table->decimal('amount', 12, 2)->nullable();

            // Legacy mobile seed
            $table->string('lawyer_name')->nullable();

            $table->timestamps();

            $table->foreign('payment_id')->references('id')->on('payments')->nullOnDelete();
        });

        Schema::create('refund_requests', function (Blueprint $table) {
            $table->string('id')->primary(); // refund_req_001 ...

            $table->string('payment_id')->nullable();
            $table->unsignedBigInteger('user_id')->nullable();

            $table->text('reason');
            $table->string('status')->nullable(); // pending_review | ...

            $table->timestamps();

            $table->foreign('payment_id')->references('id')->on('payments')->nullOnDelete();
            $table->foreign('user_id')->references('id')->on('users')->nullOnDelete();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('refund_requests');
        Schema::dropIfExists('invoices');
        Schema::dropIfExists('payments');
    }
};

