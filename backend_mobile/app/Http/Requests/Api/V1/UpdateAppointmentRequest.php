<?php

namespace App\Http\Requests\Api\V1;

use Illuminate\Foundation\Http\FormRequest;

class UpdateAppointmentRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'date' => 'sometimes|date|after_or_equal:today',
            'time' => 'sometimes|date_format:H:i',
            'duration_min' => 'sometimes|integer|min:15|max:480',
            'type' => 'sometimes|string|in:consultation,suivi,reunion,video',
            'notes' => 'sometimes|nullable|string|max:1000',
        ];
    }
}
