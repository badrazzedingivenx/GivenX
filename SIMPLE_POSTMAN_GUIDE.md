# Guide Simple pour Tester HEQQI API avec Postman

## Étape 1: Installation

1. Téléchargez Postman: https://www.postman.com/downloads/
2. Installez et ouvrez Postman

## Étape 2: Créer une Collection

1. Dans Postman, cliquez sur "Collections" (à gauche)
2. Cliquez sur le bouton "+" pour créer une nouvelle collection
3. Nommez-la: "HAQ API Test"

## Étape 3: Configurer l'URL de Base

1. Cliquez sur l'icône engrenage (⚙️) en haut à droite
2. Cliquez sur "Globals" 
3. Ajoutez cette variable:
   - **VARIABLE**: `base_url`
   - **INITIAL VALUE**: `http://localhost:8000`
   - **CURRENT VALUE**: `http://localhost:8000`
4. Cliquez sur "Save"

---

# 📋 Tableau Récapitulatif de Tous les Endpoints

## Vue d'ensemble complète de l'API HEQQI Legal Services

| Module | Méthode | Endpoint | Accès |
|--------|---------|----------|-------|
| **Authentication** | POST | `/api/v1/auth/register-user` | Public |
| **Authentication** | POST | `/api/v1/auth/register-lawyer` | Public |
| **Authentication** | POST | `/api/v1/auth/login` | Public |
| **Authentication** | POST | `/api/v1/auth/logout` | User \| Lawyer |
| **Authentication** | POST | `/api/v1/auth/refresh-token` | Public |
| **Authentication** | POST | `/api/v1/auth/forgot-password` | Public |
| **Authentication** | POST | `/api/v1/auth/reset-password` | Public |
| **Authentication** | POST | `/api/v1/auth/change-password` | User \| Lawyer |
| **Authentication** | POST | `/api/v1/auth/verify-email` | Public |
| **Authentication** | POST | `/api/v1/auth/resend-verification` | Public |
| **User Profile** | GET | `/api/v1/users/me` | User |
| **User Profile** | PUT | `/api/v1/users/me` | User |
| **User Profile** | POST | `/api/v1/users/me/avatar` | User |
| **User Profile** | DELETE | `/api/v1/users/me/account` | User |
| **Lawyer Profile** | GET | `/api/v1/lawyers/me` | Lawyer |
| **Lawyer Profile** | PUT | `/api/v1/lawyers/me` | Lawyer |
| **Lawyer Profile** | POST | `/api/v1/lawyers/me/avatar` | Lawyer |
| **Lawyer Profile** | GET | `/api/v1/lawyers/me/stats` | Lawyer |
| **Lawyer Profile** | PUT | `/api/v1/lawyers/me/availability` | Lawyer |
| **Lawyer Discovery** | GET | `/api/v1/lawyers` | User |
| **Lawyer Discovery** | GET | `/api/v1/lawyers/{id}` | User |
| **Lawyer Discovery** | POST | `/api/v1/lawyers/{id}/reviews` | User |
| **Lawyer Discovery** | GET | `/api/v1/lawyers/domains` | User |
| **Legal Matching** | POST | `/api/v1/matching/request` | User |
| **Legal Matching** | GET | `/api/v1/matching/history` | User |
| **Appointments** | GET | `/api/v1/appointments/` | User \| Lawyer |
| **Appointments** | POST | `/api/v1/appointments/` | User |
| **Appointments** | GET | `/api/v1/appointments/{id}` | User \| Lawyer |
| **Appointments** | PUT | `/api/v1/appointments/{id}` | User \| Lawyer |
| **Appointments** | PATCH | `/api/v1/appointments/{id}/status` | User \| Lawyer |
| **Appointments** | GET | `/api/v1/appointments/lawyer/{id}/availability` | User |
| **Payments** | GET | `/api/v1/payments/` | User \| Lawyer |
| **Payments** | POST | `/api/v1/payments/initiate` | User |
| **Payments** | GET | `/api/v1/payments/{id}` | User \| Lawyer |
| **Payments** | POST | `/api/v1/payments/{id}/refund` | User |
| **Payments** | GET | `/api/v1/payments/{id}/invoice` | User \| Lawyer |
| **Payments** | POST | `/api/v1/payments/webhook` | System |
| **Messaging** | GET | `/api/v1/conversations/` | User \| Lawyer |
| **Messaging** | POST | `/api/v1/conversations/` | User |
| **Messaging** | GET | `/api/v1/conversations/{id}` | User \| Lawyer |
| **Messaging** | GET | `/api/v1/conversations/{id}/messages` | User \| Lawyer |
| **Messaging** | POST | `/api/v1/conversations/{id}/messages` | User \| Lawyer |
| **Messaging** | POST | `/api/v1/conversations/{id}/messages/file` | User \| Lawyer |
| **Messaging** | PATCH | `/api/v1/conversations/{id}/read` | User \| Lawyer |
| **Notifications** | GET | `/api/v1/notifications/` | User \| Lawyer |
| **Notifications** | PATCH | `/api/v1/notifications/{id}/read` | User \| Lawyer |
| **Notifications** | PATCH | `/api/v1/notifications/read-all` | User \| Lawyer |
| **Notifications** | DELETE | `/api/v1/notifications/{id}` | User \| Lawyer |
| **Notifications** | DELETE | `/api/v1/notifications/all` | User \| Lawyer |
| **Notifications** | POST | `/api/v1/notifications/device-token` | User \| Lawyer |
| **Notifications** | DELETE | `/api/v1/notifications/device-token` | User \| Lawyer |
| **Live Sessions** | GET | `/api/v1/live-sessions/` | User |
| **Live Sessions** | POST | `/api/v1/live-sessions/` | Lawyer |
| **Live Sessions** | GET | `/api/v1/live-sessions/{id}` | User \| Lawyer |
| **Live Sessions** | PATCH | `/api/v1/live-sessions/{id}/end` | Lawyer |
| **Live Sessions** | GET | `/api/v1/live-sessions/{id}/comments` | User \| Lawyer |
| **Stories & Reels** | GET | `/api/v1/stories` | User |
| **Stories & Reels** | POST | `/api/v1/stories` | Lawyer |
| **Stories & Reels** | DELETE | `/api/v1/stories/{id}` | Lawyer |
| **Stories & Reels** | POST | `/api/v1/stories/{id}/view` | User |
| **Stories & Reels** | GET | `/api/v1/reels` | User |
| **Stories & Reels** | POST | `/api/v1/reels` | Lawyer |
| **Stories & Reels** | DELETE | `/api/v1/reels/{id}` | Lawyer |
| **Stories & Reels** | POST | `/api/v1/reels/{id}/like` | User |
| **Stories & Reels** | POST | `/api/v1/reels/{id}/view` | User |
| **Document Vault** | GET | `/api/v1/documents/` | User |
| **Document Vault** | POST | `/api/v1/documents/` | User |
| **Document Vault** | GET | `/api/v1/documents/{id}` | User |
| **Document Vault** | PATCH | `/api/v1/documents/{id}` | User |
| **Document Vault** | DELETE | `/api/v1/documents/{id}` | User |
| **Document Vault** | POST | `/api/v1/documents/{id}/share` | User |
| **Dashboard** | GET | `/api/v1/dashboard/user` | User |
| **Dashboard** | GET | `/api/v1/dashboard/lawyer` | Lawyer |
| **Dashboard** | GET | `/api/v1/dashboard/lawyer/schedule` | Lawyer |
| **Dashboard** | POST | `/api/v1/dashboard/lawyer/tasks` | Lawyer |
| **Dashboard** | PATCH | `/api/v1/dashboard/lawyer/tasks/{id}` | Lawyer |
| **Dashboard** | DELETE | `/api/v1/dashboard/lawyer/tasks/{id}` | Lawyer |
| **Settings** | GET | `/api/v1/settings/` | User \| Lawyer |
| **Settings** | PUT | `/api/v1/settings/` | User \| Lawyer |
| **Settings** | DELETE | `/api/v1/settings/sessions` | User \| Lawyer |
| **Admin** | GET | `/api/v1/admin/lawyers/pending` | Admin |
| **Admin** | PATCH | `/api/v1/admin/lawyers/{id}/verify` | Admin |
| **Admin** | PATCH | `/api/v1/admin/users/{id}/suspend` | Admin |
| **Admin** | GET | `/api/v1/admin/stats` | Admin |

---

# 🔐 AUTHENTIFICATION - Tests Complets

---

## Test 1: Enregistrer un Client

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/auth/register-user

HEADERS:
  Content-Type: application/json

BODY:
{
  "full_name": "Ahmed Benali",
  "email": "ahmed@email.com",
  "phone": "+212600000001",
  "password": "password123",
  "address": "Casablanca, Maroc"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "user": { ... },
    "token": "eyJ0eXAiOiJKV1Qi...",
    "refresh_token": "..."
  },
  "message": "User registered successfully. Please verify your email."
}
```

> **IMPORTANT**: Copiez le `token` et `refresh_token` pour les tests suivants!

---

## Test 2: Enregistrer un Avocat

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/auth/register-lawyer

HEADERS:
  Content-Type: application/json

BODY:
{
  "full_name": "Me. Karim Alaoui",
  "email": "karim@avocat.ma",
  "phone": "+212600000002",
  "password": "password123",
  "address": "123 Blvd Mohammed V, Casablanca",
  "speciality": "Droit des affaires",
  "bar_association": "Barreau de Casablanca",
  "bar_number": "CASA-2024-001",
  "years_experience": 10,
  "bio": "Expert en droit commercial"
}
```

---

## Test 3: Connexion (Login)

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/auth/login

HEADERS:
  Content-Type: application/json

BODY:
{
  "email": "ahmed@email.com",
  "password": "password123"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "profile": {
      "id": 1,
      "full_name": "Ahmed Benali",
      "email": "ahmed@email.com",
      "role": "user",
      "status": "active"
    },
    "token": "eyJ0eXAiOiJKV1Qi...",
    "refresh_token": "..."
  }
}
```

---

## Test 4: Rafraîchir le Token (Refresh Token)

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/auth/refresh-token

HEADERS:
  Content-Type: application/json

BODY:
{
  "refresh_token": "votre_refresh_token_ici"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "token": "nouveau_jwt_token",
    "refresh_token": "nouveau_refresh_token"
  }
}
```

