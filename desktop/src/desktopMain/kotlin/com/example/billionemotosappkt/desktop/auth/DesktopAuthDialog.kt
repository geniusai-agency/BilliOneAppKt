package com.example.billionemotosappkt.desktop.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

enum class DesktopAuthMode { LOGIN, REGISTER }

private val BrandGreen = Color(0xFF20E65B)
private val BrandGreenSoft = Color(0x3320E65B)
private val PanelBg = Color(0xFF0B100D)
private val PanelBorder = Color(0xFF2B5B3A)
private val FieldBg = Color(0xFF242B27)
private val FieldBgFocused = Color(0xFF2B332E)
private val FieldBorder = Color(0xFF313934)

private enum class RegisterStep {
	CPF,
	DETAILS,
}

@Composable
fun DesktopAuthDialog(
	mode: DesktopAuthMode,
	isBusy: Boolean,
	errorMessage: String?,
	onDismiss: () -> Unit,
	onLogin: (email: String, password: String) -> Unit,
	onRegister: (request: CreateUserRequest) -> Unit,
) {
	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(usePlatformDefaultWidth = false),
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(Color.Black.copy(alpha = 0.66f)),
			contentAlignment = Alignment.Center,
		) {
			Column(
				modifier = Modifier
					.widthIn(max = 760.dp)
					.padding(18.dp)
					.border(1.dp, PanelBorder, RoundedCornerShape(28.dp))
					.background(
						brush = Brush.linearGradient(
							colors = listOf(
								Color(0xFF101614),
								PanelBg,
								Color(0xFF09120D),
							),
						),
						shape = RoundedCornerShape(28.dp),
					)
					.padding(24.dp)
					.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.spacedBy(16.dp),
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
						Text(
							text = if (mode == DesktopAuthMode.LOGIN) "Entrar" else "Criar cadastro",
							color = Color.White,
							fontWeight = FontWeight.Black,
							style = MaterialTheme.typography.headlineSmall,
						)
						Text(
							text = if (mode == DesktopAuthMode.LOGIN) {
								"Acesse sua conta no sistema."
							} else {
								"Cadastre seus dados iniciais com o visual da marca."
							},
							color = Color.White.copy(alpha = 0.68f),
						)
					}

					Box(
						modifier = Modifier
							.border(1.dp, BrandGreen.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
							.background(BrandGreenSoft, RoundedCornerShape(18.dp))
							.padding(horizontal = 14.dp, vertical = 10.dp),
					) {
						Text(
							text = "Billione Moto",
							color = BrandGreen,
							fontWeight = FontWeight.Bold,
						)
					}
				}

				Box(
					modifier = Modifier
						.fillMaxWidth()
						.height(1.dp)
						.background(Color.White.copy(alpha = 0.08f)),
				)

				if (mode == DesktopAuthMode.LOGIN) {
					LoginForm(
						isBusy = isBusy,
						errorMessage = errorMessage,
						onDismiss = onDismiss,
						onSubmit = onLogin,
					)
				} else {
					RegisterForm(
						isBusy = isBusy,
						errorMessage = errorMessage,
						onDismiss = onDismiss,
						onSubmit = onRegister,
					)
				}
			}
		}
	}
}

@Composable
private fun LoginForm(
	isBusy: Boolean,
	errorMessage: String?,
	onDismiss: () -> Unit,
	onSubmit: (email: String, password: String) -> Unit,
) {
	var email by remember { mutableStateOf("") }
	var password by remember { mutableStateOf("") }

	Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
		BrandField(
			value = email,
			onValueChange = { email = it },
			label = "Email",
			enabled = !isBusy,
		)
		BrandField(
			value = password,
			onValueChange = { password = it },
			label = "Senha",
			enabled = !isBusy,
			visualTransformation = PasswordVisualTransformation(),
		)
		errorMessage?.let {
			Text(text = it, color = Color(0xFFFF6B6B))
		}
		Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
			OutlinedButton(
				onClick = onDismiss,
				enabled = !isBusy,
				colors = ButtonDefaults.outlinedButtonColors(
					contentColor = Color.White,
				),
				border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
			) {
				Text("Cancelar")
			}
			Button(
				onClick = { onSubmit(email.trim(), password) },
				enabled = !isBusy,
				colors = ButtonDefaults.buttonColors(
					containerColor = BrandGreen,
					contentColor = Color.Black,
				),
				shape = RoundedCornerShape(16.dp),
			) {
				if (isBusy) {
					CircularProgressIndicator(
						modifier = Modifier.padding(2.dp),
						strokeWidth = 2.dp,
						color = Color.Black,
					)
				} else {
					Text("Entrar")
				}
			}
		}
	}
}

