package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminSidebar(
    authState: AuthUiState?,
    overview: DashboardOverviewData,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            BrandBlock()
            UserCard(authState = authState)
            SidebarQuickMetrics(overview = overviejw)
            SidebarActions()
            SidebarAttention(overview = overview)
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Sair do painel")
            }
        }
    }
}

@Composable
fun AdminSidebar(
    authState: com.example.billionemotosappkt.screens.auth.AuthUiState,
    overview: DashboardOverviewData,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            BrandBlock()
            UserCard(authState = authState)
            SidebarQuickMetrics(overview = overview)
            SidebarActions()
            SidebarAttention(overview = overview)
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Sair do painel")
            }
        }
    }
}