> L'ancien refresh token est invalidé après utilisation!

---

## Test 5: Mot de Passe Oublié (Forgot Password)

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/auth/forgot-password

HEADERS:
  Content-Type: application/json

BODY:
{
  "email": "ahmed@email.com"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "message": "Reset link sent if account exists"
}
```

> **Note**: La réponse est identique que l'email existe ou non (sécurité anti-énumération)

---

## Test 6: Réinitialiser le Mot de Passe (Reset Password)

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/auth/reset-password

HEADERS:
  Content-Type: application/json

BODY:
{
  "token": "le_token_recu_par_email",
  "new_password": "nouveaumdp123",
  "confirm_password": "nouveaumdp123"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "message": "Password updated successfully"
}
```

> **⚠️ Les champs obligatoires:**
> - `token` - Token reçu par email
> - `new_password` - Nouveau mot de passe (min 8 caractères)
> - `confirm_password` - Doit correspondre exactement à `new_password`

---

## Test 7: Changer le Mot de Passe (Change Password)

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/auth/change-password

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_ici

BODY:
{
  "current_password": "password123",
  "new_password": "nouveaumdp123",
  "confirm_password": "nouveaumdp123"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "message": "Password changed successfully"
}
```

> **⚠️ Nécessite authentification!** Ajoutez le header `Authorization: Bearer <token>`

---

## Test 8: Vérifier l'Email (Verify Email)

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/auth/verify-email

HEADERS:
  Content-Type: application/json

BODY:
{
  "email": "ahmed@email.com",
  "code": "123456"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "message": "Email verified successfully"
}
```

> **⚠️ Le champ est `code` (pas `otp`)** - Code à 6 chiffres reçu par email

---

## Test 9: Renvoyer le Code de Vérification

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/auth/resend-verification

HEADERS:
  Content-Type: application/json

BODY:
{
  "email": "ahmed@email.com"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "message": "Verification code resent"
}
```

> **Note**: Cet endpoint est limité en taux (rate-limited) pour éviter les abus

---

## Test 10: Déconnexion (Logout)

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/auth/logout

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_ici

BODY:
{
  "refresh_token": "votre_refresh_token_ici"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

---

# 📋 RÉSUMÉ DES CHAMPS

| Endpoint | Champ | Type | Obligatoire | Description |
|----------|-------|------|-------------|-------------|
| **refresh-token** | `refresh_token` | string | ✅ | Refresh token valide
| **forgot-password** | `email` | string | ✅ | Email du compte
| **reset-password** | `token` | string | ✅ | Token de réinitialisation
| | `new_password` | string | ✅ | Nouveau mot de passe (min 8)
| | `confirm_password` | string | ✅ | Doit correspondre à new_password
| **change-password** | `current_password` | string | ✅ | Mot de passe actuel
| | `new_password` | string | ✅ | Nouveau mot de passe (min 8)
| | `confirm_password` | string | ✅ | Doit correspondre à new_password
| **verify-email** | `email` | string | ✅ | Email à vérifier
| | `code` | string | ✅ | Code OTP à 6 chiffres
| **resend-verification** | `email` | string | ✅ | Email pour recevoir le code

---

# 👤 USER PROFILE APIs - Tests Complets

> **⚠️ Tous ces endpoints nécessitent un token JWT et un rôle CLIENT**

---

## Test 11: Voir Mon Profil (Get User Profile)

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/users/me

HEADERS:
  Authorization: Bearer votre_token_jwt_ici
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "20",
    "full_name": "Hajar test",
    "email": "hajarelmounaichet04@gmail.com",
    "phone": "+212600000001",
    "address": "Casablanca, Maroc",
    "avatar_url": null,
    "role": "user",
    "status": "active",
    "created_at": "2026-04-21T16:42:28+00:00",
    "updated_at": "2026-04-22T14:14:06+00:00"
  },
  "message": ""
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Test 12: Modifier Mon Profil (Update User Profile)

```
MÉTHODE: PUT
URL: {{base_url}}/api/v1/users/me

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_ici

BODY:
{
  "full_name": "Hajar Mise à Jour",
  "phone": "+212661234567"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "20",
    "full_name": "Hajar Mise à Jour",
    "email": "hajarelmounaichet04@gmail.com",
    "phone": "+212661234567",
    "address": "Casablanca, Maroc",
    "avatar_url": null,
    "role": "user",
    "status": "active",
    "created_at": "2026-04-21T16:42:28+00:00",
    "updated_at": "2026-04-22T14:20:00+00:00"
  },
  "message": ""
}
```

> **Note**: Seuls les champs fournis sont mis à jour. Les champs omis gardent leur valeur actuelle.

**Champs disponibles (tous optionnels):**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `full_name` | string | No | Updated display name |
| `phone` | string | No | Updated contact number |
| `address` | string | No | Updated physical address |

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Submitted field values fail validation |
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Test 13: Uploader un Avatar (Upload User Avatar)

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/users/me/avatar

HEADERS:
  Authorization: Bearer votre_token_jwt_ici

BODY: (form-data)
  avatar = [Sélectionner un fichier image]
```

> **⚠️ IMPORTANT dans Postman:**
> - Dans l'onglet **Body**, sélectionnez **form-data**
> - Key: `avatar`, Type: **File** (cliquez sur la flèche à côté de Key)
> - Value: Cliquez sur "Select Files" et choisissez une image

**Formats acceptés:** JPG, PNG, WebP (max 5 MB)

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "avatar_url": "/storage/avatars/20_1713702000.jpg"
  },
  "message": ""
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | INVALID_FILE_TYPE | Unsupported file format (not JPG/PNG/WebP) |
| 400 | FILE_TOO_LARGE | File exceeds the 5 MB limit |
| 401 | UNAUTHORIZED | Access token is missing or invalid |

**Exemple réponse erreur:**
```json
{
  "success": false,
  "error": "INVALID_FILE_TYPE",
  "message": "Unsupported file format. Accepted: JPG, PNG, WebP."
}
```

---

## Test 14: Supprimer Mon Compte (Delete User Account)

```
MÉTHODE: DELETE
URL: {{base_url}}/api/v1/users/me/account

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_ici

BODY:
{
  "password": "Hajar1234"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "Account deleted successfully"
}
```

> **⚠️ ATTENTION**: Cette action est IRRÉVERSIBLE! Le compte et toutes les données associées seront supprimés définitivement.

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | WRONG_PASSWORD | The provided confirmation password is incorrect |
| 401 | UNAUTHORIZED | Access token is missing or invalid |

**Exemple réponse erreur (mauvais mot de passe):**
```json
{
  "success": false,
  "error": "WRONG_PASSWORD",
  "message": "The provided confirmation password is incorrect."
}
```

---

# ⚖️ LAWYER PROFILE APIs - Tests Complets

> **⚠️ Tous ces endpoints nécessitent un token JWT et un rôle LAWYER**

---

## Test 15: Voir Profil Avocat (Get Lawyer Profile - Self)

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/lawyers/me

HEADERS:
  Authorization: Bearer votre_token_jwt_ici
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "full_name": "Me. Karim Alaoui",
    "email": "karim@avocat.ma",
    "speciality": "Droit des affaires",
    "bar_number": "CASA-2024-001",
    "is_verified": true,
    "is_available": true,
    "rating": 4.8,
    "review_count": 27,
    "status": "active",
    "updated_at": "ISO8601"
  }
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Test 16: Modifier Profil Avocat (Update Lawyer Profile)

```
MÉTHODE: PUT
URL: {{base_url}}/api/v1/lawyers/me

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_ici

BODY:
{
  "full_name": "Me. Karim Alaoui",
  "phone": "+212600000002",
  "bio": "Expert en droit commercial",
  "speciality": "Droit des affaires",
  "years_experience": 12,
  "is_available": true
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    // updated lawyer object
  }
}
```

**Champs disponibles (tous optionnels):**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `full_name` | string | No | Updated display name |
| `phone` | string | No | Updated contact number |
| `address` | string | No | Updated office address |
| `bio` | string | No | Updated professional biography |
| `speciality` | string | No | Updated primary speciality |
| `years_experience` | integer | No | Updated years of experience |
| `specializations` | string[] | No | Updated list of secondary specializations |
| `is_available` | boolean | No | Toggle availability for new appointments |

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Submitted field values fail validation |
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Test 17: Statistiques Dashboard Avocat

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/lawyers/me/stats

HEADERS:
  Authorization: Bearer votre_token_jwt_ici
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "total_clients": 42,
    "active_clients": 18,
    "new_requests": 5,
    "closed_cases": 24,
    "total_revenue_month": 8500.0,
    "total_revenue_year": 76000.0,
    "average_rating": 4.7,
    "monthly_revenue": [
      { "month": "Jan", "amount": 6200.0 }
    ]
  }
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Test 18: Définir l'Horaires de Disponibilité

```
MÉTHODE: PUT
URL: {{base_url}}/api/v1/lawyers/me/availability

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_ici

BODY:
{
  "slot_duration_min": 60,
  "buffer_between_min": 15,
  "schedule": [
    {
      "day": "monday",
      "is_working": true,
      "start_time": "09:00",
      "end_time": "17:00",
      "break_start": "12:00",
      "break_end": "13:00"
    },
    {
      "day": "sunday",
      "is_working": false
    }
  ]
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "schedule": [
      // schedule array
    ]
  }
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Submitted field values fail validation |
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

# 🔍 Lawyer Discovery APIs (Browse & Search)

## Browse & Search Lawyers

Retourne une liste paginée et filtrable d'avocats vérifiés et actifs.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/lawyers

HEADERS:
  Authorization: Bearer votre_token_jwt_ici
```

