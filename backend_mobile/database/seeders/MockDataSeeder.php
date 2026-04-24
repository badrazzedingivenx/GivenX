<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use App\Models\User;
use App\Models\Profile;
use App\Models\Lawyer;
use App\Models\Client;
use App\Models\Message;
use App\Models\Conversation;
use App\Models\Story;
use App\Models\Reel;
use App\Models\LiveSession;
use App\Models\Appointment;
use App\Models\Payment;
use App\Models\Document;
use App\Models\Notification;
use App\Models\Consultation;
use App\Models\Dossier;

class MockDataSeeder extends Seeder
{
    public function run(): void
    {
        $json = '{
  "users": [
    { "id": 1, "email": "jean.dupont@law.com", "password": "password123", "role": "LAWYER" },
    { "id": 2, "email": "marie.legrand@law.com", "password": "password123", "role": "LAWYER" },
    { "id": 3, "email": "sofia.ben@law.com", "password": "password123", "role": "LAWYER" },
    { "id": 4, "email": "marc.villeneuve@law.com", "password": "password123", "role": "LAWYER" },
    { "id": 5, "email": "luc.bertrand@law.com", "password": "password123", "role": "LAWYER" },
    { "id": 6, "email": "alice.client@gmail.com", "password": "password123", "role": "CLIENT" },
    { "id": 7, "email": "robert.brown@yahoo.fr", "password": "password123", "role": "CLIENT" },
    { "id": 8, "email": "claire.martin@outlook.com", "password": "password123", "role": "CLIENT" },
    { "id": 9, "email": "pierre.durand@company.com", "password": "password123", "role": "CLIENT" },
    { "id": 10, "email": "emma.wilson@tech.io", "password": "password123", "role": "CLIENT" }
  ],
  "profiles": [
    { "id": 1, "userId": 1, "full_name": "Jean Dupont", "avatar_url": "https://randomuser.me/api/portraits/men/1.jpg", "phone": "+33 6 12 34 56 78", "role": "LAWYER", "address": "123 Rue de la Loi, Paris" },
    { "id": 2, "userId": 2, "full_name": "Marie Legrand", "avatar_url": "https://randomuser.me/api/portraits/women/2.jpg", "phone": "+33 6 23 45 67 89", "role": "LAWYER", "address": "45 Avenue des Avocats, Lyon" },
    { "id": 3, "userId": 3, "full_name": "Sofia Benali", "avatar_url": "https://randomuser.me/api/portraits/women/3.jpg", "phone": "+212 6 61 22 33 44", "role": "LAWYER", "address": "78 Boulevard Anfa, Casablanca" },
    { "id": 4, "userId": 4, "full_name": "Marc Villeneuve", "avatar_url": "https://randomuser.me/api/portraits/men/4.jpg", "phone": "+33 6 34 56 78 90", "role": "LAWYER", "address": "12 Rue de la Paix, Marseille" },
    { "id": 5, "userId": 5, "full_name": "Luc Bertrand", "avatar_url": "https://randomuser.me/api/portraits/men/5.jpg", "phone": "+33 6 45 67 89 01", "role": "LAWYER", "address": "90 Cours de l\'Intendance, Bordeaux" },
    { "id": 6, "userId": 6, "full_name": "Alice Martin", "avatar_url": "https://randomuser.me/api/portraits/women/6.jpg", "phone": "+33 7 11 22 33 44", "role": "CLIENT", "address": "15 Rue de Rivoli, Paris" },
    { "id": 7, "userId": 7, "full_name": "Robert Brown", "avatar_url": "https://randomuser.me/api/portraits/men/7.jpg", "phone": "+33 7 22 33 44 55", "role": "CLIENT", "address": "London, UK" },
    { "id": 8, "userId": 8, "full_name": "Claire Martin", "avatar_url": "https://randomuser.me/api/portraits/women/8.jpg", "phone": "+33 7 33 44 55 66", "role": "CLIENT", "address": "Nantes, France" },
    { "id": 9, "userId": 9, "full_name": "Pierre Durand", "avatar_url": "https://randomuser.me/api/portraits/men/9.jpg", "phone": "+33 7 44 55 66 77", "role": "CLIENT", "address": "Lille, France" },
    { "id": 10, "userId": 10, "full_name": "Emma Wilson", "avatar_url": "https://randomuser.me/api/portraits/women/10.jpg", "phone": "+33 7 55 66 77 88", "role": "CLIENT", "address": "Berlin, Germany" }
  ],
  "lawyers": [
    {
      "id": "1", "profileId": 1,
      "name": "Maître Jean Dupont",
      "specialty": "Droit Immobilier",
      "speciality": "Droit Immobilier",
      "domaine": "Droit Immobilier",
      "location": "Paris",
      "city": "Paris",
      "avatar_url": "https://randomuser.me/api/portraits/men/1.jpg",
      "rating": 4.8,
      "reviewCount": 124,
      "review_count": 124,
      "experience": 12,
      "years_experience": 12,
      "isVerified": true,
      "is_verified": true,
      "bar_number": "PAR-12345",
      "bio": "Spécialiste en transactions immobilières et litiges de copropriété."
    },
    {
      "id": "2", "profileId": 2,
      "name": "Maître Marie Legrand",
      "speciality": "Droit de la Famille",
      "domaine": "Droit Civil",
      "location": "Lyon",
      "city": "Lyon",
      "avatar_url": "https://randomuser.me/api/portraits/women/2.jpg",
      "rating": 4.9,
      "review_count": 87,
      "years_experience": 15,
      "is_verified": true,
      "bar_number": "LYO-67890",
      "bio": "Accompagnement en divorce, garde d\'enfants et successions."
    },
    {
      "id": "3", "profileId": 3,
      "name": "Maître Sofia Benali",
      "speciality": "Droit Pénal",
      "domaine": "Droit Pénal",
      "location": "Casablanca",
      "city": "Casablanca",
      "avatar_url": "https://randomuser.me/api/portraits/women/3.jpg",
      "rating": 4.7,
      "review_count": 56,
      "years_experience": 8,
      "is_verified": true,
      "bar_number": "CAS-11223",
      "bio": "Défense pénale et assistance aux victimes d\'infractions."
    },
    {
      "id": "4", "profileId": 4,
      "name": "Maître Marc Villeneuve",
      "speciality": "Droit du Travail",
      "domaine": "Droit du Travail",
      "location": "Marseille",
      "city": "Marseille",
      "avatar_url": "https://randomuser.me/api/portraits/men/4.jpg",
      "rating": 4.6,
      "review_count": 43,
      "years_experience": 10,
      "is_verified": false,
      "bar_number": "MAR-44556",
      "bio": "Conseil aux entreprises et défense des salariés aux prud\'hommes."
    },
    {
      "id": "5", "profileId": 5,
      "name": "Maître Luc Bertrand",
      "speciality": "Droit des Affaires",
      "domaine": "Droit des Affaires",
      "location": "Bordeaux",
      "city": "Bordeaux",
      "avatar_url": "https://randomuser.me/api/portraits/men/5.jpg",
      "rating": 4.5,
      "review_count": 31,
      "years_experience": 7,
      "is_verified": true,
      "bar_number": "BOR-77889",
      "bio": "Accompagnement juridique pour la création et la gestion d\'entreprises."
    }
  ],
  "clients": [
    { "id": 1, "profileId": 6, "company_name": null },
    { "id": 2, "profileId": 7, "company_name": "Brown Consulting" },
    { "id": 3, "profileId": 8, "company_name": null },
    { "id": 4, "profileId": 9, "company_name": "Durand & Co" },
    { "id": 5, "profileId": 10, "company_name": "Tech Solutions IO" }
  ],
  "consultations": [
    { "id": 1, "clientId": 1, "lawyerId": 1, "date": "2024-06-01T10:00:00Z", "status": "accepted", "subject": "Achat appartement Paris 15" }
  ],
  "dossiers": [
    {
      "id": "dos_001",
      "case_number": "FR-2024-001",
      "category": "IMMOBILIER",
      "status": "OPEN",
      "opening_date": "2024-01-10",
      "lawyer_id": "1",
      "lawyer_name": "Jean Dupont",
      "lawyer_specialty": "Droit Immobilier",
      "client_name": "Alice Martin",
      "progress": 45
    }
  ]
}';

