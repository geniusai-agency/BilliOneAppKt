package com.example.billionemotosappkt.desktop.admin.screens

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
fun MotosSection(
	api: BillioneMotosApi,
	apiBaseUrl: String,
	apiAccessToken: String?,
	selectedTab: MotoSectionTab,
	onTabChange: (MotoSectionTab) -> Unit,
) {
	var uiState by remember { mutableStateOf(MotosUiState()) }
	var filters by remember { mutableStateOf(MotosFilters()) }
	var editingMoto by remember { mutableStateOf<MotoResponse?>(null) }
	var viewingMoto by remember { mutableStateOf<MotoResponse?>(null) }
	var savingMoto by remember { mutableStateOf(false) }
	var motoEditError by remember { mutableStateOf<String?>(null) }
	val scope = rememberCoroutineScope()
	
	LaunchedEffect(api) {
		uiState = uiState.copy(isLoading = true)
		runCatching {
			api.motos.list(ListMotosQuery(page = 1, limit = 50)).items
		}.onSuccess {
			uiState = uiState.copy(isLoading = false, motos = it, errorMessage = null)
		}.onFailure {
			uiState = uiState.copy(
				isLoading = false,
				errorMessage = it.message ?: "Erro ao carregar motos"
			)
		}
	}
	
	val filteredMotos = remember(uiState.motos, filters) {
		uiState.motos.filter { moto ->
			val matchesQuery = filters.query.isBlank() ||
					moto.modelo?.contains(filters.query, ignoreCase = true) == true ||
					moto.placa.contains(filters.query, ignoreCase = true) ||
					moto.marca?.contains(filters.query, ignoreCase = true) == true
			
			val matchesStatus = filters.status == null || moto.status == filters.status
			
			matchesQuery && matchesStatus
		}
	}
	
	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.spacedBy(24.dp),
	) {
		// Header conforme imagem do Front Web
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(
					Icons.Default.TwoWheeler, 
					contentDescription = null, 
					tint = Color(0xFFFFB300), // Dourado como no ícone da imagem
					modifier = Modifier.size(36.dp)
				)
				Spacer(Modifier.width(16.dp))
				Column {
					Text(
						if (selectedTab == MotoSectionTab.MODELOS) "Modelos de Motos" else "Frota de Motos", 
						color = Color.White, 
						fontSize = 32.sp, 
						fontWeight = FontWeight.Bold
					)
					Text(
						"${uiState.motos.size} cadastradas", 
						color = Color.White.copy(0.4f), 
						fontSize = 14.sp
					)
				}
			}
			
			Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				OutlinedButton(
					onClick = { /* Exportar PDF */ },
					shape = RoundedCornerShape(12.dp),
					colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
					border = BorderStroke(1.dp, Color.White.copy(0.1f)),
					modifier = Modifier.height(48.dp)
				) {
					Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(20.dp))
					Spacer(Modifier.width(8.dp))
					Text("Exportar PDF", fontWeight = FontWeight.SemiBold)
				}
				
				Button(
					onClick = { /* Nova Moto */ },
					shape = RoundedCornerShape(12.dp),
					colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20E65B), contentColor = Color.Black),
					modifier = Modifier.height(48.dp)
				) {
					Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
					Spacer(Modifier.width(8.dp))
					Text(if (selectedTab == MotoSectionTab.MODELOS) "Novo modelo" else "Nova moto", fontWeight = FontWeight.SemiBold)
				}
			}
		}

		when (selectedTab) {
			MotoSectionTab.FROTA -> {
				if (uiState.isLoading) {
					Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
						CircularProgressIndicator(color = Color(0xFF20E65B))
					}
				} else if (uiState.errorMessage != null) {
					Card(
						colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(0.1f)),
						border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.2f))
					) {
						Text(
							text = uiState.errorMessage!!,
							color = Color(0xFFFF4A4A),
							modifier = Modifier.padding(16.dp)
						)
					}
				} else {
					MotosList(
						motos = filteredMotos,
						onView = { viewingMoto = it },
						onEdit = { editingMoto = it },
						onDelete = { /* manter por enquanto */ },
					)
				}
			}
			
			MotoSectionTab.MODELOS -> {
				MotoModelosSection(
					api = api,
					apiBaseUrl = apiBaseUrl,
					apiAccessToken = apiAccessToken
				)
			}
		}

		editingMoto?.let { moto ->
			MotoEditDialogExpanded(
				moto = moto,
				isSaving = savingMoto,
				errorMessage = motoEditError,
				apiBaseUrl = apiBaseUrl,
				apiAccessToken = apiAccessToken,
				onDismiss = {
					editingMoto = null
					motoEditError = null
				},
				onSave = { request ->
					savingMoto = true
					motoEditError = null
					scope.launch {
						runCatching {
							api.motos.update(moto.id, request)
						}.onSuccess {
							editingMoto = null
							runCatching {
								api.motos.list(ListMotosQuery(page = 1, limit = 50)).items
							}.onSuccess { motos ->
								uiState = uiState.copy(isLoading = false, motos = motos, errorMessage = null)
							}.onFailure { refreshError ->
								motoEditError = refreshError.message ?: "Moto salva, mas falha ao recarregar a lista."
							}
						}.onFailure { error ->
							motoEditError = error.message ?: "Falha ao salvar a moto."
						}
						savingMoto = false
					}
				},
			)
		}

		viewingMoto?.let { moto ->
			MotoDetailScreenLikeDialog(
				moto = moto,
				onDismiss = {
					viewingMoto = null
				},
				onEdit = {
					editingMoto = moto
				},
			)
		}
	}
}

