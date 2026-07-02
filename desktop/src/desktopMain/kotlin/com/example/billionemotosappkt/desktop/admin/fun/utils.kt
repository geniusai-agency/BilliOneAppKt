package com.example.billionemotosappkt.desktop.admin.`fun`

import androidx.compose.ui.graphics.Color
import com.example.billionemotosappkt.desktop.api.ContratoResponse
import com.example.billionemotosappkt.desktop.api.ContratoStatus
import com.example.billionemotosappkt.desktop.api.ManutencaoResponse
import com.example.billionemotosappkt.desktop.api.ManutencaoStatus
import com.example.billionemotosappkt.desktop.api.PagamentoResponse
import com.example.billionemotosappkt.desktop.api.PagamentoStatus
import com.example.billionemotosappkt.desktop.api.TicketPrioridade
import com.example.billionemotosappkt.desktop.api.TicketResponse
import com.example.billionemotosappkt.desktop.api.TicketStatus
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

fun percentage(value: Int, total: Int): Int {
    if (total <= 0) return 0
    return ((value.toDouble() / total.toDouble()) * 100.0).roundToInt().coerceIn(0, 100)
}

fun percentLabel(value: Int, total: Int): String = "${percentage(value, total)}%"

fun formatCurrency(value: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    return formatter.format(value)
}

fun greeting(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 5..11 -> "bom dia"
    in 12..17 -> "boa tarde"
    else -> "boa noite"
}

fun initials(value: String): String {
    return value.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "AD" }
}

fun contractLabel(contract: ContratoResponse): String {
    return contract.cliente?.nome?.takeIf { it.isNotBlank() }
        ?: "Contrato ${contract.id.takeLast(6).uppercase(Locale.getDefault())}"
}

fun contractSubLabel(contract: ContratoResponse): String {
    val moto = contract.moto
    val motoLabel = buildString {
        if (!moto?.marca.isNullOrBlank()) append(moto?.marca).append(" ")
        append(moto?.modelo ?: contract.planoSnapshotNome)
    }
    return "$motoLabel | Plano ${contract.planoSnapshotNivel.lowercase(Locale.getDefault())}"
}

fun contractEndLabel(contract: ContratoResponse): String {
    val date = contract.dataFim?.takeIf { it.isNotBlank() } ?: contract.dataInicio
    val days = daysUntil(date)
    return when {
        days == null -> formatDate(date) ?: "Sem data"
        days <= 0 -> "vencido"
        days == 1 -> "vence amanha"
        else -> "vence em $days dias"
    }
}

fun contractStatusLabel(status: ContratoStatus): String = when (status) {
    ContratoStatus.ATIVO -> "Ativo"
    ContratoStatus.INADIMPLENTE -> "Inadimplente"
    ContratoStatus.ENCERRADO -> "Encerrado"
    ContratoStatus.CANCELADO -> "Cancelado"
    ContratoStatus.PENDENTE_ASSINATURA -> "Pendente"
}

fun accentForContract(status: ContratoStatus): Color = when (status) {
    ContratoStatus.ATIVO -> Color(0xFF45D483)
    ContratoStatus.PENDENTE_ASSINATURA -> Color(0xFFE0B64C)
    ContratoStatus.INADIMPLENTE -> Color(0xFFFF8A65)
    ContratoStatus.ENCERRADO -> Color(0xFF7C8A8F)
    ContratoStatus.CANCELADO -> Color(0xFFE05B64)
}

fun paymentLabel(payment: PagamentoResponse): String = "Parcela ${payment.parcelaId.takeLast(5).uppercase(Locale.getDefault())}"

fun paymentSubLabel(payment: PagamentoResponse): String = "${payment.formaPagamento} | ${formatDate(payment.dataPagamento)}"

fun paymentAmount(value: String): String = formatCurrency(value.toLongOrNull() ?: 0L)

fun paymentStatusLabel(status: PagamentoStatus): String = when (status) {
    PagamentoStatus.CONFIRMADO -> "Confirmado"
    PagamentoStatus.PENDENTE -> "Pendente"
    PagamentoStatus.CANCELADO -> "Cancelado"
    PagamentoStatus.ESTORNADO -> "Estornado"
}

