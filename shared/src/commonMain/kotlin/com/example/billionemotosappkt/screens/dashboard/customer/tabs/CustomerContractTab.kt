package com.example.billionemotosappkt.screens.dashboard.customer.components



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerContract
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerContractTab(
	selectedContract: CustomerContract?,
	contracts: List<CustomerContract>,
	documents: List<String>,
	onSelectContract: (String) -> Unit,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(14.dp),
	) {
		Button(
			onClick = onBack,
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.20f), contentColor = MaterialTheme.colorScheme.onSurface),
			modifier = Modifier.fillMaxWidth(),
			shape = RoundedCornerShape(16.dp),
		) {
			Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
			Spacer(modifier = Modifier.size(8.dp))
			Text(text = "Voltar", fontWeight = FontWeight.Black)
		}

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


