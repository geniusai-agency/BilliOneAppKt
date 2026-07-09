package com.example.billionemotosappkt.components.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class AuthQuickActionItem(
	val title: String,
	val icon: androidx.compose.ui.graphics.vector.ImageVector,
	val onClick: () -> Unit,
)

@Composable
fun AuthQuickActions(
	modifier: Modifier = Modifier,
	onMotosClick: () -> Unit = {},
	onPlanosClick: () -> Unit = {},
	onOndeEstamosClick: () -> Unit = {},
) {
	val items = listOf(
		AuthQuickActionItem("Nossas motos", Icons.Filled.TwoWheeler, onMotosClick),
		AuthQuickActionItem("Nossos planos", Icons.AutoMirrored.Filled.ReceiptLong, onPlanosClick),
		AuthQuickActionItem("Onde estamos", Icons.Filled.Place, onOndeEstamosClick),
	)

	Column(
		modifier = modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		items.forEach { item ->
			OutlinedButton(
				onClick = item.onClick,
				modifier = Modifier
					.fillMaxWidth(0.94f)
					.height(58.dp),
				shape = RoundedCornerShape(14.dp),
				border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					Row(
						horizontalArrangement = Arrangement.spacedBy(14.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						Icon(
							imageVector = item.icon,
							contentDescription = item.title,
							tint = MaterialTheme.colorScheme.primary,
						)
						Text(
							text = item.title,
							color = MaterialTheme.colorScheme.onSurface,
							fontSize = 16.sp,
						)
					}
					Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowForward,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.primary,
					)
				}
			}
		}
		Spacer(modifier = Modifier.height(2.dp))
	}
}
