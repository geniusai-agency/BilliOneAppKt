package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.admin.components.SectionHeader
import com.example.billionemotosappkt.desktop.admin.components.motos.MotoFiltersRow
import com.example.billionemotosappkt.desktop.admin.components.motos.MotosList
import com.example.billionemotosappkt.desktop.admin.components.motos.MotosSearchField
import com.example.billionemotosappkt.desktop.admin.components.motos.MotosSubsectionToggle
import com.example.billionemotosappkt.desktop.admin.data.MotosFilters
import com.example.billionemotosappkt.desktop.admin.data.MotosUiState
import com.example.billionemotosappkt.desktop.admin.model.MotoSectionTab
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ListMotosQuery

@Composable
fun MotosSection(
	api: BillioneMotosApi,
	apiBaseUrl: String,
	apiAccessToken: String?,
	selectedTab: MotoSectionTab,
	onTabChange: (MotoSectionTab) -> Unit,
) {
	var uiState by remember { mutableStateOf(MotosUiState()) }
	var filters by remember { mutableStateOf(MotosFilters()) }
	
	LaunchedEffect(api) {
		uiState = uiState.copy(isLoading = true)
		runCatching {
			api.motos.list(ListMotosQuery(page = 1, limit = 50)).items
		}.onSuccess {
			uiState = uiState.copy(isLoading = false, motos = it, errorMessage = null)
		}.onFailure {
			uiState = uiState.copy(
				isLoading = false,
				errorMessage = it.message ?: "Erro ao carregar motos"
			)
		}
	}
	
	val filteredMotos = remember(uiState.motos, filters) {
		uiState.motos.filter { moto ->
			val matchesQuery = filters.query.isBlank() ||
					moto.modelo?.contains(filters.query, ignoreCase = true) == true ||
					moto.placa.contains(filters.query, ignoreCase = true) ||
					moto.marca?.contains(filters.query, ignoreCase = true) == true
			
			val matchesStatus = filters.status == null || moto.status == filters.status
			
			matchesQuery && matchesStatus
		}
	}
	
	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.spacedBy(20.dp),
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.Bottom,
		) {
			SectionHeader(
				title = "Motos",
				subtitle = "Gerenciamento de frota, disponibilidade e manutenção operacional."
			)
			
			if (selectedTab == MotoSectionTab.FROTA) {
				MotosSearchField(
					value = filters.query,
					onValueChange = { filters = filters.copy(query = it) }
				)
			}
		}
		
		MotosSubsectionToggle(
			selected = selectedTab,
			onSelected = onTabChange,
		)
		
		when (selectedTab) {
			MotoSectionTab.FROTA -> {
				MotoFiltersRow(
					selectedStatus = filters.status,
					onStatusChange = { filters = filters.copy(status = it) }
				)
				
				if (uiState.isLoading) {
					Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
						CircularProgressIndicator(color = Color(0xFF20E65B))
					}
				} else if (uiState.errorMessage != null) {
					Card(
						colors = CardDefaults.cardColors(
							containerColor = Color(0xFFFF4A4A).copy(
								alpha = 0.1f
							)
						),
						border = androidx.compose.foundation.BorderStroke(
							1.dp,
							Color(0xFFFF4A4A).copy(alpha = 0.2f)
						)
					) {
						Text(
							text = uiState.errorMessage!!,
							color = Color(0xFFFF4A4A),
							modifier = Modifier.padding(16.dp)
						)
					}
				} else {
					MotosList(motos = filteredMotos)
				}
			}
			
			MotoSectionTab.MODELOS -> {
				MotoModelosSection(
					api = api,
					apiBaseUrl = apiBaseUrl,
					apiAccessToken = apiAccessToken
				)
			}
		}
	}
}