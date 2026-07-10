package com.example.billionemotosappkt.screens.dashboard.customer.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.billionemotosappkt.shared.utils.toImageBitmap
import com.example.billionemotosappkt.shared.utils.rememberImagePicker
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.billionemotosappkt.screens.dashboard.customer.TicketsViewModel
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.TicketArquivoResponse
import com.example.billionemotosappkt.shared.api.TicketCategoria
import com.example.billionemotosappkt.shared.api.TicketCommentResponse
import com.example.billionemotosappkt.shared.api.TicketPrioridade
import com.example.billionemotosappkt.shared.api.TicketResponse
import com.example.billionemotosappkt.shared.api.TicketStatus
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private enum class TicketsScreen { LIST, CREATE, DETAIL, EDIT }

private val createCategorias = listOf(
	TicketCategoria.MANUTENCAO,
	TicketCategoria.SOCORRO,
	TicketCategoria.AJUDA,
	TicketCategoria.DUVIDA,
	TicketCategoria.OUTROS,
)

@Composable
fun CustomerTicketsTab(
	api: BillioneMotosApi,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val viewModel = remember(api) { TicketsViewModel(api) }
	val state = viewModel.uiState.collectAsStateWithLifecycle().value

	var screen by rememberSaveable { androidx.compose.runtime.mutableStateOf(TicketsScreen.LIST) }
	var selectedId by rememberSaveable { androidx.compose.runtime.mutableStateOf<String?>(null) }

	val selected = state.selectedTicket

	when (screen) {
		TicketsScreen.LIST -> TicketsListView(
			isLoading = state.isLoadingList,
			error = state.listError,
			tickets = state.tickets,
			onRefresh = viewModel::loadList,
			onNewTicket = {
				viewModel.clearCreateState()
				screen = TicketsScreen.CREATE
			},
			onOpen = { ticket ->
				selectedId = ticket.id
				viewModel.openDetail(ticket.id)
				screen = TicketsScreen.DETAIL
			},
			onBack = onBack,
			modifier = modifier,
		)

		TicketsScreen.CREATE -> TicketCreateView(
			api = api,
			isCreating = state.isCreating,
			error = state.createError,
			onSubmit = { titulo, descricao, categoria, prioridade, photos ->
				viewModel.createTicket(
					titulo = titulo,
					descricao = descricao,
					categoria = categoria,
					prioridade = prioridade,
					photos = photos,
					onCreated = { id ->
						selectedId = id
						screen = TicketsScreen.DETAIL
					},
				)
			},
			onBack = { screen = TicketsScreen.LIST },
			modifier = modifier,
		)

		TicketsScreen.DETAIL -> TicketDetailView(
			api = api,
			isLoading = state.isLoadingDetail,
			error = state.detailError,
			actionError = state.actionError,
			ticket = selected.takeIf { it?.id == selectedId },
			isAddingComment = state.isAddingComment,
			isUploadingPhoto = state.isUploadingPhoto,
			canEdit = viewModel.canEdit(selected),
			onEdit = { screen = TicketsScreen.EDIT },
			onAddComment = { mensagem, photo ->
				selectedId?.let { viewModel.addComment(it, mensagem, photo) }
			},
			onAddPhoto = { file ->
				selectedId?.let { viewModel.addPhoto(it, file) }
			},
			onBack = {
				viewModel.clearDetail()
				screen = TicketsScreen.LIST
			},
			modifier = modifier,
		)

		TicketsScreen.EDIT -> TicketEditView(
			isSaving = state.isSaving,
			error = state.actionError,
			ticket = selected.takeIf { it?.id == selectedId },
			onSave = { titulo, descricao ->
				selectedId?.let { viewModel.updateTicket(it, titulo, descricao) }
				screen = TicketsScreen.DETAIL
			},
			onBack = { screen = TicketsScreen.DETAIL },
			modifier = modifier,
		)
	}
}

// ---------------------------------------------------------------------------
// LIST
// ---------------------------------------------------------------------------

