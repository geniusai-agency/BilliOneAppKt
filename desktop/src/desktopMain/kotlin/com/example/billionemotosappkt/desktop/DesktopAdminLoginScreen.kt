package com.example.billionemotosappkt.desktop

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
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
import androidx.compose.ui.graphics.ColorFilter
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
    val amber = Color(0xFFD4AF37)

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
        val pulseTransition = rememberInfiniteTransition(label = "admin_login_pulse")
        val orbAlpha by pulseTransition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.55f,
            animationSpec = infiniteRepeatable(
                animation = tween(5200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "admin_login_orb_alpha",
        )
        val glowScale by pulseTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(6800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "admin_login_orb_scale",
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource("hero_bg_moto1.png"),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(Color.Black.copy(alpha = if (compactLayout) 0.16f else 0.22f)),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Black.copy(alpha = if (compactLayout) 0.90f else 0.88f),
                                Color.Black.copy(alpha = if (compactLayout) 0.72f else 0.65f),
                                Color.Black.copy(alpha = if (compactLayout) 0.36f else 0.28f),
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
                                Color.Black.copy(alpha = 0.64f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.78f),
                            ),
                        ),
                    ),
            )

            Box(modifier = Modifier.fillMaxSize()) {
                AmbientOrb(
                    alignment = Alignment.TopStart,
                    color = primary,
                    alpha = orbAlpha,
                    scale = glowScale,
                    size = 520.dp,
                    offsetX = (-150).dp,
                    offsetY = (-120).dp,
                )
                AmbientOrb(
                    alignment = Alignment.TopEnd,
                    color = amber,
                    alpha = orbAlpha * 0.9f,
                    scale = glowScale * 1.02f,
                    size = 560.dp,
                    offsetX = 160.dp,
                    offsetY = 40.dp,
                )
                AmbientOrb(
                    alignment = Alignment.BottomStart,
                    color = primary,
                    alpha = orbAlpha * 0.55f,
                    scale = glowScale * 0.95f,
                    size = 460.dp,
                    offsetX = 40.dp,
                    offsetY = 140.dp,
                )
            }

            TopLeftExitButton(onBackToSite = onBackToSite, compactLayout = compactLayout)

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = surfaceHorizontalPadding, vertical = surfaceVerticalPadding),
            ) {
                if (showBrandPanel) {
                    BrandSidePanel(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(end = 36.dp),
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(if (showBrandPanel) 0.92f else 1f)
                        .fillMaxHeight(),
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
