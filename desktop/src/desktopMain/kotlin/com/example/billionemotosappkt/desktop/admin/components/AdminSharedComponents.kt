package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.admin.model.AdminListItem
import com.example.billionemotosappkt.desktop.admin.model.AdminMetric

@Composable
fun Pill(
    text: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(containerColor)
            .border(1.dp, contentColor.copy(alpha = 0.16f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(12.dp))
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = contentColor)
    }
}

private fun greeting(): String = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
    in 0..11 -> "bom dia"
    in 12..17 -> "boa tarde"
    else -> "boa noite"
}

@Composable
fun AvatarCircle(
    label: String,
    size: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(Color(0xFF20E65B), Color(0xFFD4AF37)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label.take(2).uppercase(), color = Color.Black, fontWeight = FontWeight.Black)
    }
}

@Composable
fun DecorativeGlow(
    modifier: Modifier = Modifier,
    color: Color,
    size: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.35f))
    )
}

fun initials(value: String): String {
    return value
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "AD" }
}

@Composable
fun SidebarQuickMetrics(overview: DashboardOverviewData) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        QuickMetricPill("Contratos", overview.activeContracts.toString(), Icons.Default.CreditCard)
        QuickMetricPill("Vencendo 60d", overview.dueIn60Days.toString(), Icons.Default.Schedule)
        QuickMetricPill("Financeiro", overview.monthlyBilling, Icons.Default.AttachMoney)
    }
}

@Composable
fun SidebarActions() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Ações", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        ActionChip(text = "Atualizar painel", icon = Icons.Default.Refresh)
        ActionChip(text = "Alertas", icon = Icons.Default.WarningAmber)
    }
}

@Composable
fun SidebarAttention(overview: DashboardOverviewData) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Atenção", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        AttentionChip(text = "${overview.dueIn60Days} contrato(s) vencendo", icon = Icons.Default.Schedule)
        AttentionChip(text = "${overview.recentWorkOrders.size} OS recente(s)", icon = Icons.Default.Build)
    }
}

@Composable
fun AdminTopBar(
    authState: AuthUiState,
    lastUpdatedAt: String?,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    compact: Boolean = true,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
            Pill(
                text = "Painel admin",
                icon = Icons.Default.Groups,
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                contentColor = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Boa ${greeting()}, ${authState.userName}",
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
            OutlinedButton(onClick = onRefresh) { Text("Sincronizar") }
            Button(onClick = onLogout) { Text("Sair") }
        }
    }
}

@Composable
fun AdminHero(
    authState: AuthUiState,
    overview: DashboardOverviewData,
    lastUpdatedAt: String?,
    onRefresh: () -> Unit,
    compact: Boolean,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Bem-vindo, ${authState.userName}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text(text = "Painel administrativo inspirado no web.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill("Contratos ${overview.activeContracts}", Icons.Default.CreditCard, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), MaterialTheme.colorScheme.primary)
                Pill("Receita ${overview.monthlyReceived}", Icons.Default.AttachMoney, MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f), MaterialTheme.colorScheme.secondary)
                if (!lastUpdatedAt.isNullOrBlank()) {
                    Pill("Atualizado", Icons.Default.Timeline, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f), MaterialTheme.colorScheme.tertiary)
                }
            }
            if (compact) {
                Text(text = "Use o botão de sincronização para carregar os dados mais recentes.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onRefresh) { Text("Atualizar agora") }
                }
            }
        }
    }
}

@Composable
fun MetricGrid(
    metrics: List<AdminMetric>,
    columns: Int,
) {
    val rows = metrics.chunked(columns)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { metric -> DashboardMetricCard(metric = metric, modifier = Modifier.weight(1f)) }
                repeat(columns - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}
@Composable
fun AdminStatusStrip(
    overview: DashboardOverviewData,
    compact: Boolean,
) {
    val blocks = listOf(
        StatusBlock("Contratos", overview.activeContracts, overview.activeContracts + overview.dueIn60Days, Color(0xFF20E65B)),
        StatusBlock("OSs", overview.recentWorkOrders.size, maxOf(overview.recentWorkOrders.size, 1), Color(0xFF69D2FF)),
        StatusBlock("Financeiro", overview.monthlyReceived.length, maxOf(overview.monthlyReceived.length, 1), Color(0xFFFFB300)),
        StatusBlock("Alertas", overview.dueIn60Days, maxOf(overview.dueIn60Days, 1), Color(0xFFFF4A4A)),
    )
    if (compact) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { blocks.forEach { HealthCard(it) } }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            blocks.forEach { HealthCard(it, Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun HealthCard(block: StatusBlock, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f))) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(block.label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${block.value}/${block.total}", style = MaterialTheme.typography.titleMedium)
            HorizontalDivider()
        }
    }
}

@Composable
fun AdminOverviewSplit(
    overview: DashboardOverviewData,
    compact: Boolean,
) {
    if (compact) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SidebarQuickMetrics(overview)
            SidebarActions()
            SidebarAttention(overview)
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SidebarQuickMetrics(overview)
                SidebarActions()
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SidebarAttention(overview)
            }
        }
    }
}

@Composable
fun AdminListsSection(
    overview: DashboardOverviewData,
    columns: Int,
) {
    val contracts = overview.contractsDue.map {
        AdminListItem(
            title = it.title,
            subtitle = it.subtitle,
            badge = it.badge,
        )
    }
    val orders = overview.recentWorkOrders.map {
        AdminListItem(title = it.title, subtitle = it.subtitle, badge = it.badge)
    }
    val sections = listOf(
        contracts to "Contratos vencendo",
        orders to "OSs recentes",
    )
    if (columns == 1) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            sections.forEach { (items, title) -> ListCard(title, items) }
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ListCard("Contratos vencendo", contracts)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ListCard("OSs recentes", orders)
            }
        }
    }
}

@Composable
private fun ListCard(title: String, items: List<AdminListItem>) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (items.isEmpty()) {
                Text("Nenhum registro.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                items.take(5).forEach { item ->
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(item.title)
                            Text(item.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Pill(item.badge, Icons.Default.Timeline, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

private data class StatusBlock(val label: String, val value: Int, val total: Int, val tint: Color)

@Composable
private fun QuickMetricPill(label: String, value: String, icon: ImageVector) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Pill(label, icon, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f), MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ActionChip(text: String, icon: ImageVector) {
    Pill(text, icon, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), MaterialTheme.colorScheme.primary)
}

@Composable
private fun AttentionChip(text: String, icon: ImageVector) {
    Pill(text, icon, MaterialTheme.colorScheme.error.copy(alpha = 0.12f), MaterialTheme.colorScheme.error)
}
