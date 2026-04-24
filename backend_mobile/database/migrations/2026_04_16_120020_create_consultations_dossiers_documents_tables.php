<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('consultations', function (Blueprint $table) {
            $table->id();

            $table->unsignedBigInteger('client_id');
            $table->unsignedBigInteger('lawyer_id');

            $table->string('status')->default('pending');
            $table->string('subject');
            $table->dateTime('date')->nullable();

            $table->timestamps();

            $table->foreign('client_id')->references('id')->on('clients')->cascadeOnDelete();
            $table->foreign('lawyer_id')->references('id')->on('lawyers')->cascadeOnDelete();
        });

        Schema::create('dossiers', function (Blueprint $table) {
            $table->string('id')->primary();

            $table->string('case_number')->nullable();
            $table->string('category')->nullable();
            $table->string('status')->nullable();
            $table->date('opening_date')->nullable();

            $table->unsignedBigInteger('lawyer_id')->nullable();
            $table->integer('progress')->default(0);

            // Pour faciliter le frontend (dénormalisation légère)
            $table->string('lawyer_name')->nullable();
            $table->string('lawyer_specialty')->nullable();
            $table->string('client_name')->nullable();

            $table->timestamps();

            $table->foreign('lawyer_id')->references('id')->on('lawyers')->nullOnDelete();
        });

        Schema::create('documents', function (Blueprint $table) {
            $table->string('id')->primary();

            $table->unsignedBigInteger('client_id')->nullable();
            $table->unsignedBigInteger('lawyer_id')->nullable();

            $table->string('title')->nullable();
            $table->string('file_url');
            $table->string('file_type')->nullable();
            $table->integer('file_size_kb')->nullable();
            $table->string('status')->nullable();
            $table->dateTime('upload_date')->nullable();

            $table->timestamps();

            $table->foreign('client_id')->references('id')->on('clients')->nullOnDelete();
            $table->foreign('lawyer_id')->references('id')->on('lawyers')->nullOnDelete();
        });

        Schema::create('document_shares', function (Blueprint $table) {
            $table->string('id')->primary();

            $table->string('document_id');
            $table->unsignedBigInteger('lawyer_id')->nullable();
            $table->dateTime('expires_at')->nullable();

            $table->timestamps();

            $table->foreign('document_id')->references('id')->on('documents')->cascadeOnDelete();
            $table->foreign('lawyer_id')->references('id')->on('lawyers')->nullOnDelete();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('document_shares');
        Schema::dropIfExists('documents');
        Schema::dropIfExists('dossiers');
        Schema::dropIfExists('consultations');
    }
};

