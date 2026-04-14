package com.example.client_mobile.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Persists the JWT access token using EncryptedSharedPreferences (AES-256-GCM).
 *
 * Initialise once in MainActivity.onCreate() before any API call:
 *   TokenManager.init(applicationContext)
 */
object TokenManager {

    private const val PREFS_NAME    = "givenx_secure_prefs"
    private const val KEY_TOKEN     = "jwt_token"
    private const val KEY_EMAIL     = "user_email"
    private const val KEY_USER_ID   = "user_id"
    private const val KEY_USER_TYPE = "user_type"
    private const val KEY_FULL_NAME = "user_full_name"
    private const val KEY_AVATAR_URL= "user_avatar_url"
    private const val KEY_CITY      = "user_city"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback to plain SharedPreferences if keystore is unavailable (emulator quirks)
            Log.w("TokenManager", "EncryptedSharedPreferences failed, falling back to plain: ${e.message}")
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    // ── Token ─────────────────────────────────────────────────────────────────

    fun saveToken(token: String) {
        if (token.isNotBlank()) {
            prefs.edit().putString(KEY_TOKEN, token).apply()
        } else {
            clearToken()
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)?.takeIf { it.isNotBlank() }

    fun clearToken() = prefs.edit().remove(KEY_TOKEN).apply()

    fun isLoggedIn(): Boolean = getToken() != null

    // ── User ID ───────────────────────────────────────────────────────────────

    fun saveUserId(id: Int) = prefs.edit().putInt(KEY_USER_ID, id).apply()

    fun getUserId(): String? = prefs.getInt(KEY_USER_ID, -1).let { if (it == -1) null else it.toString() }
    fun getUserIdInt(): Int = prefs.getInt(KEY_USER_ID, -1)

    // ── Email ─────────────────────────────────────────────────────────────────

    fun saveEmail(email: String?) = prefs.edit().putString(KEY_EMAIL, email ?: "").apply()

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    // ── User type ("user" | "lawyer") ─────────────────────────────────────────

    fun saveUserType(type: String?) = prefs.edit().putString(KEY_USER_TYPE, type ?: "user").apply()

    fun getUserType(): String = prefs.getString(KEY_USER_TYPE, "user") ?: "user"

    // ── Full clear (logout) ───────────────────────────────────────────────────

    fun saveFullName(name: String?) = prefs.edit().putString(KEY_FULL_NAME, name ?: "").apply()
    fun getFullName(): String = prefs.getString(KEY_FULL_NAME, "") ?: ""

    fun saveAvatarUrl(url: String?) = prefs.edit().putString(KEY_AVATAR_URL, url ?: "").apply()
    fun getAvatarUrl(): String = prefs.getString(KEY_AVATAR_URL, "") ?: ""

    private const val KEY_LAWYER_ID = "lawyer_id"
    private const val KEY_CLIENT_ID = "client_id"

    fun saveLawyerId(id: Int) = prefs.edit().putInt(KEY_LAWYER_ID, id).apply()
    fun getLawyerId(): Int = prefs.getInt(KEY_LAWYER_ID, -1)

    fun saveClientId(id: Int) = prefs.edit().putInt(KEY_CLIENT_ID, id).apply()
    fun getClientId(): Int = prefs.getInt(KEY_CLIENT_ID, -1)

    // ── Cached user/lawyer JSON (avoids re-fetching profile on every screen open) ──

    private const val KEY_USER_JSON   = "cached_user_json"
    private const val KEY_LAWYER_JSON = "cached_lawyer_json"

    /** Persist the full UserDto object as a JSON string after a successful login. */
    fun saveUserJson(json: String)   = prefs.edit().putString(KEY_USER_JSON,   json).apply()
    /** Returns the cached UserDto JSON, or null if nothing was saved yet. */
    fun getUserJson(): String?        = prefs.getString(KEY_USER_JSON,   null)?.takeIf { it.isNotBlank() }

    /** Persist the full LawyerProfileDto object as a JSON string after a successful login. */
    fun saveLawyerJson(json: String)  = prefs.edit().putString(KEY_LAWYER_JSON, json).apply()
    /** Returns the cached LawyerProfileDto JSON, or null if nothing was saved yet. */
    fun getLawyerJson(): String?       = prefs.getString(KEY_LAWYER_JSON, null)?.takeIf { it.isNotBlank() }

    fun clear() = prefs.edit().clear().apply()
}
