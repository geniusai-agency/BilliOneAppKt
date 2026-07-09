package com.example.billionemotosappkt.desktop.admin.screens.planosparts

import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.ContratoStatus
import com.example.billionemotosappkt.shared.api.PlanoResponse
import com.example.billionemotosappkt.shared.utils.yearMonthOf

/**
 * Cálculo das métricas da seção de Planos a partir de dados reais.
 *
 * Fontes: catálogo de planos (`/planos`) + contratos (`/contratos`). Tudo é
 * derivado de contratos ATIVOS, então os números refletem a base real de
 * assinantes e a receita recorrente (MRR). O lucro é rotulado como estimativa,
 * pois a API não expõe o custo por plano — usamos uma margem explícita.
 */

/** Margem usada para estimar o lucro (o backend não fornece custo por plano). */
internal const val MARGEM_LUCRO_ESTIMADA = 0.30

/** Estatísticas de um plano individual. */
internal data class PlanoStats(
    val plano: PlanoResponse,
    val assinantes: Int,
    val receitaMensal: Double,
    val shareAssinantes: Double,
    val novosNoMes: Int,
)

/** Visão agregada de todos os planos. */
internal data class PlanosOverview(
    val totalAssinantes: Int,
    val receitaMensal: Double,
    val projecaoAnual: Double,
    val lucroEstimado: Double,
    val margem: Double,
    val novosNoMes: Int,
    val novosMesAnterior: Int,
    val crescimentoPct: Double?,
    val ticketMedio: Double,
    val statsPorPlano: List<PlanoStats>,
) {
    companion object {
        val EMPTY = PlanosOverview(0, 0.0, 0.0, 0.0, MARGEM_LUCRO_ESTIMADA, 0, 0, null, 0.0, emptyList())
    }
}

/** Converte "R$ 1.234,56", "199,90", "199.90" ou "199" em Double. */
internal fun parseMoney(raw: String?): Double {
    if (raw.isNullOrBlank()) return 0.0
    val cleaned = raw.filter { it.isDigit() || it == ',' || it == '.' || it == '-' }
    if (cleaned.isBlank()) return 0.0
    val normalized = when {
        cleaned.contains('.') && cleaned.contains(',') ->
            cleaned.replace(".", "").replace(",", ".")
        cleaned.contains(',') -> cleaned.replace(",", ".")
        else -> cleaned
    }
    return normalized.toDoubleOrNull() ?: 0.0
}

/** Formata um valor em reais no padrão "R$ 1.234,56". */
internal fun formatMoneyBRL(value: Double): String {
    val negative = value < 0
    val cents = kotlin.math.round(kotlin.math.abs(value) * 100).toLong()
    val digits = cents.toString().padStart(3, '0')
    val decimal = digits.takeLast(2)
    val integer = digits.dropLast(2)
    val withThousands = integer.reversed().chunked(3).joinToString(".").reversed()
    val prefix = if (negative) "-R$ " else "R$ "
    return "$prefix$withThousands,$decimal"
}

/** Formata valores grandes de forma compacta: 1.2K, 3.4M. */
internal fun formatCompactMoneyBRL(value: Double): String = when {
    kotlin.math.abs(value) >= 1_000_000 -> "R$ ${trim1(value / 1_000_000)}M"
    kotlin.math.abs(value) >= 1_000 -> "R$ ${trim1(value / 1_000)}K"
    else -> formatMoneyBRL(value)
}

private fun trim1(v: Double): String {
    val rounded = kotlin.math.round(v * 10) / 10.0
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString().replace('.', ',')
}

private fun contratoYearMonth(contrato: ContratoResponse): String? {
    val source = contrato.createdAt?.takeIf { it.isNotBlank() } ?: contrato.dataInicio
    return yearMonthOf(source)
}

/**
 * Monta a visão agregada. `yearMonthNow`/`yearMonthPrev` no formato "yyyy-MM"
 * são injetados pela tela para manter esta função pura.
 */
internal fun buildPlanosOverview(
    planos: List<PlanoResponse>,
    contratos: List<ContratoResponse>,
    yearMonthNow: String,
    yearMonthPrev: String,
): PlanosOverview {
    val ativos = contratos.filter { it.status == ContratoStatus.ATIVO }
    val totalAssinantes = ativos.size

    fun contratosDoPlano(plano: PlanoResponse) = ativos.filter {
        it.planoId == plano.id || it.planoSnapshotNome.equals(plano.nome, ignoreCase = true)
    }

    val stats = planos.map { plano ->
        val doPlano = contratosDoPlano(plano)
        val receita = doPlano.sumOf { parseMoney(it.valorParcela).takeIf { v -> v > 0 } ?: parseMoney(plano.valor) }
        val novos = doPlano.count { contratoYearMonth(it) == yearMonthNow }
        PlanoStats(
            plano = plano,
            assinantes = doPlano.size,
            receitaMensal = receita,
            shareAssinantes = if (totalAssinantes > 0) doPlano.size.toDouble() / totalAssinantes else 0.0,
            novosNoMes = novos,
        )
    }.sortedByDescending { it.assinantes }

    val receitaMensal = stats.sumOf { it.receitaMensal }
    val novosNoMes = ativos.count { contratoYearMonth(it) == yearMonthNow }
    val novosMesAnterior = ativos.count { contratoYearMonth(it) == yearMonthPrev }
    val crescimento = when {
        novosMesAnterior > 0 -> (novosNoMes - novosMesAnterior).toDouble() / novosMesAnterior
        novosNoMes > 0 -> 1.0
        else -> null
    }

    return PlanosOverview(
        totalAssinantes = totalAssinantes,
        receitaMensal = receitaMensal,
        projecaoAnual = receitaMensal * 12,
        lucroEstimado = receitaMensal * MARGEM_LUCRO_ESTIMADA,
        margem = MARGEM_LUCRO_ESTIMADA,
        novosNoMes = novosNoMes,
        novosMesAnterior = novosMesAnterior,
        crescimentoPct = crescimento,
        ticketMedio = if (totalAssinantes > 0) receitaMensal / totalAssinantes else 0.0,
        statsPorPlano = stats,
    )
}
