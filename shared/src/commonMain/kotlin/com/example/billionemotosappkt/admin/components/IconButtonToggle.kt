package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun IconButtonToggle(
    pressed: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (pressed) Color(0xFF20E65B) else Color.White.copy(alpha = 0.48f)
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(42.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = tint),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
    ) {
        Icon(
            imageVector = if (pressed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = null,
            tint = tint,
        )
    }
}