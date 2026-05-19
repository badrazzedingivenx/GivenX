<?php

namespace App\Http\Requests\Api\V1;

use Illuminate\Foundation\Http\FormRequest;

class ChangePasswordRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'current_password' => 'required|string',
            'new_password' => 'required|string|min:8',
            'confirm_password' => 'required|string|same:new_password',
        ];
    }

    public function messages(): array
    {
        return [
            'confirm_password.same' => 'The confirm password must exactly match new_password.',
            'new_password.min' => 'The new password must be at least 8 characters.',
        ];
    }
}
