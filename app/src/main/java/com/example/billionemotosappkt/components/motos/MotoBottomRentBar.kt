package com.example.billionemotosappkt.components.motos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.components.ActionButton

@Composable
fun MotoBottomRentBar(
	price: String,
	benefitText: String,
	onRentClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier,
		color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
		shadowElevation = 18.dp,
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
	) {
		Column(
			modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.Bottom,
			) {
				Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
					Text(
						text = "Mensal a partir de",
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						fontSize = 10.sp,
						fontWeight = FontWeight.Bold,
					)
					Text(
						text = price,
						color = MaterialTheme.colorScheme.onSurface,
						fontWeight = FontWeight.Black,
						fontSize = 28.sp,
					)
				}
				Column(
					horizontalAlignment = Alignment.End,
					verticalArrangement = Arrangement.spacedBy(2.dp),
				) {
					Text(
						text = benefitText,
						color = MaterialTheme.colorScheme.primary,
						fontSize = 11.sp,
						fontWeight = FontWeight.Bold,
						textAlign = TextAlign.End,
					)
				}
			}
			
			ActionButton(
				text = "Quero alugar",
				onClick = onRentClick,
				containerColor = MaterialTheme.colorScheme.primary,
				pressedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.84f),
				contentColor = MaterialTheme.colorScheme.onPrimary,
				borderColor = MaterialTheme.colorScheme.primary,
				pressedBorderColor = MaterialTheme.colorScheme.primary,
				glowPulse = false,
			)
		}
	}
}
