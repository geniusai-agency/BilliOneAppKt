package com.example.billionemotosappkt.desktop.admin.clientes.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.billionemotosappkt.desktop.admin.clientes.components.ClienteEditDialog
import com.example.billionemotosappkt.desktop.admin.clientes.components.DocumentPreviewCard
import com.example.billionemotosappkt.desktop.admin.clientes.components.DocumentPreviewData
import com.example.billionemotosappkt.desktop.admin.clientes.model.AnaliseListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.accent
import com.example.billionemotosappkt.desktop.admin.clientes.model.label
import com.example.billionemotosappkt.desktop.admin.clientes.model.toUpdateRequest
import com.example.billionemotosappkt.desktop.admin.clientes.repository.AnalisesLoadResult
import com.example.billionemotosappkt.desktop.admin.clientes.repository.ClientesLoadResult
import com.example.billionemotosappkt.desktop.admin.clientes.repository.ClientesRepository
import com.example.billionemotosappkt.desktop.admin.components.BadgePill
import com.example.billionemotosappkt.desktop.admin.components.clientes.ClientesSubsectionToggle
import com.example.billionemotosappkt.desktop.admin.model.ClientesSectionTab
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ClienteAnalisePedidoStatus
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.DecisaoAprovacaoClienteResponse
import com.example.billionemotosappkt.shared.api.DecisaoClienteRequest
import com.example.billionemotosappkt.shared.api.ListAnalisesQuery
import com.example.billionemotosappkt.shared.api.ListClientesQuery
import com.example.billionemotosappkt.shared.api.ListMotosQuery
import com.example.billionemotosappkt.shared.api.MotoModeloResponse
import com.example.billionemotosappkt.shared.api.MotoResponse
import com.example.billionemotosappkt.shared.api.MotoStatus
import com.example.billionemotosappkt.shared.api.PlanoResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.billionemotosappkt.desktop.admin.clientes.screens.parts.*

