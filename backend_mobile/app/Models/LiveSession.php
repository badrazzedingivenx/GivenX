<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Fillable;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

#[Fillable([
    'id',
    'lawyer_user_id',
    'lawyer_name',
    'topic',
    'description',
    'domain',
    'status',
    'viewer_count',
    'participants',
    'thumbnail_url',
    'stream_url',
    'stream_key',
    'rtmp_url',
    'playback_url',
    'started_at',
    'scheduled_at',
    'duration_sec',
])]
class LiveSession extends Model
{
    use HasFactory;

    public $incrementing = false;
    protected $keyType = 'string';

    protected $casts = [
        'viewer_count' => 'integer',
        'participants' => 'integer',
        'duration_sec' => 'integer',
        'started_at' => 'datetime',
        'scheduled_at' => 'datetime',
    ];

    public function lawyer(): BelongsTo
    {
        return $this->belongsTo(User::class, 'lawyer_user_id');
    }

    public function comments(): HasMany
    {
        return $this->hasMany(LiveComment::class);
    }

    public function scopeLive($query)
    {
        return $query->where('status', 'LIVE');
    }

    public function scopeScheduled($query)
    {
        return $query->where('status', 'Scheduled');
    }

    public function isLive(): bool
    {
        return $this->status === 'LIVE';
    }

    public function end(): void
    {
        $this->update([
            'status' => 'ended',
            'duration_sec' => $this->started_at ? now()->diffInSeconds($this->started_at) : null,
        ]);
    }
}
