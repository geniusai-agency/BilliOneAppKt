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
import com.example.billionemotosappkt.shared.api.FotoResponse
import com.example.billionemotosappkt.shared.utils.rememberImagePicker
import com.example.billionemotosappkt.desktop.admin.`fun`.desktopImagePainter
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
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
internal fun MotoDetailScreenLikeDialog(
	moto: MotoResponse,
	api: BillioneMotosApi,
	apiBaseUrl: String,
	apiAccessToken: String?,
	onDismiss: () -> Unit,
	onEdit: () -> Unit,
) {
	val accent = statusAccent(moto.status)
	val createdText = formatDate(moto.createdAt)
	val updatedText = formatDate(moto.updatedAt)
	val tabs = listOf("Dashboard", "Visão geral", "Fotos", "Manutenções", "Contratos", "Rastreador", "Histórico")
	var activeTab by remember(moto.id) { mutableStateOf(1) }
	var photos by remember(moto.id) { mutableStateOf<List<FotoResponse>>(emptyList()) }
	var isLoadingPhotos by remember(moto.id) { mutableStateOf(false) }
	var photoError by remember(moto.id) { mutableStateOf<String?>(null) }
	val scope = rememberCoroutineScope()

	LaunchedEffect(moto.id, activeTab) {
		if (activeTab == 2) {
			isLoadingPhotos = true
			runCatching {
				api.motos.listMotoPhotos(moto.id).items
			}.onSuccess {
				photos = it
				photoError = null
			}.onFailure {
				photoError = it.message ?: "Erro ao carregar fotos"
			}
			isLoadingPhotos = false
		}
	}

	val kmAtual = moto.kmAtual
	val manutencoesCount = moto.count?.manutencoes ?: if (moto.status == MotoStatus.MANUTENCAO) 1 else 0
	val contratosCount = moto.count?.contratos ?: if (moto.status == MotoStatus.ALUGADA || moto.status == MotoStatus.CONTRATADA) 1 else 0
	val dashboardAlerts = listOfNotNull(
		if (moto.status == MotoStatus.MANUTENCAO) "1 OS aberta(s) há mais de 7 dias" else null,
		if (moto.rastreador == null) "Sem rastreador vinculado" else null,
		if (moto.chassi.isNullOrBlank()) "CRLV não cadastrado" else null,
	)

	Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
		Surface(
			modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.94f),
			shape = RoundedCornerShape(24.dp),
			color = Color(0xFF060907),
			border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
		) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(24.dp),
				contentAlignment = Alignment.TopCenter,
			) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.widthIn(max = 1600.dp)
						.verticalScroll(rememberScrollState()),
					verticalArrangement = Arrangement.spacedBy(16.dp),
				) {
					Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
						Surface(
							onClick = onDismiss,
							shape = RoundedCornerShape(999.dp),
							color = Color.White.copy(alpha = 0.05f),
							border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
						) {
							Row(
								modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.spacedBy(8.dp),
							) {
								Icon(Icons.Default.ArrowBack, null, tint = Color.White.copy(alpha = 0.86f), modifier = Modifier.size(18.dp))
								Text("Frota", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
							}
						}

						Spacer(Modifier.width(16.dp))

						Column(modifier = Modifier.weight(1f)) {
							Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
								Icon(Icons.Default.DirectionsBike, null, tint = Color(0xFF20E65B), modifier = Modifier.size(30.dp))
								Text(
									text = listOfNotNull(moto.marca, moto.modelo).joinToString(" ").ifBlank { "Moto" },
									color = Color.White,
									fontSize = 30.sp,
									fontWeight = FontWeight.ExtraBold,
								)
								Surface(
									shape = RoundedCornerShape(999.dp),
									color = accent.copy(alpha = 0.18f),
									border = BorderStroke(1.dp, accent.copy(alpha = 0.24f)),
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
								text = listOf("Placa ${moto.placa}", moto.ano?.let { "Ano $it" }, moto.cor).filterNotNull().joinToString(" · "),
								color = Color.White.copy(alpha = 0.56f),
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
							Triple("KM atual", kmAtual.toString(), Color(0xFF7DD3FC)),
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

					Row(
						modifier = Modifier
							.fillMaxWidth()
							.background(Color(0xFF262A26), RoundedCornerShape(14.dp))
							.padding(4.dp),
						horizontalArrangement = Arrangement.spacedBy(4.dp),
					) {
						tabs.forEachIndexed { index, label ->
							val selected = activeTab == index
							Surface(
								onClick = { activeTab = index },
								shape = RoundedCornerShape(12.dp),
								color = if (selected) Color(0xFF0D100E) else Color.Transparent,
								border = BorderStroke(1.dp, if (selected) Color.White.copy(alpha = 0.06f) else Color.Transparent),
								modifier = Modifier.weight(1f),
							) {
								Row(
									modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
									verticalAlignment = Alignment.CenterVertically,
									horizontalArrangement = Arrangement.Center,
								) {
									val tabIcon = when (label) {
										"Dashboard" -> Icons.Default.Dashboard
										"Visão geral" -> Icons.Default.DirectionsBike
										"Manutenções" -> Icons.Default.Build
										"Contratos" -> Icons.Default.Description
										"Rastreador" -> Icons.Default.Radio
										else -> Icons.Default.History
									}
									Icon(tabIcon, null, tint = if (selected) Color.White else Color.White.copy(alpha = 0.42f), modifier = Modifier.size(15.dp))
									Spacer(Modifier.width(8.dp))
									Text(
										label,
										color = if (selected) Color.White else Color.White.copy(alpha = 0.58f),
										fontSize = 12.sp,
										fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
									)
								}
							}
						}
					}

					when (activeTab) {
						0 -> {
							Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
								dashboardAlerts.forEach { AlertLine(it) }
							}

							Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
								MetricCard("KM atual", kmAtual.toString(), Icons.Default.Speed, Color(0xFF7DD3FC), "Atualizado em $updatedText", Modifier.weight(1f))
								MetricCard("Status", statusLabel(moto.status), Icons.Default.Info, accent, if (moto.updatedAt != null) "Há ${daysBetween(moto.updatedAt)} dia(s)" else "Sem data", Modifier.weight(1f))
								MetricCard("Ocupação 90d", if (moto.status == MotoStatus.ALUGADA || moto.status == MotoStatus.CONTRATADA) "100%" else "0%", Icons.Default.Dashboard, Color(0xFF20E65B), "Estimativa local", Modifier.weight(1f))
								MetricCard("Lucro líquido", if (moto.status == MotoStatus.MANUTENCAO) "-R$ 300,00" else "R$ 0,00", Icons.Default.TrendingUp, if (moto.status == MotoStatus.MANUTENCAO) Color(0xFFFF4A4A) else Color(0xFF20E65B), "ROI estimado", Modifier.weight(1f))
							}

							Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
								MetricCard("Receita recebida", if (moto.status == MotoStatus.ALUGADA || moto.status == MotoStatus.CONTRATADA) formatMoney(1800.0) else formatMoney(0.0), Icons.Default.Wallet, Color(0xFF20E65B), "Últimos recebimentos", Modifier.weight(1f))
								MetricCard("Custo total", if (moto.status == MotoStatus.MANUTENCAO) formatMoney(300.0) else formatMoney(0.0), Icons.Default.Build, Color(0xFFFF4A4A), "Manutenção acumulada", Modifier.weight(1f))
								MetricCard("Ticket médio", formatMoney(900.0), Icons.Default.Description, Color(0xFF7DD3FC), "1 contrato(s)", Modifier.weight(1f))
								MetricCard("Inadimplência", if (moto.status == MotoStatus.BLOQUEADA) "100%" else "0%", Icons.Default.WarningAmber, if (moto.status == MotoStatus.BLOQUEADA) Color(0xFFFF4A4A) else Color(0xFF20E65B), "Parcelas em atraso", Modifier.weight(1f))
							}

							Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
								MetricCard("OS em aberto", if (moto.status == MotoStatus.MANUTENCAO) "1" else "0", Icons.Default.Build, Color(0xFFFFB300), "Abrir lista →", Modifier.weight(1f))
								MetricCard("Próxima preventiva", if (moto.status == MotoStatus.MANUTENCAO) "Hoje" else "Nenhuma agendada", Icons.Default.AccessTime, Color(0xFFFFB300), if (moto.status == MotoStatus.MANUTENCAO) "Manutenção urgente" else "Sem agenda", Modifier.weight(1f))
								MetricCard("Última manutenção", if (moto.status == MotoStatus.MANUTENCAO) "06/05/2026" else updatedText, Icons.Default.History, Color(0xFF7DD3FC), if (moto.status == MotoStatus.MANUTENCAO) "Corretiva - R$ 0,00" else "Sem registro recente", Modifier.weight(1f))
								MetricCard("Custo por KM", if (kmAtual > 0) formatMoney((if (moto.status == MotoStatus.MANUTENCAO) 300.0 else 0.0) / kmAtual) else formatMoney(0.0), Icons.Default.TrendingDown, Color(0xFF20E65B), "MTBF local", Modifier.weight(1f))
							}
						}

						1 -> {
							Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
								InfoPanel(
									title = "Dados do veículo",
									icon = Icons.Default.DirectionsBike,
									subtitle = "Dados principais",
									rows = listOf(
										"Modelo" to moto.modelo,
										"Marca" to (moto.marca ?: "-"),
										"Placa" to moto.placa,
										"Ano" to (moto.ano?.toString() ?: "-"),
										"Cor" to (moto.cor ?: "-"),
										"Chassi" to (moto.chassi ?: "-"),
										"Renavam" to (moto.renavam ?: "-"),
										"KM atual" to "${moto.kmAtual} km",
										"Cadastrada em" to createdText,
									),
									modifier = Modifier.weight(1f),
								)
								InfoPanel(
									title = "Situação atual",
									icon = Icons.Default.Info,
									subtitle = if (moto.status == MotoStatus.ALUGADA || moto.status == MotoStatus.CONTRATADA) "Contrato ativo no momento." else "Nenhum contrato ativo no momento.",
									rows = listOf(
										"Status" to statusLabel(moto.status),
										"Rastreador" to if (moto.rastreador != null) "Ativo" else "Sem rastreador vinculado",
										"Última atualização" to updatedText,
										"Observações" to (moto.observacoes ?: "-"),
									),
									modifier = Modifier.weight(1f),
								)
							}

							Card(
								shape = RoundedCornerShape(18.dp),
								colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
								border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
								modifier = Modifier.fillMaxWidth(),
							) {
								Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
									Text("Composição de custos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
									Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
										MetricCard("Peças", formatMoney(180.0), Icons.Default.Build, Color(0xFF7DD3FC), "", Modifier.weight(1f))
										MetricCard("Mão de obra", formatMoney(120.0), Icons.Default.Build, Color(0xFFFFB300), "", Modifier.weight(1f))
										MetricCard("Total", formatMoney(300.0), Icons.Default.Wallet, Color.White, "", Modifier.weight(1f))
									}
								}
							}
						}

						2 -> {
							Card(
								shape = RoundedCornerShape(18.dp),
								colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
								border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
								modifier = Modifier.fillMaxWidth(),
							) {
								Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
									Row(
										modifier = Modifier.fillMaxWidth(),
										horizontalArrangement = Arrangement.SpaceBetween,
										verticalAlignment = Alignment.CenterVertically
									) {
										Text("Fotos da Moto", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
										
										val pickImage = rememberImagePicker { fileRequest ->
											if (fileRequest != null) {
												scope.launch {
													runCatching {
														api.motos.addMotoPhoto(moto.id, fileRequest)
													}.onSuccess {
														photos = api.motos.listMotoPhotos(moto.id).items
														photoError = null
													}.onFailure {
														photoError = it.message ?: "Erro ao enviar foto"
													}
												}
											}
										}
										
										Button(
											onClick = { pickImage() },
											shape = RoundedCornerShape(12.dp),
											colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20E65B), contentColor = Color.Black),
										) {
											Icon(Icons.Default.Image, null, modifier = Modifier.size(16.dp))
											Spacer(Modifier.width(8.dp))
											Text("Adicionar Foto", fontWeight = FontWeight.SemiBold)
										}
									}
									
									if (isLoadingPhotos) {
										Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
											CircularProgressIndicator(color = Color(0xFF20E65B))
										}
									} else {
										if (photoError != null) {
											Text(photoError!!, color = Color(0xFFFF4A4A), fontSize = 13.sp)
										}
										
										if (photos.isEmpty() && moto.fotoUrls.isEmpty()) {
											Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
												Text("Nenhuma foto cadastrada para esta moto.", color = Color.White.copy(alpha = 0.4f))
											}
										} else {
											Row(
												modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 8.dp),
												horizontalArrangement = Arrangement.spacedBy(14.dp)
											) {
												photos.forEach { photo ->
													Box(
														modifier = Modifier
															.size(160.dp, 120.dp)
															.clip(RoundedCornerShape(12.dp))
															.border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
													) {
														Image(
															painter = desktopImagePainter(
																photo.url?.takeIf { it.isNotBlank() } ?: "/arquivos/${photo.id}/render",
																apiBaseUrl,
																apiAccessToken
															),
															contentDescription = null,
															modifier = Modifier.fillMaxSize(),
															contentScale = androidx.compose.ui.layout.ContentScale.Crop
														)
														IconButton(
															onClick = {
																scope.launch {
																	runCatching {
																		api.motos.deleteMotoPhoto(moto.id, photo.id)
																	}.onSuccess {
																		photos = api.motos.listMotoPhotos(moto.id).items
																		photoError = null
																	}.onFailure {
																		photoError = it.message ?: "Erro ao remover foto"
																	}
																}
															},
															modifier = Modifier
																.align(Alignment.TopEnd)
																.padding(6.dp)
																.size(28.dp)
																.clip(RoundedCornerShape(999.dp))
																.background(Color.Black.copy(alpha = 0.6f))
														) {
															Icon(Icons.Default.Delete, null, tint = Color(0xFFFF4A4A), modifier = Modifier.size(16.dp))
														}
													}
												}
												if (photos.isEmpty() && moto.fotoUrls.isNotEmpty()) {
													moto.fotoUrls.forEach { urlStr ->
														Box(
															modifier = Modifier
																.size(160.dp, 120.dp)
																.clip(RoundedCornerShape(12.dp))
																.border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
														) {
															Image(
																painter = desktopImagePainter(urlStr, apiBaseUrl, apiAccessToken),
																contentDescription = null,
																modifier = Modifier.fillMaxSize(),
																contentScale = androidx.compose.ui.layout.ContentScale.Crop
															)
														}
													}
												}
											}
										}
									}
								}
							}
						}

						3 -> {
							Card(
								shape = RoundedCornerShape(18.dp),
								colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
								border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
								modifier = Modifier.fillMaxWidth(),
							) {
								Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
									Text("Manutenções", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
									HistoryCard(
										title = "OS #001",
										subtitle = "aberta",
										right = updatedText,
										description = "Aba de manutenção disponível para expansão futura.",
										accent = Color(0xFFFFB300),
									)
								}
							}
						}

						4 -> {
							Card(
								shape = RoundedCornerShape(18.dp),
								colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
								border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
								modifier = Modifier.fillMaxWidth(),
							) {
								Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
									Text("Contratos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
									InfoPanel(
										title = "Resumo",
										icon = Icons.Default.Description,
										subtitle = "Vínculo local",
										rows = listOf(
											"Contrato ativo" to if (contratosCount > 0) "Sim" else "Não",
											"Plano" to (moto.modeloMoto?.nome ?: "-"),
											"Receita estimada" to if (contratosCount > 0) formatMoney(900.0) else formatMoney(0.0),
										),
										modifier = Modifier.fillMaxWidth(),
									)
								}
							}
						}

						5 -> {
							Card(
								shape = RoundedCornerShape(18.dp),
								colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
								border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
								modifier = Modifier.fillMaxWidth(),
							) {
								Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
									Text("Rastreador", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
									InfoPanel(
										title = "Dispositivo",
										icon = Icons.Default.Radio,
										subtitle = if (moto.rastreador != null) "Rastreador ativo" else "Sem rastreador vinculado",
										rows = listOf(
											"Provider" to (moto.rastreador?.provider ?: "-"),
											"Dispositivo" to (moto.rastreador?.deviceId ?: "-"),
											"API" to (moto.rastreador?.apiUrl ?: "-"),
											"Observações" to (moto.rastreador?.observacoes ?: "-"),
										),
										modifier = Modifier.fillMaxWidth(),
									)
								}
							}
						}

						else -> {
							Card(
								shape = RoundedCornerShape(18.dp),
								colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
								border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
								modifier = Modifier.fillMaxWidth(),
							) {
								Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
									Text("Histórico", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
									HistoryCard(
										title = "Cadastro",
										subtitle = "Moto criada",
										right = createdText,
										description = "Registro inicial da moto no sistema.",
										accent = Color(0xFF20E65B),
									)
									HistoryCard(
										title = "Atualização",
										subtitle = "Última alteração",
										right = updatedText,
										description = "Última atualização local do cadastro.",
										accent = Color(0xFF7DD3FC),
									)
								}
							}
						}
					}
				}
			}
		}
	}
}
