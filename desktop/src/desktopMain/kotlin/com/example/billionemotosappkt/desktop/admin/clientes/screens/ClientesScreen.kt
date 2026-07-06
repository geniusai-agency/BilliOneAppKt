package com.example.billionemotosappkt.desktop.admin.clientes.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.clientes.components.ClienteEditDialog
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClientesFilters
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClientesSortMode
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClientesSummary
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClientesUiState
import com.example.billionemotosappkt.desktop.admin.clientes.model.accent
import com.example.billionemotosappkt.desktop.admin.clientes.model.label
import com.example.billionemotosappkt.desktop.admin.clientes.model.toUpdateRequest
import com.example.billionemotosappkt.desktop.admin.clientes.repository.ClientesRepository
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.PlanoResponse

@Composable
fun ClientesSection(
    api: BillioneMotosApi,
    compact: Boolean,
) {
    var selectedCliente by remember { mutableStateOf<ClienteListItem?>(null) }
    var editOn by remember { mutableStateOf(false) }

    val repository = remember(api) { ClientesRepository(api) }
    var uiState by remember { mutableStateOf(ClientesUiState()) }
    var filters by remember { mutableStateOf(ClientesFilters()) }

    var deleteOn by remember { mutableStateOf(false) }
    var viewOn by remember { mutableStateOf(false) }
    var plans by remember { mutableStateOf(emptyList<PlanoResponse>()) }

    LaunchedEffect(repository) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        runCatching { repository.loadClientes() }
            .onSuccess { result ->
                uiState = ClientesUiState(
                    isLoading = false,
                    isFallbackData = result.isFallbackData,
                    clientes = result.clientes,
                )

                println(uiState.clientes.find {it.nome == "Ana Paula Santos Ribeiro"})
            }
            .onFailure { error ->
                uiState = ClientesUiState(
                    isLoading = false,
                    isFallbackData = false,
                    errorMessage = error.message ?: "Falha ao carregar clientes.",
                    clientes = emptyList(),
                )
            }

        runCatching { repository.loadPlano() }
            .onSuccess { result ->
                plans = result
                println(plans)
            }
            .onFailure { error ->
                println(error)
            }

    }

    val filteredClientes by remember(uiState.clientes, filters) {
        derivedStateOf {
            applyFilters(uiState.clientes, filters)
        }
    }

    val summary by remember(filteredClientes) {
        derivedStateOf {
            ClientesSummary(
                total = filteredClientes.size,
                approved = filteredClientes.count { it.status == ClienteAprovacaoStatus.APROVADO },
                analysis = filteredClientes.count { it.status == ClienteAprovacaoStatus.EM_ANALISE },
                pending = filteredClientes.count { it.status == ClienteAprovacaoStatus.PENDENTE },
                rejected = filteredClientes.count { it.status == ClienteAprovacaoStatus.REPROVADO },
            )
        }
    }

    val cityOptions = remember(uiState.clientes) {
        uiState.clientes
            .map { it.cityLabel }
            .distinct()
            .sorted()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Box {
            if (editOn && selectedCliente != null) {
                ClienteEditDialog(
                    api = api,
                    cliente = (selectedCliente as ClienteListItem).toUpdateRequest(),
                    planosDisponiveis = plans,
                    onDismiss = {
                        editOn = false        // Fecha visualmente
                        selectedCliente = null // Limpa o cliente selecionado
                    },
                    onSave = { body ->
                        val clientId = selectedCliente?.id ?: return@ClienteEditDialog
                        val updated: ClienteListItem = repository.updateCliente(id = clientId, body = body)
                        uiState = uiState.copy(
                            clientes = uiState.clientes.map { clt ->
                                if (clt.id == updated.id) updated else clt
                            }
                        )
                    }
                )
            }
            ClientsHeader(
                compact = compact,
                total = uiState.clientes.size,
                query = filters.query,
                onQueryChange = { filters = filters.copy(query = it) },
                onReset = {
                    filters = ClientesFilters()
                },
            )

            if (uiState.errorMessage != null) {
                StatusBanner(
                    title = "A API não respondeu",
                    message = uiState.errorMessage ?: "",
                    accent = Color(0xFFFF4A4A),
                )
            } else if (uiState.isFallbackData) {
                StatusBanner(
                    title = "Dados de demonstração",
                    message = "A tela está carregando com fallback local até a API responder.",
                    accent = Color(0xFFFFB300),
                )
            }

            ClientMetricsRow(summary = summary, compact = compact)

            FilterStrip(
                filters = filters,
                cityOptions = cityOptions,
                onStatusSelected = { status ->
                    filters = filters.copy(status = if (filters.status == status) null else status)
                },
                onCitySelected = { city ->
                    filters = filters.copy(city = if (filters.city == city) null else city)
                },
                onSortSelected = { sort -> filters = filters.copy(sortMode = sort) },
                onClear = { filters = ClientesFilters(query = filters.query) },
            )

            ClientsListCard(
                clientes = filteredClientes,
                compact = compact,
                onEditOpen = { it, clt ->
                    editOn = it
                    selectedCliente = clt
                },
                onViewOpen = { viewOn = it },
                onDeleteOpen = { deleteOn = it },
                emptyMessage = if (uiState.isLoading) "Carregando clientes..." else "Nenhum cliente encontrado com esses filtros.",
            )
        }
    }
}

