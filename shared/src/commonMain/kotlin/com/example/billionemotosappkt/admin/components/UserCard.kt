package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.shared.api.AppRole

@Composable
fun UserCard(authState: AuthUiState?) {
    UserCardContent(
        userName = authState?.userName,
        userEmail = authState?.userEmail,
        roles = authState?.roles ?: emptyList(),
        isAdmin = authState?.isAdmin ?: false,
    )
}

@Composable
fun UserCard(authState: com.example.billionemotosappkt.screens.auth.AuthUiState) {
    UserCardContent(
        userName = authState.userName,
        userEmail = authState.userEmail,
        roles = authState.roles,
        isAdmin = authState.isAdmin,
    )
}

@Composable
private fun UserCardContent(
    userName: String?,
    userEmail: String?,
    roles: List<AppRole>,
    isAdmin: Boolean,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.36f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle(
                    label = initials(userName ?: userEmail ?: "AD"),
                    size = 52.dp,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName ?: "Administrador",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = userEmail ?: "Conta interna",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            val roleLabel = when {
                roles.contains(AppRole.ADMIN) -> "Administrador"
                roles.contains(AppRole.FINANCEIRO) -> "Financeiro"
                roles.contains(AppRole.MECANICO) -> "Mecânico"
                roles.contains(AppRole.OFICINA) -> "Oficina"
                roles.contains(AppRole.CLIENTE) -> "Cliente (Sem acesso admin)"
                else -> if (isAdmin) "Administrador" else "Operação"
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill(
                    text = roleLabel,
                    icon = Icons.Default.SupportAgent,
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    contentColor = MaterialTheme.colorScheme.primary,
                )
                Pill(
                    text = "Online",
                    icon = Icons.Default.Timeline,
                    containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                    contentColor = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}
