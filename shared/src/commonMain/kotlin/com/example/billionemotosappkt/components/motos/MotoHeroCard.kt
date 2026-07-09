package com.example.billionemotosappkt.components.motos

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.example.billionemotosappkt.shared.utils.rememberAssetPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MotoHeroCard(
	imageRes: String,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.fillMaxWidth()
			.height(250.dp)
			.border(
				1.dp,
				MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
				RoundedCornerShape(28.dp),
			)
			.background(
				Brush.linearGradient(
					colors = listOf(
						Color(0xFF09110C),
						Color(0xFF0E1711),
						Color(0xFF08100B),
					),
				),
				RoundedCornerShape(28.dp),
			),
		contentAlignment = Alignment.Center,
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth(0.76f)
				.height(180.dp)
				.background(
					Brush.radialGradient(
						colors = listOf(
							Color(0xFF1FE36A).copy(alpha = 0.18f),
							Color.Transparent,
						),
					),
					RoundedCornerShape(24.dp),
				),
		)
		Image(
			painter = rememberAssetPainter(imageRes),
			contentDescription = null,
			modifier = Modifier
				.fillMaxWidth(0.90f)
				.height(200.dp),
			contentScale = ContentScale.Fit,
		)
		Text(
			text = "TOQUE PARA VER DETALHES",
			color = MaterialTheme.colorScheme.primary,
			fontSize = 11.sp,
			fontWeight = FontWeight.Bold,
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(bottom = 12.dp),
		)
	}
}
