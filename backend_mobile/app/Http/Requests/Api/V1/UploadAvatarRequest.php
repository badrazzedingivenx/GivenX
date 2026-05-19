<?php

namespace App\Http\Requests\Api\V1;

use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Http\Exceptions\HttpResponseException;

class UploadAvatarRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'avatar' => 'required|file|mimes:jpeg,jpg,png,webp|max:5120',
        ];
    }

    public function messages(): array
    {
        return [
            'avatar.required' => 'Avatar file is required.',
            'avatar.mimes' => 'Unsupported file format. Accepted: JPG, PNG, WebP.',
            'avatar.max' => 'File exceeds the 5 MB limit.',
        ];
    }

    protected function failedValidation(Validator $validator): void
    {
        $errors = $validator->errors()->all();
        $firstError = $errors[0] ?? 'Validation failed';

        // Determine error code
        $errorCode = 'VALIDATION_ERROR';
        if (str_contains($firstError, 'file format') || str_contains($firstError, 'mimes')) {
            $errorCode = 'INVALID_FILE_TYPE';
        } elseif (str_contains($firstError, '5 MB') || str_contains($firstError, 'max')) {
            $errorCode = 'FILE_TOO_LARGE';
        }

        throw new HttpResponseException(response()->json([
            'success' => false,
            'error' => $errorCode,
            'message' => $firstError,
        ], 400));
    }
}
