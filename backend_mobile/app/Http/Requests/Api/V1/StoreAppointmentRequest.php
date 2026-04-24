<?php

namespace App\Http\Requests\Api\V1;

use Illuminate\Foundation\Http\FormRequest;

class StoreAppointmentRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'lawyer_id' => 'required|integer|exists:users,id',
            'date' => 'required|date|after_or_equal:today',
            'time' => 'required|date_format:H:i',
            'duration_min' => 'sometimes|integer|min:15|max:480',
            'type' => 'sometimes|string|in:consultation,suivi,reunion,video',
            'notes' => 'sometimes|nullable|string|max:1000',
        ];
    }
}
