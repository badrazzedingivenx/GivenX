package com.example.client_mobile.network

import com.example.client_mobile.network.dto.Consultation
import com.example.client_mobile.network.dto.Profile
import com.example.client_mobile.network.dto.Specialty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Repository for Legal Services.
 * Encapsulates the logic for fetching and managing legal-related data.
 */
class LegalRepository(private val apiService: HaqApiService) {

    /**
     * Generic helper to execute API calls and wrap the result in a Result<T>.
     * It handles the [ApiResponse] envelope automatically.
     */
    private suspend fun <T> safeApiCall(call: suspend () -> Response<ApiResponse<T>>): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = call()
                val body = response.body()
                if (response.isSuccessful && body != null && body.success) {
                    val data = body.data
                    if (data != null) {
                        Result.success(data)
                    } else {
                        // For the current methods in this repository, they all expect non-null data.
                        Result.failure(Exception("API success but data is null"))
                    }
                } else {
                    val errorMsg = body?.errorMessage() ?: "API Error: ${response.code()}"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getProfiles(role: String? = null): Result<List<Profile>> =
        safeApiCall { apiService.getProfiles(role) }

    suspend fun createConsultation(consultation: Consultation): Result<Consultation> =
        safeApiCall { apiService.createConsultation(consultation) }

    suspend fun getConsultations(clientId: Int? = null, lawyerId: Int? = null): Result<List<Consultation>> =
        safeApiCall { apiService.getConsultations(clientId, lawyerId) }

    suspend fun getSpecialties(): Result<List<Specialty>> =
        safeApiCall { apiService.getSpecialties() }

    suspend fun updateConsultation(id: Int, consultation: Consultation): Result<Consultation> =
        safeApiCall { apiService.updateConsultation(id, consultation) }
}
