package com.example.billionemotosappkt.desktop.admin.`fun`

import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import com.example.billionemotosappkt.desktop.api.ContratoResponse
import com.example.billionemotosappkt.desktop.api.ContratoStatus
import com.example.billionemotosappkt.desktop.api.ManutencaoResponse
import com.example.billionemotosappkt.desktop.api.ManutencaoStatus
import com.example.billionemotosappkt.desktop.api.PagamentoResponse
import com.example.billionemotosappkt.desktop.api.PagamentoStatus
import com.example.billionemotosappkt.desktop.api.TicketPrioridade
import com.example.billionemotosappkt.desktop.api.TicketResponse
import com.example.billionemotosappkt.desktop.api.TicketStatus
import com.example.billionemotosappkt.shared.api.MotoModeloResponse
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ApiConfig
import com.example.billionemotosappkt.shared.utils.toImageBitmap
import com.example.billionemotosappkt.shared.utils.openUri
import com.example.billionemotosappkt.shared.utils.rememberAssetPainter
import com.example.billionemotosappkt.shared.utils.getDaysUntil
import com.example.billionemotosappkt.shared.utils.getHourOfDay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

fun percentage(value: Int, total: Int): Int {
	if (total <= 0) return 0
	return ((value.toDouble() / total.toDouble()) * 100.0).roundToInt().coerceIn(0, 100)
}

fun percentLabel(value: Int, total: Int): String = "${percentage(value, total)}%"

fun formatCurrency(value: Long): String {
	val cents = value % 100
	val integral = value / 100
	val centsStr = if (cents < 10) "0$cents" else "$cents"
	val integralStr = integral.toString().reversed().chunked(3).joinToString(".").reversed()
	return "R$ $integralStr,$centsStr"
}

fun isLocalImagePath(value: String): Boolean {
	return false
}

fun resolveImageSource(value: String?, apiBaseUrl: String? = null): String {
	val trimmed = value?.trim().orEmpty()
	if (trimmed.isBlank()) return ""
	if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed
	if (trimmed.startsWith("/") && !apiBaseUrl.isNullOrBlank()) {
		return apiBaseUrl.trimEnd('/') + trimmed
	}
	return trimmed
}

fun MotoModeloResponse.toPanelImage(): String = getMotoModelCardImage(marca, modelo)

fun getMotoImage(modelo: String?): String {
	return getMotoModelCardImage(null, modelo)
}

fun getMotoModelCardImage(marca: String?, modelo: String?): String {
	val combined = listOfNotNull(marca, modelo).joinToString(" ")
	return when {
		combined.contains("Avelloz", ignoreCase = true) -> "avelloz_160_black_new.png"
		combined.contains("Ninja", ignoreCase = true) -> "moto_sport_updated.png"
		combined.contains("Eletrica", ignoreCase = true) -> "moto_eletrica_new.png"
		combined.contains("CG", ignoreCase = true) -> "moto_premium.png"
		else -> "moto_premium.png"
	}
}

@Composable
fun fallbackMotoPainter(): Painter {
	return rememberAssetPainter("moto_premium.png")
}

fun resolveMotoImagePreview(imageValue: String?, apiBaseUrl: String? = null): String {
	val value = imageValue?.trim().orEmpty()
	if (value.isBlank()) return "moto_premium.png"
	if (value.startsWith("http://") || value.startsWith("https://")) return value
	if (value.startsWith("/") && !apiBaseUrl.isNullOrBlank()) {
		return apiBaseUrl.trimEnd('/') + value
	}
	return value
}

fun guessImageContentType(fileName: String): String {
	return when (fileName.substringAfterLast('.').lowercase()) {
		"png" -> "image/png"
		"jpg", "jpeg" -> "image/jpeg"
		"webp" -> "image/webp"
		"gif" -> "image/gif"
		else -> "application/octet-stream"
	}
}

fun String?.toUploadFileRequest(): UploadFileRequest? {
	return null
}

fun getMotoHeroImage(marca: String?, modelo: String?): String {
	val combined = listOfNotNull(marca, modelo).joinToString(" ")
	return when {
		combined.contains("Avelloz", ignoreCase = true) -> "hero_bg_moto1.png"
		combined.contains("Eletrica", ignoreCase = true) -> "hero_bg_moto2.png"
		combined.contains("Ninja", ignoreCase = true) -> "hero_bg_moto1.png"
		else -> "hero_bg_moto2.png"
	}
}

