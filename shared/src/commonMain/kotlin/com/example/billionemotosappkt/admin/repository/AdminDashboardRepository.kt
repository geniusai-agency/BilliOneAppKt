package com.example.billionemotosappkt.desktop.admin.repository

import com.example.billionemotosappkt.desktop.admin.model.AdminChartPoint
import com.example.billionemotosappkt.desktop.admin.model.AdminDashboardSnapshot
import com.example.billionemotosappkt.desktop.admin.model.AdminListItem
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.ContratoStatus
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import com.example.billionemotosappkt.shared.api.ListManutencoesQuery
import com.example.billionemotosappkt.shared.api.ListTicketsQuery
import com.example.billionemotosappkt.shared.api.ManutencaoResponse
import com.example.billionemotosappkt.shared.api.PagedResponse
import com.example.billionemotosappkt.shared.api.TicketOrigem
import com.example.billionemotosappkt.shared.api.TicketStatus
import com.example.billionemotosappkt.shared.utils.formatBRL
import com.example.billionemotosappkt.shared.utils.formatBrDate
import com.example.billionemotosappkt.shared.utils.getDaysUntil

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
            .filter { (getDaysUntil(it.dataFim) ?: Int.MAX_VALUE) in 0..60 }
            .sortedBy { getDaysUntil(it.dataFim) ?: Int.MAX_VALUE }

        val ticketsNovos = runCatching {
            api.tickets.list(
                ListTicketsQuery(
                    status = TicketStatus.ABERTO,
                    origem = TicketOrigem.CLIENTE,
                    limit = 1,
                ),
            ).total
        }.getOrDefault(0)

        return AdminDashboardSnapshot(
            activeContracts = resumo.contratos.ativos,
            dueIn60Days = contratosVencendo.size,
            monthlyBilling = formatBRL(resumo.financeiro.totalRecebido),
            monthlyReceived = formatBRL(resumo.financeiro.totalRecebido),
            openItems = formatBRL(resumo.financeiro.totalDespesas),
            contractsDue = contratosVencendo.take(5).map { it.toListItem() },
            recentWorkOrders = manutencoes.items.map { it.toListItem() },
            financeMonths = defaultFinanceMonths(resumo.financeiro.totalRecebido),
            ticketsNovos = ticketsNovos,
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
        val fim = formatBrDate(dataFim) ?: "sem vencimento"
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

    private fun defaultFinanceMonths(currentValue: Int): List<AdminChartPoint> {
        val labels = listOf("Fev/26", "Mar/26", "Abr/26", "Mai/26", "Jun/26", "Jul/26")
        return labels.mapIndexed { index, label ->
            AdminChartPoint(label, if (index == labels.lastIndex) currentValue else 0)
        }
    }
}