@Composable
private fun RegisterForm(
	isBusy: Boolean,
	errorMessage: String?,
	onDismiss: () -> Unit,
	onSubmit: (request: CreateUserRequest) -> Unit,
) {
	var cpf by remember { mutableStateOf("") }
	var step by remember { mutableStateOf(RegisterStep.CPF) }
	var nome by remember { mutableStateOf("") }
	var nascimento by remember { mutableStateOf("") }
	var ddd by remember { mutableStateOf("") }
	var telefone by remember { mutableStateOf("") }
	var termosAceitos by remember { mutableStateOf(false) }

	Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
		when (step) {
			RegisterStep.CPF -> {
				RegisterCpfStep(
					cpf = cpf,
					isBusy = isBusy,
					errorMessage = errorMessage,
					onBack = onDismiss,
					onCpfChange = { cpf = it },
					onContinue = {
						if (isValidCpf(cpf)) {
							step = RegisterStep.DETAILS
						}
					},
				)
			}

			RegisterStep.DETAILS -> {
				RegisterDetailsStep(
					cpf = cpf,
					nome = nome,
					nascimento = nascimento,
					ddd = ddd,
					telefone = telefone,
					termosAceitos = termosAceitos,
					isBusy = isBusy,
					errorMessage = errorMessage,
					onBack = { step = RegisterStep.CPF },
					onCpfChange = { cpf = it },
					onNomeChange = { nome = it },
					onNascimentoChange = { nascimento = it },
					onDddChange = { ddd = it },
					onTelefoneChange = { telefone = it },
					onTermosChange = { termosAceitos = it },
					onContinue = {
						onSubmit(
							CreateUserRequest(
								email = "${onlyDigits(cpf).ifBlank { "cadastro" }}@billione.local",
								cpf = cpf.trim(),
								password = onlyDigits(cpf).ifBlank { "000000" },
								nome = nome.trim(),
								telefone = listOf(ddd, telefone)
									.joinToString(separator = "")
									.trim()
									.takeIf { it.isNotBlank() },
								kind = UserKind.CLIENT,
								status = UserStatus.ACTIVE,
								roles = listOf(AppRole.CLIENTE),
							),
						)
					},
				)
			}
		}
	}
}

@Composable
private fun RegisterCpfStep(
	cpf: String,
	isBusy: Boolean,
	errorMessage: String?,
	onBack: () -> Unit,
	onCpfChange: (String) -> Unit,
	onContinue: () -> Unit,
) {
	RegistrationHeader(
		onBack = onBack,
		centerTitle = "CADASTRO",
	)
	WelcomeBlock(
		title = "BEM-VINDO A *BILLIONE MOTO*.",
		subtitle = "Para realizar seu cadastro, precisaremos de alguns dados:",
	)
	BrandField(
		value = cpf,
		onValueChange = onCpfChange,
		label = "Seu CPF",
		placeholder = "999.999.999-99",
		enabled = !isBusy,
		keyboardType = KeyboardType.Number,
		visualTransformation = CpfVisualTransformation,
		errorMessage = errorMessage,
	)
	BrandPrimaryButton(
		text = "CONTINUAR",
		isBusy = isBusy,
		onClick = onContinue,
	)
}

