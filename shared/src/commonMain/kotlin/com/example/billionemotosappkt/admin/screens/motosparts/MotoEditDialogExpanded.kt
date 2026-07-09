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
internal fun MotoEditDialogExpanded(
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
