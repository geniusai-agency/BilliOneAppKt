package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun TextButtonLink(
    text: String,
    accent: Color,
    compactLayout: Boolean = false,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    TextButton(
        onClick = onClick,
        modifier = Modifier.hoverable(interactionSource),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (hovered) accent else accent.copy(alpha = 0.84f),
        ),
    ) {
        Text(text = text, fontSize = if (compactLayout) 11.sp else 12.sp)
    }
}