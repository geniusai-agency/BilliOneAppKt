package com.example.billionemotosappkt.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.`data-class`.SlideLogin
import com.example.billionemotosappkt.ui.theme.AppDimens
import com.example.billionemotosappkt.utils.highlightAsterisks
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

@Composable
fun CarrosselComSlide(
	imagens: List<SlideLogin>,
	topContent: @Composable () -> Unit,
	content: @Composable () -> Unit,
	autoAdvance: Boolean = true,
) {
	var imagemAtual by remember { mutableIntStateOf(0) }

	LaunchedEffect(imagens, autoAdvance) {
		if (!autoAdvance || imagens.size < 2) return@LaunchedEffect
		while (true) {
			delay(5.seconds)
			imagemAtual = (imagemAtual + 1) % imagens.size
		}
	}

	val item = imagens.getOrNull(imagemAtual) ?: return

	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		Box(modifier = Modifier.fillMaxSize()) {
			AnimatedContent(
				targetState = item.img,
				transitionSpec = {
					fadeIn(animationSpec = tween(650)) togetherWith
						fadeOut(animationSpec = tween(650))
				},
				label = "slide_imagens",
			) { imgRes ->
				Image(
					painter = painterResource(imgRes),
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
						brush = Brush.verticalGradient(
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
					.padding(horizontal = AppDimens.ScreenPadding, vertical = AppDimens.SectionPadding),
			) {
				topContent()
				Column(
					modifier = Modifier
						.weight(1f)
						.fillMaxWidth(),
					verticalArrangement = Arrangement.Bottom,
				) {
					Text(
						text = highlightAsterisks(
							text = item.title,
							baseColor = MaterialTheme.colorScheme.onSurface,
							highlightColor = MaterialTheme.colorScheme.primary,
						),
						fontSize = 38.sp,
						lineHeight = 40.sp,
						fontWeight = FontWeight.Black,
					)
					Text(
						text = highlightAsterisks(
							text = item.desc,
							baseColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.84f),
							highlightColor = MaterialTheme.colorScheme.onSurface,
						),
						fontSize = 16.sp,
						lineHeight = 24.sp,
						fontWeight = FontWeight.Normal,
					)

					Row(
						modifier = Modifier
							.padding(top = 16.dp)
							.fillMaxWidth(),
						horizontalArrangement = Arrangement.Center,
					) {
						imagens.forEachIndexed { index, _ ->
							Box(
								modifier = Modifier
									.padding(horizontal = 3.dp)
									.height(6.dp)
									.width(if (index == imagemAtual) 24.dp else 6.dp)
									.background(
										color = if (index == imagemAtual) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
										shape = CircleShape,
									),
							)
						}
					}

					Box(modifier = Modifier.height(18.dp))
					content()
				}
			}
		}
		Spacer(modifier = Modifier.height(16.dp))
	}
}
