package com.example.billionemotosappkt.desktop.admin.clientes.repository

import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.toListItem
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.ClienteResponse
import com.example.billionemotosappkt.shared.api.ListClientesQuery

data class ClientesLoadResult(
    val clientes: List<ClienteListItem>,
    val isFallbackData: Boolean,
)

class ClientesRepository(
    private val api: BillioneMotosApi,
) {
    suspend fun loadClientes(): ClientesLoadResult {
        return runCatching {
            val response = api.clientes.list(
                ListClientesQuery(
                    page = 1,
                    limit = 200,
                ),
            )
            val clientes = response.items.map { it.toListItem() }
            ClientesLoadResult(
                clientes = if (clientes.isNotEmpty()) clientes else sampleClientes(),
                isFallbackData = clientes.isEmpty(),
            )
        }.getOrElse {
            ClientesLoadResult(
                clientes = sampleClientes(),
                isFallbackData = true,
            )
        }
    }

    private fun sampleClientes(): List<ClienteListItem> {
        return listOf(
            ClienteResponse(
                id = "cli-001",
                nome = "João Silva Santos",
                cpf = "123.456.789-01",
                telefone = "(11) 98765-4321",
                email = "joao@billione.com",
                cidade = "São Paulo",
                estado = "SP",
                statusAprovacao = ClienteAprovacaoStatus.APROVADO,
            ),
            ClienteResponse(
                id = "cli-002",
                nome = "Marina Costa Oliveira",
                cpf = "234.567.890-12",
                telefone = "(11) 97654-3210",
                email = "marina@billione.com",
                cidade = "São Paulo",
                estado = "SP",
                statusAprovacao = ClienteAprovacaoStatus.EM_ANALISE,
            ),
            ClienteResponse(
                id = "cli-003",
                nome = "Carlos Mendes Pereira",
                cpf = "345.678.901-23",
                telefone = "(11) 96543-2109",
                email = "carlos@billione.com",
                cidade = "São Paulo",
                estado = "SP",
                statusAprovacao = ClienteAprovacaoStatus.PENDENTE,
            ),
            ClienteResponse(
                id = "cli-004",
                nome = "Ana Paula Ferreira",
                cpf = "456.789.012-34",
                telefone = "(11) 95432-1098",
                email = "ana@billione.com",
                cidade = "São Paulo",
                estado = "SP",
                statusAprovacao = ClienteAprovacaoStatus.APROVADO,
            ),
            ClienteResponse(
                id = "cli-005",
                nome = "Lucas Andrade Souza",
                cpf = "567.890.123-45",
                telefone = "(11) 94321-0987",
                email = "lucas@billione.com",
                cidade = "São Paulo",
                estado = "SP",
                statusAprovacao = ClienteAprovacaoStatus.APROVADO,
            ),
            ClienteResponse(
                id = "cli-006",
                nome = "Ricardo Gomes Lima",
                cpf = "678.901.234-56",
                telefone = "(21) 93210-9876",
                email = "ricardo@billione.com",
                cidade = "Rio de Janeiro",
                estado = "RJ",
                statusAprovacao = ClienteAprovacaoStatus.APROVADO_COM_RESSALVA,
            ),
            ClienteResponse(
                id = "cli-007",
                nome = "Beatriz Almeida Rocha",
                cpf = "789.012.345-67",
                telefone = "(11) 92109-8765",
                email = "beatriz@billione.com",
                cidade = "São Paulo",
                estado = "SP",
                statusAprovacao = ClienteAprovacaoStatus.APROVADO,
            ),
            ClienteResponse(
                id = "cli-008",
                nome = "Pedro Henrique Martins",
                cpf = "890.123.456-78",
                telefone = "(11) 91098-7654",
                email = "pedro@billione.com",
                cidade = "São Paulo",
                estado = "SP",
                statusAprovacao = ClienteAprovacaoStatus.REPROVADO,
            ),
            ClienteResponse(
                id = "cli-009",
                nome = "João Pedro Amaral Santana",
                cpf = "075.107.835-26",
                telefone = "55778878363",
                email = "joaopedro@billione.com",
                cidade = "Vitória da Conquista",
                estado = "BA",
                statusAprovacao = ClienteAprovacaoStatus.PENDENTE,
            ),
        ).map { it.toListItem() }
    }
}