@Composable
fun ClientesSection(
	api: BillioneMotosApi,
	compact: Boolean,
	selectedTab: ClientesSectionTab,
	onTabChange: (ClientesSectionTab) -> Unit,
	onOpenClienteDetail: (ClienteListItem) -> Unit,
) {
	val repository = remember(api) { ClientesRepository(api) }
	val scope = rememberCoroutineScope()
	
	var clientesResult by remember {
		mutableStateOf(
			ClientesLoadResult(
				emptyList(),
				total = 0,
				page = 1,
				limit = 20,
				isFallbackData = false
			)
		)
	}
	var clientesLoading by remember { mutableStateOf(true) }
	var clientesContractsLoading by remember { mutableStateOf(false) }
	var clientesError by remember { mutableStateOf<String?>(null) }
	var clientesSearch by remember { mutableStateOf("") }
	var clientesStatus by remember { mutableStateOf<ClienteAprovacaoStatus?>(null) }
	var clientesCity by remember { mutableStateOf("") }
	var clientesState by remember { mutableStateOf("") }
	var clientesPlanoId by remember { mutableStateOf<String?>(null) }
	var clientesMotoFilter by remember { mutableStateOf("") }
	var clientesPage by remember { mutableIntStateOf(1) }
	val clientesPageSize = 12
	var planosDisponiveis by remember { mutableStateOf<List<PlanoResponse>>(emptyList()) }
	var modelosDisponiveis by remember { mutableStateOf<List<MotoModeloResponse>>(emptyList()) }
	var motosDisponiveis by remember { mutableStateOf<List<MotoResponse>>(emptyList()) }
	var clientesEnriched by remember { mutableStateOf<List<ClienteListItem>>(emptyList()) }
	
	var analisesResult by remember {
		mutableStateOf(
			AnalisesLoadResult(
				emptyList(),
				total = 0,
				page = 1,
				limit = 20,
				isFallbackData = false
			)
		)
	}
	var analisesLoading by remember { mutableStateOf(true) }
	var analisesError by remember { mutableStateOf<String?>(null) }
	var analisesSearch by remember { mutableStateOf("") }
	var analisesStatus by remember { mutableStateOf<ClienteAnalisePedidoStatus?>(null) }
	var analisesPage by remember { mutableIntStateOf(1) }
	val analisesPageSize = 10
	var analisePanel by remember { mutableStateOf<AnaliseListItem?>(null) }
	var editingCliente by remember { mutableStateOf<ClienteListItem?>(null) }
	var history by remember { mutableStateOf(emptyList<DecisaoAprovacaoClienteResponse>()) }
	var historyLoading by remember { mutableStateOf(false) }
	var decisionTarget by remember { mutableStateOf<DecisionDialogState?>(null) }
	
	var reloadTick by remember { mutableIntStateOf(0) }
	
	LaunchedEffect(repository) {
		runCatching { repository.loadPlano() }
			.onSuccess { planosDisponiveis = it }
	}
	
	LaunchedEffect(api) {
		runCatching {
			api.motos.modelos()
		}.onSuccess { modelosDisponiveis = it }
	}
	
	LaunchedEffect(api) {
		runCatching {
			api.motos.list(
				ListMotosQuery(
					status = MotoStatus.DISPONIVEL,
					page = 1,
					limit = 200,
				),
			).items
		}.onSuccess { motosDisponiveis = it }
			.onFailure {
				runCatching {
					api.motos.list(ListMotosQuery(page = 1, limit = 200)).items
				}.onSuccess { motosDisponiveis = it }
			}
	}
	
	LaunchedEffect(
		repository,
		clientesPage,
		clientesSearch,
		clientesStatus,
		clientesCity,
		clientesState,
		clientesPlanoId,
		reloadTick
	) {
		clientesLoading = true
		clientesError = null
		// Debounce: evita disparar uma chamada de API a cada tecla digitada
		// na busca / cidade / estado. Se qualquer chave mudar antes do tempo,
		// o efeito reinicia e cancela a chamada anterior.
		delay(300)
		runCatching {
			repository.loadClientes(
				ListClientesQuery(
					q = clientesSearch.trim().ifBlank { null },
					statusAprovacao = clientesStatus,
					cidade = clientesCity.trim().ifBlank { null },
					estado = clientesState.trim().ifBlank { null },
					planoId = clientesPlanoId,
					page = clientesPage,
					limit = clientesPageSize,
				),
			)
		}
			.onSuccess {
				clientesResult = it
				clientesLoading = false
			}
			.onFailure { error ->
				clientesResult = ClientesLoadResult(
					emptyList(),
					total = 0,
					page = clientesPage,
					limit = clientesPageSize,
					isFallbackData = false
				)
				clientesLoading = false
				clientesError = error.message ?: "Falha ao carregar clientes."
			}
	}
	
	LaunchedEffect(clientesResult.clientes) {
		clientesContractsLoading = true
		clientesEnriched = runCatching {
			clientesResult.clientes.map { cliente ->
				async {
					val contrato =
						runCatching { repository.loadContratoAtual(cliente.id) }.getOrNull()
					cliente.copy(
						planoNome = contrato?.plano?.nome ?: contrato?.planoSnapshotNome.orEmpty(),
						contratoId = contrato?.id.orEmpty(),
						contratoStatus = contrato?.status?.name.orEmpty(),
						motoNome = contrato?.moto?.modelo
							?: contrato?.moto?.modeloMoto?.nome
							?: contrato?.moto?.modeloMoto?.modelo
								.orEmpty(),
						motoPlaca = contrato?.moto?.placa.orEmpty(),
						contratoAssinadoEm = contrato?.assinadoEm,
					)
				}
			}.awaitAll()
		}.getOrElse {
			clientesResult.clientes
		}
		clientesContractsLoading = false
	}
	
	LaunchedEffect(repository, analisesPage, reloadTick) {
		analisesLoading = true
		analisesError = null
		runCatching {
			repository.loadAnalises(
				ListAnalisesQuery(
					page = analisesPage,
					limit = analisesPageSize,
					sortBy = "createdAt",
					sortOrder = "desc",
				),
			)
		}.onSuccess {
			analisesResult = it
			analisesLoading = false
		}.onFailure { error ->
			analisesResult = AnalisesLoadResult(
				emptyList(),
				total = 0,
				page = analisesPage,
				limit = analisesPageSize,
				isFallbackData = false
			)
			analisesLoading = false
			analisesError = error.message ?: "Falha ao carregar pedidos de analise."
		}
	}
	
	val filteredAnalises = remember(analisesResult.analises, analisesSearch, analisesStatus) {
		val query = analisesSearch.trim().lowercase()
		analisesResult.analises.filter { item ->
			val matchesQuery = query.isBlank() || listOf(
				item.nome,
				item.cpf,
				item.email,
				item.telefone,
				item.cidade,
				item.estado,
				item.planoNome,
				item.motoNome,
			).any { candidate -> candidate.lowercase().contains(query) }
			val matchesStatus = analisesStatus == null || item.status == analisesStatus
			matchesQuery && matchesStatus
		}
	}
	
	val clientesVisiveis = remember(clientesEnriched, clientesMotoFilter) {
		val query = clientesMotoFilter.trim().lowercase()
		if (query.isBlank()) {
			clientesEnriched
		} else {
			clientesEnriched.filter { cliente ->
				listOf(
					cliente.motoNome,
					cliente.motoPlaca,
					cliente.planoNome,
				).any { candidate ->
					candidate.contains(query, ignoreCase = true)
				}
			}
		}
	}
	
	val clientesSummary = remember(clientesVisiveis) {
		SummaryCounters.from(clientesVisiveis)
	}
	val analisesSummary = remember(analisesResult.analises) {
		SummaryCounters.fromAnalises(analisesResult.analises)
	}
	
	fun refreshAll() {
		reloadTick += 1
	}
	
	fun totalPages(total: Int, limit: Int): Int {
		if (limit <= 0) return 1
		return ((total + limit - 1) / limit).coerceAtLeast(1)
	}
	
	fun openDecision(cliente: AnaliseListItem, decisao: ClienteAprovacaoStatus) {
		decisionTarget = DecisionDialogState(
			cliente = cliente,
			decisao = decisao,
			justificativa = defaultJustificativa(decisao),
			motoId = "",
		)
	}
	
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(if (compact) 8.dp else 4.dp),
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(18.dp),
			modifier = Modifier.fillMaxWidth()
		) {
			Header(
				selectedTab = selectedTab,
				compact = compact,
				clientesCount = clientesResult.total,
				analisesCount = analisesResult.total,
				onTabChange = onTabChange,
				onRefresh = { refreshAll() },
			)
			
			if (clientesError != null && selectedTab == ClientesSectionTab.CLIENTES) {
				ErrorBanner(message = clientesError.orEmpty())
			}
			
			if (analisesError != null && selectedTab == ClientesSectionTab.PEDIDOS) {
				ErrorBanner(message = analisesError.orEmpty())
			}
			
			when (selectedTab) {
				ClientesSectionTab.CLIENTES -> ClientsPane(
					clientes = clientesVisiveis,
					compact = compact,
					search = clientesSearch,
					onSearchChange = {
						clientesSearch = it
						clientesPage = 1
					},
					statusFilter = clientesStatus,
					onStatusFilterChange = {
						clientesStatus = it
						clientesPage = 1
					},
					city = clientesCity,
					onCityChange = {
						clientesCity = it
						clientesPage = 1
					},
					state = clientesState,
					onStateChange = {
						clientesState = it
						clientesPage = 1
					},
					planosDisponiveis = planosDisponiveis,
					planoFilter = clientesPlanoId,
					onPlanoFilterChange = {
						clientesPlanoId = it
						clientesPage = 1
					},
					motoFilter = clientesMotoFilter,
					onMotoFilterChange = {
						clientesMotoFilter = it
					},
					onClearFilters = {
						clientesSearch = ""
						clientesStatus = null
						clientesCity = ""
						clientesState = ""
						clientesPlanoId = null
						clientesMotoFilter = ""
						clientesPage = 1
					},
					isLoading = clientesLoading || clientesContractsLoading,
					summary = clientesSummary,
					currentPage = clientesResult.page,
					totalPages = totalPages(clientesResult.total, clientesResult.limit),
					totalItems = clientesResult.total,
					onPrev = {
						if (clientesPage > 1) clientesPage -= 1
					},
					onNext = {
						if (clientesPage < totalPages(
								clientesResult.total,
								clientesResult.limit
							)
						) clientesPage += 1
					},
					onView = onOpenClienteDetail,
					onEdit = { editingCliente = it },
				)
				
				ClientesSectionTab.PEDIDOS -> AnalisesPane(
					analises = filteredAnalises,
					compact = compact,
					search = analisesSearch,
					onSearchChange = { analisesSearch = it },
					statusFilter = analisesStatus,
					onStatusFilterChange = { analisesStatus = it },
					isLoading = analisesLoading,
					summary = analisesSummary,
					currentPage = analisesResult.page,
					totalPages = totalPages(analisesResult.total, analisesResult.limit),
					totalItems = analisesResult.total,
					onPrev = {
						if (analisesPage > 1) analisesPage -= 1
					},
					onNext = {
						if (analisesPage < totalPages(
								analisesResult.total,
								analisesResult.limit
							)
						) analisesPage += 1
					},
					onView = { analisePanel = it },
					onApprove = { openDecision(it, ClienteAprovacaoStatus.APROVADO) },
					onReject = { openDecision(it, ClienteAprovacaoStatus.REPROVADO) },
				)
			}
		}
		
		analisePanel?.let { cliente ->
			AnalysisDetailRichDialog(
				api = api,
				cliente = cliente,
				modelosDisponiveis = modelosDisponiveis,
				motosDisponiveis = motosDisponiveis,
				history = history,
				isLoadingHistory = historyLoading,
				onDismiss = {
					analisePanel = null
					history = emptyList()
				},
				onDecision = { decisao, justificativa, motoId ->
					scope.launch {
						historyLoading = true
						runCatching {
							repository.decideAnalise(
								cliente.userId,
								DecisaoClienteRequest(
									decisao = decisao,
									justificativa = justificativa.trim(),
									motoId = motoId?.trim().takeIf { !it.isNullOrBlank() },
								),
							)
						}.onSuccess {
							refreshAll()
							runCatching { repository.loadAnaliseHistory(cliente.userId) }
								.onSuccess { history = it }
								.onFailure {
									history = emptyList()
								}
							historyLoading = false
						}.onFailure { error ->
							historyLoading = false
							analisesError = error.message ?: "Falha ao registrar decisao."
						}
					}
				},
				onLoadHistory = { clienteId ->
					historyLoading = true
					scope.launch {
						runCatching { repository.loadAnaliseHistory(clienteId) }
							.onSuccess {
								history = it
								historyLoading = false
							}
							.onFailure {
								history = emptyList()
								historyLoading = false
							}
					}
				},
			)
		}
		
		decisionTarget?.let { dialogState ->
			DecisionDialog(
				state = dialogState,
				onDismiss = { decisionTarget = null },
				onStateChange = { decisionTarget = it },
				onConfirm = { currentState ->
					scope.launch {
						analisesLoading = true
						analisesError = null
						runCatching {
							repository.decideAnalise(
								currentState.cliente.id,
								DecisaoClienteRequest(
									decisao = currentState.decisao,
									justificativa = currentState.justificativa.trim(),
									motoId = currentState.motoId.trim().ifBlank { null },
								),
							)
						}.onSuccess {
							decisionTarget = null
							refreshAll()
						}.onFailure { error ->
							analisesLoading = false
							analisesError = error.message ?: "Falha ao registrar decisao."
						}
					}
				},
			)
		}
		
		editingCliente?.let { cliente ->
			ClienteEditDialog(
				api = api,
				cliente = cliente.toUpdateRequest(),
				planosDisponiveis = planosDisponiveis,
				onDismiss = { editingCliente = null },
				onSave = { body ->
					try {
						repository.updateCliente(cliente.id, body)
						editingCliente = null
						refreshAll()
					} catch (error: Throwable) {
						clientesError = error.message ?: "Falha ao salvar cliente."
					}
				},
			)
		}
	}
}
