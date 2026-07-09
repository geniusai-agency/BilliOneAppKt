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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAltOff
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

@Composable
internal fun FilterPanel(
	search: String,
	onSearchChange: (String) -> Unit,
	statusFilter: ClienteAprovacaoStatus?,
	onStatusFilterChange: (ClienteAprovacaoStatus?) -> Unit,
	city: String,
	onCityChange: (String) -> Unit,
	state: String,
	onStateChange: (String) -> Unit,
	planosDisponiveis: List<PlanoResponse>,
	planoFilter: String?,
	onPlanoFilterChange: (String?) -> Unit,
	motoFilter: String,
	onMotoFilterChange: (String) -> Unit,
	onClearFilters: () -> Unit,
	compact: Boolean,
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(
			modifier = Modifier.padding(18.dp),
			verticalArrangement = Arrangement.spacedBy(14.dp)
		) {
			val activeCount = listOf(
				search.isNotBlank(),
				statusFilter != null,
				city.isNotBlank(),
				state.isNotBlank(),
				!planoFilter.isNullOrBlank(),
				motoFilter.isNotBlank(),
			).count { it }

			FilterPanelHeader(
				activeCount = activeCount,
				onClearFilters = onClearFilters,
			)

			SearchField(
				value = search,
				onValueChange = onSearchChange,
				placeholder = "Buscar cliente, CPF, email ou telefone...",
			)
			
			BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
				val narrow = maxWidth < 980.dp
				if (narrow) {
					Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
						Row(
							horizontalArrangement = Arrangement.spacedBy(12.dp),
							modifier = Modifier.fillMaxWidth()
						) {
							SelectFilterField(
								value = planoFilter,
								options = planosDisponiveis,
								label = "Plano",
								placeholder = "Todos",
								modifier = Modifier.weight(1f),
								onSelect = onPlanoFilterChange,
							)
							MiniFilterField(
								value = motoFilter,
								onValueChange = onMotoFilterChange,
								label = "Moto",
								placeholder = "Modelo ou placa",
								modifier = Modifier.weight(1f),
							)
						}
						Row(
							horizontalArrangement = Arrangement.spacedBy(12.dp),
							modifier = Modifier.fillMaxWidth()
						) {
							MiniFilterField(
								value = city,
								onValueChange = onCityChange,
								label = "Cidade",
								placeholder = "Qualquer",
								modifier = Modifier.weight(1f),
							)
							MiniFilterField(
								value = state,
								onValueChange = onStateChange,
								label = "UF",
								placeholder = "Qualquer",
								modifier = Modifier.width(120.dp),
							)
						}
					}
				} else {
					Row(
						horizontalArrangement = Arrangement.spacedBy(12.dp),
						modifier = Modifier.fillMaxWidth()
					) {
						SelectFilterField(
							value = planoFilter,
							options = planosDisponiveis,
							label = "Plano",
							placeholder = "Todos",
							modifier = Modifier.weight(1.1f),
							onSelect = onPlanoFilterChange,
						)
						MiniFilterField(
							value = motoFilter,
							onValueChange = onMotoFilterChange,
							label = "Moto",
							placeholder = "Modelo ou placa",
							modifier = Modifier.weight(1.3f),
						)
						MiniFilterField(
							value = city,
							onValueChange = onCityChange,
							label = "Cidade",
							placeholder = "Qualquer",
							modifier = Modifier.weight(1f),
						)
						MiniFilterField(
							value = state,
							onValueChange = onStateChange,
							label = "UF",
							placeholder = "Qualquer",
							modifier = Modifier.width(130.dp),
						)
					}
				}
			}
			
			FilterStrip(
				statusFilter = statusFilter,
				onStatusFilterChange = onStatusFilterChange,
				compact = compact,
			)
		}
	}
}

