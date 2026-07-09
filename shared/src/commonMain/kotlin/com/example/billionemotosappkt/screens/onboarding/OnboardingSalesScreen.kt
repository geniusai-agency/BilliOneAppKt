package com.example.billionemotosappkt.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.billionemotosappkt.screens.Loading.LoadingScreen
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.MotoModeloResponse
import com.example.billionemotosappkt.shared.api.PlanoResponse
import com.example.billionemotosappkt.shared.api.SolicitarAnaliseRequest
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import com.example.billionemotosappkt.shared.utils.BackHandler
import com.example.billionemotosappkt.shared.utils.rememberImagePicker
import com.example.billionemotosappkt.shared.utils.toImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val STEP_VITRINE = 0
private const val STEP_MOTO = 1
private const val STEP_PLANO = 2
private const val STEP_DOCS = 3
private const val STEP_REVIEW = 4
private const val STEP_SUCCESS = 5

/**
 * Pre-contract sales/onboarding screen. Shown to authenticated users that do not
 * yet have a contract. Presents the storefront (motos + planos) and a step-by-step
 * flow to submit an analysis request with the required document photos.
 */
@Composable
fun OnboardingSalesScreen(
	authState: AuthUiState,
	api: BillioneMotosApi,
	onLogout: () -> Unit,
	onRefreshSession: () -> Unit = {},
	modifier: Modifier = Modifier,
) {
	val viewModel = remember(api) { OnboardingSalesViewModel(api) }
	val state = viewModel.uiState.collectAsStateWithLifecycle().value

	var step by rememberSaveable { mutableStateOf(STEP_VITRINE) }
	var selectedModeloId by rememberSaveable { mutableStateOf("") }
	var selectedPlanoId by rememberSaveable { mutableStateOf("") }

	var cnh by rememberSaveable { mutableStateOf("") }
	var cnhCategoria by rememberSaveable { mutableStateOf("") }
	var endereco by rememberSaveable { mutableStateOf("") }
	var cidade by rememberSaveable { mutableStateOf("") }
	var estado by rememberSaveable { mutableStateOf("") }
	var cep by rememberSaveable { mutableStateOf("") }
	var enderecoParente by rememberSaveable { mutableStateOf("") }
	var telefone1 by rememberSaveable { mutableStateOf("") }
	var telefone2 by rememberSaveable { mutableStateOf("") }
	var comprovanteData by rememberSaveable { mutableStateOf("") }
	var observacoes by rememberSaveable { mutableStateOf("") }

	var cnhImage by remember { mutableStateOf<UploadFileRequest?>(null) }
	var identidadeImage by remember { mutableStateOf<UploadFileRequest?>(null) }
	var comprovanteImage by remember { mutableStateOf<UploadFileRequest?>(null) }

	val cnhImagePicker = rememberImagePicker { if (it != null) cnhImage = it }
	val identidadeImagePicker = rememberImagePicker { if (it != null) identidadeImage = it }
	val comprovanteImagePicker = rememberImagePicker { if (it != null) comprovanteImage = it }

	LaunchedEffect(state.submitSuccess) {
		if (state.submitSuccess) {
			step = STEP_SUCCESS
			onRefreshSession()
		}
	}

	BackHandler(enabled = step != STEP_VITRINE && step != STEP_SUCCESS) {
		if (step > STEP_VITRINE) step -= 1
	}

	if (state.isLoading && state.planos.isEmpty() && state.modelos.isEmpty()) {
		LoadingScreen(modifier = modifier)
		return
	}

	Scaffold(
		modifier = modifier.fillMaxSize().safeDrawingPadding(),
		topBar = {
			OnboardingTopBar(
				userName = authState.userName,
				showBack = step != STEP_VITRINE && step != STEP_SUCCESS,
				onBack = { if (step > STEP_VITRINE) step -= 1 },
				onLogout = onLogout,
			)
		},
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.verticalScroll(rememberScrollState())
				.padding(horizontal = 16.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			when (step) {
				STEP_VITRINE -> VitrineStep(
					authState = authState,
					state = state,
					api = api,
					onStart = { step = STEP_MOTO },
				)

				STEP_MOTO -> ChooseMotoStep(
					api = api,
					modelos = state.modelos,
					selectedId = selectedModeloId,
					onSelect = { selectedModeloId = it },
					onNext = { step = STEP_PLANO },
				)

				STEP_PLANO -> ChoosePlanoStep(
					planos = state.planos,
					selectedId = selectedPlanoId,
					onSelect = { selectedPlanoId = it },
					onNext = { step = STEP_DOCS },
				)

				STEP_DOCS -> DocumentsStep(
					cnh = cnh, onCnh = { cnh = it },
					cnhCategoria = cnhCategoria, onCnhCategoria = { cnhCategoria = it },
					endereco = endereco, onEndereco = { endereco = it },
					cidade = cidade, onCidade = { cidade = it },
					estado = estado, onEstado = { estado = it },
					cep = cep, onCep = { cep = it },
					enderecoParente = enderecoParente, onEnderecoParente = { enderecoParente = it },
					telefone1 = telefone1, onTelefone1 = { telefone1 = it },
					telefone2 = telefone2, onTelefone2 = { telefone2 = it },
					comprovanteData = comprovanteData, onComprovanteData = { comprovanteData = it },
					observacoes = observacoes, onObservacoes = { observacoes = it },
					cnhImageName = cnhImage?.fileName,
					identidadeImageName = identidadeImage?.fileName,
					comprovanteImageName = comprovanteImage?.fileName,
					onPickCnh = { cnhImagePicker() },
					onPickIdentidade = { identidadeImagePicker() },
					onPickComprovante = { comprovanteImagePicker() },
					onNext = { step = STEP_REVIEW },
				)

				STEP_REVIEW -> ReviewStep(
					modeloLabel = state.modelos.firstOrNull { it.id == selectedModeloId }.label(),
					planoLabel = state.planos.firstOrNull { it.id == selectedPlanoId }?.nome ?: "—",
					cnhImageName = cnhImage?.fileName,
					identidadeImageName = identidadeImage?.fileName,
					comprovanteImageName = comprovanteImage?.fileName,
					isSubmitting = state.isSubmitting,
					errorMessage = state.submitError,
					onSubmit = {
						viewModel.requestAnalysis(
							request = SolicitarAnaliseRequest(
								cnh = cnh.trim(),
								cnhCategoria = cnhCategoria.trim(),
								cnhUrl = "",
								comprovanteResidenciaUrl = "",
								comprovanteData = comprovanteData.trim(),
								enderecoParente = enderecoParente.trim(),
								telefoneEmergencia1 = telefone1.trim(),
								telefoneEmergencia2 = telefone2.trim(),
								observacoes = observacoes.trim(),
								endereco = endereco.trim(),
								cidade = cidade.trim(),
								estado = estado.trim(),
								cep = cep.trim(),
								planoId = selectedPlanoId.trim(),
								modeloMotoId = selectedModeloId.trim(),
							),
							cnhImage = cnhImage,
							identidadeImage = identidadeImage,
							comprovanteResidenciaImage = comprovanteImage,
						)
					},
				)

				STEP_SUCCESS -> SuccessStep(
					onDone = {
						viewModel.consumeSubmitSuccess()
						step = STEP_VITRINE
					},
				)
			}

			Spacer(modifier = Modifier.height(24.dp))
		}
	}
}

