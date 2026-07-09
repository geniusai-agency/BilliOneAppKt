package com.example.billionemotosappkt.desktop

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.auth.AuthenticationContextResponse
import com.example.billionemotosappkt.desktop.auth.DesktopAuthClient
import com.example.billionemotosappkt.desktop.auth.UserKind
import com.example.billionemotosappkt.shared.api.ApiConfig
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import kotlinx.coroutines.launch

@Composable
fun BillioneDesktopPortalApp() {
    val authBaseUrl = remember { DesktopConfig.apiBaseUrl }
    val authClient = remember(authBaseUrl) { DesktopAuthClient(authBaseUrl) }
    val adminApi = remember(authBaseUrl, authClient) {
        BillioneMotosApi(
            ApiConfig(
                baseUrl = authBaseUrl,
                accessTokenProvider = { authClient.currentAccessToken() },
                refreshTokenProvider = { authClient.currentRefreshToken() },
                onTokenRefreshed = { access, refresh ->
                    // Salva os novos tokens para as próximas requisições
                    authClient.saveTokens(access, refresh)
                }
            ),
        )
    }
    val scope = rememberCoroutineScope()
    var screen by remember { mutableStateOf(DesktopPortalScreen.ADMIN_LOGIN) }
    var bootstrapping by remember { mutableStateOf(true) }
    var restoredSession by remember { mutableStateOf<AuthenticationContextResponse?>(null) }

    LaunchedEffect(authClient) {
        runCatching { authClient.restoreAuthenticationContext() }
            .onSuccess { session ->
                restoredSession = session
                if (session != null) {
                    screen = if (session.user.kind == UserKind.INTERNAL) {
                        DesktopPortalScreen.ADMIN_DASHBOARD
                    } else {
                        DesktopPortalScreen.SITE
                    }
                }
            }
            .onFailure { restoredSession = null }
        bootstrapping = false
    }

    DisposableEffect(Unit) {
        onDispose {
            authClient.close()
            scope.launch { adminApi.close() }
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
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
            if (bootstrapping) {
                DesktopPortalBootScreen()
            } else {
                Crossfade(targetState = screen, label = "desktop_portal_screen") { current ->
                    when (current) {
                        DesktopPortalScreen.ADMIN_LOGIN -> DesktopAdminLoginScreen(
                            authClient = authClient,
                            restoredSession = restoredSession,
                            onBackToSite = { screen = DesktopPortalScreen.SITE },
                            onAuthenticated = {
                                restoredSession = it
                                screen = DesktopPortalScreen.ADMIN_DASHBOARD
                            },
                        )
                        DesktopPortalScreen.ADMIN_DASHBOARD -> DesktopAdminDashboardScreen(
                            authContext = restoredSession,
                            api = adminApi,
                            apiBaseUrl = authBaseUrl,
                            apiAccessToken = authClient.currentAccessToken(),
                            onOpenSite = { screen = DesktopPortalScreen.SITE },
                            onLogout = {
                                scope.launch {
                                    runCatching { authClient.logout() }
                                    restoredSession = null
                                    screen = DesktopPortalScreen.ADMIN_LOGIN
                                }
                            },
                        )
                        DesktopPortalScreen.SITE -> BillioneDesktopApp()
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopPortalBootScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05060A)),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
        ) {
            androidx.compose.material3.CircularProgressIndicator(color = Color(0xFF20E65B))
            androidx.compose.material3.Text(
                text = "Carregando acesso administrativo...",
                color = Color.White.copy(alpha = 0.75f),
            )
        }
    }
}