@Composable
private fun ClientsHeader(
    compact: Boolean,
    total: Int,
    query: String,
    onQueryChange: (String) -> Unit,
    onReset: () -> Unit,
) {
    if (compact) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Clientes",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "$total cadastrados",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 14.sp
                )
            }
            SearchField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(text = "Novo locatário", icon = Icons.Default.Groups, filled = true)
                ActionButton(text = "Cadastro rápido", icon = Icons.Default.Add, filled = false)
            }
            TextButtonLike(text = "Limpar filtros", onClick = onReset)
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Clientes",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "$total cadastrados",
                    color = Color.White.copy(alpha = 0.58f),
                    fontSize = 14.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.widthIn(max = 760.dp),
            ) {
                SearchField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.widthIn(min = 240.dp, max = 320.dp),
                )
                ActionButton(text = "Novo locatário", icon = Icons.Default.Groups, filled = true)
                ActionButton(text = "Cadastro rápido", icon = Icons.Default.Add, filled = false)
            }
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        placeholder = {
            Text(text = "Buscar...")
        },
    )
}

@Composable
private fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    filled: Boolean,
) {
    if (filled) {
        Button(
            onClick = { },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF20E65B),
                contentColor = Color.Black
            ),
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, fontWeight = FontWeight.SemiBold)
        }
    } else {
        OutlinedButton(
            onClick = { },
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TextButtonLike(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.72f),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@Composable
private fun ClientMetricsRow(
    summary: ClientesSummary,
    compact: Boolean,
) {
    val cards = listOf(
        Triple("Total", summary.total.toString(), Color(0xFF20E65B)),
        Triple("Aprovados", summary.approved.toString(), Color(0xFF20E65B)),
        Triple("Em análise", summary.analysis.toString(), Color(0xFFFFB300)),
        Triple("Pendentes", summary.pending.toString(), Color(0xFFFF8A00)),
    )

    if (compact) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            cards.chunked(2).forEach { chunk ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    chunk.forEach { (label, value, tint) ->
                        MetricCard(
                            label = label,
                            value = value,
                            tint = tint,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            cards.forEach { (label, value, tint) ->
                MetricCard(
                    label = label,
                    value = value,
                    tint = tint,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1411)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(tint.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = null,
                        tint = tint
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(tint),
                )
            }
            Text(
                text = label.uppercase(),
                color = Color.White.copy(alpha = 0.52f),
                fontSize = 11.sp
            )
            Text(text = value, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun FilterStrip(
    filters: ClientesFilters,
    cityOptions: List<String>,
    onStatusSelected: (ClienteAprovacaoStatus) -> Unit,
    onCitySelected: (String) -> Unit,
    onSortSelected: (ClientesSortMode) -> Unit,
    onClear: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B100D)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = null,
                    tint = Color(0xFF20E65B)
                )
                Text(text = "Filtros", color = Color.White, fontWeight = FontWeight.Bold)
            }

            ScrollChipRow {
                statusFilterChips().forEach { status ->
                    FilterChip(
                        text = status.label(),
                        selected = filters.status == status,
                        color = status.accent(),
                        onClick = { onStatusSelected(status) },
                    )
                }
                cityOptions.take(8).forEach { city ->
                    FilterChip(
                        text = city,
                        selected = filters.city == city,
                        color = Color(0xFF7DD3FC),
                        onClick = { onCitySelected(city) },
                    )
                }
            }

            ScrollChipRow {
                ClientesSortMode.entries.forEach { mode ->
                    FilterChip(
                        text = mode.label,
                        selected = filters.sortMode == mode,
                        color = Color(0xFF20E65B),
                        onClick = { onSortSelected(mode) },
                    )
                }
                FilterChip(
                    text = "Limpar",
                    selected = false,
                    color = Color.White.copy(alpha = 0.35f),
                    onClick = onClear,
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (selected) color else Color.White.copy(alpha = 0.45f)),
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) color.copy(alpha = 0.12f) else Color(0xFF101613),
            labelColor = if (selected) color else Color.White.copy(alpha = 0.78f),
            leadingIconContentColor = if (selected) color else Color.White.copy(alpha = 0.45f),
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) color else Color.White.copy(alpha = 0.06f),
        ),
    )
}

@Composable
private fun ScrollChipRow(
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        content()
    }
}

