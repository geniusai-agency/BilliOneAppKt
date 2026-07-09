package com.example.billionemotosappkt.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActionButton(
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
	pulseScaleStart: Float = 1f,
	pulseScaleEnd: Float = 1.015f,
	pulseGlowStart: Float = 0.32f,
	pulseGlowEnd: Float = 0.5f,
	pulseElevationStart: Float = 18f,
	pulseElevationEnd: Float = 34f,
) {
	val interactionSource = remember { MutableInteractionSource() }
	val isPressed by interactionSource.collectIsPressedAsState()
	val infiniteTransition = if (glowPulse) {
		rememberInfiniteTransition(label = "auth_action_button_pulse")
	} else {
		null
	}

	val animatedContainerColor by animateColorAsState(
		targetValue = if (isPressed) pressedContainerColor else containerColor,
		label = "auth_action_button_container",
	)
	val animatedBorderColor by animateColorAsState(
		targetValue = if (isPressed) pressedBorderColor else borderColor,
		label = "auth_action_button_border",
	)

	val pulseScale by if (glowPulse && infiniteTransition != null) {
		infiniteTransition.animateFloat(
			initialValue = pulseScaleStart,
			targetValue = pulseScaleEnd,
			animationSpec = infiniteRepeatable(
				animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
				repeatMode = RepeatMode.Reverse,
			),
			label = "auth_action_button_pulse_scale",
		)
	} else {
		remember { androidx.compose.runtime.mutableFloatStateOf(1f) }
	}
	val pulseGlowAlpha by if (glowPulse && infiniteTransition != null) {
		infiniteTransition.animateFloat(
			initialValue = pulseGlowStart,
			targetValue = pulseGlowEnd,
			animationSpec = infiniteRepeatable(
				animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
				repeatMode = RepeatMode.Reverse,
			),
			label = "auth_action_button_pulse_alpha",
		)
	} else {
		remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
	}
	val pulseElevation by if (glowPulse && infiniteTransition != null) {
		infiniteTransition.animateFloat(
			initialValue = pulseElevationStart,
			targetValue = pulseElevationEnd,
			animationSpec = infiniteRepeatable(
				animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
				repeatMode = RepeatMode.Reverse,
			),
			label = "auth_action_button_pulse_elevation",
		)
	} else {
		remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
	}

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
					.height(64.dp)
			)
			{
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
