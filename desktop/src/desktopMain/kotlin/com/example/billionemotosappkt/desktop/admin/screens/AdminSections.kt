package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.billionemotosappkt.desktop.admin.components.AdminSectionCard
import com.example.billionemotosappkt.desktop.admin.components.motos.MotoModeloPanelDialog
import com.example.billionemotosappkt.desktop.admin.components.motos.MotoModelosGrid
import com.example.billionemotosappkt.desktop.admin.components.motos.MotoModelosMetaPill
import com.example.billionemotosappkt.desktop.admin.data.toCreateRequest
import com.example.billionemotosappkt.desktop.admin.data.toResponseLike
import com.example.billionemotosappkt.desktop.admin.data.toUpdateRequest
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.MotoModeloResponse
import com.example.billionemotosappkt.shared.api.MotoStatus
import kotlinx.coroutines.launch

@Composable
fun MotoModelosSection(api: BillioneMotosApi, apiBaseUrl: String, apiAccessToken: String?) {
	val scope = rememberCoroutineScope()
	var isLoading by remember { mutableStateOf(true) }
	var errorMessage by remember { mutableStateOf<String?>(null) }
	var query by remember { mutableStateOf("") }
	var models by remember { mutableStateOf<List<MotoModeloResponse>>(emptyList()) }
	var selectedModel by remember { mutableStateOf<MotoModeloResponse?>(null) }
	var creatingModel by remember { mutableStateOf(false) }
	var editingModel by remember { mutableStateOf<MotoModeloResponse?>(null) }
	var deletingModel by remember { mutableStateOf<MotoModeloResponse?>(null) }
	var actionMessage by remember { mutableStateOf<String?>(null) }
	
	LaunchedEffect(api) {
		isLoading = true
		runCatching { api.motos.modelos() }
			.onSuccess {
				models = it
				errorMessage = null
			}
			.onFailure {
				errorMessage = it.message ?: "Erro ao carregar modelos"
			}
		isLoading = false
	}
	
	val filteredModels = remember(models, query) {
		val normalized = query.trim()
		if (normalized.isBlank()) {
			models
		} else {
			models.filter { model ->
				listOfNotNull(
					model.marca,
					model.nome,
					model.modelo,
					model.categoria,
					model.tipo,
					model.combustivel,
					model.codigoFipe,
					model.descricao,
				).any { it.contains(normalized, ignoreCase = true) }
			}
		}
	}
	
	val totalBrands = remember(models) {
		models.mapNotNull { it.marca?.takeIf(String::isNotBlank) }.distinct().size
	}
	
	val trackedModels = remember(models) {
		models.count { it.possuiRastreador }
	}
	
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.Bottom,
		) {
			Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
				Text(
					text = "Modelos",
					color = Color.White,
					fontSize = 30.sp,
					fontWeight = FontWeight.Black,
				)
				Text(
					text = "Leitura, edição e exclusão de modelos cadastrados.",
					color = Color.White.copy(alpha = 0.58f),
					fontSize = 14.sp,
				)
			}
			
			Row(
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				OutlinedTextField(
					value = query,
					onValueChange = { query = it },
					modifier = Modifier.width(320.dp).height(40.dp),
					placeholder = { Text("Buscar marca, nome ou categoria...") },
					leadingIcon = {
						Icon(
							Icons.Default.Search,
							null,
							modifier = Modifier.size(20.dp)
						)
					},
					shape = RoundedCornerShape(16.dp),
					singleLine = true,
					colors = OutlinedTextFieldDefaults.colors(
						focusedBorderColor = Color(0xFF20E65B),
						unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
						focusedContainerColor = Color(0xFF111614),
						unfocusedContainerColor = Color(0xFF0D1210),
					),
				)
				Button(
					onClick = { creatingModel = true },
					modifier = Modifier.height(40.dp)
						.widthIn(min = 150.dp), // ALTURA IGUAL E LARGURA MÍNIMA
					shape = RoundedCornerShape(12.dp),
					colors = ButtonDefaults.buttonColors(
						containerColor = Color(0xFF20E65B),
						contentColor = Color.Black,
					),
					contentPadding = PaddingValues(horizontal = 16.dp) // Espaçamento interno fixo
				) {
					Icon(
						Icons.Default.Add,
						contentDescription = null,
						modifier = Modifier.size(18.dp)
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						"Novo modelo",
						fontWeight = FontWeight.Bold,
						fontSize = 13.sp,
						maxLines = 1, // Impede a quebra de linha
						softWrap = false
					)
				}
			}
		}
		
		TwoUpCards(
			first = { modifier ->
				AdminSectionCard(
					title = "Modelos carregados",
					subtitle = "Total disponível no catálogo",
					icon = Icons.Default.DirectionsBike,
					accent = Color(0xFF20E65B),
					value = models.size.toString(),
					modifier = modifier,
				)
			},
			second = { modifier ->
				AdminSectionCard(
					title = "Com rastreador",
					subtitle = "Modelos com suporte ativo",
					icon = Icons.Default.LocationOn,
					accent = Color(0xFF7DD3FC),
					value = trackedModels.toString(),
					modifier = modifier,
				)
			},
		)
		
		Row(
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			modifier = Modifier.fillMaxWidth()
		) {
			MotoModelosMetaPill("Marcas", totalBrands.toString(), Color(0xFF20E65B))
			MotoModelosMetaPill("Exibidos", filteredModels.size.toString(), Color(0xFF7DD3FC))
			MotoModelosMetaPill("Catálogo", models.size.toString(), Color(0xFFFFB300))
		}
		
		actionMessage?.let { message ->
			Card(
				colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1711)),
				border = androidx.compose.foundation.BorderStroke(
					1.dp,
					Color.White.copy(alpha = 0.08f)
				),
			) {
				Text(
					text = message,
					color = Color.White.copy(alpha = 0.78f),
					modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
				)
			}
		}
		
		if (isLoading) {
			Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
				CircularProgressIndicator(color = Color(0xFF20E65B))
			}
		} else if (errorMessage != null) {
			Card(
				colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(alpha = 0.1f)),
				border = androidx.compose.foundation.BorderStroke(
					1.dp,
					Color(0xFFFF4A4A).copy(alpha = 0.2f)
				),
			) {
				Text(
					text = errorMessage!!,
					color = Color(0xFFFF4A4A),
					modifier = Modifier.padding(16.dp),
				)
			}
		} else if (filteredModels.isEmpty()) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(220.dp),
				contentAlignment = Alignment.Center,
			) {
				Text("Nenhum modelo encontrado.", color = Color.White.copy(alpha = 0.45f))
			}
		} else {
			MotoModelosGrid(
				models = filteredModels,
				onView = { selectedModel = it },
				onEdit = { editingModel = it },
				onDelete = { deletingModel = it },
				apiBaseUrl = apiBaseUrl,
				apiAccessToken = apiAccessToken,
			)
		}
	}
	
	selectedModel?.let { model ->
		MotoModeloPanelDialog(
			title = "Detalhes do modelo",
			model = model,
			onDismiss = { selectedModel = null },
			onSubmit = null,
			submitLabel = "Fechar",
			apiBaseUrl = apiBaseUrl,
			apiAccessToken = apiAccessToken,
		)
	}
	
	if (creatingModel) {
		MotoModeloPanelDialog(
			title = "Novo modelo",
			model = null,
			onDismiss = { creatingModel = false },
			onSubmit = { form ->
				scope.launch {
					runCatching {
						api.motos.createModelo(
							request = form.toCreateRequest(),
							imagemReferenciaImage = form.imagemReferenciaUpload,
						)
					}
						.onSuccess { created ->
							models = listOf(created) + models
							actionMessage = "Modelo criado com sucesso."
							creatingModel = false
						}
						.onFailure {
							actionMessage = "Não foi possível criar o modelo no backend."
						}
				}
			},
			submitLabel = "Criar",
			apiBaseUrl = apiBaseUrl,
			apiAccessToken = apiAccessToken,
		)
	}
	
	editingModel?.let { model ->
		MotoModeloPanelDialog(
			title = "Editar modelo",
			model = model,
			onDismiss = { editingModel = null },
			onSubmit = { form ->
				val targetId = model.id
				if (targetId.isNullOrBlank()) {
					models =
						models.map { current -> if (current == model) form.toResponseLike(model) else current }
					actionMessage = "Modelo atualizado localmente."
					editingModel = null
				} else {
					val request = form.toUpdateRequest()
					scope.launch {
						runCatching {
							api.motos.updateModelo(
								id = targetId,
								request = request,
								imagemReferenciaImage = form.imagemReferenciaUpload,
							).let {
								println(it)
								it
							}
						}
							.onSuccess { response ->
								models =
									models.map { current -> if (current.id == targetId) response else current }
								actionMessage = "Modelo salvo com sucesso."
								editingModel = null
							}
							.onFailure {
								models = models.map { current ->
									if (current.id == targetId) form.toResponseLike(model)
										.copy(id = targetId) else current
								}
								actionMessage =
									"Não foi possível salvar no backend; alteração aplicada localmente."
								editingModel = null
							}
					}
				}
			},
			submitLabel = "Salvar",
			apiBaseUrl = apiBaseUrl,
			apiAccessToken = apiAccessToken,
		)
	}
	
	deletingModel?.let { model ->
		Dialog(
			onDismissRequest = { deletingModel = null },
			properties = DialogProperties(usePlatformDefaultWidth = false),
		) {
			Surface(
				modifier = Modifier
					.fillMaxWidth(0.55f)
					.heightIn(min = 260.dp)
					.padding(24.dp),
				shape = RoundedCornerShape(28.dp),
				color = Color(0xFF0B100D),
				border = androidx.compose.foundation.BorderStroke(
					1.dp,
					Color.White.copy(alpha = 0.08f)
				),
			) {
				Column(
					modifier = Modifier.padding(24.dp),
					verticalArrangement = Arrangement.spacedBy(16.dp)
				) {
					Text(
						"Desativar modelo?",
						color = Color.White,
						fontWeight = FontWeight.Black,
						fontSize = 22.sp
					)
					Text(
						text = "Essa ação desativa ${model.marca.orEmpty()} ${model.nome.orEmpty()} da lista de modelos exibidos.",
						color = Color.White.copy(alpha = 0.72f),
					)
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.End,
					) {
						Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
							TextButton(onClick = { deletingModel = null }) { Text("Cancelar") }
							Button(
								onClick = {
									val targetId = model.id
									if (targetId.isNullOrBlank()) {
										models = models.filterNot { it == model }
										actionMessage = "Modelo desativado localmente."
										deletingModel = null
									} else {
										scope.launch {
											runCatching { api.motos.deleteModelo(targetId) }
												.onSuccess {
													models = models.filterNot { it.id == targetId }
													actionMessage = "Modelo desativado com sucesso."
													deletingModel = null
												}
												.onFailure {
													models = models.filterNot { it.id == targetId }
													actionMessage =
														"Não foi possível desativar no backend; remoção aplicada localmente."
													deletingModel = null
												}
										}
									}
								},
								colors = ButtonDefaults.buttonColors(
									containerColor = Color(
										0xFFFF4A4A
									), contentColor = Color.White
								),
							) {
								Text("Desativar")
							}
						}
					}
				}
			}
		}
	}
}

