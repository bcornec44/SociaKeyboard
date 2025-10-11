package com.keyfluent.keyboard.latin.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.keyfluent.keyboard.latin.R
import kotlinx.coroutines.tasks.await

object AuthManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_ID_TOKEN = "google_id_token"

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val serverClientId = context.getString(R.string.server_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .apply {
                if (serverClientId.isNotBlank()) {
                    requestIdToken(serverClientId)
                }
            }
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun getIdToken(context: Context): String? {
        val client = getGoogleSignInClient(context)
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
        return getCachedIdToken(context)
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
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_ID_TOKEN, token).apply()
    }

    fun getCachedIdToken(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_ID_TOKEN, null)
}
