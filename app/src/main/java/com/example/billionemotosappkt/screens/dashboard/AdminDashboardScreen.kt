package com.example.billionemotosappkt.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.billionemotosappkt.data.dashboard.DashboardOverviewData
import com.example.billionemotosappkt.screens.Loading.LoadingScreen
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

@Composable
fun DashboardScreenRoute(
    authState: AuthUiState,
    api: BillioneMotosApi,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.factory(api))
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.isLoading && state.overview == null -> {
            LoadingScreen(modifier = modifier.fillMaxSize())
        }
        state.overview != null -> {
            AdminDashboardScreen(
                authState = authState,
                overview = state.overview!!,
                lastUpdatedAt = state.lastUpdatedAt,
                onRefresh = viewModel::refresh,
                onLogout = onLogout,
                modifier = modifier,
            )
        }
        else -> DashboardErrorState(
            message = state.errorMessage ?: "Nao foi possivel carregar a dashboard.",
            onRetry = viewModel::refresh,
            modifier = modifier,
        )
    }
}

@Composable
fun AdminDashboardScreen(
    authState: AuthUiState,
    overview: DashboardOverviewData,
    lastUpdatedAt: String?,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val metrics = adminMetrics(overview)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        MaterialTheme.colorScheme.background,
                    ),
                ),
            ),
    ) {
        val compact = maxWidth < 1200.dp

        Surface(color = Color.Transparent, modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(if (compact) 16.dp else 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                AdminTopBar(
                    authState = authState,
                    lastUpdatedAt = lastUpdatedAt,
                    onRefresh = onRefresh,
                    onLogout = onLogout,
                    compact = compact,
                )

                AdminHero(
                    authState = authState,
                    overview = overview,
                    lastUpdatedAt = lastUpdatedAt,
                    onRefresh = onRefresh,
                    compact = compact,
                )

                MetricGrid(
                    metrics = metrics,
                    columns = if (compact) 2 else 3,
                )

                AdminStatusStrip(
                    overview = overview,
                    compact = compact,
                )

                AdminListsSection(
                    overview = overview,
                    columns = if (compact) 1 else 2,
                )
            }
        }
    }
}

@Composable
private fun AdminTopBar(
    authState: AuthUiState,
    lastUpdatedAt: String?,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    compact: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            Pill(
                text = "Painel de comando",
                icon = Icons.Default.Insights,
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                contentColor = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Boa ${greeting()}, ${authState.userName ?: "administrador"}",
                style = if (compact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Visao consolidada da operacao, frota, contratos e financeiro.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (!lastUpdatedAt.isNullOrBlank()) {
                Pill(
                    text = "Atualizado $lastUpdatedAt",
                    icon = Icons.Default.Schedule,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Sincronizar")
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.Notifications, contentDescription = "Sair")
            }
        }
    }
}

@Composable
private fun AdminHero(
    authState: AuthUiState,
    overview: DashboardOverviewData,
    lastUpdatedAt: String?,
    onRefresh: () -> Unit,
    compact: Boolean,
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.34f),
                        ),
                    ),
                )
                .padding(if (compact) 18.dp else 24.dp),
        ) {
            if (compact) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    HeroCopy(authState = authState, overview = overview)
                    HeroSnapshot(overview = overview, lastUpdatedAt = lastUpdatedAt, compact = true)
                    HeroActions(onRefresh = onRefresh)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        HeroCopy(authState = authState, overview = overview)
                        HeroActions(onRefresh = onRefresh)
                    }
                    HeroSnapshot(overview = overview, lastUpdatedAt = lastUpdatedAt, compact = false)
                }
            }
        }
    }
}