@Composable
private fun TicketsListView(
	isLoading: Boolean,
	error: String?,
	tickets: List<TicketResponse>,
	onRefresh: () -> Unit,
	onNewTicket: () -> Unit,
	onOpen: (TicketResponse) -> Unit,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
		CustomerAppCard(
			title = "Meus chamados",
			description = "Acompanhe seus pedidos de manutencao, socorro e ajuda.",
			content = {
				Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
					Button(
						onClick = onNewTicket,
						colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
						modifier = Modifier.fillMaxWidth(),
					) {
						Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null)
						Spacer(modifier = Modifier.size(8.dp))
						Text(text = "Novo chamado", fontWeight = FontWeight.Black)
					}

					when {
						isLoading -> LoadingRow()
						error != null -> ErrorRow(message = error, onRetry = onRefresh)
						tickets.isEmpty() -> Text(
							text = "Voce ainda nao abriu nenhum chamado.",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
						)
						else -> tickets.forEach { ticket ->
							TicketRow(ticket = ticket, onClick = { onOpen(ticket) })
						}
					}
				}
			},
		)
	}
}

@Composable
private fun TicketRow(ticket: TicketResponse, onClick: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f))
			.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
			.padding(14.dp),
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(8.dp)),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = "#${ticket.numero} ${ticket.titulo}",
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Black,
				color = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.weight(1f),
			)
			StatusChip(status = ticket.status)
		}
		Text(
			text = "${categoriaLabel(ticket.categoria)} • ${prioridadeLabel(ticket.prioridade)}",
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		ticket.createdAt?.let {
			Text(
				text = "Aberto em ${formatDate(it)}",
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
		TextButton(onClick = onClick, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
			Text(text = "Abrir detalhes ›", color = MaterialTheme.colorScheme.primary)
		}
	}
}

// ---------------------------------------------------------------------------
// CREATE
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketCreateView(
	api: BillioneMotosApi,
	isCreating: Boolean,
	error: String?,
	onSubmit: (String, String?, TicketCategoria, TicketPrioridade?, List<UploadFileRequest>) -> Unit,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var titulo by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
	var descricao by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
	var categoria by rememberSaveable { androidx.compose.runtime.mutableStateOf(TicketCategoria.MANUTENCAO) }
	var prioridade by rememberSaveable { androidx.compose.runtime.mutableStateOf<TicketPrioridade?>(null) }
	val photos = remember { mutableStateListOf<UploadFileRequest>() }

	var categoriaExpanded by rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }
	var prioridadeExpanded by rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }

	val photoPicker = rememberImagePicker { file ->
		if (file != null) {
			photos.add(file)
		}
	}

	Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
		CustomerAppCard(
			title = "Novo chamado",
			description = "Descreva o que voce precisa e anexe fotos se quiser.",
			content = {
				Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
					ExposedDropdownMenuBox(
						expanded = categoriaExpanded,
						onExpandedChange = { categoriaExpanded = !categoriaExpanded },
					) {
						OutlinedTextField(
							value = categoriaLabel(categoria),
							onValueChange = {},
							readOnly = true,
							label = { Text("Categoria") },
							trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
							modifier = Modifier.fillMaxWidth(),
							colors = OutlinedTextFieldDefaults.colors(),
						)
						ExposedDropdownMenu(
							expanded = categoriaExpanded,
							onDismissRequest = { categoriaExpanded = false },
						) {
							createCategorias.forEach { option ->
								DropdownMenuItem(
									text = { Text(categoriaLabel(option)) },
									onClick = {
										categoria = option
										categoriaExpanded = false
									},
								)
							}
						}
					}

					OutlinedTextField(
						value = titulo,
						onValueChange = { titulo = it },
						label = { Text("Titulo") },
						modifier = Modifier.fillMaxWidth(),
					)
					OutlinedTextField(
						value = descricao,
						onValueChange = { descricao = it },
						label = { Text("Descricao") },
						modifier = Modifier.fillMaxWidth(),
						minLines = 3,
					)

					ExposedDropdownMenuBox(
						expanded = prioridadeExpanded,
						onExpandedChange = { prioridadeExpanded = !prioridadeExpanded },
					) {
						OutlinedTextField(
							value = prioridade?.let { prioridadeLabel(it) } ?: "Sem prioridade",
							onValueChange = {},
							readOnly = true,
							label = { Text("Prioridade (opcional)") },
							trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prioridadeExpanded) },
							modifier = Modifier.fillMaxWidth(),
							colors = OutlinedTextFieldDefaults.colors(),
						)
						ExposedDropdownMenu(
							expanded = prioridadeExpanded,
							onDismissRequest = { prioridadeExpanded = false },
						) {
							DropdownMenuItem(
								text = { Text("Sem prioridade") },
								onClick = {
									prioridade = null
									prioridadeExpanded = false
								},
							)
							TicketPrioridade.values().forEach { option ->
								DropdownMenuItem(
									text = { Text(prioridadeLabel(option)) },
									onClick = {
										prioridade = option
										prioridadeExpanded = false
									},
								)
							}
						}
					}

					TextButton(onClick = { photoPicker() }) {
						Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null)
						Spacer(modifier = Modifier.size(8.dp))
						Text(text = if (photos.isEmpty()) "Anexar foto" else "Anexar outra foto")
					}

					if (photos.isNotEmpty()) {
						Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
							photos.forEachIndexed { index, file ->
								Row(
									modifier = Modifier.fillMaxWidth(),
									horizontalArrangement = Arrangement.SpaceBetween,
									verticalAlignment = Alignment.CenterVertically,
								) {
									Text(
										text = file.fileName,
										style = MaterialTheme.typography.bodySmall,
										color = MaterialTheme.colorScheme.onSurface,
										modifier = Modifier.weight(1f),
									)
									TextButton(onClick = { photos.removeAt(index) }) {
										Text(text = "Remover", color = MaterialTheme.colorScheme.error)
									}
								}
							}
						}
					}

					if (error != null) {
						Text(text = error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
					}

					Button(
						onClick = {
							if (titulo.isNotBlank()) {
								onSubmit(
									titulo.trim(),
									descricao.trim().takeIf { it.isNotBlank() },
									categoria,
									prioridade,
									photos.toList(),
								)
							}
						},
						enabled = !isCreating && titulo.isNotBlank(),
						colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
						modifier = Modifier.fillMaxWidth(),
					) {
						Text(text = if (isCreating) "Enviando..." else "Abrir chamado", fontWeight = FontWeight.Black)
					}
				}
			},
		)
	}
}

