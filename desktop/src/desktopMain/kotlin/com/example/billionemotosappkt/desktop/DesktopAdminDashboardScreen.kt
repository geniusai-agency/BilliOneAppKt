package com.example.billionemotosappkt.desktop

import androidx.compose.runtime.Composable
import com.example.billionemotosappkt.desktop.admin.screens.AdminDashboardScreen
import com.example.billionemotosappkt.desktop.auth.AuthenticationContextResponse
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

@Composable
fun DesktopAdminDashboardScreen(
    authContext: AuthenticationContextResponse?,
    api: BillioneMotosApi,
    apiBaseUrl: String,
    apiAccessToken: String?,
    onOpenSite: () -> Unit,
    onLogout: () -> Unit,
) {
    AdminDashboardScreen(
        authContext = authContext?.toSharedApiContext(),
        api = api,
        apiBaseUrl = apiBaseUrl,
        apiAccessToken = apiAccessToken,
        onOpenSite = onOpenSite,
        onLogout = onLogout,
    )
}

/**
 * Bridges the desktop module's local auth model to the shared-api model expected by the
 * migrated [AdminDashboardScreen]. The desktop [UserResponse]/[AuthJourneyResponse] are a
 * subset of the shared ones (extra shared fields default), and the enums share member names.
 */
private fun AuthenticationContextResponse.toSharedApiContext(): com.example.billionemotosappkt.shared.api.AuthenticationContextResponse {
    return com.example.billionemotosappkt.shared.api.AuthenticationContextResponse(
        user = com.example.billionemotosappkt.shared.api.UserResponse(
            id = user.id,
            email = user.email,
            nome = user.nome,
            telefone = user.telefone,
            kind = com.example.billionemotosappkt.shared.api.UserKind.valueOf(user.kind.name),
            status = com.example.billionemotosappkt.shared.api.UserStatus.valueOf(user.status.name),
            clienteId = user.clienteId,
        ),
        journey = com.example.billionemotosappkt.shared.api.AuthJourneyResponse(
            state = journey.state,
            screen = journey.screen,
            title = journey.title,
            message = journey.message,
            nextAction = journey.nextAction,
        ),
    )
}
