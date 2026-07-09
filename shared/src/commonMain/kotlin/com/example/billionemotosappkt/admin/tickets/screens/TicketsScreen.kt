package com.example.billionemotosappkt.desktop.admin.tickets.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.fillMaxSize
import com.example.billionemotosappkt.desktop.admin.components.BadgePill
import com.example.billionemotosappkt.desktop.admin.tickets.model.accent
import com.example.billionemotosappkt.desktop.admin.tickets.model.clienteLabel
import com.example.billionemotosappkt.desktop.admin.tickets.model.contratoLabel
import com.example.billionemotosappkt.desktop.admin.tickets.model.formatTicketDate
import com.example.billionemotosappkt.desktop.admin.tickets.model.label
import com.example.billionemotosappkt.desktop.admin.tickets.model.motoLabel
import com.example.billionemotosappkt.desktop.admin.tickets.repository.TicketsRepository
import com.example.billionemotosappkt.desktop.admin.tickets.screens.parts.EmptyState
import com.example.billionemotosappkt.desktop.admin.tickets.screens.parts.LoadingState
import com.example.billionemotosappkt.desktop.admin.tickets.screens.parts.TicketsErrorBanner
import com.example.billionemotosappkt.desktop.admin.tickets.screens.parts.TicketsFilterPanel
import com.example.billionemotosappkt.desktop.admin.tickets.screens.parts.TicketsHeader
import com.example.billionemotosappkt.desktop.admin.tickets.screens.parts.TicketsPagerBar
import com.example.billionemotosappkt.desktop.admin.tickets.screens.parts.TicketsSummary
import com.example.billionemotosappkt.desktop.admin.tickets.screens.parts.TicketsSummaryRow
import com.example.billionemotosappkt.desktop.admin.tickets.screens.parts.TicketsTable
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.CreateTicketCommentRequest
import com.example.billionemotosappkt.shared.api.CreateTicketRequest
import com.example.billionemotosappkt.shared.api.ListTicketsQuery
import com.example.billionemotosappkt.shared.api.TicketArquivoResponse
import com.example.billionemotosappkt.shared.api.TicketCategoria
import com.example.billionemotosappkt.shared.api.TicketOrigem
import com.example.billionemotosappkt.shared.api.TicketPrioridade
import com.example.billionemotosappkt.shared.api.TicketResponse
import com.example.billionemotosappkt.shared.api.TicketStatus
import com.example.billionemotosappkt.shared.api.UpdateTicketRequest
import com.example.billionemotosappkt.shared.api.UpdateTicketStatusRequest
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import com.example.billionemotosappkt.shared.utils.rememberImagePicker
import com.example.billionemotosappkt.shared.utils.toImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.ceil

private const val PAGE_LIMIT = 20
private val ACCENT = Color(0xFF20E65B)
private val CARD_BG = Color(0xFF0C120E)

