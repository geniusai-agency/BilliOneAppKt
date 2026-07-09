package com.example.billionemotosappkt.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.billionemotosappkt.shared.api.AuthSessionResponse
import com.example.billionemotosappkt.shared.auth.SessionTokens
import com.example.billionemotosappkt.shared.auth.TokenSessionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

private const val AUTH_DATASTORE_NAME = "billione_auth_session"

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(
    name = AUTH_DATASTORE_NAME,
)

class AndroidTokenSessionStore(context: Context) : TokenSessionStore {
    private val dataStore = context.applicationContext.authDataStore

    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val persistenceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    private var cachedTokens: SessionTokens? = null

    override suspend fun hydrate() {
        println("[TokenSessionStore] hydrate start")
        cachedTokens = dataStore.data.first().toSessionTokens()
        println(
            "[TokenSessionStore] hydrate complete hasTokens=${cachedTokens != null} accessTokenLength=${cachedTokens?.accessToken?.length ?: 0} refreshTokenLength=${cachedTokens?.refreshToken?.length ?: 0}"
        )
    }

    override fun currentTokens(): SessionTokens? {
        return cachedTokens
    }

    override fun currentAccessToken(): String? {
        return cachedTokens?.accessToken
    }

    override fun currentRefreshToken(): String? {
        return cachedTokens?.refreshToken
    }

    override fun updateCurrentTokens(tokens: SessionTokens) {
        cachedTokens = tokens
        persistenceScope.launch { save(tokens) }
    }

    override suspend fun save(tokens: SessionTokens) {
        cachedTokens = tokens
        dataStore.edit { preferences ->
            preferences[accessTokenKey] = tokens.accessToken
            preferences[refreshTokenKey] = tokens.refreshToken
        }
    }

    override suspend fun save(session: AuthSessionResponse) {
        save(
            SessionTokens(
                accessToken = session.accessToken,
                refreshToken = session.refreshToken,
            ),
        )
    }

    override suspend fun clear() {
        cachedTokens = null
        dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(refreshTokenKey)
        }
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
