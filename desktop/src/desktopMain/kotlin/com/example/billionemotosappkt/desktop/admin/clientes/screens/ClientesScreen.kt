package com.example.billionemotosappkt.desktop.admin.clientes.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.billionemotosappkt.desktop.admin.components.BadgePill
import com.example.billionemotosappkt.desktop.admin.components.SegmentedToggleButton
import com.example.billionemotosappkt.desktop.admin.clientes.model.AnaliseListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.accent
import com.example.billionemotosappkt.desktop.admin.clientes.model.label
import com.example.billionemotosappkt.desktop.admin.clientes.repository.AnalisesLoadResult
import com.example.billionemotosappkt.desktop.admin.clientes.repository.ClientesLoadResult
import com.example.billionemotosappkt.desktop.admin.clientes.repository.ClientesRepository
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ClienteAnalisePedidoStatus
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.DecisaoAprovacaoClienteResponse
import com.example.billionemotosappkt.shared.api.DecisaoClienteRequest
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import com.example.billionemotosappkt.shared.api.ListAnalisesQuery
import com.example.billionemotosappkt.shared.api.ListClientesQuery
import com.example.billionemotosappkt.shared.api.PlanoResponse
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.jetbrains.skia.Image
import androidx.compose.ui.graphics.toComposeImageBitmap

@Composable
fun ClientesSection(
    api: BillioneMotosApi,
    compact: Boolean,
    onOpenClienteDetail: (ClienteListItem) -> Unit,
) {
    val repository = remember(api) { ClientesRepository(api) }
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf(ClientesTab.CLIENTES) }

    var clientesResult by remember {
        mutableStateOf(ClientesLoadResult(emptyList(), total = 0, page = 1, limit = 20, isFallbackData = false))
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
    var clientesEnriched by remember { mutableStateOf<List<ClienteListItem>>(emptyList()) }

    var analisesResult by remember {
        mutableStateOf(AnalisesLoadResult(emptyList(), total = 0, page = 1, limit = 20, isFallbackData = false))
    }
    var analisesLoading by remember { mutableStateOf(true) }
    var analisesError by remember { mutableStateOf<String?>(null) }
    var analisesSearch by remember { mutableStateOf("") }
    var analisesStatus by remember { mutableStateOf<ClienteAnalisePedidoStatus?>(null) }
    var analisesPage by remember { mutableIntStateOf(1) }
    val analisesPageSize = 10
    var analisePanel by remember { mutableStateOf<AnaliseListItem?>(null) }
    var history by remember { mutableStateOf(emptyList<DecisaoAprovacaoClienteResponse>()) }
    var historyLoading by remember { mutableStateOf(false) }
    var decisionTarget by remember { mutableStateOf<DecisionDialogState?>(null) }

    var reloadTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(repository) {
        runCatching { repository.loadPlano() }
            .onSuccess { planosDisponiveis = it }
    }

    LaunchedEffect(repository, clientesPage, clientesSearch, clientesStatus, clientesCity, clientesState, clientesPlanoId, reloadTick) {
        clientesLoading = true
        clientesError = null
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
                clientesResult = ClientesLoadResult(emptyList(), total = 0, page = clientesPage, limit = clientesPageSize, isFallbackData = false)
                clientesLoading = false
                clientesError = error.message ?: "Falha ao carregar clientes."
            }
    }

    LaunchedEffect(clientesResult.clientes) {
        clientesContractsLoading = true
        clientesEnriched = runCatching {
            clientesResult.clientes.map { cliente ->
                async {
                    val contrato = runCatching { repository.loadContratoAtual(cliente.id) }.getOrNull()
                    cliente.copy(
                        planoNome = contrato?.plano?.nome ?: contrato?.planoSnapshotNome.orEmpty(),
                        contratoId = contrato?.id.orEmpty(),
                        contratoStatus = contrato?.status?.name.orEmpty(),
                        motoNome = contrato?.moto?.modelo
                            ?: contrato?.moto?.modeloMoto?.nome
                            ?: contrato?.moto?.modeloMoto?.modelo
                            .orEmpty(),
                        motoPlaca = contrato?.moto?.placa.orEmpty(),
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
            analisesResult = AnalisesLoadResult(emptyList(), total = 0, page = analisesPage, limit = analisesPageSize, isFallbackData = false)
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
        Column(verticalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.fillMaxWidth()) {
            Header(
                activeTab = activeTab,
                compact = compact,
                clientesCount = clientesResult.total,
                analisesCount = analisesResult.total,
                onTabChange = { activeTab = it },
                onRefresh = { refreshAll() },
            )

            if (clientesError != null && activeTab == ClientesTab.CLIENTES) {
                ErrorBanner(message = clientesError.orEmpty())
            }

            if (analisesError != null && activeTab == ClientesTab.PEDIDOS) {
                ErrorBanner(message = analisesError.orEmpty())
            }

            when (activeTab) {
                ClientesTab.CLIENTES -> ClientsPane(
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
                        if (clientesPage < totalPages(clientesResult.total, clientesResult.limit)) clientesPage += 1
                    },
                    onView = onOpenClienteDetail,
                )

                ClientesTab.PEDIDOS -> AnalisesPane(
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
                        if (analisesPage < totalPages(analisesResult.total, analisesResult.limit)) analisesPage += 1
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
                            history = repository.loadAnaliseHistory(cliente.userId)
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
    }
}

@Composable
private fun Header(
    activeTab: ClientesTab,
    compact: Boolean,
    clientesCount: Int,
    analisesCount: Int,
    onTabChange: (ClientesTab) -> Unit,
    onRefresh: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Clientes",
                    color = Color.White,
                    fontSize = if (compact) 28.sp else 34.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "$clientesCount clientes e $analisesCount pedidos em fila",
                    color = Color.White.copy(alpha = 0.42f),
                    fontSize = 13.sp,
                )
            }

            OutlinedButton(
                onClick = onRefresh,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp),
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Atualizar", fontWeight = FontWeight.SemiBold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SegmentedToggleButton(
                text = "Clientes",
                selected = activeTab == ClientesTab.CLIENTES,
                primary = Color(0xFF20E65B),
                compactLayout = compact,
                modifier = Modifier.weight(1f),
                onClick = { onTabChange(ClientesTab.CLIENTES) },
            )
            SegmentedToggleButton(
                text = "Pedidos",
                selected = activeTab == ClientesTab.PEDIDOS,
                primary = Color(0xFF20E65B),
                compactLayout = compact,
                modifier = Modifier.weight(1f),
                onClick = { onTabChange(ClientesTab.PEDIDOS) },
            )
        }
    }
}