@Composable
fun desktopImagePainter(
	resourceName: String,
	api: BillioneMotosApi
): Painter {
	val resolved = resolveImageSource(resourceName, api.config.baseUrl)
	return when {
		resolved.isBlank() -> fallbackMotoPainter()
		resolved.startsWith("http://") || resolved.startsWith("https://") -> {
			var hasError by remember(resolved) { mutableStateOf(false) }
			val bitmap = remember(resolved) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
			LaunchedEffect(resolved) {
				hasError = false
				runCatching {
					val bytes = api.fetchRawBytes(resolved)
					bytes.toImageBitmap()
				}.onSuccess {
					bitmap.value = it
				}.onFailure {
					hasError = true
				}
			}
			when {
				hasError -> androidx.compose.ui.graphics.vector.rememberVectorPainter(androidx.compose.material.icons.Icons.Default.BrokenImage)
				bitmap.value != null -> androidx.compose.ui.graphics.painter.BitmapPainter(bitmap.value!!)
				else -> fallbackMotoPainter()
			}
		}
		else -> rememberAssetPainter(resolved)
	}
}

// Keep signature backward-compatible for callers who might pass base URL
@Composable
fun desktopImagePainter(
	resourceName: String,
	apiBaseUrl: String? = null,
	apiAccessToken: String? = null
): Painter {
	val resolved = resolveImageSource(resourceName, apiBaseUrl)
	return when {
		resolved.isBlank() -> fallbackMotoPainter()
		resolved.startsWith("http://", ignoreCase = true) ||
			resolved.startsWith("https://", ignoreCase = true) -> {
			val scope = rememberCoroutineScope()
			val api = remember(apiBaseUrl, apiAccessToken) {
				BillioneMotosApi(
					ApiConfig(
						baseUrl = apiBaseUrl?.takeIf { it.isNotBlank() } ?: resolved,
						accessTokenProvider = { apiAccessToken },
					),
				)
			}
			DisposableEffect(api) {
				onDispose {
					scope.launch {
						runCatching { api.close() }
					}
				}
			}
			var hasError by remember(resolved) { mutableStateOf(false) }
			val bitmap = remember(resolved) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
			LaunchedEffect(resolved, apiAccessToken) {
				hasError = false
				runCatching {
					api.fetchRawBytes(resolved).toImageBitmap()
				}.onSuccess {
					bitmap.value = it
				}.onFailure {
					hasError = true
				}
			}
			when {
				hasError -> androidx.compose.ui.graphics.vector.rememberVectorPainter(androidx.compose.material.icons.Icons.Default.BrokenImage)
				bitmap.value != null -> androidx.compose.ui.graphics.painter.BitmapPainter(bitmap.value!!)
				else -> fallbackMotoPainter()
			}
		}
		else -> rememberAssetPainter(resolved)
	}
}

fun greeting(): String = when (getHourOfDay()) {
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
		?: "Contrato ${contract.id.takeLast(6).uppercase()}"
}

fun contractSubLabel(contract: ContratoResponse): String {
	val moto = contract.moto
	val motoLabel = buildString {
		if (!moto?.marca.isNullOrBlank()) append(moto?.marca).append(" ")
		append(moto?.modelo ?: contract.planoSnapshotNome)
	}
	return "$motoLabel | Plano ${contract.planoSnapshotNivel.lowercase()}"
}

fun contractEndLabel(contract: ContratoResponse): String {
	val date = contract.dataFim?.takeIf { it.isNotBlank() } ?: contract.dataInicio
	val days = getDaysUntil(date)
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

fun paymentLabel(payment: PagamentoResponse): String =
	"Parcela ${payment.parcelaId.takeLast(5).uppercase()}"

fun paymentSubLabel(payment: PagamentoResponse): String =
	"${payment.formaPagamento} | ${formatDate(payment.dataPagamento)}"

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
	val motoLabel = maintenance.moto?.modelo ?: "Moto ${
		maintenance.motoId.takeLast(5).uppercase()
	}"
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
	return listOfNotNull(
		client?.let { "Cliente $it" },
		responsible?.let { "Resp. $it" }).joinToString(" | ")
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
	val pieces = normalized.split('-')
	if (pieces.size == 3) {
		return "${pieces[2]}/${pieces[1]}/${pieces[0]}"
	}
	return normalized
}
