<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Interest; // ✅ IMPORTANT

class InterestSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        Interest::insert([
            ['name' => 'قانون الشغل',        'icon' => '💼', 'created_at' => now(), 'updated_at' => now()],
            ['name' => 'مدونة الأسرة',       'icon' => '👨‍👩‍👧', 'created_at' => now(), 'updated_at' => now()],
            ['name' => 'قانون الأعمال',      'icon' => '🏢', 'created_at' => now(), 'updated_at' => now()],
            ['name' => 'القانون العقاري',    'icon' => '🏠', 'created_at' => now(), 'updated_at' => now()],
            ['name' => 'القانون الجنائي',    'icon' => '⚖️', 'created_at' => now(), 'updated_at' => now()],
            ['name' => 'قانون المقاولات',    'icon' => '🚀', 'created_at' => now(), 'updated_at' => now()],
            ['name' => 'حوادث السير',        'icon' => '🚗', 'created_at' => now(), 'updated_at' => now()],
            ['name' => 'الإرث والوصية',      'icon' => '📜', 'created_at' => now(), 'updated_at' => now()],
            ['name' => 'قانون الاستهلاك',    'icon' => '🛒', 'created_at' => now(), 'updated_at' => now()],
            ['name' => 'التوقيع الإلكتروني', 'icon' => '✍️', 'created_at' => now(), 'updated_at' => now()],
        ]);
    }
}