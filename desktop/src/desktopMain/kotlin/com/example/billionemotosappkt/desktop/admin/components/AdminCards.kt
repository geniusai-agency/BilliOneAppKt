package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.model.AdminChartPoint
import com.example.billionemotosappkt.desktop.admin.model.AdminDashboardSnapshot
import com.example.billionemotosappkt.desktop.admin.model.AdminListItem
import com.example.billionemotosappkt.desktop.admin.model.AdminMetric

@Composable
fun DashboardSection(
    snapshot: AdminDashboardSnapshot,
    compact: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ManagementHeader()
        MetricsGrid(snapshot = snapshot, compact = compact)
        DueContractsPanel(snapshot = snapshot)
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val gap = 18.dp
            val panelWidth = (maxWidth - gap) / 2
            if (!compact && panelWidth >= 360.dp) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(gap),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RecentPanel(
                        title = "OSs Recentes",
                        subtitle = "Últimas ordens de serviço registradas",
                        items = snapshot.recentWorkOrders,
                        modifier = Modifier.width(panelWidth),
                    )
                    FinancePanel(
                        title = "Resumo Financeiro",
                        subtitle = "Faturamento dos últimos 6 meses",
                        points = snapshot.financeMonths,
                        modifier = Modifier.width(panelWidth),
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                    RecentPanel(
                        title = "OSs Recentes",
                        subtitle = "Últimas ordens de serviço registradas",
                        items = snapshot.recentWorkOrders,
                    )
                    FinancePanel(
                        title = "Resumo Financeiro",
                        subtitle = "Faturamento dos últimos 6 meses",
                        points = snapshot.financeMonths,
                    )
                }
            }
        }
        if (compact) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                InsightCard(
                    title = "Clientes",
                    description = "Fluxo de aprovação, documentos e status por cidade.",
                )
                InsightCard(
                    title = "Operação",
                    description = "Motos, manutenção e contratos concentrados em uma visão só.",
                )
            }
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val gap = 18.dp
                val cardWidth = (maxWidth - gap) / 2
                if (cardWidth >= 320.dp) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(gap),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        InsightCard(
                            title = "Clientes",
                            description = "Fluxo de aprovação, documentos e status por cidade.",
                            modifier = Modifier.width(cardWidth),
                        )
                        InsightCard(
                            title = "Operação",
                            description = "Motos, manutenção e contratos concentrados em uma visão só.",
                            modifier = Modifier.width(cardWidth),
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        InsightCard(
                            title = "Clientes",
                            description = "Fluxo de aprovação, documentos e status por cidade.",
                        )
                        InsightCard(
                            title = "Operação",
                            description = "Motos, manutenção e contratos concentrados em uma visão só.",
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricsGrid(
    snapshot: AdminDashboardSnapshot,
    compact: Boolean,
) {
    val metrics = listOf(
        AdminMetric(
            "Contratos ativos",
            snapshot.activeContracts.toString(),
            Icons.Default.FileCopy,
            Color(0xFF20E65B)
        ),
        AdminMetric(
            "Vencendo em 60 dias",
            snapshot.dueIn60Days.toString(),
            Icons.Default.NotificationsNone,
            Color(0xFFFFB300)
        ),
        AdminMetric(
            "Faturamento do mês",
            snapshot.monthlyBilling,
            Icons.Default.AttachMoney,
            Color(0xFF20E65B)
        ),
        AdminMetric(
            "Recebido no mês",
            snapshot.monthlyReceived,
            Icons.Default.Dashboard,
            Color(0xFF20E65B)
        ),
        AdminMetric("Em aberto", snapshot.openItems, Icons.Default.Analytics, Color(0xFFFF4A4A)),
    )

    if (compact) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            metrics.forEach { metric ->
                MetricCard(metric = metric, modifier = Modifier.fillMaxWidth())
            }
        }
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val gap = 12.dp
        val cardWidth = maxWidth
        val usable = cardWidth - gap * 4
        val itemWidth = usable / 5

        if (itemWidth < 170.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                metrics.forEach { metric ->
                    DashboardMetricCard(metric = metric, modifier = Modifier.fillMaxWidth())
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(gap),
                modifier = Modifier.fillMaxWidth()
            ) {
                metrics.forEach { metric ->
                    DashboardMetricCard(metric = metric, modifier = Modifier.width(itemWidth))
                }
            }
        }
    }
}

