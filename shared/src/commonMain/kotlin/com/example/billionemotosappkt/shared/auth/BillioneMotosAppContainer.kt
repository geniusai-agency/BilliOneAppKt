package com.example.billionemotosappkt.shared.auth

import com.example.billionemotosappkt.shared.api.ApiConfig
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

/**
 * Platform-agnostic container for the API client and auth session manager.
 *
 * Keep this in a single place so the same token cache is shared by all callers.
 */
class BillioneMotosAppContainer(
    val tokenStore: TokenSessionStore,
    val baseUrl: String,
) {
    val api: BillioneMotosApi = BillioneMotosApi(
        ApiConfig(
            baseUrl = baseUrl,
            accessTokenProvider = { tokenStore.currentAccessToken() },
            refreshTokenProvider = { tokenStore.currentRefreshToken() },
            onTokenRefreshed = { accessToken, refreshToken ->
                tokenStore.updateCurrentTokens(SessionTokens(accessToken, refreshToken))
            },
            onAuthFailed = { tokenStore.clear() },
        ),
    )

    val sessionManager: BillioneMotosSessionManager = BillioneMotosSessionManager(
        api = api,
        tokenStore = tokenStore,
    )
}
