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

@Composable
internal fun AnalysisDetailDialog(
	cliente: AnaliseListItem,
	history: List<DecisaoAprovacaoClienteResponse>,
	isLoadingHistory: Boolean,
	onDismiss: () -> Unit,
	onApprove: () -> Unit,
	onReject: () -> Unit,
	onLoadHistory: (String) -> Unit,
) {
	LaunchedEffect(cliente.id) {
		onLoadHistory(cliente.userId)
	}
	
	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(usePlatformDefaultWidth = false),
	) {
		Surface(
			modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.92f),
			shape = RoundedCornerShape(24.dp),
			color = Color(0xFF060907),
			border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
		) {
			Column(modifier = Modifier.fillMaxSize()) {
				Row(
					modifier = Modifier.fillMaxWidth().padding(24.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(16.dp),
				) {
					Column(modifier = Modifier.weight(1f)) {
						Text(
							cliente.nome,
							color = Color.White,
							fontSize = 28.sp,
							fontWeight = FontWeight.Bold
						)
						Spacer(Modifier.height(6.dp))
						Row(verticalAlignment = Alignment.CenterVertically) {
							BadgePill(
								text = cliente.status.label(),
								accent = cliente.status.accent()
							)
							Spacer(Modifier.width(10.dp))
							Text(
								"ID ${cliente.id.take(8)}",
								color = Color.White.copy(alpha = 0.34f),
								fontSize = 12.sp
							)
						}
					}
					TextButton(onClick = onDismiss) {
						Text("Fechar", color = Color.White.copy(alpha = 0.70f))
					}
				}
				
				HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
				
				Row(
					modifier = Modifier.fillMaxSize().padding(24.dp),
					horizontalArrangement = Arrangement.spacedBy(20.dp),
				) {
					Column(
						modifier = Modifier.weight(1.05f),
						verticalArrangement = Arrangement.spacedBy(16.dp)
					) {
						DetailCard(
							title = "Dados principais",
							rows = listOf(
								"CPF" to cliente.cpf.ifBlank { "-" },
								"Telefone" to cliente.telefone.ifBlank { "-" },
								"Email" to cliente.email.ifBlank { "-" },
								"Cidade" to cliente.cityLabel,
								"Plano" to cliente.planoNome.ifBlank { "-" },
								"Moto" to cliente.motoNome.ifBlank { "-" },
							),
						)
						DetailCard(
							title = "Documentos",
							rows = listOf(
								"CNH" to listOf(
									cliente.cnh,
									cliente.cnhCategoria
								).filter { it.isNotBlank() }.joinToString(" / ").ifBlank { "-" },
								"Endereco" to cliente.endereco.ifBlank { "-" },
								"Endereco parente" to cliente.enderecoParente.ifBlank { "-" },
								"CEP" to cliente.cep.ifBlank { "-" },
							),
						)
						DetailCard(
							title = "Observacoes",
							rows = emptyList(),
							body = {
								Text(
									text = cliente.observacoes.ifBlank { "Sem observacoes registradas." },
									color = Color.White.copy(alpha = 0.72f),
									fontSize = 13.sp,
									lineHeight = 18.sp,
								)
							},
						)
					}
					
					Column(
						modifier = Modifier.weight(0.95f),
						verticalArrangement = Arrangement.spacedBy(16.dp)
					) {
						DetailCard(
							title = "Historico",
							rows = emptyList(),
							body = {
								if (isLoadingHistory) {
									Box(
										Modifier.fillMaxWidth().height(160.dp),
										contentAlignment = Alignment.Center
									) {
										CircularProgressIndicator(color = Color(0xFF20E65B))
									}
								} else if (history.isEmpty()) {
									Text(
										"Nenhum historico encontrado.",
										color = Color.White.copy(alpha = 0.42f),
										fontSize = 13.sp
									)
								} else {
									Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
										history.take(6).forEach { item ->
											HistoryRow(item)
										}
									}
								}
							},
						)
						
						DetailCard(
							title = "Acoes rapidas",
							rows = emptyList(),
							body = {
								Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
									Button(
										onClick = onApprove,
										shape = RoundedCornerShape(14.dp),
										colors = ButtonDefaults.buttonColors(
											containerColor = Color(0xFF20E65B),
											contentColor = Color.Black,
										),
									) {
										Text("Aceitar", fontWeight = FontWeight.SemiBold)
									}
									OutlinedButton(
										onClick = onReject,
										shape = RoundedCornerShape(14.dp),
										border = BorderStroke(
											1.dp,
											Color(0xFFFF4A4A).copy(alpha = 0.28f)
										),
										colors = ButtonDefaults.outlinedButtonColors(
											contentColor = Color(
												0xFFFF4A4A
											)
										),
									) {
										Text("Negar", fontWeight = FontWeight.SemiBold)
									}
								}
							},
						)
					}
				}
			}
		}
	}
}