**Query Parameters:**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| page | integer | No | 1 | Page number for pagination |
| limit | integer | No | 20 | Results per page (max 100) |
| search | string | No | — | Free-text search on name and speciality |
| domaine | string | No | — | Filter by legal domain |
| city | string | No | — | Filter by city |
| min_rating | float | No | — | Minimum rating threshold (e.g., 4.0) |
| min_years | integer | No | — | Minimum years of experience |
| is_available | boolean | No | — | Filter for currently available lawyers |
| sort_by | string | No | rating | Sort field: rating \| reviews \| experience \| name |
| sort_order | string | No | desc | Sort direction: asc \| desc |

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "lawyers": [
      {
        "id": "uuid",
        "full_name": "string",
        "speciality": "Droit Pénal",
        "city": "Casablanca",
        "rating": 4.9,
        "review_count": 52,
        "years_experience": 12,
        "is_verified": true,
        "is_available": true
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 20,
      "total": 240,
      "total_pages": 12
    }
  }
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Get Lawyer Details

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/lawyers/{id}

HEADERS:
  Authorization: Bearer votre_token_jwt_ici
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 404 | NOT_FOUND | Lawyer not found |

---

## Get Legal Domains

Retourne la liste complète des domaines juridiques disponibles avec le nombre d'avocats vérifiés dans chaque domaine.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/lawyers/domains
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "domains": [
      { "id": "uuid", "name": "Droit Pénal", "lawyer_count": 42 },
      { "id": "uuid", "name": "Droit Civil", "lawyer_count": 35 }
    ]
  }
}
```

---

## Submit Lawyer Review

Permet à un client de soumettre une évaluation (étoiles) et un commentaire optionnel pour un avocat après une consultation terminée.

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/lawyers/{id}/reviews

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_ici

BODY:
{
  "rating": 5,
  "comment": "Excellent service.",
  "appointment_id": "uuid"
}
```

**Request Body Parameters:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| rating | integer | Yes | Star rating from 1 (lowest) to 5 (highest) |
| comment | string | No | Optional written review, maximum 500 characters |
| appointment_id | uuid | Yes | ID of the completed appointment this review is for |

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "rating": 5,
    "comment": "Excellent service.",
    "date": "2026-04-22T12:00:00Z"
  }
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Required fields missing or rating is out of range |
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | NO_COMPLETED_APPOINTMENT | You must have a completed appointment to leave a review. |
| 409 | REVIEW_ALREADY_SUBMITTED | A review for this appointment has already been submitted |

**Exemple réponse erreur (pas de rendez-vous complété):**
```json
{
  "success": false,
  "error": "NO_COMPLETED_APPOINTMENT",
  "message": "You must have a completed appointment to leave a review."
}
```

---

# 🎯 4.5 Legal Matching APIs

> **⚠️ Tous ces endpoints nécessitent un token JWT et un rôle USER (Client)**

---

## Submit Matching Request

Processes a client's legal situation description and returns a ranked list of lawyer matches with compatibility scores, match rationale, estimated costs, and available time slots.

```text
MÉTHODE: POST
URL: {{base_url}}/api/v1/matching/request

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_ici

BODY:
{
  "legal_domain": "Droit du Travail",
  "description": "Description de ma situation juridique (min 50 caractères)...",
  "urgency": "high",
  "budget_range": "3000-5000 MAD",
  "preferred_city": "Casablanca"
}
```

**Request Body Parameters:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `legal_domain` | string | Yes | The relevant legal domain (e.g., Droit du Travail) |
| `description` | string | Yes | Description of the legal situation, minimum 50 characters |
| `urgency` | string | Yes | Case urgency level: low \| medium \| high |
| `budget_range` | string | No | Budget range in MAD (e.g., '300–600 MAD') |
| `preferred_city` | string | No | Preferred city for the lawyer |

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "match_id": "uuid",
    "matches": [
      {
        "lawyer": {
          "id": "uuid",
          "full_name": "Me. Karim Alaoui",
          "speciality": "Droit du Travail"
        },
        "match_score": 92.5,
        "match_reasons": [
          "Specializes in labor law",
          "Available this week"
        ],
        "estimated_cost": "400–600 MAD",
        "availability_slots": [
          {
            "date": "2025-04-10",
            "times": [
              "09:00",
              "14:00"
            ]
          }
        ]
      }
    ]
  }
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Required fields missing or description is too short |
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Get Matching History

Retourne l'historique des requêtes de matching de l'utilisateur.

```text
MÉTHODE: GET
URL: {{base_url}}/api/v1/matching/history

HEADERS:
  Authorization: Bearer votre_token_jwt_ici
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

# 📅 APPOINTMENTS (Rendez-vous) - Tests Complets

---

## Test 20: List Appointments

Retourne la liste paginée des rendez-vous de l'utilisateur authentifié.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/appointments?page=1&limit=20&status=all&sort_by=date&sort_order=asc

HEADERS:
  Authorization: Bearer votre_token_jwt_ici
  Accept: application/json
```

**Query Parameters:**
- `page` (optionnel, défaut: 1) - Numéro de page
- `limit` (optionnel, défaut: 20) - Résultats par page
- `status` (optionnel, défaut: all) - Filter: pending, confirmed, completed, cancelled, all
- `from_date` (optionnel) - Date début (YYYY-MM-DD)
- `to_date` (optionnel) - Date fin (YYYY-MM-DD)
- `sort_by` (optionnel, défaut: date) - Tri: date ou status
- `sort_order` (optionnel, défaut: asc) - Direction: asc ou desc

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "appointments": [
      {
        "id": "apt_opji4rpxMyVF",
        "lawyer": {
          "full_name": "Me. Jean Updated",
          "speciality": "Droit Commercial"
        },
        "client": {
          "full_name": "Alice Martin"
        },
        "date": "2026-04-23",
        "time": "10:00:00",
        "duration_min": 60,
        "type": "consultation",
        "status": "confirmed",
        "price": 500.0,
        "notes": "Consultation regarding contract issue",
        "cancellation_reason": null,
        "created_at": "2026-04-22T19:52:10+00:00"
      }
    ],
    "pagination": {
      "current_page": 1,
      "last_page": 1,
      "per_page": 20,
      "total": 1
    }
  },
  "message": ""
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Test 21: Get Lawyer Availability Slots

Retourne les créneaux horaires disponibles pour un avocat spécifique.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/appointments/lawyer/{lawyer_id}/availability?from_date=2026-04-22&to_date=2026-04-29

HEADERS:
  Authorization: Bearer votre_token_jwt_ici
  Accept: application/json
```

**Query Parameters:**
- `from_date` (OBLIGATOIRE) - Date début (YYYY-MM-DD)
- `to_date` (OBLIGATOIRE) - Date fin (YYYY-MM-DD, max 30 jours)

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "lawyer_id": "1",
    "from_date": "2026-04-22",
    "to_date": "2026-04-29",
    "slots": [
      {
        "date": "2026-04-22",
        "times": ["09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"]
      },
      {
        "date": "2026-04-23",
        "times": ["09:00", "10:00", "14:00", "15:00"]
      }
    ]
  },
  "message": ""
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Required fields missing or date range too large |
| 400 | DATE_RANGE_TOO_LARGE | Date range cannot exceed 30 days |
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 404 | LAWYER_NOT_FOUND | Lawyer not found |

---

## Test 22: Book Appointment

Crée un nouveau rendez-vous entre un client et un avocat.

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/appointments

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_client
  Accept: application/json

BODY:
{
  "lawyer_id": "1",
  "date": "2026-04-23",
  "time": "10:00",
  "type": "consultation",
  "notes": "Consultation regarding contract issue",
  "duration_min": 60
}
```

**Body Parameters:**
- `lawyer_id` (OBLIGATOIRE) - ID de l'avocat
- `date` (OBLIGATOIRE) - Date (YYYY-MM-DD)
- `time` (OBLIGATOIRE) - Heure (HH:MM)
- `type` (OBLIGATOIRE) - Type: consultation, suivi, reunion
- `notes` (optionnel) - Notes pour l'avocat
- `duration_min` (optionnel, défaut: 60) - Durée en minutes

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "apt_opji4rpxMyVF",
    "lawyer": {
      "full_name": "Me. Jean Updated",
      "speciality": "Droit Commercial"
    },
    "client": {
      "full_name": "Alice Martin"
    },
    "date": "2026-04-23",
    "time": "10:00:00",
    "duration_min": 60,
    "type": "consultation",
    "status": "pending",
    "price": 500.0,
    "notes": "Consultation regarding contract issue",
    "cancellation_reason": null,
    "created_at": "2026-04-22T19:52:10+00:00"
  },
  "message": "Appointment booked successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Required fields missing or values are invalid |
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | LAWYER_NOT_AVAILABLE | The lawyer is not accepting appointments |
| 404 | LAWYER_NOT_FOUND | Lawyer not found |
| 409 | SLOT_NOT_AVAILABLE | The requested time slot is already booked or outside working hours |

---

## Test 23: Update Appointment Status

Met à jour le statut d'un rendez-vous.

```
MÉTHODE: PATCH
URL: {{base_url}}/api/v1/appointments/{appointment_id}/status

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt
  Accept: application/json

BODY (Confirm by Lawyer):
{
  "status": "confirmed"
}

BODY (Cancel by Client):
{
  "status": "cancelled",
  "cancellation_reason": "Client has a scheduling conflict"
}

BODY (Complete by Lawyer):
{
  "status": "completed"
}
```

**Body Parameters:**
- `status` (OBLIGATOIRE) - New status: confirmed, completed, cancelled
- `cancellation_reason` (CONDITIONNEL) - Required when status is "cancelled"