fun accentForPayment(status: PagamentoStatus): Color = when (status) {
    PagamentoStatus.CONFIRMADO -> Color(0xFF45D483)
    PagamentoStatus.PENDENTE -> Color(0xFFE0B64C)
    PagamentoStatus.CANCELADO -> Color(0xFFE05B64)
    PagamentoStatus.ESTORNADO -> Color(0xFF7C8A8F)
}

fun maintenanceLabel(maintenance: ManutencaoResponse): String = "OS #${maintenance.numeroOs}"

fun maintenanceSubLabel(maintenance: ManutencaoResponse): String {
    val motoLabel = maintenance.moto?.modelo ?: "Moto ${maintenance.motoId.takeLast(5).uppercase(Locale.getDefault())}"
    val workLabel = maintenance.oficina ?: "Sem oficina"
    return "$motoLabel | $workLabel"
}

fun maintenanceCost(value: String): String = formatCurrency(value.toLongOrNull() ?: 0L)

fun maintenanceStatusLabel(status: ManutencaoStatus): String = when (status) {
    ManutencaoStatus.ABERTA -> "Aberta"
    ManutencaoStatus.EM_ANDAMENTO -> "Em andamento"
    ManutencaoStatus.CONCLUIDA -> "Concluida"
    ManutencaoStatus.CANCELADA -> "Cancelada"
}

fun accentForMaintenance(status: ManutencaoStatus): Color = when (status) {
    ManutencaoStatus.ABERTA -> Color(0xFFE0B64C)
    ManutencaoStatus.EM_ANDAMENTO -> Color(0xFF69D2FF)
    ManutencaoStatus.CONCLUIDA -> Color(0xFF45D483)
    ManutencaoStatus.CANCELADA -> Color(0xFFE05B64)
}

fun ticketLabel(ticket: TicketResponse): String = "#${ticket.numero} • ${ticket.titulo}"

fun ticketSubLabel(ticket: TicketResponse): String {
    val client = ticket.cliente?.nome ?: ticket.clienteId?.takeLast(6)
    val responsible = ticket.responsavelNome ?: ticket.abertoPorNome
    return listOfNotNull(client?.let { "Cliente $it" }, responsible?.let { "Resp. $it" }).joinToString(" | ")
}

fun ticketPriorityLabel(priority: TicketPrioridade): String = when (priority) {
    TicketPrioridade.BAIXA -> "Baixa"
    TicketPrioridade.MEDIA -> "Media"
    TicketPrioridade.ALTA -> "Alta"
    TicketPrioridade.URGENTE -> "Urgente"
}

fun ticketStatusLabel(status: TicketStatus): String = when (status) {
    TicketStatus.ABERTO -> "Aberto"
    TicketStatus.EM_ANALISE -> "Em analise"
    TicketStatus.AGUARDANDO_CLIENTE -> "Aguardando cliente"
    TicketStatus.RESOLVIDO -> "Resolvido"
    TicketStatus.CANCELADO -> "Cancelado"
}

fun accentForTicket(status: TicketStatus): Color = when (status) {
    TicketStatus.RESOLVIDO -> Color(0xFF45D483)
    TicketStatus.ABERTO -> Color(0xFFE0B64C)
    TicketStatus.EM_ANALISE -> Color(0xFF69D2FF)
    TicketStatus.AGUARDANDO_CLIENTE -> Color(0xFFFFB300)
    TicketStatus.CANCELADO -> Color(0xFFE05B64)
}

fun formatDate(value: String?): String? {
    if (value.isNullOrBlank()) return null
    val normalized = value.take(10)
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
    val parsed = runCatching { parser.parse(normalized) }.getOrNull() ?: return normalized
    val output = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return output.format(parsed)
}

private fun daysUntil(value: String?): Int? {
    val parsed = parseDate(value) ?: return null
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val target = Calendar.getInstance().apply {
        time = parsed
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val diff = target.timeInMillis - today.timeInMillis
    return (diff / (24 * 60 * 60 * 1000L)).toInt()
}

private fun parseDate(value: String?): java.util.Date? {
    if (value.isNullOrBlank()) return null
    val normalized = value.take(10)
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
    return runCatching { format.parse(normalized) }.getOrNull()
}
