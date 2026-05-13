package com.example.client_mobile.network

import com.example.client_mobile.network.dto.HaqCreatePaymentRequest
import com.example.client_mobile.network.dto.HaqPaymentDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for payment endpoints.
 *
 * GET  /payments         — list the authenticated user's payment history
 * POST /payments         — initiate a new payment (returns a checkout/redirect URL)
 * GET  /payments/{id}    — get details of a specific payment
 *
 * Auth header injected automatically by [AuthInterceptor].
 * All paths relative to [NetworkModule.BASE_URL].
 */
interface PaymentApi {

    /**
     * GET /payments
     *
     * Lists all payments associated with the authenticated user.
     *
     * @param status Optional status filter (e.g. "pending", "completed", "failed")
     * @param page   Page number
     *
     * Response 200: data = List<HaqPaymentDto>
     * Errors: 401 UNAUTHORIZED
     */
    @GET("payments")
    suspend fun getPayments(
        @Query("status") status: String? = null,
        @Query("page")   page:   Int     = 1
    ): Response<ApiResponse<List<HaqPaymentDto>>>

    /**
     * POST /payments
     *
     * Initiates a payment for a consultation or live session.
     * The response contains a checkout URL that the client should open in a browser/WebView.
     *
     * Response 201: data = HaqPaymentDto (includes checkout_url)
     * Errors: 401 UNAUTHORIZED, 422 UNPROCESSABLE_ENTITY
     */
    @POST("payments")
    suspend fun createPayment(
        @Body request: HaqCreatePaymentRequest
    ): Response<ApiResponse<HaqPaymentDto>>

    /**
     * GET /payments/{id}
     *
     * Returns the details and current status of a specific payment.
     *
     * Response 200: data = HaqPaymentDto
     * Errors: 401 UNAUTHORIZED, 403 FORBIDDEN, 404 NOT_FOUND
     */
    @GET("payments/{id}")
    suspend fun getPaymentById(
        @Path("id") id: Int
    ): Response<ApiResponse<HaqPaymentDto>>
}