@Composable
private fun ClientsListCard(
    clientes: List<ClienteListItem>,
    compact: Boolean,
    onEditOpen: (Boolean, ClienteListItem) -> Unit,
    onDeleteOpen: (Boolean) -> Unit = {},
    onViewOpen: (Boolean) -> Unit = {},

    emptyMessage: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
    ) {
        if (compact) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (clientes.isEmpty()) {
                    EmptyState(emptyMessage)
                } else {
                    clientes.forEach { cliente ->
                        ClientCompactCard(cliente = cliente)
                    }
                }
            }
        } else {
            Column {
                ClientTableHeader()
                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                if (clientes.isEmpty()) {
                    EmptyState(emptyMessage)
                } else {
                    clientes.forEachIndexed { index, cliente ->
                        ClientTableRow(
                            cliente = cliente,
                            highlight = index % 2 == 0,
                            onEditOpen = { onEditOpen(it, cliente) },
                            onViewOpen = onViewOpen,
                            onDeleteOpen = onDeleteOpen
                        )
                        if (index != clientes.lastIndex) {
                            HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Nome",
            color = Color.White.copy(alpha = 0.54f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = "CPF",
            color = Color.White.copy(alpha = 0.54f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = "Telefone",
            color = Color.White.copy(alpha = 0.54f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.4f)
        )
        Text(
            text = "Cidade",
            color = Color.White.copy(alpha = 0.54f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.2f)
        )
        Spacer(modifier = Modifier.width(120.dp))
    }
}

@Composable
private fun ClientTableRow(
    cliente: ClienteListItem,
    onEditOpen: (Boolean) -> Unit = {},
    onDeleteOpen: (Boolean) -> Unit = {},
    onViewOpen: (Boolean) -> Unit = {},
    highlight: Boolean,
) {
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (highlight) Color.White.copy(alpha = 0.01f) else Color.Transparent)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(initials = cliente.initials)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = cliente.nome, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(
                    text = cliente.email,
                    color = Color.White.copy(alpha = 0.48f),
                    fontSize = 12.sp
                )
            }
        }
        Text(text = cliente.cpf, color = Color.White, modifier = Modifier.weight(1.3f))
        Text(text = cliente.telefone, color = Color.White, modifier = Modifier.weight(1.4f))
        Text(text = cliente.cityLabel, color = Color.White, modifier = Modifier.weight(1.2f))
        Row(
            modifier = Modifier.width(120.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusPill(status = cliente.status)
            Spacer(modifier = Modifier.width(8.dp))

            // Ícone de Funções com Dropdown
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Ações", tint = Color.White)
                }

                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false },
                    modifier = Modifier.background(Color(0xFF0E1411))
                ) {
                    DropdownMenuItem(
                        text = { Text("Ver detalhes") },
                        leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null) },
                        onClick = {
                            menuOpen = false
                            onViewOpen(true)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = {
                            menuOpen = false
                            onEditOpen(true)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Excluir", color = Color(0xFFFF4A4A)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = Color(0xFFFF4A4A)
                            )
                        },
                        onClick = {
                            menuOpen = false
                            onDeleteOpen(true)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientCompactCard(cliente: ClienteListItem) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101613)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(initials = cliente.initials)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = cliente.nome, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(text = cliente.cpf, color = Color.White.copy(alpha = 0.54f), fontSize = 12.sp)
                Text(
                    text = cliente.cityLabel,
                    color = Color.White.copy(alpha = 0.54f),
                    fontSize = 12.sp
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusPill(status = cliente.status)
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    RowActionIcon(icon = Icons.Default.Visibility)
                    RowActionIcon(icon = Icons.Default.Edit)
                }
            }
        }
    }
}

