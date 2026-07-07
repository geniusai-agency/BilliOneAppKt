package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.components.AdminSectionCard
import com.example.billionemotosappkt.desktop.admin.components.DetailGrid
import com.example.billionemotosappkt.desktop.admin.components.SectionHeader
import com.example.billionemotosappkt.desktop.admin.components.SectionShell
import com.example.billionemotosappkt.desktop.admin.model.AdminDashboardSnapshot
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.ContratoStatus
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ContratosSection(snapshot: AdminDashboardSnapshot, api: BillioneMotosApi) {
    var contratos by remember { mutableStateOf<List<ContratoResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedPlan by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<ContratoStatus?>(null) }

    LaunchedEffect(api) {
        isLoading = true
        runCatching {
            api.contratos.list(
                ListContratosQuery(
                    includeRelations = true,
                    page = 1,
                    limit = 100,
                ),
            ).items
        }.onSuccess {
            contratos = it
            loadError = null
        }.onFailure {
            loadError = it.message ?: "Falha ao carregar contratos."
        }
        isLoading = false
    }

    val planChips = remember(contratos) {
        buildList {
            add(PlanChip("Todos", contratos.size))
            contratos
                .groupBy { it.planoSnapshotNivel.ifBlank { "Sem plano" } }
                .toSortedMap()
                .forEach { (nivel, items) ->
                    add(PlanChip(nivel, items.size))
                }
        }
    }

    val filteredContratos = remember(contratos, searchQuery, selectedPlan, selectedStatus) {
        val query = searchQuery.trim().lowercase()
        contratos.filter { contrato ->
            val matchesQuery = query.isBlank() || listOf(
                contrato.cliente?.nome.orEmpty(),
                contrato.moto?.modelo.orEmpty(),
                contrato.moto?.placa.orEmpty(),
                contrato.planoSnapshotNome,
                contrato.planoSnapshotNivel,
                contrato.clienteId,
                contrato.motoId,
            ).any { candidate -> candidate.contains(query, ignoreCase = true) }
            val matchesPlan = selectedPlan == null || contrato.planoSnapshotNivel.equals(selectedPlan, ignoreCase = true)
            val matchesStatus = selectedStatus == null || contrato.status == selectedStatus
            matchesQuery && matchesPlan && matchesStatus
        }
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        HeaderRow(
            snapshot = snapshot,
            total = contratos.size,
            onRefresh = {
                selectedPlan = null
                selectedStatus = null
                searchQuery = ""
            },
        )

        SearchBar(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Buscar cliente, moto, placa ou plano...",
        )

        FilterSurface(
            planChips = planChips,
            selectedPlan = selectedPlan,
            onPlanSelected = { selectedPlan = it },
            selectedStatus = selectedStatus,
            onStatusSelected = { selectedStatus = it },
            onClear = {
                searchQuery = ""
                selectedPlan = null
                selectedStatus = null
            },
        )

        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(280.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF20E65B))
            }
        } else if (loadError != null) {
            ErrorState(loadError.orEmpty())
        } else {
            ContractsTable(
                contratos = filteredContratos,
                total = contratos.size,
            )
        }
    }
}

@Composable
private fun HeaderRow(
    snapshot: AdminDashboardSnapshot,
    total: Int,
    onRefresh: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SectionHeader(
                title = "Contratos",
                subtitle = "$total contratos cadastrados",
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = onRefresh,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Rodar automacoes", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF20E65B),
                    contentColor = Color.Black,
                ),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            ) {
                Text("Novo contrato", fontWeight = FontWeight.SemiBold)
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val cardWidth = ((maxWidth - 12.dp) / 2).coerceAtLeast(280.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            AdminSectionCard(
                title = "Contratos ativos",
                subtitle = "Em andamento",
                icon = Icons.Default.Description,
                accent = Color(0xFF20E65B),
                value = snapshot.activeContracts.toString(),
                modifier = Modifier.width(cardWidth),
            )
            AdminSectionCard(
                title = "Vencendo 60d",
                subtitle = "Proximas renovacoes",
                icon = Icons.Default.EventNote,
                accent = Color(0xFFFFB300),
                value = snapshot.dueIn60Days.toString(),
                modifier = Modifier.width(cardWidth),
            )
        }
    }
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.34f)) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.38f)) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
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