@Composable
fun TicketsSection(
    api: BillioneMotosApi,
    apiBaseUrl: String,
    apiAccessToken: String?,
    compact: Boolean,
) {
    val scope = rememberCoroutineScope()
    val repository = remember(api) { TicketsRepository(api) }

    var tickets by remember { mutableStateOf<List<TicketResponse>>(emptyList()) }
    var total by remember { mutableStateOf(0) }
    var page by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var refreshTick by remember { mutableStateOf(0) }

    var search by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf<TicketStatus?>(null) }
    var origemFilter by remember { mutableStateOf<TicketOrigem?>(null) }
    var categoriaFilter by remember { mutableStateOf<TicketCategoria?>(null) }

    var detailId by remember { mutableStateOf<String?>(null) }
    var editing by remember { mutableStateOf<TicketResponse?>(null) }
    var statusTarget by remember { mutableStateOf<TicketResponse?>(null) }
    var showCreate by remember { mutableStateOf(false) }

    LaunchedEffect(page, statusFilter, origemFilter, categoriaFilter, search, refreshTick) {
        delay(250)
        isLoading = true
        error = null
        runCatching {
            repository.loadTickets(
                ListTicketsQuery(
                    page = page,
                    limit = PAGE_LIMIT,
                    q = search.trim().ifBlank { null },
                    status = statusFilter,
                    origem = origemFilter,
                    categoria = categoriaFilter,
                ),
            )
        }.onSuccess {
            tickets = it.tickets
            total = it.total
        }.onFailure {
            error = it.message ?: "Falha ao carregar chamados."
            tickets = emptyList()
            total = 0
        }
        isLoading = false
    }

    val summary = TicketsSummary.from(tickets, total)
    val totalPages = maxOf(1, ceil(total / PAGE_LIMIT.toDouble()).toInt())

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        TicketsHeader(
            compact = compact,
            totalCount = total,
            novosCount = summary.novosCliente,
            onNovos = {
                statusFilter = TicketStatus.ABERTO
                origemFilter = TicketOrigem.CLIENTE
                page = 1
            },
            onRefresh = { refreshTick++ },
            onCreate = { showCreate = true },
        )

        TicketsSummaryRow(summary = summary, compact = compact)

        if (error != null) {
            TicketsErrorBanner(message = error!!)
        }

        TicketsFilterPanel(
            search = search,
            onSearchChange = { search = it; page = 1 },
            statusFilter = statusFilter,
            onStatusChange = { statusFilter = it; page = 1 },
            origemFilter = origemFilter,
            onOrigemChange = { origemFilter = it; page = 1 },
            categoriaFilter = categoriaFilter,
            onCategoriaChange = { categoriaFilter = it; page = 1 },
            onClearFilters = {
                search = ""
                statusFilter = null
                origemFilter = null
                categoriaFilter = null
                page = 1
            },
            compact = compact,
        )

        TicketsTable(
            tickets = tickets,
            isLoading = isLoading,
            compact = compact,
            onView = { detailId = it.id },
            onEdit = { editing = it },
            onStatus = { statusTarget = it },
        )

        if (total > PAGE_LIMIT) {
            TicketsPagerBar(
                currentPage = page,
                totalPages = totalPages,
                totalItems = total,
                onPrev = { if (page > 1) page-- },
                onNext = { if (page < totalPages) page++ },
            )
        }
    }

    detailId?.let { id ->
        TicketDetailDialog(
            api = api,
            apiBaseUrl = apiBaseUrl,
            ticketId = id,
            onClose = { detailId = null },
            onChanged = { refreshTick++ },
            onEdit = { editing = it; detailId = null },
            onStatus = { statusTarget = it; detailId = null },
        )
    }

    editing?.let { ticket ->
        TicketEditDialog(
            ticket = ticket,
            onClose = { editing = null },
            onSave = { req ->
                scope.launch {
                    runCatching { repository.updateTicket(ticket.id, req) }
                        .onSuccess { editing = null; refreshTick++ }
                }
            },
        )
    }

    statusTarget?.let { ticket ->
        TicketStatusDialog(
            ticket = ticket,
            onClose = { statusTarget = null },
            onConfirm = { req ->
                scope.launch {
                    runCatching { repository.updateStatus(ticket.id, req) }
                        .onSuccess { statusTarget = null; refreshTick++ }
                }
            },
        )
    }

    if (showCreate) {
        TicketCreateDialog(
            onClose = { showCreate = false },
            onCreate = { req, files ->
                scope.launch {
                    runCatching {
                        val created = repository.createTicket(req)
                        files.forEach { file ->
                            runCatching {
                                repository.addPhoto(created.id, file)
                            }.onFailure { println("Photo upload failed: ${it.message}") }
                        }
                    }
                    .onSuccess { showCreate = false; refreshTick++ }
                }
            },
        )
    }
}

// ---------------------------------------------------------------------------
// Detail dialog
// ---------------------------------------------------------------------------

