package com.example.billionemotosappkt.desktop.admin.screens.planosparts

import androidx.compose.ui.graphics.Color

/**
 * Paleta e helpers visuais compartilhados pela seção de Planos do portal admin.
 *
 * Centralizado aqui para manter os cards pequenos e consistentes com o restante
 * do painel (mesmo verde de acento, mesmos tons de fundo escuro).
 */
internal object PlanoPalette {
    val accent = Color(0xFF20E65B)
    val info = Color(0xFF7DD3FC)
    val warning = Color(0xFFFFB300)
    val danger = Color(0xFFFF4A4A)
    val cardBackground = Color(0xFF0C120E)
    val hairline = Color.White.copy(alpha = 0.05f)
}

/** Cor de acento associada ao nível comercial do plano. */
internal fun planoNivelAccent(nivel: String): Color = when (nivel.trim().lowercase()) {
    "premium", "black", "diamante", "platinum" -> PlanoPalette.warning
    "plus", "gold", "avancado", "avançado" -> PlanoPalette.info
    else -> PlanoPalette.accent
}

/** Formata o valor do plano, tolerando tanto "199.9" quanto "R$ 199,90". */
internal fun formatPlanoValor(valor: String): String {
    val trimmed = valor.trim()
    if (trimmed.isEmpty()) return "R$ 0,00"
    if (trimmed.contains("R$", ignoreCase = true)) return trimmed
    val numeric = trimmed.replace(",", ".").toDoubleOrNull() ?: return trimmed
    val fixed = ((numeric * 100).toLong()).toString().padStart(3, '0')
    val cents = fixed.takeLast(2)
    val units = fixed.dropLast(2)
    val withThousands = units.reversed().chunked(3).joinToString(".").reversed()
    return "R$ $withThousands,$cents"
}
