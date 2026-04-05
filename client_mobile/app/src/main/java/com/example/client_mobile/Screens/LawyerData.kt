package com.example.client_mobile.Screens

import androidx.compose.runtime.mutableStateListOf

// ─── Data Models ──────────────────────────────────────────────────────────────
data class LawyerItem(
    val id: String,
    val name: String,
    val specialty: String,
    val city: String,
    val rating: Float,
    val reviewCount: Int,
    val yearsExp: Int,
    val bio: String,
    val isVerified: Boolean,
    val domaine: String
)

data class InboxMessage(
    val id: String,
    val fromName: String,
    val content: String,
    val timestamp: String,
    val lawyerId: String,
    val isRead: Boolean = false
)

// ─── Shared Message Repository (simulated) ────────────────────────────────────
object MessageRepository {
    val messages = mutableStateListOf<InboxMessage>()

    fun sendMessage(fromName: String, content: String, lawyerId: String) {
        messages.add(
            InboxMessage(
                id = System.currentTimeMillis().toString(),
                fromName = fromName,
                content = content,
                timestamp = "À l'instant",
                lawyerId = lawyerId
            )
        )
    }
}

// ─── Sample Lawyers ───────────────────────────────────────────────────────────
val sampleLawyers = listOf(
    LawyerItem(
        id = "1",
        name = "Maître Yassine El Amrani",
        specialty = "Droit Pénal",
        city = "Casablanca",
        rating = 4.9f,
        reviewCount = 127,
        yearsExp = 12,
        bio = "Spécialiste en droit pénal, Maître El Amrani intervient devant les tribunaux de grande instance, cours d'appel et la Cour de cassation. Reconnu pour son engagement total envers ses clients et ses résultats exceptionnels.",
        isVerified = true,
        domaine = "Droit Pénal"
    ),
    LawyerItem(
        id = "2",
        name = "Maître Sara Benali",
        specialty = "Droit de la Famille",
        city = "Rabat",
        rating = 4.8f,
        reviewCount = 94,
        yearsExp = 9,
        bio = "Avocate spécialisée en droit de la famille : divorce, garde d'enfants, succession et pension alimentaire. Approche humaine et professionnelle, reconnue pour sa bienveillance et son efficacité.",
        isVerified = true,
        domaine = "Droit Civil"
    ),
    LawyerItem(
        id = "3",
        name = "Maître Khalid Tazi",
        specialty = "Droit des Affaires",
        city = "Casablanca",
        rating = 4.7f,
        reviewCount = 203,
        yearsExp = 15,
        bio = "Expert en droit des sociétés, fusions-acquisitions et contrats commerciaux. Conseille PME, startups et grands groupes dans leurs opérations juridiques courantes et stratégiques.",
        isVerified = true,
        domaine = "Droit des Affaires"
    ),
    LawyerItem(
        id = "4",
        name = "Maître Nadia Ouali",
        specialty = "Droit Immobilier",
        city = "Marrakech",
        rating = 4.6f,
        reviewCount = 76,
        yearsExp = 7,
        bio = "Spécialiste en droit immobilier : transactions foncières, litiges locatifs, copropriété et urbanisme. Accompagne particuliers et promoteurs immobiliers avec rigueur et réactivité.",
        isVerified = false,
        domaine = "Droit Immobilier"
    ),
    LawyerItem(
        id = "5",
        name = "Maître Omar Hadri",
        specialty = "Droit du Travail",
        city = "Fès",
        rating = 4.5f,
        reviewCount = 58,
        yearsExp = 6,
        bio = "Défend salariés et employeurs dans les litiges professionnels : licenciement abusif, harcèlement moral, négociation de conventions collectives et gestion des conflits collectifs.",
        isVerified = true,
        domaine = "Droit du Travail"
    ),
    LawyerItem(
        id = "6",
        name = "Maître Amina El Fassi",
        specialty = "Droit Fiscal",
        city = "Rabat",
        rating = 4.8f,
        reviewCount = 112,
        yearsExp = 11,
        bio = "Experte en optimisation fiscale, contrôles fiscaux et contentieux avec l'administration. Accompagne entreprises et particuliers pour sécuriser leur situation fiscale et contester les redressements.",
        isVerified = true,
        domaine = "Droit Fiscal"
    )
)

val lawyerFilterDomaines = listOf(
    "Tous",
    "Droit Pénal",
    "Droit Civil",
    "Droit des Affaires",
    "Droit Immobilier",
    "Droit du Travail",
    "Droit Fiscal"
)