@Composable
private fun FilterPanelHeader(
	activeCount: Int,
	onClearFilters: () -> Unit,
) {
	val hasActive = activeCount > 0
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Icon(
				Icons.Default.FilterAlt,
				contentDescription = null,
				tint = if (hasActive) Color(0xFF20E65B) else Color.White.copy(alpha = 0.42f),
				modifier = Modifier.size(18.dp),
			)
			Text(
				"Filtros",
				color = Color.White,
				fontSize = 15.sp,
				fontWeight = FontWeight.SemiBold,
			)
			if (hasActive) FilterCountBadge(activeCount)
		}

		TextButton(
			onClick = onClearFilters,
			enabled = hasActive,
			contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
		) {
			Icon(
				Icons.Default.FilterAltOff,
				contentDescription = null,
				modifier = Modifier.size(16.dp),
				tint = if (hasActive) Color(0xFFFF4A4A) else Color.White.copy(alpha = 0.25f),
			)
			Spacer(Modifier.width(6.dp))
			Text(
				"Limpar tudo",
				color = if (hasActive) Color(0xFFFF6B6B) else Color.White.copy(alpha = 0.25f),
				fontSize = 12.sp,
				fontWeight = FontWeight.SemiBold,
			)
		}
	}
}

@Composable
private fun FilterCountBadge(count: Int) {
	Box(
		modifier = Modifier
			.background(Color(0xFF20E65B), CircleShape)
			.heightIn(min = 18.dp)
			.widthIn(min = 18.dp)
			.padding(horizontal = 6.dp),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = count.toString(),
			color = Color.Black,
			fontSize = 11.sp,
			fontWeight = FontWeight.Bold,
		)
	}
}

@Composable
internal fun SelectFilterField(
	value: String?,
	options: List<PlanoResponse>,
	label: String,
	placeholder: String,
	modifier: Modifier = Modifier,
	onSelect: (String?) -> Unit,
) {
	var expanded by remember { mutableStateOf(false) }
	val active = !value.isNullOrBlank()
	val selectedName = options.firstOrNull { it.id == value }?.nome ?: placeholder

	Box(modifier = modifier) {
		OutlinedTextField(
			value = selectedName,
			onValueChange = {},
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(min = 56.dp),
			label = { Text(label, color = if (active) Color(0xFF20E65B).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.45f)) },
			placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.30f)) },
			singleLine = true,
			readOnly = true,
			trailingIcon = {
				Icon(
					imageVector = Icons.Default.ArrowDropDown,
					contentDescription = null,
					tint = if (active) Color(0xFF20E65B) else Color.White.copy(alpha = 0.40f),
				)
			},
			shape = RoundedCornerShape(14.dp),
			colors = OutlinedTextFieldDefaults.colors(
				focusedTextColor = Color.White,
				unfocusedTextColor = Color.White,
				focusedContainerColor = Color.White.copy(alpha = 0.02f),
				unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
				focusedBorderColor = if (active) Color(0xFF20E65B).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.10f),
				unfocusedBorderColor = if (active) Color(0xFF20E65B).copy(alpha = 0.30f) else Color.White.copy(alpha = 0.08f),
				cursorColor = Color(0xFF20E65B),
			),
		)

		Box(
			modifier = Modifier
				.matchParentSize()
				.clickable { expanded = true },
		)
	}

	DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
		DropdownMenuItem(
			text = {
				Text(
					"Todos os planos",
					color = if (!active) Color(0xFF20E65B) else Color.White,
					fontWeight = if (!active) FontWeight.SemiBold else FontWeight.Normal,
				)
			},
			trailingIcon = if (!active) {
				{ Icon(Icons.Default.Check, null, tint = Color(0xFF20E65B), modifier = Modifier.size(18.dp)) }
			} else null,
			onClick = {
				onSelect(null)
				expanded = false
			},
		)
		options.forEach { plano ->
			val isSelected = plano.id == value
			DropdownMenuItem(
				text = {
					Text(
						plano.nome,
						color = if (isSelected) Color(0xFF20E65B) else Color.White,
						fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
					)
				},
				trailingIcon = if (isSelected) {
					{ Icon(Icons.Default.Check, null, tint = Color(0xFF20E65B), modifier = Modifier.size(18.dp)) }
				} else null,
				onClick = {
					onSelect(plano.id)
					expanded = false
				},
			)
		}
	}
}

