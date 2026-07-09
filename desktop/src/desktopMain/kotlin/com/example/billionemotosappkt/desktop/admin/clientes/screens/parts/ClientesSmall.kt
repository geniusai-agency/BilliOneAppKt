package com.example.billionemotosappkt.desktop.admin.clientes.screens.parts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.billionemotosappkt.desktop.admin.clientes.components.ClienteEditDialog
import com.example.billionemotosappkt.desktop.admin.clientes.components.DocumentPreviewCard
import com.example.billionemotosappkt.desktop.admin.clientes.components.DocumentPreviewData
import com.example.billionemotosappkt.desktop.admin.clientes.model.AnaliseListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.accent
import com.example.billionemotosappkt.desktop.admin.clientes.model.label
import com.example.billionemotosappkt.desktop.admin.clientes.model.toUpdateRequest
import com.example.billionemotosappkt.desktop.admin.clientes.repository.AnalisesLoadResult
import com.example.billionemotosappkt.desktop.admin.clientes.repository.ClientesLoadResult
import com.example.billionemotosappkt.desktop.admin.clientes.repository.ClientesRepository
import com.example.billionemotosappkt.desktop.admin.components.BadgePill
import com.example.billionemotosappkt.desktop.admin.components.clientes.ClientesSubsectionToggle
import com.example.billionemotosappkt.desktop.admin.model.ClientesSectionTab
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ClienteAnalisePedidoStatus
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.DecisaoAprovacaoClienteResponse
import com.example.billionemotosappkt.shared.api.DecisaoClienteRequest
import com.example.billionemotosappkt.shared.api.ListAnalisesQuery
import com.example.billionemotosappkt.shared.api.ListClientesQuery
import com.example.billionemotosappkt.shared.api.ListMotosQuery
import com.example.billionemotosappkt.shared.api.MotoModeloResponse
import com.example.billionemotosappkt.shared.api.MotoResponse
import com.example.billionemotosappkt.shared.api.MotoStatus
import com.example.billionemotosappkt.shared.api.PlanoResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
internal fun StatusChip(
	status: ClienteAprovacaoStatus,
	selected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		onClick = onClick,
		shape = RoundedCornerShape(999.dp),
		color = if (selected) status.accent()
			.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.03f),
		border = BorderStroke(
			1.dp,
			if (selected) status.accent().copy(alpha = 0.32f) else Color.White.copy(alpha = 0.06f)
		),
		modifier = modifier,
	) {
		Text(
			text = status.label(),
			color = if (selected) status.accent() else Color.White.copy(alpha = 0.72f),
			fontSize = 11.sp,
			fontWeight = FontWeight.SemiBold,
			modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
			textAlign = TextAlign.Center,
		)
	}
}

@Composable
internal fun ActionChip(
	text: String,
	accent: Color,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	outlined: Boolean = false,
) {
	val container = if (outlined) Color.Transparent else accent.copy(alpha = 0.14f)
	val border = BorderStroke(1.dp, accent.copy(alpha = if (outlined) 0.34f else 0.20f))
	
	Surface(
		onClick = onClick,
		shape = RoundedCornerShape(12.dp),
		color = container,
		border = border,
		shadowElevation = 0.dp,
		modifier = modifier,
	) {
		Text(
			text = text,
			color = accent,
			fontSize = 11.sp,
			fontWeight = FontWeight.SemiBold,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
		)
	}
}

@Composable
internal fun RoundIconButton(
	icon: androidx.compose.ui.graphics.vector.ImageVector,
	tint: Color,
	onClick: () -> Unit,
) {
	Surface(
		onClick = onClick,
		shape = CircleShape,
		color = Color.White.copy(alpha = 0.05f),
		border = BorderStroke(1.dp, tint.copy(alpha = 0.20f)),
		modifier = Modifier.size(36.dp),
	) {
		Box(contentAlignment = Alignment.Center) {
			Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
		}
	}
	Spacer(Modifier.width(8.dp))
}

@Composable
internal fun LoadingState() {
	Box(
		modifier = Modifier.fillMaxWidth().height(220.dp),
		contentAlignment = Alignment.Center,
	) {
		CircularProgressIndicator(color = Color(0xFF20E65B))
	}
}

@Composable
internal fun EmptyState(message: String) {
	Box(
		modifier = Modifier.fillMaxWidth().height(220.dp),
		contentAlignment = Alignment.Center,
	) {
		Text(message, color = Color.White.copy(alpha = 0.35f), fontSize = 14.sp)
	}
}

@Composable
internal fun ErrorBanner(message: String) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(alpha = 0.08f)),
		border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.2f)),
	) {
		Row(
			modifier = Modifier.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Icon(Icons.Default.Close, null, tint = Color(0xFFFF4A4A))
			Spacer(Modifier.width(10.dp))
			Text("Erro: $message", color = Color.White, fontSize = 14.sp)
		}
	}
}

