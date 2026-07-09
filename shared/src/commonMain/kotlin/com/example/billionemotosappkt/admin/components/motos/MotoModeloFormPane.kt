package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.data.MotoModeloFormState


@Composable
fun MotoModeloFormPane(
	form: MotoModeloFormState,
	onFormChange: (MotoModeloFormState) -> Unit,
	onSubmit: ((MotoModeloFormState) -> Unit)?,
	onDismiss: () -> Unit,
	submitLabel: String,
	apiBaseUrl: String,
	apiAccessToken: String?,
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