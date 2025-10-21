package com.keyfluent.keyboard.latin.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import java.security.MessageDigest

class SignInActivity : Activity() {
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client = AuthManager.getGoogleSignInClient(this)
        val intent: Intent = client.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.result
                val token = account?.idToken
                if (!token.isNullOrBlank()) {
                    AuthManager.saveIdToken(this, token)
                    Toast.makeText(this, "Connecté à Google", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Échec de la connexion Google", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                val status = (e as? ApiException)?.statusCode
                if (status == 10) {
                    // DEVELOPER_ERROR: usually package name / SHA-1 mismatch or wrong client IDs
                    val sha1 = getSigningSha1() ?: "(introuvable)"
                    val serverClientId = AuthManager.getServerClientId()
                    // Copy helpful diagnostics to clipboard
                    val clipMgr = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val text = "Package: $packageName\nSHA1: $sha1\nserver_client_id: $serverClientId"
                    clipMgr.setPrimaryClip(ClipData.newPlainText("GoogleSignIn diagnostics", text))
                    Toast.makeText(this, "Erreur configuration (code 10). Infos copiées. Vérifiez l'ID client Web, le nom du package et le SHA-1.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Erreur connexion Google: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                finish()
            }
        }
    }

    private fun getSigningSha1(): String? = try {
        val pm = packageManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val sig = pi.signingInfo?.apkContentsSigners?.firstOrNull() ?: return null
            sha1Hex(sig.toByteArray())
        } else {
            @Suppress("DEPRECATION")
            val pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            @Suppress("DEPRECATION") val sig = pi.signatures?.firstOrNull() ?: return null
            sha1Hex(sig.toByteArray())
        }
    } catch (_: Exception) { null }

    private fun sha1Hex(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("SHA1").digest(bytes)
        return md.joinToString(":") { b -> "%02X".format(b) }
    }
}
