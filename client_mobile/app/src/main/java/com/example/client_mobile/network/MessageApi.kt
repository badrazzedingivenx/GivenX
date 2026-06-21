package com.example.client_mobile.network

import com.example.client_mobile.network.dto.ReservationMessageDto
import com.example.client_mobile.network.dto.SendReservationMessageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MessageApi {

    @GET("messages")
    suspend fun getReservationMessages(
        @Query("reservationId") reservationId: String
    ): Response<ApiResponse<List<ReservationMessageDto>>>

    @POST("messages")
    suspend fun sendReservationMessage(
        @Body request: SendReservationMessageRequest
    ): Response<ApiResponse<ReservationMessageDto>>
}
