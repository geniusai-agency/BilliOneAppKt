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

private data class DesktopSlide(
	val title: String,
	val image: String,
	val desc: String,
)

private data class DesktopMotoVariant(
	val title: String,
	val subtitle: String,
	val image: String,
	val monthlyPrice: String,
	val consumption: String,
	val details: List<Pair<String, String>>,
	val specs: List<Pair<String, String>>,
)

private enum class DesktopSection {
	HOME,
	MOTOS,
	PLANOS,
	ONDE_ESTAMOS,
}

private const val DEFAULT_BILLIONE_API_BASE_URL = "https://engulf-blaming-scorpion.ngrok-free.dev"

@Composable
fun BillioneDesktopApp() {
	val authBaseUrl = remember {
		System.getenv("BILLIONE_API_BASE_URL")?.takeIf { it.isNotBlank() } ?: DEFAULT_BILLIONE_API_BASE_URL
	}
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

@Composable
private fun DesktopHeroCarousel(
	slides: List<DesktopSlide>,
	authContext: AuthenticationContextResponse?,
	onLoginClick: () -> Unit,
	onRegisterClick: () -> Unit,
	onMotosClick: () -> Unit,
	onPlanosClick: () -> Unit,
	onOndeEstamosClick: () -> Unit,
) {
	var currentIndex by remember { mutableIntStateOf(0) }

	LaunchedEffect(slides) {
		if (slides.isEmpty()) return@LaunchedEffect
		while (true) {
			delay(5.seconds)
			currentIndex = (currentIndex + 1) % slides.size
		}
	}

	val slide = slides.getOrNull(currentIndex) ?: return

	Box(modifier = Modifier.fillMaxSize()) {
		AnimatedContent(
			targetState = slide.image,
			transitionSpec = {
				fadeIn(animationSpec = tween(650)) togetherWith
					fadeOut(animationSpec = tween(650))
			},
			label = "desktop_hero_slide",
		) { image ->
			Image(
				painter = desktopImagePainter(image),
				contentDescription = null,
				modifier = Modifier.fillMaxSize(),
				colorFilter = ColorFilter.tint(
					Color.Black.copy(alpha = 0.35f),
					blendMode = BlendMode.Darken,
				),
				contentScale = ContentScale.Crop,
			)
		}

		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(
					Brush.verticalGradient(
						colors = listOf(
							Color.Black.copy(alpha = 0.05f),
							Color.Black.copy(alpha = 0.28f),
							Color.Black.copy(alpha = 0.70f),
						),
					),
				),
		)

		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 20.dp, vertical = 16.dp),
		) {
			DesktopTopBar()
			Spacer(modifier = Modifier.weight(1f))

			Text(
				text = highlightAsterisks(
					text = slide.title,
					baseColor = MaterialTheme.colorScheme.onSurface,
					highlightColor = MaterialTheme.colorScheme.primary,
				),
				fontSize = 38.sp,
				lineHeight = 40.sp,
				fontWeight = FontWeight.Black,
			)
			Text(
				text = highlightAsterisks(
					text = slide.desc,
					baseColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.88f),
					highlightColor = MaterialTheme.colorScheme.onSurface,
				),
				fontSize = 16.sp,
				lineHeight = 24.sp,
			)

			Spacer(modifier = Modifier.height(18.dp))
			DesktopActionButton(
				text = "SOU NOVO AQUI",
				onClick = onRegisterClick,
				containerColor = MaterialTheme.colorScheme.primary,
				pressedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.88f),
				contentColor = MaterialTheme.colorScheme.onPrimary,
				borderColor = MaterialTheme.colorScheme.primary,
				pressedBorderColor = MaterialTheme.colorScheme.primary,
				glowPulse = true,
				glowColor = MaterialTheme.colorScheme.primary,
			)
			Spacer(modifier = Modifier.height(10.dp))
			DesktopActionButton(
				text = "JÁ SOU CLIENTE",
				onClick = onLoginClick,
				containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.02f),
				pressedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.12f),
				contentColor = MaterialTheme.colorScheme.onSurface,
				borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.9f),
				pressedBorderColor = MaterialTheme.colorScheme.outline,
				borderWidth = 1.dp,
				glowPulse = false,
			)

			if (authContext != null) {
				Spacer(modifier = Modifier.height(14.dp))
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.border(
							1.dp,
							MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
							RoundedCornerShape(14.dp),
						)
						.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.22f))
						.padding(horizontal = 16.dp, vertical = 12.dp),
				) {
					Text(
						text = "Logado como ${authContext!!.user.nome}",
						color = MaterialTheme.colorScheme.onSurface,
					)
				}
			}

			Spacer(modifier = Modifier.height(18.dp))
			DesktopQuickActions(
				onMotosClick = onMotosClick,
				onPlanosClick = onPlanosClick,
				onOndeEstamosClick = onOndeEstamosClick,
			)
			Spacer(modifier = Modifier.height(8.dp))
		}
	}
}

