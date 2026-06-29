package com.example.billionemotosappkt.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.billionemotosappkt.screens.Loading.LoadingScreen
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardScreen
import com.example.billionemotosappkt.screens.dashboard.customer.toCustomerDashboardData
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

@Composable
fun DashboadScreen(
	authState: AuthUiState,
	api: BillioneMotosApi,
	onLogout: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.factory(api))
	val state = viewModel.uiState.collectAsStateWithLifecycle().value

	when {
		state.isLoading && state.overview == null -> {
			LoadingScreen(modifier = modifier.fillMaxSize())
		}
		state.overview != null -> {
			CustomerDashboardScreen(
				authState = authState,
				dashboardData = state.overview.toCustomerDashboardData(authState),
				showPlanAlert = false,
				onLogout = onLogout,
				modifier = modifier,
			)
		}
		else -> DashboardErrorState(
			message = state.errorMessage ?: "Nao foi possivel carregar a dashboard.",
			onRetry = viewModel::refresh,
			modifier = modifier,
		)
	}
}

@Composable
fun DashboardScreen(
	authState: AuthUiState,
	api: BillioneMotosApi,
	onLogout: () -> Unit,
	modifier: Modifier = Modifier,
): Unit {
	DashboadScreen(
		authState = authState,
		api = api,
		onLogout = onLogout,
		modifier = modifier,
	)
}

@Composable
private fun DashboardErrorState(
	message: String,
	onRetry: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(modifier = modifier.fillMaxSize()) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(24.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Card(
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.24f),
				),
			) {
				Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
					Text(
						text = "Falha ao carregar dados",
						style = MaterialTheme.typography.titleMedium,
						color = MaterialTheme.colorScheme.onSurface,
					)
					Text(
						text = message,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
					Button(onClick = onRetry) {
						Text(text = "Tentar novamente")
					}
				}
			}
		}
	}
}