**Règles de transition:**
- Seul l'avocat peut confirmer (`confirmed`)
- Seul l'avocat peut marquer comme terminé (`completed`)
- Les deux parties peuvent annuler (`cancelled`)
- Impossible de modifier un rendez-vous `completed` ou `cancelled`

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "apt_opji4rpxMyVF",
    "lawyer": {
      "full_name": "Me. Jean Updated",
      "speciality": "Droit Commercial"
    },
    "client": {
      "full_name": "Alice Martin"
    },
    "date": "2026-04-23",
    "time": "10:00:00",
    "duration_min": 60,
    "type": "consultation",
    "status": "confirmed",
    "price": 500.0,
    "notes": "Consultation regarding contract issue",
    "cancellation_reason": null,
    "created_at": "2026-04-22T19:52:10+00:00"
  },
  "message": "Appointment status updated successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Required fields missing |
| 400 | INVALID_STATUS_TRANSITION | The requested status change is not permitted from the current state |
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | FORBIDDEN | Authenticated user is not a participant of this appointment |
| 404 | APPOINTMENT_NOT_FOUND | Appointment not found |

---

## Test 24: Update Appointment (PUT)

Modifie un rendez-vous existant (notes, duration, date, time).

```
MÉTHODE: PUT
URL: {{base_url}}/api/v1/appointments/{appointment_id}

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt
  Accept: application/json

BODY:
{
  "notes": "Updated notes for this appointment",
  "duration_min": 90
}
```

**Body Parameters:**
- `notes` (optionnel) - Nouvelles notes
- `duration_min` (optionnel) - Nouvelle durée en minutes
- `date` (optionnel) - Nouvelle date (YYYY-MM-DD) - uniquement si status = pending
- `time` (optionnel) - Nouveau temps (HH:MM) - uniquement si status = pending

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "apt_jRiNprJhsCYK",
    "lawyer": {
      "full_name": "Me. Jean Updated",
      "speciality": "Droit Commercial"
    },
    "client": {
      "full_name": "User Full 6"
    },
    "date": "2026-04-24",
    "time": "14:00:00",
    "duration_min": 90,
    "type": "consultation",
    "status": "pending",
    "price": 500.0,
    "notes": "Updated notes for this appointment",
    "cancellation_reason": null,
    "created_at": "2026-04-22T20:00:00+00:00"
  },
  "message": "Appointment updated successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | CANNOT_RESCHEDULE | Only pending appointments can be rescheduled |
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | FORBIDDEN | You are not authorized to update this appointment |
| 404 | APPOINTMENT_NOT_FOUND | Appointment not found |

---

## Test 25: Get Appointment Details

Retourne les détails d'un rendez-vous spécifique.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/appointments/{appointment_id}

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:** (Même format que Test 22)

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | FORBIDDEN | You are not authorized to view this appointment |
| 404 | APPOINTMENT_NOT_FOUND | Appointment not found |

---

# 💰 PAYMENTS & BILLING - Tests Complets

---

## Test 26: Initiate Payment

Initie un paiement pour un rendez-vous confirmé.

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/payments/initiate

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_client
  Accept: application/json

BODY (Card Payment):
{
  "appointment_id": "apt_xxx",
  "method": "card",
  "return_url": "https://haq.ma/payment-success"
}

BODY (Bank Transfer):
{
  "appointment_id": "apt_xxx",
  "method": "bank_transfer"
}

BODY (Payment Link):
{
  "appointment_id": "apt_xxx",
  "method": "payment_link"
}
```

**Body Parameters:**
- `appointment_id` (OBLIGATOIRE) - ID du rendez-vous à payer
- `method` (OBLIGATOIRE) - card, bank_transfer, ou payment_link
- `return_url` (CONDITIONNEL) - URL de retour, requis pour card

**Réponse attendue (Card):**
```json
{
  "success": true,
  "data": {
    "payment_id": "pay_f3h5XYetebUl",
    "appointment_id": "apt_xxx",
    "amount": 500.0,
    "currency": "MAD",
    "status": "Completed",
    "checkout_url": "https://payment.haq.ma/checkout/pay_f3h5XYetebUl",
    "return_url": "https://haq.ma/payment-success",
    "paid_at": "2026-04-22T20:54:37+00:00"
  },
  "message": "Payment initiated successfully."
}
```

**Réponse attendue (Bank Transfer):**
```json
{
  "success": true,
  "data": {
    "payment_id": "pay_xxx",
    "appointment_id": "apt_xxx",
    "amount": 800.0,
    "currency": "MAD",
    "status": "Completed",
    "bank_details": {
      "rib": "007090000100100102584530",
      "bank_name": "Attijariwafa Bank",
      "account_name": "HAQ Platform SARL",
      "reference": "pay_xxx"
    },
    "paid_at": "2026-04-22T20:54:37+00:00"
  },
  "message": "Payment initiated successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | APPOINTMENT_ALREADY_PAID | This appointment has already been paid for |
| 400 | APPOINTMENT_NOT_CONFIRMED | Appointment must be confirmed before payment |
| 404 | APPOINTMENT_NOT_FOUND | No appointment found with the provided ID |
| 404 | CLIENT_NOT_FOUND | Client profile not found |

---

## Test 27: Request Refund

Soumet une demande de remboursement pour un paiement complété.

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/payments/{payment_id}/refund

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_client
  Accept: application/json

BODY:
{
  "reason": "I need to cancel this appointment due to scheduling conflict and personal reasons"
}
```

**Body Parameters:**
- `reason` (OBLIGATOIRE) - Explication de la demande (min 10 caractères)

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "refund_request_id": "refund_abc123",
    "payment_id": "pay_xxx",
    "status": "pending_review",
    "amount": 500.0,
    "reason": "I need to cancel this appointment...",
    "created_at": "2026-04-22T20:54:37+00:00"
  },
  "message": "Refund request submitted successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | NOT_ELIGIBLE_FOR_REFUND | The payment does not qualify for a refund based on platform policy |
| 400 | REFUND_EXISTS | Refund already requested for this payment |
| 403 | FORBIDDEN | You are not authorized to request a refund for this payment |
| 404 | PAYMENT_NOT_FOUND | Payment not found |

---

## Test 28: Payment Gateway Webhook

Reçoit les notifications de statut des passerelles de paiement (CMI, Stripe, etc.)

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/payments/webhook

HEADERS:
  Content-Type: application/json
  X-Webhook-Signature: signature_du_gateway

BODY (Stripe Example):
{
  "payment_id": "pay_xxx",
  "status": "completed",
  "gateway": "stripe",
  "transaction_id": "txn_test_123"
}
```

**Réponse attendue:**
```json
{
  "received": true
}
```

> **Note**: Cet endpoint est pour usage système uniquement. L'authentification se fait via signature cryptographique.

---

## Test 29: Get Payment Details

Retourne les détails d'un paiement spécifique.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/payments/{payment_id}

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "pay_xxx",
    "date": "2026-04-22",
    "status": "Completed",
    "subject": "Consultation - Test appointment",
    "method": "card",
    "amount": 500.0,
    "amount_text": "500.00 MAD",
    "currency": "MAD",
    "paid_at": "2026-04-22T20:54:37+00:00",
    "other_party": {
      "name": "Me. Jean Updated"
    },
    "appointment_id": "apt_xxx",
    "invoice_available": true
  },
  "message": ""
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | FORBIDDEN | You are not authorized to view this payment |
| 404 | PAYMENT_NOT_FOUND | Payment not found |

---

## Test 30: Get Invoice

Récupère la facture d'un paiement.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/payments/{payment_id}/invoice

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "inv_xxx",
    "payment_id": "pay_xxx",
    "date": "2026-04-22",
    "status": "issued",
    "amount": 500.0,
    "amount_text": "500.00 MAD",
    "lawyer_name": "Me. Jean Updated",
    "download_url": "http://127.0.0.1:8000/api/v1/payments/pay_xxx/invoice/download"
  },
  "message": ""
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | FORBIDDEN | You are not authorized to view this invoice |
| 404 | PAYMENT_NOT_FOUND | Payment not found |
| 404 | INVOICE_NOT_FOUND | Invoice not found |

---

## Test 31: List Payments

Retourne la liste des paiements de l'utilisateur.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/payments?page=1&per_page=20

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "data": [
      {
        "id": "pay_xxx",
        "date": "2026-04-22",
        "status": "Completed",
        "method": "card",
        "amount": 500.0,
        "amount_text": "500.00 MAD",
        "currency": "MAD",
        "other_party": {
          "name": "Me. Jean Updated"
        },
        "appointment_id": "apt_xxx",
        "invoice_available": true
      }
    ],
    "pagination": {
      "current_page": 1,
      "last_page": 1,
      "per_page": 20,
      "total": 1
    }
  },
  "message": ""
}
```

---

# 💬 MESSAGING (CHAT) - Tests Complets

---

## Test 32: List Conversations

Retourne toutes les conversations de l'utilisateur avec le dernier message et le compteur de messages non lus.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/conversations?page=1&limit=20

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Query Parameters:**
- `page` (optionnel, défaut: 1) - Numéro de page
- `limit` (optionnel, défaut: 20) - Résultats par page

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "conversations": [
      {
        "id": "conv_test_1776891938",
        "lawyer": {
          "full_name": "Me. Jean Updated"
        },
        "client": {
          "full_name": "User Full 6"
        },
        "last_message": {
          "content": "Hello, I need legal advice",
          "sent_at": "2026-04-22T21:05:38+00:00"
        },
        "unread_count_user": 0
      }
    ],
    "pagination": {
      "current_page": 1,
      "last_page": 1,
      "per_page": 20,
      "total": 1
    }
  },
  "message": ""
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Test 33: Create Conversation

Crée une nouvelle conversation entre un client et un avocat.

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/conversations

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_client
  Accept: application/json

BODY (Client creating):
{
  "lawyer_id": "1",
  "initial_message": "Hello, I need legal advice regarding my contract"
}

BODY (Lawyer creating):
{
  "client_id": "6",
  "initial_message": "Hello, following up on your case"
}
```

**Body Parameters:**
- `lawyer_id` (OBLIGATOIRE si client) - ID de l'avocat
- `client_id` (OBLIGATOIRE si lawyer) - ID du client
- `initial_message` (OBLIGATOIRE) - Premier message (max 2000 caractères)

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "conv_xxx",
    "other_party": {
      "id": "1",
      "full_name": "Me. Jean Updated",
      "avatar_url": "...",
      "role": "lawyer"
    },
    "unread_count": 0,
    "created_at": "2026-04-22T21:05:38+00:00"
  },
  "message": "Conversation created successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Required fields missing |
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 409 | CONVERSATION_EXISTS | Conversation already exists |

