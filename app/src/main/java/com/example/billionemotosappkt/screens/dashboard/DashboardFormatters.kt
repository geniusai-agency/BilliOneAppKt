package com.example.billionemotosappkt.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.data.dashboard.DashboardOverviewData
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.ContratoStatus
import com.example.billionemotosappkt.shared.api.ManutencaoResponse
import com.example.billionemotosappkt.shared.api.ManutencaoStatus
import com.example.billionemotosappkt.shared.api.PagamentoResponse
import com.example.billionemotosappkt.shared.api.PagamentoStatus
import com.example.billionemotosappkt.shared.api.TicketPrioridade
import com.example.billionemotosappkt.shared.api.TicketResponse
import com.example.billionemotosappkt.shared.api.TicketStatus
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DashboardErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier, color = Color.Transparent) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                ),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(28.dp),
                    )
                    Text(
                        text = "Nao foi possivel carregar o painel.",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Text(text = "Tentar novamente", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun adminMetrics(overview: DashboardOverviewData): List<AdminMetricModel> {
    return listOf(
        AdminMetricModel(
            label = "Contratos ativos",
            value = overview.resumo.contratos.ativos.toString(),
            subtitle = "Base operacional principal",
            icon = Icons.Default.Groups,
            accent = MaterialTheme.colorScheme.primary,
            kicker = "Atual",
        ),
        AdminMetricModel(
            label = "Vencendo em 60 dias",
            value = overview.contratosVencendo.size.toString(),
            subtitle = "Renovacoes e risco de perda",
            icon = Icons.Default.Schedule,
            accent = MaterialTheme.colorScheme.tertiary,
            kicker = "60d",
        ),
        AdminMetricModel(
            label = "Faturamento do mes",
            value = formatCurrency(overview.resumo.financeiro.totalRecebido.toLong()),
            subtitle = "Receita registrada no periodo",
            icon = Icons.Default.AttachMoney,
            accent = MaterialTheme.colorScheme.primary,
            kicker = "Mês",
        ),
        AdminMetricModel(
            label = "Recebido no mes",
            value = overview.resumo.financeiro.pagamentosConfirmados.toString(),
            subtitle = "Pagamentos conciliados",
            icon = Icons.Default.CreditCard,
            accent = MaterialTheme.colorScheme.secondary,
        ),
        AdminMetricModel(
            label = "Em aberto",
            value = formatCurrency(overview.resumo.financeiro.totalDespesas.toLong()),
            subtitle = "Despesas e pendencias",
            icon = Icons.Default.WarningAmber,
            accent = MaterialTheme.colorScheme.error,
        ),
        AdminMetricModel(
            label = "Tickets ativos",
            value = (overview.resumo.tickets.abertos + overview.resumo.tickets.emAnalise).toString(),
            subtitle = "Atendimento em andamento",
            icon = Icons.Default.SupportAgent,
            accent = MaterialTheme.colorScheme.tertiary,
            kicker = "Help",
        ),
    )
}

fun contractLabel(contract: ContratoResponse): String {
    val client = contract.cliente?.nome?.takeIf { it.isNotBlank() }
        ?: contract.clienteId.take8()
    val moto = contract.moto?.modelo?.takeIf { it.isNotBlank() }
        ?: contract.motoId.take8()
    return listOfNotNull(client, moto).joinToString(" • ").ifBlank { contract.planoSnapshotNome }
}

fun contractSubLabel(contract: ContratoResponse): String {
    val plate = contract.moto?.placa?.takeIf { it.isNotBlank() }
    val periodicity = when (contract.periodicidade) {
        com.example.billionemotosappkt.shared.api.ContratoPeriodicidade.SEMANAL -> "Semanal"
        com.example.billionemotosappkt.shared.api.ContratoPeriodicidade.MENSAL -> "Mensal"
    }
    return listOfNotNull(
        plate?.let { "Placa $it" },
        "Plano ${contract.planoSnapshotNivel}",
        periodicity,
    ).joinToString(" • ")
}

fun contractEndLabel(contract: ContratoResponse): String {
    val end = formatDate(contract.dataFim) ?: "Sem vencimento"
    return if (contract.status == ContratoStatus.ATIVO) "Vence $end" else end
}

fun contractStatusLabel(status: ContratoStatus): String = when (status) {
    ContratoStatus.PENDENTE_ASSINATURA -> "Assinatura"
    ContratoStatus.ATIVO -> "Ativo"
    ContratoStatus.ENCERRADO -> "Encerrado"
    ContratoStatus.INADIMPLENTE -> "Inadimplente"
    ContratoStatus.CANCELADO -> "Cancelado"
}

fun paymentLabel(payment: PagamentoResponse): String {
    val method = payment.formaPagamento.trim().ifBlank { "Pagamento" }
    return "Pagamento $method"
}

fun paymentSubLabel(payment: PagamentoResponse): String {
    val date = formatDate(payment.dataPagamento)
    return listOfNotNull(
        payment.clienteId.take8().takeIf { it.isNotBlank() }?.let { "Cliente $it" },
        date?.let { "Em $it" },
    ).joinToString(" • ")
}

fun paymentAmount(value: String): String = formatCurrency(value.toLongOrNull() ?: value.toDoubleOrNull()?.toLong() ?: 0L)

fun paymentStatusLabel(status: PagamentoStatus): String = when (status) {
    PagamentoStatus.PENDENTE -> "Pendente"
    PagamentoStatus.CONFIRMADO -> "Confirmado"
    PagamentoStatus.CANCELADO -> "Cancelado"
    PagamentoStatus.ESTORNADO -> "Estornado"
}

fun maintenanceLabel(maintenance: ManutencaoResponse): String {
    return "OS #${maintenance.numeroOs} • ${maintenance.tipo.name.lowercase().replaceFirstChar { it.uppercase() }}"
}

fun maintenanceSubLabel(maintenance: ManutencaoResponse): String {
    val moto = maintenance.moto?.modelo?.takeIf { it.isNotBlank() }
        ?: maintenance.motoId.take8()
    val office = maintenance.oficina?.takeIf { it.isNotBlank() } ?: "Oficina nao informada"
    return listOfNotNull(moto.takeIf { it.isNotBlank() }?.let { "Moto $it" }, office).joinToString(" • ")
}

fun maintenanceCost(value: String): String = formatCurrency(value.toLongOrNull() ?: value.toDoubleOrNull()?.toLong() ?: 0L)

fun maintenanceStatusLabel(status: ManutencaoStatus): String = when (status) {
    ManutencaoStatus.ABERTA -> "Aberta"
    ManutencaoStatus.EM_ANDAMENTO -> "Em andamento"
    ManutencaoStatus.CONCLUIDA -> "Concluida"
    ManutencaoStatus.CANCELADA -> "Cancelada"
}

fun ticketLabel(ticket: TicketResponse): String = "#${ticket.numero} • ${ticket.titulo}"

fun ticketSubLabel(ticket: TicketResponse): String {
    val client = ticket.cliente?.nome?.takeIf { it.isNotBlank() } ?: ticket.clienteId.take8()
    val responsible = ticket.responsavelNome?.takeIf { it.isNotBlank() } ?: ticket.abertoPorNome?.takeIf { it.isNotBlank() }
    return listOfNotNull(
        client?.let { "Cliente $it" },
        responsible?.let { "Responsavel $it" },
    ).joinToString(" • ")
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

fun accentForContract(status: ContratoStatus): Color = when (status) {
    ContratoStatus.ATIVO -> Color(0xFF22C55E)
    ContratoStatus.PENDENTE_ASSINATURA -> Color(0xFFF59E0B)
    ContratoStatus.ENCERRADO -> Color(0xFF94A3B8)
    ContratoStatus.INADIMPLENTE -> Color(0xFFEF4444)
    ContratoStatus.CANCELADO -> Color(0xFF64748B)
}

fun accentForPayment(status: PagamentoStatus): Color = when (status) {
    PagamentoStatus.CONFIRMADO -> Color(0xFF22C55E)
    PagamentoStatus.PENDENTE -> Color(0xFFF59E0B)
    PagamentoStatus.CANCELADO -> Color(0xFF64748B)
    PagamentoStatus.ESTORNADO -> Color(0xFFEF4444)
}

fun accentForMaintenance(status: ManutencaoStatus): Color = when (status) {
    ManutencaoStatus.CONCLUIDA -> Color(0xFF22C55E)
    ManutencaoStatus.ABERTA -> Color(0xFFF59E0B)
    ManutencaoStatus.EM_ANDAMENTO -> Color(0xFF38BDF8)
    ManutencaoStatus.CANCELADA -> Color(0xFF64748B)
}

fun accentForTicket(status: TicketStatus): Color = when (status) {
    TicketStatus.RESOLVIDO -> Color(0xFF22C55E)
    TicketStatus.ABERTO -> Color(0xFFF59E0B)
    TicketStatus.EM_ANALISE -> Color(0xFF38BDF8)
    TicketStatus.AGUARDANDO_CLIENTE -> Color(0xFFF97316)
    TicketStatus.CANCELADO -> Color(0xFF64748B)
}

fun formatCurrency(value: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(value)
}

fun formatDate(value: String?): String? {
    if (value.isNullOrBlank()) return null
    val normalized = value.take(10)
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
    val parsed = runCatching { parser.parse(normalized) }.getOrNull() ?: return normalized
    val output = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return output.format(parsed)
}

fun greeting(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 0..11 -> "bom dia"
    in 12..17 -> "boa tarde"
    else -> "boa noite"
}

private fun String?.take8(): String = this?.take(8).orEmpty()

private fun String?.trimOrNull(): String? = this?.trim()?.takeIf { it.isNotBlank() }