@Composable
private fun ClientsPane(
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
private fun FilterPanel(
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
    compact: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SearchField(
                value = search,
                onValueChange = onSearchChange,
                placeholder = "Buscar cliente, CPF, email ou telefone...",
            )

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val narrow = maxWidth < 980.dp
                if (narrow) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            SelectFilterField(
                                value = planoFilter,
                                options = planosDisponiveis,
                                label = "Plano",
                                placeholder = "Todos",
                                modifier = Modifier.weight(1f),
                                onSelect = onPlanoFilterChange,
                            )
                            MiniFilterField(
                                value = motoFilter,
                                onValueChange = onMotoFilterChange,
                                label = "Moto",
                                placeholder = "Modelo ou placa",
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            MiniFilterField(
                                value = city,
                                onValueChange = onCityChange,
                                label = "Cidade",
                                placeholder = "Qualquer",
                                modifier = Modifier.weight(1f),
                            )
                            MiniFilterField(
                                value = state,
                                onValueChange = onStateChange,
                                label = "UF",
                                placeholder = "Qualquer",
                                modifier = Modifier.width(120.dp),
                            )
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        SelectFilterField(
                            value = planoFilter,
                            options = planosDisponiveis,
                            label = "Plano",
                            placeholder = "Todos",
                            modifier = Modifier.weight(1.1f),
                            onSelect = onPlanoFilterChange,
                        )
                        MiniFilterField(
                            value = motoFilter,
                            onValueChange = onMotoFilterChange,
                            label = "Moto",
                            placeholder = "Modelo ou placa",
                            modifier = Modifier.weight(1.3f),
                        )
                        MiniFilterField(
                            value = city,
                            onValueChange = onCityChange,
                            label = "Cidade",
                            placeholder = "Qualquer",
                            modifier = Modifier.weight(1f),
                        )
                        MiniFilterField(
                            value = state,
                            onValueChange = onStateChange,
                            label = "UF",
                            placeholder = "Qualquer",
                            modifier = Modifier.width(130.dp),
                        )
                    }
                }
            }

            FilterStrip(
                statusFilter = statusFilter,
                onStatusFilterChange = onStatusFilterChange,
                compact = compact,
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onClearFilters) {
                    Text("Limpar filtros", color = Color.White.copy(alpha = 0.68f))
                }
            }
        }
    }
}

@Composable
private fun SelectFilterField(
    value: String?,
    options: List<PlanoResponse>,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    onSelect: (String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = options.firstOrNull { it.id == value }?.nome ?: placeholder

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            label = { Text(label, color = Color.White.copy(alpha = 0.45f)) },
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.30f)) },
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.40f),
                )
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.White.copy(alpha = 0.02f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
                focusedBorderColor = Color.White.copy(alpha = 0.10f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                cursorColor = Color(0xFF20E65B),
            ),
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true },
        )
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text("Todos os planos") },
            onClick = {
                onSelect(null)
                expanded = false
            },
        )
        options.forEach { plano ->
            DropdownMenuItem(
                text = { Text(plano.nome) },
                onClick = {
                    onSelect(plano.id)
                    expanded = false
                },
            )
        }
    }
}

@Composable
private fun MiniFilterField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.heightIn(min = 56.dp),
        label = { Text(label, color = Color.White.copy(alpha = 0.45f)) },
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.30f)) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.02f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
            focusedBorderColor = Color.White.copy(alpha = 0.10f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
            cursorColor = Color(0xFF20E65B),
        ),
    )
}

@Composable
private fun AnalisesPane(
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

        SearchField(value = search, onValueChange = onSearchChange, placeholder = "Buscar pedido...")

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
private fun SummaryRow(
    items: List<Pair<String, Int>>,
    compact: Boolean,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val narrow = maxWidth < 980.dp
        if (narrow) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach {
                            SummaryCard(label = it.first, value = it.second.toString(), modifier = Modifier.weight(1f))
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                items.forEach {
                    SummaryCard(label = it.first, value = it.second.toString(), modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, color = Color.White.copy(alpha = 0.42f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .fillMaxWidth(0.45f)
                    .background(Color(0xFF20E65B).copy(alpha = 0.65f), RoundedCornerShape(999.dp)),
            )
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().heightIn(min = 58.dp),
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.35f), fontSize = 14.sp) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        leadingIcon = {
            Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.35f))
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.02f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
            focusedBorderColor = Color.White.copy(alpha = 0.10f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
            cursorColor = Color(0xFF20E65B),
        ),
    )
}