@Composable
private fun Avatar(initials: String) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Color(0xFF20E65B), Color(0xFF16B44A)))),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = initials, color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
    }
}

@Composable
private fun StatusPill(status: ClienteAprovacaoStatus) {
    val accent = status.accent()
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(accent.copy(alpha = 0.12f))
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(text = status.label(), color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RowActionIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color = Color.White.copy(alpha = 0.82f),
) {
    IconButton(onClick = { }) {
        Icon(imageVector = icon, contentDescription = null, tint = tint)
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.52f),
        )
    }
}

@Composable
private fun StatusBanner(
    title: String,
    message: String,
    accent: Color,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = title, color = accent, fontWeight = FontWeight.Bold)
            Text(text = message, color = Color.White.copy(alpha = 0.72f))
        }
    }
}

private fun applyFilters(
    clientes: List<ClienteListItem>,
    filters: ClientesFilters,
): List<ClienteListItem> {
    val query = filters.query.trim().lowercase()
    return clientes
        .asSequence()
        .filter { cliente ->
            query.isBlank() || listOf(
                cliente.nome,
                cliente.cpf,
                cliente.telefone,
                cliente.email,
                cliente.cidade,
                cliente.estado,
            ).any { it.lowercase().contains(query) }
        }
        .filter { cliente ->
            filters.status == null || cliente.status == filters.status
        }
        .filter { cliente ->
            filters.city == null || cliente.cityLabel == filters.city
        }
        .filter { cliente ->
            filters.state == null || cliente.estado.isBlank() || cliente.estado == filters.state
        }
        .sortedWith(
            when (filters.sortMode) {
                ClientesSortMode.NAME -> compareBy(String.CASE_INSENSITIVE_ORDER) { it.nome }
                ClientesSortMode.CITY -> compareBy(String.CASE_INSENSITIVE_ORDER) { it.cityLabel }
                ClientesSortMode.STATUS -> compareBy<ClienteListItem> { it.status.ordinal }.thenBy { it.nome }
            },
        )
        .toList()
}

private fun statusFilterChips(): List<ClienteAprovacaoStatus> {
    return listOf(
        ClienteAprovacaoStatus.APROVADO,
        ClienteAprovacaoStatus.EM_ANALISE,
        ClienteAprovacaoStatus.PENDENTE,
        ClienteAprovacaoStatus.APROVADO_COM_RESSALVA,
        ClienteAprovacaoStatus.REPROVADO,
    )
}
