package com.example.billionemotosappkt.screens.dashboard.customer.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardTab

private val EmeraldNeon = Color(0xFF20D86B)
private val NavGlassBg = Color(0xFF080D0A)

@Composable
fun CustomerDashboardBottomNav(
	activeTab: CustomerDashboardTab,
	onTabChange: (CustomerDashboardTab) -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		color = NavGlassBg,
		tonalElevation = 6.dp,
		shadowElevation = 8.dp,
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
		shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
		modifier = modifier.fillMaxWidth(),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp, vertical = 6.dp),
			horizontalArrangement = Arrangement.SpaceEvenly,
			verticalAlignment = Alignment.CenterVertically,
		) {
			BottomNavItem(
				tab = CustomerDashboardTab.RESUMO,
				label = "Início",
				icon = Icons.Default.Dashboard,
				active = activeTab == CustomerDashboardTab.RESUMO,
				onClick = { onTabChange(CustomerDashboardTab.RESUMO) },
				modifier = Modifier.weight(1f),
			)
			BottomNavItem(
				tab = CustomerDashboardTab.PAGAMENTOS,
				label = "Pagamentos",
				icon = Icons.Default.CreditCard,
				active = activeTab == CustomerDashboardTab.PAGAMENTOS,
				onClick = { onTabChange(CustomerDashboardTab.PAGAMENTOS) },
				modifier = Modifier.weight(1f),
			)
			BottomNavItem(
				tab = CustomerDashboardTab.CONTRATO,
				label = "Contratos",
				icon = Icons.Default.Description,
				active = activeTab == CustomerDashboardTab.CONTRATO,
				onClick = { onTabChange(CustomerDashboardTab.CONTRATO) },
				modifier = Modifier.weight(1f),
			)
			BottomNavItem(
				tab = CustomerDashboardTab.SUPORTE,
				label = "Suporte",
				icon = Icons.Default.HeadsetMic,
				active = activeTab == CustomerDashboardTab.SUPORTE,
				onClick = { onTabChange(CustomerDashboardTab.SUPORTE) },
				modifier = Modifier.weight(1f),
			)
		}
	}
}

@Composable
private fun BottomNavItem(
	tab: CustomerDashboardTab,
	label: String,
	icon: ImageVector,
	active: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val activeColor = EmeraldNeon
	val inactiveColor = Color.White.copy(alpha = 0.40f)

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = modifier
			.clip(RoundedCornerShape(8.dp))
			.border(
				width = 1.dp,
				color = if (active) EmeraldNeon.copy(alpha = 0.30f) else Color.Transparent,
				shape = RoundedCornerShape(8.dp),
			)
			.background(
				if (active) EmeraldNeon.copy(alpha = 0.10f) else Color.Transparent
			)
			.clickable(onClick = onClick)
			.padding(vertical = 6.dp, horizontal = 2.dp),
	) {
		Icon(
			imageVector = icon,
			contentDescription = label,
			tint = if (active) activeColor else inactiveColor,
			modifier = Modifier.size(18.dp),
		)

		Spacer(modifier = Modifier.height(2.dp))

		Text(
			text = label,
			fontSize = 10.sp,
			fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
			color = if (active) activeColor else inactiveColor,
			textAlign = TextAlign.Center,
			maxLines = 1,
		)
	}
}
