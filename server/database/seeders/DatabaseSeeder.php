<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    use WithoutModelEvents;

    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        if (!User::where('email', 'test@test.com')->exists()) {
            User::factory()->create([
                'username' => 'testuser',
                'full_name' => 'Test User',
                'email' => 'test@test.com',
                'password' => \Illuminate\Support\Facades\Hash::make('password'),
            ]);
        }

        $this->call([
            VideoSeeder::class,
        ]);
    }
}