@Composable
private fun MotoDetailScreenLikeDialog(
	moto: MotoResponse,
	onDismiss: () -> Unit,
	onEdit: () -> Unit,
) {
	val accent = statusAccent(moto.status)
	val createdText = formatDate(moto.createdAt)
	val updatedText = formatDate(moto.updatedAt)
	val tabs = listOf("Dashboard", "Visão geral", "Manutenções", "Contratos", "Rastreador", "Histórico")
	var activeTab by remember(moto.id) { mutableStateOf(1) }
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

						3 -> {
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

						4 -> {
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

@Composable
private fun MotoDetailDialog(
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

@Composable
private fun MotoStatCard(
	title: String,
	value: String,
	icon: ImageVector,
	accent: Color,
	modifier: Modifier = Modifier,
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				Icon(icon, null, tint = accent, modifier = Modifier.size(16.dp))
				Text(title, color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
			}
			Text(value, color = accent, fontSize = 28.sp, fontWeight = FontWeight.Bold)
		}
	}
}

@Composable
private fun MetricCard(
	title: String,
	value: String,
	icon: ImageVector,
	accent: Color,
	subtitle: String,
	modifier: Modifier = Modifier,
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				Icon(icon, null, tint = accent, modifier = Modifier.size(15.dp))
				Text(title, color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
			}
			Text(value, color = accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
			if (subtitle.isNotBlank()) {
				Text(subtitle, color = Color.White.copy(alpha = 0.38f), fontSize = 11.sp)
			}
		}
	}
}

@Composable
private fun AlertLine(text: String) {
	Card(
		shape = RoundedCornerShape(14.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF3A180F).copy(alpha = 0.55f)),
		border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.45f)),
	) {
		Row(
			modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Icon(Icons.Default.WarningAmber, null, tint = Color(0xFFFF4A4A), modifier = Modifier.size(18.dp))
			Text(text, color = Color.White.copy(alpha = 0.88f), fontSize = 13.sp)
		}
	}
}

