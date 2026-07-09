package com.example.billionemotosappkt.screens.dashboard.customer.components



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerSummaryChip

import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerSummaryChips(
	chips: List<CustomerSummaryChip>,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
		chips.forEach { chip ->
			SummaryChip(chip = chip, modifier = Modifier.fillMaxWidth())
		}
	}
}

@Composable
private fun SummaryChip(
	chip: CustomerSummaryChip,
	modifier: Modifier = Modifier,
) {
	CustomerAppCard(
		modifier = modifier,
		contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
		containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f),
		content = {
			Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
				Text(
					text = chip.label,
					style = MaterialTheme.typography.labelSmall,
					fontWeight = FontWeight.Black,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
				Text(
					text = chip.value,
					style = MaterialTheme.typography.titleSmall,
					fontWeight = FontWeight.Black,
					color = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
	)
}


