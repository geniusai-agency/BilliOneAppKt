package com.example.billionemotosappkt.screens.dashboard.customer.components



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.AutoMirrored
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerContract

import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerMotoCard(
	contract: CustomerContract?,
	modifier: Modifier = Modifier,
) {
	CustomerAppCard(
		modifier = modifier,
		title = "Sua moto",
		description = "Resumo rapido do veiculo vinculado ao contrato.",
		content = {
			Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
				CustomerMotoImage(imageRes = contract?.imageRes)
				Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
					Text(
						text = contract?.motoName ?: "Nenhum contrato ativo",
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Black,
						color = MaterialTheme.colorScheme.onSurface,
					)
					Text(
						text = contract?.motoSubtitle ?: "Conecte sua conta para ver a moto vinculada.",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
					Text(
						text = contract?.mileage ?: "0 km rodados",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
			}
		},
	)
}

@Composable
private fun CustomerMotoImage(imageRes: String?) {
	androidx.compose.foundation.layout.Box(
		modifier = Modifier
			.size(96.dp, 70.dp)
			.clip(RoundedCornerShape(18.dp))
			.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f))
			.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(18.dp)),
		contentAlignment = Alignment.Center,
	) {
		if (imageRes != null) {
			androidx.compose.foundation.Image(
				painter = com.example.billionemotosappkt.shared.utils.rememberAssetPainter(imageRes),
				contentDescription = null,
				contentScale = ContentScale.Fit,
				modifier = Modifier.fillMaxWidth(0.92f),
			)
		} else {
			Icon(
				imageVector = AutoMirrored.Filled.DirectionsBike,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary,
			)
		}
	}
}


