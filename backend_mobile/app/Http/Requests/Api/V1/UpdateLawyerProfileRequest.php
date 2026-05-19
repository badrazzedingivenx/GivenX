<?php

namespace App\Http\Requests\Api\V1;

use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Http\Exceptions\HttpResponseException;

class UpdateLawyerProfileRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'full_name' => 'sometimes|string|max:255',
            'phone' => 'sometimes|string|max:20',
            'address' => 'sometimes|nullable|string|max:500',
            'bio' => 'sometimes|nullable|string|max:2000',
            'speciality' => 'sometimes|string|max:255',
            'years_experience' => 'sometimes|integer|min:0|max:100',
            'specializations' => 'sometimes|array',
            'specializations.*' => 'string|max:255',
            'is_available' => 'sometimes|boolean',
        ];
    }

    protected function failedValidation(Validator $validator): void
    {
        throw new HttpResponseException(response()->json([
            'success' => false,
            'error' => 'VALIDATION_ERROR',
            'message' => $validator->errors()->first(),
        ], 400));
    }
}
