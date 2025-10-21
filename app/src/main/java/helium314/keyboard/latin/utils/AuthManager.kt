package com.keyfluent.keyboard.latin.utils

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.keyfluent.keyboard.latin.BuildConfig
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

object AuthManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_ID_TOKEN = "google_id_token"

    // Small clock skew to avoid using tokens that are about to expire
    private const val EXP_SKEW_SECONDS = 60L

    // Fallback value; override via BuildConfig field GOOGLE_SIGN_IN_SERVER_CLIENT_ID in Gradle.
    private const val DEFAULT_SERVER_CLIENT_ID = "407408718192.apps.googleusercontent.com"

    private fun resolveServerClientId(): String {
        return try {
            val field = BuildConfig::class.java.getField("GOOGLE_SIGN_IN_SERVER_CLIENT_ID")
            (field.get(null) as? String)?.takeIf { it.isNotBlank() } ?: DEFAULT_SERVER_CLIENT_ID
        } catch (_: Throwable) {
            DEFAULT_SERVER_CLIENT_ID
        }
    }

    // Expose server client ID for diagnostics/UI
    fun getServerClientId(): String = resolveServerClientId()

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val serverClientId = resolveServerClientId()
        val gsoBuilder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
        if (serverClientId.isNotBlank()) {
            gsoBuilder.requestIdToken(serverClientId)
        }
        val gso = gsoBuilder.build()
        return GoogleSignIn.getClient(context, gso)
    }

    /**
     * Try to obtain a fresh Google ID token, falling back to a cached token.
     * Returns null if none is available (caller can then prompt interactive sign-in).
     */
    suspend fun getIdToken(context: Context): String? {
        val client = getGoogleSignInClient(context)
        // First, try silent sign-in (refresh token if possible)
        val account = try {
            client.silentSignIn().await()
        } catch (_: Exception) {
            GoogleSignIn.getLastSignedInAccount(context)
        }
        val token = account?.idToken
        if (!token.isNullOrBlank()) {
            saveIdToken(context, token)
            return token
        }
        // Fallback to cached token if it exists and is not expired
        val cached = getCachedIdToken(context)
        if (!cached.isNullOrBlank() && !isTokenExpired(cached)) return cached
        return null
    }

    fun startSignIn(context: Context) {
        try {
            val intent = Intent(context, SignInActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot start Google sign-in: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun saveIdToken(context: Context, token: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ID_TOKEN, token)
            .apply()
    }

    fun getCachedIdToken(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ID_TOKEN, null)

    fun clearCachedIdToken(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_ID_TOKEN)
            .apply()
    }

    fun isSignedIn(context: Context): Boolean =
        GoogleSignIn.getLastSignedInAccount(context) != null

    /** Signs out from Google and clears any cached token. */
    suspend fun signOut(context: Context) {
        try {
            getGoogleSignInClient(context).signOut().await()
        } catch (_: Exception) {
            // ignore
        } finally {
            clearCachedIdToken(context)
        }
    }

    /** Revokes access and clears any cached token. */
    suspend fun revokeAccess(context: Context) {
        try {
            getGoogleSignInClient(context).revokeAccess().await()
        } catch (_: Exception) {
            // ignore
        } finally {
            clearCachedIdToken(context)
        }
    }

    // --- Helpers ---
    private fun isTokenExpired(idToken: String): Boolean {
        return try {
            val parts = idToken.split('.')
            if (parts.size < 2) return false // Not a JWT? assume usable
            val payloadB64 = parts[1]
            val payloadJson = String(Base64.decode(payloadB64, Base64.URL_SAFE or Base64.NO_WRAP))
            val expSec = JSONObject(payloadJson).optLong("exp", 0L)
            if (expSec <= 0L) return false
            val nowSec = System.currentTimeMillis() / 1000L
            nowSec + EXP_SKEW_SECONDS >= expSec
        } catch (_: Exception) {
            false // If decoding fails, don't block usage
        }
    }
}
