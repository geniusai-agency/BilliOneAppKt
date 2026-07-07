package com.example.billionemotosappkt.desktop.admin.clientes.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import com.example.billionemotosappkt.desktop.admin.clientes.repository.ClientBody
import kotlinx.coroutines.launch
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.desktop.admin.`fun`.pickDesktopImageFile
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.PlanoResponse
import com.example.billionemotosappkt.shared.api.UpdateClienteRequest
import java.awt.Desktop
import java.io.File
import java.net.URI

// Modelo auxiliar simples para mapear a listagem
data class PlanoOpcao(val id: String, val nome: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteEditDialog(
	api: BillioneMotosApi,
	cliente: UpdateClienteRequest,
	planosDisponiveis: List<PlanoResponse>,
	onDismiss: () -> Unit,
	onSave: suspend (ClientBody) -> Unit
) {
	var form by remember { mutableStateOf(cliente) }
	var isSaving by remember { mutableStateOf(false) }
	val scope = rememberCoroutineScope()

	var cnhFile by remember { mutableStateOf<File?>(null) }
	var identidadeFile by remember { mutableStateOf<File?>(null) }
	var comprovanteFile by remember { mutableStateOf<File?>(null) }

	// Encontra o nome do plano selecionado atualmente para exibir no campo
	val planoSelecionadoNome =
		planosDisponiveis.find { it.id == form.planoId }?.nome ?: "Selecione um plano"
	
	// Instanciando as máscaras
	val cpfMask = remember { MaskTransformation("###.###.###-##") }
	val cepMask = remember { MaskTransformation("#####-###") }
	val phoneMask = remember { MaskTransformation("(##) #####-####") }
	
	Dialog(
		onDismissRequest = { if (!isSaving) onDismiss() },
		properties = DialogProperties(usePlatformDefaultWidth = false)
	) {
		Surface(
			modifier = Modifier.fillMaxWidth(0.85f).fillMaxHeight(0.9f),
			shape = RoundedCornerShape(28.dp),
			color = Color(0xFF090D0B),
			border = BorderStroke(1.dp, Color.White.copy(0.1f))
		) {
			Column(modifier = Modifier.padding(24.dp)) {
				// Header
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Column {
						Text(
							"Editar Cliente",
							fontSize = 28.sp,
							fontWeight = FontWeight.Black,
							color = Color.White
						)
						Text(
							"Atualize as informações cadastrais e documentos",
							fontSize = 14.sp,
							color = Color.White.copy(0.5f)
						)
					}
					IconButton(
						onClick = onDismiss,
						enabled = !isSaving,
						modifier = Modifier.background(Color.White.copy(0.05f), CircleShape)
					) {
						Icon(
							Icons.Default.Close,
							null,
							tint = Color.White
						)
					}
				}
				
				Spacer(modifier = Modifier.height(24.dp))

				Column(
					modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
					verticalArrangement = Arrangement.spacedBy(24.dp)
				) {
					// SEÇÃO 1: Identidade e Vínculos
					FormSection(title = "Dados Principais e Assinatura") {
						Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
							EditField(
								label = "Nome Completo",
								value = form.nome.orEmpty(),
								onValueChange = { form = form.copy(nome = it) },
								modifier = Modifier.weight(2f),
								enabled = !isSaving
							)
							EditField(
								label = "CPF",
								value = form.cpf.orEmpty(),
								onValueChange = {
									if (it.length <= 11) form =
										form.copy(cpf = it.filter { c -> c.isDigit() })
								},
								modifier = Modifier.weight(1f),
								visualTransformation = cpfMask,
								enabled = !isSaving
							)
						}
						Row(
							horizontalArrangement = Arrangement.spacedBy(16.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							EditField(
								"CNH",
								form.cnh.orEmpty(),
								{ form = form.copy(cnh = it.filter { c -> c.isDigit() }) },
								Modifier.weight(1f),
								enabled = !isSaving
							)
							EditField(
								"Categoria",
								form.cnhCategoria.orEmpty(),
								{ form = form.copy(cnhCategoria = it.take(2).uppercase()) },
								Modifier.weight(0.5f),
								enabled = !isSaving
							)
							
							SelectEditField(
								label = "Plano de Assinatura",
								selectedName = planoSelecionadoNome,
								itens = planosDisponiveis.map { ItensSelect(it.nome, it.id) },
								onOptionSelected = { opcao ->
									form = form.copy(planoId = opcao.value)
								},
								modifier = Modifier.weight(1.5f),
								enabled = !isSaving
							)
						}
					}
					
					// SEÇÃO 2: Contato
					FormSection(title = "Canais de Contato") {
						Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
							EditField(
								"E-mail Corporativo/Pessoal",
								form.email.orEmpty(),
								{ form = form.copy(email = it) },
								Modifier.weight(1f),
								enabled = !isSaving
							)
							EditField(
								label = "Telefone Principal",
								value = form.telefone.orEmpty(),
								onValueChange = {
									if (it.length <= 11) form =
										form.copy(telefone = it.filter { c -> c.isDigit() })
								},
								modifier = Modifier.weight(1f),
								visualTransformation = phoneMask,
								enabled = !isSaving
							)
						}
						Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
							EditField(
								label = "Telefone Emergência 1",
								value = form.telefoneEmergencia1.orEmpty(),
								onValueChange = {
									if (it.length <= 11) form =
										form.copy(telefoneEmergencia1 = it.filter { c -> c.isDigit() })
								},
								modifier = Modifier.weight(1f),
								visualTransformation = phoneMask,
								enabled = !isSaving
							)
							EditField(
								label = "Telefone Emergência 2",
								value = form.telefoneEmergencia2.orEmpty(),
								onValueChange = {
									if (it.length <= 11) form =
										form.copy(telefoneEmergencia2 = it.filter { c -> c.isDigit() })
								},
								modifier = Modifier.weight(1f),
								visualTransformation = phoneMask,
								enabled = !isSaving
							)
						}
					}
					
					// SEÇÃO 3: Endereço
					FormSection(title = "Localização") {
						EditField("Logradouro e Número", form.endereco.orEmpty(), { form = form.copy(endereco = it) }, enabled = !isSaving)
						EditField("Endereço de Parente/Referência", form.enderecoParente.orEmpty(), { form = form.copy(enderecoParente = it) }, enabled = !isSaving)
						
						Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
							EditField(
								"Cidade",
								form.cidade.orEmpty(),
								{ form = form.copy(cidade = it) },
								Modifier.weight(2f),
								enabled = !isSaving
							)
							SelectEditField(
								label = "Estado",
								selectedName = form.estado.orEmpty(),
								itens = estadosBrasileiros.map { ItensSelect(it.sigla, it.nome) },
								onOptionSelected = { opcao ->
									form = form.copy(estado = opcao.name)
								},
								modifier = Modifier.weight(0.8f),
								enabled = !isSaving
							)
							EditField(
								label = "CEP",
								value = form.cep.orEmpty(),
								onValueChange = {
									if (it.length <= 8) form =
										form.copy(cep = it.filter { c -> c.isDigit() })
								},
								modifier = Modifier.weight(1f),
								visualTransformation = cepMask,
								enabled = !isSaving
							)
						}
					}
					
					// SEÇÃO 5: Upload de Novas Imagens
					FormSection(title = "Documentação (Upload)") {
						Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
							FileUploadField(
								label = "Foto CNH",
								initialUrl = form.cnhUrl,
								api = api,
								modifier = Modifier.weight(1f),
								enabled = !isSaving
							) { cnhFile = it }
							FileUploadField(
								label = "Foto Identidade",
								initialUrl = form.identidadeUrl,
								api = api,
								modifier = Modifier.weight(1f),
								enabled = !isSaving
							) { identidadeFile = it }
							FileUploadField(
								label = "Comprovante Residência",
								initialUrl = form.comprovanteResidenciaUrl,
								api = api,
								modifier = Modifier.weight(1f),
								enabled = !isSaving
							) { comprovanteFile = it }
						}
					}
					
					// SEÇÃO 6: Observações
					FormSection(title = "Observações Adicionais") {
						EditField(
							"Notas Internas",
							form.observacoes.orEmpty(),
							{ form = form.copy(observacoes = it) },
							singleLine = false,
							minLines = 3,
							enabled = !isSaving
						)
					}
				}

				// Footer Actions
				Row(
					modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
					horizontalArrangement = Arrangement.End,
					verticalAlignment = Alignment.CenterVertically
				) {
					TextButton(
						onClick = onDismiss,
						enabled = !isSaving,
						modifier = Modifier.padding(horizontal = 16.dp)
					) {
						Text("Descartar Alterações", color = Color.White.copy(0.6f))
					}
					Button(
						onClick = {
							scope.launch {
								isSaving = true
								try {
									onSave(
										ClientBody(
											nome = form.nome.orEmpty(),
											cpf = form.cpf.orEmpty(),
											email = form.email.orEmpty(),
											cnh = form.cnh.orEmpty(),
											cnhCategoria = form.cnhCategoria.orEmpty(),
											endereco = form.endereco.orEmpty(),
											enderecoParente = form.enderecoParente.orEmpty(),
											cidade = form.cidade.orEmpty(),
											estado = form.estado.orEmpty(),
											telefone = form.telefone.orEmpty(),
											cep = form.cep.orEmpty(),
											telefoneEmergencia1 = form.telefoneEmergencia1.orEmpty(),
											telefoneEmergencia2 = form.telefoneEmergencia2.orEmpty(),
											observacoes = form.observacoes.orEmpty(),
											planoId = form.planoId.orEmpty(),
											comprovanteData = form.comprovanteData.orEmpty(),
											cnhFile = cnhFile,
											identidadeFile = identidadeFile,
											comprovanteFile = comprovanteFile
										)
									)
									onDismiss()
								} finally {
									isSaving = false
								}
							}
						},
						enabled = !isSaving,
						shape = RoundedCornerShape(16.dp),
						colors = ButtonDefaults.buttonColors(
							containerColor = Color(0xFF20E65B),
							contentColor = Color.Black,
							disabledContainerColor = Color(0xFF20E65B).copy(0.3f)
						),
						modifier = Modifier.height(52.dp).padding(horizontal = 8.dp)
					) {
						if (isSaving) {
							CircularProgressIndicator(
								modifier = Modifier.size(24.dp),
								color = Color.Black,
								strokeWidth = 3.dp
							)
							Spacer(Modifier.width(12.dp))
						}
						Text(
							if (isSaving) "Salvando..." else "Salvar Alterações",
							fontWeight = FontWeight.Bold,
							fontSize = 16.sp
						)
					}
				}
			}
		}
	}
}