@Composable
internal fun AnalysisDetailRichDialog(
	api: BillioneMotosApi,
	cliente: AnaliseListItem,
	modelosDisponiveis: List<MotoModeloResponse>,
	motosDisponiveis: List<MotoResponse>,
	history: List<DecisaoAprovacaoClienteResponse>,
	isLoadingHistory: Boolean,
	onDismiss: () -> Unit,
	onDecision: (ClienteAprovacaoStatus, String, String?) -> Unit,
	onLoadHistory: (String) -> Unit,
) {
	LaunchedEffect(cliente.id) {
		onLoadHistory(cliente.userId)
	}
	
	var selectedStatus by remember(cliente.id) {
		mutableStateOf(
			when (cliente.status) {
				ClienteAnalisePedidoStatus.PENDENTE -> ClienteAprovacaoStatus.PENDENTE
				ClienteAnalisePedidoStatus.EM_ANALISE -> ClienteAprovacaoStatus.EM_ANALISE
				ClienteAnalisePedidoStatus.APROVADO -> ClienteAprovacaoStatus.APROVADO
				ClienteAnalisePedidoStatus.REPROVADO -> ClienteAprovacaoStatus.REPROVADO
				ClienteAnalisePedidoStatus.CANCELADO -> ClienteAprovacaoStatus.PENDENTE
			},
		)
	}
	var justificativa by remember(cliente.id) { mutableStateOf(defaultJustificativa(selectedStatus)) }
	var motoId by remember(cliente.id) { mutableStateOf(cliente.motoId) }
	var activeTab by remember(cliente.id) { mutableStateOf(ClientDetailTab.RESUMO) }
	
	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(usePlatformDefaultWidth = false)
	) {
		Surface(
			modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.92f),
			shape = RoundedCornerShape(24.dp),
			color = Color(0xFF060907),
			border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
		) {
			BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
				val narrow = maxWidth < 1240.dp
				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(24.dp)
						.verticalScroll(rememberScrollState()),
					verticalArrangement = Arrangement.spacedBy(18.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
				) {
					Column(
						modifier = Modifier.fillMaxWidth().widthIn(max = 1600.dp),
						verticalArrangement = Arrangement.spacedBy(18.dp),
					) {
						Row(
							modifier = Modifier.fillMaxWidth(),
							verticalAlignment = Alignment.Top,
							horizontalArrangement = Arrangement.spacedBy(16.dp),
						) {
							Column(
								modifier = Modifier.weight(1f),
								verticalArrangement = Arrangement.spacedBy(6.dp)
							) {
								Row(
									verticalAlignment = Alignment.CenterVertically,
									horizontalArrangement = Arrangement.spacedBy(12.dp)
								) {
									Box(
										modifier = Modifier
											.size(46.dp)
											.background(
												cliente.status.accent().copy(alpha = 0.18f),
												CircleShape
											),
										contentAlignment = Alignment.Center,
									) {
										Icon(
											Icons.Default.Groups,
											null,
											tint = cliente.status.accent(),
											modifier = Modifier.size(22.dp)
										)
									}
									Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
										Text(
											text = cliente.nome.ifBlank { "Pedido de cliente" },
											color = Color.White,
											fontSize = 28.sp,
											fontWeight = FontWeight.Bold,
											maxLines = 1,
											overflow = TextOverflow.Ellipsis,
										)
										Row(
											verticalAlignment = Alignment.CenterVertically,
											horizontalArrangement = Arrangement.spacedBy(10.dp)
										) {
											BadgePill(
												text = cliente.status.label(),
												accent = cliente.status.accent()
											)
											Text(
												"ID ${cliente.id.take(8)}",
												color = Color.White.copy(alpha = 0.34f),
												fontSize = 12.sp
											)
											Text(
												"Criado ${formatDate(cliente.createdAt)}",
												color = Color.White.copy(alpha = 0.34f),
												fontSize = 12.sp
											)
										}
									}
								}
							}
							
							androidx.compose.material3.IconButton(
								onClick = onDismiss,
								modifier = Modifier.background(Color.White.copy(alpha = 0.04f), CircleShape)
							) {
								Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.White.copy(alpha = 0.7f))
							}
						}
						
						Row(
							horizontalArrangement = Arrangement.spacedBy(12.dp),
							modifier = Modifier.fillMaxWidth()
						) {
							listOf(
								"CPF" to (cliente.cpf.ifBlank { "-" }),
								"Telefone" to (cliente.telefone.ifBlank { "-" }),
								"Plano" to (cliente.planoNome.ifBlank { "-" }),
								"Cidade" to cliente.cityLabel,
							).forEach { (label, value) ->
								MiniMetricCard(
									title = label,
									value = value,
									modifier = Modifier.weight(1f)
								)
							}
						}
						
						TabStrip(
							tabs = listOf(
								ClientDetailTab.RESUMO,
								ClientDetailTab.DOCUMENTOS,
								ClientDetailTab.ACOES,
								ClientDetailTab.HISTORICO,
							),
							activeTab = activeTab,
							onChange = { activeTab = it },
						)
						
						when (activeTab) {
							ClientDetailTab.RESUMO -> {
								RichClientMainPanel(
									api = api,
									cliente = cliente,
									modelosDisponiveis = modelosDisponiveis,
								)
							}
							
							ClientDetailTab.DOCUMENTOS -> {
								DocumentsOnlyPanel(
									api = api,
									cliente = cliente,
									modelosDisponiveis = modelosDisponiveis,
								)
							}
							
	ClientDetailTab.ACOES -> {
		RichClientSidePanel(
			api = api,
			cliente = cliente,
			modelosDisponiveis = modelosDisponiveis,
			motosDisponiveis = motosDisponiveis,
			selectedStatus = selectedStatus,
			justificativa = justificativa,
			motoId = motoId,
			onStatusChange = { selectedStatus = it },
									onJustificativaChange = { justificativa = it },
									onMotoIdChange = { motoId = it },
									onSave = {
										onDecision(
											selectedStatus,
											justificativa.ifBlank {
												defaultJustificativa(
													selectedStatus
												)
											},
											motoId.trim().ifBlank { null },
										)
									},
								)
							}
							
							ClientDetailTab.HISTORICO -> {
								DetailCard(
									title = "Histórico",
									rows = emptyList(),
									body = {
										if (isLoadingHistory) {
											Box(
												Modifier.fillMaxWidth().height(180.dp),
												contentAlignment = Alignment.Center
											) {
												CircularProgressIndicator(color = Color(0xFF20E65B))
											}
										} else if (history.isEmpty()) {
											Text(
												"Nenhum historico encontrado.",
												color = Color.White.copy(alpha = 0.42f),
												fontSize = 13.sp
											)
										} else {
											Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
												history.take(6).forEach { item ->
													HistoryRow(item)
												}
											}
										}
									},
								)
							}
						}
					}
				}
			}
		}
	}
}

