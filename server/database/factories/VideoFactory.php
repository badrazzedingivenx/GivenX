<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;

class VideoFactory extends Factory
{
    public function definition(): array
    {
        return [
            'title' => fake()->sentence(),
            'description' => fake()->paragraph(),
            'tags' => [fake()->word(), fake()->word()],
            'video_path' => 'https://www.w3schools.com/html/mov_bbb.mp4',
            'thumbnail_path' => fake()->imageUrl(),
            'duration_seconds' => fake()->numberBetween(10, 120),
            'views_count' => fake()->numberBetween(0, 1000),
            'likes_count' => fake()->numberBetween(0, 500),
            'comments_count' => fake()->numberBetween(0, 100),
            'status' => 'published',
            'published_at' => fake()->dateTimeBetween('-1 month', 'now'),
        ];
    }
}
