package com.example.billionemotosappkt.screens.dashboard.customer.components



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerMetricCards(
	contractCount: Int,
	paidCount: Int,
	openCount: Int,
	modifier: Modifier = Modifier,
) {
	val metrics = listOf(
		MetricItem(
			label = "Contrato",
			value = contractCount.toString(),
			helper = "Contrato principal vinculado",
			accent = true,
		),
		MetricItem(
			label = "Pagamentos",
			value = paidCount.toString(),
			helper = "Parcelas pagas",
		),
		MetricItem(
			label = "Em aberto",
			value = openCount.toString(),
			helper = "Parcelas pendentes",
		),
	)

	BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
		val cardHeight = if (maxWidth < 360.dp) 112.dp else 124.dp

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(10.dp),
		) {
			metrics.forEach { metric ->
				MetricCard(
					label = metric.label,
					value = metric.value,
					helper = metric.helper,
					accent = metric.accent,
					modifier = Modifier
						.weight(1f)
						.height(cardHeight),
				)
			}
		}
	}
}

@Composable
private fun MetricCard(
	label: String,
	value: String,
	helper: String,
	accent: Boolean = false,
	modifier: Modifier = Modifier,
) {
	CustomerAppCard(
		modifier = modifier,
		containerColor = if (accent) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surfaceVariant.copy(
			alpha = 0.22f
		),
		borderColor = if (accent) MaterialTheme.colorScheme.primary.copy(alpha = 0.20f) else MaterialTheme.colorScheme.outline.copy(
			alpha = 0.30f
		),
		contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
		content = {
			Column(
				modifier = Modifier.fillMaxHeight(),
				verticalArrangement = Arrangement.SpaceBetween,
			) {
				Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
					Text(
						text = label.uppercase(),
						style = MaterialTheme.typography.labelSmall,
						fontWeight = FontWeight.Black,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						maxLines = 1,
					)
					Text(
						text = value,
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.Black,
						color = if (accent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
						maxLines = 1,
					)
				}
				Text(
					text = helper,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 2,
				)
			}
		},
	)
}

private data class MetricItem(
	val label: String,
	val value: String,
	val helper: String,
	val accent: Boolean = false,
)




