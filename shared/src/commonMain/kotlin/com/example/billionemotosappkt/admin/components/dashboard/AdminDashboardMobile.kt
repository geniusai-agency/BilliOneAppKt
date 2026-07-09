package com.example.billionemotosappkt.desktop.admin.components.dashboard

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.admin.components.AdminHero
import com.example.billionemotosappkt.desktop.admin.components.AdminListsSection
import com.example.billionemotosappkt.desktop.admin.components.AdminOverviewSplit
import com.example.billionemotosappkt.desktop.admin.components.AdminStatusStrip
import com.example.billionemotosappkt.desktop.admin.components.AdminTopBar
import com.example.billionemotosappkt.desktop.admin.components.AuthUiState
import com.example.billionemotosappkt.desktop.admin.components.DashboardOverviewData
import com.example.billionemotosappkt.desktop.admin.components.MetricGrid
import com.example.billionemotosappkt.desktop.admin.model.adminMetrics

@Composable
fun AdminDashboardMobile(
    authState: AuthUiState,
    overview: DashboardOverviewData,
    lastUpdatedAt: String?,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    scrollState: ScrollState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AdminTopBar(
            authState = authState,
            lastUpdatedAt = lastUpdatedAt,
            onRefresh = onRefresh,
            onLogout = onLogout,
            compact = true,
        )
        AdminHero(
            authState = authState,
            overview = overview,
            lastUpdatedAt = lastUpdatedAt,
            onRefresh = onRefresh,
            compact = true,
        )
        MetricGrid(
            metrics = adminMetrics(overview),
            columns = 2,
        )
        AdminStatusStrip(overview = overview, compact = true)
        AdminOverviewSplit(overview = overview, compact = true)
        AdminListsSection(overview = overview, columns = 1)
        Spacer(modifier = Modifier.height(8.dp))
    }
}