// FormSection e EditField mantidos idênticos aqui abaixo...
@Composable
private fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		Text(
			title.uppercase(),
			fontSize = 11.sp,
			fontWeight = FontWeight.Bold,
			color = Color(0xFF20E65B)
		)
		content()
	}
}

data class ItensSelect(val name: String, val value: String) {}

// 1. Corrija o SelectEditField para aceitar um modifier
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectEditField(
	label: String,
	selectedName: String,
	itens: List<ItensSelect>,
	onOptionSelected: (ItensSelect) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true
) {
	var expanded by remember { mutableStateOf(false) }
	
	ExposedDropdownMenuBox(
		expanded = expanded && enabled,
		onExpandedChange = { if (enabled) expanded = !expanded },
		modifier = modifier
	) {
		OutlinedTextField(
			value = selectedName,
			onValueChange = {},
			readOnly = true,
			enabled = enabled,
			label = { Text(label) },
			trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
			modifier = Modifier.menuAnchor().fillMaxWidth(),
			shape = RoundedCornerShape(12.dp),
			colors = OutlinedTextFieldDefaults.colors(
				focusedTextColor = Color.White,
				unfocusedTextColor = Color.White,
				disabledTextColor = Color.White.copy(0.4f),
				unfocusedBorderColor = Color.White.copy(0.1f),
				focusedBorderColor = Color(0xFF20E65B),
				disabledBorderColor = Color.White.copy(0.05f),
				unfocusedLabelColor = Color.White.copy(0.4f),
				focusedLabelColor = Color(0xFF20E65B),
				disabledLabelColor = Color.White.copy(0.2f)
			)
		)
		
		ExposedDropdownMenu(
			expanded = expanded && enabled,
			onDismissRequest = { expanded = false }
		) {
			itens.forEach { opcao ->
				DropdownMenuItem(
					text = { Text(opcao.name) },
					onClick = {
						onOptionSelected(opcao)
						expanded = false
					}
				)
			}
		}
	}
}

