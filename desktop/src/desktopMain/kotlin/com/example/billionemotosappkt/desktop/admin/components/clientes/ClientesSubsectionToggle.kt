package com.example.billionemotosappkt.desktop.admin.components.clientes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.model.ClientesSectionTab

@Composable
fun ClientesSubsectionToggle(
    selected: ClientesSectionTab,
    onSelected: (ClientesSectionTab) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ClientesSubsectionChip(
            label = "Clientes",
            selected = selected == ClientesSectionTab.CLIENTES,
            onClick = { onSelected(ClientesSectionTab.CLIENTES) },
        )
        ClientesSubsectionChip(
            label = "Pedidos",
            selected = selected == ClientesSectionTab.PEDIDOS,
            onClick = { onSelected(ClientesSectionTab.PEDIDOS) },
        )
    }
}

@Composable
fun ClientesSubsectionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (selected) Color(0xFF20E65B).copy(alpha = 0.15f) else Color(0xFF111614)
    val borderColor = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.1f)
    val textColor = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.6f)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.height(36.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                text = label,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
