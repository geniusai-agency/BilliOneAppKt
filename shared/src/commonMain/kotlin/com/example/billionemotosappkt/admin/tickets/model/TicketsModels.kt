package com.example.billionemotosappkt.desktop.admin.tickets.model

import androidx.compose.ui.graphics.Color
import com.example.billionemotosappkt.shared.api.TicketCategoria
import com.example.billionemotosappkt.shared.api.TicketOrigem
import com.example.billionemotosappkt.shared.api.TicketPrioridade
import com.example.billionemotosappkt.shared.api.TicketResponse
import com.example.billionemotosappkt.shared.api.TicketStatus
import com.example.billionemotosappkt.shared.utils.formatBrDateTime

/** Resultado paginado da listagem de chamados. */
data class TicketsLoadResult(
    val tickets: List<TicketResponse>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val isFallbackData: Boolean,
)

// ---------------------------------------------------------------------------
// Rótulos pt-BR e cores por enum (consistente com o restante do admin)
// ---------------------------------------------------------------------------

fun TicketStatus.label(): String = when (this) {
    TicketStatus.ABERTO -> "Aberto"
    TicketStatus.EM_ANALISE -> "Em análise"
    TicketStatus.AGUARDANDO_CLIENTE -> "Aguardando cliente"
    TicketStatus.RESOLVIDO -> "Resolvido"
    TicketStatus.CANCELADO -> "Cancelado"
}

fun TicketStatus.accent(): Color = when (this) {
    TicketStatus.ABERTO -> Color(0xFFFFB300)
    TicketStatus.EM_ANALISE -> Color(0xFF5BC0FF)
    TicketStatus.AGUARDANDO_CLIENTE -> Color(0xFFB388FF)
    TicketStatus.RESOLVIDO -> Color(0xFF20E65B)
    TicketStatus.CANCELADO -> Color(0xFFFF4A4A)
}

fun TicketPrioridade.label(): String = when (this) {
    TicketPrioridade.BAIXA -> "Baixa"
    TicketPrioridade.MEDIA -> "Média"
    TicketPrioridade.ALTA -> "Alta"
    TicketPrioridade.URGENTE -> "Urgente"
}

fun TicketPrioridade.accent(): Color = when (this) {
    TicketPrioridade.BAIXA -> Color(0xFF9AA6A0)
    TicketPrioridade.MEDIA -> Color(0xFF5BC0FF)
    TicketPrioridade.ALTA -> Color(0xFFFFB300)
    TicketPrioridade.URGENTE -> Color(0xFFFF4A4A)
}

fun TicketOrigem.label(): String = when (this) {
    TicketOrigem.INTERNO -> "Interno"
    TicketOrigem.CLIENTE -> "Cliente"
}

fun TicketCategoria.label(): String = when (this) {
    TicketCategoria.DIVERGENCIA_VALOR -> "Divergência de valor"
    TicketCategoria.CONTESTACAO_COBRANCA -> "Contestação de cobrança"
    TicketCategoria.PEDIDO_ESTORNO -> "Pedido de estorno"
    TicketCategoria.SEGUNDA_VIA_BOLETO -> "Segunda via de boleto"
    TicketCategoria.NEGOCIACAO -> "Negociação"
    TicketCategoria.COMPROVANTE_PAGAMENTO -> "Comprovante de pagamento"
    TicketCategoria.MANUTENCAO -> "Manutenção"
    TicketCategoria.SOCORRO -> "Socorro"
    TicketCategoria.DUVIDA -> "Dúvida"
    TicketCategoria.AJUDA -> "Ajuda"
    TicketCategoria.OUTROS -> "Outros"
}

/** Nome amigável do cliente do chamado (usa a relação carregada quando disponível). */
fun TicketResponse.clienteLabel(): String {
    val nome = cliente?.nome?.takeIf { it.isNotBlank() }
    return nome ?: clienteId?.takeIf { it.isNotBlank() }?.take(8) ?: "—"
}

/** Rótulo curto da moto vinculada. */
fun TicketResponse.motoLabel(): String {
    val moto = moto ?: return "—"
    val base = moto.modelo.trim()
    val placa = moto.placa.trim()
    return if (placa.isBlank()) base.ifBlank { "—" } else "$base • $placa"
}

/** Rótulo curto do contrato vinculado. */
fun TicketResponse.contratoLabel(): String {
    val contrato = contrato ?: return "—"
    val plano = contrato.planoSnapshotNome.trim()
    val short = contrato.id.take(8)
    return if (plano.isBlank()) short else "$short • $plano"
}

fun formatTicketDate(value: String?): String {
    if (value.isNullOrBlank()) return "-"
    return formatBrDateTime(value) ?: value
}
