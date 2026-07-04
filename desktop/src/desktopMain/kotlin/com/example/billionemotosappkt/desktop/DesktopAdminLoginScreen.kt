package com.example.billionemotosappkt.desktop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.auth.AppRole
import com.example.billionemotosappkt.desktop.auth.AuthenticationContextResponse
import com.example.billionemotosappkt.desktop.auth.CreateProfileRequest
import com.example.billionemotosappkt.desktop.auth.CreateUserRequest
import com.example.billionemotosappkt.desktop.auth.DesktopAuthClient
import com.example.billionemotosappkt.desktop.auth.UserKind
import com.example.billionemotosappkt.desktop.auth.UserStatus
import com.example.billionemotosappkt.desktop.auth.toContext
import com.example.billionemotosappkt.desktop.admin.components.AdminLoginCard
import com.example.billionemotosappkt.desktop.admin.components.BrandSidePanel
import com.example.billionemotosappkt.desktop.admin.components.TopLeftExitButton
import kotlinx.coroutines.launch

@Composable
fun DesktopAdminLoginScreen(
    authClient: DesktopAuthClient,
    restoredSession: AuthenticationContextResponse?,
    onBackToSite: () -> Unit,
    onAuthenticated: (AuthenticationContextResponse) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val primary = Color(0xFF20E65B)

    var mode by rememberSaveable { mutableStateOf(DesktopLoginMode.LOGIN) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var statusMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var statusTone by rememberSaveable { mutableStateOf(MessageTone.NEUTRAL) }

    LaunchedEffect(restoredSession) {
        if (restoredSession != null) {
            statusMessage = "Sessão restaurada com sucesso."
            statusTone = MessageTone.SUCCESS
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05060A)),
    ) {
        val showBrandPanel = maxWidth >= 1080.dp
        val compactLayout = maxWidth < 700.dp
        val surfaceHorizontalPadding = if (compactLayout) 16.dp else if (showBrandPanel) 56.dp else 20.dp
        val surfaceVerticalPadding = if (compactLayout) 18.dp else 32.dp
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource("hero_bg_moto2.png"),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Black.copy(alpha = if (compactLayout) 0.88f else 0.82f),
                                Color.Black.copy(alpha = if (compactLayout) 0.70f else 0.54f),
                                Color.Black.copy(alpha = if (compactLayout) 0.44f else 0.18f),
                            ),
                        ),
                    ),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.48f),
                                Color.Black.copy(alpha = 0.10f),
                                Color.Black.copy(alpha = 0.70f),
                            ),
                        ),
                    ),
            )

            TopLeftExitButton(onBackToSite = onBackToSite, compactLayout = compactLayout)

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = surfaceHorizontalPadding, vertical = surfaceVerticalPadding),
                horizontalArrangement = if (showBrandPanel) Arrangement.SpaceBetween else Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showBrandPanel) {
                    BrandSidePanel(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(min = 420.dp, max = 520.dp),
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(min = 480.dp, max = 580.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    AdminLoginCard(
                        mode = mode,
                        email = email,
                        password = password,
                        name = name,
                        showPassword = showPassword,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        statusMessage = statusMessage,
                        statusTone = statusTone,
                        primary = primary,
                        compactLayout = compactLayout,
                        onModeChange = {
                            mode = it
                            errorMessage = null
                            statusMessage = null
                            statusTone = MessageTone.NEUTRAL
                        },
                        onEmailChange = {
                            email = it
                            errorMessage = null
                        },
                        onPasswordChange = {
                            password = it
                            errorMessage = null
                        },
                        onNameChange = {
                            name = it
                            errorMessage = null
                        },
                        onTogglePassword = { showPassword = !showPassword },
                        onSubmit = {
                            scope.launch {
                                errorMessage = null
                                statusMessage = null
                                isLoading = true
                                when (mode) {
                                    DesktopLoginMode.LOGIN -> {
                                        val emailCheck = email.trim()
                                        val passwordCheck = password
                                        when {
                                            !emailCheck.isValidEmail() -> errorMessage = "E-mail inválido"
                                            passwordCheck.length < 6 -> errorMessage = "Mínimo 6 caracteres"
                                            else -> runCatching {
                                                authClient.login(emailCheck, passwordCheck)
                                            }.onSuccess { session ->
                                                onAuthenticated(session.toContext())
                                            }.onFailure { error ->
                                                errorMessage = error.message ?: "Erro ao autenticar"
                                            }
                                        }
                                    }
                                    DesktopLoginMode.SIGNUP -> {
                                        val emailCheck = email.trim()
                                        val passwordCheck = password
                                        val nameCheck = name.trim()
                                        when {
                                            !emailCheck.isValidEmail() -> errorMessage = "E-mail inválido"
                                            nameCheck.length < 2 -> errorMessage = "Informe seu nome"
                                            passwordCheck.length < 6 -> errorMessage = "Mínimo 6 caracteres"
                                            else -> runCatching {
                                                authClient.register(
                                                    CreateUserRequest(
                                                        email = emailCheck,
                                                        cpf = generateCpfForEmail(emailCheck),
                                                        password = passwordCheck,
                                                        nome = nameCheck,
                                                        telefone = null,
                                                        kind = UserKind.INTERNAL,
                                                        status = UserStatus.ACTIVE,
                                                        roles = listOf(AppRole.ADMIN),
                                                        profile = CreateProfileRequest(),
                                                    ),
                                                )
                                            }.onSuccess {
                                                mode = DesktopLoginMode.LOGIN
                                                statusMessage = "Cadastro realizado. Faça login."
                                                statusTone = MessageTone.SUCCESS
                                            }.onFailure { error ->
                                                errorMessage = error.message ?: "Erro ao cadastrar"
                                            }
                                        }
                                    }
                                    DesktopLoginMode.FORGOT -> {
                                        if (!email.trim().isValidEmail()) {
                                            errorMessage = "E-mail inválido"
                                        } else {
                                            statusMessage = "Recuperação de senha ainda não está conectada nesta build."
                                            statusTone = MessageTone.NEUTRAL
                                            mode = DesktopLoginMode.LOGIN
                                        }
                                    }
                                }
                                isLoading = false
                            }
                        },
                    )
                }
            }
        }
    }
}