@Composable
private fun FilterStrip(
    statusFilter: ClienteAprovacaoStatus?,
    onStatusFilterChange: (ClienteAprovacaoStatus?) -> Unit,
    compact: Boolean,
) {
    val chips = listOf(
        null to "Todos",
        ClienteAprovacaoStatus.PENDENTE to "Pendentes",
        ClienteAprovacaoStatus.EM_ANALISE to "Em analise",
        ClienteAprovacaoStatus.APROVADO to "Aprovados",
        ClienteAprovacaoStatus.APROVADO_COM_RESSALVA to "Ressalva",
        ClienteAprovacaoStatus.REPROVADO to "Reprovados",
    )

    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Default.FilterAlt, null, tint = Color.White.copy(alpha = 0.35f), modifier = Modifier.size(18.dp))
        chips.forEach { (status, label) ->
            val selected = statusFilter == status
            Surface(
                onClick = { onStatusFilterChange(status) },
                shape = RoundedCornerShape(999.dp),
                color = if (selected) Color(0xFF20E65B).copy(alpha = 0.16f) else Color.White.copy(alpha = 0.03f),
                border = BorderStroke(
                    1.dp,
                    if (selected) Color(0xFF20E65B).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f),
                ),
            ) {
                Text(
                    text = label,
                    color = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.72f),
                    fontSize = if (compact) 11.sp else 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun AnalisesFilterStrip(
    statusFilter: ClienteAnalisePedidoStatus?,
    onStatusFilterChange: (ClienteAnalisePedidoStatus?) -> Unit,
    compact: Boolean,
) {
    val chips = listOf(
        null to "Todos",
        ClienteAnalisePedidoStatus.PENDENTE to "Pendentes",
        ClienteAnalisePedidoStatus.EM_ANALISE to "Em analise",
        ClienteAnalisePedidoStatus.APROVADO to "Aprovados",
        ClienteAnalisePedidoStatus.REPROVADO to "Reprovados",
        ClienteAnalisePedidoStatus.CANCELADO to "Cancelados",
    )

    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Default.FilterAlt, null, tint = Color.White.copy(alpha = 0.35f), modifier = Modifier.size(18.dp))
        chips.forEach { (status, label) ->
            val selected = statusFilter == status
            Surface(
                onClick = { onStatusFilterChange(status) },
                shape = RoundedCornerShape(999.dp),
                color = if (selected) Color(0xFF20E65B).copy(alpha = 0.16f) else Color.White.copy(alpha = 0.03f),
                border = BorderStroke(
                    1.dp,
                    if (selected) Color(0xFF20E65B).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f),
                ),
            ) {
                Text(
                    text = label,
                    color = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.72f),
                    fontSize = if (compact) 11.sp else 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun ClientsTable(
    clientes: List<ClienteListItem>,
    isLoading: Boolean,
    compact: Boolean,
    onView: (ClienteListItem) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TableHeader(
                columns = listOf("Cliente", "Cidade", "Status", "Criado"),
                showActions = true,
                compact = compact,
            )
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

            when {
                isLoading -> LoadingState()
                clientes.isEmpty() -> EmptyState("Nenhum cliente encontrado")
                else -> clientes.forEachIndexed { index, cliente ->
                    ClientRow(cliente = cliente, compact = compact, onView = onView)
                    if (index < clientes.lastIndex) {
                        HorizontalDivider(color = Color.White.copy(alpha = 0.03f), modifier = Modifier.padding(horizontal = 22.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisTable(
    analises: List<AnaliseListItem>,
    isLoading: Boolean,
    compact: Boolean,
    onView: (AnaliseListItem) -> Unit,
    onApprove: (AnaliseListItem) -> Unit,
    onReject: (AnaliseListItem) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TableHeader(
                columns = listOf("Cliente", "Cidade", "Status", "Criado"),
                showActions = true,
                compact = compact,
            )
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

            when {
                isLoading -> LoadingState()
                analises.isEmpty() -> EmptyState("Nenhum pedido encontrado")
                else -> analises.forEachIndexed { index, cliente ->
                    AnalysisRow(
                        cliente = cliente,
                        compact = compact,
                        onView = onView,
                        onApprove = onApprove,
                        onReject = onReject,
                    )
                    if (index < analises.lastIndex) {
                        HorizontalDivider(color = Color.White.copy(alpha = 0.03f), modifier = Modifier.padding(horizontal = 22.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeader(
    columns: List<String>,
    showActions: Boolean,
    compact: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val headerColor = Color.White.copy(alpha = 0.42f)
        Text(columns[0], modifier = Modifier.weight(2.3f), color = headerColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(columns[1], modifier = Modifier.weight(1.1f), color = headerColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(columns[2], modifier = Modifier.weight(1.1f), color = headerColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(columns[3], modifier = Modifier.weight(1.0f), color = headerColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        if (showActions) {
            Spacer(Modifier.width(if (compact) 88.dp else 112.dp))
        }
    }
}

@Composable
private fun ClientRow(
    cliente: ClienteListItem,
    compact: Boolean,
    onView: (ClienteListItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onView(cliente) }
            .padding(horizontal = 22.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(2.3f)) {
            Text(cliente.nome, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text("${cliente.cpf}  -  ${cliente.email}", color = Color.White.copy(alpha = 0.48f), fontSize = 11.sp)
            if (cliente.planoNome.isNotBlank() || cliente.motoNome.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (cliente.planoNome.isNotBlank()) {
                        BadgePill(text = "Plano: ${cliente.planoNome}", accent = Color(0xFF7DD3FC))
                    }
                    if (cliente.motoNome.isNotBlank()) {
                        BadgePill(text = "Moto: ${cliente.motoNome}", accent = Color(0xFF20E65B))
                    }
                }
            }
        }
        Column(modifier = Modifier.weight(1.1f)) {
            Text(cliente.cityLabel, color = Color.White.copy(alpha = 0.80f), fontSize = 13.sp)
            Text(cliente.telefone, color = Color.White.copy(alpha = 0.42f), fontSize = 11.sp)
        }
        Box(modifier = Modifier.weight(1.1f)) {
            BadgePill(text = cliente.status.label(), accent = cliente.status.accent())
        }
        Text(formatDate(cliente.createdAt), modifier = Modifier.weight(1.0f), color = Color.White.copy(alpha = 0.68f), fontSize = 13.sp)
        ActionCell(
            onView = { onView(cliente) },
            onApprove = null,
            onReject = null,
        )
    }
}

@Composable
private fun AnalysisRow(
    cliente: AnaliseListItem,
    compact: Boolean,
    onView: (AnaliseListItem) -> Unit,
    onApprove: (AnaliseListItem) -> Unit,
    onReject: (AnaliseListItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onView(cliente) }
            .padding(horizontal = 22.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(2.3f)) {
            Text(cliente.nome, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text("${cliente.cpf}  -  ${cliente.email}", color = Color.White.copy(alpha = 0.48f), fontSize = 11.sp)
            if (cliente.planoNome.isNotBlank() || cliente.motoNome.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (cliente.planoNome.isNotBlank()) {
                        BadgePill(text = "Plano: ${cliente.planoNome}", accent = Color(0xFF7DD3FC))
                    }
                    if (cliente.motoNome.isNotBlank()) {
                        BadgePill(text = "Moto: ${cliente.motoNome}", accent = Color(0xFF20E65B))
                    }
                }
            }
        }
        Column(modifier = Modifier.weight(1.1f)) {
            Text(cliente.cityLabel, color = Color.White.copy(alpha = 0.80f), fontSize = 13.sp)
            Text(cliente.telefone, color = Color.White.copy(alpha = 0.42f), fontSize = 11.sp)
        }
        Box(modifier = Modifier.weight(1.1f)) {
            BadgePill(text = cliente.status.label(), accent = cliente.status.accent())
        }
        Text(formatDate(cliente.createdAt), modifier = Modifier.weight(1.0f), color = Color.White.copy(alpha = 0.68f), fontSize = 13.sp)
        ActionCell(
            onView = { onView(cliente) },
            onApprove = { onApprove(cliente) },
            onReject = { onReject(cliente) },
        )
    }
}

@Composable
private fun ActionCell(
    onView: () -> Unit,
    onApprove: (() -> Unit)?,
    onReject: (() -> Unit)?,
) {
    Row(
        modifier = Modifier.width(112.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundIconButton(icon = Icons.Default.Visibility, tint = Color.White.copy(alpha = 0.72f), onClick = onView)
        if (onApprove != null) {
            RoundIconButton(icon = Icons.Default.Check, tint = Color(0xFF20E65B), onClick = onApprove)
        }
        if (onReject != null) {
            RoundIconButton(icon = Icons.Default.Close, tint = Color(0xFFFF4A4A), onClick = onReject)
        }
    }
}

@Composable
private fun RoundIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.20f)),
        modifier = Modifier.size(36.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        }
    }
    Spacer(Modifier.width(8.dp))
}

@Composable
private fun PagerBar(
    currentPage: Int,
    totalPages: Int,
    totalItems: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Mostrando página $currentPage de $totalPages · $totalItems registros",
            color = Color.White.copy(alpha = 0.42f),
            fontSize = 12.sp,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onPrev,
                enabled = currentPage > 1,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            ) {
                Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Anterior")
            }
            Button(
                onClick = onNext,
                enabled = currentPage < totalPages,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF20E65B),
                    contentColor = Color.Black,
                ),
            ) {
                Text("Próxima")
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Color(0xFF20E65B))
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(message, color = Color.White.copy(alpha = 0.35f), fontSize = 14.sp)
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.2f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Close, null, tint = Color(0xFFFF4A4A))
            Spacer(Modifier.width(10.dp))
            Text("Erro: $message", color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    rows: List<Pair<String, String>>,
    body: (@Composable () -> Unit)? = null,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            rows.forEach { (label, value) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(label, color = Color.White.copy(alpha = 0.42f), fontSize = 12.sp, modifier = Modifier.weight(0.38f))
                    Text(value, color = Color.White.copy(alpha = 0.82f), fontSize = 12.sp, modifier = Modifier.weight(0.62f), textAlign = TextAlign.End)
                }
            }
            body?.invoke()
        }
    }
}

@Composable
private fun MiniMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, color = Color.White.copy(alpha = 0.42f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(value.ifBlank { "-" }, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .fillMaxWidth(0.45f)
                    .background(Color(0xFF20E65B).copy(alpha = 0.65f), RoundedCornerShape(999.dp)),
            )
        }
    }
}

@Composable
private fun AnalysisDetailDialog(
    cliente: AnaliseListItem,
    history: List<DecisaoAprovacaoClienteResponse>,
    isLoadingHistory: Boolean,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onLoadHistory: (String) -> Unit,
) {
    LaunchedEffect(cliente.id) {
        onLoadHistory(cliente.userId)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF060907),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cliente.nome, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BadgePill(text = cliente.status.label(), accent = cliente.status.accent())
                            Spacer(Modifier.width(10.dp))
                            Text("ID ${cliente.id.take(8)}", color = Color.White.copy(alpha = 0.34f), fontSize = 12.sp)
                        }
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Fechar", color = Color.White.copy(alpha = 0.70f))
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                Row(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Column(modifier = Modifier.weight(1.05f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetailCard(
                            title = "Dados principais",
                            rows = listOf(
                                "CPF" to cliente.cpf.ifBlank { "-" },
                                "Telefone" to cliente.telefone.ifBlank { "-" },
                                "Email" to cliente.email.ifBlank { "-" },
                                "Cidade" to cliente.cityLabel,
                                "Plano" to cliente.planoNome.ifBlank { "-" },
                                "Moto" to cliente.motoNome.ifBlank { "-" },
                            ),
                        )
                        DetailCard(
                            title = "Documentos",
                            rows = listOf(
                                "CNH" to listOf(cliente.cnh, cliente.cnhCategoria).filter { it.isNotBlank() }.joinToString(" / ").ifBlank { "-" },
                                "Endereco" to cliente.endereco.ifBlank { "-" },
                                "Endereco parente" to cliente.enderecoParente.ifBlank { "-" },
                                "CEP" to cliente.cep.ifBlank { "-" },
                            ),
                        )
                        DetailCard(
                            title = "Observacoes",
                            rows = emptyList(),
                            body = {
                                Text(
                                    text = cliente.observacoes.ifBlank { "Sem observacoes registradas." },
                                    color = Color.White.copy(alpha = 0.72f),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                )
                            },
                        )
                    }

                    Column(modifier = Modifier.weight(0.95f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetailCard(
                            title = "Historico",
                            rows = emptyList(),
                            body = {
                                if (isLoadingHistory) {
                                    Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = Color(0xFF20E65B))
                                    }
                                } else if (history.isEmpty()) {
                                    Text("Nenhum historico encontrado.", color = Color.White.copy(alpha = 0.42f), fontSize = 13.sp)
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        history.take(6).forEach { item ->
                                            HistoryRow(item)
                                        }
                                    }
                                }
                            },
                        )

                        DetailCard(
                            title = "Acoes rapidas",
                            rows = emptyList(),
                            body = {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Button(
                                        onClick = onApprove,
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF20E65B),
                                            contentColor = Color.Black,
                                        ),
                                    ) {
                                        Text("Aceitar", fontWeight = FontWeight.SemiBold)
                                    }
                                    OutlinedButton(
                                        onClick = onReject,
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.28f)),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF4A4A)),
                                    ) {
                                        Text("Negar", fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisDetailRichDialog(
    api: BillioneMotosApi,
    cliente: AnaliseListItem,
    history: List<DecisaoAprovacaoClienteResponse>,
    isLoadingHistory: Boolean,
    onDismiss: () -> Unit,
    onDecision: (ClienteAprovacaoStatus, String, String?) -> Unit,
    onLoadHistory: (String) -> Unit,
) {
    LaunchedEffect(cliente.id) {
        onLoadHistory(cliente.userId)
    }

    var selectedStatus by remember(cliente.id) {
        mutableStateOf(
            when (cliente.status) {
                ClienteAnalisePedidoStatus.PENDENTE -> ClienteAprovacaoStatus.PENDENTE
                ClienteAnalisePedidoStatus.EM_ANALISE -> ClienteAprovacaoStatus.EM_ANALISE
                ClienteAnalisePedidoStatus.APROVADO -> ClienteAprovacaoStatus.APROVADO
                ClienteAnalisePedidoStatus.REPROVADO -> ClienteAprovacaoStatus.REPROVADO
                ClienteAnalisePedidoStatus.CANCELADO -> ClienteAprovacaoStatus.PENDENTE
            },
        )
    }
    var justificativa by remember(cliente.id) { mutableStateOf(defaultJustificativa(selectedStatus)) }
    var motoId by remember(cliente.id) { mutableStateOf(cliente.motoId) }
    var activeTab by remember(cliente.id) { mutableStateOf(ClientDetailTab.RESUMO) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF060907),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val narrow = maxWidth < 1240.dp
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().widthIn(max = 1600.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Surface(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(999.dp),
                                color = Color.White.copy(alpha = 0.04f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Icon(Icons.Default.ArrowBack, null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
                                    Text("Pedidos", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .background(cliente.status.accent().copy(alpha = 0.18f), CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(Icons.Default.Groups, null, tint = cliente.status.accent(), modifier = Modifier.size(22.dp))
                                    }
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = cliente.nome.ifBlank { "Pedido de cliente" },
                                            color = Color.White,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            BadgePill(text = cliente.status.label(), accent = cliente.status.accent())
                                            Text("ID ${cliente.id.take(8)}", color = Color.White.copy(alpha = 0.34f), fontSize = 12.sp)
                                            Text("Criado ${formatDate(cliente.createdAt)}", color = Color.White.copy(alpha = 0.34f), fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            TextButton(onClick = onDismiss) {
                                Text("Fechar", color = Color.White.copy(alpha = 0.70f))
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf(
                                "CPF" to (cliente.cpf.ifBlank { "-" }),
                                "Telefone" to (cliente.telefone.ifBlank { "-" }),
                                "Plano" to (cliente.planoNome.ifBlank { "-" }),
                                "Cidade" to cliente.cityLabel,
                            ).forEach { (label, value) ->
                                MiniMetricCard(title = label, value = value, modifier = Modifier.weight(1f))
                            }
                        }

                        TabStrip(
                            tabs = listOf(
                                ClientDetailTab.RESUMO,
                                ClientDetailTab.DOCUMENTOS,
                                ClientDetailTab.ACOES,
                                ClientDetailTab.HISTORICO,
                            ),
                            activeTab = activeTab,
                            onChange = { activeTab = it },
                        )

                        when (activeTab) {
                            ClientDetailTab.RESUMO -> {
                                RichClientMainPanel(api = api, cliente = cliente)
                            }

                            ClientDetailTab.DOCUMENTOS -> {
                                DocumentsOnlyPanel(api = api, cliente = cliente)
                            }

                            ClientDetailTab.ACOES -> {
                                RichClientSidePanel(
                                    cliente = cliente,
                                    history = history,
                                    isLoadingHistory = isLoadingHistory,
                                    selectedStatus = selectedStatus,
                                    justificativa = justificativa,
                                    motoId = motoId,
                                    onStatusChange = { selectedStatus = it },
                                    onJustificativaChange = { justificativa = it },
                                    onMotoIdChange = { motoId = it },
                                    onSave = {
                                        onDecision(
                                            selectedStatus,
                                            justificativa.ifBlank { defaultJustificativa(selectedStatus) },
                                            motoId.trim().ifBlank { null },
                                        )
                                    },
                                )
                            }

                            ClientDetailTab.HISTORICO -> {
                                DetailCard(
                                    title = "Histórico",
                                    rows = emptyList(),
                                    body = {
                                        if (isLoadingHistory) {
                                            Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                                                CircularProgressIndicator(color = Color(0xFF20E65B))
                                            }
                                        } else if (history.isEmpty()) {
                                            Text("Nenhum historico encontrado.", color = Color.White.copy(alpha = 0.42f), fontSize = 13.sp)
                                        } else {
                                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                                history.take(6).forEach { item ->
                                                    HistoryRow(item)
                                                }
                                            }
                                        }
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

@Composable
private fun RichClientMainPanel(
    api: BillioneMotosApi,
    cliente: AnaliseListItem,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailCard(
            title = "Dados principais",
            rows = listOf(
                "CPF" to cliente.cpf.ifBlank { "-" },
                "Telefone" to cliente.telefone.ifBlank { "-" },
                "Email" to cliente.email.ifBlank { "-" },
                "Cidade" to cliente.cityLabel,
                "Plano" to cliente.planoNome.ifBlank { "-" },
                "Moto" to cliente.motoNome.ifBlank { "-" },
            ),
        )

        DetailCard(
            title = "Documentos",
            rows = emptyList(),
            body = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DocumentRow(
                        title = "CNH",
                        subtitle = listOf(cliente.cnh, cliente.cnhCategoria).filter { it.isNotBlank() }.joinToString(" / ").ifBlank { "Documento pendente" },
                        value = cliente.cnhUrl.ifBlank { "Sem arquivo" },
                    )
                    DocumentRow(
                        title = "Identidade",
                        subtitle = "Documento de identidade",
                        value = cliente.identidadeUrl.ifBlank { "Sem arquivo" },
                    )
                    DocumentRow(
                        title = "Comprovante",
                        subtitle = "Residência",
                        value = cliente.comprovanteResidenciaUrl.ifBlank { "Sem arquivo" },
                    )
                }
            },
        )

        DetailCard(
            title = "Mini previews",
            rows = emptyList(),
            body = {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val compact = maxWidth < 980.dp
                    val docs = listOf(
                        DocumentPreviewData("CNH", cliente.cnhUrl.ifBlank { "Sem arquivo" }, cliente.cnhUrl.takeIf { it.isNotBlank() }),
                        DocumentPreviewData("Identidade", cliente.identidadeUrl.ifBlank { "Sem arquivo" }, cliente.identidadeUrl.takeIf { it.isNotBlank() }),
                        DocumentPreviewData("Comprovante", cliente.comprovanteResidenciaUrl.ifBlank { "Sem arquivo" }, cliente.comprovanteResidenciaUrl.takeIf { it.isNotBlank() }),
                    )
                    if (compact) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            docs.forEach { doc -> DocumentPreviewCard(data = doc, api = api, modifier = Modifier.fillMaxWidth()) }
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            docs.forEach { doc -> DocumentPreviewCard(data = doc, api = api, modifier = Modifier.weight(1f)) }
                        }
                    }
                }
            },
        )

        DetailCard(
            title = "Observacoes",
            rows = emptyList(),
            body = {
                Text(
                    text = cliente.observacoes.ifBlank { "Sem observacoes registradas." },
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            },
        )

        DetailCard(
            title = "Contrato e origem",
            rows = listOf(
                "Usuario" to (cliente.userId.ifBlank { "-" }),
                "Pedido" to cliente.status.label(),
                "Plano ID" to (cliente.planoId.ifBlank { "-" }),
                "Moto solicitada" to (cliente.motoNome.ifBlank { "-" }),
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
private fun DocumentsOnlyPanel(
    api: BillioneMotosApi,
    cliente: AnaliseListItem,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailCard(
            title = "Arquivos enviados",
            rows = emptyList(),
            body = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DocumentRow("CNH", listOf(cliente.cnh, cliente.cnhCategoria).filter { it.isNotBlank() }.joinToString(" / ").ifBlank { "Documento pendente" }, cliente.cnhUrl.ifBlank { "Sem arquivo" })
                    DocumentRow("Identidade", "Documento de identidade", cliente.identidadeUrl.ifBlank { "Sem arquivo" })
                    DocumentRow("Comprovante", "Residência", cliente.comprovanteResidenciaUrl.ifBlank { "Sem arquivo" })
                }
            },
        )

        DetailCard(
            title = "Mini previews",
            rows = emptyList(),
            body = {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val compact = maxWidth < 980.dp
                    val docs = listOf(
                        DocumentPreviewData("CNH", cliente.cnhUrl.ifBlank { "Sem arquivo" }, cliente.cnhUrl.takeIf { it.isNotBlank() }),
                        DocumentPreviewData("Identidade", cliente.identidadeUrl.ifBlank { "Sem arquivo" }, cliente.identidadeUrl.takeIf { it.isNotBlank() }),
                        DocumentPreviewData("Comprovante", cliente.comprovanteResidenciaUrl.ifBlank { "Sem arquivo" }, cliente.comprovanteResidenciaUrl.takeIf { it.isNotBlank() }),
                    )
                    if (compact) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            docs.forEach { doc -> DocumentPreviewCard(data = doc, api = api, modifier = Modifier.fillMaxWidth()) }
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            docs.forEach { doc -> DocumentPreviewCard(data = doc, api = api, modifier = Modifier.weight(1f)) }
                        }
                    }
                }
            },
        )
        DetailCard(
            title = "Dados do pedido",
            rows = listOf(
                "Plano" to cliente.planoNome.ifBlank { "-" },
                "Cidade" to cliente.cityLabel,
                "Estado" to cliente.estado.ifBlank { "-" },
                "CEP" to cliente.cep.ifBlank { "-" },
                "Observações" to (cliente.observacoes.ifBlank { "-" }),
            ),
        )
    }
}

private data class DocumentPreviewData(
    val title: String,
    val subtitle: String,
    val url: String?,
)

@Composable
private fun DocumentPreviewCard(
    data: DocumentPreviewData,
    api: BillioneMotosApi,
    modifier: Modifier = Modifier,
) {
    var bitmap by remember(data.url) { mutableStateOf<ImageBitmap?>(null) }
    var loading by remember(data.url) { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(data.url) {
        bitmap = null
        loading = false
        val resolved = data.url?.takeIf { it.isNotBlank() } ?: return@LaunchedEffect
        loading = true
        runCatching {
            val fullUrl = if (resolved.startsWith("http", ignoreCase = true)) {
                resolved
            } else {
                api.config.baseUrl.removeSuffix("/") + "/" + resolved.removePrefix("/")
            }
            val bytes = api.fetchRawBytes(fullUrl)
            Image.makeFromEncoded(bytes).toComposeImageBitmap()
        }.onSuccess {
            bitmap = it
        }.onFailure {
            bitmap = null
        }
        loading = false
    }

    if (expanded && bitmap != null) {
        Dialog(
            onDismissRequest = { expanded = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.82f))
                    .clickable { expanded = false },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    bitmap = bitmap!!,
                    contentDescription = data.title,
                    modifier = Modifier.fillMaxHeight(0.86f).clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }

    Card(
        modifier = modifier.clickable(enabled = bitmap != null) { expanded = true },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    loading -> CircularProgressIndicator(color = Color(0xFF20E65B), modifier = Modifier.size(28.dp))
                    bitmap != null -> Image(bitmap = bitmap!!, contentDescription = data.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Visibility, null, tint = Color.White.copy(alpha = 0.22f), modifier = Modifier.size(32.dp))
                        Text("Sem visualização", color = Color.White.copy(alpha = 0.34f), fontSize = 12.sp)
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(data.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(data.subtitle, color = Color.White.copy(alpha = 0.44f), fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BadgePill(
                    text = if (bitmap != null) "Visualizado" else "Pendente",
                    accent = if (bitmap != null) Color(0xFF20E65B) else Color(0xFFFFB300),
                )
                Text(
                    text = if (bitmap != null) "Clique para ampliar" else "Arquivo indisponível",
                    color = Color.White.copy(alpha = 0.34f),
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun RichClientSidePanel(
    cliente: AnaliseListItem,
    history: List<DecisaoAprovacaoClienteResponse>,
    isLoadingHistory: Boolean,
    selectedStatus: ClienteAprovacaoStatus,
    justificativa: String,
    motoId: String,
    onStatusChange: (ClienteAprovacaoStatus) -> Unit,
    onJustificativaChange: (String) -> Unit,
    onMotoIdChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailCard(
            title = "Status e ações",
            rows = emptyList(),
            body = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        "Defina o status do pedido e salve a decisão nesta mesma tela.",
                        color = Color.White.copy(alpha = 0.48f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    )

                    val statusOptions = listOf(
                        ClienteAprovacaoStatus.PENDENTE,
                        ClienteAprovacaoStatus.EM_ANALISE,
                        ClienteAprovacaoStatus.APROVADO,
                        ClienteAprovacaoStatus.APROVADO_COM_RESSALVA,
                        ClienteAprovacaoStatus.REPROVADO,
                    )

                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val compact = maxWidth < 520.dp
                        if (compact) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                statusOptions.chunked(2).forEach { row ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        row.forEach { status ->
                                            StatusChip(status = status, selected = selectedStatus == status, onClick = { onStatusChange(status) }, modifier = Modifier.weight(1f))
                                        }
                                        if (row.size == 1) Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    statusOptions.take(3).forEach { status ->
                                        StatusChip(status = status, selected = selectedStatus == status, onClick = { onStatusChange(status) }, modifier = Modifier.weight(1f))
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    statusOptions.drop(3).forEach { status ->
                                        StatusChip(status = status, selected = selectedStatus == status, onClick = { onStatusChange(status) }, modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = justificativa,
                        onValueChange = onJustificativaChange,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
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

                    OutlinedTextField(
                        value = motoId,
                        onValueChange = onMotoIdChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Moto vinculada") },
                        placeholder = { Text("Opcional") },
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

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = onSave,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = selectedStatus.accent(),
                                contentColor = Color.Black,
                            ),
                            modifier = Modifier.weight(1f).height(44.dp),
                        ) {
                            Text("Salvar status", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
        )

        DetailCard(
            title = "Atalhos rápidos",
            rows = emptyList(),
            body = {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            onStatusChange(ClienteAprovacaoStatus.APROVADO)
                            onJustificativaChange(defaultJustificativa(ClienteAprovacaoStatus.APROVADO))
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20E65B), contentColor = Color.Black),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Aprovar")
                    }
                    OutlinedButton(
                        onClick = {
                            onStatusChange(ClienteAprovacaoStatus.REPROVADO)
                            onJustificativaChange(defaultJustificativa(ClienteAprovacaoStatus.REPROVADO))
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.28f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF4A4A)),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Reprovar")
                    }
                }
            },
        )

        DetailCard(
            title = "Histórico",
            rows = emptyList(),
            body = {
                if (isLoadingHistory) {
                    Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF20E65B))
                    }
                } else if (history.isEmpty()) {
                    Text("Nenhum historico encontrado.", color = Color.White.copy(alpha = 0.42f), fontSize = 13.sp)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        history.take(6).forEach { item ->
                            HistoryRow(item)
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun StatusChip(
    status: ClienteAprovacaoStatus,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = if (selected) status.accent().copy(alpha = 0.18f) else Color.White.copy(alpha = 0.03f),
        border = BorderStroke(1.dp, if (selected) status.accent().copy(alpha = 0.32f) else Color.White.copy(alpha = 0.06f)),
        modifier = modifier,
    ) {
        Text(
            text = status.label(),
            color = if (selected) status.accent() else Color.White.copy(alpha = 0.72f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DocumentRow(
    title: String,
    subtitle: String,
    value: String,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFF20E65B).copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Visibility, null, tint = Color(0xFF20E65B), modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color.White.copy(alpha = 0.42f), fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BadgePill(
                    text = if (value == "Sem arquivo") "Pendente" else "Enviado",
                    accent = if (value == "Sem arquivo") Color(0xFFFFB300) else Color(0xFF20E65B),
                )
                Text(value, color = Color.White.copy(alpha = 0.40f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun TabStrip(
    tabs: List<ClientDetailTab>,
    activeTab: ClientDetailTab,
    onChange: (ClientDetailTab) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF121712), RoundedCornerShape(16.dp)).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tabs.forEach { tab ->
            val selected = tab == activeTab
            Surface(
                onClick = { onChange(tab) },
                shape = RoundedCornerShape(12.dp),
                color = if (selected) Color(0xFF0D100E) else Color.Transparent,
                border = BorderStroke(1.dp, if (selected) Color.White.copy(alpha = 0.08f) else Color.Transparent),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = tab.label,
                    color = if (selected) Color.White else Color.White.copy(alpha = 0.60f),
                    fontSize = 12.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun HistoryRow(item: DecisaoAprovacaoClienteResponse) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        border = BorderStroke(1.dp, item.decisao.accent().copy(alpha = 0.16f)),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                BadgePill(text = item.decisao.label(), accent = item.decisao.accent())
                Text(formatDate(item.createdAt), color = Color.White.copy(alpha = 0.36f), fontSize = 11.sp)
            }
            Text(item.justificativa, color = Color.White.copy(alpha = 0.72f), fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun DecisionDialog(
    state: DecisionDialogState,
    onDismiss: () -> Unit,
    onStateChange: (DecisionDialogState) -> Unit,
    onConfirm: (DecisionDialogState) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.64f).fillMaxHeight(0.72f),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF060907),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (state.decisao == ClienteAprovacaoStatus.REPROVADO) "Negar pedido" else "Aceitar pedido",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(state.cliente.nome, color = Color.White.copy(alpha = 0.45f), fontSize = 13.sp)
                }

                DetailCard(
                    title = "Resumo",
                    rows = listOf(
                        "CPF" to state.cliente.cpf,
                        "Cidade" to state.cliente.cityLabel,
                        "Status atual" to state.cliente.status.label(),
                    ),
                )

                OutlinedTextField(
                    value = state.justificativa,
                    onValueChange = { onStateChange(state.copy(justificativa = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
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

                if (state.decisao != ClienteAprovacaoStatus.REPROVADO) {
                    OutlinedTextField(
                        value = state.motoId,
                        onValueChange = { onStateChange(state.copy(motoId = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Moto vinculada") },
                        placeholder = { Text("Opcional, mas recomendado") },
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
                }

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = Color.White.copy(alpha = 0.65f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        ) {
                            Text("Voltar")
                        }
                        Button(
                            onClick = { onConfirm(state) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.decisao == ClienteAprovacaoStatus.REPROVADO) Color(0xFFFF4A4A) else Color(0xFF20E65B),
                                contentColor = Color.Black,
                            ),
                        ) {
                            Text(
                                text = if (state.decisao == ClienteAprovacaoStatus.REPROVADO) "Confirmar recusa" else "Confirmar aprovação",
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class ClientesTab { CLIENTES, PEDIDOS }

private enum class ClientDetailTab(val label: String) {
    RESUMO("Resumo"),
    DOCUMENTOS("Documentos"),
    ACOES("Ações"),
    HISTORICO("Histórico"),
}

private data class SummaryCounters(
    val total: Int,
    val pendentes: Int,
    val emAnalise: Int,
    val aprovados: Int,
    val ressalvas: Int,
    val reprovados: Int,
) {
    companion object {
        fun from(items: List<ClienteListItem>): SummaryCounters {
            return SummaryCounters(
                total = items.size,
                pendentes = items.count { it.status == ClienteAprovacaoStatus.PENDENTE },
                emAnalise = items.count { it.status == ClienteAprovacaoStatus.EM_ANALISE },
                aprovados = items.count { it.status == ClienteAprovacaoStatus.APROVADO },
                ressalvas = items.count { it.status == ClienteAprovacaoStatus.APROVADO_COM_RESSALVA },
                reprovados = items.count { it.status == ClienteAprovacaoStatus.REPROVADO },
            )
        }

        fun fromAnalises(items: List<AnaliseListItem>): SummaryCounters {
            return SummaryCounters(
                total = items.size,
                pendentes = items.count { it.status == ClienteAnalisePedidoStatus.PENDENTE },
                emAnalise = items.count { it.status == ClienteAnalisePedidoStatus.EM_ANALISE },
                aprovados = items.count { it.status == ClienteAnalisePedidoStatus.APROVADO },
                ressalvas = 0,
                reprovados = items.count { it.status == ClienteAnalisePedidoStatus.REPROVADO },
            )
        }
    }
}

private data class DecisionDialogState(
    val cliente: AnaliseListItem,
    val decisao: ClienteAprovacaoStatus,
    val justificativa: String,
    val motoId: String,
)

private fun defaultJustificativa(decisao: ClienteAprovacaoStatus): String = when (decisao) {
    ClienteAprovacaoStatus.APROVADO -> "Documentacao validada."
    ClienteAprovacaoStatus.APROVADO_COM_RESSALVA -> "Aprovado com ressalvas apos validacao parcial."
    ClienteAprovacaoStatus.REPROVADO -> "Documentacao inconsistente ou incompleta."
    ClienteAprovacaoStatus.EM_ANALISE -> "Registro atualizado para analise interna."
    ClienteAprovacaoStatus.PENDENTE -> "Mantido como pendente."
}

private fun formatDate(value: String?): String {
    if (value.isNullOrBlank()) return "-"
    return runCatching {
        val instant = Instant.parse(value)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault())
        formatter.format(instant)
    }.getOrElse { value }
}
