package com.example.billionemotosappkt.screens.dashboard.customer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.AutoMirrored
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.HomeMenuItem

import androidx.compose.foundation.clickable

@Composable
fun HomeActionRow(item: HomeMenuItem, onClick: () -> Unit = {}) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.clickable(onClick = onClick)
			.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f))
			.border(
				1.dp,
				MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
				RoundedCornerShape(16.dp)
			)
			.padding(14.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
			Box(
				modifier = Modifier
					.size(38.dp)
					.clip(RoundedCornerShape(14.dp))
					.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
				contentAlignment = Alignment.Center,
			) {
				Icon(
					imageVector = actionIcon(item.label),
					contentDescription = item.label,
					tint = MaterialTheme.colorScheme.primary,
				)
			}
			Spacer(modifier = Modifier.size(12.dp))
			Text(
				text = item.label,
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Black,
				color = MaterialTheme.colorScheme.onSurface,
			)
		}
		Text(
			text = if (item.badge.isNullOrBlank()) "›" else item.badge,
			style = MaterialTheme.typography.labelSmall,
			fontWeight = FontWeight.Black,
			color = MaterialTheme.colorScheme.primary,
		)
	}
}

private fun actionIcon(name: String) = when (name) {
	"Compra final da moto" -> Icons.Default.Key
	"Trocar de moto" -> AutoMirrored.Filled.DirectionsBike
	"Meus contratos" -> Icons.Default.Description
	"Central de ajuda" -> Icons.Default.HeadsetMic
	else -> Icons.Default.CreditCard
}
