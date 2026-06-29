package com.example.billionemotosappkt.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
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
fun FormularioCadastro(
	submit: (String) -> Unit,
	onMotosClick: () -> Unit = {},
	onPlanosClick: () -> Unit = {},
	onOndeEstamosClick: () -> Unit = {},
) {
	var cpfInput by remember { mutableStateOf("") }
	var submitAttempted by remember { mutableStateOf(false) }

	val cpfError = when {
		!submitAttempted && cpfInput.isBlank() -> null
		cpfInput.isBlank() -> "Informe seu CPF"
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
		FormEtapaUm(
			value = cpfInput,
			errorMessage = cpfError,
			submit = {
				submitAttempted = true
				if (isValidCpf(cpfInput)) {
					submit(cpfInput)
				}
			},
			onMotosClick = onMotosClick,
			onPlanosClick = onPlanosClick,
			onOndeEstamosClick = onOndeEstamosClick,
			onValueChange = { value ->
				cpfInput = value
				submitAttempted = false
			},
		)
	}
}

@Composable
private fun FormEtapaUm(
	value: String,
	errorMessage: String?,
	submit: () -> Unit,
	onValueChange: (String) -> Unit,
	onMotosClick: () -> Unit,
	onPlanosClick: () -> Unit,
	onOndeEstamosClick: () -> Unit,
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
		text = "Para iniciar o cadastro, insira seu CPF",
		color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f),
		fontSize = 14.sp,
		lineHeight = 36.sp,
		fontWeight = FontWeight.Bold
	)
	
	AuthInputField(
		value = value,
		onValueChange = onValueChange,
		label = "Seu CPF",
		placeholder = "999.999.999-99",
		visualTransformation = CpfVisualTransformation,
		keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
		errorMessage = errorMessage,
	)
	ActionButton(
		text = "CONTINUAR",
		onClick = submit,
		containerColor = MaterialTheme.colorScheme.primary,
		pressedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.88f),
		contentColor = MaterialTheme.colorScheme.onPrimary,
		borderColor = MaterialTheme.colorScheme.primary,
		pressedBorderColor = MaterialTheme.colorScheme.primary,
		glowPulse = false,
		pulseScaleStart = 1.03f,
		pulseScaleEnd = 0.992f,
		pulseGlowStart = 1f,
		pulseGlowEnd = 0.5f,
		pulseElevationStart = 58f,
		pulseElevationEnd = 24f,
	)
	AuthQuickActions(
		modifier = Modifier.fillMaxWidth(),
		onMotosClick = onMotosClick,
		onPlanosClick = onPlanosClick,
		onOndeEstamosClick = onOndeEstamosClick,
	)
	Spacer(modifier = Modifier.size(16.dp))
}
