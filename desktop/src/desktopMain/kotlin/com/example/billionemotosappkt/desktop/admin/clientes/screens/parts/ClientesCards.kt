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
internal fun Header(
	selectedTab: ClientesSectionTab,
	compact: Boolean,
	clientesCount: Int,
	analisesCount: Int,
	onTabChange: (ClientesSectionTab) -> Unit,
	onRefresh: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
				Text(
					text = "Clientes",
					color = Color.White,
					fontSize = if (compact) 28.sp else 34.sp,
					fontWeight = FontWeight.Bold,
				)
				Text(
					text = "$clientesCount clientes e $analisesCount pedidos em fila",
					color = Color.White.copy(alpha = 0.42f),
					fontSize = 13.sp,
				)
			}
			
			OutlinedButton(
				onClick = onRefresh,
				shape = RoundedCornerShape(14.dp),
				border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
				colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
				contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp),
			) {
				Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
				Spacer(Modifier.width(8.dp))
				Text("Atualizar", fontWeight = FontWeight.SemiBold)
			}
		}
		
		ClientesSubsectionToggle(
			selected = selectedTab,
			onSelected = onTabChange,
		)
	}
}

@Composable
internal fun SummaryRow(
	items: List<Pair<String, Int>>,
	compact: Boolean,
) {
	BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
		val narrow = maxWidth < 980.dp
		if (narrow) {
			Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
				items.chunked(2).forEach { row ->
					Row(
						horizontalArrangement = Arrangement.spacedBy(10.dp),
						modifier = Modifier.fillMaxWidth()
					) {
						row.forEach {
							SummaryCard(
								label = it.first,
								value = it.second.toString(),
								modifier = Modifier.weight(1f)
							)
						}
						if (row.size == 1) Spacer(Modifier.weight(1f))
					}
				}
			}
		} else {
			Row(
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				modifier = Modifier.fillMaxWidth()
			) {
				items.forEach {
					SummaryCard(
						label = it.first,
						value = it.second.toString(),
						modifier = Modifier.weight(1f)
					)
				}
			}
		}
	}
}

@Composable
internal fun SummaryCard(
	label: String,
	value: String,
	modifier: Modifier = Modifier,
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Text(
				label,
				color = Color.White.copy(alpha = 0.42f),
				fontSize = 11.sp,
				fontWeight = FontWeight.SemiBold
			)
			Text(value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
			Box(
				modifier = Modifier
					.height(3.dp)
					.fillMaxWidth(0.45f)
					.background(Color(0xFF20E65B).copy(alpha = 0.65f), RoundedCornerShape(999.dp)),
			)
		}
	}
}

@Composable
internal fun DetailCard(
	title: String,
	rows: List<Pair<String, String>>,
	body: (@Composable () -> Unit)? = null,
) {
	Card(
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(
			modifier = Modifier.padding(18.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
			rows.forEach { (label, value) ->
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(12.dp)
				) {
					Text(
						label,
						color = Color.White.copy(alpha = 0.42f),
						fontSize = 12.sp,
						modifier = Modifier.weight(0.38f)
					)
					Text(
						value,
						color = Color.White.copy(alpha = 0.82f),
						fontSize = 12.sp,
						modifier = Modifier.weight(0.62f),
						textAlign = TextAlign.End
					)
				}
			}
			body?.invoke()
		}
	}
}

@Composable
internal fun MiniMetricCard(
	title: String,
	value: String,
	modifier: Modifier = Modifier,
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Text(
				title,
				color = Color.White.copy(alpha = 0.42f),
				fontSize = 11.sp,
				fontWeight = FontWeight.SemiBold
			)
			Text(
				value.ifBlank { "-" },
				color = Color.White,
				fontSize = 18.sp,
				fontWeight = FontWeight.Bold
			)
			Box(
				modifier = Modifier
					.height(3.dp)
					.fillMaxWidth(0.45f)
					.background(Color(0xFF20E65B).copy(alpha = 0.65f), RoundedCornerShape(999.dp)),
			)
		}
	}
}

@Composable
internal fun CompactMetaCard(
	title: String,
	value: String,
	modifier: Modifier = Modifier,
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(14.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.025f)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(
			modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp),
		) {
			Text(title, color = Color.White.copy(alpha = 0.40f), fontSize = 10.sp)
			Text(
				value,
				color = Color.White,
				fontSize = 12.sp,
				fontWeight = FontWeight.SemiBold,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		}
	}
}