@Composable
private fun InfoPanel(
	title: String,
	icon: ImageVector,
	subtitle: String,
	rows: List<Pair<String, String>>,
	modifier: Modifier = Modifier,
	highlight: String? = null,
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				Icon(icon, null, tint = Color(0xFF20E65B), modifier = Modifier.size(16.dp))
				Column {
					Text(title, color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
					Text(subtitle, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
				}
			}
			rows.forEach { (label, value) ->
				Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
					Text(label, color = Color.White.copy(alpha = 0.42f), fontSize = 12.sp, modifier = Modifier.weight(0.42f))
					Text(value, color = Color.White.copy(alpha = 0.84f), fontSize = 12.sp, modifier = Modifier.weight(0.58f), textAlign = TextAlign.End)
				}
			}
			if (!highlight.isNullOrBlank()) {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.border(1.dp, Color(0xFFFFB300).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
						.background(Color(0xFFFFB300).copy(alpha = 0.08f), RoundedCornerShape(12.dp))
						.padding(horizontal = 12.dp, vertical = 10.dp),
				) {
					Text(highlight, color = Color.White.copy(alpha = 0.82f), fontSize = 12.sp)
				}
			}
		}
	}
}

@Composable
private fun MotoAlertRow(text: String) {
	Card(
		shape = RoundedCornerShape(14.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB300).copy(alpha = 0.08f)),
		border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.25f)),
	) {
		Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
			Icon(Icons.Default.WarningAmber, null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
			Spacer(Modifier.width(10.dp))
			Text(text, color = Color.White.copy(alpha = 0.82f), fontSize = 13.sp)
		}
	}
}

@Composable
private fun SectionCard(
	title: String,
	icon: ImageVector,
	heading: String,
	rows: List<Pair<String, String>> = emptyList(),
	body: (@Composable () -> Unit)? = null,
) {
	Card(
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				Icon(icon, null, tint = Color(0xFF20E65B), modifier = Modifier.size(16.dp))
				Text(title, color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
			}
			Text(heading, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
			rows.forEach { (label, value) ->
				Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
					Text(label, color = Color.White.copy(alpha = 0.42f), fontSize = 12.sp, modifier = Modifier.weight(0.42f))
					Text(value, color = Color.White.copy(alpha = 0.84f), fontSize = 12.sp, modifier = Modifier.weight(0.58f), textAlign = TextAlign.End)
				}
			}
			body?.invoke()
		}
	}
}

@Composable
private fun HistoryCard(
	title: String,
	subtitle: String,
	right: String,
	description: String,
	accent: Color,
) {
	Card(
		shape = RoundedCornerShape(14.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
		border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
	) {
		Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
				Column(modifier = Modifier.weight(1f)) {
					Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
					Text(subtitle, color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
				}
				Text(right, color = Color.White.copy(alpha = 0.35f), fontSize = 11.sp)
			}
			Text(description.ifBlank { "-" }, color = Color.White.copy(alpha = 0.72f), fontSize = 12.sp, lineHeight = 16.sp)
		}
	}
}

