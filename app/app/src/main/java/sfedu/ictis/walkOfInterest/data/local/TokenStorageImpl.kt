package sfedu.ictis.walkOfInterest.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

class TokenStorageImpl(context: Context) : TokenStorage {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun saveAccessToken(token: String) {
        sharedPreferences.edit { putString("ACCESS_TOKEN", token) }
    }

    override fun getAccessToken(): String? {
        return sharedPreferences.getString("ACCESS_TOKEN", null)
    }

    override fun saveRefreshToken(token: String) {
        sharedPreferences.edit { putString("REFRESH_TOKEN", token) }
    }

    override fun getRefreshToken(): String? = sharedPreferences.getString("REFRESH_TOKEN", null)

    override fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit {
            putString("ACCESS_TOKEN", accessToken)
            putString("REFRESH_TOKEN", refreshToken)
        }
    }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }
}