<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class VideoSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
{
    // Évite les doublons sur les lawyers de test
    $lawyers = [];
    
    foreach (['lawyer1', 'lawyer2', 'lawyer3'] as $username) {
        $lawyers[] = \App\Models\User::firstOrCreate(
            ['username' => $username],
            [
                'full_name' => 'Maître ' . ucfirst($username),
                'email' => $username . '@test.com',
                'password' => \Illuminate\Support\Facades\Hash::make('password'),
                'role' => 'lawyer',
            ]
        );
    }

    foreach ($lawyers as $lawyer) {
        // Crée les vidéos seulement si ce lawyer n'en a pas encore
        if ($lawyer->videos()->count() === 0) {
            \App\Models\Video::factory()->count(5)->create([
                'lawyer_id' => $lawyer->id,
            ]);
        }
    }
}}