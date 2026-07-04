package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.components.DetailGrid
import com.example.billionemotosappkt.desktop.admin.components.SectionHeader
import com.example.billionemotosappkt.desktop.admin.components.SectionShell
import com.example.billionemotosappkt.desktop.admin.components.AdminSectionCard
import com.example.billionemotosappkt.desktop.admin.model.AdminDashboardSnapshot
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.ContratoStatus
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import androidx.compose.foundation.BorderStroke

@Composable
fun ContratosSection(snapshot: AdminDashboardSnapshot, api: BillioneMotosApi) {
    var contratos by remember { mutableStateOf<List<ContratoResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<ContratoStatus?>(null) }

    LaunchedEffect(api) {
        isLoading = true
        runCatching {
            api.contratos.list(ListContratosQuery(includeRelations = true, page = 1, limit = 50)).items
        }.onSuccess {
            contratos = it
            loadError = null
        }.onFailure {
            loadError = it.message ?: "Falha ao carregar contratos."
        }
        isLoading = false
    }

    val filteredContratos = remember(contratos, searchQuery, selectedStatus) {
        contratos.filter {
            val matchesQuery = searchQuery.isBlank() ||
                it.cliente?.nome?.contains(searchQuery, ignoreCase = true) == true ||
                it.moto?.modelo?.contains(searchQuery, ignoreCase = true) == true
            val matchesStatus = selectedStatus == null || it.status == selectedStatus
            matchesQuery && matchesStatus
        }
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            SectionHeader(title = "Contratos", subtitle = "Gestão de assinaturas, vencimentos e adimplência.")

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.width(320.dp),
                placeholder = { Text("Buscar cliente ou moto...") },
                leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF20E65B),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                ),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ContratoFilterChip("Todos", selectedStatus == null) { selectedStatus = null }
            ContratoStatus.entries.forEach { status ->
                ContratoFilterChip(status.name.replace("_", " ").lowercase().capitalizeFirst(), selectedStatus == status) {
                    selectedStatus = status
                }
            }
        }

        TwoUpCards(
            first = { modifier ->
                AdminSectionCard("Contratos ativos", "Em andamento", Icons.Default.Description, Color(0xFF20E65B), snapshot.activeContracts.toString(), modifier)
            },
            second = { modifier ->
                AdminSectionCard("Vencendo 60d", "Próximas renovações", Icons.Default.EventNote, Color(0xFFFFB300), snapshot.dueIn60Days.toString(), modifier)
            },
        )

        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF20E65B))
            }
        } else if (loadError != null) {
            Text(loadError!!, color = Color(0xFFFF4A4A), modifier = Modifier.padding(16.dp))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 32.dp)) {
                filteredContratos.forEach { contrato ->
                    ContratoListItemCard(contrato)
                }
                if (filteredContratos.isEmpty()) {
                    Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhum contrato encontrado.", color = Color.White.copy(alpha = 0.4f))
                    }
                }
            }
        }
    }
}

@Composable
fun ContratosJuridicosSection() = SectionShell(
    title = "Contratos Jurídicos",
    subtitle = "Termos, aditivos e documentos legais.",
    body = "Editor de termos, aditivos e documentos legais.",
    chips = listOf("Editor", "Aditivo", "Assinatura", "Versionamento"),
    stats = listOf("Rascunhos", "Publicados", "Assinados"),
    content = {
        DetailGrid(
            listOf(
                "Estrutura visual pensada para documentos jurídicos e versionamento.",
                "Pode receber editor, upload e histórico de termos.",
            ),
        )
    },
)

@Composable
fun FinanceiroSection(snapshot: AdminDashboardSnapshot) = SectionShell(
    title = "Financeiro",
    subtitle = "Receitas, despesas e fluxo de caixa.",
    body = "Receitas, despesas, cobranças e fluxo de caixa.",
    chips = listOf("Receita", "Despesa", "Caixa", "Fluxo"),
    stats = listOf(snapshot.monthlyBilling, snapshot.monthlyReceived, snapshot.openItems),
    content = {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            AdminSectionCard(
                title = "Faturamento",
                subtitle = "Total do período",
                icon = Icons.Default.AttachMoney,
                accent = Color(0xFF20E65B),
                value = snapshot.monthlyBilling,
            )
            AdminSectionCard(
                title = "Em aberto",
                subtitle = "Pendências financeiras",
                icon = Icons.Default.Wallet,
                accent = Color(0xFFFF4A4A),
                value = snapshot.openItems,
            )
        }
        DetailGrid(
            listOf(
                "Esse bloco segue o estilo do dashboard web e pode ser ligado ao gráfico real.",
                "Use esta área para relatórios e resumo mensal.",
            ),
        )
    },
)

