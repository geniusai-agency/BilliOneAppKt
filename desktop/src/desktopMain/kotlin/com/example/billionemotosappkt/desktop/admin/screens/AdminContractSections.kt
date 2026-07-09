package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.billionemotosappkt.desktop.admin.screens.contractparts.*

@Composable
fun ContratosSection(snapshot: AdminDashboardSnapshot, api: BillioneMotosApi) {
    val scope = rememberCoroutineScope()
    var contratos by remember { mutableStateOf<List<ContratoResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedPlan by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<ContratoStatus?>(null) }
    var currentPage by remember { mutableIntStateOf(1) }
    var reloadTick by remember { mutableStateOf(0) }
    var viewingContrato by remember { mutableStateOf<ContratoResponse?>(null) }
    var previewContrato by remember { mutableStateOf<ContratoResponse?>(null) }
    var editingContrato by remember { mutableStateOf<ContratoResponse?>(null) }
    var inactivatingContrato by remember { mutableStateOf<ContratoResponse?>(null) }
    var mutationLoading by remember { mutableStateOf(false) }
    var mutationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(api, reloadTick) {
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

    val pageSize = 6
    val totalPages = remember(filteredContratos.size, pageSize) {
        ((filteredContratos.size + pageSize - 1) / pageSize).coerceAtLeast(1)
    }
    val pagedContratos = remember(filteredContratos, currentPage, pageSize) {
        val safePage = currentPage.coerceIn(1, totalPages)
        val startIndex = (safePage - 1) * pageSize
        filteredContratos.drop(startIndex).take(pageSize)
    }

    LaunchedEffect(searchQuery, selectedPlan, selectedStatus) {
        currentPage = 1
    }

    LaunchedEffect(totalPages) {
        if (currentPage > totalPages) {
            currentPage = totalPages
        }
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        HeaderRow(
            snapshot = snapshot,
            total = contratos.size,
            onRefresh = {
                reloadTick += 1
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
                contratos = pagedContratos,
                totalItems = filteredContratos.size,
                currentPage = currentPage,
                totalPages = totalPages,
                onPrev = { if (currentPage > 1) currentPage -= 1 },
                onNext = { if (currentPage < totalPages) currentPage += 1 },
                onView = { viewingContrato = it },
                onEdit = { editingContrato = it },
                onInactivate = { inactivatingContrato = it },
            )
        }
    }

    viewingContrato?.let { contrato ->
        ContractDetailsDialog(
            contrato = contrato,
            onOpenPdf = { previewContrato = contrato },
            onDismiss = { viewingContrato = null },
        )
    }

    previewContrato?.let { contrato ->
        ContractPreviewDialog(
            api = api,
            contrato = contrato,
            onDismiss = { previewContrato = null },
        )
    }

    editingContrato?.let { contrato ->
        ContractEditDialog(
            contrato = contrato,
            isLoading = mutationLoading,
            onDismiss = { editingContrato = null },
            onConfirm = { request ->
                mutationLoading = true
                mutationError = null
                scope.launch {
                    runCatching {
                        api.contratos.update(contrato.id, request)
                    }.onSuccess {
                        editingContrato = null
                        reloadTick += 1
                    }.onFailure { error ->
                        mutationError = error.message ?: "Falha ao atualizar contrato."
                    }
                    mutationLoading = false
                }
            },
        )
    }

    inactivatingContrato?.let { contrato ->
        ContractInactivateDialog(
            contrato = contrato,
            isLoading = mutationLoading,
            onDismiss = { inactivatingContrato = null },
            onConfirm = {
                mutationLoading = true
                mutationError = null
                scope.launch {
                    runCatching {
                        api.contratos.cancelar(contrato.id)
                    }.onSuccess {
                        inactivatingContrato = null
                        reloadTick += 1
                    }.onFailure { error ->
                        mutationError = error.message ?: "Falha ao inativar contrato."
                    }
                    mutationLoading = false
                }
            },
        )
    }

    mutationError?.let {
        ErrorState(message = it)
    }
}

@Composable
fun ContratosJuridicosSection() {
    SectionShell(
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
}

@Composable
fun FinanceiroSection(snapshot: AdminDashboardSnapshot) {
    SectionShell(
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
}

@Composable
fun SantanderSection() {
    SectionShell(
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
}

@Composable
fun ManutencaoSection() {
    SectionShell(
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
}

@Composable
fun OrdemServicoSection() {
    SectionShell(
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
}

@Composable
fun RelatoriosSection() {
    SectionShell(
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
}

