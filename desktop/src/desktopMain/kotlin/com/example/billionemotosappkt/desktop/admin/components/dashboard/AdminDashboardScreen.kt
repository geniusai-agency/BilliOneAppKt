package com.example.billionemotosappkt.desktop.admin.components.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.admin.components.AuthUiState
import com.example.billionemotosappkt.desktop.admin.components.DashboardOverviewData
import com.example.billionemotosappkt.desktop.admin.components.DecorativeGlow

@Composable
fun AdminDashboardScreen(
    authState: AuthUiState,
    overview: DashboardOverviewData,
    lastUpdatedAt: String?,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val compact = maxWidth < 900.dp
        val contentScrollState = rememberScrollState()

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f),
                                MaterialTheme.colorScheme.background,
                            ),
                        ),
                    ),
            ) {
                DecorativeGlow(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 24.dp, end = 12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                    size = 260.dp,
                )
                DecorativeGlow(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 32.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f),
                    size = 240.dp,
                )

                if (compact) {
                    AdminDashboardMobile(
                        authState = authState,
                        overview = overview,
                        lastUpdatedAt = lastUpdatedAt,
                        onRefresh = onRefresh,
                        onLogout = onLogout,
                        scrollState = contentScrollState,
                    )
                } else {
                    AdminDashboardDesktop(
                        authState = authState,
                        overview = overview,
                        lastUpdatedAt = lastUpdatedAt,
                        onRefresh = onRefresh,
                        onLogout = onLogout,
                        scrollState = contentScrollState,
                    )
                }
            }
        }
    }
}