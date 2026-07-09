package com.example.billionemotosappkt.screens.dashboard.customer.components



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.initialsFrom

import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerDashboardHeader(
	userName: String,
	userEmail: String?,
	showBackButton: Boolean = false,
	onBack: () -> Unit = {},
	onEditProfile: () -> Unit = {},
	onLogout: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		if (showBackButton) {
			TextButton(onClick = onBack) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowBack,
						contentDescription = "Voltar",
						tint = MaterialTheme.colorScheme.onSurface,
					)
					Spacer(modifier = Modifier.size(4.dp))
					Text("Voltar", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
				}
			}
			Spacer(modifier = Modifier.size(4.dp))
		}

		Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
			Box(
				contentAlignment = Alignment.Center,
				modifier = Modifier
					.size(46.dp)
					.clip(CircleShape)
					.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
					.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.24f), CircleShape),
			) {
				Text(
					text = initialsFrom(userName),
					color = MaterialTheme.colorScheme.primary,
					fontWeight = FontWeight.Black,
					style = MaterialTheme.typography.labelLarge,
				)
			}

			Spacer(modifier = Modifier.size(12.dp))
			Box {
				androidx.compose.foundation.layout.Column {
					Text(
						text = "Bem-vindo,",
						style = MaterialTheme.typography.labelMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
					Text(
						text = userName,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Black,
						color = MaterialTheme.colorScheme.onSurface,
					)
					if (!userEmail.isNullOrBlank()) {
						Text(
							text = userEmail,
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
						)
					}
				}
			}
		}

		Row {
			IconButton(onClick = onEditProfile) {
				Box(
					modifier = Modifier
						.size(38.dp)
						.background(
							color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
							shape = RoundedCornerShape(14.dp),
						),
					contentAlignment = Alignment.Center,
				) {
					Icon(
						imageVector = Icons.Default.Edit,
						contentDescription = "Editar perfil",
						tint = MaterialTheme.colorScheme.primary,
					)
				}
			}
			IconButton(onClick = onLogout) {
				Box(
					modifier = Modifier
						.size(38.dp)
						.background(
							color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.24f),
							shape = RoundedCornerShape(14.dp),
						),
					contentAlignment = Alignment.Center,
				) {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.Logout,
						contentDescription = "Sair",
						tint = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
			}
		}
	}
}


