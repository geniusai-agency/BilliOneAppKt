package com.example.billionemotosappkt.desktop.admin.data

data class ListSectionModel(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val items: List<ListItemModel>,
)