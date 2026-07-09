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
internal fun ClientsTable(
	clientes: List<ClienteListItem>,
	isLoading: Boolean,
	compact: Boolean,
	onView: (ClienteListItem) -> Unit,
	onEdit: (ClienteListItem) -> Unit,
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(modifier = Modifier.fillMaxWidth()) {
			TableHeader(
				columns = listOf("Cliente", "Cidade", "Status", "Criado"),
				showActions = true,
				compact = compact,
			)
			HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
			
			when {
				isLoading -> LoadingState()
				clientes.isEmpty() -> EmptyState("Nenhum cliente encontrado")
				else -> clientes.forEachIndexed { index, cliente ->
					ClientRow(
						cliente = cliente,
						compact = compact,
						onView = onView,
						onEdit = onEdit
					)
					if (index < clientes.lastIndex) {
						HorizontalDivider(
							color = Color.White.copy(alpha = 0.03f),
							modifier = Modifier.padding(horizontal = 22.dp)
						)
					}
				}
			}
		}
	}
}

@Composable
internal fun AnalysisTable(
	analises: List<AnaliseListItem>,
	isLoading: Boolean,
	compact: Boolean,
	onView: (AnaliseListItem) -> Unit,
	onApprove: (AnaliseListItem) -> Unit,
	onReject: (AnaliseListItem) -> Unit,
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(modifier = Modifier.fillMaxWidth()) {
			TableHeader(
				columns = listOf("Cliente", "Cidade", "Status", "Criado"),
				showActions = true,
				compact = compact,
			)
			HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
			
			when {
				isLoading -> LoadingState()
				analises.isEmpty() -> EmptyState("Nenhum pedido encontrado")
				else -> analises.forEachIndexed { index, cliente ->
					AnalysisRow(
						cliente = cliente,
						compact = compact,
						onView = onView,
						onApprove = onApprove,
						onReject = onReject,
					)
					if (index < analises.lastIndex) {
						HorizontalDivider(
							color = Color.White.copy(alpha = 0.03f),
							modifier = Modifier.padding(horizontal = 22.dp)
						)
					}
				}
			}
		}
	}
}

@Composable
internal fun TableHeader(
	columns: List<String>,
	showActions: Boolean,
	compact: Boolean,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 22.dp, vertical = 18.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		val headerColor = Color.White.copy(alpha = 0.42f)
		Text(
			columns[0],
			modifier = Modifier.weight(2.3f),
			color = headerColor,
			fontSize = 13.sp,
			fontWeight = FontWeight.SemiBold
		)
		Text(
			columns[1],
			modifier = Modifier.weight(1.1f),
			color = headerColor,
			fontSize = 13.sp,
			fontWeight = FontWeight.SemiBold
		)
		Text(
			columns[2],
			modifier = Modifier.weight(1.1f),
			color = headerColor,
			fontSize = 13.sp,
			fontWeight = FontWeight.SemiBold
		)
		Text(
			columns[3],
			modifier = Modifier.weight(1.0f),
			color = headerColor,
			fontSize = 13.sp,
			fontWeight = FontWeight.SemiBold
		)
		if (showActions) {
			Spacer(Modifier.width(if (compact) 88.dp else 112.dp))
		}
	}
}

@Composable
internal fun ClientRow(
	cliente: ClienteListItem,
	compact: Boolean,
	onView: (ClienteListItem) -> Unit,
	onEdit: (ClienteListItem) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onView(cliente) }
			.padding(horizontal = 22.dp, vertical = 16.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Column(modifier = Modifier.weight(2.3f)) {
			Text(
				cliente.nome,
				color = Color.White,
				fontSize = 14.sp,
				fontWeight = FontWeight.SemiBold
			)
			Spacer(Modifier.height(2.dp))
			Text(
				"${cliente.cpf}  -  ${cliente.email}",
				color = Color.White.copy(alpha = 0.48f),
				fontSize = 11.sp
			)
			if (cliente.planoNome.isNotBlank() || cliente.motoNome.isNotBlank()) {
				Spacer(Modifier.height(6.dp))
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					if (cliente.planoNome.isNotBlank()) {
						BadgePill(text = "Plano: ${cliente.planoNome}", accent = Color(0xFF7DD3FC))
					}
					if (cliente.motoNome.isNotBlank()) {
						BadgePill(text = "Moto: ${cliente.motoNome}", accent = Color(0xFF20E65B))
					}
				}
			}
		}
		Column(modifier = Modifier.weight(1.1f)) {
			Text(cliente.cityLabel, color = Color.White.copy(alpha = 0.80f), fontSize = 13.sp)
			Text(cliente.telefone, color = Color.White.copy(alpha = 0.42f), fontSize = 11.sp)
		}
		Box(modifier = Modifier.weight(1.1f)) {
			BadgePill(text = cliente.status.label(), accent = cliente.status.accent())
		}
		Text(
			formatDate(cliente.createdAt),
			modifier = Modifier.weight(1.0f),
			color = Color.White.copy(alpha = 0.68f),
			fontSize = 13.sp
		)
		ActionCell(
			onView = { onView(cliente) },
			onEdit = { onEdit(cliente) },
			onApprove = null,
			onReject = null,
		)
	}
}

