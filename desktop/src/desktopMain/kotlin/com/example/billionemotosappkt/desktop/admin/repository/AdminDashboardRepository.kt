package com.example.billionemotosappkt.desktop.admin.repository

import com.example.billionemotosappkt.desktop.admin.model.AdminChartPoint
import com.example.billionemotosappkt.desktop.admin.model.AdminDashboardSnapshot
import com.example.billionemotosappkt.desktop.admin.model.AdminListItem
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.ContratoStatus
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import com.example.billionemotosappkt.shared.api.ListManutencoesQuery
import com.example.billionemotosappkt.shared.api.ManutencaoResponse
import com.example.billionemotosappkt.shared.api.PagedResponse
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

class AdminDashboardRepository(
    private val api: BillioneMotosApi,
) {
    suspend fun loadSnapshot(): AdminDashboardSnapshot {
        val resumo = api.relatorios.resumo()
        val contratosAtivos = loadPagedOrEmpty {
            api.contratos.list(
                ListContratosQuery(
                    status = ContratoStatus.ATIVO,
                    includeRelations = true,
                    page = 1,
                    limit = 50,
                ),
            )
        }
        val contratosInadimplentes = loadPagedOrEmpty {
            api.contratos.list(
                ListContratosQuery(
                    status = ContratoStatus.INADIMPLENTE,
                    includeRelations = true,
                    page = 1,
                    limit = 50,
                ),
            )
        }
        val manutencoes = loadPagedOrEmpty {
            api.manutencoes.list(
                ListManutencoesQuery(
                    page = 1,
                    limit = 5,
                ),
            )
        }
        val contratosVencendo = (contratosAtivos.items + contratosInadimplentes.items)
            .distinctBy { it.id }
            .filter { (daysUntil(it.dataFim) ?: Int.MAX_VALUE) in 0..60 }
            .sortedBy { daysUntil(it.dataFim) ?: Int.MAX_VALUE }

        return AdminDashboardSnapshot(
            activeContracts = resumo.contratos.ativos,
            dueIn60Days = contratosVencendo.size,
            monthlyBilling = formatCurrency(resumo.financeiro.totalRecebido),
            monthlyReceived = formatCurrency(resumo.financeiro.totalRecebido),
            openItems = formatCurrency(resumo.financeiro.totalDespesas),
            contractsDue = contratosVencendo.take(5).map { it.toListItem() },
            recentWorkOrders = manutencoes.items.map { it.toListItem() },
            financeMonths = defaultFinanceMonths(resumo.financeiro.totalRecebido),
        )
    }

    private suspend fun <T> loadPagedOrEmpty(block: suspend () -> PagedResponse<T>): PagedResponse<T> {
        return runCatching { block() }.getOrElse {
            PagedResponse(items = emptyList(), total = 0, page = 1, limit = 20)
        }
    }

    private fun ContratoResponse.toListItem(): AdminListItem {
        val clienteNome = cliente?.nome?.takeIf { it.isNotBlank() } ?: clienteId.take(8)
        val motoNome = moto?.modelo?.takeIf { it.isNotBlank() } ?: motoId.take(8)
        val fim = formatDate(dataFim) ?: "sem vencimento"
        return AdminListItem(
            title = clienteNome,
            subtitle = "$motoNome • $planoSnapshotNome",
            badge = fim,
        )
    }

    private fun ManutencaoResponse.toListItem(): AdminListItem {
        val motoNome = moto?.modelo?.takeIf { it.isNotBlank() } ?: motoId.take(8)
        return AdminListItem(
            title = "OS #$numeroOs",
            subtitle = "$motoNome • ${tipo.name}",
            badge = status.name,
        )
    }

    private fun daysUntil(dateValue: String?): Int? {
        val parsed = parseDate(dateValue) ?: return null
        val today = startOfDay(Calendar.getInstance())
        val target = startOfDay(Calendar.getInstance().apply { time = parsed })
        return ceil((target.timeInMillis - today.timeInMillis) / MILLIS_PER_DAY.toDouble()).toInt()
    }

    private fun parseDate(value: String?): Date? {
        if (value.isNullOrBlank()) return null
        return runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }.parse(value.take(10))
        }.getOrNull()
    }

    private fun startOfDay(calendar: Calendar): Calendar {
        return calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun formatDate(value: String?): String? {
        val parsed = parseDate(value) ?: return null
        return SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(parsed)
    }

    private fun formatCurrency(value: Int): String {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value)
    }

    private fun defaultFinanceMonths(currentValue: Int): List<AdminChartPoint> {
        val labels = listOf("Fev/26", "Mar/26", "Abr/26", "Mai/26", "Jun/26", "Jul/26")
        return labels.mapIndexed { index, label ->
            AdminChartPoint(label, if (index == labels.lastIndex) currentValue else 0)
        }
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
