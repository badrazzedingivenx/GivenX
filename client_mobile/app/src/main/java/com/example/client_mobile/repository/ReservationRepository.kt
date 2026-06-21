package com.example.client_mobile.repository

import android.util.Log
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.dto.CreateReservationRequest
import com.example.client_mobile.network.dto.ReservationDto
import com.example.client_mobile.network.dto.ReservationMessageDto
import com.example.client_mobile.network.dto.SendReservationMessageRequest
import com.example.client_mobile.network.dto.UpdatePaymentStatusRequest
import com.example.client_mobile.network.dto.UpdateReservationStatusRequest

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

class ReservationRepository {

    private val reservationApi = RetrofitClient.reservationApi
    private val messageApi = RetrofitClient.messageApi

    suspend fun createReservation(request: CreateReservationRequest): Result<ReservationDto> {
        Log.d("ReservationRepo", "Creating reservation: clientId=${request.clientId}, lawyerId=${request.lawyerId}")
        return try {
            val response = reservationApi.createReservation(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) {
                    Log.d("ReservationRepo", "Reservation created: id=${data.id}, status=${data.status}")
                    Result.Success(data)
                } else Result.Error("Reservation data is null")
            } else {
                val msg = response.body()?.errorMessage() ?: "Echec de la creation"
                Log.e("ReservationRepo", "Create failed: $msg")
                Result.Error(msg)
            }
        } catch (e: Exception) {
            Log.e("ReservationRepo", "Create error: ${e.message}")
            Result.Error(e.message ?: "Erreur reseau")
        }
    }

    suspend fun getLawyerReservations(lawyerId: String): Result<List<ReservationDto>> {
        Log.d("ReservationRepo", "Fetching lawyer reservations: lawyerId=$lawyerId")
        return try {
            val numericLawyerId = lawyerId.toIntOrNull()
            val response = reservationApi.getReservations(lawyerId = numericLawyerId)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: emptyList()
                Log.d("ReservationRepo", "Found ${data.size} reservations for lawyer $lawyerId")
                Result.Success(data)
            } else {
                Result.Error(response.body()?.errorMessage() ?: "Echec du chargement")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur reseau")
        }
    }

    suspend fun getClientReservations(clientId: String): Result<List<ReservationDto>> {
        Log.d("ReservationRepo", "Fetching client reservations: clientId=$clientId")
        return try {
            val numericClientId = clientId.toIntOrNull()
            val response = reservationApi.getReservations(clientId = numericClientId)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: emptyList()
                Log.d("ReservationRepo", "Found ${data.size} reservations for client $clientId")
                Result.Success(data)
            } else {
                Result.Error(response.body()?.errorMessage() ?: "Echec du chargement")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur reseau")
        }
    }

    suspend fun updateReservationStatus(id: String, status: String): Result<ReservationDto> {
        Log.d("ReservationRepo", "Updating reservation $id to $status")
        return try {
            val response = reservationApi.updateReservationStatus(id, UpdateReservationStatusRequest(status))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) Result.Success(data)
                else Result.Error("Reservation data is null")
            } else {
                val msg = response.body()?.errorMessage() ?: "Echec de la mise a jour"
                Log.e("ReservationRepo", "Update failed: $msg")
                Result.Error(msg)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur reseau")
        }
    }

    suspend fun getReservationMessages(reservationId: String): Result<List<ReservationMessageDto>> {
        return try {
            val response = messageApi.getReservationMessages(reservationId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.Success(response.body()?.data ?: emptyList())
            } else {
                Result.Error(response.body()?.errorMessage() ?: "Echec du chargement")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur reseau")
        }
    }

    suspend fun sendReservationMessage(request: SendReservationMessageRequest): Result<ReservationMessageDto> {
        Log.d("ReservationRepo", "Sending message: senderId=${request.senderId}, receiverId=${request.receiverId}, reservationId=${request.reservationId}")
        return try {
            val response = messageApi.sendReservationMessage(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) Result.Success(data)
                else Result.Error(response.body()?.errorMessage() ?: "Echec de l'envoi")
            } else {
                val msg = response.body()?.errorMessage() ?: "Echec de l'envoi"
                Log.e("ReservationRepo", "Send failed ($msg)")
                Result.Error(msg)
            }
        } catch (e: Exception) {
            Log.e("ReservationRepo", "Send error: ${e.message}")
            Result.Error(e.message ?: "Erreur reseau")
        }
    }

    suspend fun payReservation(id: String): Result<ReservationDto> {
        Log.d("ReservationRepo", "Paying reservation $id")
        return try {
            val response = reservationApi.updatePaymentStatus(id, UpdatePaymentStatusRequest("paid"))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) Result.Success(data)
                else Result.Error("Reservation data is null")
            } else {
                val msg = response.body()?.errorMessage() ?: "Echec du paiement"
                Log.e("ReservationRepo", "Payment failed: $msg")
                Result.Error(msg)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erreur reseau")
        }
    }
}
