package com.example.billionemotosappkt.desktop.site

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

internal data class DesktopSlide(
	val title: String,
	val image: String,
	val desc: String,
)

@Composable
internal fun DesktopHeroCarousel(
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
internal fun DesktopTopBar() {
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
internal fun DesktopQuickActions(
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

internal data class DesktopQuickActionItem(
	val title: String,
	val icon: androidx.compose.ui.graphics.vector.ImageVector,
	val onClick: () -> Unit,
)

@Composable
internal fun DesktopActionButton(
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
