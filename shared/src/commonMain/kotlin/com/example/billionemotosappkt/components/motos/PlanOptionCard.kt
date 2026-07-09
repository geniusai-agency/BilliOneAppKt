package com.example.billionemotosappkt.components.motos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlanOptionCard(
	option: PlanOption,
	isSelected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val accent = MaterialTheme.colorScheme.primary
	
	Card(
		onClick = onClick,
		modifier = modifier.height(76.dp),
		shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(
			containerColor = if (isSelected) accent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.16f),
		),
		border = androidx.compose.foundation.BorderStroke(
			1.dp,
			if (isSelected) accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
		),
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 14.dp, vertical = 12.dp),
			horizontalAlignment = Alignment.Start,
			verticalArrangement = Arrangement.Center,
		) {
			Text(
				text = option.title,
				color = if (isSelected) accent else MaterialTheme.colorScheme.onSurface,
				fontSize = 13.sp,
				fontWeight = FontWeight.Bold,
				textAlign = TextAlign.Start,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis,
			)
			option.subtitle?.let {
				Spacer(modifier = Modifier.height(4.dp))
				Text(
					text = it,
					color = if (isSelected) accent.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
					fontSize = 11.sp,
					textAlign = TextAlign.Start,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			}
		}
	}
}
