package com.example.billionemotosappkt.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.billionemotosappkt.shared.api.AuthSessionResponse
import kotlinx.coroutines.flow.first

private const val AUTH_DATASTORE_NAME = "billione_auth_session"

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(
    name = AUTH_DATASTORE_NAME,
)

/**
 * Keeps auth tokens in memory for synchronous access and mirrors them to DataStore.
 *
 * The API client needs a synchronous token provider for the Authorization header,
 * while the actual source of truth remains DataStore.
 */
class TokenSessionStore(context: Context) {
    private val dataStore = context.applicationContext.authDataStore

    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    @Volatile
    private var cachedTokens: SessionTokens? = null

    suspend fun hydrate() {
        println("[TokenSessionStore] hydrate start")
        cachedTokens = dataStore.data.first().toSessionTokens()
        println(
            "[TokenSessionStore] hydrate complete hasTokens=${cachedTokens != null} accessTokenLength=${cachedTokens?.accessToken?.length ?: 0} refreshTokenLength=${cachedTokens?.refreshToken?.length ?: 0}"
        )
    }

    fun currentTokens(): SessionTokens? {
        println("[TokenSessionStore] currentTokens hasTokens=${cachedTokens != null}")
        return cachedTokens
    }

    fun currentAccessToken(): String? {
        val token = cachedTokens?.accessToken
        println("[TokenSessionStore] currentAccessToken hasToken=${!token.isNullOrBlank()} length=${token?.length ?: 0}")
        return token
    }

    fun currentRefreshToken(): String? {
        val token = cachedTokens?.refreshToken
        println("[TokenSessionStore] currentRefreshToken hasToken=${!token.isNullOrBlank()} length=${token?.length ?: 0}")
        return token
    }

    suspend fun save(tokens: SessionTokens) {
        println(
            "[TokenSessionStore] save start accessTokenLength=${tokens.accessToken.length} refreshTokenLength=${tokens.refreshToken.length}"
        )
        cachedTokens = tokens
        dataStore.edit { preferences ->
            preferences[accessTokenKey] = tokens.accessToken
            preferences[refreshTokenKey] = tokens.refreshToken
        }
        println("[TokenSessionStore] save complete")
    }

    suspend fun save(session: AuthSessionResponse) {
        save(
            SessionTokens(
                accessToken = session.accessToken,
                refreshToken = session.refreshToken,
            ),
        )
    }

    suspend fun clear() {
        println("[TokenSessionStore] clear start")
        cachedTokens = null
        dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(refreshTokenKey)
        }
        println("[TokenSessionStore] clear complete")
    }

    private fun Preferences.toSessionTokens(): SessionTokens? {
        val accessToken = this[accessTokenKey]
        val refreshToken = this[refreshTokenKey]
        return if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
            null
        } else {
            SessionTokens(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }
    }
}