@Composable
private fun FilterSurface(
    planChips: List<PlanChip>,
    selectedPlan: String?,
    onPlanSelected: (String?) -> Unit,
    selectedStatus: ContratoStatus?,
    onStatusSelected: (ContratoStatus?) -> Unit,
    onClear: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Filtrar por plano:", color = Color.White.copy(alpha = 0.58f), fontSize = 13.sp)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
                    planChips.forEach { chip ->
                        PlanChipView(
                            label = chip.label,
                            count = chip.count,
                            selected = selectedPlan == null && chip.label == "Todos" || selectedPlan?.equals(chip.label, ignoreCase = true) == true,
                            onClick = {
                                onPlanSelected(if (chip.label == "Todos") null else chip.label)
                            },
                        )
                    }
                }

        Spacer(Modifier.width(12.dp))
        Text("${planChips.firstOrNull()?.count ?: 0} contrato(s)", color = Color.White.copy(alpha = 0.42f), fontSize = 12.sp)
    }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                StatusChip("Todos", selectedStatus == null) { onStatusSelected(null) }
                ContratoStatus.entries.forEach { status ->
                    StatusChip(statusLabel(status), selectedStatus == status) { onStatusSelected(status) }
                }
                OutlinedButton(
                    onClick = onClear,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                ) {
                    Text("Limpar filtros")
                }
            }
        }
    }
}

