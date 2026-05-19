<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Fillable;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\Casts\Attribute;

#[Fillable([
    'id',
    'lawyer_user_id',
    'lawyer_name',
    'title',
    'caption',
    'video_url',
    'thumbnail_url',
    'likes_count',
    'views_count',
    'duration',
    'duration_sec',
    'trend',
    'status',
    'domain',
])]
class Reel extends Model
{
    use HasFactory;

    public $incrementing = false;
    protected $keyType = 'string';

    protected $casts = [
        'likes_count' => 'integer',
        'views_count' => 'integer',
        'duration_sec' => 'integer',
    ];

    protected function videoUrl(): Attribute
    {
        return Attribute::make(
            get: fn ($value) => empty($value) ? null : (filter_var($value, FILTER_VALIDATE_URL) ? $value : asset($value)),
        );
    }

    protected function thumbnailUrl(): Attribute
    {
        return Attribute::make(
            get: fn ($value) => empty($value) ? null : (filter_var($value, FILTER_VALIDATE_URL) ? $value : asset($value)),
        );
    }

    public function lawyer(): BelongsTo
    {
        return $this->belongsTo(User::class, 'lawyer_user_id');
    }

    public function likes(): HasMany
    {
        return $this->hasMany(ReelLike::class);
    }

    public function isLikedBy(User $user): bool
    {
        return $this->likes()->where('user_id', $user->id)->exists();
    }

    public function incrementViews(): void
    {
        $this->increment('views_count');
    }

    public function incrementLikes(): void
    {
        $this->increment('likes_count');
    }

    public function decrementLikes(): void
    {
        $this->decrement('likes_count');
    }
}
