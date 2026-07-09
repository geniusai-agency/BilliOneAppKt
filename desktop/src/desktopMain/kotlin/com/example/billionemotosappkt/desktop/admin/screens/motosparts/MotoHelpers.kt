package com.example.billionemotosappkt.desktop.admin.screens.motosparts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.components.motos.MotosList
import com.example.billionemotosappkt.desktop.admin.components.motos.MotoImagePickerField
import com.example.billionemotosappkt.desktop.admin.data.MotosFilters
import com.example.billionemotosappkt.desktop.admin.data.MotosUiState
import com.example.billionemotosappkt.desktop.admin.model.MotoSectionTab
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ListMotosQuery
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import com.example.billionemotosappkt.shared.api.ListManutencoesQuery
import com.example.billionemotosappkt.shared.api.MotoResponse
import com.example.billionemotosappkt.shared.api.MotoStatus
import com.example.billionemotosappkt.shared.api.ManutencaoResponse
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.UpdateMotoRequest
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info

internal data class MotoDetailState(
	val moto: MotoResponse? = null,
	val contratos: List<ContratoResponse> = emptyList(),
	val manutencoes: List<ManutencaoResponse> = emptyList(),
	val loading: Boolean = false,
	val error: String? = null,
)

internal fun maintenanceAccent(status: com.example.billionemotosappkt.shared.api.ManutencaoStatus): Color = when (status) {
	com.example.billionemotosappkt.shared.api.ManutencaoStatus.ABERTA -> Color(0xFFFFB300)
	com.example.billionemotosappkt.shared.api.ManutencaoStatus.EM_ANDAMENTO -> Color(0xFF7DD3FC)
	com.example.billionemotosappkt.shared.api.ManutencaoStatus.CONCLUIDA -> Color(0xFF20E65B)
	com.example.billionemotosappkt.shared.api.ManutencaoStatus.CANCELADA -> Color(0xFFFF4A4A)
}

internal fun statusAccent(name: String): Color = when (name.lowercase()) {
	"ativo", "disponivel" -> Color(0xFF20E65B)
	"alugada", "em_andamento" -> Color(0xFF7DD3FC)
	"contratada" -> Color(0xFF8B5CF6)
	"manutencao" -> Color(0xFFFFB300)
	"bloqueada", "cancelada", "inadimplente" -> Color(0xFFFF4A4A)
	else -> Color.White.copy(alpha = 0.5f)
}

internal fun statusAccent(status: MotoStatus): Color = when (status) {
	MotoStatus.DISPONIVEL -> Color(0xFF20E65B)
	MotoStatus.ALUGADA -> Color(0xFF7DD3FC)
	MotoStatus.CONTRATADA -> Color(0xFF8B5CF6)
	MotoStatus.MANUTENCAO -> Color(0xFFFFB300)
	MotoStatus.BLOQUEADA -> Color(0xFFFF4A4A)
	MotoStatus.PENDENTE_CONTRATO -> Color(0xFFFFB300)
	MotoStatus.AGUARDANDO_DEVOLUCAO -> Color(0xFFFFB300)
}

internal fun statusLabel(status: MotoStatus): String = when (status) {
	MotoStatus.DISPONIVEL -> "Disponível"
	MotoStatus.ALUGADA -> "Alugada"
	MotoStatus.CONTRATADA -> "Contratada"
	MotoStatus.MANUTENCAO -> "Manutenção"
	MotoStatus.BLOQUEADA -> "Bloqueada"
	MotoStatus.PENDENTE_CONTRATO -> "Pendente contrato"
	MotoStatus.AGUARDANDO_DEVOLUCAO -> "Aguardando devolução"
}

internal fun formatMoney(value: Double): String {
	return "R$ " + String.format(java.util.Locale("pt", "BR"), "%,.2f", value).replace(',', '#').replace('.', ',').replace('#', '.')
}

internal fun daysBetween(value: String?): Int {
	if (value.isNullOrBlank()) return 0
	return runCatching {
		val instant = java.time.Instant.parse(if (value.length == 10) "${value}T00:00:00Z" else value)
		val now = java.time.Instant.now()
		kotlin.math.abs(java.time.temporal.ChronoUnit.DAYS.between(instant, now).toInt())
	}.getOrDefault(0)
}

internal fun formatDate(value: String?): String {
	if (value.isNullOrBlank()) return "-"
	return runCatching {
		java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(java.time.ZoneId.systemDefault()).format(
			java.time.Instant.parse(if (value.length == 10) "${value}T00:00:00Z" else value)
		)
	}.getOrElse { value }
}
