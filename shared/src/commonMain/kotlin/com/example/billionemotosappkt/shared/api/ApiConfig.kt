package com.example.billionemotosappkt.shared.api

/**
 * Shared configuration for the Billione Motos API client.
 *
 * The API is expected to return camelCase JSON for domain payloads and
 * snake_case for auth tokens (`access_token` and `refresh_token`).
 */
data class ApiConfig(
    val baseUrl: String,
    val accessTokenProvider: () -> String? = { null },
    val refreshTokenProvider: () -> String? = { null },
    val onTokenRefreshed: (accessToken: String, refreshToken: String) -> Unit = { _, _ -> },
    val defaultHeaders: Map<String, String> = mapOf("ngrok-skip-browser-warning" to "true"),
    val requestTimeoutMillis: Long = 30_000,
)