@Composable
private fun EmptyInline(text: String) {
	Box(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
		Text(text, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
	}
}

@Composable
private fun MotoEditDialog(
	moto: MotoResponse,
	isSaving: Boolean,
	errorMessage: String?,
	apiBaseUrl: String,
	apiAccessToken: String?,
	onDismiss: () -> Unit,
	onSave: (UpdateMotoRequest) -> Unit,
) {
	var modelo by remember(moto.id) { mutableStateOf(moto.modelo) }
	var placa by remember(moto.id) { mutableStateOf(moto.placa) }
	var marca by remember(moto.id) { mutableStateOf(moto.marca.orEmpty()) }
	var chassi by remember(moto.id) { mutableStateOf(moto.chassi.orEmpty()) }
	var renavam by remember(moto.id) { mutableStateOf(moto.renavam.orEmpty()) }
	var cor by remember(moto.id) { mutableStateOf(moto.cor.orEmpty()) }
	var ano by remember(moto.id) { mutableStateOf(moto.ano?.toString().orEmpty()) }
	var kmAtual by remember(moto.id) { mutableStateOf(moto.kmAtual.toString()) }
	var logicaVeiculoId by remember(moto.id) { mutableStateOf(moto.logicaVeiculoId.orEmpty()) }
	var status by remember(moto.id) { mutableStateOf(moto.status) }
	var observacoes by remember(moto.id) { mutableStateOf(moto.observacoes.orEmpty()) }
	var imageValue by remember(moto.id) { mutableStateOf(moto.fotoPrincipalUrl.orEmpty()) }
	var imageUpload by remember(moto.id) { mutableStateOf<com.example.billionemotosappkt.shared.api.UploadFileRequest?>(null) }

	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(usePlatformDefaultWidth = false),
	) {
		Surface(
			modifier = Modifier.fillMaxWidth(0.90f).fillMaxHeight(0.90f),
			shape = RoundedCornerShape(24.dp),
			color = Color(0xFF060907),
			border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
		) {
			Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
				Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
					Text("Editar moto", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
					Text("${moto.placa} · ${moto.modelo}", color = Color.White.copy(alpha = 0.45f), fontSize = 13.sp)
				}

				if (errorMessage != null) {
					Card(
						colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(alpha = 0.08f)),
						border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.25f)),
					) {
						Text(errorMessage, color = Color.White, modifier = Modifier.padding(12.dp))
					}
				}

				Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
					Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
						OutlinedTextField(
							value = marca,
							onValueChange = { marca = it },
							modifier = Modifier.weight(1f),
							label = { Text("Marca") },
							singleLine = true,
						)
						OutlinedTextField(
							value = modelo,
							onValueChange = { modelo = it },
							modifier = Modifier.weight(1.2f),
							label = { Text("Modelo") },
							singleLine = true,
						)
					}
					Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
						OutlinedTextField(
							value = placa,
							onValueChange = { placa = it.uppercase() },
							modifier = Modifier.weight(1f),
							label = { Text("Placa") },
							singleLine = true,
						)
						OutlinedTextField(
							value = ano,
							onValueChange = { ano = it.filter { ch -> ch.isDigit() }.take(4) },
							modifier = Modifier.weight(0.6f),
							label = { Text("Ano") },
							singleLine = true,
						)
						OutlinedTextField(
							value = kmAtual,
							onValueChange = { kmAtual = it.filter { ch -> ch.isDigit() } },
							modifier = Modifier.weight(0.8f),
							label = { Text("KM atual") },
							singleLine = true,
						)
					}
					Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
						OutlinedTextField(
							value = observacoes,
							onValueChange = { observacoes = it },
							modifier = Modifier.weight(1f),
							label = { Text("Observações") },
							minLines = 3,
						)
					}
				}

				Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
					listOf(
						MotoStatus.DISPONIVEL,
						MotoStatus.ALUGADA,
						MotoStatus.CONTRATADA,
						MotoStatus.MANUTENCAO,
						MotoStatus.BLOQUEADA,
						MotoStatus.PENDENTE_CONTRATO,
					).forEach { option ->
						FilterChip(
							selected = status == option,
							onClick = { status = option },
							label = { Text(option.name.replace('_', ' ').lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) },
						)
					}
				}

				Spacer(Modifier.weight(1f))

				Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
					Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
						OutlinedButton(onClick = onDismiss, enabled = !isSaving) {
							Text("Cancelar")
						}
						Button(
							onClick = {
								onSave(
									UpdateMotoRequest(
										modelo = modelo.trim().ifBlank { moto.modelo },
										placa = placa.trim().ifBlank { moto.placa },
										marca = marca.trim().ifBlank { null },
										ano = ano.toIntOrNull(),
										kmAtual = kmAtual.toIntOrNull(),
										status = status,
										observacoes = observacoes.trim().ifBlank { null },
									),
								)
							},
							enabled = !isSaving,
							colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20E65B), contentColor = Color.Black),
						) {
							Text(if (isSaving) "Salvando..." else "Salvar")
						}
					}
				}
			}
		}
	}
}

