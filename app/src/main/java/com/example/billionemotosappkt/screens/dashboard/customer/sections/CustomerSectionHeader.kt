package com.example.billionemotosappkt.screens.dashboard.customer.components

import android.content.res.Configuration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
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

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerSectionHeaderPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerSectionHeader(
			eyebrow = "BILLI ONE",
			title = "Dashboard completa",
			description = "Blocos separados com preview individual.",
		)
	}
}