@Composable
private fun TicketDetailDialog(
    api: BillioneMotosApi,
    apiBaseUrl: String,
    ticketId: String,
    onClose: () -> Unit,
    onChanged: () -> Unit,
    onEdit: (TicketResponse) -> Unit,
    onStatus: (TicketResponse) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val repository = remember(api) { TicketsRepository(api) }
    var ticket by remember(ticketId) { mutableStateOf<TicketResponse?>(null) }
    var loading by remember(ticketId) { mutableStateOf(true) }
    var comentario by remember(ticketId) { mutableStateOf("") }
    var sending by remember(ticketId) { mutableStateOf(false) }
    var reloadTick by remember(ticketId) { mutableStateOf(0) }

    LaunchedEffect(ticketId, reloadTick) {
        loading = true
        runCatching { repository.getTicket(ticketId) }
            .onSuccess { ticket = it }
        loading = false
    }

    DialogShell(onClose = onClose, title = "Chamado", widthFraction = 0.82f) {
        val t = ticket
        when {
            loading && t == null -> LoadingState()
            t == null -> EmptyState("Chamado não encontrado.")
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    "#${t.numero} • ${t.titulo}",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    BadgePill(text = t.status.label(), accent = t.status.accent())
                                    BadgePill(text = t.prioridade.label(), accent = t.prioridade.accent())
                                    BadgePill(text = t.categoria.label(), accent = Color(0xFF5BC0FF))
                                    BadgePill(text = t.origem.label(), accent = Color(0xFFB388FF))
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { onStatus(t) },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFF5BC0FF).copy(alpha = 0.4f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5BC0FF)),
                                ) { Text("Status") }
                                OutlinedButton(
                                    onClick = { onEdit(t) },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                ) { Text("Editar") }
                            }
                        }
                    }

                    item {
                        InfoCard {
                            InfoLine("Cliente", t.clienteLabel())
                            InfoLine("Moto", t.motoLabel())
                            InfoLine("Contrato", t.contratoLabel())
                            InfoLine("Aberto por", t.abertoPorNome ?: "—")
                            InfoLine("Responsável", t.responsavelNome ?: "—")
                            InfoLine("Aberto em", formatTicketDate(t.createdAt))
                            if (!t.descricao.isNullOrBlank()) InfoLine("Descrição", t.descricao!!)
                            if (!t.resolucao.isNullOrBlank()) InfoLine("Resolução", t.resolucao!!)
                        }
                    }

                    item {
                        SectionTitle("Fotos (${t.arquivos.size})")
                    }
                    if (t.arquivos.isEmpty()) {
                        item { MutedText("Nenhuma foto anexada.") }
                    } else {
                        items(t.arquivos) { arquivo ->
                            TicketPhotoThumb(api = api, apiBaseUrl = apiBaseUrl, arquivo = arquivo)
                        }
                    }

                    item {
                        SectionTitle("Comentários (${t.comentarios.size})")
                    }
                    if (t.comentarios.isEmpty()) {
                        item { MutedText("Nenhum comentário ainda.") }
                    } else {
                        items(t.comentarios) { c ->
                            InfoCard {
                                Text(
                                    text = (c.autorNome ?: c.autorTipo),
                                    color = ACCENT,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(Modifier.size(4.dp))
                                Text(c.mensagem, color = Color.White, fontSize = 13.sp)
                                Spacer(Modifier.size(4.dp))
                                Text(formatTicketDate(c.createdAt), color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                            }
                        }
                    }

                    item {
                        DarkTextField(
                            value = comentario,
                            onValueChange = { comentario = it },
                            label = "Responder ao cliente",
                            minLines = 2,
                        )
                    }
                    item {
                        Button(
                            onClick = {
                                val msg = comentario.trim()
                                if (msg.isNotBlank() && !sending) {
                                    sending = true
                                    scope.launch {
                                        runCatching {
                                            repository.addComment(t.id, CreateTicketCommentRequest(mensagem = msg))
                                        }.onSuccess {
                                            comentario = ""
                                            reloadTick++
                                            onChanged()
                                        }
                                        sending = false
                                    }
                                }
                            },
                            enabled = comentario.isNotBlank() && !sending,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ACCENT, contentColor = Color.Black),
                        ) { Text(if (sending) "Enviando..." else "Enviar comentário", fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketPhotoThumb(
    api: BillioneMotosApi,
    apiBaseUrl: String,
    arquivo: TicketArquivoResponse,
) {
    var bitmap by remember(arquivo.id) { mutableStateOf<ImageBitmap?>(null) }
    var failed by remember(arquivo.id) { mutableStateOf(false) }

    LaunchedEffect(arquivo.id) {
        failed = false
        bitmap = null
        runCatching {
            withContext(Dispatchers.Default) {
                val url = apiBaseUrl.trimEnd('/') + "/arquivos/${arquivo.id}/render"
                val bytes = api.fetchRawBytes(url)
                bytes.toImageBitmap()
            }
        }.onSuccess { bitmap = it }.onFailure { failed = true }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center,
    ) {
        val current = bitmap
        when {
            current != null -> Image(
                bitmap = current,
                contentDescription = arquivo.nomeArquivo,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit,
            )
            failed -> Icon(Icons.Default.BrokenImage, null, tint = Color.White.copy(alpha = 0.4f))
            else -> CircularProgressIndicator(color = ACCENT, modifier = Modifier.size(28.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Edit dialog
// ---------------------------------------------------------------------------

@Composable
private fun TicketEditDialog(
    ticket: TicketResponse,
    onClose: () -> Unit,
    onSave: (UpdateTicketRequest) -> Unit,
) {
    var titulo by remember { mutableStateOf(ticket.titulo) }
    var descricao by remember { mutableStateOf(ticket.descricao.orEmpty()) }
    var categoria by remember { mutableStateOf(ticket.categoria) }
    var prioridade by remember { mutableStateOf(ticket.prioridade) }

    DialogShell(onClose = onClose, title = "Editar chamado #${ticket.numero}", widthFraction = 0.5f) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
            DarkTextField(value = titulo, onValueChange = { titulo = it }, label = "Título")
            DarkTextField(value = descricao, onValueChange = { descricao = it }, label = "Descrição", minLines = 3)
            EnumDropdown("Categoria", categoria, TicketCategoria.entries, { it.label() }) { categoria = it }
            EnumDropdown("Prioridade", prioridade, TicketPrioridade.entries, { it.label() }) { prioridade = it }
            DialogActions(
                confirmLabel = "Salvar",
                confirmEnabled = titulo.isNotBlank(),
                onCancel = onClose,
                onConfirm = {
                    onSave(
                        UpdateTicketRequest(
                            titulo = titulo.trim(),
                            descricao = descricao.trim().ifBlank { null },
                            categoria = categoria,
                            prioridade = prioridade,
                        ),
                    )
                },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Status dialog
// ---------------------------------------------------------------------------

@Composable
private fun TicketStatusDialog(
    ticket: TicketResponse,
    onClose: () -> Unit,
    onConfirm: (UpdateTicketStatusRequest) -> Unit,
) {
    var status by remember { mutableStateOf(ticket.status) }
    var resolucao by remember { mutableStateOf(ticket.resolucao.orEmpty()) }
    val needsResolucao = status == TicketStatus.RESOLVIDO || status == TicketStatus.CANCELADO

    DialogShell(onClose = onClose, title = "Alterar status #${ticket.numero}", widthFraction = 0.45f) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
            EnumDropdown("Status", status, TicketStatus.entries, { it.label() }) { status = it }
            if (needsResolucao) {
                DarkTextField(value = resolucao, onValueChange = { resolucao = it }, label = "Resolução / motivo", minLines = 3)
            }
            DialogActions(
                confirmLabel = "Aplicar",
                confirmEnabled = true,
                onCancel = onClose,
                onConfirm = {
                    onConfirm(
                        UpdateTicketStatusRequest(
                            status = status,
                            resolucao = resolucao.trim().ifBlank { null },
                        ),
                    )
                },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Create dialog
// ---------------------------------------------------------------------------

@Composable
private fun TicketCreateDialog(
    onClose: () -> Unit,
    onCreate: (CreateTicketRequest, List<UploadFileRequest>) -> Unit,
) {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(TicketCategoria.MANUTENCAO) }
    var prioridade by remember { mutableStateOf(TicketPrioridade.MEDIA) }
    var clienteId by remember { mutableStateOf("") }

    DialogShell(onClose = onClose, title = "Novo chamado", widthFraction = 0.5f) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
            DarkTextField(value = titulo, onValueChange = { titulo = it }, label = "Título")
            DarkTextField(value = descricao, onValueChange = { descricao = it }, label = "Descrição", minLines = 3)
            EnumDropdown("Categoria", categoria, TicketCategoria.entries, { it.label() }) { categoria = it }
            EnumDropdown("Prioridade", prioridade, TicketPrioridade.entries, { it.label() }) { prioridade = it }
            DarkTextField(value = clienteId, onValueChange = { clienteId = it }, label = "ID do cliente (opcional)")
            
            var selectedFiles by remember { mutableStateOf<List<UploadFileRequest>>(emptyList()) }
            val pickImage = rememberImagePicker { request ->
                if (request != null) selectedFiles = selectedFiles + request
            }
            OutlinedButton(
                onClick = { pickImage() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text(if (selectedFiles.isEmpty()) "Anexar Fotos" else "Fotos Selecionadas (${selectedFiles.size})")
            }

            DialogActions(
                confirmLabel = "Abrir chamado",
                confirmEnabled = titulo.isNotBlank(),
                onCancel = onClose,
                onConfirm = {
                    onCreate(
                        CreateTicketRequest(
                            titulo = titulo.trim(),
                            descricao = descricao.trim().ifBlank { null },
                            categoria = categoria,
                            prioridade = prioridade,
                            clienteId = clienteId.trim().ifBlank { null },
                        ),
                        selectedFiles
                    )
                },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Shared dialog scaffolding
// ---------------------------------------------------------------------------

@Composable
private fun DialogShell(
    onClose: () -> Unit,
    title: String,
    widthFraction: Float,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.84f))
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(widthFraction).fillMaxHeight(0.9f),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF060907),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            ) {
                Column(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Surface(onClick = onClose, shape = CircleShape, color = Color.White.copy(alpha = 0.06f)) {
                            Icon(Icons.Default.Close, "Fechar", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(8.dp).size(18.dp))
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) { content() }
                }
            }
        }
    }
}

@Composable
private fun DialogActions(
    confirmLabel: String,
    confirmEnabled: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        ) { Text("Cancelar") }
        Button(
            onClick = onConfirm,
            enabled = confirmEnabled,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ACCENT, contentColor = Color.Black),
        ) { Text(confirmLabel, fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CARD_BG),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp), content = content)
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(label, color = Color.White.copy(alpha = 0.42f), fontSize = 12.sp, modifier = Modifier.width(110.dp))
        Text(value, color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun MutedText(text: String) {
    Text(text, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
}

@Composable
private fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, color = Color.White.copy(alpha = 0.45f)) },
        minLines = minLines,
        singleLine = minLines == 1,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.02f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
            focusedBorderColor = ACCENT.copy(alpha = 0.45f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.10f),
            cursorColor = ACCENT,
        ),
    )
}

@Composable
private fun <T> EnumDropdown(
    label: String,
    selected: T,
    options: List<T>,
    render: (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = render(selected),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label, color = Color.White.copy(alpha = 0.45f)) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = ACCENT) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.White.copy(alpha = 0.02f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
                focusedBorderColor = ACCENT.copy(alpha = 0.35f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.10f),
                cursorColor = ACCENT,
            ),
        )
        Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(render(option), color = if (option == selected) ACCENT else Color.White) },
                    onClick = { onSelect(option); expanded = false },
                )
            }
        }
    }
}
