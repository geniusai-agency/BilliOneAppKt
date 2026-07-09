package com.example.billionemotosappkt.screens.dashboard.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.billionemotosappkt.screens.Loading.LoadingScreen
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.SolicitarAnaliseRequest
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.utils.rememberImagePicker

@Composable
fun CustomerDashboardRoute(
	authState: AuthUiState,
	api: BillioneMotosApi,
	onPlanosClick: () -> Unit = {},
	onLogout: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val viewModel = remember(api, authState) { CustomerDashboardViewModel(api, authState) }
	val state = viewModel.uiState.collectAsStateWithLifecycle().value
	val dashboardData = state.dashboardData

	var showProfileDialog by rememberSaveable { mutableStateOf(false) }
	var showAnalysisDialog by rememberSaveable { mutableStateOf(false) }

	var profileName by rememberSaveable { mutableStateOf("") }
	var profilePhone by rememberSaveable { mutableStateOf("") }
	var profileBio by rememberSaveable { mutableStateOf("") }
	var profileTimezone by rememberSaveable { mutableStateOf("") }
	var profileLanguage by rememberSaveable { mutableStateOf("") }

	var analysisCnh by rememberSaveable { mutableStateOf("") }
	var analysisCnhCategory by rememberSaveable { mutableStateOf("") }
	var analysisCnhUrl by rememberSaveable { mutableStateOf("") }
	var analysisResidencyUrl by rememberSaveable { mutableStateOf("") }
	var analysisDate by rememberSaveable { mutableStateOf("") }
	var analysisParentAddress by rememberSaveable { mutableStateOf("") }
	var analysisEmergency1 by rememberSaveable { mutableStateOf("") }
	var analysisEmergency2 by rememberSaveable { mutableStateOf("") }
	var analysisNotes by rememberSaveable { mutableStateOf("") }
	var analysisAddress by rememberSaveable { mutableStateOf("") }
	var analysisCity by rememberSaveable { mutableStateOf("") }
	var analysisState by rememberSaveable { mutableStateOf("") }
	var analysisCep by rememberSaveable { mutableStateOf("") }
	var analysisPlanId by rememberSaveable { mutableStateOf("") }
	var analysisModelId by rememberSaveable { mutableStateOf("") }

	// Documentos essenciais enviados como arquivo (foto): CNH, Identidade e Comprovante de Residência.
	var cnhImage by remember { mutableStateOf<UploadFileRequest?>(null) }
	var identidadeImage by remember { mutableStateOf<UploadFileRequest?>(null) }
	var comprovanteImage by remember { mutableStateOf<UploadFileRequest?>(null) }

	val avatarPicker = rememberImagePicker { fileRequest ->
		if (fileRequest != null) {
			viewModel.uploadAvatar(fileRequest, descricao = "Foto de perfil")
		}
	}

	val cnhImagePicker = rememberImagePicker { fileRequest ->
		if (fileRequest != null) cnhImage = fileRequest
	}
	val identidadeImagePicker = rememberImagePicker { fileRequest ->
		if (fileRequest != null) identidadeImage = fileRequest
	}
	val comprovanteImagePicker = rememberImagePicker { fileRequest ->
		if (fileRequest != null) comprovanteImage = fileRequest
	}

	LaunchedEffect(showProfileDialog, state.user?.id) {
		if (showProfileDialog) {
			profileName = state.user?.nome ?: authState.userName.orEmpty()
			profilePhone = state.user?.telefone.orEmpty()
			profileBio = state.user?.profile?.bio.orEmpty()
			profileTimezone = state.user?.profile?.timezone.orEmpty()
			profileLanguage = state.user?.profile?.language.orEmpty()
		}
	}

	LaunchedEffect(showAnalysisDialog, state.user?.id, state.availablePlans, state.availableModels) {
		if (showAnalysisDialog) {
			analysisCnh = ""
			analysisCnhCategory = ""
			analysisCnhUrl = ""
			analysisResidencyUrl = ""
			analysisDate = ""
			analysisParentAddress = ""
			analysisEmergency1 = ""
			analysisEmergency2 = ""
			analysisNotes = ""
			analysisAddress = ""
			analysisCity = ""
			analysisState = ""
			analysisCep = ""
			analysisPlanId = state.availablePlans.firstOrNull()?.id.orEmpty()
			analysisModelId = state.availableModels.firstOrNull()?.id.orEmpty()
			cnhImage = null
			identidadeImage = null
			comprovanteImage = null
		}
	}

	val recommendedPlanName = state.availablePlans.firstOrNull()?.nome
	val recommendedPlanValue = state.availablePlans.firstOrNull()?.valor

	if (state.isLoading && dashboardData == null) {
		LoadingScreen(modifier = modifier)
		return
	}

	if (dashboardData == null) {
		CustomerDashboardErrorCard(
			message = state.errorMessage ?: "Nao foi possivel carregar seus dados.",
			recommendedPlanName = recommendedPlanName,
			onPrimaryAction = onPlanosClick,
			onRetry = viewModel::refresh,
			modifier = modifier,
		)
		return
	}

	val showPlanAlert = authState.clienteStatus == ClienteAprovacaoStatus.EM_ANALISE ||
		authState.journeyScreen == "analysis-pending" ||
		authState.journeyState == "WAITING_ANALYSIS"

	CustomerDashboardScreen(
		authState = authState,
		dashboardData = dashboardData,
		displayUserName = state.user?.nome ?: authState.userName,
		displayUserEmail = state.user?.email ?: authState.userEmail,
		displayAvatarUrl = state.user?.avatarUrl ?: authState.avatarUrl,
		showPlanAlert = showPlanAlert,
		recommendedPlanName = recommendedPlanName,
		recommendedPlanValue = recommendedPlanValue,
		onPlanosClick = onPlanosClick,
		onEditProfile = { showProfileDialog = true },
		onRequestAnalysis = { showAnalysisDialog = true },
		onLogout = onLogout,
		api = api,
		modifier = modifier,
	)

	if (showProfileDialog) {
		ProfileEditorDialog(
			name = profileName,
			phone = profilePhone,
			bio = profileBio,
			timezone = profileTimezone,
			language = profileLanguage,
			isSaving = state.isSavingProfile,
			isUploading = state.isUploadingAvatar,
			onNameChange = { profileName = it },
			onPhoneChange = { profilePhone = it },
			onBioChange = { profileBio = it },
			onTimezoneChange = { profileTimezone = it },
			onLanguageChange = { profileLanguage = it },
			onPickAvatar = { avatarPicker() },
			onSave = {
				viewModel.saveProfile(
					nome = profileName.trim(),
					telefone = profilePhone.takeIf { it.isNotBlank() }?.trim(),
					bio = profileBio.takeIf { it.isNotBlank() }?.trim(),
					timezone = profileTimezone.takeIf { it.isNotBlank() }?.trim(),
					language = profileLanguage.takeIf { it.isNotBlank() }?.trim(),
				)
				showProfileDialog = false
			},
			onDismiss = { showProfileDialog = false },
		)
	}

	if (showAnalysisDialog) {
		RequestAnalysisDialog(
			cnh = analysisCnh,
			cnhCategory = analysisCnhCategory,
			cnhUrl = analysisCnhUrl,
			residencyUrl = analysisResidencyUrl,
			date = analysisDate,
			parentAddress = analysisParentAddress,
			emergency1 = analysisEmergency1,
			emergency2 = analysisEmergency2,
			notes = analysisNotes,
			address = analysisAddress,
			city = analysisCity,
			stateValue = analysisState,
			cep = analysisCep,
			planId = analysisPlanId,
			model = analysisModelId,
			isSubmitting = state.isSubmittingAnalysis,
			availablePlans = state.availablePlans,
			availableModels = state.availableModels,
			cnhImageName = cnhImage?.fileName,
			identidadeImageName = identidadeImage?.fileName,
			comprovanteImageName = comprovanteImage?.fileName,
			onPickCnhImage = { cnhImagePicker() },
			onPickIdentidadeImage = { identidadeImagePicker() },
			onPickComprovanteImage = { comprovanteImagePicker() },
			onCnhChange = { analysisCnh = it },
			onCnhCategoryChange = { analysisCnhCategory = it },
			onCnhUrlChange = { analysisCnhUrl = it },
			onResidencyUrlChange = { analysisResidencyUrl = it },
			onDateChange = { analysisDate = it },
			onParentAddressChange = { analysisParentAddress = it },
			onEmergency1Change = { analysisEmergency1 = it },
			onEmergency2Change = { analysisEmergency2 = it },
			onNotesChange = { analysisNotes = it },
			onAddressChange = { analysisAddress = it },
			onCityChange = { analysisCity = it },
			onStateChange = { analysisState = it },
			onCepChange = { analysisCep = it },
			onPlanIdChange = { analysisPlanId = it },
			onModelChange = { analysisModelId = it },
			onSubmit = {
				viewModel.requestAnalysis(
					request = SolicitarAnaliseRequest(
						cnh = analysisCnh.trim(),
						cnhCategoria = analysisCnhCategory.trim(),
						cnhUrl = analysisCnhUrl.trim(),
						comprovanteResidenciaUrl = analysisResidencyUrl.trim(),
						comprovanteData = analysisDate.trim(),
						enderecoParente = analysisParentAddress.trim(),
						telefoneEmergencia1 = analysisEmergency1.trim(),
						telefoneEmergencia2 = analysisEmergency2.trim(),
						observacoes = analysisNotes.trim(),
						endereco = analysisAddress.trim(),
						cidade = analysisCity.trim(),
						estado = analysisState.trim(),
						cep = analysisCep.trim(),
						planoId = analysisPlanId.trim(),
						modeloMotoId = analysisModelId.trim(),
					),
					cnhImage = cnhImage,
					identidadeImage = identidadeImage,
					comprovanteResidenciaImage = comprovanteImage,
				)
				showAnalysisDialog = false
			},
			onDismiss = { showAnalysisDialog = false },
		)
	}
}