        $data = json_decode($json, true);

        foreach ($data['users'] as $u) {
            $user = User::firstOrCreate(
                ['email' => $u['email']],
                [
                    'name' => 'User ' . $u['id'],
                    'full_name' => 'User Full ' . $u['id'],
                    'password' => Hash::make($u['password']),
                    'role' => $u['role'],
                    'status' => 'active'
                ]
            );

            // Find matching profile from $data['profiles']
            $pData = collect($data['profiles'])->firstWhere('userId', $u['id']);
            if ($pData) {
                $profile = Profile::firstOrCreate(
                    ['user_id' => $user->id],
                    [
                        'full_name' => $pData['full_name'],
                        'avatar_url' => $pData['avatar_url'],
                        'phone' => $pData['phone'],
                        'address' => $pData['address']
                    ]
                );

                // If user is lawyer, create Lawyer record
                if ($user->role === 'LAWYER') {
                    $lData = collect($data['lawyers'])->firstWhere('profileId', $pData['id']);
                    if ($lData) {
                        Lawyer::firstOrCreate(
                            ['profile_id' => $profile->id],
                            [
                                'name' => $lData['name'],
                                'speciality' => $lData['speciality'] ?? $lData['specialty'],
                                'domaine' => $lData['domaine'] ?? $lData['specialty'],
                                'location' => $lData['location'],
                                'city' => $lData['city'],
                                'avatar_url' => $lData['avatar_url'],
                                'rating' => $lData['rating'],
                                'review_count' => $lData['review_count'] ?? $lData['reviewCount'],
                                'years_experience' => $lData['years_experience'] ?? $lData['experience'],
                                'is_verified' => $lData['is_verified'] ?? $lData['isVerified'],
                                'bar_number' => $lData['bar_number'],
                                'bio' => $lData['bio'],
                                'is_available' => true
                            ]
                        );
                    }
                } elseif ($user->role === 'CLIENT') {
                    $cData = collect($data['clients'])->firstWhere('profileId', $pData['id']);
                    if ($cData) {
                        Client::firstOrCreate(
                            ['profile_id' => $profile->id],
                            [
                                'company_name' => $cData['company_name'] ?? null
                            ]
                        );
                    }
                }
            }
        }
        
        // Also insert consultations
        foreach ($data['consultations'] as $c) {
             Consultation::firstOrCreate(
                ['id' => $c['id']],
                [
                    'client_id' => $c['clientId'],
                    'lawyer_id' => $c['lawyerId'],
                    'status' => $c['status'],
                    'subject' => $c['subject'],
                    'date' => $c['date']
                ]
            );
        }
        
        // Also insert dossiers
        foreach ($data['dossiers'] as $d) {
             Dossier::firstOrCreate(
                ['id' => $d['id']],
                [
                    'case_number' => $d['case_number'],
                    'category' => $d['category'],
                    'status' => $d['status'],
                    'opening_date' => $d['opening_date'],
                    'lawyer_id' => $d['lawyer_id'],
                    'progress' => $d['progress'],
                    'lawyer_name' => $d['lawyer_name'],
                    'lawyer_specialty' => $d['lawyer_specialty'],
                    'client_name' => $d['client_name']
                ]
            );
        }
    }
}