@Composable
private fun RegisterDetailsStep(
	cpf: String,
	nome: String,
	nascimento: String,
	ddd: String,
	telefone: String,
	termosAceitos: Boolean,
	isBusy: Boolean,
	errorMessage: String?,
	onBack: () -> Unit,
	onCpfChange: (String) -> Unit,
	onNomeChange: (String) -> Unit,
	onNascimentoChange: (String) -> Unit,
	onDddChange: (String) -> Unit,
	onTelefoneChange: (String) -> Unit,
	onTermosChange: (Boolean) -> Unit,
	onContinue: () -> Unit,
) {
	RegistrationHeader(
		onBack = onBack,
		centerTitle = "CADASTRO",
	)
	WelcomeBlock(
		title = "BEM-VINDO A *BILLIONE MOTO*.",
		subtitle = "Para realizar seu cadastro, precisaremos de alguns dados:",
	)

	BrandField(
		value = cpf,
		onValueChange = onCpfChange,
		label = "Seu CPF",
		placeholder = "999.999.999-99",
		enabled = !isBusy,
		keyboardType = KeyboardType.Number,
		visualTransformation = CpfVisualTransformation,
	)
	BrandField(
		value = nome,
		onValueChange = onNomeChange,
		label = "Nome Completo",
		placeholder = "Nome Completo",
		enabled = !isBusy,
	)
	BrandField(
		value = nascimento,
		onValueChange = onNascimentoChange,
		label = "Data Nascimento",
		placeholder = "dd/mm/aaaa",
		enabled = !isBusy,
		keyboardType = KeyboardType.Number,
		visualTransformation = DateVisualTransformation,
	)
	Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
		BrandField(
			value = ddd,
			onValueChange = onDddChange,
			label = "DDD",
			placeholder = "XX",
			enabled = !isBusy,
			keyboardType = KeyboardType.Number,
			modifier = Modifier.width(92.dp),
		)
		BrandField(
			value = telefone,
			onValueChange = onTelefoneChange,
			label = "Telefone",
			placeholder = "XXXXX-XXXX",
			enabled = !isBusy,
			keyboardType = KeyboardType.Number,
			visualTransformation = PhoneVisualTransformation,
			modifier = Modifier.width(290.dp),
		)
	}

	Text(
		text = buildAnnotatedString {
			append("Ao avançar, você declara que leu e aceita os ")
			withStyle(SpanStyle(fontWeight = FontWeight.Bold, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) {
				append("termos de uso")
			}
			append(" e o ")
			withStyle(SpanStyle(fontWeight = FontWeight.Bold, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) {
				append("termos de contrato de locação")
			}
		},
		color = Color.White.copy(alpha = 0.72f),
		fontWeight = FontWeight.Normal,
	)

	errorMessage?.let {
		Text(text = it, color = Color(0xFFFF6B6B))
	}
	BrandPrimaryButton(
		text = "CONTINUAR",
		isBusy = isBusy,
		onClick = onContinue,
	)
}

@Composable
private fun RegistrationHeader(
	onBack: () -> Unit,
	centerTitle: String,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		OutlinedButton(
			onClick = onBack,
			modifier = Modifier.height(40.dp),
			colors = ButtonDefaults.outlinedButtonColors(
				contentColor = Color.White,
			),
			border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
		) {
			androidx.compose.material3.Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Voltar",
			)
		}
		Text(
			text = centerTitle,
			color = Color.White,
			fontWeight = FontWeight.Black,
		)
		Spacer(modifier = Modifier.height(40.dp))
	}
}

@Composable
private fun WelcomeBlock(
	title: String,
	subtitle: String,
) {
	Text(
		text = highlightBrand(title),
		color = Color.White,
		fontWeight = FontWeight.Black,
	)
	Text(
		text = subtitle,
		color = Color.White.copy(alpha = 0.72f),
	)
}