---

## Test 34: Send Message

Envoie un message texte dans une conversation existante.

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/conversations/{conversation_id}/messages

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt
  Accept: application/json

BODY:
{
  "content": "Hello, I need legal advice"
}
```

**Body Parameters:**
- `content` (OBLIGATOIRE) - Message texte (1-2000 caractères)

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "msg_VOvRMGp23IQRKfuq",
    "content": "Hello, I need legal advice",
    "sender_id": "6",
    "type": "text",
    "sent_at": "2026-04-22T21:05:38+00:00"
  },
  "message": "Message sent successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Content is required or too long |
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | FORBIDDEN | You are not authorized to send messages in this conversation |
| 404 | CONVERSATION_NOT_FOUND | Conversation not found |

---

## Test 35: Send File in Conversation

Envoie un fichier dans une conversation (JPG, PNG, PDF, DOCX, max 20 MB).

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/conversations/{conversation_id}/messages/file

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json

BODY (multipart/form-data):
  file: [select file] (jpg, png, pdf, docx, max 20MB)
  caption: "Contract document for review" (optionnel)
```

**Form Data Parameters:**
- `file` (OBLIGATOIRE) - Fichier (jpg, jpeg, png, pdf, docx, max 20MB)
- `caption` (optionnel) - Légende du fichier (max 1000 caractères)

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "msg_xxx",
    "type": "file",
    "file_url": "/storage/messages/msg_conv_xxx_1234567890_abc12345.pdf",
    "file_name": "contract.pdf",
    "caption": "Contract document for review",
    "sent_at": "2026-04-22T21:05:38+00:00"
  },
  "message": "File sent successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | File is required, invalid format, or too large |
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | FORBIDDEN | You are not authorized to send files in this conversation |
| 404 | CONVERSATION_NOT_FOUND | Conversation not found |
| 500 | UPLOAD_FAILED | Failed to upload file |

---

## Test 36: Get Messages

Récupère les messages d'une conversation avec pagination.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/conversations/{conversation_id}/messages?page=1&per_page=50

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Query Parameters:**
- `per_page` (optionnel, défaut: 50) - Messages par page

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "data": [
      {
        "id": "msg_xxx",
        "content": "Hello, I need legal advice",
        "type": "text",
        "file_url": null,
        "file_name": null,
        "is_from_me": true,
        "sender_name": "User Full 6",
        "sent_at": "2026-04-22T21:05:38+00:00"
      }
    ],
    "pagination": {
      "current_page": 1,
      "last_page": 1,
      "per_page": 50,
      "total": 1
    }
  },
  "message": ""
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | FORBIDDEN | You are not authorized to view this conversation |
| 404 | CONVERSATION_NOT_FOUND | Conversation not found |

---

## Test 37: Mark Conversation as Read

Marque une conversation comme lue, réinitialisant le compteur de messages non lus.

```
MÉTHODE: PATCH
URL: {{base_url}}/api/v1/conversations/{conversation_id}/read

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "Conversation marked as read."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | FORBIDDEN | You are not authorized to access this conversation |
| 404 | CONVERSATION_NOT_FOUND | Conversation not found |

---

## Test 38: Get Conversation Details

Retourne les détails d'une conversation spécifique.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/conversations/{conversation_id}

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:** (Même format que Test 33)

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | FORBIDDEN | You are not authorized to view this conversation |
| 404 | CONVERSATION_NOT_FOUND | Conversation not found |

---

# 🔔 NOTIFICATIONS - Tests Complets

---

## Test 39: List Notifications

Retourne les notifications paginées de l'utilisateur avec le compteur de notifications non lues.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/notifications?page=1&limit=30&is_read=false

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Query Parameters:**
- `page` (optionnel, défaut: 1) - Numéro de page
- `limit` (optionnel, défaut: 30) - Résultats par page
- `is_read` (optionnel) - Filter: true (lues) ou false (non lues)

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "notifications": [
      {
        "id": "1",
        "type": "appointment_confirmed",
        "title": "Appointment Confirmed #2",
        "body": "Your appointment on 2026-04-2 at 10:00",
        "is_read": false,
        "created_at": "2026-04-22T21:15:00+00:00"
      },
      {
        "id": "3",
        "type": "appointment_confirmed",
        "title": "Appointment Confirmed #3",
        "body": "Your appointment on 2026-04-3 at 10:00",
        "is_read": false,
        "created_at": "2026-04-22T21:15:00+00:00"
      }
    ],
    "unread_count": 2,
    "pagination": {
      "current_page": 1,
      "last_page": 1,
      "per_page": 30,
      "total": 2
    }
  },
  "message": ""
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Test 40: Mark Notification as Read

Marque une notification spécifique comme lue.

```
MÉTHODE: PATCH
URL: {{base_url}}/api/v1/notifications/{notification_id}/read

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "Notification marked as read."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 404 | NOTIFICATION_NOT_FOUND | Notification not found |

---

## Test 41: Mark All Notifications as Read

Marque toutes les notifications comme lues.

```
MÉTHODE: PATCH
URL: {{base_url}}/api/v1/notifications/read-all

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "All notifications marked as read."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Test 42: Delete Notification

Supprime une notification spécifique.

```
MÉTHODE: DELETE
URL: {{base_url}}/api/v1/notifications/{notification_id}

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "Notification deleted."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 404 | NOTIFICATION_NOT_FOUND | Notification not found |

---

## Test 43: Delete All Notifications

Supprime toutes les notifications de l'utilisateur.

```
MÉTHODE: DELETE
URL: {{base_url}}/api/v1/notifications/all

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "All notifications deleted."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Test 44: Register Device Token

Enregistre un token de device pour les notifications push (FCM/APNS).

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/notifications/device-token

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt
  Accept: application/json

BODY:
{
  "token": "fcm_token_abc123xyz",
  "platform": "android"
}
```

**Body Parameters:**
- `token` (OBLIGATOIRE) - Token FCM (Android) ou APNS (iOS)
- `platform` (OBLIGATOIRE) - android ou ios

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "Device token registered successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Token or platform is missing |
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Test 45: Delete Device Token

Supprime un token de device (utile lors du logout).

```
MÉTHODE: DELETE
URL: {{base_url}}/api/v1/notifications/device-token

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt
  Accept: application/json

BODY:
{
  "token": "fcm_token_abc123xyz"
}
```

**Body Parameters:**
- `token` (OBLIGATOIRE) - Token du device à supprimer

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "Device token removed successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Token is required |
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

# 📹 LIVE SESSIONS - Tests Complets

---

## Test 46: Start Live Session

Permet à un avocat de créer et démarrer une session vidéo en direct.

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/live-sessions

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json

BODY (Start Immediately):
{
  "topic": "Introduction au Droit du Travail",
  "description": "Session sur les droits des employés",
  "domain": "Droit du Travail"
}

BODY (Scheduled Session):
{
  "topic": "Q&R Droit de la Famille",
  "description": "Questions et réponses sur le divorce",
  "domain": "Droit de la Famille",
  "scheduled_at": "2026-04-25T14:00:00Z"
}
```

**Body Parameters:**
- `topic` (OBLIGATOIRE) - Titre de la session (max 255 caractères)
- `description` (optionnel) - Description détaillée (max 1000 caractères)
- `domain` (optionnel) - Domaine juridique
- `scheduled_at` (optionnel) - ISO8601 datetime pour session future; null pour démarrer immédiatement

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "session_id": "live_abc123xyz",
    "topic": "Introduction au Droit du Travail",
    "status": "LIVE",
    "stream_key": "7p5wGt3OjEx5qjOm5l0DfJm851dxYQKG",
    "rtmp_url": "rtmp://127.0.0.1/live",
    "playback_url": "http://127.0.0.1:8888/7p5wGt3OjEx5qjOm5l0DfJm851dxYQKG/index.m3u8",
    "scheduled_at": null,
    "started_at": "2026-04-22T21:30:00+00:00"
  },
  "message": "Live session started successfully."
}
```

> **Important - Configuration du streaming local:**
> 
> **Étape 1:** Lancer MediaMTX (Docker):
> ```bash
> docker run -d --name mediamtx --restart always -p 1935:1935 -p 8888:8888 bluenviron/mediamtx
> ```
> 
> **Étape 2:** Configurer OBS Studio:
> - Server: `rtmp://127.0.0.1/live`
> - Stream Key: (copier depuis la réponse API)
> - Cliquer "Start Streaming"
> 
> **Étape 3:** Voir le live:
> - **VLC:** Média → Ouvrir un flux réseau → coller `playback_url`
> - **Navigateur:** Utiliser HLS.js (voir exemple dans README)

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | ACTIVE_SESSION_EXISTS | You already have an active live session |
| 400 | VALIDATION_ERROR | Topic is required |
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 404 | LAWYER_NOT_FOUND | Lawyer profile not found |

---

## Test 47: List Live Sessions

Retourne la liste des sessions live avec pagination.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/live-sessions?status=LIVE&domain=Droit+du+Travail&page=1&per_page=20

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Query Parameters:**
- `status` (optionnel, défaut: LIVE) - LIVE, SCHEDULED, ou ended
- `domain` (optionnel) - Filtrer par domaine juridique
- `page` (optionnel, défaut: 1) - Numéro de page
- `per_page` (optionnel, défaut: 20) - Résultats par page

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "data": [
      {
        "id": "live_abc123xyz",
        "topic": "Introduction au Droit du Travail",
        "description": "Session sur les droits des employés",
        "domain": "Droit du Travail",
        "status": "LIVE",
        "viewer_count": 142,
        "thumbnail_url": null,
        "stream_url": "...",
        "started_at": "2026-04-22T21:30:00+00:00",
        "lawyer": {
          "id": "1",
          "full_name": "Me. Jean Updated",
          "avatar_url": "..."
        }
      }
    ],
    "pagination": {
      "current_page": 1,
      "last_page": 1,
      "per_page": 20,
      "total": 1
    }
  },
  "message": ""
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |

