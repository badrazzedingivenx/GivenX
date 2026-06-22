package com.example.client_mobile.screens.shared

import android.util.Log
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.ReservationDto
import com.example.client_mobile.repository.ReservationRepository
import com.example.client_mobile.repository.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ─── Backward-compatible legacy models ─────────────────────────────────────

enum class ConsultationStatus { ACTIVE, COMPLETED }

data class Consultation(
    val id: String,
    val clientId: String,
    val lawyerId: String,
    val lawyerName: String,
    val avatarUrl: String = "",
    val isPaid: Boolean = true,
    var status: ConsultationStatus = ConsultationStatus.ACTIVE
)

// ─── API-backed consultation repository ────────────────────────────────────

object ConsultationRepository {
    private val reservationRepo = ReservationRepository()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Backend-backed cache of all reservations for the current user. */
    private val _reservations = mutableListOf<ReservationDto>()

    /** Reactive stream — emitted after every cache mutation so consumers (e.g. [PaymentViewModel]) stay in sync. */
    private val _reservationsFlow = MutableStateFlow<List<ReservationDto>>(emptyList())
    val reservationsFlow: StateFlow<List<ReservationDto>> = _reservationsFlow

    @Volatile
    private var initialized = false

    // ── Lifecycle ──────────────────────────────────────────────────────────

    /**
     * Fetches all reservations for the current user from the backend.
     * Safe to call multiple times — the first call triggers the fetch;
     * subsequent calls are no-ops unless [forceRefresh] is true.
     */
    fun refresh(forceRefresh: Boolean = false) {
        if (initialized && !forceRefresh) return
        initialized = true
        scope.launch { loadFromApi() }
    }

    private suspend fun loadFromApi() {
        try {
            val userIdInt = TokenManager.getUserIdInt().takeIf { it > 0 } ?: run {
                Log.w("ConsultRepo", "loadFromApi: invalid userId, skipping")
                return
            }
            val userIdStr = userIdInt.toString()

            val clientResult = reservationRepo.getClientReservations(userIdStr)
            val lawyerResult = reservationRepo.getLawyerReservations(userIdStr)

            val all = mutableListOf<ReservationDto>()
            if (clientResult is Result.Success) all.addAll(clientResult.data)
            if (lawyerResult is Result.Success) all.addAll(lawyerResult.data)

            synchronized(_reservations) {
                _reservations.clear()
                _reservations.addAll(all.distinctBy { it.id })
                _reservationsFlow.value = _reservations.toList()
            }
            Log.d("ConsultRepo", "loadFromApi: loaded ${all.size} reservations")
        } catch (e: Exception) {
            Log.e("ConsultRepo", "loadFromApi failed: ${e.message}", e)
        }
    }

    // ── Read operations ────────────────────────────────────────────────────

    /** Returns a snapshot of all cached reservations. */
    fun getAllReservations(): List<ReservationDto> = synchronized(_reservations) {
        ensureLoaded()
        _reservations.toList()
    }

    /** Returns reservations where the given user is the lawyer. */
    fun getLawyerReservations(lawyerId: String): List<ReservationDto> = synchronized(_reservations) {
        ensureLoaded()
        _reservations.filter { it.lawyerId == lawyerId }
    }

    /** Returns reservations where the given user is the client. */
    fun getClientReservations(clientId: String): List<ReservationDto> = synchronized(_reservations) {
        ensureLoaded()
        _reservations.filter { it.clientId == clientId }
    }

    /**
     * Checks whether the client has an active paid consultation with the given lawyer.
     * Survives app restarts — backed by the API.
     */
    fun hasPaidActiveConsultation(clientId: String, lawyerId: String): Boolean = synchronized(_reservations) {
        ensureLoaded()
        _reservations.any {
            it.clientId == clientId &&
            it.lawyerId == lawyerId &&
            it.paymentStatus == "paid" &&
            it.status in listOf("pending", "accepted", "completed")
        }
    }

