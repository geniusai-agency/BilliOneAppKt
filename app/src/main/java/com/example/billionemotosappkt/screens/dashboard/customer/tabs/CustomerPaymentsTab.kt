package com.example.billionemotosappkt.screens.dashboard.customer.components

import android.content.res.Configuration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerPayment
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerPaymentStatus
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerPaymentsTab(
	payments: List<CustomerPayment>,
	onDownloadBoletoClick: (CustomerPayment) -> Unit = {},
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(14.dp),
	) {
		CustomerMetricCards(
			contractCount = 1,
			paidCount = payments.count { it.status == CustomerPaymentStatus.PAID },
			openCount = payments.count { it.status != CustomerPaymentStatus.PAID },
		)

		CustomerAppCard(
			title = "Próximas parcelas",
			description = "Toque em uma parcela para abrir o valor e baixar o boleto.",
			content = {
				Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
					if (payments.isEmpty()) {
						Text(
							text = "Assim que o contrato estiver ativo, as parcelas aparecem aqui.",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
						)
					} else {
						payments.forEach { payment ->
							CustomerPaymentCard(
								payment = payment,
								onDownloadBoletoClick = onDownloadBoletoClick,
							)
						}
					}
				}
			},
		)

		Button(
			onClick = { },
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary,
			),
			modifier = Modifier
				.fillMaxWidth()
				.height(54.dp),
			shape = RoundedCornerShape(16.dp),
		) {
			Icon(imageVector = Icons.Default.CreditCard, contentDescription = null)
			Spacer(modifier = Modifier.size(8.dp))
			Text(text = "Pagar mensalidade", fontWeight = FontWeight.Black)
		}
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerPaymentsTabPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerPaymentsTab(payments = CustomerDashboardPreviewData.dashboardData.payments)
	}
}
