package com.example.billionemotosappkt.screens.dashboard.customer.components

import android.content.res.Configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HeadsetMic
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
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardTab
import androidx.compose.ui.tooling.preview.Preview
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerDashboardBottomNav(
	activeTab: CustomerDashboardTab,
	onTabChange: (CustomerDashboardTab) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.padding(horizontal = 16.dp, vertical = 12.dp)
			.clip(RoundedCornerShape(18.dp))
			.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.16f))
			.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
			.padding(6.dp),
	) {
		BottomItemView(
			item = BottomItem(CustomerDashboardTab.RESUMO, "Home", Icons.Default.Dashboard),
			active = activeTab == CustomerDashboardTab.RESUMO,
			onClick = { onTabChange(CustomerDashboardTab.RESUMO) },
			modifier = Modifier.width(76.dp),
		)
		BottomItemView(
			item = BottomItem(CustomerDashboardTab.PAGAMENTOS, "Pagar", Icons.Default.CreditCard),
			active = activeTab == CustomerDashboardTab.PAGAMENTOS,
			onClick = { onTabChange(CustomerDashboardTab.PAGAMENTOS) },
			modifier = Modifier.width(76.dp),
		)
		BottomItemView(
			item = BottomItem(CustomerDashboardTab.CONTRATO, "Contrato", Icons.Default.Description),
			active = activeTab == CustomerDashboardTab.CONTRATO,
			onClick = { onTabChange(CustomerDashboardTab.CONTRATO) },
			modifier = Modifier.width(84.dp),
		)
		BottomItemView(
			item = BottomItem(CustomerDashboardTab.SUPORTE, "Suporte", Icons.Default.HeadsetMic),
			active = activeTab == CustomerDashboardTab.SUPORTE,
			onClick = { onTabChange(CustomerDashboardTab.SUPORTE) },
			modifier = Modifier.width(84.dp),
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerDashboardBottomNavPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerDashboardBottomNav(
			activeTab = CustomerDashboardTab.PAGAMENTOS,
			onTabChange = {},
		)
	}
}

private data class BottomItem(
	val tab: CustomerDashboardTab,
	val label: String,
	val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

@Composable
private fun BottomItemView(
	item: BottomItem,
	active: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.clip(RoundedCornerShape(14.dp))
			.background(if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else androidx.compose.ui.graphics.Color.Transparent)
			.clickable(onClick = onClick)
			.padding(vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Icon(
			imageVector = item.icon,
			contentDescription = item.label,
			tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.padding(start = 8.dp),
		)
		Text(
			text = item.label,
			modifier = Modifier.fillMaxWidth(),
			textAlign = TextAlign.Center,
			style = MaterialTheme.typography.labelSmall,
			fontWeight = FontWeight.Black,
			color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}
