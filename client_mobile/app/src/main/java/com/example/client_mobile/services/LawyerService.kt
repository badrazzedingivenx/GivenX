package com.example.client_mobile.services

import com.example.client_mobile.network.LawyerApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class Lawyer(
    val id: String = "",
    val name: String = "",
    val specialty: String = "",
    val location: String = "",
    val experience: Int = 0,
    val rating: Float = 0f,
    val compatibility: Int = 0,
    val reviewCount: Int = 0,
    val bio: String = "",
    val isVerified: Boolean = true,
    val domaine: String = ""
)

/**
 * Thin facade over [LawyerApiRepository].
 * ViewModels use LawyerApiRepository directly; this object exists for
 * backward compatibility in case other code still references LawyerService.
 */
object LawyerService {

    private fun com.example.client_mobile.screens.shared.LawyerItem.toLawyer() = Lawyer(
        id            = id,
        name          = name,
        specialty     = specialty,
        location      = city,
        experience    = yearsExp,
        rating        = rating,
        compatibility = 0,
        reviewCount   = reviewCount,
        bio           = bio,
        isVerified    = isVerified,
        domaine       = domaine
    )

    suspend fun getLawyers(): List<Lawyer> =
        LawyerApiRepository.getLawyers().map { it.toLawyer() }

    suspend fun getLawyerById(id: String): Lawyer? =
        LawyerApiRepository.getLawyerById(id)?.toLawyer()

    fun getLawyersFlow(): Flow<List<Lawyer>> =
        LawyerApiRepository.getLawyersFlow().map { items -> items.map { it.toLawyer() } }
}
