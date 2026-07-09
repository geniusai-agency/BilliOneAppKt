package com.example.billionemotosappkt.screens.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.billionemotosappkt.desktop.admin.screens.AdminDashboardScreen
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.shared.api.AuthJourneyResponse
import com.example.billionemotosappkt.shared.api.AuthenticationContextResponse
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.UserKind
import com.example.billionemotosappkt.shared.api.UserResponse
import com.example.billionemotosappkt.shared.api.UserRoleResponse
import com.example.billionemotosappkt.shared.api.UserStatus

@Composable
fun DashboadScreen(
    authState: AuthUiState,
    api: BillioneMotosApi,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AdminDashboardScreen(
            authContext = authState.toAuthenticationContext(),
            api = api,
            apiBaseUrl = api.config.baseUrl,
            apiAccessToken = api.config.accessTokenProvider(),
            onOpenSite = {},
            onLogout = onLogout,
        )
    }
}

private fun AuthUiState.toAuthenticationContext(): AuthenticationContextResponse {
    val email = userEmail?.takeIf { it.isNotBlank() } ?: email.takeIf { it.isNotBlank() } ?: "admin@billione.local"
    val name = userName?.takeIf { it.isNotBlank() } ?: "Administrador"

    return AuthenticationContextResponse(
        user = UserResponse(
            id = clienteId ?: email,
            email = email,
            nome = name,
            avatarUrl = avatarUrl,
            kind = UserKind.INTERNAL,
            status = UserStatus.ACTIVE,
            clienteId = clienteId,
            roles = roles.map { role ->
                UserRoleResponse(
                    userId = clienteId ?: email,
                    role = role,
                )
            },
        ),
        journey = AuthJourneyResponse(
            state = journeyState ?: "authenticated",
            screen = journeyScreen ?: "admin_dashboard",
            title = journeyTitle ?: "Painel administrativo",
            message = journeyMessage ?: "Acesso administrativo autorizado.",
            nextAction = "admin_dashboard",
            clienteId = clienteId,
            clienteStatus = clienteStatus,
            contractId = contractId,
            contractStatus = contractStatus,
            roles = roles,
        ),
    )
}