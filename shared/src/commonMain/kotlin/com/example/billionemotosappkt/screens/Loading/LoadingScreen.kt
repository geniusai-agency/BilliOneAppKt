package com.example.billionemotosappkt.screens.Loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.safeDrawingPadding()
			.background(
				Brush.verticalGradient(
					colors = listOf(
						MaterialTheme.colorScheme.background,
						MaterialTheme.colorScheme.surface,
						MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
					),
				),
			),
		contentAlignment = Alignment.Center,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Text(
				text = "BilliOne",
				color = MaterialTheme.colorScheme.onSurface,
				fontSize = 30.sp,
				lineHeight = 34.sp,
				fontWeight = FontWeight.Black,
				textAlign = TextAlign.Center,
			)
			Text(
				text = "Motos",
				color = MaterialTheme.colorScheme.primary,
				fontSize = 13.sp,
				fontWeight = FontWeight.Bold,
				letterSpacing = 2.sp,
				textAlign = TextAlign.Center,
			)
			Spacer(modifier = Modifier.height(30.dp))
			CircularProgressIndicator(
				modifier = Modifier.size(44.dp),
				color = MaterialTheme.colorScheme.primary,
				strokeWidth = 4.dp,
			)
			Spacer(modifier = Modifier.height(20.dp))
			Text(
				text = "Preparando sua sessao",
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				fontSize = 14.sp,
				fontWeight = FontWeight.SemiBold,
				textAlign = TextAlign.Center,
			)
		}
	}
}
