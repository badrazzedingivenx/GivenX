<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class LawyerFollower extends Model
{
    protected $fillable = [
        'user_id',
        'lawyer_id',
        'created_at',
    ];

    const UPDATED_AT = null;

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function lawyer()
    {
        return $this->belongsTo(User::class, 'lawyer_id');
    }
}