/* --------------------------------- Steps --------------------------------- */

@Composable
private fun VitrineStep(
	authState: AuthUiState,
	state: OnboardingSalesUiState,
	api: BillioneMotosApi,
	onStart: () -> Unit,
) {
	val pending = authState.journeyScreen == "analysis-pending" || authState.journeyState == "WAITING_ANALYSIS"
	val rejected = authState.journeyScreen == "analysis-rejected"

	if (pending || rejected) {
		Card(
			colors = CardDefaults.cardColors(
				containerColor = if (rejected) {
					MaterialTheme.colorScheme.errorContainer
				} else {
					MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
				},
			),
		) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					text = authState.journeyTitle
						?: if (rejected) "Análise reprovada" else "Sua solicitação está em análise",
					style = MaterialTheme.typography.titleMedium,
				)
				Spacer(modifier = Modifier.height(6.dp))
				Text(
					text = authState.journeyMessage
						?: if (rejected) {
							"Você pode revisar os dados e enviar uma nova solicitação."
						} else {
							"Assim que a equipe concluir, seu contrato será liberado. Você pode enviar uma nova solicitação a qualquer momento."
						},
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
	}

	Text(
		text = "Escolha sua moto Billione",
		style = MaterialTheme.typography.headlineSmall,
		fontWeight = FontWeight.Black,
	)
	Text(
		text = "Monte seu plano, envie seus documentos e receba a análise da nossa equipe.",
		style = MaterialTheme.typography.bodyMedium,
		color = MaterialTheme.colorScheme.onSurfaceVariant,
	)

	if (state.errorMessage != null) {
		Text(
			text = state.errorMessage,
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.error,
		)
	}

	SectionTitle("Modelos disponíveis")
	if (state.modelos.isEmpty()) {
		Text(
			text = "Nenhum modelo disponível no momento.",
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	} else {
		LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
			items(state.modelos.size) { index ->
				val modelo = state.modelos[index]
				ModeloShowcaseCard(api = api, modelo = modelo)
			}
		}
	}

	SectionTitle("Planos")
	if (state.planos.isEmpty()) {
		Text(
			text = "Nenhum plano disponível no momento.",
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	} else {
		state.planos.forEach { plano ->
			PlanoRow(plano = plano)
		}
	}

	Spacer(modifier = Modifier.height(4.dp))
	Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
		Text(if (pending || rejected) "Enviar nova solicitação" else "Solicitar análise")
	}
}

