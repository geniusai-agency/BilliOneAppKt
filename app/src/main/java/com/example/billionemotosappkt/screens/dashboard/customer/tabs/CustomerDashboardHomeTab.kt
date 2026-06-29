package com.example.billionemotosappkt.screens.dashboard.customer.components

import android.content.res.Configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerContract
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardData
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardTab
import com.example.billionemotosappkt.screens.dashboard.customer.HomeMenuItem
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerDashboardHomeTab(
	contract: CustomerContract?,
	data: CustomerDashboardData,
	openCount: Int,
	activeTab: CustomerDashboardTab = CustomerDashboardTab.RESUMO,
	onTabChange: (CustomerDashboardTab) -> Unit,
	paidCount: Int,
	onActionClick: (CustomerDashboardTab?) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(22.dp),
	) {
		CustomerPlanCard(contract = contract)
		
		CustomerDashboardTabs(activeTab = activeTab, onTabChange = onTabChange)
		
		CustomerQuickActions(actions = data.quickActions, onActionClick = onActionClick)
		
		Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
			homeMenuItems().forEach { item ->
				HomeActionRow(item = item)
			}
		}
		
		CustomerMotoCard(contract = contract)
		CustomerUpgradeBanner(banner = data.upgradeBanner)
		
		Spacer(modifier = Modifier.height(4.dp))
	}
}

private fun homeMenuItems(): List<HomeMenuItem> {
	return listOf(
		HomeMenuItem("Compra final da moto", "NOVO"),
		HomeMenuItem("Trocar de moto"),
		HomeMenuItem("Meus contratos"),
		HomeMenuItem("Central de ajuda"),
	)
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerHomePreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerDashboardHomeTab(
			contract = CustomerDashboardPreviewData.contract,
			data = CustomerDashboardPreviewData.dashboardData,
			openCount = 3,
			paidCount = 9,
			activeTab = CustomerDashboardTab.RESUMO,
			onTabChange = {},
			onActionClick = {},
		)
	}
}
