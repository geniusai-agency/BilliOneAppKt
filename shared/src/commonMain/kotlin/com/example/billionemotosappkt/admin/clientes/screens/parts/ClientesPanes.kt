package com.example.billionemotosappkt.desktop.admin.clientes.screens.parts

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
import kotlinx.coroutines.launch

@Composable
internal fun ClientsPane(
	clientes: List<ClienteListItem>,
	compact: Boolean,
	search: String,
	onSearchChange: (String) -> Unit,
	statusFilter: ClienteAprovacaoStatus?,
	onStatusFilterChange: (ClienteAprovacaoStatus?) -> Unit,
	city: String,
	onCityChange: (String) -> Unit,
	state: String,
	onStateChange: (String) -> Unit,
	planosDisponiveis: List<PlanoResponse>,
	planoFilter: String?,
	onPlanoFilterChange: (String?) -> Unit,
	motoFilter: String,
	onMotoFilterChange: (String) -> Unit,
	onClearFilters: () -> Unit,
	isLoading: Boolean,
	summary: SummaryCounters,
	currentPage: Int,
	totalPages: Int,
	totalItems: Int,
	onPrev: () -> Unit,
	onNext: () -> Unit,
	onView: (ClienteListItem) -> Unit,
	onEdit: (ClienteListItem) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
		SummaryRow(
			items = listOf(
				"Total" to summary.total,
				"Aprovados" to summary.aprovados,
				"Pendentes" to summary.pendentes,
				"Reprovados" to summary.reprovados,
			),
			compact = compact,
		)
		
		FilterPanel(
			search = search,
			onSearchChange = onSearchChange,
			statusFilter = statusFilter,
			onStatusFilterChange = onStatusFilterChange,
			city = city,
			onCityChange = onCityChange,
			state = state,
			onStateChange = onStateChange,
			planosDisponiveis = planosDisponiveis,
			planoFilter = planoFilter,
			onPlanoFilterChange = onPlanoFilterChange,
			motoFilter = motoFilter,
			onMotoFilterChange = onMotoFilterChange,
			onClearFilters = onClearFilters,
			compact = compact,
		)
		
		ClientsTable(
			clientes = clientes,
			isLoading = isLoading,
			compact = compact,
			onView = onView,
			onEdit = onEdit,
		)
		
		PagerBar(
			currentPage = currentPage,
			totalPages = totalPages,
			totalItems = totalItems,
			onPrev = onPrev,
			onNext = onNext,
		)
	}
}

@Composable
internal fun AnalisesPane(
	analises: List<AnaliseListItem>,
	compact: Boolean,
	search: String,
	onSearchChange: (String) -> Unit,
	statusFilter: ClienteAnalisePedidoStatus?,
	onStatusFilterChange: (ClienteAnalisePedidoStatus?) -> Unit,
	isLoading: Boolean,
	summary: SummaryCounters,
	currentPage: Int,
	totalPages: Int,
	totalItems: Int,
	onPrev: () -> Unit,
	onNext: () -> Unit,
	onView: (AnaliseListItem) -> Unit,
	onApprove: (AnaliseListItem) -> Unit,
	onReject: (AnaliseListItem) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
		SummaryRow(
			items = listOf(
				"Fila" to summary.total,
				"Pendentes" to summary.pendentes,
				"Em análise" to summary.emAnalise,
				"Reprovados" to summary.reprovados,
			),
			compact = compact,
		)
		
		SearchField(
			value = search,
			onValueChange = onSearchChange,
			placeholder = "Buscar pedido..."
		)
		
		AnalisesFilterStrip(
			statusFilter = statusFilter,
			onStatusFilterChange = onStatusFilterChange,
			compact = compact,
		)
		
		AnalysisTable(
			analises = analises,
			isLoading = isLoading,
			compact = compact,
			onView = onView,
			onApprove = onApprove,
			onReject = onReject,
		)
		
		PagerBar(
			currentPage = currentPage,
			totalPages = totalPages,
			totalItems = totalItems,
			onPrev = onPrev,
			onNext = onNext,
		)
	}
}

