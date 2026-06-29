package com.example.billionemotosappkt.screens.dashboard.customer.components

import android.content.res.Configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerPayment
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerPaymentStatus
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerPaymentCard(
	payment: CustomerPayment,
	onDownloadBoletoClick: (CustomerPayment) -> Unit = {},
	initiallyExpanded: Boolean = false,
	modifier: Modifier = Modifier,
) {
	var expanded by remember(payment.month, payment.dueDate) {
		mutableStateOf(initiallyExpanded)
	}

	Column(
		modifier = modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f))
			.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
			.clickable { expanded = !expanded }
			.padding(14.dp),
		verticalArrangement = Arrangement.spacedBy(10.dp),
	) {
		Row(verticalAlignment = Alignment.Top) {
			Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
				Text(
					text = payment.month,
					style = MaterialTheme.typography.titleSmall,
					fontWeight = FontWeight.Black,
					color = MaterialTheme.colorScheme.onSurface,
				)
				Text(
					text = payment.dueDate,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
			Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					StatusDot(status = payment.status)
					Spacer(modifier = Modifier.size(8.dp))
					Text(
						text = statusLabel(payment.status),
						style = MaterialTheme.typography.labelSmall,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
				Icon(
					imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}

		if (expanded) {
			Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
				Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
					Text(
						text = "Valor da parcela",
						style = MaterialTheme.typography.labelSmall,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
					Text(
						text = payment.value,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Black,
						color = MaterialTheme.colorScheme.onSurface,
					)
				}
				Text(
					text = payment.subtitle,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
				TextButton(
					onClick = { onDownloadBoletoClick(payment) },
					modifier = Modifier.heightIn(min = 40.dp),
				) {
					Icon(imageVector = Icons.Default.Download, contentDescription = null)
					Spacer(modifier = Modifier.size(8.dp))
					Text(text = "Baixar boleto")
				}
			}
		} else {
			Text(
				text = "Toque para ver o valor e baixar o boleto.",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}

@Composable
private fun StatusDot(status: CustomerPaymentStatus) {
	val color = when (status) {
		CustomerPaymentStatus.PAID -> MaterialTheme.colorScheme.primary
		CustomerPaymentStatus.PENDING -> androidx.compose.ui.graphics.Color(0xFFF59E0B)
		CustomerPaymentStatus.OVERDUE -> androidx.compose.ui.graphics.Color(0xFFEF4444)
	}
	androidx.compose.foundation.layout.Box(
		modifier = Modifier
			.size(8.dp)
			.clip(CircleShape)
			.background(color),
	)
}

private fun statusLabel(status: CustomerPaymentStatus): String {
	return when (status) {
		CustomerPaymentStatus.PAID -> "PAGO"
		CustomerPaymentStatus.PENDING -> "PENDENTE"
		CustomerPaymentStatus.OVERDUE -> "ATRASADO"
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerPaymentCardPreviewCollapsed() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerPaymentCard(payment = CustomerDashboardPreviewData.dashboardData.payments[0])
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerPaymentCardPreviewExpanded() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerPaymentCard(
			payment = CustomerDashboardPreviewData.dashboardData.payments[1],
			initiallyExpanded = true,
		)
	}
}