@Composable
internal fun DecisionDialog(
	state: DecisionDialogState,
	onDismiss: () -> Unit,
	onStateChange: (DecisionDialogState) -> Unit,
	onConfirm: (DecisionDialogState) -> Unit,
) {
	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(usePlatformDefaultWidth = false),
	) {
		Surface(
			modifier = Modifier.fillMaxWidth(0.64f).fillMaxHeight(0.72f),
			shape = RoundedCornerShape(22.dp),
			color = Color(0xFF060907),
			border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
		) {
			Column(
				modifier = Modifier.fillMaxSize().padding(24.dp),
				verticalArrangement = Arrangement.spacedBy(18.dp)
			) {
				Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
					Text(
						text = if (state.decisao == ClienteAprovacaoStatus.REPROVADO) "Negar pedido" else "Aceitar pedido",
						color = Color.White,
						fontSize = 24.sp,
						fontWeight = FontWeight.Bold,
					)
					Text(
						state.cliente.nome,
						color = Color.White.copy(alpha = 0.45f),
						fontSize = 13.sp
					)
				}
				
				DetailCard(
					title = "Resumo",
					rows = listOf(
						"CPF" to state.cliente.cpf,
						"Cidade" to state.cliente.cityLabel,
						"Status atual" to state.cliente.status.label(),
					),
				)
				
				OutlinedTextField(
					value = state.justificativa,
					onValueChange = { onStateChange(state.copy(justificativa = it)) },
					modifier = Modifier.fillMaxWidth(),
					minLines = 4,
					label = { Text("Justificativa") },
					placeholder = { Text("Explique a decisão") },
					shape = RoundedCornerShape(14.dp),
					colors = OutlinedTextFieldDefaults.colors(
						focusedTextColor = Color.White,
						unfocusedTextColor = Color.White,
						focusedContainerColor = Color.White.copy(alpha = 0.02f),
						unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
						focusedBorderColor = Color.White.copy(alpha = 0.12f),
						unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
						cursorColor = Color(0xFF20E65B),
					),
				)
				
				if (state.decisao != ClienteAprovacaoStatus.REPROVADO) {
					OutlinedTextField(
						value = state.motoId,
						onValueChange = { onStateChange(state.copy(motoId = it)) },
						modifier = Modifier.fillMaxWidth(),
						label = { Text("Moto vinculada") },
						placeholder = { Text("Opcional, mas recomendado") },
						shape = RoundedCornerShape(14.dp),
						colors = OutlinedTextFieldDefaults.colors(
							focusedTextColor = Color.White,
							unfocusedTextColor = Color.White,
							focusedContainerColor = Color.White.copy(alpha = 0.02f),
							unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
							focusedBorderColor = Color.White.copy(alpha = 0.12f),
							unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
							cursorColor = Color(0xFF20E65B),
						),
					)
				}
				
				Spacer(Modifier.weight(1f))
				
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					TextButton(onClick = onDismiss) {
						Text("Cancelar", color = Color.White.copy(alpha = 0.65f))
					}
					Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
						Button(
							onClick = { onConfirm(state) },
							shape = RoundedCornerShape(14.dp),
							colors = ButtonDefaults.buttonColors(
								containerColor = if (state.decisao == ClienteAprovacaoStatus.REPROVADO) Color(
									0xFFFF4A4A
								) else Color(0xFF20E65B),
								contentColor = Color.Black,
							),
						) {
							Text(
								text = if (state.decisao == ClienteAprovacaoStatus.REPROVADO) "Confirmar recusa" else "Confirmar aprovação",
								fontWeight = FontWeight.SemiBold,
							)
						}
					}
				}
			}
		}
	}
}

internal data class DecisionDialogState(
	val cliente: AnaliseListItem,
	val decisao: ClienteAprovacaoStatus,
	val justificativa: String,
	val motoId: String,
)

