<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Fillable;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\Casts\Attribute;

#[Fillable([
    'profile_id',
    'user_id',
    'name',
    'speciality',
    'domaine',
    'specializations',
    'schedule',
    'slot_duration_min',
    'buffer_between_min',
    'location',
    'city',
    'avatar_url',
    'rating',
    'review_count',
    'years_experience',
    'is_verified',
    'is_available',
    'bar_number',
    'bio',
])]
class Lawyer extends Model
{
    use HasFactory;

    protected $casts = [
        'rating' => 'decimal:2',
        'is_verified' => 'boolean',
        'is_available' => 'boolean',
        'specializations' => 'array',
        'schedule' => 'array',
    ];

    protected function avatarUrl(): Attribute
    {
        return Attribute::make(
            get: fn ($value) => empty($value) ? null : (filter_var($value, FILTER_VALIDATE_URL) ? $value : asset($value)),
        );
    }

    public function profile(): BelongsTo
    {
        return $this->belongsTo(Profile::class);
    }

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    public function appointments(): HasMany
    {
        return $this->hasMany(Appointment::class, 'lawyer_user_id');
    }

    public function documents(): HasMany
    {
        return $this->hasMany(Document::class);
    }

    public function reels(): HasMany
    {
        return $this->hasMany(Reel::class, 'lawyer_user_id');
    }

    public function stories(): HasMany
    {
        return $this->hasMany(Story::class, 'lawyer_user_id');
    }

    public function liveSessions(): HasMany
    {
        return $this->hasMany(LiveSession::class, 'lawyer_user_id');
    }

    public function scopeVerified($query)
    {
        return $query->where('is_verified', true);
    }

    public function scopeAvailable($query)
    {
        return $query->where('is_available', true);
    }
}
