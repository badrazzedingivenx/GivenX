<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class VideoSaved extends Model
{
    protected $table = 'video_saved';
    public $timestamps = false;

    protected $fillable = [
        'user_id', 'video_id', 'created_at'
    ];

    protected $casts = [
        'created_at' => 'datetime',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function video()
    {
        return $this->belongsTo(Video::class);
    }
}