@Composable
private fun ChooseMotoStep(
	api: BillioneMotosApi,
	modelos: List<MotoModeloResponse>,
	selectedId: String,
	onSelect: (String) -> Unit,
	onNext: () -> Unit,
) {
	StepHeader(stepNumber = 1, total = 4, title = "Escolha a moto")
	if (modelos.isEmpty()) {
		Text("Nenhum modelo disponível.", color = MaterialTheme.colorScheme.onSurfaceVariant)
	}
	modelos.forEach { modelo ->
		SelectableRow(
			selected = selectedId == modelo.id,
			title = modelo.label(),
			subtitle = listOfNotNull(modelo.marca, modelo.precoInicial?.let { "R$ $it" }).joinToString(" • "),
			onClick = { onSelect(modelo.id.orEmpty()) },
		)
	}
	Button(
		onClick = onNext,
		enabled = selectedId.isNotBlank(),
		modifier = Modifier.fillMaxWidth(),
	) { Text("Continuar") }
}

@Composable
private fun ChoosePlanoStep(
	planos: List<PlanoResponse>,
	selectedId: String,
	onSelect: (String) -> Unit,
	onNext: () -> Unit,
) {
	StepHeader(stepNumber = 2, total = 4, title = "Escolha o plano")
	if (planos.isEmpty()) {
		Text("Nenhum plano disponível.", color = MaterialTheme.colorScheme.onSurfaceVariant)
	}
	planos.forEach { plano ->
		SelectableRow(
			selected = selectedId == plano.id,
			title = plano.nome,
			subtitle = listOfNotNull(plano.nivel, "R$ ${plano.valor}").joinToString(" • "),
			onClick = { onSelect(plano.id) },
		)
	}
	Button(
		onClick = onNext,
		enabled = selectedId.isNotBlank(),
		modifier = Modifier.fillMaxWidth(),
	) { Text("Continuar") }
}

