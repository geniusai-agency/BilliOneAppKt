package com.example.billionemotosappkt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.billionemotosappkt.screens.Loading.LoadingScreen
import com.example.billionemotosappkt.screens.auth.AuthScreen
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.screens.auth.AuthViewModel
import com.example.billionemotosappkt.screens.dashboard.DashboadScreen
import com.example.billionemotosappkt.screens.dashboard.SectionPlaceholderScreen
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardRoute
import com.example.billionemotosappkt.screens.motos.MotosScreen
import com.example.billionemotosappkt.screens.planos.PlanosScreen
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

@Composable
fun Screens(
	viewModel: AuthViewModel,
	api: BillioneMotosApi,
) {
	val state = viewModel.uiState.collectAsStateWithLifecycle().value
	var appSection by remember { mutableStateOf<AppSection>(AppSection.AUTH) }

	LaunchedEffect(state.isAuthenticated) {
		if (state.isAuthenticated) {
			appSection = AppSection.DASHBOARD
		} else if (appSection == AppSection.DASHBOARD || appSection == AppSection.PLANOS || appSection == AppSection.MOTOS || appSection == AppSection.ONDE_ESTAMOS) {
			appSection = AppSection.AUTH
		}
	}
	
	Surface(modifier = Modifier.fillMaxSize()) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(
					Brush.verticalGradient(
						colors = listOf(
							MaterialTheme.colorScheme.surface,
							MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
						),
					),
				),
		) {
			when {
				state.isLoading -> LoadingScreen(modifier = Modifier.align(Alignment.Center))
				appSection == AppSection.PLANOS -> PlanosScreen(
					onBack = {
						appSection = if (state.isAuthenticated) AppSection.DASHBOARD else AppSection.AUTH
					},
					onComprarPlano = { _ ->
						appSection = if (state.isAuthenticated) AppSection.DASHBOARD else AppSection.AUTH
					},
					modifier = Modifier.fillMaxSize(),
				)

				appSection == AppSection.MOTOS -> MotosScreen(
					onBack = { appSection = if (state.isAuthenticated) AppSection.DASHBOARD else AppSection.AUTH },
					onPlanosClick = { appSection = AppSection.PLANOS },
					onOndeEstamosClick = { appSection = AppSection.ONDE_ESTAMOS },
					onRentClick = { appSection = AppSection.AUTH },
					modifier = Modifier.fillMaxSize(),
				)

				appSection == AppSection.ONDE_ESTAMOS -> SectionPlaceholderScreen(
					title = "Onde Estamos",
					description = "A próxima tela da localização entra aqui. O botão já está conectado.",
					onBack = { appSection = if (state.isAuthenticated) AppSection.DASHBOARD else AppSection.AUTH },
					modifier = Modifier.fillMaxSize(),
				)

				state.isAuthenticated && state.isAdmin -> DashboadScreen(
					authState = state,
					api = api,
					onLogout = {
						appSection = AppSection.AUTH
						viewModel.logout()
					},
					modifier = Modifier.fillMaxSize(),
				)

				state.isAuthenticated -> CustomerDashboardRoute(
					authState = state,
					api = api,
					onPlanosClick = { appSection = AppSection.PLANOS },
					onLogout = {
						appSection = AppSection.AUTH
						viewModel.logout()
					},
					modifier = Modifier.fillMaxSize(),
				)

				appSection == AppSection.AUTH -> AuthScreen(
					viewModel = viewModel,
					onMotosClick = {
						appSection = AppSection.MOTOS
					},
					onPlanosClick = { appSection = AppSection.PLANOS },
					onOndeEstamosClick = { appSection = AppSection.ONDE_ESTAMOS },
					modifier = Modifier.fillMaxSize(),
				)

				else -> CustomerDashboardRoute(
					authState = state,
					api = api,
					onPlanosClick = { appSection = AppSection.PLANOS },
					onLogout = {
						appSection = AppSection.AUTH
						viewModel.logout()
					},
					modifier = Modifier.fillMaxSize(),
				)
			}
		}
	}
}

private enum class AppSection {
	AUTH,
	MOTOS,
	PLANOS,
	ONDE_ESTAMOS,
	DASHBOARD
}
