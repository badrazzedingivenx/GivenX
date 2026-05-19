<?php

namespace App\Mail;

use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Mail\Mailable;
use Illuminate\Mail\Mailables\Attachment;
use Illuminate\Mail\Mailables\Content;
use Illuminate\Mail\Mailables\Envelope;
use Illuminate\Queue\SerializesModels;

class PasswordResetToken extends Mailable implements ShouldQueue
{
    use Queueable, SerializesModels;

    public function __construct(
        public string $token,
        public string $userName,
    ) {}

    public function envelope(): Envelope
    {
        return new Envelope(
            subject: 'HEQQI - Réinitialisation de votre mot de passe',
        );
    }

    public function content(): Content
    {
        // URL frontend - à configurer selon votre app
        $frontendUrl = config('app.frontend_url', config('app.url'));
        
        return new Content(
            markdown: 'emails.password-reset',
            with: [
                'token' => $this->token,
                'userName' => $this->userName,
                'resetUrl' => $frontendUrl . '/reset-password?token=' . $this->token,
            ],
        );
    }

    public function attachments(): array
    {
        return [];
    }
}