@Composable
private fun HeroCopy(
    authState: AuthUiState,
    overview: DashboardOverviewData,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill(
                text = if (authState.isAdmin) "Perfil admin" else "Acesso operacional",
                icon = Icons.Default.Groups,
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                contentColor = MaterialTheme.colorScheme.primary,
            )
            Pill(
                text = "${overview.resumo.contratos.ativos} contratos ativos",
                icon = Icons.Default.Timeline,
                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                contentColor = MaterialTheme.colorScheme.tertiary,
            )
        }
        Text(
            text = "Um painel bonito, denso e util para operar a Billione Motos sem perder contexto.",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
        )
        Text(
            text = "Acompanhamento em tempo real de contratos, frota, manutencao, tickets e financeiro em uma leitura unica.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun HeroActions(onRefresh: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        androidx.compose.material3.Button(
            onClick = onRefresh,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Sincronizar")
        }
        OutlinedButton(
            onClick = { },
            shape = RoundedCornerShape(18.dp),
        ) {
            Icon(imageVector = Icons.Default.Insights, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Ver relatorios")
        }
    }
}

@Composable
private fun HeroSnapshot(
    overview: DashboardOverviewData,
    lastUpdatedAt: String?,
    compact: Boolean,
) {
    Card(
        modifier = if (compact) Modifier.fillMaxWidth() else Modifier.width(280.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.62f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SnapshotRow(
                title = "Saldo estimado",
                value = formatCurrency(overview.resumo.financeiro.saldoEstimado.toLong()),
                icon = Icons.Default.AttachMoney,
                tint = MaterialTheme.colorScheme.primary,
            )
            SnapshotRow(
                title = "Motos disponiveis",
                value = overview.resumo.motos.disponiveis.toString(),
                icon = Icons.Default.DirectionsBike,
                tint = MaterialTheme.colorScheme.tertiary,
            )
            SnapshotRow(
                title = "Tickets abertos",
                value = (overview.resumo.tickets.abertos + overview.resumo.tickets.emAnalise).toString(),
                icon = Icons.Default.SupportAgent,
                tint = MaterialTheme.colorScheme.tertiary,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Ultima sincronizacao", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = lastUpdatedAt ?: "Agora", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
private fun SnapshotRow(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}

@Composable
private fun AdminStatusStrip(
    overview: DashboardOverviewData,
    compact: Boolean,
) {
    val blocks = listOf(
        StatusBlock("Clientes", overview.resumo.clientes.aprovados, overview.resumo.clientes.total, MaterialTheme.colorScheme.primary),
        StatusBlock("Motos", overview.resumo.motos.disponiveis, overview.resumo.motos.total, MaterialTheme.colorScheme.tertiary),
        StatusBlock("Contratos", overview.resumo.contratos.ativos, overview.resumo.contratos.total, MaterialTheme.colorScheme.secondary),
        StatusBlock("Tickets", overview.resumo.tickets.resolvidos, overview.resumo.tickets.total, MaterialTheme.colorScheme.primary),
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Saude da operacao", style = MaterialTheme.typography.titleMedium)
        if (compact) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                blocks.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { block ->
                            HealthCard(block = block, modifier = Modifier.weight(1f))
                        }
                        repeat(2 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                blocks.forEach { block -> HealthCard(block = block, modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun HealthCard(
    block: StatusBlock,
    modifier: Modifier = Modifier,
) {
    val percent = percentage(block.value, block.total)
    Card(modifier = modifier, shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f))) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(text = block.label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${block.value}/${block.total}", style = MaterialTheme.typography.titleMedium)
                }
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(block.accent))
            }
            androidx.compose.material3.LinearProgressIndicator(
                progress = { percent },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = block.accent,
                trackColor = block.accent.copy(alpha = 0.12f),
            )
        }
    }
}

@Composable
private fun AdminListsSection(
    overview: DashboardOverviewData,
    columns: Int,
) {
    val sections = listOf(
        ListSectionModel(
            title = "Contratos vencendo",
            subtitle = "Vigencia e risco de renovacao.",
            icon = Icons.Default.Schedule,
            items = overview.contratosVencendo.take(5).map { contract ->
                ListItemModel(
                    title = contractLabel(contract),
                    subtitle = contractSubLabel(contract),
                    value = contractEndLabel(contract),
                    status = contractStatusLabel(contract.status),
                    tint = accentForContract(contract.status),
                )
            },
        ),
        ListSectionModel(
            title = "Pagamentos recentes",
            subtitle = "Ultimos recebimentos processados.",
            icon = Icons.Default.CreditCard,
            items = overview.pagamentosRecentes.take(5).map { payment ->
                ListItemModel(
                    title = paymentLabel(payment),
                    subtitle = paymentSubLabel(payment),
                    value = paymentAmount(payment.valor),
                    status = paymentStatusLabel(payment.status),
                    tint = accentForPayment(payment.status),
                )
            },
        ),
        ListSectionModel(
            title = "Manutencoes",
            subtitle = "Ordens e revisoes acompanhadas.",
            icon = Icons.Default.Build,
            items = overview.manutencoesRecentes.take(5).map { maintenance ->
                ListItemModel(
                    title = maintenanceLabel(maintenance),
                    subtitle = maintenanceSubLabel(maintenance),
                    value = maintenanceCost(maintenance.custoTotal ?: maintenance.custoCliente),
                    status = maintenanceStatusLabel(maintenance.status),
                    tint = accentForMaintenance(maintenance.status),
                )
            },
        ),
        ListSectionModel(
            title = "Tickets",
            subtitle = "Atendimentos e pendencias de operacao.",
            icon = Icons.Default.SupportAgent,
            items = overview.ticketsRecentes.take(5).map { ticket ->
                ListItemModel(
                    title = ticketLabel(ticket),
                    subtitle = ticketSubLabel(ticket),
                    value = ticketPriorityLabel(ticket.prioridade),
                    status = ticketStatusLabel(ticket.status),
                    tint = accentForTicket(ticket.status),
                )
            },
        ),
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Filas operacionais", style = MaterialTheme.typography.titleMedium)
        if (columns == 1) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                sections.forEach { section -> SectionCard(section = section) }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionCard(section = sections[0])
                    SectionCard(section = sections[1])
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionCard(section = sections[2])
                    SectionCard(section = sections[3])
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    section: ListSectionModel,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = section.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = section.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = section.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (section.items.isEmpty()) {
                EmptyListState(text = "Nenhum item para exibir")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    section.items.forEach { item -> ListItemCard(item = item) }
                }
            }
        }
    }
}

@Composable
private fun ListItemCard(item: ListItemModel) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f))) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(item.tint.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.History, contentDescription = null, tint = item.tint, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = item.title, style = MaterialTheme.typography.bodyMedium)
                Text(text = item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = item.value, style = MaterialTheme.typography.bodyMedium)
                Pill(text = item.status, icon = Icons.Default.Timeline, containerColor = item.tint.copy(alpha = 0.12f), contentColor = item.tint)
            }
        }
    }
}

@Composable
private fun EmptyListState(text: String) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f))) {
        Text(text = text, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MetricGrid(
    metrics: List<AdminMetricModel>,
    columns: Int,
) {
    val rows = metrics.chunked(columns)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { metric -> MetricCard(metric = metric, modifier = Modifier.weight(1f)) }
                repeat(columns - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun MetricCard(
    metric: AdminMetricModel,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier, shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(metric.accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = metric.icon, contentDescription = null, tint = metric.accent, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                if (!metric.kicker.isNullOrBlank()) {
                    Pill(text = metric.kicker, icon = Icons.Default.Timeline, containerColor = metric.accent.copy(alpha = 0.12f), contentColor = metric.accent)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = metric.label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = metric.value, style = MaterialTheme.typography.headlineSmall)
                Text(text = metric.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun Pill(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clip(RoundedCornerShape(100)).background(containerColor).padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(12.dp))
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = contentColor)
    }
}

private fun percentage(value: Int, total: Int): Float {
    if (total <= 0) return 0f
    return (value.toFloat() / total.toFloat()).coerceIn(0f, 1f)
}