@Composable
fun SantanderSection() = SectionShell(
    title = "Santander",
    subtitle = "Boletos, remessas e retorno CNAB.",
    body = "Boletos, remessas, retorno CNAB e conciliação bancária.",
    chips = listOf("CNAB", "Boletos", "Retorno", "Baixa"),
    stats = listOf("Arquivos", "Pendentes", "Processados"),
    content = {
        DetailGrid(
            listOf(
                "Área reservada para integração bancária e conciliação.",
                "Aqui entra a operação de boletos e retorno CNAB.",
            ),
        )
    },
)

@Composable
fun ManutencaoSection() = SectionShell(
    title = "Manutenção",
    subtitle = "OS, agenda e ordens abertas.",
    body = "Ordens abertas, agenda e histórico de manutenção.",
    chips = listOf("Agenda", "Abertas", "Pecas", "Executadas"),
    stats = listOf("OS abertas", "Em execução", "Finalizadas"),
    content = {
        DetailGrid(
            listOf(
                "Fluxo de manutenção visualmente consistente com o restante do admin.",
                "Integre aqui a lista de serviços e ordens abertas.",
            ),
        )
    },
)

@Composable
fun OrdemServicoSection() = SectionShell(
    title = "Ordem de Serviço",
    subtitle = "Detalhes, peças e etapas da OS.",
    body = "Detalhes da OS, peças e etapas operacionais.",
    chips = listOf("Detalhes", "Peças", "Etapas", "Notas"),
    stats = listOf("Aberta", "Em fila", "Concluída"),
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
    title = "Relatórios",
    subtitle = "Indicadores e exportações da operação.",
    body = "Indicadores, exportações e visão gerencial.",
    chips = listOf("PDF", "CSV", "Período", "KPIs"),
    stats = listOf("Mensal", "Trimestral", "Anual"),
    content = {
        DetailGrid(
            listOf(
                "Espaço para gráficos, exportação e indicadores chave.",
                "Visual alinhado ao dashboard principal.",
            ),
        )
    },
)

@Composable
private fun ContratoListItemCard(contrato: ContratoResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF20E65B).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF20E65B))
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contrato.cliente?.nome ?: "Cliente #${contrato.clienteId.take(6)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsBike, null, modifier = Modifier.size(14.dp), tint = Color.White.copy(alpha = 0.4f))
                        Spacer(Modifier.width(4.dp))
                        Text(contrato.moto?.modelo ?: "Moto s/ modelo", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Layers, null, modifier = Modifier.size(14.dp), tint = Color.White.copy(alpha = 0.4f))
                        Spacer(Modifier.width(4.dp))
                        Text(contrato.planoSnapshotNome, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                ContratoStatusBadge(contrato.status)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(12.dp), tint = Color.White.copy(alpha = 0.3f))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Vence: ${contrato.dataFim?.take(10) ?: "Indeterminado"}",
                        color = Color.White.copy(alpha = 0.3f),
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ContratoFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFF20E65B).copy(alpha = 0.15f) else Color(0xFF111614),
        border = BorderStroke(1.dp, if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.height(32.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(text = label, color = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun ContratoStatusBadge(status: ContratoStatus) {
    val (color, label) = when (status) {
        ContratoStatus.ATIVO -> Color(0xFF20E65B) to "Ativo"
        ContratoStatus.INADIMPLENTE -> Color(0xFFFF4A4A) to "Inadimplente"
        ContratoStatus.PENDENTE_ASSINATURA -> Color(0xFFFFB300) to "Pendente"
        else -> Color.White.copy(alpha = 0.4f) to status.name
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(text = label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun TwoUpCards(
    first: @Composable (Modifier) -> Unit,
    second: @Composable (Modifier) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val gap = 16.dp
        val cardWidth = (maxWidth - gap) / 2
        if (cardWidth >= 280.dp) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap), modifier = Modifier.fillMaxWidth()) {
                first(Modifier.width(cardWidth))
                second(Modifier.width(cardWidth))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                first(Modifier.fillMaxWidth())
                second(Modifier.fillMaxWidth())
            }
        }
    }
}

private fun String.capitalizeFirst(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
