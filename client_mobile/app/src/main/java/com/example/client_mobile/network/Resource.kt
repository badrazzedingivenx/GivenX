package com.example.client_mobile.network

import retrofit2.Response

// ─── Resource sealed class ────────────────────────────────────────────────────

/**
 * Represents the three states of a network / repository operation.
 *
 * Usage in a ViewModel:
 *   viewModelScope.launch {
 *       _state.value = Resource.Loading
 *       _state.value = safeApiCall { NetworkModule.authApi.login(req) }
 *   }
 */
sealed class Resource<out T> {
    /** Call succeeded and a payload is available (data may be Unit for void endpoints). */
    data class Success<T>(val data: T) : Resource<T>()

    /**
     * Call failed at HTTP or application level.
     *
     * [message]          — Human-readable error for display.
     * [errorCode]        — Machine-readable code from the API spec
     *                      (e.g. UNAUTHORIZED, VALIDATION_ERROR).
     * [validationErrors] — Field-level messages returned on HTTP 422.
     *                      Key = field name, Value = list of violation messages.
     */
    data class Error(
        val message: String,
        val errorCode: String                          = "",
        val validationErrors: Map<String, List<String>>? = null
    ) : Resource<Nothing>()

    /** In-flight indicator — emit before launching the coroutine. */
    object Loading : Resource<Nothing>()
}

// ─── safeApiCall helper ───────────────────────────────────────────────────────

/**
 * Wraps any Retrofit suspend call that returns `Response<ApiResponse<T>>` and
 * maps the outcome to a [Resource].
 *
 * Void endpoints should use `Response<ApiResponse<Unit>>`. When the server
 * returns `{ "success": true }` with no `data` field, the function returns
 * `Resource.Success(Unit)` automatically.
 *
 * Error-code mapping (HTTP status → errorCode):
 *
 * | HTTP | errorCode              |
 * |------|------------------------|
 * | 400  | VALIDATION_ERROR       |
 * | 401  | UNAUTHORIZED           |
 * | 403  | FORBIDDEN              |
 * | 404  | NOT_FOUND              |
 * | 409  | CONFLICT               |
 * | 422  | UNPROCESSABLE_ENTITY   |
 * | 429  | TOO_MANY_REQUESTS      |
 * | 500  | INTERNAL_SERVER_ERROR  |
 *
 * The body's `errors` map (422) is forwarded in [Resource.Error.validationErrors].
 * The body's own `message` / `error` field always takes priority over the HTTP mapping.
 *
 * Example:
 *   val result: Resource<HaqLoginData> = safeApiCall { NetworkModule.authApi.login(req) }
 *   when (result) {
 *       is Resource.Success -> saveToken(result.data.accessToken)
 *       is Resource.Error   -> showSnackbar(result.message)
 *       Resource.Loading    -> Unit
 *   }
 */
@Suppress("UNCHECKED_CAST")
suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<ApiResponse<T>>
): Resource<T> = try {
    val response = apiCall()
    val body     = response.body()

    when {
        // ── Happy path ────────────────────────────────────────────────────────
        // For void endpoints (T = Unit) body.data is null but success is true.
        // We cast Unit to T which is safe when T = Unit; for typed responses
        // body.data is always non-null when success == true.
        response.isSuccessful && body?.success == true ->
            Resource.Success((body.data ?: Unit) as T)

        // ── Null body (unexpected) ────────────────────────────────────────────
        response.isSuccessful && body == null ->
            Resource.Error("Réponse vide du serveur", "EMPTY_RESPONSE")

        // ── App-level error with success == false (2xx but rejected payload) ─
        response.isSuccessful ->
            Resource.Error(
                message           = body?.validationMessage() ?: "Erreur inconnue",
                errorCode         = body?.error.orEmpty(),
                validationErrors  = body?.errors
            )

        // ── HTTP error ────────────────────────────────────────────────────────
        else -> {
            val httpCode = when (response.code()) {
                400  -> "VALIDATION_ERROR"
                401  -> "UNAUTHORIZED"
                403  -> "FORBIDDEN"
                404  -> "NOT_FOUND"
                409  -> "CONFLICT"
                422  -> "UNPROCESSABLE_ENTITY"
                429  -> "TOO_MANY_REQUESTS"
                500  -> "INTERNAL_SERVER_ERROR"
                else -> "UNKNOWN_ERROR"
            }
            Resource.Error(
                message          = body?.validationMessage() ?: "HTTP ${response.code()}",
                errorCode        = body?.error?.takeIf { it.isNotBlank() } ?: httpCode,
                validationErrors = body?.errors
            )
        }
    }
} catch (e: Exception) {
    Resource.Error(
        message   = e.localizedMessage ?: "Erreur réseau",
        errorCode = "NETWORK_ERROR"
    )
}
