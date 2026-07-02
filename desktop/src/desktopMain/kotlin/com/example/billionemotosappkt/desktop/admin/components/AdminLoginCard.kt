package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.DesktopLoginMode
import com.example.billionemotosappkt.desktop.MessageTone

@Composable
fun AdminLoginCard(
    mode: DesktopLoginMode,
    email: String,
    password: String,
    name: String,
    showPassword: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    statusMessage: String?,
    statusTone: MessageTone,
    primary: Color,
    compactLayout: Boolean,
    onModeChange: (DesktopLoginMode) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onSubmit: () -> Unit,
) {
    val cardShape = RoundedCornerShape(if (compactLayout) 26.dp else 32.dp)

    Box(
        modifier = Modifier
            .widthIn(max = if (compactLayout) 520.dp else 560.dp)
            .shadow(
                elevation = if (compactLayout) 34.dp else 42.dp,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.75f),
                spotColor = Color.Black.copy(alpha = 0.85f),
            )
            .border(1.dp, Color.White.copy(alpha = 0.12f), cardShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = if (compactLayout) 0.09f else 0.085f),
                        Color.White.copy(alpha = if (compactLayout) 0.05f else 0.045f),
                        Color(0xFF08110D).copy(alpha = 0.96f),
                    ),
                ),
                cardShape,
            )
            .padding(if (compactLayout) 20.dp else 26.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(if (compactLayout) 14.dp else 18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(if (compactLayout) 4.dp else 6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(if (compactLayout) 38.dp else 42.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF20E65B), Color(0xFFD4AF37)),
                                    ),
                                    RoundedCornerShape(14.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (mode == DesktopLoginMode.FORGOT) Icons.Default.Key else Icons.Default.Security,
                                contentDescription = null,
                                tint = Color.Black,
                            )
                        }
                        Column {
                            Text(
                                text = when (mode) {
                                    DesktopLoginMode.LOGIN -> "Acessar painel"
                                    DesktopLoginMode.SIGNUP -> "Criar conta"
                                    DesktopLoginMode.FORGOT -> "Recuperar senha"
                                },
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = if (compactLayout) 20.sp else 22.sp,
                            )
                            Text(
                                text = when (mode) {
                                    DesktopLoginMode.LOGIN -> "Entre para gerenciar a operação"
                                    DesktopLoginMode.SIGNUP -> "Cadastro de gestor interno"
                                    DesktopLoginMode.FORGOT -> "Enviaremos um link de recuperação"
                                },
                                color = Color.White.copy(alpha = 0.55f),
                                fontSize = if (compactLayout) 11.sp else 12.sp,
                            )
                        }
                    }
                }
            }

            if (mode != DesktopLoginMode.FORGOT) {
                ModeToggle(
                    mode = mode,
                    primary = primary,
                    compactLayout = compactLayout,
                    onModeChange = onModeChange,
                )
            }

            AnimatedContent(targetState = mode, label = "admin_login_mode_content") { currentMode ->
                Column(verticalArrangement = Arrangement.spacedBy(if (compactLayout) 12.dp else 14.dp)) {
                    if (currentMode == DesktopLoginMode.SIGNUP) {
                        AdminField(
                            label = "Nome completo",
                            value = name,
                            onValueChange = onNameChange,
                            placeholder = "Seu nome",
                            leadingIcon = Icons.Default.Person,
                            enabled = !isLoading,
                            compactLayout = compactLayout,
                        )
                    }

                    AdminField(
                        label = "E-mail",
                        value = email,
                        onValueChange = onEmailChange,
                        placeholder = "voce@billione.com",
                        leadingIcon = Icons.Default.Mail,
                        enabled = !isLoading,
                        compactLayout = compactLayout,
                    )

                    if (currentMode != DesktopLoginMode.FORGOT) {
                        AdminField(
                            label = "Senha",
                            value = password,
                            onValueChange = onPasswordChange,
                            placeholder = "••••••••",
                            leadingIcon = Icons.Default.Lock,
                            enabled = !isLoading,
                            compactLayout = compactLayout,
                            secure = !showPassword,
                            trailingIcon = {
                                IconButtonToggle(
                                    pressed = showPassword,
                                    enabled = !isLoading,
                                    onClick = onTogglePassword,
                                )
                            },
                        )
                    }
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF6B6B),
                    fontSize = if (compactLayout) 12.sp else 13.sp,
                )
            }

            if (!statusMessage.isNullOrBlank()) {
                Text(
                    text = statusMessage,
                    color = when (statusTone) {
                        MessageTone.SUCCESS -> Color(0xFF87F2A2)
                        MessageTone.ERROR -> Color(0xFFFF8C8C)
                        MessageTone.NEUTRAL -> Color.White.copy(alpha = 0.75f)
                    },
                    fontSize = if (compactLayout) 12.sp else 13.sp,
                )
            }

            DesktopPrimaryButton(
                text = when {
                    isLoading && mode == DesktopLoginMode.LOGIN -> "Aguarde..."
                    isLoading && mode == DesktopLoginMode.SIGNUP -> "Criando conta..."
                    isLoading && mode == DesktopLoginMode.FORGOT -> "Enviando..."
                    mode == DesktopLoginMode.LOGIN -> "Entrar no painel"
                    mode == DesktopLoginMode.SIGNUP -> "Criar conta"
                    else -> "Enviar link de recuperação"
                },
                onClick = onSubmit,
                primary = primary,
                loading = isLoading && mode != DesktopLoginMode.FORGOT,
                compactLayout = compactLayout,
            )

            if (mode == DesktopLoginMode.LOGIN) {
                TextButtonLink(
                    text = "Esqueci minha senha",
                    accent = Color(0xFF87F2A2),
                    compactLayout = compactLayout,
                    onClick = { onModeChange(DesktopLoginMode.FORGOT) },
                )
            } else if (mode == DesktopLoginMode.FORGOT) {
                TextButtonLink(
                    text = "← Voltar ao login",
                    accent = Color.White.copy(alpha = 0.72f),
                    compactLayout = compactLayout,
                    onClick = { onModeChange(DesktopLoginMode.LOGIN) },
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.08f)),
            )

            Text(
                text = "Acesso restrito · time interno",
                color = Color.White.copy(alpha = 0.38f),
                fontSize = if (compactLayout) 10.sp else 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
