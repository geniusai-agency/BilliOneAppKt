package com.example.billionemotosappkt.shared.auth

import com.example.billionemotosappkt.shared.api.AuthSessionResponse

data class SessionTokens(
    val accessToken: String,
    val refreshToken: String,
)

interface TokenSessionStore {
    suspend fun hydrate()
    fun currentTokens(): SessionTokens?
    fun currentAccessToken(): String?
    fun currentRefreshToken(): String?
    fun updateCurrentTokens(tokens: SessionTokens)
    suspend fun save(tokens: SessionTokens)
    suspend fun save(session: AuthSessionResponse)
    suspend fun clear()
}