@Composable
private fun TwoUpCards(
	first: @Composable (Modifier) -> Unit,
	second: @Composable (Modifier) -> Unit,
) {
	BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
		val gap = 16.dp
		val cardWidth = (maxWidth - gap) / 2
		if (cardWidth >= 280.dp) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(gap),
				modifier = Modifier.fillMaxWidth()
			) {
				first(Modifier.width(cardWidth))
				second(Modifier.width(cardWidth))
			}
		} else {
			Column(verticalArrangement = Arrangement.spacedBy(gap)) {
				first(Modifier.fillMaxWidth())
				second(Modifier.fillMaxWidth())
			}
		}
	}
}


fun fallbackMotoPainter(): BitmapPainter {
	val fallback = Thread.currentThread().contextClassLoader.getResourceAsStream("moto_premium.png")
	return fallback?.use { BitmapPainter(loadImageBitmap(it)) }
		?: error("Fallback image resource `moto_premium.png` not found.")
}

fun getMotoModelCardImage(marca: String?, modelo: String?): String {
	val combined = listOfNotNull(marca, modelo).joinToString(" ")
	return when {
		combined.contains("Avelloz", ignoreCase = true) -> "avelloz_160_black_new.png"
		combined.contains("Ninja", ignoreCase = true) -> "moto_sport_updated.png"
		combined.contains("Eletrica", ignoreCase = true) -> "moto_eletrica_new.png"
		combined.contains("CG", ignoreCase = true) -> "moto_premium.png"
		else -> "moto_premium.png"
	}
}