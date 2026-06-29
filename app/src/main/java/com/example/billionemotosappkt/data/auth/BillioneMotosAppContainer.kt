package com.example.billionemotosappkt.data.auth

import android.content.Context
import com.example.billionemotosappkt.shared.api.ApiConfig
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

/**
 * Minimal Android container for the API client and auth session manager.
 *
 * Keep this in a single place so the same token cache is shared by all callers.
 */
class BillioneMotosAppContainer(
    context: Context,
    baseUrl: String,
) {
    private val tokenStore = TokenSessionStore(context)

    val api: BillioneMotosApi = BillioneMotosApi(
        ApiConfig(
            baseUrl = baseUrl,
            accessTokenProvider = { tokenStore.currentAccessToken() },
        ),
    )

    val sessionManager: BillioneMotosSessionManager = BillioneMotosSessionManager(
        api = api,
        tokenStore = tokenStore,
    )
}