@Composable
private fun MotoEditDialogExpanded(
	moto: MotoResponse,
	isSaving: Boolean,
	errorMessage: String?,
	apiBaseUrl: String,
	apiAccessToken: String?,
	onDismiss: () -> Unit,
	onSave: (UpdateMotoRequest) -> Unit,
) {
	var modelo by remember(moto.id) { mutableStateOf(moto.modelo) }
	var placa by remember(moto.id) { mutableStateOf(moto.placa) }
	var marca by remember(moto.id) { mutableStateOf(moto.marca.orEmpty()) }
	var chassi by remember(moto.id) { mutableStateOf(moto.chassi.orEmpty()) }
	var renavam by remember(moto.id) { mutableStateOf(moto.renavam.orEmpty()) }
	var cor by remember(moto.id) { mutableStateOf(moto.cor.orEmpty()) }
	var ano by remember(moto.id) { mutableStateOf(moto.ano?.toString().orEmpty()) }
	var kmAtual by remember(moto.id) { mutableStateOf(moto.kmAtual.toString()) }
	var logicaVeiculoId by remember(moto.id) { mutableStateOf(moto.logicaVeiculoId.orEmpty()) }
	var status by remember(moto.id) { mutableStateOf(moto.status) }
	var observacoes by remember(moto.id) { mutableStateOf(moto.observacoes.orEmpty()) }
	var imageValue by remember(moto.id) { mutableStateOf(moto.fotoPrincipalUrl.orEmpty()) }
	var imageUpload by remember(moto.id) { mutableStateOf<com.example.billionemotosappkt.shared.api.UploadFileRequest?>(null) }

	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(usePlatformDefaultWidth = false),
	) {
		Surface(
			modifier = Modifier.fillMaxWidth(0.90f).fillMaxHeight(0.90f),
			shape = RoundedCornerShape(24.dp),
			color = Color(0xFF060907),
			border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(24.dp),
				verticalArrangement = Arrangement.spacedBy(18.dp),
			) {
				Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
					Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
						Text("Editar moto", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
						Text("${moto.placa} · ${moto.modelo}", color = Color.White.copy(alpha = 0.45f), fontSize = 13.sp)
					}
					Surface(
						shape = RoundedCornerShape(999.dp),
						color = Color(0xFF20E65B).copy(alpha = 0.12f),
						border = BorderStroke(1.dp, Color(0xFF20E65B).copy(alpha = 0.22f)),
					) {
						Text(
							text = "Atualização",
							color = Color(0xFF20E65B),
							modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
							fontSize = 12.sp,
							fontWeight = FontWeight.SemiBold,
						)
					}
				}

				if (errorMessage != null) {
					Card(
						colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(alpha = 0.08f)),
						border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.25f)),
					) {
						Text(errorMessage, color = Color.White, modifier = Modifier.padding(12.dp))
					}
				}

				Column(
					modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
					verticalArrangement = Arrangement.spacedBy(16.dp),
				) {
					Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
						Card(
							modifier = Modifier.weight(1f),
							shape = RoundedCornerShape(18.dp),
							colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
							border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
						) {
							Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
								Text("Imagem da moto", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
								MotoImagePickerField(
									imageValue = imageValue,
									onImageSelected = { path, upload ->
										imageValue = path
										imageUpload = upload
									},
									apiBaseUrl = apiBaseUrl,
									apiAccessToken = apiAccessToken,
									title = "Foto da moto",
									description = "Selecione a imagem principal da moto.",
									buttonLabel = "Selecionar foto",
								)
								Text(
									text = if (imageUpload != null) "Arquivo local pronto para envio" else "Nenhum arquivo selecionado",
									color = Color.White.copy(alpha = 0.55f),
									fontSize = 11.sp,
								)
							}
						}

						Card(
							modifier = Modifier.weight(1f),
							shape = RoundedCornerShape(18.dp),
							colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
							border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
						) {
							Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
								Text("Status e operação", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
								Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
									listOf(
										MotoStatus.DISPONIVEL,
										MotoStatus.ALUGADA,
										MotoStatus.CONTRATADA,
										MotoStatus.MANUTENCAO,
									).forEach { option ->
										FilterChip(
											selected = status == option,
											onClick = { status = option },
											label = {
												Text(option.name.replace('_', ' ').lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
											},
										)
									}
								}
								Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
									FilterChip(
										selected = status == MotoStatus.BLOQUEADA,
										onClick = { status = MotoStatus.BLOQUEADA },
										label = { Text("Bloqueada") },
									)
									FilterChip(
										selected = status == MotoStatus.PENDENTE_CONTRATO,
										onClick = { status = MotoStatus.PENDENTE_CONTRATO },
										label = { Text("Pendente contrato") },
									)
									FilterChip(
										selected = status == MotoStatus.AGUARDANDO_DEVOLUCAO,
										onClick = { status = MotoStatus.AGUARDANDO_DEVOLUCAO },
										label = { Text("Aguardando devolução") },
									)
								}
							}
						}
					}

					Card(
						shape = RoundedCornerShape(18.dp),
						colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
						border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
					) {
						Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
							Text("Dados principais", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
							Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
								OutlinedTextField(
									value = marca,
									onValueChange = { marca = it },
									modifier = Modifier.weight(1f),
									label = { Text("Marca") },
									singleLine = true,
								)
								OutlinedTextField(
									value = modelo,
									onValueChange = { modelo = it },
									modifier = Modifier.weight(1.2f),
									label = { Text("Modelo") },
									singleLine = true,
								)
								OutlinedTextField(
									value = placa,
									onValueChange = { placa = it.uppercase() },
									modifier = Modifier.weight(0.9f),
									label = { Text("Placa") },
									singleLine = true,
								)
							}
							Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
								OutlinedTextField(
									value = ano,
									onValueChange = { ano = it.filter { ch -> ch.isDigit() }.take(4) },
									modifier = Modifier.weight(0.5f),
									label = { Text("Ano") },
									singleLine = true,
								)
								OutlinedTextField(
									value = kmAtual,
									onValueChange = { kmAtual = it.filter { ch -> ch.isDigit() } },
									modifier = Modifier.weight(0.7f),
									label = { Text("KM atual") },
									singleLine = true,
								)
								OutlinedTextField(
									value = cor,
									onValueChange = { cor = it },
									modifier = Modifier.weight(0.9f),
									label = { Text("Cor") },
									singleLine = true,
								)
								OutlinedTextField(
									value = logicaVeiculoId,
									onValueChange = { logicaVeiculoId = it },
									modifier = Modifier.weight(1f),
									label = { Text("Lógica veículo ID") },
									singleLine = true,
								)
							}
							Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
								OutlinedTextField(
									value = chassi,
									onValueChange = { chassi = it },
									modifier = Modifier.weight(1f),
									label = { Text("Chassi") },
									singleLine = true,
								)
								OutlinedTextField(
									value = renavam,
									onValueChange = { renavam = it },
									modifier = Modifier.weight(1f),
									label = { Text("Renavam") },
									singleLine = true,
								)
							}
							OutlinedTextField(
								value = observacoes,
								onValueChange = { observacoes = it },
								modifier = Modifier.fillMaxWidth(),
								label = { Text("Observações") },
								minLines = 4,
							)
						}
					}
				}

				Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
					Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
						OutlinedButton(onClick = onDismiss, enabled = !isSaving) {
							Text("Cancelar")
						}
						Button(
							onClick = {
								onSave(
									UpdateMotoRequest(
										modelo = modelo.trim().ifBlank { moto.modelo },
										placa = placa.trim().ifBlank { moto.placa },
										marca = marca.trim().ifBlank { null },
										chassi = chassi.trim().ifBlank { null },
										renavam = renavam.trim().ifBlank { null },
										cor = cor.trim().ifBlank { null },
										ano = ano.toIntOrNull(),
										kmAtual = kmAtual.toIntOrNull(),
										status = status,
										logicaVeiculoId = logicaVeiculoId.trim().ifBlank { null },
										observacoes = observacoes.trim().ifBlank { null },
									),
								)
							},
							enabled = !isSaving,
							colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20E65B), contentColor = Color.Black),
						) {
							Text(if (isSaving) "Salvando..." else "Salvar")
						}
					}
				}
			}
		}
	}
}

