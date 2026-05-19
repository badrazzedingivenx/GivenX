@component('mail::message')
# Bonjour {{ $userName }} !

Vous avez demandé la réinitialisation de votre mot de passe sur **HEQQI Legal Services**.

Cliquez sur le bouton ci-dessous pour créer un nouveau mot de passe :

@component('mail::button', ['url' => $resetUrl, 'color' => 'primary'])
Réinitialiser mon mot de passe
@endcomponent

Ce lien est valable pendant **1 heure**.

Si vous n'avez pas demandé cette réinitialisation, vous pouvez ignorer cet email en toute sécurité. Votre mot de passe actuel restera inchangé.

---

**Vous ne voyez pas le bouton ?**  
Copiez et collez ce lien dans votre navigateur :
{{ $resetUrl }}

Merci,<br>
**L'équipe HEQQI Legal Services**
@endcomponent