@Composable
private fun DesktopSectionScreen(
	title: String,
	description: String,
	onBack: () -> Unit,
	content: @Composable () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(
				Brush.verticalGradient(
					colors = listOf(
						Color(0xFF050806),
						Color(0xFF0B110D),
						Color(0xFF050806),
					),
				),
			)
			.padding(horizontal = 20.dp, vertical = 16.dp),
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Button(
				onClick = onBack,
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.16f),
					contentColor = MaterialTheme.colorScheme.onSurface,
				),
				border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
			) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = null,
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text("Voltar")
			}
			Image(
				painter = painterResource("logo_billione.png"),
				contentDescription = "Logo Billione",
				modifier = Modifier.size(48.dp),
			)
		}

		Spacer(modifier = Modifier.height(22.dp))
		Text(
			text = title,
			fontSize = 34.sp,
			fontWeight = FontWeight.Black,
			color = MaterialTheme.colorScheme.onSurface,
		)
		Spacer(modifier = Modifier.height(6.dp))
		Text(
			text = description,
			fontSize = 16.sp,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)

	Spacer(modifier = Modifier.height(20.dp))
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f, fill = true),
		) {
			content()
		}
	}
}

@Composable
private fun DesktopMotosSection() {
	DesktopMotosScreen(
		onPlanosClick = { },
		onOndeEstamosClick = { },
		onRentClick = { },
	)
}

