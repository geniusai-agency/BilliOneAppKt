package com.example.billionemotosappkt.screens.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

@Composable
fun DashboadScreen(
    authState: AuthUiState,
    api: BillioneMotosApi,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DashboardScreenRoute(
        authState = authState,
        api = api,
        onLogout = onLogout,
        modifier = modifier,
    )
}
