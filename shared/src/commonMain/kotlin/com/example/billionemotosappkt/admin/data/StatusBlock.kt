package com.example.billionemotosappkt.desktop.admin.data

import androidx.compose.ui.graphics.Color

data class StatusBlock(
    val label: String,
    val value: Int,
    val total: Int,
    val accent: Color,
)