---

## Test 48: Get Live Session Details

Retourne les détails d'une session spécifique.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/live-sessions/{session_id}

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "live_abc123xyz",
    "topic": "Introduction au Droit du Travail",
    "description": "Session sur les droits des employés",
    "domain": "Droit du Travail",
    "status": "LIVE",
    "viewer_count": 143,
    "participants": 50,
    "thumbnail_url": null,
    "stream_url": "...",
    "started_at": "2026-04-22T21:30:00+00:00",
    "duration_sec": 3600,
    "lawyer": {
      "id": "1",
      "full_name": "Me. Jean Updated",
      "avatar_url": "..."
    }
  },
  "message": ""
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 404 | SESSION_NOT_FOUND | Live session not found |

---

## Test 49: End Live Session

Termine une session live en cours. Seul l'avocat créateur peut appeler cet endpoint.

```
MÉTHODE: PATCH
URL: {{base_url}}/api/v1/live-sessions/{session_id}/end

HEADERS:
  Authorization: Bearer votre_token_jwt_lawyer (doit être le créateur)
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "session_id": "live_abc123xyz",
    "status": "ended",
    "duration_sec": 3600,
    "total_viewers": 142
  },
  "message": "Live session ended successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | SESSION_NOT_LIVE | This session is not currently live |
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 403 | NOT_SESSION_OWNER | The authenticated lawyer is not the owner of this session |
| 404 | SESSION_NOT_FOUND | Live session not found |

---

## Test 50: Get Live Session Comments

Récupère les commentaires d'une session live avec pagination.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/live-sessions/{session_id}/comments?page=1&per_page=50

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Query Parameters:**
- `per_page` (optionnel, défaut: 50) - Commentaires par page

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "data": [
      {
        "id": "1",
        "author_name": "User Full 6",
        "content": "Très intéressant! Merci pour ces informations",
        "sent_at": "2026-04-22T21:35:00+00:00"
      }
    ],
    "pagination": {
      "current_page": 1,
      "last_page": 1,
      "per_page": 50,
      "total": 1
    }
  },
  "message": ""
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 404 | SESSION_NOT_FOUND | Live session not found |

---

## Test 51: Add Comment to Live Session

Ajoute un commentaire à une session live (User ou Lawyer).

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/live-sessions/{session_id}/comments

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt
  Accept: application/json

BODY:
{
  "content": "Très intéressant! Merci pour ces informations"
}
```

**Body Parameters:**
- `content` (OBLIGATOIRE) - Texte du commentaire (max 500 caractères)

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "1",
    "author_name": "User Full 6",
    "content": "Très intéressant! Merci pour ces informations",
    "sent_at": "2026-04-22T21:35:00+00:00"
  },
  "message": "Comment added successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Content is required |
| 401 | UNAUTHORIZED | Access token is missing or invalid |
| 404 | SESSION_NOT_FOUND | Live session not found |

---

# 📚 STORIES & REELS - Tests Complets

---

## Test 52: Get Stories Feed

Retourne le flux des stories actives.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/stories

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "stories": [
      {
        "id": "story_abc123",
        "lawyer": {
          "full_name": "Me. Jean Updated"
        },
        "media_url": "/storage/stories/images/story_1_123.jpg",
        "caption": "Nouvelle loi sur le travail",
        "is_seen": false,
        "expires_at": "2026-04-23T12:00:00+00:00"
      }
    ]
  },
  "message": ""
}
```

---

## Test 53: Publish Story

Publie une nouvelle story (Lawyer uniquement).

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/stories

HEADERS:
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json

BODY (multipart/form-data):
  media: [File - jpg/png/webp/mp4/mov/avi, max 50MB]  ← IMPORTANT: Choisir "File" dans Postman
  caption: "Nouvelle mise à jour juridique importante"

**Instructions Postman:**
1. Aller dans **Body** → **form-data**
2. Key: `media` → Changer "Text" en **"File"** (dropdown à droite)
3. Cliquer **"Select Files"** et choisir votre image ou vidéo
4. Key: `caption` → Laisser en "Text" et écrire votre description

**Formats acceptés:**
- 📷 Images: jpg, jpeg, png, webp
- 🎥 Vidéos: mp4, mov, avi
- 📏 Taille max: 50MB
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "story_xyz789",
    "media_url": "/storage/stories/images/story_1_456.jpg",
    "caption": "Nouvelle mise à juridique importante",
    "expires_at": "2026-04-24T12:00:00+00:00"
  },
  "message": "Story created successfully."
}
```

---

## Test 54: Get Reels Feed

Retourne le flux des reels avec pagination.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/reels?page=1&limit=20&domain=Droit+du+Travail

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "reels": [
      {
        "id": "reel_abc123",
        "lawyer": {
          "id": "1",
          "full_name": "Me. Jean Updated",
          "avatar_url": "..."
        },
        "title": "Understanding Labor Law",
        "video_url": "/storage/reels/videos/reel_1_123.mp4",
        "likes_count": 340,
        "views_count": 2100,
        "is_liked": false,
        "duration_sec": 45
      }
    ],
    "pagination": {
      "current_page": 1,
      "last_page": 1,
      "per_page": 20,
      "total": 1
    }
  },
  "message": ""
}
```

---

## Test 55: Upload Reel

Upload un reel vidéo (Lawyer uniquement).

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/reels

HEADERS:
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json

BODY (multipart/form-data):
  video: [File - MP4, max 100MB]  ← IMPORTANT: Choisir "File" dans Postman
  thumbnail: [File - jpg/png, optional]  ← Choisir "File" si fourni
  title: "Introduction au Droit du Travail"
  domain: "Droit du Travail"

**Instructions Postman:**
1. Aller dans **Body** → **form-data**
2. Pour `video` et `thumbnail`, changer "Text" en **"File"**
3. Cliquer **"Select Files"** pour choisir vos fichiers
4. `title` et `domain` restent en "Text"

**Formats acceptés:**
- 🎥 Vidéo: mp4 uniquement (max 100MB) - Obligatoire
- 📷 Thumbnail: jpg, png (max 5MB) - Optionnel
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "reel_xyz789",
    "status": "processing",
    "video_url": null,
    "thumbnail_url": null
  },
  "message": "Reel uploaded successfully. Processing will begin shortly."
}
```

---

# 📁 DOCUMENT VAULT - Tests Complets

---

## Test 56: List Documents

Retourne les documents du client avec pagination et stockage.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/documents?page=1&limit=20&search=Contract&type=pdf&sort_by=date&sort_order=desc

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Query Parameters:**
- `page` (optionnel, défaut: 1)
- `limit` (optionnel, défaut: 20)
- `search` (optionnel) - Recherche par nom
- `type` (optionnel, défaut: all) - pdf | image | docx | all
- `sort_by` (optionnel, défaut: date) - name | date | size
- `sort_order` (optionnel, défaut: desc) - asc | desc

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "documents": [
      {
        "id": "doc_abc123",
        "name": "Contract.pdf",
        "file_url": "/storage/documents/doc_1_123.pdf",
        "file_type": "pdf",
        "file_size_kb": 320,
        "is_shared_with_lawyer": false
      }
    ],
    "storage_used_mb": 45.2,
    "storage_limit_mb": 200
  },
  "message": ""
}
```

---

## Test 57: Upload Document

Upload un document dans le vault du client.

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/documents

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json

BODY (multipart/form-data):
  file: [File - pdf/jpg/png/docx/xlsx, max 50MB]  ← IMPORTANT: Choisir "File" dans Postman
  name: "Mon Contract" (optionnel)

**Instructions Postman:**
1. Aller dans **Body** → **form-data**
2. Key: `file` → Changer "Text" en **"File"** (dropdown à droite)
3. Cliquer **"Select Files"** et choisir votre document
4. Key: `name` → Laisser en "Text" (optionnel, sinon utilise le nom du fichier)

**Formats acceptés:**
- 📄 Documents: pdf, docx, xlsx
- 📷 Images: jpg, png
- 📏 Taille max: 50MB par fichier
- 💾 Stockage total: 200MB par client
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "doc_xyz789",
    "name": "Mon Contract.pdf",
    "file_url": "/storage/documents/doc_1_456.pdf",
    "file_type": "pdf",
    "file_size_kb": 320,
    "added_at": "2026-04-22T21:30:00+00:00"
  },
  "message": "Document uploaded successfully."
}
```

**Erreurs possibles:**

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | INVALID_FILE_TYPE | The file format is not supported |
| 400 | FILE_TOO_LARGE | The file exceeds the 50 MB limit |
| 403 | STORAGE_LIMIT_EXCEEDED | The client's 200 MB storage quota has been reached |

---

# ⚙️ SETTINGS - Tests Complets

---

## Test 58: Get Account Settings

Retourne les paramètres du compte.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/settings

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "notifications": {
      "push_enabled": true,
      "email_enabled": true,
      "new_message": true,
      "appointment_reminder": true
    },
    "privacy": {
      "profile_visible": true,
      "show_online_status": false
    },
    "language": "fr",
    "theme": "light"
  },
  "message": ""
}
```

---

## Test 59: Update Account Settings

Met à jour les paramètres du compte.

```
MÉTHODE: PUT
URL: {{base_url}}/api/v1/settings

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt
  Accept: application/json