private data class MotoDetailState(
	val moto: MotoResponse? = null,
	val contratos: List<ContratoResponse> = emptyList(),
	val manutencoes: List<ManutencaoResponse> = emptyList(),
	val loading: Boolean = false,
	val error: String? = null,
)

private fun maintenanceAccent(status: com.example.billionemotosappkt.shared.api.ManutencaoStatus): Color = when (status) {
	com.example.billionemotosappkt.shared.api.ManutencaoStatus.ABERTA -> Color(0xFFFFB300)
	com.example.billionemotosappkt.shared.api.ManutencaoStatus.EM_ANDAMENTO -> Color(0xFF7DD3FC)
	com.example.billionemotosappkt.shared.api.ManutencaoStatus.CONCLUIDA -> Color(0xFF20E65B)
	com.example.billionemotosappkt.shared.api.ManutencaoStatus.CANCELADA -> Color(0xFFFF4A4A)
}

private fun statusAccent(name: String): Color = when (name.lowercase()) {
	"ativo", "disponivel" -> Color(0xFF20E65B)
	"alugada", "em_andamento" -> Color(0xFF7DD3FC)
	"contratada" -> Color(0xFF8B5CF6)
	"manutencao" -> Color(0xFFFFB300)
	"bloqueada", "cancelada", "inadimplente" -> Color(0xFFFF4A4A)
	else -> Color.White.copy(alpha = 0.5f)
}

