package com.example.billionemotosappkt.screens.dashboard.customer.components

import android.content.res.Configuration

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerAppCard(
	modifier: Modifier = Modifier,
	title: String? = null,
	description: String? = null,
	contentPadding: PaddingValues = PaddingValues(16.dp),
	containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f),
	borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
	content: @Composable () -> Unit,
) {
	Card(
		colors = CardDefaults.cardColors(containerColor = containerColor),
		shape = RoundedCornerShape(24.dp),
		modifier = modifier
			.fillMaxWidth()
			.border(1.dp, borderColor, RoundedCornerShape(24.dp))
			.clip(RoundedCornerShape(24.dp)),
	) {
		Column(modifier = Modifier.padding(contentPadding)) {
			if (!title.isNullOrBlank() || !description.isNullOrBlank()) {
				Column(modifier = Modifier.padding(bottom = 12.dp)) {
					if (!title.isNullOrBlank()) {
						Text(
							text = title,
							style = MaterialTheme.typography.titleMedium,
							color = MaterialTheme.colorScheme.onSurface,
						)
					}
					if (!description.isNullOrBlank()) {
						Text(
							text = description,
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
						)
					}
				}
			}
			content()
		}
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerAppCardPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerAppCard(
			title = "Amostra de card",
			description = "Estrutura base usada em todo o dashboard.",
		) {
			Text(
				text = "Conteudo de exemplo",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}
