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

@Composable
internal fun DesktopMotoCategorySelector(
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
internal fun DesktopMotoHeroCard(
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
internal fun DesktopMotoFeatureCard(
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
internal fun DesktopMotoDetailsCard(
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
internal fun DesktopMotoBottomRentBar(
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
