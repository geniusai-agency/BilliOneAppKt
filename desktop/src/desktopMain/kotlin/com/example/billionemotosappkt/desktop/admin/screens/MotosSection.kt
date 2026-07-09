package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.components.motos.MotosList
import com.example.billionemotosappkt.desktop.admin.components.motos.MotoImagePickerField
import com.example.billionemotosappkt.desktop.admin.data.MotosFilters
import com.example.billionemotosappkt.desktop.admin.data.MotosUiState
import com.example.billionemotosappkt.desktop.admin.model.MotoSectionTab
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ListMotosQuery
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import com.example.billionemotosappkt.shared.api.ListManutencoesQuery
import com.example.billionemotosappkt.shared.api.MotoResponse
import com.example.billionemotosappkt.shared.api.MotoStatus
import com.example.billionemotosappkt.shared.api.ManutencaoResponse
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.UpdateMotoRequest
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import com.example.billionemotosappkt.desktop.admin.screens.motosparts.*

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
	var editingMoto by remember { mutableStateOf<MotoResponse?>(null) }
	var viewingMoto by remember { mutableStateOf<MotoResponse?>(null) }
	var savingMoto by remember { mutableStateOf(false) }
	var motoEditError by remember { mutableStateOf<String?>(null) }
	var motosPage by remember { mutableStateOf(1) }
	val scope = rememberCoroutineScope()

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

	val motosPageSize = 8
	val motosTotalPages = ((filteredMotos.size + motosPageSize - 1) / motosPageSize).coerceAtLeast(1)
	val pagedMotos = remember(filteredMotos, motosPage, motosTotalPages) {
		val safePage = motosPage.coerceIn(1, motosTotalPages)
		filteredMotos.drop((safePage - 1) * motosPageSize).take(motosPageSize)
	}

	LaunchedEffect(filters) { motosPage = 1 }
	LaunchedEffect(motosTotalPages) {
		if (motosPage > motosTotalPages) motosPage = motosTotalPages
	}

	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.spacedBy(24.dp),
	) {
		// Header conforme imagem do Front Web
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(
					Icons.Default.TwoWheeler,
					contentDescription = null,
					tint = Color(0xFFFFB300), // Dourado como no ícone da imagem
					modifier = Modifier.size(36.dp)
				)
				Spacer(Modifier.width(16.dp))
				Column {
					Text(
						if (selectedTab == MotoSectionTab.MODELOS) "Modelos de Motos" else "Frota de Motos",
						color = Color.White,
						fontSize = 32.sp,
						fontWeight = FontWeight.Bold
					)
					Text(
						"${uiState.motos.size} cadastradas",
						color = Color.White.copy(0.4f),
						fontSize = 14.sp
					)
				}
			}

			Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				OutlinedButton(
					onClick = { /* Exportar PDF */ },
					shape = RoundedCornerShape(12.dp),
					colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
					border = BorderStroke(1.dp, Color.White.copy(0.1f)),
					modifier = Modifier.height(48.dp)
				) {
					Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(20.dp))
					Spacer(Modifier.width(8.dp))
					Text("Exportar PDF", fontWeight = FontWeight.SemiBold)
				}

				Button(
					onClick = { /* Nova Moto */ },
					shape = RoundedCornerShape(12.dp),
					colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20E65B), contentColor = Color.Black),
					modifier = Modifier.height(48.dp)
				) {
					Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
					Spacer(Modifier.width(8.dp))
					Text(if (selectedTab == MotoSectionTab.MODELOS) "Novo modelo" else "Nova moto", fontWeight = FontWeight.SemiBold)
				}
			}
		}

		when (selectedTab) {
			MotoSectionTab.FROTA -> {
				Column(verticalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.fillMaxWidth()) {
					FrotaControls(
						query = filters.query,
						onQueryChange = { filters = filters.copy(query = it) },
						status = filters.status,
						onStatusChange = { filters = filters.copy(status = it) },
						resultCount = filteredMotos.size,
					)

					if (uiState.isLoading) {
						Box(Modifier.fillMaxWidth().height(280.dp), contentAlignment = Alignment.Center) {
							CircularProgressIndicator(color = Color(0xFF20E65B))
						}
					} else if (uiState.errorMessage != null) {
						Card(
							colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(0.1f)),
							border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.2f))
						) {
							Text(
								text = uiState.errorMessage!!,
								color = Color(0xFFFF4A4A),
								modifier = Modifier.padding(16.dp)
							)
						}
					} else if (filteredMotos.isEmpty()) {
						Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
							Text(
								"Nenhuma moto encontrada para os filtros.",
								color = Color.White.copy(alpha = 0.4f),
								fontSize = 14.sp,
							)
						}
					} else {
						MotosList(
							motos = pagedMotos,
							onView = { viewingMoto = it },
							onEdit = { editingMoto = it },
							onDelete = { /* manter por enquanto */ },
						)
						if (motosTotalPages > 1) {
							MotosPager(
								currentPage = motosPage.coerceIn(1, motosTotalPages),
								totalPages = motosTotalPages,
								totalItems = filteredMotos.size,
								onPrev = { if (motosPage > 1) motosPage -= 1 },
								onNext = { if (motosPage < motosTotalPages) motosPage += 1 },
							)
						}
					}
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

		editingMoto?.let { moto ->
			MotoEditDialogExpanded(
				moto = moto,
				isSaving = savingMoto,
				errorMessage = motoEditError,
				apiBaseUrl = apiBaseUrl,
				apiAccessToken = apiAccessToken,
				onDismiss = {
					editingMoto = null
					motoEditError = null
				},
				onSave = { request ->
					savingMoto = true
					motoEditError = null
					scope.launch {
						runCatching {
							api.motos.update(moto.id, request)
						}.onSuccess {
							editingMoto = null
							runCatching {
								api.motos.list(ListMotosQuery(page = 1, limit = 50)).items
							}.onSuccess { motos ->
								uiState = uiState.copy(isLoading = false, motos = motos, errorMessage = null)
							}.onFailure { refreshError ->
								motoEditError = refreshError.message ?: "Moto salva, mas falha ao recarregar a lista."
							}
						}.onFailure { error ->
							motoEditError = error.message ?: "Falha ao salvar a moto."
						}
						savingMoto = false
					}
				},
			)
		}

		viewingMoto?.let { moto ->
			MotoDetailScreenLikeDialog(
				moto = moto,
				onDismiss = {
					viewingMoto = null
				},
				onEdit = {
					editingMoto = moto
				},
			)
		}
	}
}
