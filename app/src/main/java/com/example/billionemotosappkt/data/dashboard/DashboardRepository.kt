package com.example.billionemotosappkt.data.dashboard

import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ApiException
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.ContratoStatus
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import com.example.billionemotosappkt.shared.api.ListManutencoesQuery
import com.example.billionemotosappkt.shared.api.ListPagamentosQuery
import com.example.billionemotosappkt.shared.api.ListTicketsQuery
import com.example.billionemotosappkt.shared.api.ManutencaoResponse
import com.example.billionemotosappkt.shared.api.PagamentoResponse
import com.example.billionemotosappkt.shared.api.PagedResponse
import com.example.billionemotosappkt.shared.api.RelatorioResumoResponse
import com.example.billionemotosappkt.shared.api.TicketResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

data class DashboardOverviewData(
    val resumo: RelatorioResumoResponse,
    val contratosVencendo: List<ContratoResponse>,
    val pagamentosRecentes: List<PagamentoResponse>,
    val manutencoesRecentes: List<ManutencaoResponse>,
    val ticketsRecentes: List<TicketResponse>,
)

class DashboardRepository(
    private val api: BillioneMotosApi,
) {
    suspend fun loadOverview(): DashboardOverviewData = coroutineScope {
        println("[DashboardRepository] loadOverview started")
        val resumo = async { api.relatorios.resumo() }
        val contratosAtivos = async {
            println("[DashboardRepository] loading contratos ativos")
            loadPagedOrEmpty("contratos ativos") {
                api.contratos.list(
                    ListContratosQuery(
                        status = ContratoStatus.ATIVO,
                        includeRelations = true,
                        page = 1,
                        limit = 50,
                    ),
                )
            }
        }
        val contratosInadimplentes = async {
            println("[DashboardRepository] loading contratos inadimplentes")
            loadPagedOrEmpty("contratos inadimplentes") {
                api.contratos.list(
                    ListContratosQuery(
                        status = ContratoStatus.INADIMPLENTE,
                        includeRelations = true,
                        page = 1,
                        limit = 50,
                    ),
                )
            }
        }
        val pagamentos = async {
            println("[DashboardRepository] loading pagamentos recentes")
            loadPagedOrEmpty("pagamentos recentes") {
                api.pagamentos.list(
                    ListPagamentosQuery(
                        includeRelations = true,
                        page = 1,
                        limit = 10,
                    ),
                )
            }
        }
        val manutencoes = async {
            println("[DashboardRepository] loading manutencoes recentes")
            loadPagedOrEmpty("manutencoes recentes") {
                api.manutencoes.list(
                    ListManutencoesQuery(
                        page = 1,
                        limit = 10,
                    ),
                )
            }
        }
        val tickets = async {
            println("[DashboardRepository] loading tickets recentes")
            loadPagedOrEmpty("tickets recentes") {
                api.tickets.list(
                    ListTicketsQuery(
                        page = 1,
                        limit = 10,
                    ),
                )
            }
        }

        val contratosVencendo = mergeUpcomingContracts(
            contratosAtivos = contratosAtivos.await().items,
            contratosInadimplentes = contratosInadimplentes.await().items,
            limiteDias = 60,
        )

        DashboardOverviewData(
            resumo = resumo.await(),
            contratosVencendo = contratosVencendo,
            pagamentosRecentes = pagamentos.await().items,
            manutencoesRecentes = manutencoes.await().items,
            ticketsRecentes = tickets.await().items,
        )
        .also {
            println(
                "[DashboardRepository] loadOverview completed contratos=${it.contratosVencendo.size} pagamentos=${it.pagamentosRecentes.size} manutencoes=${it.manutencoesRecentes.size} tickets=${it.ticketsRecentes.size}"
            )
        }
    }

    private fun mergeUpcomingContracts(
        contratosAtivos: List<ContratoResponse>,
        contratosInadimplentes: List<ContratoResponse>,
        limiteDias: Int,
    ): List<ContratoResponse> {
        val unique = LinkedHashMap<String, ContratoResponse>()
        (contratosAtivos + contratosInadimplentes).forEach { contrato ->
            val dias = daysUntil(contrato.dataFim) ?: return@forEach
            if (dias in 0..limiteDias) {
                unique[contrato.id] = contrato
            }
        }
        return unique.values.sortedBy { daysUntil(it.dataFim) ?: Int.MAX_VALUE }
    }

    private fun daysUntil(dateValue: String?): Int? {
        val parsed = parseDate(dateValue) ?: return null
        val today = startOfDay(Calendar.getInstance())
        val target = startOfDay(Calendar.getInstance().apply { time = parsed })
        val diff = target.timeInMillis - today.timeInMillis
        return ceil(diff / MILLIS_PER_DAY.toDouble()).toInt()
    }

    private fun parseDate(value: String?): Date? {
        if (value.isNullOrBlank()) return null
        val normalized = value.take(10)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            isLenient = false
        }
        return runCatching { format.parse(normalized) }.getOrNull()
    }

    private fun startOfDay(calendar: Calendar): Calendar {
        return calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    companion object {
        private const val MILLIS_PER_DAY = 86_400_000L
    }

    private suspend inline fun <reified T> loadPagedOrEmpty(
        label: String,
        block: suspend () -> PagedResponse<T>,
    ): PagedResponse<T> {
        return runCatching { block() }.getOrElse { error ->
            if (error is ApiException && error.statusCode == 403) {
                println("[DashboardRepository] $label blocked by 403: ${error.message}")
                PagedResponse(items = emptyList(), total = 0, page = 1, limit = 20)
            } else {
                throw error
            }
        }
    }
}