@Composable
private fun DesktopMotosScreen(
	onPlanosClick: () -> Unit,
	onOndeEstamosClick: () -> Unit,
	onRentClick: () -> Unit,
) {
	val motoVariants = remember {
		listOf(
			DesktopMotoVariant(
				title = "Sport",
				subtitle = "A mais agressiva da linha",
				image = "moto_sport_updated.png",
				monthlyPrice = "R$ 660",
				consumption = "45-65 km/L",
				details = listOf(
					"Freio" to "Tambor",
					"Partida" to "Pedal / Elétrica",
					"Tanque" to "10L",
					"Rodas" to "Liga leve",
				),
				specs = listOf(
					"Motor" to "160cc",
					"Cor" to "Preto",
					"Velocidade" to "Alta resposta",
				),
			),
			DesktopMotoVariant(
				title = "Sport ESD",
				subtitle = "Visual esportivo e urbano",
				image = "hero_bg_moto2.png",
				monthlyPrice = "R$ 620",
				consumption = "48-67 km/L",
				details = listOf(
					"Freio" to "Disco",
					"Partida" to "Elétrica",
					"Tanque" to "10L",
					"Rodas" to "Liga leve",
				),
				specs = listOf(
					"Motor" to "160cc",
					"Cor" to "Vermelha",
					"Robustez" to "Uso diário",
				),
			),
			DesktopMotoVariant(
				title = "Elétrica",
				subtitle = "Silenciosa e moderna",
				image = "moto_eletrica_new.png",
				monthlyPrice = "R$ 580",
				consumption = "Economia máxima",
				details = listOf(
					"Freio" to "Disco",
					"Partida" to "Botão",
					"Tanque" to "Sem tanque",
					"Rodas" to "Liga leve",
				),
				specs = listOf(
					"Motor" to "Elétrico",
					"Cor" to "Azul",
					"Autonomia" to "Longa duração",
				),
			),
			DesktopMotoVariant(
				title = "Premium",
				subtitle = "Conforto e presença",
				image = "moto_premium.png",
				monthlyPrice = "R$ 740",
				consumption = "Conforto premium",
				details = listOf(
					"Freio" to "Disco",
					"Partida" to "Elétrica",
					"Tanque" to "12L",
					"Rodas" to "Liga leve",
				),
				specs = listOf(
					"Motor" to "Premium",
					"Cor" to "Branca",
					"Acabamento" to "Top",
				),
			),
		)
	}

	var selectedIndex by remember { mutableIntStateOf(0) }
	var selectedCondition by remember { mutableStateOf(0) }
	var selectedContract by remember { mutableStateOf(1) }
	var selectedMileage by remember { mutableStateOf(1) }
	var selectedPayment by remember { mutableStateOf(1) }
	val selected = motoVariants[selectedIndex]

	Box(
		modifier = Modifier
			.fillMaxSize()
			.padding(bottom = 2.dp),
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(horizontal = 20.dp, vertical = 8.dp)
				.padding(bottom = 160.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					text = "ESCOLHA SUA MOTO",
					color = MaterialTheme.colorScheme.onSurface,
					fontSize = 18.sp,
					fontWeight = FontWeight.Black,
				)
				DesktopSectionLinks(
					onPlanosClick = onPlanosClick,
					onOndeEstamosClick = onOndeEstamosClick,
				)
			}

			Row(
				modifier = Modifier
					.fillMaxWidth()
					.horizontalScroll(rememberScrollState()),
				horizontalArrangement = Arrangement.spacedBy(10.dp),
			) {
				motoVariants.forEachIndexed { index, variant ->
					DesktopMotoCategorySelector(
						variant = variant,
						isSelected = index == selectedIndex,
						onClick = { selectedIndex = index },
					)
				}
			}

			Column(
				modifier = Modifier.fillMaxWidth(),
				verticalArrangement = Arrangement.spacedBy(16.dp),
			) {
				Text(
					text = "Conheça a nova",
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					fontSize = 16.sp,
					textAlign = TextAlign.Center,
					modifier = Modifier.fillMaxWidth(),
				)
				Text(
					text = "Billione ${selected.title} 160",
					color = MaterialTheme.colorScheme.onSurface,
					fontSize = 30.sp,
					fontWeight = FontWeight.Black,
					textAlign = TextAlign.Center,
					modifier = Modifier.fillMaxWidth(),
				)

				DesktopMotoHeroCard(imageName = selected.image)

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(12.dp),
				) {
					DesktopMotoFeatureCard(
						title = "Modelo",
						value = "Avelloz 160",
						modifier = Modifier.weight(1f),
					)
					DesktopMotoFeatureCard(
						title = "Consumo",
						value = selected.consumption,
						modifier = Modifier.weight(1f),
					)
				}

				DesktopMotoDetailsCard(items = selected.details)

				DesktopPlanSummaryCard(
					condition = planConditionLabel(selectedCondition),
					contract = planContractLabel(selectedContract),
					mileage = planMileageLabel(selectedMileage),
					payment = planPaymentLabel(selectedPayment),
				)
			}

			DesktopPlanCustomizationSection(
				selectedCondition = selectedCondition,
				onConditionSelected = { selectedCondition = it },
				selectedContract = selectedContract,
				onContractSelected = { selectedContract = it },
				selectedMileage = selectedMileage,
				onMileageSelected = { selectedMileage = it },
				selectedPayment = selectedPayment,
				onPaymentSelected = { selectedPayment = it },
			)

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				DesktopSectionLinkButton(text = "Nossos planos", onClick = onPlanosClick)
				DesktopSectionLinkButton(text = "Onde estamos", onClick = onOndeEstamosClick)
			}
		}

		DesktopMotoBottomRentBar(
			price = "R$ 1.100",
			benefitText = "Billione Premium",
			onRentClick = onRentClick,
			modifier = Modifier.align(Alignment.BottomCenter),
		)
	}
}

@Composable
private fun DesktopSectionLinks(
	onPlanosClick: () -> Unit,
	onOndeEstamosClick: () -> Unit,
) {
	Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
		DesktopSectionLinkButton(text = "Nossos planos", onClick = onPlanosClick)
		DesktopSectionLinkButton(text = "Onde estamos", onClick = onOndeEstamosClick)
	}
}