// ---------------------------------------------------------------------------
// DETAIL
// ---------------------------------------------------------------------------

@Composable
private fun TicketDetailView(
	api: BillioneMotosApi,
	isLoading: Boolean,
	error: String?,
	actionError: String?,
	ticket: TicketResponse?,
	isAddingComment: Boolean,
	isUploadingPhoto: Boolean,
	canEdit: Boolean,
	onEdit: () -> Unit,
	onAddComment: (String, UploadFileRequest?) -> Unit,
	onAddPhoto: (UploadFileRequest) -> Unit,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var commentText by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
	var commentPhoto by remember { mutableStateOf<UploadFileRequest?>(null) }

	val commentPhotoPicker = rememberImagePicker { file ->
		if (file != null) {
			commentPhoto = file
		}
	}
	val extraPhotoPicker = rememberImagePicker { file ->
		if (file != null) {
			onAddPhoto(file)
		}
	}

	Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
		when {
			isLoading && ticket == null -> CustomerAppCard(content = { LoadingRow() })
			error != null && ticket == null -> CustomerAppCard(content = { ErrorRow(message = error, onRetry = onBack) })
			ticket == null -> CustomerAppCard(content = {
				Text(
					text = "Chamado indisponivel.",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			})
			else -> {
				CustomerAppCard(
					title = "#${ticket.numero} ${ticket.titulo}",
					description = "${categoriaLabel(ticket.categoria)} • ${prioridadeLabel(ticket.prioridade)}",
					content = {
						Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
							StatusChip(status = ticket.status)
							if (!ticket.descricao.isNullOrBlank()) {
								Text(
									text = ticket.descricao!!,
									style = MaterialTheme.typography.bodySmall,
									color = MaterialTheme.colorScheme.onSurface,
								)
							}
							ticket.moto?.let {
								Text(
									text = "Moto: ${it.modelo} • ${it.placa}",
									style = MaterialTheme.typography.labelSmall,
									color = MaterialTheme.colorScheme.onSurfaceVariant,
								)
							}
							ticket.createdAt?.let {
								Text(
									text = "Aberto em ${formatDate(it)}",
									style = MaterialTheme.typography.labelSmall,
									color = MaterialTheme.colorScheme.onSurfaceVariant,
								)
							}
							if (canEdit) {
								Button(
									onClick = onEdit,
									colors = ButtonDefaults.buttonColors(
										containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.24f),
									),
									modifier = Modifier.fillMaxWidth(),
								) {
									Text(text = "Editar chamado", fontWeight = FontWeight.Black)
								}
							}
						}
					},
				)

				// Fotos
				CustomerAppCard(
					title = "Fotos",
					content = {
						Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
							if (ticket.arquivos.isEmpty()) {
								Text(
									text = "Nenhuma foto anexada.",
									style = MaterialTheme.typography.bodySmall,
									color = MaterialTheme.colorScheme.onSurfaceVariant,
								)
							} else {
								ticket.arquivos.forEach { arquivo ->
									TicketPhoto(api = api, arquivo = arquivo)
								}
							}
							TextButton(
								onClick = { extraPhotoPicker() },
								enabled = !isUploadingPhoto,
							) {
								Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null)
								Spacer(modifier = Modifier.size(8.dp))
								Text(text = if (isUploadingPhoto) "Enviando foto..." else "Adicionar foto")
							}
						}
					},
				)

				// Comentarios
				CustomerAppCard(
					title = "Comentarios",
					content = {
						Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
							if (ticket.comentarios.isEmpty()) {
								Text(
									text = "Ainda nao ha comentarios.",
									style = MaterialTheme.typography.bodySmall,
									color = MaterialTheme.colorScheme.onSurfaceVariant,
								)
							} else {
								ticket.comentarios.forEach { comment ->
									CommentRow(comment = comment)
								}
							}

							OutlinedTextField(
								value = commentText,
								onValueChange = { commentText = it },
								label = { Text("Escreva um comentario") },
								modifier = Modifier.fillMaxWidth(),
								minLines = 2,
							)
							TextButton(onClick = { commentPhotoPicker() }) {
								Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null)
								Spacer(modifier = Modifier.size(8.dp))
								Text(text = commentPhoto?.fileName?.let { "Foto: $it" } ?: "Anexar foto (opcional)")
							}
							if (actionError != null) {
								Text(text = actionError, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
							}
							Button(
								onClick = {
									if (commentText.isNotBlank()) {
										onAddComment(commentText.trim(), commentPhoto)
										commentText = ""
										commentPhoto = null
									}
								},
								enabled = !isAddingComment && commentText.isNotBlank(),
								colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
								modifier = Modifier.fillMaxWidth(),
							) {
								Text(text = if (isAddingComment) "Enviando..." else "Comentar", fontWeight = FontWeight.Black)
							}
						}
					},
				)
			}
		}
	}
}