@Composable
internal fun AnalysisRow(
	cliente: AnaliseListItem,
	compact: Boolean,
	onView: (AnaliseListItem) -> Unit,
	onApprove: (AnaliseListItem) -> Unit,
	onReject: (AnaliseListItem) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onView(cliente) }
			.padding(horizontal = 22.dp, vertical = 16.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Column(modifier = Modifier.weight(2.3f)) {
			Text(
				cliente.nome,
				color = Color.White,
				fontSize = 14.sp,
				fontWeight = FontWeight.SemiBold
			)
			Spacer(Modifier.height(2.dp))
			Text(
				"${cliente.cpf}  -  ${cliente.email}",
				color = Color.White.copy(alpha = 0.48f),
				fontSize = 11.sp
			)
			if (cliente.planoNome.isNotBlank() || cliente.motoNome.isNotBlank()) {
				Spacer(Modifier.height(6.dp))
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					if (cliente.planoNome.isNotBlank()) {
						BadgePill(text = "Plano: ${cliente.planoNome}", accent = Color(0xFF7DD3FC))
					}
					if (cliente.motoNome.isNotBlank()) {
						BadgePill(text = "Moto: ${cliente.motoNome}", accent = Color(0xFF20E65B))
					}
				}
			}
		}
		Column(modifier = Modifier.weight(1.1f)) {
			Text(cliente.cityLabel, color = Color.White.copy(alpha = 0.80f), fontSize = 13.sp)
			Text(cliente.telefone, color = Color.White.copy(alpha = 0.42f), fontSize = 11.sp)
		}
		Box(modifier = Modifier.weight(1.1f)) {
			BadgePill(text = cliente.status.label(), accent = cliente.status.accent())
		}
		Text(
			formatDate(cliente.createdAt),
			modifier = Modifier.weight(1.0f),
			color = Color.White.copy(alpha = 0.68f),
			fontSize = 13.sp
		)
		ActionCell(
			onView = { onView(cliente) },
			onApprove = { onApprove(cliente) },
			onReject = { onReject(cliente) },
		)
	}
}

@Composable
internal fun ActionCell(
	onView: () -> Unit,
	onEdit: (() -> Unit)? = null,
	onApprove: (() -> Unit)?,
	onReject: (() -> Unit)?,
) {
	val width = when {
		onApprove != null || onReject != null -> 184.dp
		onEdit != null -> 96.dp
		else -> 56.dp
	}
	Row(
		modifier = Modifier.width(width),
		horizontalArrangement = Arrangement.End,
		verticalAlignment = Alignment.CenterVertically,
	) {
		RoundIconButton(
			icon = Icons.Default.Visibility,
			tint = Color.White.copy(alpha = 0.72f),
			onClick = onView
		)
		if (onEdit != null) {
			RoundIconButton(icon = Icons.Default.Edit, tint = Color(0xFF7DD3FC), onClick = onEdit)
		}
		if (onApprove != null) {
			RoundIconButton(
				icon = Icons.Default.Check,
				tint = Color(0xFF20E65B),
				onClick = onApprove
			)
		}
		if (onReject != null) {
			RoundIconButton(
				icon = Icons.Default.Close,
				tint = Color(0xFFFF4A4A),
				onClick = onReject
			)
		}
	}
}

@Composable
internal fun PagerBar(
	currentPage: Int,
	totalPages: Int,
	totalItems: Int,
	onPrev: () -> Unit,
	onNext: () -> Unit,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Text(
			text = "Mostrando página $currentPage de $totalPages · $totalItems registros",
			color = Color.White.copy(alpha = 0.42f),
			fontSize = 12.sp,
		)
		Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
			OutlinedButton(
				onClick = onPrev,
				enabled = currentPage > 1,
				shape = RoundedCornerShape(12.dp),
				border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
				colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
			) {
				Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(16.dp))
				Spacer(Modifier.width(6.dp))
				Text("Anterior")
			}
			Button(
				onClick = onNext,
				enabled = currentPage < totalPages,
				shape = RoundedCornerShape(12.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = Color(0xFF20E65B),
					contentColor = Color.Black,
				),
			) {
				Text("Próxima")
			}
		}
	}
}

