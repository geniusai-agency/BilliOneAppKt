package com.example.billionemotosappkt.desktop.admin.screens.planosparts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.shared.api.PlanoResponse

private val CARD_HEIGHT = 460.dp

/**
 * Grade responsiva de planos: colunas de largura igual que preenchem toda a
 * largura disponível, e cards de altura fixa para um alinhamento limpo (sem
 * bases irregulares). O número de colunas se adapta à largura da janela.
 */
@Composable
internal fun PlanosGrid(
    stats: List<PlanoStats>,
    onEdit: (PlanoResponse) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columns = when {
            maxWidth >= 1500.dp -> 4
            maxWidth >= 1080.dp -> 3
            maxWidth >= 720.dp -> 2
            else -> 1
        }
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            stats.chunked(columns).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    rowItems.forEach { planoStats ->
                        PlanoCard(
                            stats = planoStats,
                            onEdit = { onEdit(planoStats.plano) },
                            modifier = Modifier.weight(1f).height(CARD_HEIGHT),
                        )
                    }
                    // Preenche a última linha para manter a largura dos cards.
                    repeat(columns - rowItems.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