@Composable
internal fun DocumentRow(
	title: String,
	subtitle: String,
) {
	Card(
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
		modifier = Modifier.fillMaxWidth(),
	) {
		Row(
			modifier = Modifier.padding(14.dp).fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(14.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Box(
				modifier = Modifier
					.size(42.dp)
					.background(Color(0xFF20E65B).copy(alpha = 0.12f), CircleShape),
				contentAlignment = Alignment.Center,
			) {
				Icon(
					Icons.Default.Visibility,
					null,
					tint = Color(0xFF20E65B),
					modifier = Modifier.size(18.dp)
				)
			}
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
				Text(subtitle, color = Color.White.copy(alpha = 0.42f), fontSize = 11.sp)
			}
		}
	}
}

@Composable
internal fun HistoryRow(item: DecisaoAprovacaoClienteResponse) {
	Card(
		shape = RoundedCornerShape(14.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
		border = BorderStroke(1.dp, item.decisao.accent().copy(alpha = 0.16f)),
	) {
		Column(
			modifier = Modifier.padding(14.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				BadgePill(text = item.decisao.label(), accent = item.decisao.accent())
				Text(
					formatDate(item.createdAt),
					color = Color.White.copy(alpha = 0.36f),
					fontSize = 11.sp
				)
			}
			Text(
				item.justificativa,
				color = Color.White.copy(alpha = 0.72f),
				fontSize = 12.sp,
				lineHeight = 16.sp
			)
		}
	}
}

internal fun resolveRequestedMotoLabel(
	modeloMotoId: String,
	fallbackName: String,
	modelosDisponiveis: List<MotoModeloResponse>,
): String {
	val resolved = modelosDisponiveis.firstOrNull { it.id == modeloMotoId.trim() }
	val resolvedLabel = listOfNotNull(resolved?.marca, resolved?.nome).joinToString(" ").trim()
	return when {
		resolvedLabel.isNotBlank() -> resolvedLabel
		fallbackName.isNotBlank() -> fallbackName
		modeloMotoId.isNotBlank() -> modeloMotoId
		else -> "Não informado"
	}
}

internal fun motoLabel(moto: MotoResponse): String {
	val base = buildString {
		append(moto.modelo.trim())
		if (moto.placa.isNotBlank()) {
			append(" • ")
			append(moto.placa.trim())
		}
	}
	val marca = moto.marca?.trim().orEmpty()
	return if (marca.isBlank()) base else "$marca $base"
}

internal enum class ClientDetailTab(val label: String) {
	RESUMO("Resumo"),
	DOCUMENTOS("Documentos"),
	ACOES("Ações"),
	HISTORICO("Histórico"),
}

internal data class SummaryCounters(
	val total: Int,
	val pendentes: Int,
	val emAnalise: Int,
	val aprovados: Int,
	val ressalvas: Int,
	val reprovados: Int,
) {
	companion object {
		fun from(items: List<ClienteListItem>): SummaryCounters {
			return SummaryCounters(
				total = items.size,
				pendentes = items.count { it.status == ClienteAprovacaoStatus.PENDENTE },
				emAnalise = items.count { it.status == ClienteAprovacaoStatus.EM_ANALISE },
				aprovados = items.count { it.status == ClienteAprovacaoStatus.APROVADO },
				ressalvas = items.count { it.status == ClienteAprovacaoStatus.APROVADO_COM_RESSALVA },
				reprovados = items.count { it.status == ClienteAprovacaoStatus.REPROVADO },
			)
		}
		
		fun fromAnalises(items: List<AnaliseListItem>): SummaryCounters {
			return SummaryCounters(
				total = items.size,
				pendentes = items.count { it.status == ClienteAnalisePedidoStatus.PENDENTE },
				emAnalise = items.count { it.status == ClienteAnalisePedidoStatus.EM_ANALISE },
				aprovados = items.count { it.status == ClienteAnalisePedidoStatus.APROVADO },
				ressalvas = 0,
				reprovados = items.count { it.status == ClienteAnalisePedidoStatus.REPROVADO },
			)
		}
	}
}

internal fun defaultJustificativa(decisao: ClienteAprovacaoStatus): String = when (decisao) {
	ClienteAprovacaoStatus.APROVADO -> "Documentacao validada."
	ClienteAprovacaoStatus.APROVADO_COM_RESSALVA -> "Aprovado com ressalvas apos validacao parcial."
	ClienteAprovacaoStatus.REPROVADO -> "Documentacao inconsistente ou incompleta."
	ClienteAprovacaoStatus.EM_ANALISE -> "Registro atualizado para analise interna."
	ClienteAprovacaoStatus.PENDENTE -> "Mantido como pendente."
}

internal fun formatDate(value: String?): String {
	if (value.isNullOrBlank()) return "-"
	return runCatching {
		val instant = Instant.parse(value)
		val formatter =
			DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault())
		formatter.format(instant)
	}.getOrElse { value }
}

