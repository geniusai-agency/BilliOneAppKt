package com.example.billionemotosappkt.desktop

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.billionemotosappkt.shared.api.ApiConfig
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.desktop.auth.AuthenticationContextResponse
import com.example.billionemotosappkt.desktop.auth.DesktopAuthClient
import com.example.billionemotosappkt.desktop.auth.DesktopAuthDialog
import com.example.billionemotosappkt.desktop.auth.DesktopAuthMode
import com.example.billionemotosappkt.desktop.auth.UserKind
import com.example.billionemotosappkt.desktop.auth.toContext
import com.example.billionemotosappkt.desktop.site.*

private enum class DesktopSection {
	HOME,
	MOTOS,
	PLANOS,
	ONDE_ESTAMOS,
}

@Composable
fun BillioneDesktopApp() {
	val authBaseUrl = remember { DesktopConfig.apiBaseUrl }
	val authClient = remember(authBaseUrl) { DesktopAuthClient(authBaseUrl) }
	val adminApi = remember(authBaseUrl, authClient) {
		BillioneMotosApi(
			ApiConfig(
				baseUrl = authBaseUrl,
				accessTokenProvider = { authClient.currentAccessToken() },
			),
		)
	}
	val scope = rememberCoroutineScope()
	var authMode by remember { mutableStateOf<DesktopAuthMode?>(null) }
	var authBusy by remember { mutableStateOf(false) }
	var authError by remember { mutableStateOf<String?>(null) }
	var authContext by remember { mutableStateOf<AuthenticationContextResponse?>(null) }
	var section by remember { mutableStateOf(DesktopSection.HOME) }

	LaunchedEffect(Unit) {
		runCatching { authClient.restoreAuthenticationContext() }
			.onSuccess { authContext = it }
	}

	DisposableEffect(Unit) {
		onDispose {
			authClient.close()
			scope.launch { adminApi.close() }
		}
	}

	MaterialTheme(
		colorScheme = darkColorScheme(
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
            if (authContext?.user?.kind == UserKind.INTERNAL) {
                DesktopAdminDashboardScreen(
                    authContext = authContext,
                    api = adminApi,
                    apiBaseUrl = authBaseUrl,
                    apiAccessToken = authClient.currentAccessToken(),
                    onOpenSite = { authContext = null },
                    onLogout = {
                        scope.launch {
                            runCatching { authClient.logout() }
                            authContext = null
                        }
                    },
                )
                return@Surface
            }

            val slides = remember {
				listOf(
					DesktopSlide(
						title = "Viva mais *liberdade* de Billione Moto",
						image = "moto_premium.png",
						desc = "Mude sua vida hoje, *sem burocracia*",
					),
					DesktopSlide(
						title = "Aventura *sem limites* na cidade",
						image = "hero_bg_moto1.png",
						desc = "AvelloZ 160 pronta para *qualquer terreno*",
					),
					DesktopSlide(
						title = "Rode com *estilo* pela cidade",
						image = "hero_bg_moto2.png",
						desc = "AvelloZ 160 vermelha, *potência e elegância*",
					),
					DesktopSlide(
						title = "Sua moto *ideal* te espera",
						image = "moto_eletrica_new.png",
						desc = "AvelloZ 160 azul, *liberdade no pôr do sol*",
					),
					DesktopSlide(
						title = "Entregas com *agilidade* e segurança",
						image = "moto_sport_updated.png",
						desc = "Entregas rápidas com *motos premium*",
					),
					DesktopSlide(
						title = "Modelos *modernos* e econômicos",
						image = "avelloz_160_black_new.png",
						desc = "Tecnologia e design *que impressionam*",
					),
					DesktopSlide(
						title = "Conquiste sua *independência*",
						image = "moto_detail_engine.png",
						desc = "No final do plano, *a moto é sua*",
					),
				)
			}

			AnimatedContent(
				targetState = section,
				transitionSpec = {
					fadeIn(animationSpec = tween(420)) togetherWith fadeOut(animationSpec = tween(420))
				},
				label = "desktop_section",
			) { currentSection ->
				when (currentSection) {
					DesktopSection.HOME -> DesktopHeroCarousel(
						slides = slides,
						authContext = authContext,
						onLoginClick = {
							authError = null
							authMode = DesktopAuthMode.LOGIN
						},
						onRegisterClick = {
							authError = null
							authMode = DesktopAuthMode.REGISTER
						},
						onMotosClick = { section = DesktopSection.MOTOS },
						onPlanosClick = { section = DesktopSection.PLANOS },
						onOndeEstamosClick = { section = DesktopSection.ONDE_ESTAMOS },
					)
					DesktopSection.MOTOS -> DesktopSectionScreen(
						title = "Nossas motos",
						description = "Escolha um modelo e veja os detalhes principais da frota.",
						onBack = { section = DesktopSection.HOME },
						content = { DesktopMotosScreen(onPlanosClick = { section = DesktopSection.PLANOS }, onOndeEstamosClick = { section = DesktopSection.ONDE_ESTAMOS }, onRentClick = { authMode = DesktopAuthMode.REGISTER }) },
					)
					DesktopSection.PLANOS -> DesktopSectionScreen(
						title = "Nossos planos",
						description = "Aqui entra a vitrine dos planos e condições de locação.",
						onBack = { section = DesktopSection.HOME },
						content = { DesktopPlaceholderSection("Planos", "A navegação já está funcionando. Agora falta conectar a lista real de planos.") },
					)
					DesktopSection.ONDE_ESTAMOS -> DesktopSectionScreen(
						title = "Onde estamos",
						description = "A navegação já leva para a seção de localização.",
						onBack = { section = DesktopSection.HOME },
						content = { DesktopPlaceholderSection("Localização", "Use este espaço para mapa, endereço e contato da unidade.") },
					)
				}
			}
		}
	}

	if (authMode != null) {
		DesktopAuthDialog(
			mode = authMode!!,
			isBusy = authBusy,
			errorMessage = authError,
			onDismiss = {
				authMode = null
				authError = null
			},
			onLogin = { email, password ->
				scope.launch {
					authBusy = true
					authError = null
					runCatching { authClient.login(email, password) }
						.onSuccess { session ->
							authContext = session.toContext()
							authMode = null
						}
						.onFailure { error ->
							authError = error.message ?: "Falha ao entrar."
						}
					authBusy = false
				}
			},
			onRegister = { request ->
				scope.launch {
					authBusy = true
					authError = null
					runCatching { authClient.register(request) }
						.onSuccess {
							authMode = DesktopAuthMode.LOGIN
							authError = null
						}
						.onFailure { error ->
							authError = error.message ?: "Falha ao cadastrar."
						}
					authBusy = false
				}
			},
		)
	}
}
