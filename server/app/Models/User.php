<?php

namespace App\Models;

use Illuminate\Foundation\Auth\User as Authenticatable;  // ← change this
use Laravel\Sanctum\HasApiTokens;
use Illuminate\Notifications\Notifiable;
use Tymon\JWTAuth\Contracts\JWTSubject;

class User extends Authenticatable implements JWTSubject  // ← extends Authenticatable
{
    use HasApiTokens, Notifiable;

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
}