@Composable
private fun CommentRow(comment: TicketCommentResponse) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(14.dp))
			.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f))
			.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
			.padding(12.dp),
		verticalArrangement = Arrangement.spacedBy(2.dp),
	) {
		Text(
			text = comment.autorNome ?: comment.autorTipo,
			style = MaterialTheme.typography.labelSmall,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.primary,
		)
		Text(
			text = comment.mensagem,
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurface,
		)
		comment.createdAt?.let {
			Text(
				text = formatDate(it),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}

// ---------------------------------------------------------------------------
// EDIT
// ---------------------------------------------------------------------------

@Composable
private fun TicketEditView(
	isSaving: Boolean,
	error: String?,
	ticket: TicketResponse?,
	onSave: (String, String?) -> Unit,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var titulo by rememberSaveable(ticket?.id) { androidx.compose.runtime.mutableStateOf(ticket?.titulo.orEmpty()) }
	var descricao by rememberSaveable(ticket?.id) { androidx.compose.runtime.mutableStateOf(ticket?.descricao.orEmpty()) }

	Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
		CustomerAppCard(
			title = "Editar chamado",
			description = "Voce pode ajustar titulo e descricao enquanto o chamado esta aberto.",
			content = {
				Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
					OutlinedTextField(
						value = titulo,
						onValueChange = { titulo = it },
						label = { Text("Titulo") },
						modifier = Modifier.fillMaxWidth(),
					)
					OutlinedTextField(
						value = descricao,
						onValueChange = { descricao = it },
						label = { Text("Descricao") },
						modifier = Modifier.fillMaxWidth(),
						minLines = 3,
					)
					if (error != null) {
						Text(text = error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
					}
					Button(
						onClick = {
							if (titulo.isNotBlank()) {
								onSave(titulo.trim(), descricao.trim().takeIf { it.isNotBlank() })
							}
						},
						enabled = !isSaving && titulo.isNotBlank(),
						colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
						modifier = Modifier.fillMaxWidth(),
					) {
						Text(text = if (isSaving) "Salvando..." else "Salvar alteracoes", fontWeight = FontWeight.Black)
					}
				}
			},
		)
	}
}

// ---------------------------------------------------------------------------
// Shared pieces
// ---------------------------------------------------------------------------

@Composable
private fun TicketPhoto(api: BillioneMotosApi, arquivo: TicketArquivoResponse) {
	var bitmap by remember(arquivo.id) { mutableStateOf<ImageBitmap?>(null) }
	var failed by remember(arquivo.id) { mutableStateOf(false) }

	androidx.compose.runtime.LaunchedEffect(arquivo.id) {
		failed = false
		bitmap = null
		val url = api.config.baseUrl.trimEnd('/') + "/arquivos/${arquivo.id}/render"
		runCatching {
			withContext(Dispatchers.Default) {
				val bytes = api.fetchRawBytes(url)
				bytes.toImageBitmap()
			}
		}.onSuccess { decoded ->
			bitmap = decoded
		}.onFailure {
			println("[CustomerTicketsTab] failed to load photo ${arquivo.id}: ${it.message}")
			failed = true
		}
	}

	Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.aspectRatio(16f / 10f)
				.clip(RoundedCornerShape(14.dp))
				.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f)),
			contentAlignment = Alignment.Center,
		) {
			val current = bitmap
			when {
				current != null -> Image(
					bitmap = current,
					contentDescription = arquivo.nomeArquivo ?: "Foto do chamado",
					modifier = Modifier.fillMaxWidth(),
					contentScale = ContentScale.Crop,
				)
				failed -> Icon(
					imageVector = Icons.Default.BrokenImage,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurfaceVariant,
				)
				else -> CircularProgressIndicator(modifier = Modifier.size(28.dp))
			}
		}
		Text(
			text = arquivo.nomeArquivo ?: arquivo.descricao ?: "Foto",
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Composable
private fun StatusChip(status: TicketStatus) {
	Box(
		modifier = Modifier
			.clip(RoundedCornerShape(10.dp))
			.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
			.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f), RoundedCornerShape(10.dp))
			.padding(horizontal = 10.dp, vertical = 4.dp),
	) {
		Text(
			text = statusLabel(status),
			style = MaterialTheme.typography.labelSmall,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.primary,
		)
	}
}

