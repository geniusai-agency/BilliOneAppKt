package com.example.billionemotosappkt.desktop.admin.screens.planosparts

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private data class Metric(
    val icon: ImageVector,
    val accent: Color,
    val label: String,
    val value: String,
    val caption: String,
)

private val METRIC_HEIGHT = 156.dp

/** Linha de indicadores-chave da operação de planos, preenchendo a largura. */
@Composable
internal fun PlanosOverviewRow(
    overview: PlanosOverview,
    modifier: Modifier = Modifier,
) {
    val metrics = listOf(
        Metric(
            Icons.Default.Groups, PlanoPalette.accent,
            "Assinantes ativos", overview.totalAssinantes.toString(), "Contratos em vigência",
        ),
        Metric(
            Icons.Default.AttachMoney, PlanoPalette.info,
            "Receita mensal (MRR)", formatCompactMoneyBRL(overview.receitaMensal), "Ticket médio ${formatMoneyBRL(overview.ticketMedio)}",
        ),
        Metric(
            Icons.Default.ShowChart, PlanoPalette.warning,
            "Projeção anual", formatCompactMoneyBRL(overview.projecaoAnual), "MRR × 12 meses",
        ),
        Metric(
            Icons.Default.Savings, Color(0xFFB794F6),
            "Lucro estimado / mês", formatCompactMoneyBRL(overview.lucroEstimado), "Margem de ${(overview.margem * 100).roundToInt()}% (estimativa)",
        ),
        Metric(
            growthIcon(overview.crescimentoPct), growthColor(overview.crescimentoPct),
            "Crescimento (mês)", growthLabel(overview.crescimentoPct),
            "${overview.novosNoMes} novos vs ${overview.novosMesAnterior} no mês anterior",
        ),
    )

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columns = when {
            maxWidth >= 1400.dp -> 5
            maxWidth >= 1080.dp -> 3
            maxWidth >= 680.dp -> 2
            else -> 1
        }
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            metrics.chunked(columns).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    rowItems.forEach { metric ->
                        MetricCard(metric = metric, modifier = Modifier.weight(1f).height(METRIC_HEIGHT))
                    }
                    repeat(columns - rowItems.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(metric: Metric, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PlanoPalette.cardBackground),
        border = BorderStroke(1.dp, PlanoPalette.hairline),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(34.dp).background(metric.accent.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(metric.icon, null, tint = metric.accent, modifier = Modifier.size(18.dp))
                }
                Text(
                    metric.label,
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                metric.value,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                metric.caption,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun growthLabel(pct: Double?): String = when {
    pct == null -> "—"
    else -> {
        val v = (pct * 100).roundToInt()
        if (v > 0) "+$v%" else "$v%"
    }
}

private fun growthColor(pct: Double?): Color = when {
    pct == null -> Color.White.copy(alpha = 0.5f)
    pct > 0 -> PlanoPalette.accent
    pct < 0 -> PlanoPalette.danger
    else -> PlanoPalette.info
}

private fun growthIcon(pct: Double?): ImageVector = when {
    pct == null || pct == 0.0 -> Icons.AutoMirrored.Filled.TrendingFlat
    pct > 0 -> Icons.AutoMirrored.Filled.TrendingUp
    else -> Icons.AutoMirrored.Filled.TrendingDown
}
