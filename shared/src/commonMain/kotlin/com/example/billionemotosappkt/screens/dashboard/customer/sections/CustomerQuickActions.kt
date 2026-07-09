package com.example.billionemotosappkt.screens.dashboard.customer.components



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.AutoMirrored
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerAction
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardTab
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerQuickActions(
	actions: List<CustomerAction>,
	onActionClick: (CustomerDashboardTab?) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(10.dp),
	) {
		actions.chunked(2).forEach { rowItems ->
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(10.dp),
			) {
				rowItems.forEach { action ->
					ActionTile(
						action = action,
						onClick = { onActionClick(action.tabTarget) },
						modifier = Modifier
							.weight(1f)
							.heightIn(min = 88.dp, max = 88.dp),
					)
				}
			}
		}
	}
}

@Composable
private fun ActionTile(
	action: CustomerAction,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	CustomerAppCard(
		modifier = modifier
			.clickable(onClick = onClick)
			.clip(RoundedCornerShape(18.dp))
			.border(
				1.dp,
				if (action.accent) MaterialTheme.colorScheme.primary.copy(alpha = 0.26f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.26f),
				RoundedCornerShape(18.dp),
			),
		containerColor = if (action.accent) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.16f),
		contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 12.dp),
		content = {
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				Box(
					modifier = Modifier
						.size(42.dp)
						.clip(RoundedCornerShape(14.dp))
						.background(
							if (action.accent) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
							else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f),
						),
					contentAlignment = Alignment.Center,
				) {
					Icon(
						imageVector = actionIcon(action.icon),
						contentDescription = action.label,
						tint = if (action.accent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
				Text(
					text = action.label,
					textAlign = TextAlign.Center,
					style = MaterialTheme.typography.bodySmall,
					fontWeight = FontWeight.Black,
					color = if (action.accent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
				)
			}
		},
	)
}

private fun actionIcon(name: String) = when (name) {
	"credit-card" -> Icons.Default.CreditCard
	"document" -> Icons.Default.Description
	"headset" -> Icons.Default.HeadsetMic
		"trending-up" -> AutoMirrored.Filled.TrendingUp
	else -> Icons.Default.CreditCard
}


