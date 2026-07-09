package com.example.billionemotosappkt.screens.dashboard.customer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardTab

@Composable
fun CustomerDashboardTabs(
	activeTab: CustomerDashboardTab,
	onTabChange: (CustomerDashboardTab) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.20f))
			.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
			.padding(4.dp),
	) {
		TabChip(
			label = "Resumo",
			active = activeTab == CustomerDashboardTab.RESUMO,
			onClick = { onTabChange(CustomerDashboardTab.RESUMO) },
			modifier = Modifier.width(76.dp),
		)
		TabChip(
			label = "Pagamentos",
			active = activeTab == CustomerDashboardTab.PAGAMENTOS,
			onClick = { onTabChange(CustomerDashboardTab.PAGAMENTOS) },
			modifier = Modifier.width(104.dp),
		)
		TabChip(
			label = "Contrato",
			active = activeTab == CustomerDashboardTab.CONTRATO,
			onClick = { onTabChange(CustomerDashboardTab.CONTRATO) },
			modifier = Modifier.width(92.dp),
		)
		TabChip(
			label = "Suporte",
			active = activeTab == CustomerDashboardTab.SUPORTE,
			onClick = { onTabChange(CustomerDashboardTab.SUPORTE) },
			modifier = Modifier.width(88.dp),
		)
	}
}



@Composable
private fun TabChip(
	label: String,
	active: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Text(
		text = label,
		modifier = modifier
			.clip(RoundedCornerShape(12.dp))
			.background(if (active) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
			.clickable(onClick = onClick)
			.padding(vertical = 12.dp),
		textAlign = TextAlign.Center,
		style = MaterialTheme.typography.labelSmall,
		fontWeight = FontWeight.Black,
		color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
	)
}
