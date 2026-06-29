package com.example.billionemotosappkt.screens.dashboard.customer

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.billionemotosappkt.screens.Loading.LoadingScreen
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.SolicitarAnaliseRequest
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

@Composable
fun CustomerDashboardRoute(
	authState: AuthUiState,
	api: BillioneMotosApi,
	onPlanosClick: () -> Unit = {},
	onLogout: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val viewModel: CustomerDashboardViewModel = viewModel(
		factory = CustomerDashboardViewModel.factory(api, authState),
	)
	val state = viewModel.uiState.collectAsStateWithLifecycle().value
	val dashboardData = state.dashboardData
	val context = LocalContext.current

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
	var analysisModel by rememberSaveable { mutableStateOf("") }

	val avatarPicker = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.GetContent(),
	) { uri ->
		if (uri != null) {
			context.toUploadFileRequest(uri)?.let { viewModel.uploadAvatar(it, descricao = "Foto de perfil") }
		}
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
			analysisModel = state.availableModels.firstOrNull()?.modelo.orEmpty()
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
			onPickAvatar = { avatarPicker.launch("image/*") },
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
			model = analysisModel,
			isSubmitting = state.isSubmittingAnalysis,
			availablePlans = state.availablePlans,
			availableModels = state.availableModels,
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
			onModelChange = { analysisModel = it },
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
						modelo = analysisModel.trim(),
					),
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
					text = "Preencha os dados para enviar a solicitação autenticada para a API.",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
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
				OutlinedTextField(value = model, onValueChange = onModelChange, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
				if (availablePlans.isNotEmpty()) {
					Text(
						text = "Planos disponíveis: ${availablePlans.joinToString { it.nome }}",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
				if (availableModels.isNotEmpty()) {
					Text(
						text = "Modelos disponíveis: ${availableModels.take(5).joinToString { it.modelo }}",
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

private fun Context.toUploadFileRequest(uri: Uri): UploadFileRequest? {
	val contentResolver = contentResolver
	val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
	val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
	val fileName = uri.lastPathSegment?.substringAfterLast('/')?.takeIf { it.isNotBlank() }
		?: "upload-${System.currentTimeMillis()}"
	return UploadFileRequest(
		bytes = bytes,
		fileName = fileName,
		contentType = mimeType,
	)
}
