package com.example.billionemotosappkt.screens.cadastro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.billionemotosappkt.components.ActionButton
import com.example.billionemotosappkt.components.auth.AuthInputField
import com.example.billionemotosappkt.components.auth.PhoneVisualTransformation
import com.example.billionemotosappkt.components.auth.onlyDigits
import com.example.billionemotosappkt.shared.api.AppRole
import com.example.billionemotosappkt.shared.api.CreateUserRequest
import com.example.billionemotosappkt.shared.api.UserKind
import com.example.billionemotosappkt.shared.api.UserStatus
import com.example.billionemotosappkt.shared.utils.BackHandler
import com.example.billionemotosappkt.screens.auth.AuthViewModel
import com.example.billionemotosappkt.ui.theme.AppDimens
import com.example.billionemotosappkt.utils.highlightAsterisks

@Composable
fun CadastroScreen(
	cpf: String,
	viewModel: AuthViewModel,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
	var nome by remember { mutableStateOf("") }
	var email by remember { mutableStateOf("") }
	var senha by remember { mutableStateOf("") }
	var telefone by remember { mutableStateOf("") }
	var errorMessage by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(uiState.registrationSuccess) {
		if (uiState.registrationSuccess) {
			viewModel.clearRegistrationSuccess()
			onBack()
		}
	}

	BackHandler(onBack = onBack)

	Box(
		modifier = modifier
			.fillMaxSize()
			.safeDrawingPadding()
			.background(MaterialTheme.colorScheme.surface),
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(
					horizontal = AppDimens.ScreenPadding,
					vertical = AppDimens.SectionPadding,
				)
				.imePadding()
				.verticalScroll(rememberScrollState()),
			verticalArrangement = Arrangement.spacedBy(AppDimens.ButtonGroupSpacing),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
			) {
				IconButton(onClick = onBack) {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowBack,
						contentDescription = "Voltar",
					)
				}
				Box(
					modifier = Modifier.fillMaxWidth(),
					contentAlignment = Alignment.Center,
				) {
					Text(
						text = "CADASTRO",
						fontSize = 18.sp,
						fontWeight = FontWeight.Black,
					)
				}
			}

			Text(
				text = highlightAsterisks(
					text = "BEM-VINDO A *BILLIONE MOTO*.",
					baseColor = MaterialTheme.colorScheme.onSurface,
					highlightColor = MaterialTheme.colorScheme.primary,
				),
				fontSize = 30.sp,
				lineHeight = 34.sp,
				fontWeight = FontWeight.Black,
			)
			Text(
				text = "CPF validado. Complete seus dados para continuar.",
				color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
				fontSize = 14.sp,
			)

			Text(
				text = "Seu CPF",
				fontSize = 16.sp,
				fontWeight = FontWeight.SemiBold,
			)
			Text(
				text = cpf,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier
					.fillMaxWidth()
					.background(
						color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f),
						shape = RoundedCornerShape(14.dp),
					)
					.padding(horizontal = 16.dp, vertical = 18.dp),
			)

			AuthInputField(
				value = nome,
				onValueChange = { nome = it },
				label = "Nome completo",
				placeholder = "Nome completo",
			)
			AuthInputField(
				value = email,
				onValueChange = { email = it },
				label = "Email",
				placeholder = "seuemail@exemplo.com",
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
			)
			AuthInputField(
				value = senha,
				onValueChange = { senha = it },
				label = "Senha",
				placeholder = "Crie uma senha",
				visualTransformation = PasswordVisualTransformation(),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
			)
			AuthInputField(
				value = telefone,
				onValueChange = { telefone = it },
				label = "Telefone",
				placeholder = "(XX) XXXXX-XXXX",
				visualTransformation = PhoneVisualTransformation,
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			)

			errorMessage?.let {
				Text(
					text = it,
					color = MaterialTheme.colorScheme.error,
					fontSize = 14.sp,
				)
			}
			
			ActionButton(
				text = if (uiState.isSubmitting) "CADASTRANDO..." else "CONTINUAR",
				onClick = {
					val cleanCpf = onlyDigits(cpf)
					errorMessage = when {
						nome.isBlank() -> "Informe seu nome."
						email.isBlank() -> "Informe seu email."
						senha.length < 6 -> "A senha precisa ter ao menos 6 caracteres."
						telefone.isBlank() -> "Informe seu telefone."
						else -> null
					}
					if (errorMessage == null) {
						viewModel.onFormChange(
							CreateUserRequest(
								email = email.trim(),
								cpf = cleanCpf,
								password = senha,
								nome = nome.trim(),
								telefone = onlyDigits(telefone).takeIf { it.isNotBlank() },
								kind = UserKind.CLIENT,
								status = UserStatus.ACTIVE,
								roles = listOf(AppRole.CLIENTE),
							),
						)
						viewModel.registerUser()
					}
				},
				containerColor = MaterialTheme.colorScheme.primary,
				pressedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.88f),
				contentColor = MaterialTheme.colorScheme.onPrimary,
				borderColor = MaterialTheme.colorScheme.primary,
				pressedBorderColor = MaterialTheme.colorScheme.primary,
				glowPulse = false,
		)
			if (uiState.errorMessage != null) {
				Text(
					text = uiState.errorMessage.orEmpty(),
					color = MaterialTheme.colorScheme.error,
					fontSize = 14.sp,
				)
			}

			Spacer(modifier = Modifier.size(8.dp))
		}
	}
}