@Composable
internal fun MiniFilterField(
	value: String,
	onValueChange: (String) -> Unit,
	label: String,
	placeholder: String,
	modifier: Modifier = Modifier,
) {
	val active = value.isNotBlank()
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		modifier = modifier.heightIn(min = 56.dp),
		label = { Text(label, color = if (active) Color(0xFF20E65B).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.45f)) },
		placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.30f)) },
		singleLine = true,
		shape = RoundedCornerShape(14.dp),
		trailingIcon = if (active) {
			{
				Icon(
					Icons.Default.Close,
					contentDescription = "Limpar $label",
					tint = Color.White.copy(alpha = 0.5f),
					modifier = Modifier.size(16.dp).clickable { onValueChange("") },
				)
			}
		} else null,
		colors = OutlinedTextFieldDefaults.colors(
			focusedTextColor = Color.White,
			unfocusedTextColor = Color.White,
			focusedContainerColor = Color.White.copy(alpha = 0.02f),
			unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
			focusedBorderColor = if (active) Color(0xFF20E65B).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.10f),
			unfocusedBorderColor = if (active) Color(0xFF20E65B).copy(alpha = 0.30f) else Color.White.copy(alpha = 0.08f),
			cursorColor = Color(0xFF20E65B),
		),
	)
}

@Composable
internal fun SearchField(
	value: String,
	onValueChange: (String) -> Unit,
	placeholder: String,
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		modifier = Modifier.fillMaxWidth().heightIn(min = 58.dp),
		placeholder = {
			Text(
				placeholder,
				color = Color.White.copy(alpha = 0.35f),
				fontSize = 14.sp
			)
		},
		singleLine = true,
		shape = RoundedCornerShape(14.dp),
		leadingIcon = {
			Icon(
				Icons.Default.Search,
				null,
				tint = if (value.isBlank()) Color.White.copy(alpha = 0.35f) else Color(0xFF20E65B),
			)
		},
		trailingIcon = {
			if (value.isNotBlank()) {
				Surface(
					onClick = { onValueChange("") },
					shape = CircleShape,
					color = Color.White.copy(alpha = 0.06f),
				) {
					Icon(
						Icons.Default.Close,
						contentDescription = "Limpar busca",
						tint = Color.White.copy(alpha = 0.6f),
						modifier = Modifier.padding(6.dp).size(16.dp),
					)
				}
			}
		},
		colors = OutlinedTextFieldDefaults.colors(
			focusedTextColor = Color.White,
			unfocusedTextColor = Color.White,
			focusedContainerColor = Color.White.copy(alpha = 0.02f),
			unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
			focusedBorderColor = if (value.isBlank()) Color.White.copy(alpha = 0.10f) else Color(0xFF20E65B).copy(alpha = 0.45f),
			unfocusedBorderColor = if (value.isBlank()) Color.White.copy(alpha = 0.08f) else Color(0xFF20E65B).copy(alpha = 0.30f),
			cursorColor = Color(0xFF20E65B),
		),
	)
}