@Composable
private fun DesktopMotoCategorySelector(
	variant: DesktopMotoVariant,
	isSelected: Boolean,
	onClick: () -> Unit,
) {
	Card(
		onClick = onClick,
		modifier = Modifier.size(width = 118.dp, height = 130.dp),
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF050806)),
		border = BorderStroke(
			width = if (isSelected) 2.dp else 1.dp,
			color = if (isSelected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.18f),
		),
	) {
		Box(modifier = Modifier.fillMaxSize()) {
			Image(
				painter = desktopImagePainter(variant.image),
				contentDescription = variant.title,
				modifier = Modifier
					.align(Alignment.Center)
					.padding(top = 8.dp)
					.size(width = 88.dp, height = 60.dp),
				contentScale = ContentScale.Fit,
			)
			Text(
				text = variant.title,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(bottom = 8.dp),
				color = if (isSelected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.72f),
				fontSize = 12.sp,
				fontWeight = FontWeight.Bold,
				maxLines = 1,
			)
		}
	}
}

@Composable
private fun DesktopMotoHeroCard(
	imageName: String,
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(340.dp),
		contentAlignment = Alignment.Center,
	) {
		Box(
			modifier = Modifier
				.size(240.dp)
				.background(
					Brush.radialGradient(
						colors = listOf(
							Color(0xFF20E65B).copy(alpha = 0.20f),
							Color.Transparent,
						),
					),
				),
		)
		Image(
			painter = desktopImagePainter(imageName),
			contentDescription = null,
			modifier = Modifier
				.fillMaxWidth(0.84f)
				.height(240.dp),
			contentScale = ContentScale.Fit,
		)
		Text(
			text = "TOQUE PARA VER DETALHES →",
			color = MaterialTheme.colorScheme.primary,
			fontSize = 14.sp,
			fontWeight = FontWeight.Bold,
			modifier = Modifier.align(Alignment.BottomCenter),
		)
	}
}

@Composable
private fun DesktopMotoFeatureCard(
	title: String,
	value: String,
	modifier: Modifier = Modifier,
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f)),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
	) {
		Column(
			modifier = Modifier
				.padding(horizontal = 16.dp, vertical = 14.dp)
				.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Text(
				text = title,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				fontSize = 13.sp,
			)
			Text(
				text = value,
				color = MaterialTheme.colorScheme.onSurface,
				fontWeight = FontWeight.Black,
				fontSize = 20.sp,
			)
		}
	}
}

@Composable
private fun DesktopMotoDetailsCard(
	items: List<Pair<String, String>>,
) {
	Card(
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f)),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
		modifier = Modifier.fillMaxWidth(),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			items.forEach { (label, value) ->
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
					Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 15.sp)
				}
			}
		}
	}
}

@Composable
private fun DesktopSectionLinkButton(
	text: String,
	onClick: () -> Unit,
) {
	OutlinedButton(
		onClick = onClick,
		shape = RoundedCornerShape(14.dp),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
	) {
		Text(text = text, color = MaterialTheme.colorScheme.onSurface)
	}
}

@Composable
private fun DesktopMotoBottomRentBar(
	price: String,
	benefitText: String,
	onRentClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
		shadowElevation = 12.dp,
		modifier = modifier.fillMaxWidth(),
	) {
		Column(
			modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.Bottom,
			) {
				Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
					Text(
						text = "MENSAL A PARTIR DE",
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						fontSize = 11.sp,
						fontWeight = FontWeight.Bold,
					)
					Text(
						text = price,
						color = MaterialTheme.colorScheme.onSurface,
						fontWeight = FontWeight.Black,
						fontSize = 30.sp,
					)
				}
				Column(
					modifier = Modifier.width(190.dp),
					horizontalAlignment = Alignment.End,
					verticalArrangement = Arrangement.spacedBy(2.dp),
				) {
					Text(
						text = "Billione Premium",
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						fontSize = 11.sp,
						fontWeight = FontWeight.Medium,
						textAlign = TextAlign.End,
					)
					Text(
						text = benefitText,
						color = MaterialTheme.colorScheme.primary,
						fontSize = 11.sp,
						fontWeight = FontWeight.Bold,
						textAlign = TextAlign.End,
					)
				}
			}

			Button(
				onClick = onRentClick,
				modifier = Modifier
					.fillMaxWidth()
					.height(56.dp),
				shape = RoundedCornerShape(16.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary,
				),
			) {
				Text(
					text = "QUERO ALUGAR",
					fontWeight = FontWeight.Black,
					fontSize = 16.sp,
					color = MaterialTheme.colorScheme.onPrimary,
				)
			}
		}
	}
}

