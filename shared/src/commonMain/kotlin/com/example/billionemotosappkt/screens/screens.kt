package com.example.billionemotosappkt.screens

import com.example.billionemotosappkt.shared.utils.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.billionemotosappkt.screens.Loading.LoadingScreen
import com.example.billionemotosappkt.screens.auth.AuthScreen
import com.example.billionemotosappkt.screens.auth.AuthViewModel
import com.example.billionemotosappkt.screens.dashboard.DashboadScreen
import com.example.billionemotosappkt.screens.dashboard.SectionPlaceholderScreen
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardRoute
import com.example.billionemotosappkt.screens.motos.MotosScreen
import com.example.billionemotosappkt.screens.onboarding.OnboardingSalesScreen
import com.example.billionemotosappkt.screens.planos.PlanosScreen
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

@Composable
fun Screens(
	viewModel: AuthViewModel,
	api: BillioneMotosApi,
) {
	val state = viewModel.uiState.collectAsStateWithLifecycle().value
	var appSection by remember { mutableStateOf(AppSection.AUTH) }
	val backStack = remember { mutableStateListOf<AppSection>() }

	fun resetTo(section: AppSection) {
		backStack.clear()
		appSection = section
	}

	fun rootSection(): AppSection = if (state.isAuthenticated) AppSection.DASHBOARD else AppSection.AUTH

	fun navigateTo(section: AppSection) {
		if (section == appSection) return
		if (state.isAuthenticated && section == AppSection.AUTH) return
		backStack.add(appSection)
		appSection = section
	}

	fun navigateBack() {
		while (backStack.isNotEmpty()) {
			val previous = backStack.removeAt(backStack.lastIndex)
			if (!(state.isAuthenticated && previous == AppSection.AUTH)) {
				appSection = previous
				return
			}
		}

		val root = rootSection()
		if (appSection != root) {
			appSection = root
		}
	}

	LaunchedEffect(state.isAuthenticated) {
		if (state.isAuthenticated) {
			resetTo(AppSection.DASHBOARD)
		} else if (appSection != AppSection.AUTH) {
			resetTo(AppSection.AUTH)
		}
	}

	BackHandler(
		enabled = appSection != AppSection.AUTH && !(state.isAuthenticated && appSection == AppSection.DASHBOARD),
	) {
		navigateBack()
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
				state.isLoading -> LoadingScreen(modifier = Modifier.fillMaxSize())

				appSection == AppSection.PLANOS -> PlanosScreen(
					onBack = ::navigateBack,
					onComprarPlano = { _ -> navigateTo(rootSection()) },
					modifier = Modifier.fillMaxSize(),
				)

				appSection == AppSection.MOTOS -> MotosScreen(
					onBack = ::navigateBack,
					onPlanosClick = { navigateTo(AppSection.PLANOS) },
					onOndeEstamosClick = { navigateTo(AppSection.ONDE_ESTAMOS) },
					onRentClick = { navigateTo(AppSection.AUTH) },
					modifier = Modifier.fillMaxSize(),
				)

				appSection == AppSection.ONDE_ESTAMOS -> SectionPlaceholderScreen(
					title = "Onde Estamos",
					description = "A próxima tela da localização entra aqui. O botão já está conectado.",
					onBack = ::navigateBack,
					modifier = Modifier.fillMaxSize(),
				)

				state.isAuthenticated && state.isAdmin -> DashboadScreen(
					authState = state,
					api = api,
					onLogout = {
						resetTo(AppSection.AUTH)
						viewModel.logout()
					},
					modifier = Modifier.fillMaxSize(),
				)

				state.isAuthenticated && state.contractId.isNullOrBlank() -> OnboardingSalesScreen(
					authState = state,
					api = api,
					onLogout = {
						resetTo(AppSection.AUTH)
						viewModel.logout()
					},
					onRefreshSession = viewModel::refreshSession,
					modifier = Modifier.fillMaxSize(),
				)

				state.isAuthenticated -> CustomerDashboardRoute(
					authState = state,
					api = api,
					onPlanosClick = { navigateTo(AppSection.PLANOS) },
					onLogout = {
						resetTo(AppSection.AUTH)
						viewModel.logout()
					},
					modifier = Modifier.fillMaxSize(),
				)

				appSection == AppSection.AUTH -> AuthScreen(
					viewModel = viewModel,
					onMotosClick = { navigateTo(AppSection.MOTOS) },
					onPlanosClick = { navigateTo(AppSection.PLANOS) },
					onOndeEstamosClick = { navigateTo(AppSection.ONDE_ESTAMOS) },
					modifier = Modifier.fillMaxSize(),
				)

				else -> CustomerDashboardRoute(
					authState = state,
					api = api,
					onPlanosClick = { navigateTo(AppSection.PLANOS) },
					onLogout = {
						resetTo(AppSection.AUTH)
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
	DASHBOARD,
}