@Composable
internal fun FilterStrip(
	statusFilter: ClienteAprovacaoStatus?,
	onStatusFilterChange: (ClienteAprovacaoStatus?) -> Unit,
	compact: Boolean,
) {
	val chips = listOf(
		null to "Todos",
		ClienteAprovacaoStatus.PENDENTE to "Pendentes",
		ClienteAprovacaoStatus.EM_ANALISE to "Em analise",
		ClienteAprovacaoStatus.APROVADO to "Aprovados",
		ClienteAprovacaoStatus.APROVADO_COM_RESSALVA to "Ressalva",
		ClienteAprovacaoStatus.REPROVADO to "Reprovados",
		ClienteAprovacaoStatus.AGUARDANDO_ASSINATURA to "Aguard. assin.",
	)
	
	Row(
		modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Icon(
			Icons.Default.FilterAlt,
			null,
			tint = Color.White.copy(alpha = 0.35f),
			modifier = Modifier.size(18.dp)
		)
		chips.forEach { (status, label) ->
			val selected = statusFilter == status
			Surface(
				onClick = { onStatusFilterChange(status) },
				shape = RoundedCornerShape(999.dp),
				color = if (selected) Color(0xFF20E65B).copy(alpha = 0.16f) else Color.White.copy(
					alpha = 0.03f
				),
				border = BorderStroke(
					1.dp,
					if (selected) Color(0xFF20E65B).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f),
				),
			) {
				Text(
					text = label,
					color = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.72f),
					fontSize = if (compact) 11.sp else 12.sp,
					fontWeight = FontWeight.SemiBold,
					modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
				)
			}
		}
	}
}

@Composable
internal fun AnalisesFilterStrip(
	statusFilter: ClienteAnalisePedidoStatus?,
	onStatusFilterChange: (ClienteAnalisePedidoStatus?) -> Unit,
	compact: Boolean,
) {
	val chips = listOf(
		null to "Todos",
		ClienteAnalisePedidoStatus.PENDENTE to "Pendentes",
		ClienteAnalisePedidoStatus.EM_ANALISE to "Em analise",
		ClienteAnalisePedidoStatus.APROVADO to "Aprovados",
		ClienteAnalisePedidoStatus.REPROVADO to "Reprovados",
		ClienteAnalisePedidoStatus.CANCELADO to "Cancelados",
	)
	
	Row(
		modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Icon(
			Icons.Default.FilterAlt,
			null,
			tint = Color.White.copy(alpha = 0.35f),
			modifier = Modifier.size(18.dp)
		)
		chips.forEach { (status, label) ->
			val selected = statusFilter == status
			Surface(
				onClick = { onStatusFilterChange(status) },
				shape = RoundedCornerShape(999.dp),
				color = if (selected) Color(0xFF20E65B).copy(alpha = 0.16f) else Color.White.copy(
					alpha = 0.03f
				),
				border = BorderStroke(
					1.dp,
					if (selected) Color(0xFF20E65B).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f),
				),
			) {
				Text(
					text = label,
					color = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.72f),
					fontSize = if (compact) 11.sp else 12.sp,
					fontWeight = FontWeight.SemiBold,
					modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
				)
			}
		}
	}
}

@Composable
internal fun TabStrip(
	tabs: List<ClientDetailTab>,
	activeTab: ClientDetailTab,
	onChange: (ClientDetailTab) -> Unit,
) {
	Row(
		modifier = Modifier.fillMaxWidth().background(Color(0xFF121712), RoundedCornerShape(16.dp))
			.padding(4.dp),
		horizontalArrangement = Arrangement.spacedBy(4.dp),
	) {
		tabs.forEach { tab ->
			val selected = tab == activeTab
			Surface(
				onClick = { onChange(tab) },
				shape = RoundedCornerShape(12.dp),
				color = if (selected) Color(0xFF0D100E) else Color.Transparent,
				border = BorderStroke(
					1.dp,
					if (selected) Color.White.copy(alpha = 0.08f) else Color.Transparent
				),
				modifier = Modifier.weight(1f),
			) {
				Text(
					text = tab.label,
					color = if (selected) Color.White else Color.White.copy(alpha = 0.60f),
					fontSize = 12.sp,
					fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
					textAlign = TextAlign.Center,
					modifier = Modifier.padding(vertical = 10.dp),
				)
			}
		}
	}
}

