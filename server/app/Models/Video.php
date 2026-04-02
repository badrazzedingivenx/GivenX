<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Video extends Model
{
    use SoftDeletes, HasFactory;

    protected $fillable = [
        'lawyer_id', 'title', 'description', 'tags', 'video_path',
        'thumbnail_path', 'duration_seconds', 'views_count', 'likes_count',
        'comments_count', 'shares_count', 'saves_count', 'status', 'published_at'
    ];

    protected $casts = [
        'tags' => 'array',
        'published_at' => 'datetime',
    ];

    public function lawyer()
    {
        return $this->belongsTo(User::class, 'lawyer_id');
    }

    public function likes()
    {
        return $this->hasMany(VideoLike::class);
    }

    public function saves()
    {
        return $this->hasMany(VideoSaved::class);
    }

    public function comments()
    {
        return $this->hasMany(VideoComment::class);
    }
}