@Composable
private fun PlanChipView(
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = when {
            selected -> Color(0xFF20E65B).copy(alpha = 0.18f)
            label.equals("Ouro", ignoreCase = true) -> Color(0xFFF59E0B).copy(alpha = 0.12f)
            label.equals("Prata", ignoreCase = true) -> Color(0xFF9CA3AF).copy(alpha = 0.12f)
            label.equals("Bronze", ignoreCase = true) -> Color(0xFFB45309).copy(alpha = 0.12f)
            else -> Color.White.copy(alpha = 0.03f)
        },
        border = BorderStroke(
            1.dp,
            when {
                selected -> Color(0xFF20E65B).copy(alpha = 0.35f)
                else -> Color.White.copy(alpha = 0.06f)
            },
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                label,
                color = when {
                    selected -> Color(0xFF20E65B)
                    else -> Color.White.copy(alpha = 0.72f)
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                count.toString(),
                color = Color.White.copy(alpha = 0.42f),
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun StatusChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = if (selected) Color(0xFF20E65B).copy(alpha = 0.18f) else Color.White.copy(alpha = 0.03f),
        border = BorderStroke(
            1.dp,
            if (selected) Color(0xFF20E65B).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f),
        ),
    ) {
        Text(
            label,
            color = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.72f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun ContractsTable(
    contratos: List<ContratoResponse>,
    total: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val tableWidth = maxOf(maxWidth, 1360.dp)
            Column(modifier = Modifier.horizontalScroll(rememberScrollState()).width(tableWidth)) {
                TableHeaderRow()
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                if (contratos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Nenhum contrato encontrado.", color = Color.White.copy(alpha = 0.42f))
                    }
                } else {
                    contratos.forEachIndexed { index, contrato ->
                        ContractTableRow(contrato = contrato)
                        if (index < contratos.lastIndex) {
                            HorizontalDivider(color = Color.White.copy(alpha = 0.03f), modifier = Modifier.padding(horizontal = 20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val header = Color.White.copy(alpha = 0.42f)
        TableHead("Cliente", 240.dp, header)
        TableHead("Moto", 180.dp, header)
        TableHead("Modalidade", 130.dp, header)
        TableHead("Plano", 130.dp, header)
        TableHead("Início", 110.dp, header)
        TableHead("Parcela", 130.dp, header)
        TableHead("Status", 130.dp, header)
        TableHead("Sinais", 180.dp, header)
    }
}

@Composable
private fun TableHead(label: String, columnWidth: Dp, color: Color) {
    Text(
        label,
        modifier = Modifier.width(columnWidth),
        color = color,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun ContractTableRow(contrato: ContratoResponse) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.width(240.dp)) {
            Text(
                contrato.cliente?.nome ?: "Cliente #${contrato.clienteId.take(6)}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                contrato.cliente?.cpf ?: "CPF nao informado",
                color = Color.White.copy(alpha = 0.44f),
                fontSize = 11.sp,
            )
        }

        Column(modifier = Modifier.width(180.dp)) {
            Text(
                "${contrato.moto?.placa ?: "Sem placa"}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                motoLabel(contrato),
                color = Color.White.copy(alpha = 0.44f),
                fontSize = 11.sp,
            )
        }

        Column(modifier = Modifier.width(130.dp)) {
            StatusBadge(contrato.modalidade.name.lowercase().replace('_', ' ').replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }, Color(0xFF7DD3FC))
            Text(
                contrato.tipo.name.lowercase().replace('_', ' ').replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                color = Color.White.copy(alpha = 0.38f),
                fontSize = 11.sp,
            )
        }

        Column(modifier = Modifier.width(130.dp)) {
            Text(
                contrato.planoSnapshotNome,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                contrato.planoSnapshotNivel,
                color = Color.White.copy(alpha = 0.44f),
                fontSize = 11.sp,
            )
        }

        Text(
            formatIsoDate(contrato.dataInicio) ?: contrato.dataInicio.take(10),
            modifier = Modifier.width(110.dp),
            color = Color.White.copy(alpha = 0.80f),
            fontSize = 13.sp,
        )

        Text(
            "R$ ${contrato.valorParcela} x ${contrato.numParcelas}",
            modifier = Modifier.width(130.dp),
            color = Color.White.copy(alpha = 0.80f),
            fontSize = 13.sp,
        )

        Box(modifier = Modifier.width(130.dp)) {
            StatusBadge(statusLabel(contrato.status), statusColor(contrato.status))
        }

        Row(
            modifier = Modifier.width(180.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ActionPill("Pagamentos")
            ActionPill("Editar")
        }
    }
}

@Composable
private fun StatusBadge(label: String, accent: Color) {
    Surface(
        color = accent.copy(alpha = 0.14f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
    ) {
        Text(
            label,
            color = accent,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun ActionPill(label: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.02f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
    ) {
        Text(
            label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun ErrorState(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.18f)),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Refresh, null, tint = Color(0xFFFF4A4A))
            Spacer(Modifier.width(10.dp))
            Text(message, color = Color.White)
        }
    }
}

@Composable
fun ContratosJuridicosSection() = SectionShell(
    title = "Contratos Juridicos",
    subtitle = "Termos, aditivos e documentos legais.",
    body = "Editor de termos, aditivos e documentos legais.",
    chips = listOf("Editor", "Aditivo", "Assinatura", "Versionamento"),
    stats = listOf("Rascunhos", "Publicados", "Assinados"),
    content = {
        DetailGrid(
            listOf(
                "Estrutura visual pensada para documentos juridicos e versionamento.",
                "Pode receber editor, upload e historico de termos.",
            ),
        )
    },
)

@Composable
fun FinanceiroSection(snapshot: AdminDashboardSnapshot) = SectionShell(
    title = "Financeiro",
    subtitle = "Receitas, despesas e fluxo de caixa.",
    body = "Receitas, despesas, cobrancas e fluxo de caixa.",
    chips = listOf("Receita", "Despesa", "Caixa", "Fluxo"),
    stats = listOf(snapshot.monthlyBilling, snapshot.monthlyReceived, snapshot.openItems),
    content = {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cardWidth = ((maxWidth - 16.dp) / 2).coerceAtLeast(280.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                AdminSectionCard(
                    title = "Faturamento",
                    subtitle = "Total do periodo",
                    icon = Icons.Default.AttachMoney,
                    accent = Color(0xFF20E65B),
                    value = snapshot.monthlyBilling,
                    modifier = Modifier.width(cardWidth),
                )
                AdminSectionCard(
                    title = "Em aberto",
                    subtitle = "Pendencias financeiras",
                    icon = Icons.Default.Wallet,
                    accent = Color(0xFFFF4A4A),
                    value = snapshot.openItems,
                    modifier = Modifier.width(cardWidth),
                )
            }
        }
        DetailGrid(
            listOf(
                "Esse bloco segue o estilo do dashboard web e pode ser ligado ao grafico real.",
                "Use esta area para relatorios e resumo mensal.",
            ),
        )
    },
)

@Composable
fun SantanderSection() = SectionShell(
    title = "Santander",
    subtitle = "Boletos, remessas e retorno CNAB.",
    body = "Boletos, remessas, retorno CNAB e conciliacao bancaria.",
    chips = listOf("CNAB", "Boletos", "Retorno", "Baixa"),
    stats = listOf("Arquivos", "Pendentes", "Processados"),
    content = {
        DetailGrid(
            listOf(
                "Area reservada para integracao bancaria e conciliacao.",
                "Aqui entra a operacao de boletos e retorno CNAB.",
            ),
        )
    },
)

@Composable
fun ManutencaoSection() = SectionShell(
    title = "Manutencao",
    subtitle = "OS, agenda e ordens abertas.",
    body = "Ordens abertas, agenda e historico de manutencao.",
    chips = listOf("Agenda", "Abertas", "Pecas", "Executadas"),
    stats = listOf("OS abertas", "Em execucao", "Finalizadas"),
    content = {
        DetailGrid(
            listOf(
                "Fluxo de manutencao visualmente consistente com o restante do admin.",
                "Integre aqui a lista de servicos e ordens abertas.",
            ),
        )
    },
)

@Composable
fun OrdemServicoSection() = SectionShell(
    title = "Ordem de Servico",
    subtitle = "Detalhes, pecas e etapas da OS.",
    body = "Detalhes da OS, pecas e etapas operacionais.",
    chips = listOf("Detalhes", "Pecas", "Etapas", "Notas"),
    stats = listOf("Aberta", "Em fila", "Concluida"),
    content = {
        DetailGrid(
            listOf(
                "Tela preparada para o detalhe operacional da OS.",
                "Pode receber timeline, checklist e anexos.",
            ),
        )
    },
)

@Composable
fun RelatoriosSection() = SectionShell(
    title = "Relatorios",
    subtitle = "Indicadores e exportacoes da operacao.",
    body = "Indicadores, exportacoes e visao gerencial.",
    chips = listOf("PDF", "CSV", "Periodo", "KPIs"),
    stats = listOf("Mensal", "Trimestral", "Anual"),
    content = {
        DetailGrid(
            listOf(
                "Espaco para graficos, exportacao e indicadores chave.",
                "Visual alinhado ao dashboard principal.",
            ),
        )
    },
)

private data class PlanChip(
    val label: String,
    val count: Int,
)

private fun motoLabel(contrato: ContratoResponse): String {
    val model = contrato.moto?.modelo ?: contrato.planoSnapshotNome
    val plate = contrato.moto?.placa?.takeIf { it.isNotBlank() }
    return listOfNotNull(model.takeIf { it.isNotBlank() }, plate?.let { "• $it" }).joinToString(" ")
}

private fun statusLabel(status: ContratoStatus): String = when (status) {
    ContratoStatus.ATIVO -> "Ativo"
    ContratoStatus.INADIMPLENTE -> "Inadimplente"
    ContratoStatus.ENCERRADO -> "Encerrado"
    ContratoStatus.CANCELADO -> "Cancelado"
    ContratoStatus.PENDENTE_ASSINATURA -> "Pendente"
}

private fun statusColor(status: ContratoStatus): Color = when (status) {
    ContratoStatus.ATIVO -> Color(0xFF20E65B)
    ContratoStatus.INADIMPLENTE -> Color(0xFFFF4A4A)
    ContratoStatus.ENCERRADO -> Color(0xFF8A8F95)
    ContratoStatus.CANCELADO -> Color(0xFFE05B64)
    ContratoStatus.PENDENTE_ASSINATURA -> Color(0xFFFFB300)
}

private fun formatIsoDate(value: String?): String? {
    if (value.isNullOrBlank()) return null
    return runCatching {
        val parsed = ZonedDateTime.parse(value)
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(parsed)
    }.getOrNull() ?: value.take(10)
}
