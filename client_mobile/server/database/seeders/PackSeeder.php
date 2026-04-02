<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Pack; // ✅ IMPORTANT

class PackSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        Pack::insert([
            [
                'title' => 'مجاني',
                'price' => 0,
                'description' => '3 أسئلة AI شهرياً',
                'ai_questions_limit' => 3,
                'contracts_limit' => 1,
                'is_active' => true
            ],
            [
                'title' => 'بريميوم',
                'price' => 149,
                'description' => 'AI غير محدود',
                'ai_questions_limit' => -1,
                'contracts_limit' => 10,
                'is_active' => true
            ],
            [
                'title' => 'B2B',
                'price' => 499,
                'description' => 'للمقاولات',
                'ai_questions_limit' => -1,
                'contracts_limit' => -1,
                'is_active' => true
            ],
        ]);
    }
}