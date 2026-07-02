package com.example.billionemotosappkt.desktop.admin.components.dashboard

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.admin.components.AdminHero
import com.example.billionemotosappkt.desktop.admin.components.AdminListsSection
import com.example.billionemotosappkt.desktop.admin.components.AdminOverviewSplit
import com.example.billionemotosappkt.desktop.admin.components.AdminSidebar
import com.example.billionemotosappkt.desktop.admin.components.AdminStatusStrip
import com.example.billionemotosappkt.desktop.admin.components.AdminTopBar
import com.example.billionemotosappkt.desktop.admin.components.AuthUiState
import com.example.billionemotosappkt.desktop.admin.components.DashboardOverviewData
import com.example.billionemotosappkt.desktop.admin.components.MetricGrid
import com.example.billionemotosappkt.desktop.admin.model.adminMetrics

@Composable
fun AdminDashboardDesktop(
    authState: AuthUiState,
    overview: DashboardOverviewData,
    lastUpdatedAt: String?,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    scrollState: ScrollState,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        AdminSidebar(
            authState = authState,
            overview = overview,
            onLogout = onLogout,
            modifier = Modifier.width(320.dp),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            AdminTopBar(
                authState = authState,
                lastUpdatedAt = lastUpdatedAt,
                onRefresh = onRefresh,
                onLogout = onLogout,
            )
            AdminHero(
                authState = authState,
                overview = overview,
                lastUpdatedAt = lastUpdatedAt,
                onRefresh = onRefresh,
                compact = false,
            )
            MetricGrid(
                metrics = adminMetrics(overview),
                columns = 3,
            )
            AdminStatusStrip(overview = overview, compact = false)
            AdminOverviewSplit(overview = overview, compact = false)
            AdminListsSection(overview = overview, columns = 2)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
