package com.example.billionemotosappkt.desktop.admin.screens.planosparts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/** Card de um plano com métricas de assinantes e receita, e ação de editar. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun PlanoCard(
    stats: PlanoStats,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val plano = stats.plano
    val accent = planoNivelAccent(plano.nivel)
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PlanoPalette.cardBackground),
        border = BorderStroke(1.dp, PlanoPalette.hairline),
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            // Cabeçalho: nome + nível à esquerda, status à direita
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                    Text(
                        plano.nome,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    PlanoNivelBadge(nivel = plano.nivel, accent = accent)
                }
                PlanoStatusChip(ativo = plano.ativo)
            }

            Spacer(Modifier.height(12.dp))

            // Preço
            Row(verticalAlignment = Alignment.Bottom) {
                Text(formatPlanoValor(plano.valor), color = accent, fontSize = 26.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.width(4.dp))
                Text("/mês", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
            }

            Spacer(Modifier.height(14.dp))

            // Métricas: assinantes + receita
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniStat(
                    icon = Icons.Default.Group,
                    accent = accent,
                    value = stats.assinantes.toString(),
                    label = "assinantes",
                    modifier = Modifier.weight(1f),
                )
                MiniStat(
                    icon = Icons.Default.Payments,
                    accent = PlanoPalette.info,
                    value = formatCompactMoneyBRL(stats.receitaMensal),
                    label = "receita/mês",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(14.dp))

            // Participação na base
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Participação da base", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                Text(
                    "${(stats.shareAssinantes * 100).roundToInt()}%",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(6.dp))
            ShareBar(fraction = stats.shareAssinantes, accent = accent)
            if (stats.novosNoMes > 0) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "+${stats.novosNoMes} novos este mês",
                    color = PlanoPalette.accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(Modifier.height(14.dp))

            // Descrição (2 linhas no máximo)
            if (!plano.descricao.isNullOrBlank()) {
                Text(
                    plano.descricao!!,
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(12.dp))
            }

            // Tags (uma linha; excedentes viram "+N")
            if (plano.tags.isNotEmpty()) {
                val visibleTags = plano.tags.take(3)
                val extra = plano.tags.size - visibleTags.size
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxLines = 1,
                ) {
                    visibleTags.forEach { tag -> PlanoTagChip(text = tag) }
                    if (extra > 0) PlanoTagChip(text = "+$extra")
                }
            }

            // Empurra o rodapé para a base → todos os cards alinhados
            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(PlanoPalette.hairline),
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null, tint = accent, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Editar", color = accent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun MiniStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(15.dp))
                Text(
                    value,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun ShareBar(fraction: Double, accent: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(999.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0.02, 1.0).toFloat())
                .height(6.dp)
                .background(accent, RoundedCornerShape(999.dp)),
        )
    }
}

@Composable
private fun PlanoNivelBadge(nivel: String, accent: Color) {
    Box(
        modifier = Modifier
            .background(accent.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(nivel.uppercase(), color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun PlanoStatusChip(ativo: Boolean) {
    val color = if (ativo) PlanoPalette.accent else PlanoPalette.danger
    val label = if (ativo) "Ativo" else "Inativo"
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Box(modifier = Modifier.size(7.dp).background(color, RoundedCornerShape(999.dp)))
        Spacer(Modifier.width(6.dp))
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PlanoTagChip(text: String) {
    Box(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(text, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, maxLines = 1)
    }
}
