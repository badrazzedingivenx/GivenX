package com.example.client_mobile.network

import com.example.client_mobile.network.dto.CreateReservationRequest
import com.example.client_mobile.network.dto.ReservationDto
import com.example.client_mobile.network.dto.UpdatePaymentStatusRequest
import com.example.client_mobile.network.dto.UpdateReservationStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReservationApi {

    @POST("reservations")
    suspend fun createReservation(
        @Body request: CreateReservationRequest
    ): Response<ApiResponse<ReservationDto>>

    @GET("reservations")
    suspend fun getReservations(
        @Query("lawyerId") lawyerId: Int? = null,
        @Query("clientId") clientId: Int? = null
    ): Response<ApiResponse<List<ReservationDto>>>

    @PATCH("reservations/{id}")
    suspend fun updateReservationStatus(
        @Path("id") id: String,
        @Body request: UpdateReservationStatusRequest
    ): Response<ApiResponse<ReservationDto>>

    @PATCH("reservations/{id}/pay")
    suspend fun updatePaymentStatus(
        @Path("id") id: String,
        @Body request: UpdatePaymentStatusRequest
    ): Response<ApiResponse<ReservationDto>>
}