@Composable
private fun LoadingRow() {
	Row(
		modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically,
	) {
		CircularProgressIndicator(modifier = Modifier.size(28.dp))
	}
}

@Composable
private fun ErrorRow(message: String, onRetry: () -> Unit) {
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Text(text = message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
		TextButton(onClick = onRetry) {
			Text(text = "Tentar novamente")
		}
	}
}



// ---------------------------------------------------------------------------
// Labels / formatting (pt-BR, ASCII to match neighboring files)
// ---------------------------------------------------------------------------

private fun categoriaLabel(categoria: TicketCategoria): String = when (categoria) {
	TicketCategoria.DIVERGENCIA_VALOR -> "Divergencia de valor"
	TicketCategoria.CONTESTACAO_COBRANCA -> "Contestacao de cobranca"
	TicketCategoria.PEDIDO_ESTORNO -> "Pedido de estorno"
	TicketCategoria.SEGUNDA_VIA_BOLETO -> "Segunda via de boleto"
	TicketCategoria.NEGOCIACAO -> "Negociacao"
	TicketCategoria.COMPROVANTE_PAGAMENTO -> "Comprovante de pagamento"
	TicketCategoria.MANUTENCAO -> "Manutencao"
	TicketCategoria.SOCORRO -> "Socorro"
	TicketCategoria.DUVIDA -> "Duvida"
	TicketCategoria.AJUDA -> "Ajuda"
	TicketCategoria.OUTROS -> "Outros"
}

private fun prioridadeLabel(prioridade: TicketPrioridade): String = when (prioridade) {
	TicketPrioridade.BAIXA -> "Baixa"
	TicketPrioridade.MEDIA -> "Media"
	TicketPrioridade.ALTA -> "Alta"
	TicketPrioridade.URGENTE -> "Urgente"
}

private fun statusLabel(status: TicketStatus): String = when (status) {
	TicketStatus.ABERTO -> "Aberto"
	TicketStatus.EM_ANALISE -> "Em analise"
	TicketStatus.AGUARDANDO_CLIENTE -> "Aguardando cliente"
	TicketStatus.RESOLVIDO -> "Resolvido"
	TicketStatus.CANCELADO -> "Cancelado"
}

/** Shows only the date portion when the API returns an ISO timestamp. */
private fun formatDate(raw: String): String {
	val datePart = raw.substringBefore('T')
	val pieces = datePart.split('-')
	return if (pieces.size == 3) "${pieces[2]}/${pieces[1]}/${pieces[0]}" else raw
}