BODY:
{
  "notifications": {
    "push_enabled": false,
    "email_enabled": true
  },
  "privacy": {
    "profile_visible": false
  },
  "language": "en",
  "theme": "dark"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "notifications": {
      "push_enabled": false,
      "email_enabled": true,
      "new_message": true,
      "appointment_reminder": true
    },
    "privacy": {
      "profile_visible": false,
      "show_online_status": false
    },
    "language": "en",
    "theme": "dark"
  },
  "message": "Settings updated successfully."
}
```

---

## Test 60: Delete All Sessions

Révoque toutes les sessions (logout tous les appareils).

```
MÉTHODE: DELETE
URL: {{base_url}}/api/v1/settings/sessions

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "All sessions revoked successfully."
}
```

---

# 🛡️ ADMIN - Tests Complets

---

## Test 61: List Pending Lawyer Verifications

Retourne la liste des avocats en attente de vérification.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/admin/lawyers/pending?page=1&limit=20

HEADERS:
  Authorization: Bearer votre_token_jwt_admin
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "lawyers": [
      {
        "id": "5",
        "full_name": "Me. New Lawyer",
        "email": "new.lawyer@haq.ma",
        "phone": "+212612345678",
        "speciality": "Droit du Travail",
        "bar_number": "BAR-12345",
        "years_experience": 10,
        "bio": "Avocat expérimenté",
        "created_at": "2026-04-20T10:00:00+00:00"
      }
    ],
    "pagination": {
      "current_page": 1,
      "last_page": 1,
      "per_page": 20,
      "total": 1
    }
  },
  "message": ""
}
```

---

## Test 62: Approve or Reject Lawyer

Approuve ou rejette un compte avocat.

```
MÉTHODE: PATCH
URL: {{base_url}}/api/v1/admin/lawyers/{lawyer_id}/verify

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_admin
  Accept: application/json

BODY (Approve):
{
  "action": "approve"
}

BODY (Reject):
{
  "action": "reject",
  "reject_reason": "Documents insuffisants"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "lawyer_id": "5",
    "status": "active"
  },
  "message": "Lawyer verified successfully."
}
```

---

## Test 63: Suspend or Unsuspend Account

Suspend ou active un compte utilisateur/avocat.

```
MÉTHODE: PATCH
URL: {{base_url}}/api/v1/admin/users/{user_id}/suspend

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_admin
  Accept: application/json

BODY (Suspend):
{
  "action": "suspend",
  "reason": "Violation des conditions d'utilisation"
}

BODY (Unsuspend):
{
  "action": "unsuspend"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "user_id": "6",
    "status": "suspended"
  },
  "message": "User suspended successfully."
}
```

---

## Test 64: Get Platform Analytics

Retourne les statistiques de la plateforme.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/admin/stats

HEADERS:
  Authorization: Bearer votre_token_jwt_admin
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "users": {
      "total_clients": 1240,
      "total_lawyers": 87,
      "verified_lawyers": 75,
      "pending_verifications": 12
    },
    "appointments": {
      "total": 4320,
      "completed": 3800,
      "pending": 520
    },
    "payments": {
      "total_completed": 3500,
      "total_revenue": 285000.0,
      "currency": "MAD"
    },
    "recent_activity": {
      "new_users": [
        {
          "id": "21",
          "full_name": "User Full 6",
          "role": "client",
          "created_at": "2026-04-22T10:00:00+00:00"
        }
      ]
    }
  },
  "message": ""
}
```

---

## Test 65: Delete Story

Supprime une story (Lawyer uniquement).

```
MÉTHODE: DELETE
URL: {{base_url}}/api/v1/stories/{story_id}

HEADERS:
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "Story deleted successfully."
}
```

---

## Test 66: View Story

Marque une story comme vue (User uniquement).

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/stories/{story_id}/view

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "story_abc123",
    "views": 145
  },
  "message": ""
}
```

---

## Test 67: Delete Reel

Supprime un reel (Lawyer uniquement).

```
MÉTHODE: DELETE
URL: {{base_url}}/api/v1/reels/{reel_id}

HEADERS:
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "Reel deleted successfully."
}
```

---

## Test 68: Like/Unlike Reel

Like ou unlike un reel (User uniquement).

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/reels/{reel_id}/like

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "likes_count": 341,
    "is_liked": true
  },
  "message": "Reel liked."
}
```

---

## Test 69: View Reel

Incrémente le compteur de vues d'un reel.

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/reels/{reel_id}/view

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "reel_abc123",
    "views_count": 2101
  },
  "message": ""
}
```

---

## Test 70: Get Document Details

Retourne les détails d'un document.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/documents/{document_id}

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "doc_abc123",
    "title": "Contract.pdf",
    "file_url": "/storage/documents/doc_1_123.pdf",
    "file_type": "pdf",
    "file_size_kb": 320,
    "status": "active",
    "upload_date": "2026-04-22T10:00:00+00:00",
    "client_name": "User Full 6",
    "shares": []
  },
  "message": ""
}
```

---

## Test 71: Update Document

Met à jour un document.

```
MÉTHODE: PATCH
URL: {{base_url}}/api/v1/documents/{document_id}

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt
  Accept: application/json

BODY:
{
  "title": "Updated Contract.pdf"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "doc_abc123",
    "title": "Updated Contract.pdf"
  },
  "message": "Document updated successfully."
}
```

---

## Test 72: Delete Document

Supprime un document.

```
MÉTHODE: DELETE
URL: {{base_url}}/api/v1/documents/{document_id}

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "Document deleted successfully."
}
```

---

## Test 73: Share Document with Lawyer

Partage un document avec un avocat.

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/documents/{document_id}/share

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt
  Accept: application/json

BODY:
{
  "lawyer_id": "1"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "share_abc123",
    "lawyer_name": "Me. Jean Updated",
    "shared_at": "2026-04-22T21:30:00+00:00"
  },
  "message": "Document shared successfully."
}
```

---

## Test 74: Get Lawyer Dashboard

Retourne le tableau de bord de l'avocat.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/dashboard/lawyer

HEADERS:
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "today_schedule": [
      {
        "id": "apt_abc123",
        "time": "14:00",
        "client_name": "User Full 6",
        "status": "confirmed"
      }
    ],
    "pending_requests_count": 4,
    "active_clients_count": 18,
    "unread_messages": 3,
    "revenue_this_month": 8500.0,
    "monthly_revenue_chart": [
      { "month": "Mar", "amount": 7200.0 },
      { "month": "Apr", "amount": 8500.0 }
    ],
    "pending_tasks": [
      {
        "id": "task_abc123",
        "label": "Review contract",
        "is_done": false
      }
    ]
  },
  "message": ""
}
```

---

## Test 75: Get Lawyer Schedule

Retourne l'emploi du temps de l'avocat.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/dashboard/lawyer/schedule

HEADERS:
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "appointments": [
      {
        "id": "apt_abc123",
        "date": "2026-04-23",
        "time": "14:00",
        "client_name": "User Full 6",
        "status": "confirmed"
      }
    ]
  },
  "message": ""
}
```

---

## Test 76: Add Lawyer Task

Ajoute une tâche à la liste de l'avocat.

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/dashboard/lawyer/tasks

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json

BODY:
{
  "label": "Review contract for Client X",
  "due_date": "2026-04-30"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "task_xyz789",
    "label": "Review contract for Client X",
    "due_date": "2026-04-30",
    "is_done": false
  },
  "message": "Task added successfully."
}
```

---

## Test 77: Update Lawyer Task

Met à jour une tâche (marquer comme terminée).

```
MÉTHODE: PATCH
URL: {{base_url}}/api/v1/dashboard/lawyer/tasks/{task_id}

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json

BODY:
{
  "is_done": true
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "task_xyz789",
    "label": "Review contract for Client X",
    "is_done": true
  },
  "message": "Task updated successfully."
}
```

---

## Test 78: Delete Lawyer Task

Supprime une tâche.

```
MÉTHODE: DELETE
URL: {{base_url}}/api/v1/dashboard/lawyer/tasks/{task_id}

HEADERS:
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "Task deleted successfully."
}
```

---

## Test 79: Get Settings

Récupère les paramètres du compte.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/settings/

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "language": "fr",
    "notifications_enabled": true,
    "email_notifications": true,
    "push_notifications": true
  },
  "message": ""
}
```

---

## Test 80: Update Settings

Met à jour les paramètres du compte.

```
MÉTHODE: PUT
URL: {{base_url}}/api/v1/settings/

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt
  Accept: application/json

BODY:
{
  "language": "en",
  "notifications_enabled": true,
  "email_notifications": false,
  "push_notifications": true
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "language": "en",
    "notifications_enabled": true
  },
  "message": "Settings updated successfully."
}
```

---

## Test 81: Revoke All Sessions

Révoque toutes les sessions actives.

```
MÉTHODE: DELETE
URL: {{base_url}}/api/v1/settings/sessions

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": null,
  "message": "All sessions have been revoked."
}
```

---

## Test 82: Get Pending Lawyers (Admin)

Récupère la liste des avocats en attente de vérification.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/admin/lawyers/pending?page=1&limit=20

HEADERS:
  Authorization: Bearer votre_token_jwt_admin
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "lawyers": [
      {
        "id": 123,
        "full_name": "Me. Karim Alaoui",
        "email": "karim@avocat.ma",
        "speciality": "Droit des affaires",
        "bar_number": "CASA-2024-001",
        "status": "pending_verification",
        "created_at": "2026-04-24T10:00:00+00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "limit": 20
  },
  "message": ""
}
```

---

## Test 83: Verify Lawyer (Admin)

Vérifie et active un compte avocat.

```
MÉTHODE: PATCH
URL: {{base_url}}/api/v1/admin/lawyers/{lawyer_id}/verify

HEADERS:
  Authorization: Bearer votre_token_jwt_admin
  Accept: application/json

BODY:
{
  "verified": true
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": 123,
    "status": "active",
    "is_verified": true
  },
  "message": "Lawyer verified successfully."
}
```

---

## Test 84: Suspend User (Admin)

Suspend un compte utilisateur ou avocat.

```
MÉTHODE: PATCH
URL: {{base_url}}/api/v1/admin/users/{user_id}/suspend

HEADERS:
  Authorization: Bearer votre_token_jwt_admin
  Accept: application/json

BODY:
{
  "suspended": true,
  "reason": "Violation des conditions d'utilisation"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": 456,
    "status": "suspended"
  },
  "message": "User suspended successfully."
}
```

---

## Test 85: Get Admin Stats (Admin)

Récupère les statistiques globales de la plateforme.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/admin/stats

HEADERS:
  Authorization: Bearer votre_token_jwt_admin
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "total_users": 1250,
    "total_lawyers": 340,
    "pending_lawyers": 15,
    "total_appointments": 5600,
    "total_revenue": 125000.00,
    "active_sessions": 89
  },
  "message": ""
}
```