@Composable
fun FileUploadField(
	label: String,
	api: BillioneMotosApi,
	modifier: Modifier = Modifier,
	initialUrl: String? = null,
	enabled: Boolean = true,
	onFileSelected: (File) -> Unit = {}
) {
	var file by remember { mutableStateOf<File?>(null) }
	var isExpandedViewOpen by remember { mutableStateOf(false) }

	val hasLocalFile = file != null
	val hasRemoteFile = !initialUrl.isNullOrBlank()
	val isSelected = hasLocalFile || hasRemoteFile

	var remoteBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
	var isLoadingRemote by remember { mutableStateOf(false) }

	LaunchedEffect(initialUrl) {
		if (hasRemoteFile && !hasLocalFile) {
			isLoadingRemote = true
			runCatching {
				val fullUrl = if (initialUrl!!.startsWith("http")) {
					initialUrl
				} else {
					api.config.baseUrl.removeSuffix("/") + "/" + initialUrl.removePrefix("/")
				}
				val bytes = api.fetchRawBytes(fullUrl)
				remoteBitmap = org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
			}.onFailure {
				println("Erro ao carregar imagem da API ($initialUrl): ${it.message}")
			}
			isLoadingRemote = false
		}
	}

	val localBitmap = remember(file) {
		file?.let {
			runCatching {
				org.jetbrains.skia.Image.makeFromEncoded(it.readBytes()).toComposeImageBitmap()
			}.getOrNull()
		}
	}

	val displayBitmap = localBitmap ?: remoteBitmap

	// MODAL DE VISUALIZAÇÃO EXPANDIDA
	if (isExpandedViewOpen && displayBitmap != null) {
		Dialog(
			onDismissRequest = { isExpandedViewOpen = false },
			properties = DialogProperties(usePlatformDefaultWidth = false)
		) {
			Box(
				modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.8f)).clickable { isExpandedViewOpen = false },
				contentAlignment = Alignment.Center
			) {
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Image(
						bitmap = displayBitmap,
						contentDescription = null,
						modifier = Modifier.fillMaxHeight(0.8f).clip(RoundedCornerShape(12.dp)),
						contentScale = androidx.compose.ui.layout.ContentScale.Fit
					)
					Spacer(Modifier.height(16.dp))
					Button(
						onClick = { isExpandedViewOpen = false },
						colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.2f))
					) {
						Text("Fechar Visualização")
					}
				}
			}
		}
	}

	Column(
		modifier = modifier
			.clip(RoundedCornerShape(18.dp))
			.background(
				if (isSelected) Color(0xFF12381E).copy(alpha = if (enabled) 1f else 0.5f)
				else Color.White.copy(if (enabled) .04f else .02f)
			)
			.border(
				1.dp,
				if (isSelected) Color(0xFF20E65B).copy(alpha = if (enabled) 1f else 0.3f)
				else Color.White.copy(if (enabled) .08f else .04f),
				RoundedCornerShape(18.dp)
			)
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Box(
			modifier = Modifier
				.size(120.dp)
				.clip(RoundedCornerShape(14.dp))
				.background(Color.Black.copy(0.2f)),
			contentAlignment = Alignment.Center
		) {
			if (isLoadingRemote) {
				CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF20E65B))
			} else if (displayBitmap != null) {
				Image(
					bitmap = displayBitmap,
					contentDescription = null,
					modifier = Modifier.fillMaxSize(),
					contentScale = androidx.compose.ui.layout.ContentScale.Crop
				)
			} else {
				Icon(
					Icons.Default.UploadFile,
					null,
					tint = Color.White.copy(.2f),
					modifier = Modifier.size(32.dp)
				)
			}
		}

		Spacer(Modifier.height(12.dp))

		Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

		if (isSelected) {
			Spacer(Modifier.height(4.dp))
			Text(
				if (hasLocalFile) "Novo arquivo" else "Já cadastrado",
				color = Color(0xFF20E65B),
				fontSize = 10.sp,
				fontWeight = FontWeight.Bold
			)

			Spacer(Modifier.height(8.dp))

			TextButton(
				onClick = { isExpandedViewOpen = true },
				enabled = displayBitmap != null,
				modifier = Modifier.height(32.dp)
			) {
				Icon(Icons.Default.Visibility, null, modifier = Modifier.size(16.dp), tint = Color(0xFF20E65B))
				Spacer(Modifier.width(6.dp))
				Text("Visualizar", color = Color(0xFF20E65B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
			}
		}

		Spacer(Modifier.height(8.dp))

		Button(
			onClick = {
				pickDesktopImageFile()?.let {
					file = it
					onFileSelected(it)
				}
			},
			enabled = enabled,
			colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(.08f), contentColor = Color.White),
			shape = RoundedCornerShape(10.dp),
			modifier = Modifier.fillMaxWidth().height(36.dp)
		) {
			Text(if (isSelected) "Substituir" else "Selecionar", fontSize = 12.sp)
		}
	}
}