@Composable
private fun DocumentsStep(
	cnh: String, onCnh: (String) -> Unit,
	cnhCategoria: String, onCnhCategoria: (String) -> Unit,
	endereco: String, onEndereco: (String) -> Unit,
	cidade: String, onCidade: (String) -> Unit,
	estado: String, onEstado: (String) -> Unit,
	cep: String, onCep: (String) -> Unit,
	enderecoParente: String, onEnderecoParente: (String) -> Unit,
	telefone1: String, onTelefone1: (String) -> Unit,
	telefone2: String, onTelefone2: (String) -> Unit,
	comprovanteData: String, onComprovanteData: (String) -> Unit,
	observacoes: String, onObservacoes: (String) -> Unit,
	cnhImageName: String?,
	identidadeImageName: String?,
	comprovanteImageName: String?,
	onPickCnh: () -> Unit,
	onPickIdentidade: () -> Unit,
	onPickComprovante: () -> Unit,
	onNext: () -> Unit,
) {
	StepHeader(stepNumber = 3, total = 4, title = "Documentos e dados")

	SectionTitle("Documentos obrigatórios (foto)")
	DocumentPicker("Foto da CNH", cnhImageName, onPickCnh)
	DocumentPicker("Foto da Identidade (RG)", identidadeImageName, onPickIdentidade)
	DocumentPicker("Comprovante de residência", comprovanteImageName, onPickComprovante)

	val allDocs = cnhImageName != null && identidadeImageName != null && comprovanteImageName != null
	if (!allDocs) {
		Text(
			text = "Anexe as três fotos para continuar.",
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.error,
		)
	}

	SectionTitle("Dados pessoais")
	Field(cnh, onCnh, "CNH")
	Field(cnhCategoria, onCnhCategoria, "Categoria da CNH")
	Field(endereco, onEndereco, "Endereço")
	Field(cidade, onCidade, "Cidade")
	Field(estado, onEstado, "Estado")
	Field(cep, onCep, "CEP")
	Field(enderecoParente, onEnderecoParente, "Endereço de um parente")
	Field(telefone1, onTelefone1, "Telefone de emergência 1")
	Field(telefone2, onTelefone2, "Telefone de emergência 2")
	Field(comprovanteData, onComprovanteData, "Data do comprovante (AAAA-MM-DD)")
	Field(observacoes, onObservacoes, "Observações")

	Button(
		onClick = onNext,
		enabled = allDocs,
		modifier = Modifier.fillMaxWidth(),
	) { Text("Revisar") }
}

@Composable
private fun ReviewStep(
	modeloLabel: String,
	planoLabel: String,
	cnhImageName: String?,
	identidadeImageName: String?,
	comprovanteImageName: String?,
	isSubmitting: Boolean,
	errorMessage: String?,
	onSubmit: () -> Unit,
) {
	StepHeader(stepNumber = 4, total = 4, title = "Revisão")
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
			ReviewLine("Moto", modeloLabel)
			ReviewLine("Plano", planoLabel)
			ReviewLine("CNH", cnhImageName ?: "—")
			ReviewLine("Identidade", identidadeImageName ?: "—")
			ReviewLine("Comprovante", comprovanteImageName ?: "—")
		}
	}
	if (errorMessage != null) {
		Text(
			text = errorMessage,
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.error,
		)
	}
	Button(
		onClick = onSubmit,
		enabled = !isSubmitting,
		modifier = Modifier.fillMaxWidth(),
	) {
		if (isSubmitting) {
			CircularProgressIndicator(
				modifier = Modifier.size(18.dp),
				strokeWidth = 2.dp,
				color = MaterialTheme.colorScheme.onPrimary,
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text("Enviando...")
		} else {
			Text("Enviar solicitação")
		}
	}
}

@Composable
private fun SuccessStep(onDone: () -> Unit) {
	Column(
		modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		Icon(
			imageVector = Icons.Filled.CheckCircle,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.primary,
			modifier = Modifier.size(72.dp),
		)
		Text(
			text = "Solicitação enviada!",
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.Black,
		)
		Text(
			text = "Seu pedido está em análise. Assim que aprovado, seu contrato será liberado e a dashboard aparecerá aqui.",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.padding(horizontal = 8.dp),
		)
		Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
			Text("Voltar à vitrine")
		}
	}
}

/* ------------------------------ Components ------------------------------ */

@Composable
private fun OnboardingTopBar(
	userName: String?,
	showBack: Boolean,
	onBack: () -> Unit,
	onLogout: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		if (showBack) {
			TextButton(onClick = onBack) { Text("Voltar") }
		}
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = "Olá, ${userName ?: "cliente"}",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
			)
			Text(
				text = "Vamos escolher sua moto",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
		TextButton(onClick = onLogout) { Text("Sair") }
	}
}

@Composable
private fun SectionTitle(text: String) {
	Text(
		text = text,
		style = MaterialTheme.typography.titleSmall,
		fontWeight = FontWeight.Bold,
		color = MaterialTheme.colorScheme.onSurface,
	)
}