---

## Test 86: Get Lawyer Shared Documents

Récupère les documents partagés avec l'avocat.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/lawyers/me/shared-documents

HEADERS:
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "documents": [
      {
        "id": "doc_abc123",
        "title": "Contract.pdf",
        "file_url": "/storage/documents/doc_1_123.pdf",
        "file_type": "pdf",
        "file_size_kb": 320,
        "client_name": "Ahmed Benali",
        "shared_at": "2026-04-24T10:00:00+00:00"
      }
    ]
  },
  "message": ""
}
```

---

## Test 87: Get Lawyer Clients

Récupère la liste des clients de l'avocat.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/lawyers/me/clients

HEADERS:
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "clients": [
      {
        "id": 789,
        "full_name": "Ahmed Benali",
        "email": "ahmed@email.com",
        "phone": "+212600000001",
        "active_cases": 2
      }
    ]
  },
  "message": ""
}
```

---

## Test 88: Get Lawyer Requests

Récupère les demandes de matching reçues par l'avocat.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/lawyers/me/requests

HEADERS:
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "requests": [
      {
        "id": "match_abc123",
        "client_name": "Ahmed Benali",
        "case_description": "Besoin d'aide pour un contrat",
        "status": "pending",
        "created_at": "2026-04-24T10:00:00+00:00"
      }
    ]
  },
  "message": ""
}
```

---

## Test 89: Update Lawyer Request

Accepte ou refuse une demande de matching.

```
MÉTHODE: PATCH
URL: {{base_url}}/api/v1/lawyers/me/requests/{request_id}

HEADERS:
  Content-Type: application/json
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json

BODY:
{
  "status": "accepted"
}
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "id": "match_abc123",
    "status": "accepted"
  },
  "message": "Request updated successfully."
}
```

---

## Test 90: Upload Lawyer Avatar

Met à jour la photo de profil de l'avocat.

```
MÉTHODE: POST
URL: {{base_url}}/api/v1/lawyers/me/avatar

HEADERS:
  Authorization: Bearer votre_token_jwt_lawyer
  Accept: application/json

BODY (form-data):
  avatar: [File - jpg/png, max 2MB]
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "avatar_url": "/storage/avatars/lawyer_123_1234567890.jpg"
  },
  "message": "Avatar uploaded successfully."
}
```

---

## Test 91: View Live Session Comments

Récupère les commentaires d'une session en direct.

```
MÉTHODE: GET
URL: {{base_url}}/api/v1/live-sessions/{session_id}/comments

HEADERS:
  Authorization: Bearer votre_token_jwt
  Accept: application/json
```

**Réponse attendue:**
```json
{
  "success": true,
  "data": {
    "comments": [
      {
        "id": "comment_abc123",
        "user_name": "Ahmed Benali",
        "message": "Très intéressante cette session!",
        "created_at": "2026-04-24T15:30:00+00:00"
      }
    ]
  },
  "message": ""
}
```

---

## Codes d'Erreur Courants

| Code | Signification | Solution |
|------|---------------|----------|
| 401 | Non autorisé | Vérifiez votre token JWT |
| 403 | Interdit | Vous n'avez pas les permissions |
| 404 | Non trouvé | Vérifiez l'ID dans l'URL |
| 409 | Conflit | Email ou données déjà existantes |
| 422 | Validation échouée | Vérifiez les données envoyées |
| 500 | Erreur serveur | Problème côté serveur |

---

# ❌ ERREURS DÉTAILLÉES PAR ENDPOINT

---

## Register Lawyer - Erreurs

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | One or more required fields are missing or invalid |
| 409 | EMAIL_ALREADY_EXISTS | An account with this email address already exists |
| 500 | INTERNAL_SERVER_ERROR | Unexpected server error |

**Exemple réponse erreur:**
```json
{
  "success": false,
  "error": "EMAIL_ALREADY_EXISTS",
  "message": "An account with this email address already exists."
}
```

---

## Login - Erreurs

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Missing or malformed fields |
| 401 | INVALID_CREDENTIALS | Email or password is incorrect |
| 403 | ACCOUNT_SUSPENDED | The account has been suspended by an administrator |
| 403 | ACCOUNT_PENDING_VERIFICATION | The lawyer account is awaiting admin approval |
| 500 | INTERNAL_SERVER_ERROR | Unexpected server error |

**Exemple réponse erreur:**
```json
{
  "success": false,
  "error": "ACCOUNT_SUSPENDED",
  "message": "The account has been suspended by an administrator."
}
```

---

## Logout - Erreurs

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | UNAUTHORIZED | Access token is missing, invalid, or expired |

---

## Refresh Token - Erreurs

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 401 | INVALID_REFRESH_TOKEN | The provided refresh token is not recognized |
| 401 | REFRESH_TOKEN_EXPIRED | The refresh token has expired; user must log in again |

**Exemple réponse erreur:**
```json
{
  "success": false,
  "error": "REFRESH_TOKEN_EXPIRED",
  "message": "The refresh token has expired; user must log in again."
}
```

---

## Forgot Password - Erreurs

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Email field is missing or malformed |

---

## Reset Password - Erreurs

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Fields are missing or do not meet requirements |
| 400 | PASSWORDS_DO_NOT_MATCH | new_password and confirm_password are not identical |
| 400 | INVALID_OR_EXPIRED_TOKEN | The reset token is invalid or has expired |

**Exemple réponse erreur:**
```json
{
  "success": false,
  "error": "INVALID_OR_EXPIRED_TOKEN",
  "message": "The reset token is invalid or has expired."
}
```

---

## Change Password - Erreurs

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | VALIDATION_ERROR | Fields are missing or fail validation rules |
| 400 | WRONG_CURRENT_PASSWORD | The provided current password is incorrect |

**Exemple réponse erreur:**
```json
{
  "success": false,
  "error": "WRONG_CURRENT_PASSWORD",
  "message": "The provided current password is incorrect."
}
```

---

## Verify Email - Erreurs

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 400 | INVALID_CODE | The OTP does not match the expected value |
| 400 | CODE_EXPIRED | The OTP has expired; request a new one |

**Exemple réponse erreur:**
```json
{
  "success": false,
  "error": "CODE_EXPIRED",
  "message": "The OTP has expired; request a new one."
}
```

---

## Resend Verification - Erreurs

| HTTP Code | Error Constant | Description |
|-----------|----------------|-------------|
| 429 | TOO_MANY_REQUESTS | Rate limit exceeded; retry after the indicated number of seconds |

**Exemple réponse erreur:**
```json
{
  "success": false,
  "error": "TOO_MANY_REQUESTS",
  "message": "Rate limit exceeded; retry after the indicated number of seconds.",
  "data": {
    "retry_after": 300
  }
}
```

---

# 📋 RÉSUMÉ COMPLET DES ERREURS

| Endpoint | Error Constant | HTTP Code |
|----------|----------------|----------|
| register-lawyer | VALIDATION_ERROR | 400 |
| register-lawyer | EMAIL_ALREADY_EXISTS | 409 |
| register-lawyer | INTERNAL_SERVER_ERROR | 500 |
| login | VALIDATION_ERROR | 400 |
| login | INVALID_CREDENTIALS | 401 |
| login | ACCOUNT_SUSPENDED | 403 |
| login | ACCOUNT_PENDING_VERIFICATION | 403 |
| login | INTERNAL_SERVER_ERROR | 500 |
| logout | UNAUTHORIZED | 401 |
| refresh-token | INVALID_REFRESH_TOKEN | 401 |
| refresh-token | REFRESH_TOKEN_EXPIRED | 401 |
| forgot-password | VALIDATION_ERROR | 400 |
| reset-password | VALIDATION_ERROR | 400 |
| reset-password | PASSWORDS_DO_NOT_MATCH | 400 |
| reset-password | INVALID_OR_EXPIRED_TOKEN | 400 |
| change-password | VALIDATION_ERROR | 400 |
| change-password | WRONG_CURRENT_PASSWORD | 400 |
| verify-email | INVALID_CODE | 400 |
| verify-email | CODE_EXPIRED | 400 |
| resend-verification | TOO_MANY_REQUESTS | 429 |
| users/me (GET) | UNAUTHORIZED | 401 |
| users/me (PUT) | VALIDATION_ERROR | 400 |
| users/me (PUT) | UNAUTHORIZED | 401 |
| users/me/avatar | INVALID_FILE_TYPE | 400 |
| users/me/avatar | FILE_TOO_LARGE | 400 |
| users/me/avatar | UNAUTHORIZED | 401 |
| users/me/account | WRONG_PASSWORD | 400 |
| users/me/account | UNAUTHORIZED | 401 |
| lawyers/me (GET) | UNAUTHORIZED | 401 |
| lawyers/me (PUT) | VALIDATION_ERROR | 400 |
| lawyers/me (PUT) | UNAUTHORIZED | 401 |
| lawyers/me/stats | UNAUTHORIZED | 401 |
| lawyers/me/availability | VALIDATION_ERROR | 400 |
| lawyers/me/availability | UNAUTHORIZED | 401 |
| lawyers (GET) | UNAUTHORIZED | 401 |
| lawyers/{id} (GET) | UNAUTHORIZED | 401 |
| lawyers/{id} (GET) | NOT_FOUND | 404 |
| lawyers/{id}/reviews | VALIDATION_ERROR | 400 |
| lawyers/{id}/reviews | UNAUTHORIZED | 401 |
| lawyers/{id}/reviews | NO_COMPLETED_APPOINTMENT | 403 |
| lawyers/{id}/reviews | REVIEW_ALREADY_SUBMITTED | 409 |
| matching/request | VALIDATION_ERROR | 400 |
| matching/request | UNAUTHORIZED | 401 |
| matching/history | UNAUTHORIZED | 401 |
