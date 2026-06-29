package com.example.billionemotosappkt.data.auth

/**
 * Persisted authentication tokens for the current app session.
 */
data class SessionTokens(
    val accessToken: String,
    val refreshToken: String,
)
