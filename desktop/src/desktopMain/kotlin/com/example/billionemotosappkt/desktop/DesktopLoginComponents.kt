@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.example.billionemotosappkt.desktop

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun BrandSidePanel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Image(
                painter = painterResource("logo_billione.png"),
                contentDescription = "Billione Moto",
                modifier = Modifier.size(width = 190.dp, height = 86.dp),
                contentScale = ContentScale.Fit,
            )

            BadgePill(text = "Painel administrativo", accent = Color(0xFFD4AF37))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.widthIn(max = 520.dp)) {
                Text(
                    text = buildAnnotatedString {
                        append("Comande sua frota com ")
                        withStyle(SpanStyle(color = Color(0xFF20E65B), fontWeight = FontWeight.Black)) {
                            append("precisão")
                        }
                    },
                    color = Color.White,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 54.sp,
                    letterSpacing = (-0.04).sp,
                )
                Text(
                    text = "Operação completa em uma só plataforma - contratos, manutenção, financeiro e rastreamento em tempo real.",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 17.sp,
                    lineHeight = 26.sp,
                    modifier = Modifier.widthIn(max = 460.dp),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                    BrandStatCard(value = "100%", label = "Operação digital")
                    BrandStatCard(value = "24/7", label = "Monitoramento")
                    BrandStatCard(value = "ISO", label = "Padrão de segurança")
                }
            }
        }

        Text(
            text = "© 2026 Billione Motos · Todos os direitos reservados",
            color = Color.White.copy(alpha = 0.35f),
            fontSize = 12.sp,
        )
    }
}

@Composable
internal fun AdminLoginCard(
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

@Composable
internal fun ModeToggle(
    mode: DesktopLoginMode,
    primary: Color,
    compactLayout: Boolean,
    onModeChange: (DesktopLoginMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(14.dp))
            .padding(if (compactLayout) 3.dp else 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SegmentedToggleButton(
            text = "Entrar",
            selected = mode == DesktopLoginMode.LOGIN,
            primary = primary,
            compactLayout = compactLayout,
            modifier = Modifier.weight(1f),
            onClick = { onModeChange(DesktopLoginMode.LOGIN) },
        )
        SegmentedToggleButton(
            text = "Cadastrar",
            selected = mode == DesktopLoginMode.SIGNUP,
            primary = primary,
            compactLayout = compactLayout,
            modifier = Modifier.weight(1f),
            onClick = { onModeChange(DesktopLoginMode.SIGNUP) },
        )
    }
}

@Composable
internal fun SegmentedToggleButton(
    text: String,
    selected: Boolean,
    primary: Color,
    compactLayout: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val hovered = remember { mutableStateOf(false) }
    val bgColor by animateFloatAsState(
        targetValue = when {
            selected -> 1f
            hovered.value -> 0.15f
            else -> 0f
        },
        label = "toggle_bg_alpha",
    )

    Box(
        modifier = modifier
            .height(if (compactLayout) 36.dp else 40.dp)
            .pointerMoveFilter(
                onEnter = {
                    hovered.value = true
                    false
                },
                onExit = {
                    hovered.value = false
                    false
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selected) Color.White else Color.Transparent,
                contentColor = if (selected) Color.Black else Color.White.copy(alpha = 0.68f),
            ),
            border = if (selected) null else BorderStroke(0.dp, Color.Transparent),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = if (compactLayout) 10.dp else 12.dp,
                vertical = if (compactLayout) 5.dp else 6.dp,
            ),
        ) {
            Text(
                text = text,
                fontSize = if (compactLayout) 11.sp else 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (!selected && bgColor > 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = bgColor * 0.18f), RoundedCornerShape(10.dp)),
            )
        }
    }
}

@Composable
internal fun AdminField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    compactLayout: Boolean = false,
    secure: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    var focused by remember { mutableStateOf(false) }
    val hovered = remember { mutableStateOf(false) }
    val borderColor = when {
        focused -> Color(0xFF20E65B).copy(alpha = 0.65f)
        hovered.value -> Color.White.copy(alpha = 0.24f)
        else -> Color.White.copy(alpha = 0.12f)
    }
    val bgColor = if (focused) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.03f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerMoveFilter(
                onEnter = {
                    hovered.value = true
                    false
                },
                onExit = {
                    hovered.value = false
                    false
                },
            )
            .background(bgColor, RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = if (compactLayout) 14.dp else 16.dp, vertical = if (compactLayout) 10.dp else 12.dp),
        verticalArrangement = Arrangement.spacedBy(if (compactLayout) 6.dp else 8.dp),
    ) {
        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = 0.45f),
            fontSize = if (compactLayout) 9.sp else 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = if (compactLayout) 1.5.sp else 1.8.sp,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compactLayout) 10.dp else 12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Color.White.copy(alpha = if (focused) 0.95f else 0.42f),
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer { alpha = if (enabled) 1f else 0.65f }
                    .onFocusChanged { focused = it.isFocused },
                enabled = enabled,
                singleLine = true,
                shape = RoundedCornerShape(0.dp),
                placeholder = { Text(placeholder) },
                trailingIcon = trailingIcon,
                visualTransformation = if (secure) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.White.copy(alpha = 0.65f),
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.30f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.30f),
                    cursorColor = Color(0xFF20E65B),
                ),
            )
        }
    }
}