@Composable
private fun DesktopPlanCustomizationSection(
	selectedCondition: Int,
	onConditionSelected: (Int) -> Unit,
	selectedContract: Int,
	onContractSelected: (Int) -> Unit,
	selectedMileage: Int,
	onMileageSelected: (Int) -> Unit,
	selectedPayment: Int,
	onPaymentSelected: (Int) -> Unit,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(14.dp),
	) {
		Text(
			text = "PERSONALIZE SEU PLANO",
			color = MaterialTheme.colorScheme.onSurface,
			fontSize = 22.sp,
			fontWeight = FontWeight.Black,
		)

		DesktopPlanOptionGroup(
			title = "CONDIÇÃO",
			options = listOf(
				DesktopPlanOption("0 km", "+R$ 200/mês"),
				DesktopPlanOption("Seminova", null),
			),
			selectedIndex = selectedCondition,
			onSelected = onConditionSelected,
		)

		DesktopPlanOptionGroup(
			title = "CONTRATO",
			options = listOf(
				DesktopPlanOption("Opção compra no final", "+R$ 150/mês"),
				DesktopPlanOption("Somente aluguel", null),
			),
			selectedIndex = selectedContract,
			onSelected = onContractSelected,
		)

		DesktopPlanOptionGroup(
			title = "QUILOMETRAGEM",
			options = listOf(
				DesktopPlanOption("Km ilimitado", "+R$ 120/mês"),
				DesktopPlanOption("Km flexível", null),
			),
			selectedIndex = selectedMileage,
			onSelected = onMileageSelected,
		)

		DesktopPlanOptionGroup(
			title = "PAGAMENTO",
			options = listOf(
				DesktopPlanOption("Pagamento semanal", null),
				DesktopPlanOption("Pagamento mensal", null),
			),
			selectedIndex = selectedPayment,
			onSelected = onPaymentSelected,
		)
	}
}

@Composable
private fun DesktopPlanSummaryCard(
	condition: String,
	contract: String,
	mileage: String,
	payment: String,
) {
	Card(
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f)),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
		modifier = Modifier.fillMaxWidth(),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Text(
				text = "INFORMAÇÕES DO PLANO",
				color = MaterialTheme.colorScheme.onSurface,
				fontSize = 14.sp,
				fontWeight = FontWeight.Black,
			)
			DesktopPlanSummaryRow(label = "Condição", value = condition)
			DesktopPlanSummaryRow(label = "Contrato", value = contract)
			DesktopPlanSummaryRow(label = "Quilometragem", value = mileage)
			DesktopPlanSummaryRow(label = "Pagamento", value = payment)
		}
	}
}

@Composable
private fun DesktopPlanSummaryRow(
	label: String,
	value: String,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = label,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontSize = 13.sp,
		)
		Text(
			text = value,
			color = MaterialTheme.colorScheme.onSurface,
			fontSize = 13.sp,
			fontWeight = FontWeight.Bold,
		)
	}
}

private fun planConditionLabel(index: Int) = if (index == 0) "0 km" else "Seminova"

private fun planContractLabel(index: Int) = if (index == 0) "Opção compra no final" else "Somente aluguel"

private fun planMileageLabel(index: Int) = if (index == 0) "Km ilimitado" else "Km flexível"

private fun planPaymentLabel(index: Int) = if (index == 0) "Pagamento semanal" else "Pagamento mensal"

private data class DesktopPlanOption(
	val title: String,
	val subtitle: String?,
)

