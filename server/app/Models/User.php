<?php

namespace App\Models;

use Illuminate\Foundation\Auth\User as Authenticatable;  // ← change this
use Laravel\Sanctum\HasApiTokens;
use Illuminate\Notifications\Notifiable;
use Tymon\JWTAuth\Contracts\JWTSubject;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class User extends Authenticatable implements JWTSubject  // ← extends Authenticatable
{
    use HasApiTokens, Notifiable, HasFactory;

    protected $fillable = [
        'username', 'full_name', 'email', 'password',
        'phone', 'image', 'role', 'city', 'is_active',
        // lawyer fields
        'experience', 'office_address', 'syndicate', 'specialisation',
    ];

    protected $hidden = [
        'password', 'remember_token',
    ];

    public function getJWTIdentifier()
    {
        return $this->getKey();
    }

    public function getJWTCustomClaims(): array
    {
        return [];
    }

    public function videos()
    {
        return $this->hasMany(Video::class, 'lawyer_id');
    }

    public function likedVideos()
    {
        return $this->hasMany(VideoLike::class);
    }

    public function savedVideos()
    {
        return $this->hasMany(VideoSaved::class);
    }

    public function receivedReviews()
    {
        return $this->hasMany(Review::class, 'lawyer_id');
    }

    public function followingLawyers()
    {
        return $this->belongsToMany(User::class, 'lawyer_followers', 'user_id', 'lawyer_id')
                    ->withPivot('created_at');
    }

    public function followers()
    {
        return $this->belongsToMany(User::class, 'lawyer_followers', 'lawyer_id', 'user_id')
                    ->withPivot('created_at');
    }
}