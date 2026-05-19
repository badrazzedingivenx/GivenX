<?php

namespace App\Http\Requests\Api\V1;

use Illuminate\Foundation\Http\FormRequest;

class RegisterLawyerRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'full_name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email',
            'phone' => 'required|string|max:20',
            'password' => 'required|string|min:8',
            'address' => 'required|string|max:500',
            'speciality' => 'required|string|max:255',
            'bar_association' => 'required|string|max:255',
            'bar_number' => 'required|string|max:255|unique:lawyers,bar_number',
            'years_experience' => 'required|integer|min:0|max:100',
            'bio' => 'nullable|string|max:2000',
            'specializations' => 'nullable|array',
            'specializations.*' => 'string|max:255',
        ];
    }

    public function messages(): array
    {
        return [
            'email.unique' => 'An account with this email address already exists.',
            'bar_number.unique' => 'This bar number is already registered.',
        ];
    }
}
