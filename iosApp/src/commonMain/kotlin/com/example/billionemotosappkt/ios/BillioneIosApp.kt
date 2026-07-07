package com.example.billionemotosappkt.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BillioneIosApp() {
	MaterialTheme(
		colorScheme = androidx.compose.material3.darkColorScheme(
			primary = Color(0xFF20E65B),
			onPrimary = Color.Black,
			surface = Color(0xFF0E1110),
			surfaceVariant = Color(0xFF171B19),
			onSurface = Color.White,
			onSurfaceVariant = Color(0xFFB8BFBA),
			outline = Color(0xFF3A443D),
		),
	) {
		Surface(modifier = Modifier.fillMaxSize()) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(
						Brush.verticalGradient(
							colors = listOf(
								Color(0xFF050806),
								Color(0xFF0A120D),
								Color(0xFF050806),
							),
						),
					),
			) {
				IosLoginScreen()
			}
		}
	}
}

@Composable
private fun IosLoginScreen() {
	var cpf by remember { mutableStateOf("") }
	var password by remember { mutableStateOf("") }
	var passwordVisible by remember { mutableStateOf(false) }
	var errorMessage by remember { mutableStateOf<String?>(null) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			// .verticalScroll(rememberScrollState())
			.padding(horizontal = 20.dp, vertical = 24.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		Text(
			text = "BILLIONE MOTO",
			color = Color(0xFF20E65B),
			fontWeight = FontWeight.Black,
			fontSize = 18.sp,
		)

		Text(
			text = buildAnnotatedString {
				append("BEM-VINDO A ")
				withStyle(
					SpanStyle(
						color = Color(0xFF20E65B),
						fontWeight = FontWeight.Black,
					),
				) {
					append("BILLIONE MOTO")
				}
			},
			fontWeight = FontWeight.Black,
			fontSize = 34.sp,
			lineHeight = 36.sp,
		)

		Text(
			text = "Para entrar, informe seu CPF e sua senha.",
			color = Color.White.copy(alpha = 0.72f),
			fontSize = 14.sp,
		)

		AuthField(
			value = cpf,
			onValueChange = {
				cpf = it
				errorMessage = null
			},
			label = "Seu CPF",
			placeholder = "999.999.999-99",
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			visualTransformation = CpfVisualTransformation,
		)

		AuthField(
			value = password,
			onValueChange = {
				password = it
				errorMessage = null
			},
			label = "Sua senha",
			placeholder = "Digite sua senha",
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
			visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
			Text(text = it, color = Color(0xFFFF6B6B))
		}

		Button(
			onClick = {
				errorMessage = when {
					!isValidCpf(cpf) -> "CPF inválido"
					password.length < 6 -> "Senha deve ter ao menos 6 caracteres"
					else -> null
				}
			},
			modifier = Modifier
				.fillMaxWidth()
				.height(56.dp),
			shape = RoundedCornerShape(16.dp),
			colors = ButtonDefaults.buttonColors(
				containerColor = Color(0xFF20E65B),
				contentColor = Color.Black,
			),
		) {
			Text(text = "CONTINUAR", fontWeight = FontWeight.Bold)
		}

		Spacer(modifier = Modifier.height(10.dp))

		Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
			QuickActionChip(title = "Nossas motos")
			QuickActionChip(title = "Nossos planos")
			QuickActionChip(title = "Onde estamos")
		}
	}
}

@Composable
private fun AuthField(
	value: String,
	onValueChange: (String) -> Unit,
	label: String,
	placeholder: String,
	keyboardOptions: KeyboardOptions,
	visualTransformation: VisualTransformation = VisualTransformation.None,
	trailingIcon: (@Composable () -> Unit)? = null,
) {
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
		OutlinedTextField(
			value = value,
			onValueChange = onValueChange,
			modifier = Modifier.fillMaxWidth(),
			singleLine = true,
			shape = RoundedCornerShape(14.dp),
			placeholder = { Text(text = placeholder) },
			visualTransformation = visualTransformation,
			keyboardOptions = keyboardOptions,
			trailingIcon = trailingIcon,
		)
	}
}

@Composable
private fun QuickActionChip(title: String) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				color = Color.White.copy(alpha = 0.06f),
				shape = RoundedCornerShape(16.dp),
			)
			.padding(horizontal = 16.dp, vertical = 16.dp),
	) {
		Text(text = title, color = Color.White)
	}
}

private fun onlyDigits(value: String): String = value.filter(Char::isDigit)

fun isValidCpf(value: String): Boolean {
	val digits = onlyDigits(value)
	if (digits.length != 11) return false
	if (digits.all { it == digits.first() }) return false

	fun calculateDigit(length: Int): Int {
		var sum = 0
		var weight = length + 1
		for (index in 0 until length) {
			sum += (digits[index].code - '0'.code) * weight
			weight--
		}
		val remainder = (sum * 10) % 11
		return if (remainder == 10) 0 else remainder
	}

	val firstDigit = calculateDigit(9)
	val secondDigit = calculateDigit(10)
	return digits[9].digitToInt() == firstDigit && digits[10].digitToInt() == secondDigit
}

object CpfVisualTransformation : VisualTransformation {
	override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
		val digits = onlyDigits(text.text).take(11)
		val formatted = buildString {
			digits.forEachIndexed { index, char ->
				if (index == 3 || index == 6) append('.')
				if (index == 9) append('-')
				append(char)
			}
		}

		return androidx.compose.ui.text.input.TransformedText(
			androidx.compose.ui.text.AnnotatedString(formatted),
			object : androidx.compose.ui.text.input.OffsetMapping {
				override fun originalToTransformed(offset: Int): Int {
					val separators = when {
						offset <= 3 -> 0
						offset <= 6 -> 1
						offset <= 9 -> 2
						else -> 3
					}
					return (offset + separators).coerceAtMost(formatted.length)
				}

				override fun transformedToOriginal(offset: Int): Int {
					val separators = listOf(3, 7, 11).count { it < offset }
					return (offset - separators).coerceIn(0, digits.length)
				}
			},
		)
	}
}
