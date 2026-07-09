package com.example.billionemotosappkt.screens.dashboard

import com.example.billionemotosappkt.data.dashboard.DashboardOverviewData

data class DashboardUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val lastUpdatedAt: String? = null,
    val overview: DashboardOverviewData? = null,
)