@Composable
private fun DesktopPlanOptionGroup(
	title: String,
	options: List<DesktopPlanOption>,
	selectedIndex: Int,
	onSelected: (Int) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
		Text(
			text = title,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontSize = 13.sp,
			fontWeight = FontWeight.Black,
		)
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			options.forEachIndexed { index, option ->
				DesktopPlanOptionCard(
					option = option,
					isSelected = index == selectedIndex,
					modifier = Modifier.weight(1f),
					onClick = { onSelected(index) },
				)
			}
		}
	}
}

@Composable
private fun DesktopPlanOptionCard(
	option: DesktopPlanOption,
	isSelected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val accent = Color(0xFF20E65B)

	Button(
		onClick = onClick,
		modifier = modifier.height(72.dp),
		shape = RoundedCornerShape(14.dp),
		colors = ButtonDefaults.buttonColors(
			containerColor = if (isSelected) accent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.18f),
			contentColor = if (isSelected) accent else MaterialTheme.colorScheme.onSurface,
		),
		border = BorderStroke(1.dp, if (isSelected) accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(2.dp),
		) {
			Text(
				text = option.title,
				fontWeight = FontWeight.Bold,
				fontSize = 14.sp,
				textAlign = TextAlign.Center,
			)
			option.subtitle?.let {
				Text(
					text = it,
					color = if (isSelected) accent.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
					fontSize = 12.sp,
					textAlign = TextAlign.Center,
				)
			}
		}
	}
}

@Composable
private fun DesktopPlaceholderSection(title: String, description: String) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f)),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
		modifier = Modifier.fillMaxWidth(),
	) {
		Column(
			modifier = Modifier.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
			Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
		}
	}
}

@Composable
private fun DesktopTopBar() {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 8.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			painter = painterResource("logo_billione.png"),
			contentDescription = "Logo Billione",
			modifier = Modifier.size(56.dp),
		)
		Text(
			text = "BRASIL",
			fontSize = 14.sp,
			fontWeight = FontWeight.Black,
		)
	}
}

@Composable
private fun DesktopQuickActions(
	onMotosClick: () -> Unit = {},
	onPlanosClick: () -> Unit = {},
	onOndeEstamosClick: () -> Unit = {},
) {
	val items = listOf(
		DesktopQuickActionItem("Nossas motos", Icons.Filled.TwoWheeler, onMotosClick),
		DesktopQuickActionItem("Nossos planos", Icons.AutoMirrored.Filled.ReceiptLong, onPlanosClick),
		DesktopQuickActionItem("Onde estamos", Icons.Filled.Place, onOndeEstamosClick),
	)

	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(12.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		items.forEach { item ->
			androidx.compose.material3.OutlinedButton(
				onClick = item.onClick,
				modifier = Modifier
					.fillMaxWidth()
					.height(58.dp),
				shape = RoundedCornerShape(14.dp),
				border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					Row(
						horizontalArrangement = Arrangement.spacedBy(14.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						androidx.compose.material3.Icon(
							imageVector = item.icon,
							contentDescription = item.title,
							tint = MaterialTheme.colorScheme.primary,
						)
						Text(
							text = item.title,
							color = MaterialTheme.colorScheme.onSurface,
							fontSize = 16.sp,
						)
					}
					androidx.compose.material3.Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowForward,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.primary,
					)
				}
			}
		}
	}
}

private data class DesktopQuickActionItem(
	val title: String,
	val icon: androidx.compose.ui.graphics.vector.ImageVector,
	val onClick: () -> Unit,
)