@Composable
internal fun RichClientMainPanel(
	api: BillioneMotosApi,
	cliente: AnaliseListItem,
	modelosDisponiveis: List<MotoModeloResponse>,
	modifier: Modifier = Modifier,
) {
	val requestedModel = remember(cliente.modeloMotoId, modelosDisponiveis, cliente.motoNome) {
		resolveRequestedMotoLabel(
			modeloMotoId = cliente.modeloMotoId,
			fallbackName = cliente.motoNome,
			modelosDisponiveis = modelosDisponiveis,
		)
	}
	
	Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
		DetailCard(
			title = "Dados principais",
			rows = listOf(
				"CPF" to cliente.cpf.ifBlank { "-" },
				"Telefone" to cliente.telefone.ifBlank { "-" },
				"Email" to cliente.email.ifBlank { "-" },
				"Cidade" to cliente.cityLabel,
				"Plano" to cliente.planoNome.ifBlank { "-" },
				"Moto solicitada" to requestedModel,
			),
		)
		
		DetailCard(
			title = "Mini previews",
			rows = emptyList(),
			body = {
				BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
					val compact = maxWidth < 980.dp
					val docs = listOf(
						DocumentPreviewData(
							"CNH",
							listOf(cliente.cnh, cliente.cnhCategoria)
								.filter { it.isNotBlank() }
								.joinToString(" / ")
								.ifBlank { "Documento pendente" },
							cliente.cnhUrl.takeIf { it.isNotBlank() }),
						DocumentPreviewData(
							"Identidade",
							"Documento de identidade",
							cliente.identidadeUrl.takeIf { it.isNotBlank() }),
						DocumentPreviewData(
							"Comprovante",
							"Comprovante de residência",
							cliente.comprovanteResidenciaUrl.takeIf { it.isNotBlank() }),
					)
					if (compact) {
						Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
							docs.forEach { doc ->
								DocumentPreviewCard(
									data = doc,
									api = api,
									modifier = Modifier.fillMaxWidth()
								)
							}
						}
					} else {
						Row(
							horizontalArrangement = Arrangement.spacedBy(12.dp),
							modifier = Modifier.fillMaxWidth()
						) {
							docs.forEach { doc ->
								DocumentPreviewCard(
									data = doc,
									api = api,
									modifier = Modifier.weight(1f)
								)
							}
						}
					}
				}
			},
		)
		
		DetailCard(
			title = "Contrato e origem",
			rows = listOf(
				"Usuario" to (cliente.userId.ifBlank { "-" }),
				"Pedido" to cliente.status.label(),
				"Plano ID" to (cliente.planoId.ifBlank { "-" }),
				"Modelo solicitado" to requestedModel,
			),
		)
		
		DetailCard(
			title = "Fluxo da análise",
			rows = listOf(
				"Criado em" to formatDate(cliente.createdAt),
				"Atualizado em" to formatDate(cliente.updatedAt.ifBlank { cliente.createdAt.orEmpty() }),
				"Cooldown" to formatDate(cliente.cooldownUntil),
				"Decidido por" to (cliente.decididoPor.ifBlank { "-" }),
				"Decidido em" to formatDate(cliente.decididoEm),
				"Decisão" to (cliente.decisaoJustificativa.ifBlank { "Sem decisão registrada" }),
			),
		)
	}
}

