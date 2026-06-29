package com.example.billionemotosappkt.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.components.ActionButton
import com.example.billionemotosappkt.ui.theme.AppDimens

@Composable
fun SectionPlaceholderScreen(
	title: String,
	description: String,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.background(
				Brush.verticalGradient(
					listOf(
						MaterialTheme.colorScheme.surface,
						MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f),
					),
				),
			),
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(AppDimens.ScreenPadding),
			verticalArrangement = Arrangement.spacedBy(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Card(
				shape = RoundedCornerShape(24.dp),
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
				modifier = Modifier.fillMaxWidth(),
			) {
				Column(
					modifier = Modifier.padding(24.dp),
					verticalArrangement = Arrangement.spacedBy(12.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
				) {
					Text(
						text = title,
						fontSize = 30.sp,
						fontWeight = FontWeight.Black,
					)
					Text(
						text = description,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						fontSize = 15.sp,
					)
				}
			}
			
			ActionButton(
				text = "VOLTAR",
				onClick = onBack,
				containerColor = MaterialTheme.colorScheme.primary,
				pressedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.88f),
				contentColor = MaterialTheme.colorScheme.onPrimary,
				borderColor = MaterialTheme.colorScheme.primary,
				pressedBorderColor = MaterialTheme.colorScheme.primary,
				glowPulse = false,
			)
		}
	}
}
