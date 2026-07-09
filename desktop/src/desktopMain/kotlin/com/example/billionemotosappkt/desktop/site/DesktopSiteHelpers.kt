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
internal fun DesktopPlaceholderSection(title: String, description: String) {
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
internal fun desktopImagePainter(resourceName: String): BitmapPainter {
	return remember(resourceName) {
		val stream = requireNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream(resourceName)) {
			"Missing desktop resource: $resourceName"
		}
		stream.use { BitmapPainter(loadImageBitmap(it)) }
	}
}

internal fun highlightAsterisks(
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