@Composable
private fun StepHeader(stepNumber: Int, total: Int, title: String) {
	Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
		Text(
			text = "Passo $stepNumber de $total",
			style = MaterialTheme.typography.labelMedium,
			color = MaterialTheme.colorScheme.primary,
			fontWeight = FontWeight.Bold,
		)
		Text(
			text = title,
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.Black,
		)
	}
}

@Composable
private fun Field(value: String, onValueChange: (String) -> Unit, label: String) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(label) },
		modifier = Modifier.fillMaxWidth(),
	)
}

@Composable
private fun DocumentPicker(label: String, fileName: String?, onPick: () -> Unit) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		OutlinedButton(onClick = onPick, modifier = Modifier.fillMaxWidth()) {
			Text(if (fileName == null) "Anexar $label" else "Trocar $label")
		}
		Text(
			text = fileName?.let { "Selecionado: $it" } ?: "Nenhum arquivo selecionado",
			style = MaterialTheme.typography.bodySmall,
			color = if (fileName == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
		)
	}
}

@Composable
private fun SelectableRow(
	selected: Boolean,
	title: String,
	subtitle: String,
	onClick: () -> Unit,
) {
	val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.border(if (selected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
			.clickable(onClick = onClick),
		colors = CardDefaults.cardColors(
			containerColor = if (selected) {
				MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
			} else {
				MaterialTheme.colorScheme.surface
			},
		),
	) {
		Row(
			modifier = Modifier.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
				Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
				if (subtitle.isNotBlank()) {
					Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
				}
			}
			if (selected) {
				Icon(
					imageVector = Icons.Filled.CheckCircle,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary,
				)
			}
		}
	}
}

@Composable
private fun PlanoRow(plano: PlanoResponse) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
	) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Text(plano.nome, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
				Text("R$ ${plano.valor}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
			}
			if (!plano.descricao.isNullOrBlank()) {
				Text(plano.descricao, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
			}
		}
	}
}

@Composable
private fun ReviewLine(label: String, value: String) {
	Row(modifier = Modifier.fillMaxWidth()) {
		Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(110.dp))
		Text(value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
	}
}

@Composable
private fun ModeloShowcaseCard(api: BillioneMotosApi, modelo: MotoModeloResponse) {
	Card(
		modifier = Modifier.width(200.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
	) {
		Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
			ModeloImage(api = api, url = modelo.imagemReferenciaUrl ?: modelo.fotoUrls.firstOrNull())
			Text(modelo.label(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
			val subtitle = listOfNotNull(modelo.marca, modelo.precoInicial?.let { "R$ $it" }).joinToString(" • ")
			if (subtitle.isNotBlank()) {
				Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
			}
		}
	}
}

@Composable
private fun ModeloImage(api: BillioneMotosApi, url: String?) {
	var bitmap by remember(url) { mutableStateOf<ImageBitmap?>(null) }
	var failed by remember(url) { mutableStateOf(false) }

	LaunchedEffect(url) {
		bitmap = null
		failed = false
		if (url.isNullOrBlank()) {
			failed = true
			return@LaunchedEffect
		}
		runCatching {
			withContext(Dispatchers.Default) {
				api.fetchRawBytes(url).toImageBitmap()
			}
		}.onSuccess { bitmap = it }.onFailure { failed = true }
	}

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(110.dp)
			.clip(RoundedCornerShape(14.dp))
			.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)),
		contentAlignment = Alignment.Center,
	) {
		val current = bitmap
		when {
			current != null -> Image(
				bitmap = current,
				contentDescription = null,
				contentScale = ContentScale.Fit,
				modifier = Modifier.fillMaxWidth(0.9f),
			)
			failed -> Icon(
				imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary,
				modifier = Modifier.size(40.dp),
			)
			else -> CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
		}
	}
}

private fun MotoModeloResponse?.label(): String {
	if (this == null) return "—"
	return listOfNotNull(nome, modelo).firstOrNull { it.isNotBlank() }
		?: marca?.takeIf { it.isNotBlank() }
		?: id.orEmpty().ifBlank { "Modelo" }
}