@Composable
fun MetricCard(metric: AdminMetric, modifier: Modifier) {
    DashboardMetricCard(metric = metric, modifier = modifier)
}

@Composable
fun DashboardMetricCard(
    metric: AdminMetric,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.height(88.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1412)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = metric.title.uppercase(),
                    color = Color.White.copy(alpha = 0.52f),
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp,
                )
                Text(
                    text = metric.value,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                )
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(metric.tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = metric.icon,
                    contentDescription = null,
                    tint = metric.tint,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
fun DueContractsPanel(snapshot: AdminDashboardSnapshot) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Contratos Próximos do Vencimento",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Próximos 60 dias",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 12.sp,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFF111915))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "${snapshot.dueIn60Days} contrato(s)",
                        color = Color.White.copy(alpha = 0.92f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            if (snapshot.contractsDue.isEmpty()) {
                EmptyState("Nenhum contrato vencendo nos próximos 60 dias.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    snapshot.contractsDue.forEach { item ->
                        SimpleListItem(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun RecentPanel(
    title: String,
    subtitle: String,
    items: List<AdminListItem>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp)
            }
            if (items.isEmpty()) {
                EmptyState("Nenhum registro encontrado.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items.forEach { item ->
                        SimpleListItem(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun FinancePanel(
    title: String,
    subtitle: String,
    points: List<AdminChartPoint>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp)
            }
            FinanceChart(points = points)
        }
    }
}

@Composable
fun FinanceChart(points: List<AdminChartPoint>) {
    val max = points.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        points.forEach { point ->
            val fraction = (point.value.toFloat() / max.toFloat()).coerceIn(0.08f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = point.label,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    modifier = Modifier.width(50.dp),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFF0D1410)),
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(18.dp)) {
                        Box(
                            modifier = Modifier
                                .width(maxWidth * fraction)
                                .height(18.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF20E65B), Color(0xFFD4AF37)),
                                    ),
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InsightCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.56f),
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
        }
    }
}

@Composable
fun SectionPlaceholder(
    title: String,
    subtitle: String,
    body: String,
    chips: List<String>,
    stats: List<String>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = title, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text(text = subtitle, color = Color.White.copy(alpha = 0.56f), fontSize = 14.sp)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.06f)
            ),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = body,
                    color = Color.White.copy(alpha = 0.74f),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    chips.forEach { chip ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFF101814))
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = 0.06f),
                                    RoundedCornerShape(999.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = chip,
                                color = Color.White.copy(alpha = 0.84f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                if (stats.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        stats.forEach { stat ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF111614)),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color.White.copy(alpha = 0.04f)
                                ),
                            ) {
                                Text(
                                    text = stat,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 12.dp
                                    ),
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
fun SectionHeader(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.56f),
            fontSize = 14.sp,
        )
    }
}

@Composable
fun SectionShell(
    title: String,
    subtitle: String,
    body: String,
    chips: List<String>,
    stats: List<String>,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        SectionHeader(title = title, subtitle = subtitle)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.06f)
            ),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = body,
                    color = Color.White.copy(alpha = 0.74f),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    chips.forEach { chip ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFF101814))
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = 0.06f),
                                    RoundedCornerShape(999.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = chip,
                                color = Color.White.copy(alpha = 0.84f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                if (stats.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        stats.forEach { stat ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF111614)),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color.White.copy(alpha = 0.04f)
                                ),
                            ) {
                                Text(
                                    text = stat,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 12.dp
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
        content()
    }
}

@Composable
fun AdminSectionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1412)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color.White.copy(alpha = 0.54f), fontSize = 11.sp)
                SpacerV(8)
                Text(value, color = accent, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ChipRow(chips: List<String>) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEach { chip ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFF101814))
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = chip,
                    color = Color.White.copy(alpha = 0.84f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
fun DetailGrid(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111614)),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.04f)
                ),
            ) {
                Text(
                    text = item,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun SimpleListItem(item: AdminListItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF111614))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.subtitle,
                color = Color.White.copy(alpha = 0.52f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFF0C1510))
                .border(1.dp, Color(0xFF20E65B).copy(alpha = 0.24f), RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
            Text(
                text = item.badge,
                color = Color(0xFF20E65B),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun SpacerV(height: Int) {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(height.dp))
}
