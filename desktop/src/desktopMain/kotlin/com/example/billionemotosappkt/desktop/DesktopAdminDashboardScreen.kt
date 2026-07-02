package com.example.billionemotosappkt.desktop

import androidx.compose.runtime.Composable
import com.example.billionemotosappkt.desktop.admin.screens.AdminDashboardScreen
import com.example.billionemotosappkt.desktop.auth.AuthenticationContextResponse
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

@Composable
fun DesktopAdminDashboardScreen(
    authContext: AuthenticationContextResponse?,
    api: BillioneMotosApi,
    onOpenSite: () -> Unit,
    onLogout: () -> Unit,
) {
    AdminDashboardScreen(
        authContext = authContext,
        api = api,
        onOpenSite = onOpenSite,
        onLogout = onLogout,
    )
}