@Composable
internal fun IconButtonToggle(
    pressed: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (pressed) Color(0xFF20E65B) else Color.White.copy(alpha = 0.48f)
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(42.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = tint),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
    ) {
        Icon(
            imageVector = if (pressed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = null,
            tint = tint,
        )
    }
}

@Composable
internal fun DesktopPrimaryButton(
    text: String,
    onClick: () -> Unit,
    primary: Color,
    loading: Boolean,
    compactLayout: Boolean,
) {
    val hovered = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (hovered.value && !compactLayout) 1.01f else 1f,
        label = "primary_button_scale",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compactLayout) 52.dp else 58.dp)
            .pointerMoveFilter(
                onEnter = {
                    hovered.value = true
                    false
                },
                onExit = {
                    hovered.value = false
                    false
                },
            )
            .shadow(
                elevation = if (hovered.value && !compactLayout) 30.dp else 22.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = primary.copy(alpha = 0.28f),
                spotColor = primary.copy(alpha = 0.42f),
            )
            .graphicsLayer { scaleX = scale; scaleY = scale },
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = primary,
                contentColor = Color.Black,
                disabledContainerColor = primary.copy(alpha = 0.62f),
                disabledContentColor = Color.Black.copy(alpha = 0.8f),
            ),
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compactLayout) 15.sp else 16.sp,
                )
            }
        }
    }
}

@Composable
internal fun TextButtonLink(
    text: String,
    accent: Color,
    compactLayout: Boolean = false,
    onClick: () -> Unit,
) {
    val hovered = remember { mutableStateOf(false) }
    TextButton(
        onClick = onClick,
        modifier = Modifier.pointerMoveFilter(
            onEnter = {
                hovered.value = true
                false
            },
            onExit = {
                hovered.value = false
                false
            },
        ),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (hovered.value) accent else accent.copy(alpha = 0.84f),
        ),
    ) {
        Text(text = text, fontSize = if (compactLayout) 11.sp else 12.sp)
    }
}

@Composable
internal fun TopLeftExitButton(onBackToSite: () -> Unit, compactLayout: Boolean = false) {
    val hovered = remember { mutableStateOf(false) }
    OutlinedButton(
        onClick = onBackToSite,
        modifier = Modifier
            .padding(if (compactLayout) 14.dp else 20.dp)
            .background(Color.Transparent)
            .pointerMoveFilter(
                onEnter = {
                    hovered.value = true
                    false
                },
                onExit = {
                    hovered.value = false
                    false
                },
            ),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = if (hovered.value) 0.26f else 0.12f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White.copy(alpha = if (hovered.value) 1f else 0.74f),
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = if (compactLayout) 12.dp else 14.dp, vertical = if (compactLayout) 7.dp else 8.dp),
    ) {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(if (compactLayout) 13.dp else 14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Voltar ao site", fontSize = 11.sp)
    }
}

@Composable
internal fun BadgePill(text: String, accent: Color) {
    Box(
        modifier = Modifier
            .border(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(
            text = text,
            color = accent.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
        )
    }
}

@Composable
internal fun BrandStatCard(value: String, label: String) {
    Column(
        modifier = Modifier
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .widthIn(min = 116.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 10.sp,
            letterSpacing = 0.9.sp,
        )
    }
}

@Composable
internal fun AmbientOrb(
    alignment: Alignment,
    color: Color,
    alpha: Float,
    scale: Float,
    size: androidx.compose.ui.unit.Dp,
    offsetX: androidx.compose.ui.unit.Dp,
    offsetY: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = offsetX, y = offsetY)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = alignment,
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = alpha),
                            color.copy(alpha = alpha * 0.28f),
                            Color.Transparent,
                        ),
                    ),
                    RoundedCornerShape(999.dp),
                ),
        )
    }
}
