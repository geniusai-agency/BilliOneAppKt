package com.example.billionemotosappkt.screens.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SmallMetric(
    val label: String,
    val value: String,
    val icon: ImageVector,
)

data class AdminMetricModel(
    val label: String,
    val value: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val kicker: String? = null,
)

data class StatusBlock(
    val label: String,
    val value: Int,
    val total: Int,
    val accent: Color,
)

data class ListSectionModel(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val items: List<ListItemModel>,
)

data class ListItemModel(
    val title: String,
    val subtitle: String,
    val value: String,
    val status: String,
    val tint: Color,
)

data class ActionHint(
    val label: String,
    val icon: ImageVector,
)

fun adminSectionIcons() = listOf(
    Icons.Default.Groups,
    Icons.Default.DirectionsBike,
    Icons.Default.CreditCard,
    Icons.Default.AttachMoney,
    Icons.Default.Build,
    Icons.Default.SupportAgent,
    Icons.Default.Analytics,
    Icons.Default.ReportProblem,
    Icons.Default.Schedule,
    Icons.Default.Insights,
    Icons.Default.History,
    Icons.Default.Timeline,
    Icons.Default.WarningAmber,
)
