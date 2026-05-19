<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Fillable;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

#[Fillable([
    'id',
    'client_id',
    'lawyer_id',
    'title',
    'file_url',
    'file_type',
    'file_size_kb',
    'status',
    'upload_date',
])]
class Document extends Model
{
    use HasFactory;

    public $incrementing = false;
    protected $keyType = 'string';

    protected $casts = [
        'upload_date' => 'datetime',
        'file_size_kb' => 'integer',
    ];

    public function client(): BelongsTo
    {
        return $this->belongsTo(Client::class);
    }

    public function lawyer(): BelongsTo
    {
        return $this->belongsTo(Lawyer::class);
    }

    public function shares(): HasMany
    {
        return $this->hasMany(DocumentShare::class);
    }

    public function scopeForUser($query, User $user)
    {
        if ($user->isClient()) {
            return $query->where('client_id', $user->client?->id);
        }
        if ($user->isLawyer()) {
            return $query->where('lawyer_id', $user->lawyer?->id)
                ->orWhereHas('shares', fn($q) => $q->where('lawyer_id', $user->lawyer?->id));
        }
        return $query;
    }
}