    /**
     * Returns paid consultations for a client (backward-compatible wrapper).
     * Consumers (e.g. [ConversationViewModel]) iterate this list to seed conversations.
     */
    fun getActiveConsultations(clientId: String): List<Consultation> = synchronized(_reservations) {
        ensureLoaded()
        _reservations
            .filter { it.clientId == clientId && it.paymentStatus == "paid" }
            .map { it.toLegacyConsultation() }
    }

    // ── Write operations (cache-only — API calls handled by callers) ───────

    /**
     * Adds a [ReservationDto] to the local cache after a successful API creation.
     */
    fun addReservation(dto: ReservationDto) {
        synchronized(_reservations) {
            if (_reservations.none { it.id == dto.id }) {
                _reservations.add(dto)
                _reservationsFlow.value = _reservations.toList()
            }
        }
    }

    /**
     * Backward-compatible: adds a [Consultation] to the local cache for immediate gating.
     * Does NOT call the API — callers must still create the reservation via
     * [ReservationRepository.createReservation] or the corresponding API endpoint.
     */
    fun addConsultation(consultation: Consultation) {
        synchronized(_reservations) {
            val existing = _reservations.indexOfFirst {
                it.clientId == consultation.clientId &&
                it.lawyerId == consultation.lawyerId &&
                it.status == consultation.status.name.lowercase()
            }
            if (existing < 0) {
                _reservations.add(consultation.toReservationDto())
                _reservationsFlow.value = _reservations.toList()
            }
        }
    }

    /**
     * Updates the status of a reservation on the backend and in the local cache.
     * Returns true on success.
     */
    suspend fun updateReservationStatus(id: String, status: String): Boolean {
        return when (reservationRepo.updateReservationStatus(id, status)) {
            is Result.Success -> {
                synchronized(_reservations) {
                    val idx = _reservations.indexOfFirst { it.id == id }
                    if (idx >= 0) {
                        _reservations[idx] = _reservations[idx].copy(status = status)
                        _reservationsFlow.value = _reservations.toList()
                    }
                }
                true
            }
            is Result.Error -> false
        }
    }

    /**
     * Marks a consultation as completed locally and fires a background API update.
     */
    fun closeConsultation(consultationId: String) {
        synchronized(_reservations) {
            val idx = _reservations.indexOfFirst { it.id == consultationId }
            if (idx >= 0) {
                _reservations[idx] = _reservations[idx].copy(status = "completed")
                _reservationsFlow.value = _reservations.toList()
            }
        }
        scope.launch {
            updateReservationStatus(consultationId, "completed")
        }
    }

    /** Clears the local cache (does NOT delete from API). */
    fun clear() {
        synchronized(_reservations) {
            _reservations.clear()
            _reservationsFlow.value = emptyList()
        }
        initialized = false
    }

    // ── Internal helpers ───────────────────────────────────────────────────

    private fun ensureLoaded() {
        if (!initialized) {
            initialized = true
            scope.launch { loadFromApi() }
        }
    }

    private fun ReservationDto.toLegacyConsultation() = Consultation(
        id         = id,
        clientId   = clientId,
        lawyerId   = lawyerId,
        lawyerName = lawyerName,
        avatarUrl  = "",
        isPaid     = paymentStatus == "paid",
        status     = if (status == "completed") ConsultationStatus.COMPLETED else ConsultationStatus.ACTIVE
    )

    private fun Consultation.toReservationDto() = ReservationDto(
        id            = id,
        clientId      = clientId,
        lawyerId      = lawyerId,
        lawyerName    = lawyerName,
        fullName      = clientId,
        contact       = "",
        domain        = "",
        description   = "",
        mode          = "MESSAGE",
        price         = 0,
        status        = status.name.lowercase(),
        paymentStatus = if (isPaid) "paid" else "unpaid"
    )
}
