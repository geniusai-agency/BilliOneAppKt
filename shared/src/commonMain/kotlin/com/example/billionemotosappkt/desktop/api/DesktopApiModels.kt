package com.example.billionemotosappkt.desktop.api

enum class ContratoStatus { ATIVO, INADIMPLENTE, ENCERRADO, CANCELADO, PENDENTE_ASSINATURA }

enum class PagamentoStatus { CONFIRMADO, PENDENTE, CANCELADO, ESTORNADO }

enum class ManutencaoStatus { ABERTA, EM_ANDAMENTO, CONCLUIDA, CANCELADA }

enum class TicketStatus { ABERTO, EM_ANALISE, AGUARDANDO_CLIENTE, RESOLVIDO, CANCELADO }

enum class TicketPrioridade { BAIXA, MEDIA, ALTA, URGENTE }

data class ClienteResponse(
    val id: String,
    val nome: String,
)

data class MotoResponse(
    val id: String,
    val modelo: String? = null,
    val marca: String? = null,
)

data class ContratoResponse(
    val id: String,
    val cliente: ClienteResponse? = null,
    val moto: MotoResponse? = null,
    val planoSnapshotNome: String = "",
    val planoSnapshotNivel: String = "",
    val dataInicio: String = "",
    val dataFim: String? = null,
    val status: ContratoStatus = ContratoStatus.ATIVO,
)

data class PagamentoResponse(
    val parcelaId: String,
    val formaPagamento: String,
    val dataPagamento: String? = null,
    val status: PagamentoStatus = PagamentoStatus.PENDENTE,
    val clienteId: String? = null,
)

data class ManutencaoResponse(
    val numeroOs: Int,
    val motoId: String,
    val moto: MotoResponse? = null,
    val oficina: String? = null,
    val status: ManutencaoStatus = ManutencaoStatus.ABERTA,
)

data class TicketResponse(
    val numero: Int,
    val titulo: String,
    val descricao: String? = null,
    val clienteId: String? = null,
    val cliente: ClienteResponse? = null,
    val responsavelNome: String? = null,
    val abertoPorNome: String? = null,
    val status: TicketStatus = TicketStatus.ABERTO,
)
