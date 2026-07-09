package com.example.billionemotosappkt.ios

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.billionemotosappkt.screens.auth.AuthViewModel
import com.example.billionemotosappkt.screens.Screens
import com.example.billionemotosappkt.shared.api.BILLIONE_API_BASE_URL
import com.example.billionemotosappkt.shared.api.AuthSessionResponse
import com.example.billionemotosappkt.shared.auth.SessionTokens
import com.example.billionemotosappkt.shared.auth.TokenSessionStore
import com.example.billionemotosappkt.shared.auth.BillioneMotosAppContainer

class IosTokenSessionStore : TokenSessionStore {
    private var cachedTokens: SessionTokens? = null
    override suspend fun hydrate() {}
    override fun currentTokens(): SessionTokens? = cachedTokens
    override fun currentAccessToken(): String? = cachedTokens?.accessToken
    override fun currentRefreshToken(): String? = cachedTokens?.refreshToken
    override fun updateCurrentTokens(tokens: SessionTokens) { cachedTokens = tokens }
    override suspend fun save(tokens: SessionTokens) { cachedTokens = tokens }
    override suspend fun save(session: AuthSessionResponse) {
        cachedTokens = SessionTokens(session.accessToken, session.refreshToken)
    }
    override suspend fun clear() { cachedTokens = null }
}

@Composable
fun BillioneIosApp() {
	val container = remember {
		val tokenStore = IosTokenSessionStore()
		BillioneMotosAppContainer(
			tokenStore = tokenStore,
			baseUrl = BILLIONE_API_BASE_URL,
		)
	}
	val authViewModel = remember(container.sessionManager) {
		AuthViewModel(container.sessionManager)
	}

	MaterialTheme(
		colorScheme = androidx.compose.material3.darkColorScheme(
			primary = Color(0xFF20E65B),
			onPrimary = Color.Black,
			surface = Color(0xFF0E1110),
			surfaceVariant = Color(0xFF171B19),
			onSurface = Color.White,
			onSurfaceVariant = Color(0xFFB8BFBA),
			outline = Color(0xFF3A443D),
		),
	) {
		Surface(modifier = Modifier.fillMaxSize()) {
			Screens(
				viewModel = authViewModel,
				api = container.api,
			)
		}
	}
}