@Composable
internal fun MotoSelectionHero(
	moto: MotoResponse,
	onClick: () -> Unit,
) {
	Surface(
		onClick = onClick,
		shape = RoundedCornerShape(16.dp),
		color = Color(0xFF0E1710),
		border = BorderStroke(1.dp, Color(0xFF20E65B).copy(alpha = 0.34f)),
		modifier = Modifier.fillMaxWidth(),
	) {
		Row(
			modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Box(
				modifier = Modifier
					.size(42.dp)
					.background(Color(0xFF20E65B).copy(alpha = 0.16f), CircleShape),
				contentAlignment = Alignment.Center,
			) {
				Icon(Icons.Default.Check, null, tint = Color(0xFF20E65B), modifier = Modifier.size(20.dp))
			}
			Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
				Text(
					"Selecionada",
					color = Color(0xFF20E65B),
					fontSize = 10.sp,
					fontWeight = FontWeight.Bold,
				)
				Text(
					motoLabel(moto),
					color = Color.White,
					fontSize = 13.sp,
					fontWeight = FontWeight.SemiBold,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
				Text(
					listOfNotNull(
						moto.marca?.trim()?.takeIf { it.isNotBlank() },
						moto.ano?.toString(),
						moto.cor?.trim()?.takeIf { it.isNotBlank() },
					).joinToString(" • ").ifBlank { moto.status.name.lowercase().replace('_', ' ') },
					color = Color.White.copy(alpha = 0.45f),
					fontSize = 11.sp,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			}
			Surface(
				shape = RoundedCornerShape(999.dp),
				color = Color(0xFF20E65B).copy(alpha = 0.12f),
				border = BorderStroke(1.dp, Color(0xFF20E65B).copy(alpha = 0.18f)),
			) {
				Text(
					"Trocar",
					color = Color(0xFF20E65B),
					fontSize = 10.sp,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
				)
			}
		}
	}
}

@Composable
internal fun MotoSelectionTile(
	moto: MotoResponse,
	selected: Boolean,
	wideMode: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val accent = if (selected) Color(0xFF20E65B) else Color.White
	Surface(
		onClick = onClick,
		shape = RoundedCornerShape(16.dp),
		color = if (selected) Color(0xFF0E1710) else Color.White.copy(alpha = 0.025f),
		border = BorderStroke(
			1.dp,
			if (selected) Color(0xFF20E65B).copy(alpha = 0.34f) else Color.White.copy(alpha = 0.06f),
		),
		shadowElevation = if (selected) 3.dp else 0.dp,
		modifier = modifier.heightIn(min = if (wideMode) 112.dp else 96.dp),
	) {
		Column(
			modifier = Modifier.padding(horizontal = 14.dp, vertical = if (wideMode) 14.dp else 12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Box(
					modifier = Modifier
						.size(34.dp)
						.background(accent.copy(alpha = 0.14f), CircleShape),
					contentAlignment = Alignment.Center,
				) {
					Text(
						text = moto.modelo.take(1).uppercase(),
						color = accent,
						fontSize = 12.sp,
						fontWeight = FontWeight.Bold,
					)
				}
				Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
					Text(
						motoLabel(moto),
						color = Color.White,
						fontWeight = FontWeight.SemiBold,
						fontSize = if (wideMode) 13.sp else 12.sp,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
					)
					Text(
						listOfNotNull(
							moto.marca?.trim()?.takeIf { it.isNotBlank() },
							moto.ano?.toString(),
							moto.cor?.trim()?.takeIf { it.isNotBlank() },
						).joinToString(" • ").ifBlank { moto.status.name.lowercase().replace('_', ' ') },
						color = Color.White.copy(alpha = 0.44f),
						fontSize = 10.sp,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
					)
				}
				if (selected) {
					Surface(
						shape = RoundedCornerShape(999.dp),
						color = Color(0xFF20E65B).copy(alpha = 0.12f),
						border = BorderStroke(1.dp, Color(0xFF20E65B).copy(alpha = 0.18f)),
					) {
						Text(
							"Atual",
							color = Color(0xFF20E65B),
							fontSize = 9.sp,
							fontWeight = FontWeight.Bold,
							modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
						)
					}
				}
			}
			Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
				Surface(
					shape = RoundedCornerShape(999.dp),
					color = Color(0xFF20E65B).copy(alpha = 0.12f),
					border = BorderStroke(1.dp, Color(0xFF20E65B).copy(alpha = 0.18f)),
				) {
					Text(
						"DISPONIVEL",
						color = Color(0xFF20E65B),
						fontSize = 9.sp,
						fontWeight = FontWeight.Bold,
						modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
					)
				}
				Text(
					"Placa ${moto.placa}",
					color = Color.White.copy(alpha = 0.42f),
					fontSize = 10.sp,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			}
		}
	}
}