@Composable
internal fun DocumentsOnlyPanel(
	api: BillioneMotosApi,
	cliente: AnaliseListItem,
	modelosDisponiveis: List<MotoModeloResponse>,
) {
	val requestedModel = remember(cliente.modeloMotoId, modelosDisponiveis, cliente.motoNome) {
		resolveRequestedMotoLabel(
			modeloMotoId = cliente.modeloMotoId,
			fallbackName = cliente.motoNome,
			modelosDisponiveis = modelosDisponiveis,
		)
	}
	
	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
		DetailCard(
			title = "Dados do pedido",
			rows = listOf(
				"Plano" to cliente.planoNome.ifBlank { "-" },
				"Cidade" to cliente.cityLabel,
				"Estado" to cliente.estado.ifBlank { "-" },
				"CEP" to cliente.cep.ifBlank { "-" },
				"Observações" to (cliente.observacoes.ifBlank { "-" }),
				"Modelo solicitado" to requestedModel,
			),
		)
		DetailCard(
			title = "Documentos",
			rows = emptyList(),
			body = {
				BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
					val compact = maxWidth < 980.dp
					val docs = listOf(
						DocumentPreviewData(
							"CNH",
							listOf(cliente.cnh, cliente.cnhCategoria)
								.filter { it.isNotBlank() }
								.joinToString(" / ")
								.ifBlank { "Documento pendente" },
							cliente.cnhUrl.takeIf { it.isNotBlank() }),
						DocumentPreviewData(
							"Identidade",
							"Documento de identidade",
							cliente.identidadeUrl.takeIf { it.isNotBlank() }),
						DocumentPreviewData(
							"Comprovante",
							"Comprovante de residência",
							cliente.comprovanteResidenciaUrl.takeIf { it.isNotBlank() }),
					)
					if (compact) {
						Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
							docs.forEach { doc ->
								DocumentPreviewCard(
									data = doc,
									api = api,
									modifier = Modifier.fillMaxWidth()
								)
							}
						}
					} else {
						Row(
							horizontalArrangement = Arrangement.spacedBy(12.dp),
							modifier = Modifier.fillMaxWidth()
						) {
							docs.forEach { doc ->
								DocumentPreviewCard(
									data = doc,
									api = api,
									modifier = Modifier.weight(1f)
								)
							}
						}
					}
				}
			},
		)
	}
}

