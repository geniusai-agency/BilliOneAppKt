package com.example.billionemotosappkt.screens.dashboard.customer.components

import android.content.res.Configuration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerContract
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerContractTab(
	selectedContract: CustomerContract?,
	contracts: List<CustomerContract>,
	documents: List<String>,
	onSelectContract: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(14.dp),
	) {
		CustomerContractHeaderCard(contract = selectedContract)
		CustomerSectionHeader(title = "Documentos")
		CustomerContractDocumentsCard(documents = documents)
		CustomerContractMotoCard(contract = selectedContract)

		if (contracts.size > 1) {
			CustomerSectionHeader(title = "Outros contratos")
			Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
				contracts.forEach { contract ->
					CustomerContractSwitcherCard(
						contract = contract,
						selected = contract.id == selectedContract?.id,
						onClick = { onSelectContract(contract.id) },
					)
				}
			}
		}
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerContractTabPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerContractTab(
			selectedContract = CustomerDashboardPreviewData.contract,
			contracts = CustomerDashboardPreviewData.dashboardData.contracts,
			documents = CustomerDashboardPreviewData.dashboardData.documents,
			onSelectContract = {},
		)
	}
}
