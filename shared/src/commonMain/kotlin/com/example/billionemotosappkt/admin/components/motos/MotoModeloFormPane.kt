package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import com.example.billionemotosappkt.desktop.admin.data.MotoModeloFormState
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.FotoResponse
import com.example.billionemotosappkt.shared.utils.rememberImagePicker
import com.example.billionemotosappkt.desktop.admin.`fun`.desktopImagePainter
import kotlinx.coroutines.launch

@Composable
fun MotoModeloFormPane(
	form: MotoModeloFormState,
	onFormChange: (MotoModeloFormState) -> Unit,
	onSubmit: ((MotoModeloFormState) -> Unit)?,
	onDismiss: () -> Unit,
	submitLabel: String,
	apiBaseUrl: String,
	apiAccessToken: String?,
	modelId: String? = null,
	api: BillioneMotosApi? = null,
	modifier: Modifier = Modifier,
) {
	val scrollState = rememberScrollState()
	Column(
		modifier = modifier
			.fillMaxHeight()
			.background(Color(0xFF090D0B))
			.padding(18.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Text(
			text = "Ficha do modelo",
			color = Color.White,
			fontSize = 18.sp,
			fontWeight = FontWeight.Black,
		)
		
		Column(
			modifier = Modifier
				.weight(1f)
				.verticalScroll(scrollState),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
				val split = maxWidth >= 760.dp
				if (split) {
					Row(
						horizontalArrangement = Arrangement.spacedBy(12.dp),
						modifier = Modifier.fillMaxWidth()
					) {
						Column(
							modifier = Modifier.weight(1f),
							verticalArrangement = Arrangement.spacedBy(12.dp)
						) {
							MotoModeloFormSection(
								title = "Identidade",
								subtitle = "Dados principais exibidos na listagem e na ficha.",
							) {
								FormGridField(
									"Marca",
									form.marca
								) { onFormChange(form.copy(marca = it)) }
								FormGridField(
									"Nome",
									form.nome
								) { onFormChange(form.copy(nome = it)) }
								FormGridField(
									"Modelo",
									form.modelo
								) { onFormChange(form.copy(modelo = it)) }
							}
							MotoModeloFormSection(
								title = "Especificações",
								subtitle = "Medições e dados técnicos que ajudam no catálogo.",
							) {
								FormGridField(
									"Cilindrada",
									form.cilindrada
								) { onFormChange(form.copy(cilindrada = it)) }
								FormGridField("Preço inicial", form.precoInicial) {
									onFormChange(
										form.copy(precoInicial = it)
									)
								}
								FormGridField(
									"Combustível",
									form.combustivel
								) { onFormChange(form.copy(combustivel = it)) }
							}
						}
						Column(
							modifier = Modifier.weight(1f),
							verticalArrangement = Arrangement.spacedBy(12.dp)
						) {
							MotoModeloFormSection(
								title = "Classificação",
								subtitle = "Segmentação usada para organização e busca.",
							) {
								FormGridField("Categoria", form.categoria) {
									onFormChange(
										form.copy(
											categoria = it
										)
									)
								}
								FormGridField(
									"Tipo",
									form.tipo
								) { onFormChange(form.copy(tipo = it)) }
								FormGridField("Ano", form.ano) { onFormChange(form.copy(ano = it)) }
								FormGridField(
									"Código FIPE",
									form.codigoFipe
								) { onFormChange(form.copy(codigoFipe = it)) }
							}
							MotoModeloFormSection(
								title = "Mídia",
								subtitle = "Envie uma imagem real do modelo para o preview.",
							) {
								MotoImagePickerField(
									imageValue = form.imagemReferenciaUrl,
									onImageSelected = { path, upload ->
										onFormChange(
											form.copy(
												imagemReferenciaUrl = path,
												imagemReferenciaUpload = upload
											)
										)
									},
									apiBaseUrl = apiBaseUrl,
									apiAccessToken = apiAccessToken,
								)
							}
							MotoModeloFormSection(
								title = "Descrição",
								subtitle = "Texto descritivo exibido no cartão e no detalhe.",
							) {
								FormGridField(
									"Descrição",
									form.descricao,
									singleLine = false,
									minLines = 4
								) {
									onFormChange(form.copy(descricao = it))
								}
							}
						}
					}
				} else {
					MotoModeloFormSection(
						title = "Identidade",
						subtitle = "Dados principais do modelo.",
					) {
						FormGridField("Marca", form.marca) { onFormChange(form.copy(marca = it)) }
						FormGridField("Nome", form.nome) { onFormChange(form.copy(nome = it)) }
						FormGridField(
							"Modelo",
							form.modelo
						) { onFormChange(form.copy(modelo = it)) }
					}
					MotoModeloFormSection(
						title = "Especificações",
						subtitle = "Informações técnicas e de exibição.",
					) {
						FormGridField("Cilindrada", form.cilindrada) {
							onFormChange(
								form.copy(
									cilindrada = it
								)
							)
						}
						FormGridField("Preço inicial", form.precoInicial) {
							onFormChange(
								form.copy(
									precoInicial = it
								)
							)
						}
						FormGridField("Combustível", form.combustivel) {
							onFormChange(
								form.copy(
									combustivel = it
								)
							)
						}
						FormGridField("Categoria", form.categoria) {
							onFormChange(
								form.copy(
									categoria = it
								)
							)
						}
						FormGridField("Tipo", form.tipo) { onFormChange(form.copy(tipo = it)) }
						FormGridField("Ano", form.ano) { onFormChange(form.copy(ano = it)) }
						FormGridField("Código FIPE", form.codigoFipe) {
							onFormChange(
								form.copy(
									codigoFipe = it
								)
							)
						}
					}
					MotoModeloFormSection(
						title = "Mídia",
						subtitle = "Use uma imagem real do modelo.",
					) {
						MotoImagePickerField(
							imageValue = form.imagemReferenciaUrl,
							onImageSelected = { path, upload ->
								onFormChange(
									form.copy(
										imagemReferenciaUrl = path,
										imagemReferenciaUpload = upload
									)
								)
							},
							apiBaseUrl = apiBaseUrl,
							apiAccessToken = apiAccessToken,
						)
					}
					MotoModeloFormSection(
						title = "Descrição",
						subtitle = "Descrição comercial ou técnica.",
					) {
						FormGridField(
							"Descrição",
							form.descricao,
							singleLine = false,
							minLines = 4
						) {
							onFormChange(form.copy(descricao = it))
						}
					}
				}
			}

			if (modelId != null && api != null) {
				var photos by remember(modelId) { mutableStateOf<List<FotoResponse>>(emptyList()) }
				var isLoadingPhotos by remember(modelId) { mutableStateOf(false) }
				var photoError by remember(modelId) { mutableStateOf<String?>(null) }
				val scope = rememberCoroutineScope()

				LaunchedEffect(modelId) {
					isLoadingPhotos = true
					runCatching {
						api.motos.listModeloPhotos(modelId).items
					}.onSuccess {
						photos = it
						photoError = null
					}.onFailure {
						photoError = it.message ?: "Erro ao carregar fotos"
					}
					isLoadingPhotos = false
				}

				MotoModeloFormSection(
					title = "Galeria de Fotos",
					subtitle = "Outras fotos do modelo cadastradas no sistema.",
				) {
					if (isLoadingPhotos) {
						Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
							CircularProgressIndicator(color = Color(0xFF20E65B))
						}
					} else {
						if (photoError != null) {
							Text(photoError!!, color = Color(0xFFFF4A4A), fontSize = 12.sp)
						}

						Row(
							modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
							horizontalArrangement = Arrangement.spacedBy(10.dp)
						) {
							photos.forEach { photo ->
								Box(
									modifier = Modifier
										.size(116.dp, 88.dp)
										.clip(RoundedCornerShape(8.dp))
										.border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
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
													api.motos.deleteModeloPhoto(modelId, photo.id)
												}.onSuccess {
													photos = api.motos.listModeloPhotos(modelId).items
													photoError = null
												}.onFailure {
													photoError = it.message ?: "Erro ao remover foto"
												}
											}
										},
										modifier = Modifier
											.align(Alignment.TopEnd)
											.padding(4.dp)
											.size(24.dp)
											.clip(RoundedCornerShape(999.dp))
											.background(Color.Black.copy(alpha = 0.6f))
									) {
										Icon(Icons.Default.Delete, null, tint = Color(0xFFFF4A4A), modifier = Modifier.size(14.dp))
									}
								}
							}
						}

						Spacer(modifier = Modifier.height(6.dp))

						val pickImage = rememberImagePicker { fileRequest ->
							if (fileRequest != null) {
								scope.launch {
									runCatching {
										api.motos.addModeloPhoto(modelId, fileRequest)
									}.onSuccess {
										photos = api.motos.listModeloPhotos(modelId).items
										photoError = null
									}.onFailure {
										photoError = it.message ?: "Erro ao enviar foto"
									}
								}
							}
						}

						Button(
							onClick = { pickImage() },
							shape = RoundedCornerShape(10.dp),
							colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20E65B).copy(alpha = 0.15f), contentColor = Color(0xFF20E65B))
						) {
							Icon(Icons.Default.Image, null, modifier = Modifier.size(16.dp))
							Spacer(modifier = Modifier.width(8.dp))
							Text("Adicionar foto", fontSize = 12.sp)
						}
					}
				}
			}
		}
		
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(10.dp),
		) {
			TextButton(onClick = onDismiss) {
				Text("Cancelar")
			}
			Spacer(modifier = Modifier.weight(1f))
			if (onSubmit != null) {
				Button(
					onClick = { onSubmit(form) },
					enabled = form.marca.isNotBlank() && form.nome.isNotBlank(),
					shape = RoundedCornerShape(16.dp),
					colors = ButtonDefaults.buttonColors(
						containerColor = Color(0xFF20E65B),
						contentColor = Color.Black,
					),
				) {
					Text(submitLabel, fontWeight = FontWeight.SemiBold)
				}
			}
		}
	}
}