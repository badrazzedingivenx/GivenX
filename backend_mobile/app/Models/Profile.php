<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Fillable;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasOne;
use Illuminate\Database\Eloquent\Casts\Attribute;

#[Fillable([
    'user_id',
    'full_name',
    'avatar_url',
    'phone',
    'role',
    'address',
])]
class Profile extends Model
{
    use HasFactory;

    protected function avatarUrl(): Attribute
    {
        return Attribute::make(
            get: fn ($value) => empty($value) ? null : (filter_var($value, FILTER_VALIDATE_URL) ? $value : asset($value)),
        );
    }

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    public function lawyer(): HasOne
    {
        return $this->hasOne(Lawyer::class);
    }

    public function client(): HasOne
    {
        return $this->hasOne(Client::class);
    }
}
