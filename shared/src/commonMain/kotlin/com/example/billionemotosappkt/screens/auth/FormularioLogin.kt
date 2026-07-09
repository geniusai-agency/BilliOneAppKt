package com.example.billionemotosappkt.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.components.ActionButton
import com.example.billionemotosappkt.components.auth.AuthInputField
import com.example.billionemotosappkt.components.auth.AuthQuickActions
import com.example.billionemotosappkt.components.auth.CpfVisualTransformation
import com.example.billionemotosappkt.components.auth.isValidCpf
import com.example.billionemotosappkt.ui.theme.AppDimens
import com.example.billionemotosappkt.utils.highlightAsterisks

@Composable
fun FormularioLogin(
	isBusy: Boolean = false,
	errorMessage: String? = null,
	onLogin: (cpf: String, password: String) -> Unit,
	onMotosClick: () -> Unit = {},
	onPlanosClick: () -> Unit = {},
	onOndeEstamosClick: () -> Unit = {},
) {
	var cpfInput by remember { mutableStateOf("") }
	var passwordInput by remember { mutableStateOf("") }
	var passwordVisible by remember { mutableStateOf(false) }

	val cpfError = when {
		cpfInput.isBlank() -> null
		isValidCpf(cpfInput) -> null
		else -> "CPF inválido"
	}
	
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(
				horizontal = AppDimens.ScreenPadding,
				vertical = AppDimens.BottomSheetPadding,
			)
			.navigationBarsPadding(),
		verticalArrangement = Arrangement.spacedBy(AppDimens.ButtonGroupSpacing),
	) {
		Text(
			text = highlightAsterisks(
				text = "BEM-VINDO A *BILLIONE MOTO*",
				baseColor = MaterialTheme.colorScheme.onSurface,
				highlightColor = MaterialTheme.colorScheme.primary,
			),
			fontSize = 22.sp,
			lineHeight = 36.sp,
			fontWeight = FontWeight.Black
		)
		Text(
			text = "Para entrar, informe seu CPF e sua senha",
			color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f),
			fontSize = 14.sp,
			lineHeight = 36.sp,
			fontWeight = FontWeight.Bold
		)
		
		AuthInputField(
			value = cpfInput,
			onValueChange = { cpfInput = it },
			label = "Seu CPF",
			placeholder = "999.999.999-99",
			visualTransformation = CpfVisualTransformation,
			keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
			errorMessage = cpfError,
		)
		
		AuthInputField(
			value = passwordInput,
			onValueChange = { passwordInput = it },
			label = "Sua senha",
			placeholder = "Digite sua senha",
			visualTransformation = if (passwordVisible) {
				androidx.compose.ui.text.input.VisualTransformation.None
			} else {
				PasswordVisualTransformation()
			},
			keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
			trailingIcon = {
				IconButton(onClick = { passwordVisible = !passwordVisible }) {
					Icon(
						imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
						contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
					)
				}
			},
		)
		errorMessage?.let {
			Text(text = it, color = MaterialTheme.colorScheme.error)
		}
		ActionButton(
			text = if (isBusy) "ENTRANDO..." else "CONTINUAR",
			onClick = {
				if (cpfError == null && passwordInput.isNotBlank() && !isBusy) {
					onLogin(cpfInput, passwordInput)
				}
			},
			containerColor = MaterialTheme.colorScheme.primary,
			pressedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.88f),
			contentColor = MaterialTheme.colorScheme.onPrimary,
			borderColor = MaterialTheme.colorScheme.primary,
			pressedBorderColor = MaterialTheme.colorScheme.primary,
			glowPulse = false,
		)
		AuthQuickActions(
			modifier = Modifier.fillMaxWidth(),
			onMotosClick = onMotosClick,
			onPlanosClick = onPlanosClick,
			onOndeEstamosClick = onOndeEstamosClick,
		)
		Spacer(modifier = Modifier.size(16.dp))
	}
}
