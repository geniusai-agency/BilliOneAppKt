package com.example.billionemotosappkt.screens.dashboard.customer.components



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerSectionHeader(
	eyebrow: String? = null,
	title: String,
	description: String? = null,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.padding(bottom = 4.dp),
	) {
		if (!eyebrow.isNullOrBlank()) {
			Text(
				text = eyebrow,
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.primary,
				fontWeight = FontWeight.Black,
			)
		}
		Text(
			text = title,
			style = MaterialTheme.typography.titleLarge,
			fontWeight = FontWeight.Black,
			color = MaterialTheme.colorScheme.onSurface,
		)
		if (!description.isNullOrBlank()) {
			Text(
				text = description,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}