@Composable
private fun EditField(
	label: String,
	value: String,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
	singleLine: Boolean = true,
	minLines: Int = 1,
	enabled: Boolean = true,
	visualTransformation: VisualTransformation = VisualTransformation.None
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(label) },
		modifier = modifier.fillMaxWidth(),
		singleLine = singleLine,
		minLines = minLines,
		enabled = enabled,
		visualTransformation = visualTransformation,
		shape = RoundedCornerShape(12.dp),
		colors = OutlinedTextFieldDefaults.colors(
			focusedTextColor = Color.White,
			unfocusedTextColor = Color.White,
			disabledTextColor = Color.White.copy(0.4f),
			unfocusedBorderColor = Color.White.copy(0.1f),
			focusedBorderColor = Color(0xFF20E65B),
			disabledBorderColor = Color.White.copy(0.05f),
			unfocusedLabelColor = Color.White.copy(0.4f),
			focusedLabelColor = Color(0xFF20E65B),
			disabledLabelColor = Color.White.copy(0.2f)
		)
	)
}


data class EstadoOpcao(val sigla: String, val nome: String)

val estadosBrasileiros = listOf(
	EstadoOpcao("AC", "Acre"),
	EstadoOpcao("AL", "Alagoas"),
	EstadoOpcao("AP", "Amapá"),
	EstadoOpcao("AM", "Amazonas"),
	EstadoOpcao("BA", "Bahia"),
	EstadoOpcao("CE", "Ceará"),
	EstadoOpcao("DF", "Distrito Federal"),
	EstadoOpcao("ES", "Espírito Santo"),
	EstadoOpcao("GO", "Goiás"),
	EstadoOpcao("MA", "Maranhão"),
	EstadoOpcao("MT", "Mato Grosso"),
	EstadoOpcao("MS", "Mato Grosso do Sul"),
	EstadoOpcao("MG", "Minas Gerais"),
	EstadoOpcao("PA", "Pará"),
	EstadoOpcao("PB", "Paraíba"),
	EstadoOpcao("PR", "Paraná"),
	EstadoOpcao("PE", "Pernambuco"),
	EstadoOpcao("PI", "Piauí"),
	EstadoOpcao("RJ", "Rio de Janeiro"),
	EstadoOpcao("RN", "Rio Grande do Norte"),
	EstadoOpcao("RS", "Rio Grande do Sul"),
	EstadoOpcao("RO", "Rondônia"),
	EstadoOpcao("RR", "Roraima"),
	EstadoOpcao("SC", "Santa Catarina"),
	EstadoOpcao("SP", "São Paulo"),
	EstadoOpcao("SE", "Sergipe"),
	EstadoOpcao("TO", "Tocantins")
)
