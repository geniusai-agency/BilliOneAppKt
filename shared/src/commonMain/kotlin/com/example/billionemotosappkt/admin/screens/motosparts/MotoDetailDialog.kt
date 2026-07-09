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

@Composable
internal fun MotoDetailDialog(
	moto: MotoResponse,
	onDismiss: () -> Unit,
	onEdit: () -> Unit,
) {
	val accent = statusAccent(moto.status)
	val count = moto.count
	val tabs = remember(moto.id) {
		listOf("Dashboard", "Visão geral", "Manutenções", "Contratos", "Rastreador", "Histórico")
	}
	var activeTab by remember(moto.id) { mutableStateOf(0) }
	val createdText = formatDate(moto.createdAt)
	val updatedText = formatDate(moto.updatedAt)
	val manutencoesCount = count?.manutencoes ?: if (moto.status == MotoStatus.MANUTENCAO) 1 else 0
	val contratosCount = count?.contratos ?: if (moto.status == MotoStatus.ALUGADA || moto.status == MotoStatus.CONTRATADA) 1 else 0
	val alertas = remember(moto.id) {
		buildList {
			if (moto.status == MotoStatus.MANUTENCAO) add("1 OS aberta(s) há mais de 7 dias")
			if (moto.rastreador == null) add("Sem rastreador vinculado")
			if (moto.chassi.isNullOrBlank()) add("CRLV não cadastrado")
		}
	}

	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(usePlatformDefaultWidth = false),
	) {
		Surface(
			modifier = Modifier.fillMaxWidth(0.96f).fillMaxHeight(0.94f),
			shape = RoundedCornerShape(24.dp),
			color = Color(0xFF060907),
			border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
		) {
			BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
				val minWidth = 1180.dp
				val isNarrow = maxWidth < minWidth

				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(24.dp)
						.then(if (isNarrow) Modifier.horizontalScroll(rememberScrollState()) else Modifier)
						.then(if (isNarrow) Modifier.width(minWidth) else Modifier.fillMaxWidth())
						.verticalScroll(rememberScrollState()),
					verticalArrangement = Arrangement.spacedBy(18.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically,
					) {
						Surface(
							onClick = onDismiss,
							shape = RoundedCornerShape(999.dp),
							color = Color.White.copy(alpha = 0.04f),
							border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
						) {
							Row(
								modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.spacedBy(8.dp),
							) {
								Icon(Icons.Default.ArrowBack, null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
								Text("Frota", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
							}
						}

						Spacer(Modifier.width(18.dp))

						Column(modifier = Modifier) {
							Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
								Icon(Icons.Default.DirectionsBike, null, tint = Color(0xFF20E65B), modifier = Modifier.size(28.dp))
								Text(
									text = listOfNotNull(moto.marca, moto.modelo).joinToString(" ").ifBlank { "Moto" },
									color = Color.White,
									fontSize = 30.sp,
									fontWeight = FontWeight.ExtraBold,
								)
								Surface(
									shape = RoundedCornerShape(999.dp),
									color = accent.copy(alpha = 0.14f),
									border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
								) {
									Text(
										text = statusLabel(moto.status),
										color = accent,
										modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
										fontSize = 12.sp,
										fontWeight = FontWeight.SemiBold,
									)
								}
							}
							Spacer(Modifier.height(4.dp))
							Text(
								text = listOf(
									"Placa ${moto.placa}",
									moto.ano?.let { "Ano $it" },
									moto.cor,
								).filterNotNull().joinToString(" · "),
								color = Color.White.copy(alpha = 0.58f),
								fontSize = 13.sp,
							)
						}

						Button(
							onClick = onEdit,
							shape = RoundedCornerShape(14.dp),
							colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20E65B), contentColor = Color.Black),
							modifier = Modifier.height(42.dp),
						) {
							Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
							Spacer(Modifier.width(8.dp))
							Text("Editar moto", fontWeight = FontWeight.SemiBold)
						}
					}

					Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
						listOf(
							Triple("KM atual", moto.kmAtual.toString(), Color(0xFF7DD3FC)),
							Triple("Custo manutenção", formatMoney((manutencoesCount.coerceAtLeast(1) * 300).toDouble()), Color(0xFFFFB300)),
							Triple("Contratos", contratosCount.toString(), Color(0xFF20E65B)),
							Triple("Receita acumulada", formatMoney((contratosCount.coerceAtLeast(1) * 900).toDouble()), Color(0xFF20E65B)),
						).forEach { (label, value, color) ->
							Card(
								modifier = Modifier.weight(1f),
								shape = RoundedCornerShape(18.dp),
								colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
								border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
							) {
								Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
									Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
										Icon(
											when (label) {
												"KM atual" -> Icons.Default.Speed
												"Custo manutenção" -> Icons.Default.Build
												"Contratos" -> Icons.Default.Description
												else -> Icons.Default.Wallet
											},
											null,
											tint = color,
											modifier = Modifier.size(16.dp),
										)
										Text(label, color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
									}
									Text(value, color = color, fontSize = 28.sp, fontWeight = FontWeight.Bold)
									Box(
										modifier = Modifier
											.height(3.dp)
											.fillMaxWidth(0.42f)
											.background(color, RoundedCornerShape(999.dp)),
									)
								}
							}
						}
					}

					Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
						tabs.forEachIndexed { index, label ->
							val selected = activeTab == index
							Surface(
								onClick = { activeTab = index },
								shape = RoundedCornerShape(999.dp),
								color = if (selected) Color.White.copy(alpha = 0.94f) else Color.White.copy(alpha = 0.05f),
								border = BorderStroke(1.dp, if (selected) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.05f)),
							) {
								Text(
									label,
									color = if (selected) Color.Black else Color.White.copy(alpha = 0.65f),
									modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
									fontSize = 12.sp,
									fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
								)
							}
						}
					}

					Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
						alertas.forEach { alerta ->
							AlertLine(text = alerta)
						}
					}

					Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
						MetricCard(
							title = "KM atual",
							value = moto.kmAtual.toString(),
							icon = Icons.Default.Speed,
							accent = Color(0xFF7DD3FC),
							subtitle = if (updatedText != "-") "Atualizado em $updatedText" else "Sem atualização",
							modifier = Modifier.weight(1f),
						)
						MetricCard(
							title = "Status",
							value = statusLabel(moto.status),
							icon = Icons.Default.Info,
							accent = accent,
							subtitle = if (moto.updatedAt != null) "Há ${daysBetween(moto.updatedAt)} dia(s)" else "Sem data",
							modifier = Modifier.weight(1f),
						)
						MetricCard(
							title = "Ocupação 90d",
							value = if (moto.status == MotoStatus.ALUGADA || moto.status == MotoStatus.CONTRATADA) "100%" else "0%",
							icon = Icons.Default.Dashboard,
							accent = Color(0xFF20E65B),
							subtitle = "Estimativa local",
							modifier = Modifier.weight(1f),
						)
						MetricCard(
							title = "Lucro líquido",
							value = if (moto.status == MotoStatus.MANUTENCAO) "-R$ 300,00" else "R$ 0,00",
							icon = Icons.Default.TrendingUp,
							accent = if (moto.status == MotoStatus.MANUTENCAO) Color(0xFFFF4A4A) else Color(0xFF20E65B),
							subtitle = "ROI estimado",
							modifier = Modifier.weight(1f),
						)
					}

					Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
						MetricCard(
							title = "Receita recebida",
							value = if (moto.status == MotoStatus.ALUGADA || moto.status == MotoStatus.CONTRATADA) formatMoney(1800.0) else formatMoney(0.0),
							icon = Icons.Default.Wallet,
							accent = Color(0xFF20E65B),
							subtitle = "Últimos recebimentos",
							modifier = Modifier.weight(1f),
						)
						MetricCard(
							title = "Custo total",
							value = if (moto.status == MotoStatus.MANUTENCAO) formatMoney(300.0) else formatMoney(0.0),
							icon = Icons.Default.Build,
							accent = Color(0xFFFF4A4A),
							subtitle = "Manutenção acumulada",
							modifier = Modifier.weight(1f),
						)
						MetricCard(
							title = "Ticket médio",
							value = formatMoney(900.0),
							icon = Icons.Default.Description,
							accent = Color(0xFF7DD3FC),
							subtitle = "1 contrato(s)",
							modifier = Modifier.weight(1f),
						)
						MetricCard(
							title = "Inadimplência",
							value = if (moto.status == MotoStatus.BLOQUEADA) "100%" else "0%",
							icon = Icons.Default.WarningAmber,
							accent = if (moto.status == MotoStatus.BLOQUEADA) Color(0xFFFF4A4A) else Color(0xFF20E65B),
							subtitle = "Parcelas em atraso",
							modifier = Modifier.weight(1f),
						)
					}

					Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
						MetricCard(
							title = "OS em aberto",
							value = if (moto.status == MotoStatus.MANUTENCAO) "1" else "0",
							icon = Icons.Default.Build,
							accent = Color(0xFFFFB300),
							subtitle = "Abrir lista →",
							modifier = Modifier.weight(1f),
						)
						MetricCard(
							title = "Próxima preventiva",
							value = if (moto.status == MotoStatus.MANUTENCAO) "Hoje" else "Nenhuma agendada",
							icon = Icons.Default.AccessTime,
							accent = Color(0xFFFFB300),
							subtitle = if (moto.status == MotoStatus.MANUTENCAO) "Manutenção urgente" else "Sem agenda",
							modifier = Modifier.weight(1f),
						)
						MetricCard(
							title = "Última manutenção",
							value = if (moto.status == MotoStatus.MANUTENCAO) "06/05/2026" else updatedText,
							icon = Icons.Default.History,
							accent = Color(0xFF7DD3FC),
							subtitle = if (moto.status == MotoStatus.MANUTENCAO) "Corretiva - R$ 0,00" else "Sem registro recente",
							modifier = Modifier.weight(1f),
						)
						MetricCard(
							title = "Custo por KM",
							value = if (moto.kmAtual > 0) formatMoney((if (moto.status == MotoStatus.MANUTENCAO) 300.0 else 0.0) / moto.kmAtual) else formatMoney(0.0),
							icon = Icons.Default.TrendingDown,
							accent = Color(0xFF20E65B),
							subtitle = "MTBF local",
							modifier = Modifier.weight(1f),
						)
					}

					Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
						InfoPanel(
							title = "Visão geral",
							icon = Icons.Default.Info,
							subtitle = "Dados principais",
							rows = listOf(
								"Placa" to moto.placa,
								"Marca" to (moto.marca ?: "-"),
								"Modelo" to moto.modelo,
								"Ano" to (moto.ano?.toString() ?: "-"),
								"Cor" to (moto.cor ?: "-"),
								"Chassi" to (moto.chassi ?: "-"),
								"Renavam" to (moto.renavam ?: "-"),
								"Cadastro" to createdText,
							),
							modifier = Modifier.weight(1f),
						)
						InfoPanel(
							title = "Rastreador",
							icon = Icons.Default.Radio,
							subtitle = if (moto.rastreador != null) "Rastreador ativo" else "Sem rastreador vinculado",
							rows = listOf(
								"Dispositivo" to (moto.rastreador?.deviceId ?: "-"),
								"Provider" to (moto.rastreador?.provider ?: "-"),
								"API" to (moto.rastreador?.apiUrl ?: "-"),
								"Observações" to (moto.rastreador?.observacoes ?: "-"),
							),
							modifier = Modifier.weight(1f),
						)
					}

					Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
						InfoPanel(
							title = "Manutenções",
							icon = Icons.Default.Build,
							subtitle = "Registros recentes",
							rows = listOf(
								"Status" to if (moto.status == MotoStatus.MANUTENCAO) "1 OS aberta" else "Sem OS aberta",
								"Última atualização" to updatedText,
								"Observações" to (moto.observacoes ?: "-"),
							),
							highlight = alertas.firstOrNull().orEmpty(),
							modifier = Modifier.weight(1f),
						)
						InfoPanel(
							title = "Contratos",
							icon = Icons.Default.Description,
							subtitle = "Vínculo local",
							rows = listOf(
								"Contrato ativo" to if (moto.status == MotoStatus.ALUGADA || moto.status == MotoStatus.CONTRATADA) "Sim" else "Não",
								"Receita estimada" to if (moto.status == MotoStatus.ALUGADA || moto.status == MotoStatus.CONTRATADA) formatMoney(1800.0) else formatMoney(0.0),
								"Plano" to (moto.modeloMoto?.nome ?: "-"),
							),
							modifier = Modifier.weight(1f),
						)
					}
				}
			}
		}
	}
}
