package com.example.billionemotosappkt.desktop.admin.clientes.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.desktop.admin.`fun`.pickDesktopImageFile
import java.io.File

// Modelo auxiliar simples para mapear a listagem
data class PlanoOpcao(val id: String, val nome: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteEditDialog(
	cliente: ClienteListItem,
	onDismiss: () -> Unit,
	onSave: (ClienteListItem) -> Unit
) {
	var form by remember { mutableStateOf(cliente) }
	
	// Controle do estado de abertura do Dropdown do Plano
	var planoExpanded by remember { mutableStateOf(false) }
	
	// Lista simulada de planos (Substitua pelos dados reais da sua aplicação)
	val planosDisponiveis = remember {
		listOf(
			PlanoOpcao("987e6543-e21b-34d3-a456-123456789abc", "Plano Mensal - Moto 160cc"),
			PlanoOpcao("111e2222-e33b-44d4-b555-666666666abc", "Plano Semanal - Entrega Rápida"),
			PlanoOpcao("555e6666-e77b-88d8-c999-000000000xyz", "Plano Trimestral - Premium")
		)
	}
	
	// Encontra o nome do plano selecionado atualmente para exibir no campo
	val planoSelecionadoNome =
		planosDisponiveis.find { it.id == form.planoId }?.nome ?: "Selecione um plano"
	
	// Instanciando as máscaras
	val cpfMask = remember { MaskTransformation("###.###.###-##") }
	val cepMask = remember { MaskTransformation("#####-###") }
	val phoneMask = remember { MaskTransformation("(##) #####-####") }
	
	Dialog(
		onDismissRequest = onDismiss,
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
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						"Editar Cliente",
						fontSize = 24.sp,
						fontWeight = FontWeight.Black,
						color = Color.White
					)
					IconButton(onClick = onDismiss) {
						Icon(
							Icons.Default.Close,
							null,
							tint = Color.White
						)
					}
				}
				
				Column(
					modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
						.padding(vertical = 16.dp),
					verticalArrangement = Arrangement.spacedBy(24.dp)
				) {
					// SEÇÃO 1: Identidade e Vínculos
					FormSection(title = "Dados Principais e Assinatura") {
						Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
							EditField(
								"Nome Completo",
								form.nome,
								{ form = form.copy(nome = it) },
								Modifier.weight(2f)
							)
							EditField(
								label = "CPF (Apenas números)",
								value = form.cpf,
								onValueChange = {
									if (it.length <= 11) form =
										form.copy(cpf = it.filter { c -> c.isDigit() })
								},
								modifier = Modifier.weight(1f),
								visualTransformation = cpfMask
							)
						}
						Row(
							horizontalArrangement = Arrangement.spacedBy(12.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							EditField(
								"CNH",
								form.cnh,
								{ form = form.copy(cnh = it.filter { c -> c.isDigit() }) },
								Modifier.weight(1f)
							)
							EditField(
								"Categoria",
								form.cnhCategoria,
								{ form = form.copy(cnhCategoria = it.take(2).uppercase()) },
								Modifier.weight(0.5f)
							)
							
							// Dropdown de seleção de Plano incorporado na Row
							// Dropdown de seleção de Plano incorporado na Row
							SelectEditField(
								label = "Plano",
								selectedName = planoSelecionadoNome,
								itens = planosDisponiveis.map { ItensSelect(it.nome, it.id) },
								onOptionSelected = {
								}
							)
						}
					}
					
					// SEÇÃO 2: Contato
					FormSection(title = "Contato") {
						Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
							EditField(
								"E-mail",
								form.email,
								{ form = form.copy(email = it) },
								Modifier.weight(1f)
							)
							EditField(
								label = "Telefone Principal",
								value = form.telefone,
								onValueChange = {
									if (it.length <= 11) form =
										form.copy(telefone = it.filter { c -> c.isDigit() })
								},
								modifier = Modifier.weight(1f),
								visualTransformation = phoneMask
							)
						}
						Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
							EditField(
								label = "Emergência 1",
								value = form.telefoneEmergencia1,
								onValueChange = {
									if (it.length <= 11) form =
										form.copy(telefoneEmergencia1 = it.filter { c -> c.isDigit() })
								},
								modifier = Modifier.weight(1f),
								visualTransformation = phoneMask
							)
							EditField(
								label = "Emergência 2",
								value = form.telefoneEmergencia2,
								onValueChange = {
									if (it.length <= 11) form =
										form.copy(telefoneEmergencia2 = it.filter { c -> c.isDigit() })
								},
								modifier = Modifier.weight(1f),
								visualTransformation = phoneMask
							)
						}
					}
					
					// SEÇÃO 3: Endereço
					FormSection(title = "Endereço") {
						EditField("Logradouro", form.endereco, { form = form.copy(endereco = it) })
//						EditField("Endereço de Parente/Referência", form.enderecoParente, { form = form.copy(enderecoParente = it) })
						
						Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
							EditField(
								"Cidade",
								form.cidade,
								{ form = form.copy(cidade = it) },
								Modifier.weight(2f)
							)
							SelectEditField(
								label = "Estado",
								selectedName = form.estado,
								itens = estadosBrasileiros.map { ItensSelect(it.sigla, it.nome) },
								onOptionSelected ={},
								modifier = Modifier.weight(0.5f),
							)
							EditField(
								label = "CEP",
								value = form.cep,
								onValueChange = {
									if (it.length <= 8) form =
										form.copy(cep = it.filter { c -> c.isDigit() })
								},
								modifier = Modifier.weight(1f),
								visualTransformation = cepMask
							)
						}
					}
					
					// SEÇÃO 5: Upload de Novas Imagens
					FormSection(title = "Documentação") {
						Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
							FileInputScreen("Foto CNH", Modifier.weight(1f))
							FileInputScreen("Identidade", Modifier.weight(1f))
							FileInputScreen("Comprovante", Modifier.weight(1f))
						}
					}
					
					// SEÇÃO 6: Observações
					FormSection(title = "Notas") {
						EditField(
							"Observações Internas",
							form.observacoes,
							{ form = form.copy(observacoes = it) },
							singleLine = false,
							minLines = 3
						)
					}
				}
				// Footer Actions
				Row(
					modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
					horizontalArrangement = Arrangement.End,
					verticalAlignment = Alignment.CenterVertically
				) {
					TextButton(
						onClick = onDismiss,
						modifier = Modifier.padding(horizontal = 8.dp)
					) {
						Text("Descartar", color = Color.White.copy(0.6f))
					}
					Button(
						onClick = { onSave(form) },
						shape = RoundedCornerShape(14.dp),
						colors = ButtonDefaults.buttonColors(
							containerColor = Color(0xFF20E65B),
							contentColor = Color.Black
						)
					) {
						Text("Salvar Alterações", fontWeight = FontWeight.Bold)
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
	modifier: Modifier = Modifier // Agora recebe o modifier
) {
	var expanded by remember { mutableStateOf(false) }
	
	ExposedDropdownMenuBox(
		expanded = expanded,
		onExpandedChange = { expanded = !expanded },
		modifier = modifier // Aplica o modifier aqui
	) {
		OutlinedTextField(
			value = selectedName,
			onValueChange = {},
			readOnly = true,
			label = { Text(label) },
			trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
			modifier = Modifier.menuAnchor().fillMaxWidth(),
			shape = RoundedCornerShape(12.dp),
			colors = OutlinedTextFieldDefaults.colors(
				focusedTextColor = Color.White,
				unfocusedTextColor = Color.White,
				unfocusedBorderColor = Color.White.copy(0.1f),
				focusedBorderColor = Color(0xFF20E65B),
				unfocusedLabelColor = Color.White.copy(0.4f),
				focusedLabelColor = Color(0xFF20E65B)
			)
		)
		
		ExposedDropdownMenu(
			expanded = expanded,
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
fun FileInputScreen(label: String, modifier: Modifier = Modifier) {
	var fileName by remember { mutableStateOf("Nenhum arquivo") }
	
	Row(
		modifier = modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(12.dp))
			.background(Color.White.copy(0.05f))
			.border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(12.dp))
			.padding(12.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Column(modifier = Modifier.weight(1f)) {
			Text(label, color = Color(0xFF20E65B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
			Text(
				text = fileName,
				color = Color.White.copy(0.6f),
				fontSize = 12.sp,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
		}
		
		Button(
			onClick = {
				pickDesktopImageFile()?.let { file ->
					fileName = file.name
				}
			},
			shape = RoundedCornerShape(8.dp),
			colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f))
		) {
			Text("Escolher", fontSize = 11.sp)
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
	visualTransformation: VisualTransformation = VisualTransformation.None
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(label) },
		modifier = modifier.fillMaxWidth(),
		singleLine = singleLine,
		minLines = minLines,
		visualTransformation = visualTransformation,
		shape = RoundedCornerShape(12.dp),
		colors = OutlinedTextFieldDefaults.colors(
			focusedTextColor = Color.White,
			unfocusedTextColor = Color.White,
			unfocusedBorderColor = Color.White.copy(0.1f),
			focusedBorderColor = Color(0xFF20E65B),
			unfocusedLabelColor = Color.White.copy(0.4f),
			focusedLabelColor = Color(0xFF20E65B)
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