package com.keyfluent.keyboard.latin.utils

import android.inputmethodservice.InputMethodService
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.OptIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import com.keyfluent.keyboard.latin.LatinIME

object TranslatorUtils {

    // --- Suggestion API Connector functionality ---
    private val client = OkHttpClient()
    private val baseUrl = "https://api.bcornec.org/api/Suggestion"


    @JvmStatic
    fun translateTo(language: String, content: String, idToken: String? = null): Flow<String> = flow {
        val jsonObject = JSONObject().apply {
            put("text", content)
            put("targetLanguage", language)
        }
        val json = jsonObject.toString()
        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val targetUrl = "$baseUrl/translate-to"
        val requestBuilder = Request.Builder()
            .url(targetUrl)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
        if (!idToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $idToken")
        }
        val request = requestBuilder.build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("HTTP ${response.code}")
            val responseBody = response.body?.string() ?: throw IOException("Empty response body")
            emit(responseBody)
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Java-friendly helper: launch a coroutine on Main and collect the translation Flow,
     * replacing the input text via LatinIME for each emitted value.
     * Returns the Job so the caller may cancel if needed.
     */
    @OptIn(DelicateCoroutinesApi::class)
    @JvmStatic
    fun translateAndReplace(language: String, content: String, inputMethodService: InputMethodService): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            try {
                // Ensure we have a Google ID token; prompt sign-in if missing
                val idToken = AuthManager.getIdToken(inputMethodService)
                if (idToken.isNullOrBlank()) {
                    Toast.makeText(inputMethodService, "Veuillez vous connecter avec Google pour utiliser la traduction.", Toast.LENGTH_LONG).show()
                    AuthManager.startSignIn(inputMethodService)
                    return@launch
                }
                translateTo(language, content, idToken).collect { value ->
                    LatinIME.replaceInputText(inputMethodService, value)
                }
            } catch (e: Exception) {
                val msg = e.message ?: "Erreur inconnue"
                // If unauthorized/forbidden, trigger sign-in
                if (msg.contains("HTTP 401") || msg.contains("HTTP 403")) {
                    Toast.makeText(inputMethodService, "Authentification requise. Connectez-vous à Google.", Toast.LENGTH_LONG).show()
                    AuthManager.startSignIn(inputMethodService)
                } else {
                    Toast.makeText(inputMethodService, "Erreur traduction: $msg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