@Composable
private fun BrandPrimaryButton(
	text: String,
	isBusy: Boolean,
	enabled: Boolean = true,
	onClick: () -> Unit,
) {
	Button(
		onClick = onClick,
		enabled = enabled && !isBusy,
		modifier = Modifier.fillMaxWidth().height(56.dp),
		colors = ButtonDefaults.buttonColors(
			containerColor = BrandGreen,
			contentColor = Color.Black,
		),
		shape = RoundedCornerShape(18.dp),
	) {
		if (isBusy) {
			CircularProgressIndicator(
				modifier = Modifier.padding(2.dp),
				strokeWidth = 2.dp,
				color = Color.Black,
			)
		} else {
			Text(text)
		}
	}
}

@Composable
private fun BrandField(
	value: String,
	onValueChange: (String) -> Unit,
	label: String,
	placeholder: String = "",
	enabled: Boolean,
	visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
	keyboardType: KeyboardType = KeyboardType.Text,
	errorMessage: String? = null,
	modifier: Modifier = Modifier,
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(label) },
		placeholder = if (placeholder.isNotBlank()) ({ Text(placeholder) }) else null,
		singleLine = true,
		modifier = modifier.fillMaxWidth(),
		enabled = enabled,
		visualTransformation = visualTransformation,
		keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
		shape = RoundedCornerShape(20.dp),
		isError = errorMessage != null,
		colors = OutlinedTextFieldDefaults.colors(
			focusedTextColor = Color.White,
			unfocusedTextColor = Color.White,
			focusedContainerColor = FieldBgFocused,
			unfocusedContainerColor = FieldBg,
			focusedBorderColor = BrandGreen,
			unfocusedBorderColor = FieldBorder,
			cursorColor = BrandGreen,
			focusedLabelColor = BrandGreen,
			unfocusedLabelColor = Color.White.copy(alpha = 0.72f),
			focusedPlaceholderColor = Color.White.copy(alpha = 0.4f),
			unfocusedPlaceholderColor = Color.White.copy(alpha = 0.4f),
		),
	)
}

private fun highlightBrand(text: String) = buildAnnotatedString {
	val marker = "*BILLIONE MOTO*"
	val start = text.indexOf(marker)
	if (start < 0) {
		append(text)
		return@buildAnnotatedString
	}
	append(text.substring(0, start))
	withStyle(SpanStyle(color = BrandGreen)) {
		append(marker.removePrefix("*").removeSuffix("*"))
	}
	append(text.substring(start + marker.length))
}

private fun onlyDigits(value: String): String = value.filter(Char::isDigit)

private fun isValidCpf(value: String): Boolean {
	val digits = onlyDigits(value)
	if (digits.length != 11) return false
	if (digits.all { it == digits.first() }) return false
	return true
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
				override fun originalToTransformed(offset: Int): Int = offset
				override fun transformedToOriginal(offset: Int): Int = offset
			},
		)
	}
}

object PhoneVisualTransformation : VisualTransformation {
	override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
		val digits = onlyDigits(text.text).take(9)
		val formatted = buildString {
			when {
				digits.length <= 4 -> append(digits)
				else -> {
					append(digits.take(4))
					append('-')
					append(digits.drop(4))
				}
			}
		}
		return androidx.compose.ui.text.input.TransformedText(
			androidx.compose.ui.text.AnnotatedString(formatted),
			object : androidx.compose.ui.text.input.OffsetMapping {
				override fun originalToTransformed(offset: Int): Int = offset
				override fun transformedToOriginal(offset: Int): Int = offset
			},
		)
	}
}

object DateVisualTransformation : VisualTransformation {
	override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
		val digits = onlyDigits(text.text).take(8)
		val formatted = buildString {
			digits.forEachIndexed { index, char ->
				if (index == 2 || index == 4) append('/')
				append(char)
			}
		}
		return androidx.compose.ui.text.input.TransformedText(
			androidx.compose.ui.text.AnnotatedString(formatted),
			object : androidx.compose.ui.text.input.OffsetMapping {
				override fun originalToTransformed(offset: Int): Int = offset
				override fun transformedToOriginal(offset: Int): Int = offset
			},
		)
	}
}