@Composable
	private fun CustomerDashboardErrorCard(
	message: String,
	recommendedPlanName: String?,
	onPrimaryAction: () -> Unit,
	onRetry: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(modifier = modifier.fillMaxWidth()) {
		Column(
			modifier = Modifier.padding(24.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Text(
				text = "Não foi possível carregar sua dashboard",
				style = MaterialTheme.typography.titleMedium,
			)
			Text(
				text = message,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			if (!recommendedPlanName.isNullOrBlank()) {
				Text(
					text = "Sugestao: $recommendedPlanName",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
			Button(onClick = onPrimaryAction) {
				Text("Escolher plano")
			}
			Button(onClick = onRetry) {
				Text("Tentar novamente")
			}
		}
	}
}

@Composable
private fun ProfileEditorDialog(
	name: String,
	phone: String,
	bio: String,
	timezone: String,
	language: String,
	isSaving: Boolean,
	isUploading: Boolean,
	onNameChange: (String) -> Unit,
	onPhoneChange: (String) -> Unit,
	onBioChange: (String) -> Unit,
	onTimezoneChange: (String) -> Unit,
	onLanguageChange: (String) -> Unit,
	onPickAvatar: () -> Unit,
	onSave: () -> Unit,
	onDismiss: () -> Unit,
) {
	Dialog(onDismissRequest = onDismiss) {
		Card(
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surface,
			),
		) {
			Column(
				modifier = Modifier
					.widthIn(max = 520.dp)
					.padding(20.dp)
					.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.spacedBy(12.dp),
			) {
				Text("Editar perfil", style = MaterialTheme.typography.titleLarge)
				OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Telefone") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = bio, onValueChange = onBioChange, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = timezone, onValueChange = onTimezoneChange, label = { Text("Timezone") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = language, onValueChange = onLanguageChange, label = { Text("Idioma") }, modifier = Modifier.fillMaxWidth())
				TextButton(onClick = onPickAvatar, enabled = !isUploading) {
					Text(if (isUploading) "Enviando imagem..." else "Mudar imagem")
				}
				Button(onClick = onSave, enabled = !isSaving) {
					Text(if (isSaving) "Salvando..." else "Salvar")
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RequestAnalysisDialog(
	cnh: String,
	cnhCategory: String,
	cnhUrl: String,
	residencyUrl: String,
	date: String,
	parentAddress: String,
	emergency1: String,
	emergency2: String,
	notes: String,
	address: String,
	city: String,
	stateValue: String,
	cep: String,
	planId: String,
	model: String,
	isSubmitting: Boolean,
	availablePlans: List<com.example.billionemotosappkt.shared.api.PlanoResponse>,
	availableModels: List<com.example.billionemotosappkt.shared.api.MotoModeloResponse>,
	cnhImageName: String?,
	identidadeImageName: String?,
	comprovanteImageName: String?,
	onPickCnhImage: () -> Unit,
	onPickIdentidadeImage: () -> Unit,
	onPickComprovanteImage: () -> Unit,
	onCnhChange: (String) -> Unit,
	onCnhCategoryChange: (String) -> Unit,
	onCnhUrlChange: (String) -> Unit,
	onResidencyUrlChange: (String) -> Unit,
	onDateChange: (String) -> Unit,
	onParentAddressChange: (String) -> Unit,
	onEmergency1Change: (String) -> Unit,
	onEmergency2Change: (String) -> Unit,
	onNotesChange: (String) -> Unit,
	onAddressChange: (String) -> Unit,
	onCityChange: (String) -> Unit,
	onStateChange: (String) -> Unit,
	onCepChange: (String) -> Unit,
	onPlanIdChange: (String) -> Unit,
	onModelChange: (String) -> Unit,
	onSubmit: () -> Unit,
	onDismiss: () -> Unit,
) {
	val selectedModel = availableModels.firstOrNull {
		it.id == model || it.nome == model || it.modelo == model
	}
	val selectedModelLabel = selectedModel?.let {
		listOfNotNull(it.nome, it.modelo, it.marca)
			.filter { value -> value.isNotBlank() }
			.joinToString(" • ")
			.ifBlank { it.id.orEmpty() }
	}.orEmpty().ifBlank { "Selecione um modelo" }
	var modelMenuExpanded by rememberSaveable { mutableStateOf(false) }

	Dialog(onDismissRequest = onDismiss) {
		Card(
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surface,
			),
		) {
			Column(
				modifier = Modifier
					.widthIn(max = 560.dp)
					.padding(20.dp)
					.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.spacedBy(12.dp),
			) {
				Text("Pedir análise", style = MaterialTheme.typography.titleLarge)
				Text(
					text = "Preencha os dados e anexe as fotos dos documentos para enviar a solicitação.",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)

				Text(
					text = "Documentos obrigatórios (foto)",
					style = MaterialTheme.typography.titleSmall,
					color = MaterialTheme.colorScheme.onSurface,
				)
				DocumentPickerRow(
					label = "Foto da CNH",
					fileName = cnhImageName,
					onPick = onPickCnhImage,
				)
				DocumentPickerRow(
					label = "Foto da Identidade (RG)",
					fileName = identidadeImageName,
					onPick = onPickIdentidadeImage,
				)
				DocumentPickerRow(
					label = "Comprovante de residência",
					fileName = comprovanteImageName,
					onPick = onPickComprovanteImage,
				)

				OutlinedTextField(value = cnh, onValueChange = onCnhChange, label = { Text("CNH") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = cnhCategory, onValueChange = onCnhCategoryChange, label = { Text("Categoria da CNH") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = cnhUrl, onValueChange = onCnhUrlChange, label = { Text("URL da CNH") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = residencyUrl, onValueChange = onResidencyUrlChange, label = { Text("URL comprovante residência") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = date, onValueChange = onDateChange, label = { Text("Data do comprovante") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = parentAddress, onValueChange = onParentAddressChange, label = { Text("Endereço do parente") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = emergency1, onValueChange = onEmergency1Change, label = { Text("Telefone emergência 1") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = emergency2, onValueChange = onEmergency2Change, label = { Text("Telefone emergência 2") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = notes, onValueChange = onNotesChange, label = { Text("Observações") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = address, onValueChange = onAddressChange, label = { Text("Endereço") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = city, onValueChange = onCityChange, label = { Text("Cidade") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = stateValue, onValueChange = onStateChange, label = { Text("Estado") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = cep, onValueChange = onCepChange, label = { Text("CEP") }, modifier = Modifier.fillMaxWidth())
				OutlinedTextField(value = planId, onValueChange = onPlanIdChange, label = { Text("Plano ID") }, modifier = Modifier.fillMaxWidth())
				ExposedDropdownMenuBox(
					expanded = modelMenuExpanded,
					onExpandedChange = { modelMenuExpanded = !modelMenuExpanded },
				) {
					OutlinedTextField(
						value = selectedModelLabel,
						onValueChange = {},
						readOnly = true,
						label = { Text("Modelo") },
						trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelMenuExpanded) },
						modifier = Modifier.fillMaxWidth(),
						colors = OutlinedTextFieldDefaults.colors(),
					)
					ExposedDropdownMenu(
						expanded = modelMenuExpanded,
						onDismissRequest = { modelMenuExpanded = false },
					) {
						availableModels.forEach { motoModel ->
							val label = listOfNotNull(motoModel.nome, motoModel.modelo, motoModel.marca)
								.filter { it.isNotBlank() }
								.joinToString(" • ")
								.ifBlank { motoModel.id.orEmpty() }
							DropdownMenuItem(
								text = { Text(label) },
								onClick = {
									onModelChange(motoModel.id.orEmpty())
									modelMenuExpanded = false
								},
							)
						}
					}
				}
				if (availablePlans.isNotEmpty()) {
					Text(
						text = "Planos disponíveis: ${availablePlans.joinToString { it.nome }}",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
				if (availableModels.isNotEmpty()) {
					Text(
						text = "Modelos disponíveis: ${availableModels.take(5).joinToString { it.nome ?: it.modelo ?: it.id.orEmpty() }}",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
				Button(onClick = onSubmit, enabled = !isSubmitting) {
					Text(if (isSubmitting) "Enviando..." else "Enviar análise")
				}
			}
		}
	}
}

@Composable
private fun DocumentPickerRow(
	label: String,
	fileName: String?,
	onPick: () -> Unit,
) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		Button(onClick = onPick, modifier = Modifier.fillMaxWidth()) {
			Text(if (fileName == null) "Anexar $label" else "Trocar $label")
		}
		Text(
			text = fileName?.let { "Selecionado: $it" } ?: "Nenhum arquivo selecionado",
			style = MaterialTheme.typography.bodySmall,
			color = if (fileName == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
		)
	}
}