@Composable
private fun DesktopActionButton(
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.primary,
	pressedContainerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
	contentColor: Color = MaterialTheme.colorScheme.onPrimary,
	borderColor: Color = Color.Transparent,
	pressedBorderColor: Color = Color.Transparent,
	borderWidth: Dp = 0.dp,
	glowPulse: Boolean = false,
	glowColor: Color = containerColor,
) {
	val interactionSource = remember { MutableInteractionSource() }
	val isPressed by interactionSource.collectIsPressedAsState()
	val infiniteTransition = rememberInfiniteTransition(label = "desktop_action_pulse")

	val animatedContainerColor by animateColorAsState(
		targetValue = if (isPressed) pressedContainerColor else containerColor,
		label = "desktop_action_container",
	)
	val animatedBorderColor by animateColorAsState(
		targetValue = if (isPressed) pressedBorderColor else borderColor,
		label = "desktop_action_border",
	)

	val pulseScale by infiniteTransition.animateFloat(
		initialValue = 1f,
		targetValue = if (glowPulse) 1.015f else 1f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse,
		),
		label = "desktop_action_pulse_scale",
	)
	val pulseGlowAlpha by infiniteTransition.animateFloat(
		initialValue = if (glowPulse) 0.32f else 0f,
		targetValue = if (glowPulse) 0.5f else 0f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse,
		),
		label = "desktop_action_pulse_alpha",
	)
	val pulseElevation by infiniteTransition.animateFloat(
		initialValue = if (glowPulse) 18f else 0f,
		targetValue = if (glowPulse) 34f else 0f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse,
		),
		label = "desktop_action_pulse_elevation",
	)

	Box(
		modifier = modifier
			.fillMaxWidth()
			.height(72.dp),
		contentAlignment = Alignment.Center,
	) {
		if (glowPulse) {
			Canvas(
				modifier = Modifier
					.fillMaxWidth()
					.height(64.dp),
			) {
				val baseRadius = size.minDimension * 0.34f
				val glowRadius = baseRadius * (1.08f + (pulseScale - 1f) * 2.6f)
				val center = Offset(size.width / 2f, size.height / 2f)

				drawCircle(
					brush = Brush.radialGradient(
						colors = listOf(
							glowColor.copy(alpha = pulseGlowAlpha * 0.95f),
							glowColor.copy(alpha = pulseGlowAlpha * 0.45f),
							Color.Transparent,
						),
						center = center,
						radius = glowRadius * 1.9f,
					),
					radius = glowRadius * 1.9f,
					center = center,
				)
				drawCircle(
					color = glowColor.copy(alpha = pulseGlowAlpha * 0.28f),
					radius = glowRadius * 0.9f,
					center = center,
				)
			}
		}

		Button(
			onClick = onClick,
			modifier = Modifier
				.fillMaxWidth()
				.height(56.dp)
				.shadow(
					elevation = pulseElevation.dp,
					shape = RoundedCornerShape(16.dp),
					clip = false,
					ambientColor = glowColor.copy(alpha = pulseGlowAlpha),
					spotColor = glowColor.copy(alpha = pulseGlowAlpha),
				)
				.graphicsLayer {
					scaleX = pulseScale
					scaleY = pulseScale
				},
			shape = RoundedCornerShape(16.dp),
			colors = ButtonDefaults.buttonColors(
				containerColor = animatedContainerColor,
				contentColor = contentColor,
			),
			border = if (borderWidth > 0.dp) BorderStroke(borderWidth, animatedBorderColor) else null,
			interactionSource = interactionSource,
		) {
			Text(
				text = text,
				fontWeight = FontWeight.Bold,
				fontSize = 17.sp,
				letterSpacing = 0.2.sp,
			)
		}
	}
}

@Composable
private fun desktopImagePainter(resourceName: String): BitmapPainter {
	return remember(resourceName) {
		val stream = requireNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream(resourceName)) {
			"Missing desktop resource: $resourceName"
		}
		stream.use { BitmapPainter(loadImageBitmap(it)) }
	}
}

private fun highlightAsterisks(
	text: String,
	baseColor: Color,
	highlightColor: Color,
): AnnotatedString {
	val start = text.indexOf('*')
	val end = if (start >= 0) text.indexOf('*', startIndex = start + 1) else -1

	if (start < 0 || end <= start) return AnnotatedString(text.replace("*", ""))

	return buildAnnotatedString {
		withStyle(
			SpanStyle(color = baseColor),
		) {
			append(text.substring(0, start))
		}
		withStyle(
			SpanStyle(
				color = highlightColor,
				fontWeight = FontWeight.Bold,
			),
		) {
			append(text.substring(start + 1, end))
		}
		withStyle(
			SpanStyle(color = baseColor),
		) {
			append(text.substring(end + 1).replace("*", ""))
		}
	}
}