@Composable
internal fun RichClientSidePanel(
	api: BillioneMotosApi,
	cliente: AnaliseListItem,
	modelosDisponiveis: List<MotoModeloResponse>,
	motosDisponiveis: List<MotoResponse>,
	selectedStatus: ClienteAprovacaoStatus,
	justificativa: String,
	motoId: String,
	onStatusChange: (ClienteAprovacaoStatus) -> Unit,
	onJustificativaChange: (String) -> Unit,
	onMotoIdChange: (String) -> Unit,
	onSave: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var motoMenuExpanded by remember(cliente.id) { mutableStateOf(false) }
	var motoSearchQuery by remember(cliente.id) { mutableStateOf("") }
	var motosLoading by remember(cliente.id) { mutableStateOf(false) }
	var motosLoadError by remember(cliente.id) { mutableStateOf<String?>(null) }
	var motosDisponiveisState by remember(cliente.id) { mutableStateOf(motosDisponiveis) }
	
	LaunchedEffect(cliente.id) {
		motosLoading = true
		motosLoadError = null
		val loaded = runCatching {
			api.motos.list(ListMotosQuery(page = 1, limit = 100, status = MotoStatus.DISPONIVEL)).items
		}.recoverCatching {
			api.motos.list(
				ListMotosQuery(
					status = MotoStatus.DISPONIVEL,
					page = 1,
					limit = 100,
				),
			).items
		}
		loaded
			.onSuccess { motosDisponiveisState = it }
			.onFailure { error ->
				motosLoadError = error.message ?: "Falha ao carregar motos disponiveis."
			}
		motosLoading = false
	}
	
	val requestedModel = remember(cliente.modeloMotoId, modelosDisponiveis, cliente.motoNome) {
		resolveRequestedMotoLabel(
			modeloMotoId = cliente.modeloMotoId,
			fallbackName = cliente.motoNome,
			modelosDisponiveis = modelosDisponiveis,
		)
	}
	val motosParaSelecao = remember(motosDisponiveisState) {
		motosDisponiveisState.ifEmpty { emptyList() }
	}
	val selectedMoto = remember(motoId, motosDisponiveisState) {
		motosDisponiveisState.firstOrNull { it.id == motoId.trim() }
	}
	val filteredMotos = remember(motosParaSelecao, motoSearchQuery, selectedMoto) {
		val query = motoSearchQuery.trim().lowercase()
		val matches = if (query.isBlank()) {
			motosParaSelecao
		} else {
			motosParaSelecao.filter { moto ->
				listOfNotNull(
					moto.modelo,
					moto.marca,
					moto.placa,
					moto.ano?.toString(),
					moto.cor,
					moto.status.name,
				).any { it.contains(query, ignoreCase = true) }
			}
		}
		matches.sortedByDescending { it.id == selectedMoto?.id }
	}
	val requiresMotoForSave = selectedStatus == ClienteAprovacaoStatus.APROVADO ||
			selectedStatus == ClienteAprovacaoStatus.APROVADO_COM_RESSALVA
	val canSave = !requiresMotoForSave || selectedMoto != null
	val motoAvailabilityLabel = when {
		motosParaSelecao.isEmpty() -> "Nenhuma moto DISPONIVEL"
		motosParaSelecao.size == 1 -> "1 moto DISPONIVEL"
		else -> "${motosParaSelecao.size} motos DISPONIVEIS"
	}
	val saveButtonLabel = when (selectedStatus) {
		ClienteAprovacaoStatus.APROVADO, ClienteAprovacaoStatus.APROVADO_COM_RESSALVA -> "Aprovar e gerar contrato"
		ClienteAprovacaoStatus.REPROVADO -> "Salvar rejeicao"
		ClienteAprovacaoStatus.EM_ANALISE -> "Salvar analise"
		ClienteAprovacaoStatus.PENDENTE -> "Salvar como pendente"
		ClienteAprovacaoStatus.AGUARDANDO_ASSINATURA -> "Salvar aguardando"
	}
	
	Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
		DetailCard(
			title = "Ações rápidas",
			rows = emptyList(),
			body = {
				Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
					Text(
						when {
							motosLoading -> "Carregando motos disponiveis..."
							motosLoadError != null && motosParaSelecao.isEmpty() -> motosLoadError.orEmpty()
							motosParaSelecao.isEmpty() -> "Nao existe moto disponivel para gerar contrato agora. Libere uma moto no estoque ou reprova a solicitacao."
							selectedStatus == ClienteAprovacaoStatus.APROVADO ||
									selectedStatus == ClienteAprovacaoStatus.APROVADO_COM_RESSALVA ->
								"Selecione uma moto disponivel antes de aprovar e gerar o contrato."
							
							else -> "Decida, vincule a moto disponivel e salve."
						},
						color = when {
							motosLoading -> Color.White.copy(alpha = 0.44f)
							motosLoadError != null && motosParaSelecao.isEmpty() -> Color(0xFFFFB4B4)
							else -> Color.White.copy(alpha = 0.44f)
						},
						fontSize = 11.sp,
						lineHeight = 15.sp,
					)
					
					Row(
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						modifier = Modifier.fillMaxWidth()
					) {
						ActionChip(
							text = "Aprovar",
							accent = Color(0xFF20E65B),
							modifier = Modifier.weight(1f),
							onClick = {
								onStatusChange(ClienteAprovacaoStatus.APROVADO)
								onJustificativaChange(defaultJustificativa(ClienteAprovacaoStatus.APROVADO))
							},
						)
						ActionChip(
							text = "Ressalva",
							accent = Color(0xFF5BC0FF),
							modifier = Modifier.weight(1f),
							onClick = {
								onStatusChange(ClienteAprovacaoStatus.APROVADO_COM_RESSALVA)
								onJustificativaChange(defaultJustificativa(ClienteAprovacaoStatus.APROVADO_COM_RESSALVA))
							},
						)
						ActionChip(
							text = "Reprovar",
							accent = Color(0xFFFF4A4A),
							modifier = Modifier.weight(1f),
							outlined = true,
							onClick = {
								onStatusChange(ClienteAprovacaoStatus.REPROVADO)
								onJustificativaChange(defaultJustificativa(ClienteAprovacaoStatus.REPROVADO))
							},
						)
					}
					
					BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
						val compact = maxWidth < 840.dp
						if (compact) {
							Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
								CompactMetaCard(title = "Modelo solicitado", value = requestedModel)
								CompactMetaCard(
									title = "Estoque",
									value = when {
										motosLoading -> "Carregando..."
										motosLoadError != null -> "Erro ao carregar"
										else -> motoAvailabilityLabel
									},
								)
								CompactMetaCard(
									title = "Moto vinculada",
									value = selectedMoto?.let { motoLabel(it) }
										?: if (motoId.isBlank()) "Nenhuma selecionada" else motoId,
								)
							}
						} else {
							Row(
								horizontalArrangement = Arrangement.spacedBy(10.dp),
								modifier = Modifier.fillMaxWidth()
							) {
								CompactMetaCard(
									title = "Modelo solicitado",
									value = requestedModel,
									modifier = Modifier.weight(1f),
								)
								CompactMetaCard(
									title = "Estoque",
									value = when {
										motosLoading -> "Carregando..."
										motosLoadError != null -> "Erro ao carregar"
										else -> motoAvailabilityLabel
									},
									modifier = Modifier.weight(1f),
								)
								CompactMetaCard(
									title = "Moto vinculada",
									value = selectedMoto?.let { motoLabel(it) }
										?: if (motoId.isBlank()) "Nenhuma selecionada" else motoId,
									modifier = Modifier.weight(1f),
								)
							}
						}
					}
					
					val statusOptions = listOf(
						ClienteAprovacaoStatus.PENDENTE,
						ClienteAprovacaoStatus.EM_ANALISE,
						ClienteAprovacaoStatus.APROVADO,
						ClienteAprovacaoStatus.APROVADO_COM_RESSALVA,
						ClienteAprovacaoStatus.REPROVADO,
						ClienteAprovacaoStatus.AGUARDANDO_ASSINATURA,
					)
					
					BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
						val compact = maxWidth < 520.dp
						if (compact) {
							Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
								statusOptions.chunked(2).forEach { row ->
									Row(
										horizontalArrangement = Arrangement.spacedBy(8.dp),
										modifier = Modifier.fillMaxWidth()
									) {
										row.forEach { status ->
											StatusChip(
												status = status,
												selected = selectedStatus == status,
												onClick = { onStatusChange(status) },
												modifier = Modifier.weight(1f)
											)
										}
										if (row.size == 1) Spacer(Modifier.weight(1f))
									}
								}
							}
						} else {
							Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
								Row(
									horizontalArrangement = Arrangement.spacedBy(8.dp),
									modifier = Modifier.fillMaxWidth()
								) {
									statusOptions.take(3).forEach { status ->
										StatusChip(
											status = status,
											selected = selectedStatus == status,
											onClick = { onStatusChange(status) },
											modifier = Modifier.weight(1f)
										)
									}
								}
								Row(
									horizontalArrangement = Arrangement.spacedBy(8.dp),
									modifier = Modifier.fillMaxWidth()
								) {
									statusOptions.drop(3).forEach { status ->
										StatusChip(
											status = status,
											selected = selectedStatus == status,
											onClick = { onStatusChange(status) },
											modifier = Modifier.weight(1f)
										)
									}
								}
							}
						}
					}
					
					BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
						val wideMode = maxWidth >= 1120.dp
						val menuWidth = if (wideMode) 760.dp else 420.dp
						val triggerPaddingY = if (wideMode) 16.dp else 12.dp
						val triggerMinHeight = if (wideMode) 76.dp else 64.dp

						Box(modifier = Modifier.fillMaxWidth()) {
							Surface(
								onClick = {
									if (!motosLoading && motosParaSelecao.isNotEmpty()) motoMenuExpanded = true
								},
								shape = RoundedCornerShape(18.dp),
								color = Color(0xFF0C120D),
								border = BorderStroke(
									1.dp,
									if (motoMenuExpanded) Color(0xFF20E65B).copy(alpha = 0.42f) else Color.White.copy(alpha = 0.06f),
								),
								shadowElevation = if (wideMode) 5.dp else 2.dp,
								modifier = Modifier.fillMaxWidth().heightIn(min = triggerMinHeight),
							) {
								Row(
									modifier = Modifier
										.fillMaxWidth()
										.padding(horizontal = 16.dp, vertical = triggerPaddingY),
									horizontalArrangement = Arrangement.spacedBy(14.dp),
									verticalAlignment = Alignment.CenterVertically,
								) {
									Box(
										modifier = Modifier
											.size(if (wideMode) 42.dp else 38.dp)
											.background(
												if (selectedMoto != null) selectedStatus.accent().copy(alpha = 0.16f) else Color.White.copy(alpha = 0.05f),
												CircleShape,
											),
										contentAlignment = Alignment.Center,
									) {
										Text(
											text = when {
												motosLoading -> "…"
												selectedMoto != null -> "✓"
												else -> "⌄"
											},
											color = if (selectedMoto != null) selectedStatus.accent() else Color.White.copy(alpha = 0.64f),
											fontWeight = FontWeight.Bold,
											fontSize = if (wideMode) 18.sp else 16.sp,
										)
									}
									Column(
										modifier = Modifier.weight(1f),
										verticalArrangement = Arrangement.spacedBy(4.dp),
									) {
										Text(
											"Moto vinculada",
											color = Color.White.copy(alpha = 0.42f),
											fontSize = 11.sp,
										)
										Text(
											when {
												motosLoading -> "Carregando motos..."
												motosLoadError != null && motosParaSelecao.isEmpty() -> "Falha ao carregar"
												selectedMoto != null -> motoLabel(selectedMoto)
												motosParaSelecao.isEmpty() -> "Sem moto disponivel para contrato"
												motoId.isNotBlank() -> motoId
												else -> "Selecione uma moto disponivel"
											},
											color = Color.White,
											fontSize = if (wideMode) 15.sp else 13.sp,
											fontWeight = FontWeight.SemiBold,
											maxLines = 1,
											overflow = TextOverflow.Ellipsis,
										)
										Text(
											text = when {
												motosLoading -> "Buscando estoque disponivel..."
												selectedMoto != null -> "Clique para trocar a moto selecionada"
												motosParaSelecao.isNotEmpty() -> "${motosParaSelecao.size} motos prontas para reserva"
												else -> "Sem estoque disponivel agora"
											},
											color = Color.White.copy(alpha = 0.40f),
											fontSize = 11.sp,
											maxLines = 1,
											overflow = TextOverflow.Ellipsis,
										)
									}
									if (wideMode) {
										Surface(
											shape = RoundedCornerShape(999.dp),
											color = Color.White.copy(alpha = 0.04f),
											border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
										) {
											Text(
												text = if (motoMenuExpanded) "Fechar" else "Abrir lista",
												color = Color.White.copy(alpha = 0.72f),
												fontSize = 12.sp,
												fontWeight = FontWeight.SemiBold,
												modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
											)
										}
									} else {
										Text(
											text = if (motoMenuExpanded) "▴" else "▾",
											color = Color.White.copy(alpha = 0.55f),
											fontSize = 14.sp,
											fontWeight = FontWeight.Bold,
										)
									}
								}
							}
							DropdownMenu(
								expanded = motoMenuExpanded,
								onDismissRequest = {
									motoMenuExpanded = false
									motoSearchQuery = ""
								},
								modifier = Modifier.widthIn(min = menuWidth, max = menuWidth),
							) {
								Column(
									modifier = Modifier.padding(14.dp).widthIn(min = menuWidth - 28.dp),
									verticalArrangement = Arrangement.spacedBy(14.dp),
								) {
									Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
										Text(
											"Selecione uma moto disponível",
											color = Color.White,
											fontWeight = FontWeight.SemiBold,
											fontSize = 14.sp,
										)
										Text(
											"Escolha uma moto física para reservar o contrato antes de aprovar.",
											color = Color.White.copy(alpha = 0.46f),
											fontSize = 11.sp,
										)
									}

									OutlinedTextField(
										value = motoSearchQuery,
										onValueChange = { motoSearchQuery = it },
										modifier = Modifier.fillMaxWidth(),
										shape = RoundedCornerShape(14.dp),
										singleLine = true,
										placeholder = { Text("Buscar por modelo, placa, cor ou ano") },
										leadingIcon = {
											Icon(
												Icons.Default.Search,
												contentDescription = null,
												tint = Color.White.copy(alpha = 0.42f),
											)
										},
										colors = OutlinedTextFieldDefaults.colors(
											focusedTextColor = Color.White,
											unfocusedTextColor = Color.White,
											focusedContainerColor = Color.White.copy(alpha = 0.03f),
											unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
											focusedBorderColor = Color(0xFF20E65B).copy(alpha = 0.38f),
											unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
											cursorColor = Color(0xFF20E65B),
										),
									)

									if (selectedMoto != null) {
										MotoSelectionHero(
											moto = selectedMoto,
											onClick = {
												onMotoIdChange(selectedMoto.id)
												motoMenuExpanded = false
												motoSearchQuery = ""
											},
										)
									}

									val available = filteredMotos.filterNot { it.id == selectedMoto?.id }
									when {
										motosLoading && motosParaSelecao.isEmpty() -> {
											Text(
												"Carregando motos disponiveis...",
												color = Color.White.copy(alpha = 0.65f),
												fontSize = 12.sp,
											)
										}
										motosLoadError != null && motosParaSelecao.isEmpty() -> {
											Text(
												motosLoadError.orEmpty(),
												color = Color(0xFFFFB4B4),
												fontSize = 12.sp,
											)
										}
										available.isEmpty() -> {
											Text(
												if (motoSearchQuery.isBlank()) {
													"Nenhuma moto disponivel para contrato"
												} else {
													"Nenhuma moto encontrada para a busca"
												},
												color = Color.White.copy(alpha = 0.58f),
												fontSize = 12.sp,
											)
										}
										wideMode -> {
											available.chunked(2).forEach { row ->
												Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
													row.forEach { moto ->
														MotoSelectionTile(
															moto = moto,
															selected = moto.id == motoId.trim(),
															wideMode = true,
															modifier = Modifier.weight(1f),
															onClick = {
																onMotoIdChange(moto.id)
																motoMenuExpanded = false
																motoSearchQuery = ""
															},
														)
													}
													if (row.size == 1) Spacer(Modifier.weight(1f))
												}
											}
										}
										else -> {
											Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
												available.forEach { moto ->
													MotoSelectionTile(
														moto = moto,
														selected = moto.id == motoId.trim(),
														wideMode = false,
														modifier = Modifier.fillMaxWidth(),
														onClick = {
															onMotoIdChange(moto.id)
															motoMenuExpanded = false
															motoSearchQuery = ""
														},
													)
												}
											}
										}
									}
								}
							}
						}
					}
					
					OutlinedTextField(
						value = justificativa,
						onValueChange = onJustificativaChange,
						modifier = Modifier.fillMaxWidth(),
						minLines = 2,
						maxLines = 4,
						label = { Text("Justificativa") },
						placeholder = { Text("Explique a decisão") },
						shape = RoundedCornerShape(14.dp),
						colors = OutlinedTextFieldDefaults.colors(
							focusedTextColor = Color.White,
							unfocusedTextColor = Color.White,
							focusedContainerColor = Color.White.copy(alpha = 0.02f),
							unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
							focusedBorderColor = Color.White.copy(alpha = 0.12f),
							unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
							cursorColor = Color(0xFF20E65B),
						),
					)
					
					Row(
						horizontalArrangement = Arrangement.End,
						modifier = Modifier.fillMaxWidth()
					) {
						Button(
							onClick = onSave,
							enabled = canSave,
							shape = RoundedCornerShape(12.dp),
							colors = ButtonDefaults.buttonColors(
								containerColor = selectedStatus.accent(),
								contentColor = Color.Black,
								disabledContainerColor = Color.White.copy(alpha = 0.08f),
								disabledContentColor = Color.White.copy(alpha = 0.35f),
							),
							contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
						) {
							Text(
								text = if (canSave) saveButtonLabel else "Selecione uma moto",
								fontWeight = FontWeight.SemiBold,
								fontSize = 12.sp,
							)
						}
					}
				}
			},
		)
		
	}
}