private fun statusAccent(status: MotoStatus): Color = when (status) {
	MotoStatus.DISPONIVEL -> Color(0xFF20E65B)
	MotoStatus.ALUGADA -> Color(0xFF7DD3FC)
	MotoStatus.CONTRATADA -> Color(0xFF8B5CF6)
	MotoStatus.MANUTENCAO -> Color(0xFFFFB300)
	MotoStatus.BLOQUEADA -> Color(0xFFFF4A4A)
	MotoStatus.PENDENTE_CONTRATO -> Color(0xFFFFB300)
	MotoStatus.AGUARDANDO_DEVOLUCAO -> Color(0xFFFFB300)
}

private fun statusLabel(status: MotoStatus): String = when (status) {
	MotoStatus.DISPONIVEL -> "Disponível"
	MotoStatus.ALUGADA -> "Alugada"
	MotoStatus.CONTRATADA -> "Contratada"
	MotoStatus.MANUTENCAO -> "Manutenção"
	MotoStatus.BLOQUEADA -> "Bloqueada"
	MotoStatus.PENDENTE_CONTRATO -> "Pendente contrato"
	MotoStatus.AGUARDANDO_DEVOLUCAO -> "Aguardando devolução"
}

private fun formatMoney(value: Double): String {
	return "R$ " + String.format(java.util.Locale("pt", "BR"), "%,.2f", value).replace(',', '#').replace('.', ',').replace('#', '.')
}

private fun daysBetween(value: String?): Int {
	if (value.isNullOrBlank()) return 0
	return runCatching {
		val instant = java.time.Instant.parse(if (value.length == 10) "${value}T00:00:00Z" else value)
		val now = java.time.Instant.now()
		kotlin.math.abs(java.time.temporal.ChronoUnit.DAYS.between(instant, now).toInt())
	}.getOrDefault(0)
}

private fun formatDate(value: String?): String {
	if (value.isNullOrBlank()) return "-"
	return runCatching {
		java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(java.time.ZoneId.systemDefault()).format(
			java.time.Instant.parse(if (value.length == 10) "${value}T00:00:00Z" else value)
		)
	}.getOrElse { value }
}
