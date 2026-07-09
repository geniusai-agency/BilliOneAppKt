package com.example.billionemotosappkt.screens.dashboard.customer.components



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.example.billionemotosappkt.shared.utils.rememberAssetPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerContract

import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme
import com.example.billionemotosappkt.screens.dashboard.customer.formatCurrency
import com.example.billionemotosappkt.screens.dashboard.customer.labelFromContract

@Composable
fun CustomerPlanCard(
	contract: CustomerContract?,
	modifier: Modifier = Modifier,
) {
	CustomerAppCard(
		modifier = modifier,
		containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
		borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
		content = {
			Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				Column(modifier = Modifier.weight(1f)) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						Icon(
							imageVector = Icons.Default.Shield,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.primary,
							modifier = Modifier.size(16.dp),
						)
						Spacer(modifier = Modifier.width(6.dp))
						Text(
							text = labelFromContract(contract?.planName),
							style = MaterialTheme.typography.labelSmall,
							fontWeight = FontWeight.Black,
							color = MaterialTheme.colorScheme.primary,
							letterSpacing = 0.8.sp,
						)
					}
					Spacer(modifier = Modifier.height(10.dp))
					Text(
						text = contract?.motoName ?: "Nenhum contrato encontrado",
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.Black,
						color = MaterialTheme.colorScheme.onSurface,
					)
					Text(
						text = contract?.motoSubtitle ?: "Aguardando contrato liberado",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}

				BoxedMotoImage(imageRes = contract?.imageRes)
			}

			Spacer(modifier = Modifier.height(12.dp))
			Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
				Column {
					Text(
						text = "Proximo vencimento",
						style = MaterialTheme.typography.labelSmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
					Text(
						text = contract?.nextDue ?: "Sem parcelas",
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Black,
						color = MaterialTheme.colorScheme.onSurface,
					)
				}
				Column(horizontalAlignment = Alignment.End) {
					Text(
						text = "Valor mensal",
						style = MaterialTheme.typography.labelSmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
					Text(
						text = contract?.monthlyValue ?: formatCurrency("0"),
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Black,
						color = MaterialTheme.colorScheme.onSurface,
					)
				}
			}

			Spacer(modifier = Modifier.height(8.dp))
			Row(verticalAlignment = Alignment.CenterVertically) {
				Box(
					modifier = Modifier
						.size(8.dp)
						.clip(CircleShape)
						.background(MaterialTheme.colorScheme.primary),
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text = contract?.nextDueSubtitle ?: "Nenhuma parcela aberta no momento",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		},
	)
}

@Composable
private fun BoxedMotoImage(
	imageRes: String?,
) {
	Box(
		modifier = Modifier
			.size(116.dp, 84.dp)
			.clip(RoundedCornerShape(18.dp))
			.background(Color.Transparent)
			.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(18.dp)),
		contentAlignment = Alignment.Center,
	) {
		if (imageRes != null) {
			androidx.compose.foundation.Image(
				painter = rememberAssetPainter(imageRes),
				contentDescription = null,
				contentScale = ContentScale.Fit,
				modifier = Modifier.fillMaxWidth(0.92f),
			)
		}
	}
}


