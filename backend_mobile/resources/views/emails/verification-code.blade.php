@component('mail::message')
# Bonjour {{ $userName }} !

Merci de vous être inscrit sur **HEQQI Legal Services**.

Veuillez vérifier votre adresse email en utilisant le code ci-dessous :

@component('mail::panel')
## 📧 Votre code de vérification

# {{ $code }}
@endcomponent

Ce code est valable pendant **30 minutes**. Si vous n'avez pas créé de compte, vous pouvez ignorer cet email.

Merci,<br>
**L'équipe HEQQI Legal Services**
@endcomponent
