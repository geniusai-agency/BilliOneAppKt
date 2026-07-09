package com.example.billionemotosappkt.desktop.admin.data

import androidx.compose.ui.graphics.Color

data class AdminMetricModel(
    val label: String,
    val value: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accent: Color,
    val kicker: String? = null,
)