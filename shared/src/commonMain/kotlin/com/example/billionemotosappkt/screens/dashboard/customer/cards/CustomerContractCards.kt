package com.example.billionemotosappkt.screens.dashboard.customer.components



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.AutoMirrored
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.billionemotosappkt.shared.utils.rememberAssetPainter
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerContract
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerContractHeaderCard(
	contract: CustomerContract?,
	modifier: Modifier = Modifier,
) {
	CustomerAppCard(
		modifier = modifier,
		containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.16f),
		borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
		contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
		content = {
			Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						Icon(
							imageVector = Icons.Default.Shield,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.primary,
							modifier = Modifier.size(16.dp),
						)
						Spacer(modifier = Modifier.size(6.dp))
						Text(
							text = "CONTRATO ATIVO",
							style = MaterialTheme.typography.labelSmall,
							fontWeight = FontWeight.Black,
							color = MaterialTheme.colorScheme.primary,
						)
					}
					Box(
						modifier = Modifier
							.size(8.dp)
							.clip(CircleShape)
							.background(MaterialTheme.colorScheme.primary),
					)
				}

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						Text(
							text = "ID:",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
						)
						Spacer(modifier = Modifier.size(10.dp))
						Box(
							modifier = Modifier
								.clip(RoundedCornerShape(6.dp))
								.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f))
								.padding(horizontal = 10.dp, vertical = 6.dp),
						) {
							Text(
								text = contract?.contractCode ?: "Sem contrato",
								style = MaterialTheme.typography.labelSmall,
								fontWeight = FontWeight.Black,
								color = MaterialTheme.colorScheme.onSurface,
							)
						}
						Spacer(modifier = Modifier.size(8.dp))
						Icon(
							imageVector = Icons.Default.ContentCopy,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.primary,
							modifier = Modifier.size(12.dp),
						)
					}
				}

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(18.dp),
				) {
					ContractInfoColumn(
						label = "Início",
						value = contract?.startDate ?: "--/--/----",
						modifier = Modifier.weight(1f),
					)
					ContractInfoColumn(
						label = "Término",
						value = contract?.endDate ?: "--/--/----",
						modifier = Modifier.weight(1f),
					)
				}

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(18.dp),
				) {
					ContractInfoColumn(
						label = "Plano",
						value = contract?.planName ?: "Sem dados",
						modifier = Modifier.weight(1f),
					)
					ContractInfoColumn(
						label = "Caução paga",
						value = contract?.caution ?: "R$ 0",
						modifier = Modifier.weight(1f),
					)
				}
			}
		},
	)
}

@Composable
fun CustomerContractDocumentsCard(
	documents: List<String>,
	modifier: Modifier = Modifier,
) {
	CustomerAppCard(
		modifier = modifier,
		title = "Documentos",
		content = {
			Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
				documents.forEach { document ->
					DocumentRow(label = document)
				}
			}
		},
	)
}

@Composable
fun CustomerContractMotoCard(
	contract: CustomerContract?,
	modifier: Modifier = Modifier,
) {
	CustomerAppCard(
		modifier = modifier,
		containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
		borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
		contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
		content = {
			Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Icon(
						imageVector = AutoMirrored.Filled.DirectionsBike,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.primary,
						modifier = Modifier.size(16.dp),
					)
					Spacer(modifier = Modifier.size(6.dp))
					Text(
						text = "SUA MOTO",
						style = MaterialTheme.typography.labelSmall,
						fontWeight = FontWeight.Black,
						color = MaterialTheme.colorScheme.primary,
					)
				}

				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(14.dp),
				) {
					Box(
						modifier = Modifier
							.size(84.dp, 54.dp)
							.clip(RoundedCornerShape(14.dp))
							.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.10f)),
						contentAlignment = Alignment.Center,
					) {
						if (contract?.imageRes != null) {
							Image(
								painter = rememberAssetPainter(contract.imageRes),
								contentDescription = null,
								modifier = Modifier.fillMaxWidth(0.88f),
							)
						}
					}

					Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
						Text(
							text = contract?.motoName ?: "Sem moto vinculada",
							style = MaterialTheme.typography.titleSmall,
							fontWeight = FontWeight.Black,
							color = MaterialTheme.colorScheme.onSurface,
						)
						Text(
							text = contract?.motoSubtitle ?: "Nenhum contrato ativo",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
						)
					}
				}
			}
		},
	)
}

@Composable
fun CustomerContractSwitcherCard(
	contract: CustomerContract,
	selected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.background(
				if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
				else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f),
			)
			.border(
				1.dp,
				if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
				else MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
				RoundedCornerShape(16.dp),
			)
			.clickable(onClick = onClick)
			.padding(14.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
			Text(
				text = contract.planName,
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Black,
				color = MaterialTheme.colorScheme.onSurface,
			)
			Text(
				text = contract.motoName,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
		Text(
			text = if (selected) "ATUAL" else "›",
			style = MaterialTheme.typography.labelSmall,
			fontWeight = FontWeight.Black,
			color = MaterialTheme.colorScheme.primary,
		)
	}
}

@Composable
private fun ContractInfoColumn(
	label: String,
	value: String,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
		Text(
			text = label,
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		Text(
			text = value,
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.Black,
			color = MaterialTheme.colorScheme.onSurface,
		)
	}
}

@Composable
private fun DocumentRow(label: String) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f))
			.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
			.padding(14.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
			Box(
				modifier = Modifier
					.size(38.dp)
					.clip(RoundedCornerShape(14.dp))
					.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
				contentAlignment = Alignment.Center,
			) {
				Icon(
					imageVector = Icons.Default.Description,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary,
				)
			}
			Spacer(modifier = Modifier.size(12.dp))
			Text(
				text = label,
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Black,
				color = MaterialTheme.colorScheme.onSurface,
			)
		}
		Text(
			text = "›",
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.primary,
		)
	}
}








